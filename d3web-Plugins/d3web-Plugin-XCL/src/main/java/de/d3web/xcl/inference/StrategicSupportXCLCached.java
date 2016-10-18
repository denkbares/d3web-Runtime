/*
 * Copyright (C) 2012 denkbares GmbH, Germany
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.xcl.inference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.StrategicSupport;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.abnormality.Abnormality;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.interview.FormStrategy;
import de.d3web.interview.Interview;
import de.d3web.interview.inference.PSMethodInterview;
import de.d3web.xcl.XCLContributedModelSet;
import de.d3web.xcl.XCLModel;
import de.d3web.xcl.XCLRelation;
import de.d3web.xcl.XCLRelationType;

/**
 * @author volker_belli
 * @created 27.05.2012
 */
public class StrategicSupportXCLCached implements StrategicSupport {

	private static void extractOrs(Collection<Condition> conds, Condition conditionedFinding) {
		// if the condition is a condNot,
		// try to replace it with condOr or condAnd
		if (conditionedFinding instanceof CondNot) {
			CondNot condNot = (CondNot) conditionedFinding;
			Condition subCondition = condNot.getTerms().get(0);
			if (subCondition instanceof CondEqual) {
				CondEqual condEqual = (CondEqual) subCondition;
				Value value = condEqual.getValue();
				if (condEqual.getQuestion() instanceof QuestionOC && value instanceof ChoiceValue) {
					ChoiceValue cv = (ChoiceValue) value;
					List<Condition> terms = new LinkedList<>();
					QuestionOC oc = (QuestionOC) condEqual.getQuestion();
					for (Choice c : oc.getAllAlternatives()) {
						if (!cv.getChoiceID().equals(new ChoiceID(c))) {
							terms.add(new CondEqual(oc, new ChoiceValue(c)));
						}
					}
					conditionedFinding = new CondOr(terms);
				}
			}
			// use De Morgan
			else if (subCondition instanceof CondAnd) {
				CondAnd condAnd = (CondAnd) subCondition;
				List<Condition> terms = new LinkedList<>();
				for (Condition c : condAnd.getTerms()) {
					terms.add(new CondNot(c));
				}
				conditionedFinding = new CondOr(terms);
			}
			else if (subCondition instanceof CondOr) {
				CondOr condOr = (CondOr) subCondition;
				List<Condition> terms = new LinkedList<>();
				for (Condition c : condOr.getTerms()) {
					terms.add(new CondNot(c));
				}
				conditionedFinding = new CondAnd(terms);
			}
		}
		if (conditionedFinding instanceof CondOr) {
			for (Condition condition : ((CondOr) conditionedFinding).getTerms()) {
				extractOrs(conds, condition);
			}
		}
		else if ((conditionedFinding instanceof CondAnd)
				&& ((CondAnd) conditionedFinding).getTerms().size() == 1) {
			extractOrs(conds, ((CondAnd) conditionedFinding).getTerms().get(0));
		}
		// replace condequals of normal answers with null
		else if (conditionedFinding instanceof CondEqual) {
			CondEqual condEqual = (CondEqual) conditionedFinding;
			DefaultAbnormality abnormalityStore = condEqual.getQuestion().getInfoStore().getValue(
					BasicProperties.DEFAULT_ABNORMALITY);
			double abnormality = Abnormality.A5;
			if (abnormalityStore != null) {
				abnormality = abnormalityStore.getValue(condEqual.getValue());
			}
			if (abnormality == Abnormality.A0) {
				conds.add(null);
			}
			else {
				conds.add(condEqual);
			}
		}
		else {
			conds.add(conditionedFinding);
		}
	}

	private static Collection<Question> getRelevantQuestions(Collection<? extends QASet> qasets, Session session) {
		Interview interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
		FormStrategy formStrategy = interview.getFormStrategy();
		Set<Question> result = new HashSet<>();
		for (QASet qaSet : qasets) {
			for (Question q : formStrategy.getActiveQuestions(qaSet, session)) {
				boolean ignore = // false;
						(q instanceof QuestionChoice)
								&& ((QuestionChoice) q).getAllAlternatives().size() <= 1;
				// unfortunately, in rare cases, ignoring irrelevant questions
				// will result in slightly different information gain
				if (!ignore) {
					result.add(q);
				}
			}
		}
		return result;
	}

	@Override
	public Collection<Question> getDiscriminatingQuestions(
			Collection<Solution> solutions, Session session) {
		Set<Question> coveredSymptoms = new HashSet<>();
		for (Solution solution : solutions) {
			KnowledgeSlice ks = solution.getKnowledgeStore().getKnowledge(XCLModel.KNOWLEDGE_KIND);
			if (ks == null) continue;
			XCLModel model = (XCLModel) ks;
			for (TerminologyObject nob : model.getCoveredSymptoms()) {
				if (nob instanceof Question) {
					coveredSymptoms.add((Question) nob);
				}
			}
		}
		return coveredSymptoms;
	}

	private Map<Question, Set<XCLRelation>> getExcludingQuestion(Collection<Solution> solutions, Collection<Question> questions) {
		Map<Question, Set<XCLRelation>> excludingQuestions = new HashMap<>();
		for (Question q : questions) {
			XCLContributedModelSet knowledge = q.getKnowledgeStore().getKnowledge(
					XCLContributedModelSet.KNOWLEDGE_KIND);
			if (knowledge != null) {
				for (XCLModel model : knowledge.getModels()) {
					if (solutions.contains(model.getSolution())) {
						for (XCLRelation relation : model.getCoveringRelations(q)) {
							if (relation.hasType(XCLRelationType.contradicted)) {
								Set<XCLRelation> conditions = excludingQuestions.get(q);
								if (conditions == null) {
									conditions = new HashSet<>();
									excludingQuestions.put(q, conditions);
								}
								conditions.add(relation);
							}
						}
					}
				}
			}
		}

		return excludingQuestions;
	}

	@Override
	public double getInformationGain(Collection<? extends QASet> qasets,
									 Collection<Solution> solutions, Session session) {
		Collection<Question> questions = getRelevantQuestions(qasets, session);
		if (questions.isEmpty()) return 0;

		InformationPots<Condition> pots = new InformationPots<>();

		Map<Question, Set<XCLRelation>> excludingQuestions =
				getExcludingQuestion(solutions, questions);

		Set<XCLModel> coveringModels = new HashSet<>();
		// collect models of the specified solutions, covering the questions
		for (Question q : questions) {
			XCLContributedModelSet knowledge = q.getKnowledgeStore().getKnowledge(
					XCLContributedModelSet.KNOWLEDGE_KIND);
			if (knowledge == null) continue;
			for (XCLModel model : knowledge.getModels()) {
				if (solutions.contains(model.getSolution())) {
					coveringModels.add(model);
				}
			}
		}

		for (XCLModel model : coveringModels) {
			ArrayList<Set<Condition>> conditionsForQuestions =
					new ArrayList<>(questions.size());
			for (Question q : questions) {
				Set<Condition> set = null;
				Set<XCLRelation> coveringRelations = model.getCoveringRelations(q);
				for (XCLRelation r : coveringRelations) {
					if (r.hasType(XCLRelationType.contradicted)) {
						set = lazyAddAll(set, filterForeignConditions(q, getNegatedExtractedOrs(r)));
					}
					else {
						set = lazyAddAll(set, getExtractedOrs(r));
					}
				}
				if (set == null) {
					set = NULL_SET;
				}
				// cover all conditions used in contra-relations of other
				// XCLModels
				Set<XCLRelation> excludingRelations = excludingQuestions.get(q);
				if (excludingRelations != null) {
					for (XCLRelation r : excludingRelations) {
						if (coveringRelations != null && coveringRelations.contains(r)) {
							continue;
						}
						set = lazyAddAll(set, filterForeignConditions(q,
								getExtractedOrs(r)));
					}
				}
				conditionsForQuestions.add(set);
			}

			// multiply possible value sets to get pots
			// and add solution probabilities to these pots
			pots.addWeights(model.getSolution(), conditionsForQuestions);
		}

		// finally we add all the solutions that are not covered at all
		float allWeight = getTotalWeight(solutions);
		ArrayList<Set<Condition>> conditionsForQuestions =
				new ArrayList<>(questions.size());
		for (Question q : questions) {
			Set<Condition> set = NULL_SET;
			Set<XCLRelation> excludingRelations = excludingQuestions.get(q);
			if (excludingRelations != null) {
				for (XCLRelation r : excludingRelations) {
					set = lazyAddAll(set, filterForeignConditions(q, getExtractedOrs(r)));
				}
			}
			conditionsForQuestions.add(set);
		}
		pots.addWeights(allWeight - pots.getTotalWeight(), conditionsForQuestions);

		// calculate information gain
		return pots.getInformationGain();
	}

	private Collection<Condition> filterForeignConditions(Question q, Collection<Condition> extractedOrs) {
		Collection<Condition> filteredExtractedOrs = new HashSet<>();
		for (Condition c : extractedOrs) {
			if (c == null || c.getTerminalObjects().contains(q)) {
				filteredExtractedOrs.add(c);
			}
		}
		return filteredExtractedOrs;
	}

	// use come cache mechanism to avoid multiple calculation
	private transient Collection<Solution> lastTotalWeightSolutions = null;
	private transient float lastTotalWeight = 0f;

	private synchronized float getTotalWeight(Collection<Solution> solutions) {
		if (lastTotalWeightSolutions == solutions) return lastTotalWeight;

		// calculate the total weight
		float totalWeight = 0;
		for (Solution solution : solutions) {
			Number apriori = solution.getInfoStore().getValue(BasicProperties.APRIORI);
			float weight = (apriori == null) ? 1f : apriori.floatValue();
			totalWeight += weight;
		}

		// use cache and return
		lastTotalWeightSolutions = solutions;
		lastTotalWeight = totalWeight;
		return totalWeight;
	}

	private static final Set<Condition> NULL_SET = Collections.unmodifiableSet(new HashSet<>(
			Collections.singletonList((Condition) null)));

	/**
	 * Adds item to a source set that may be null and returns the resulting set.
	 * The specified source set is used destructively. If the source set is null
	 * a new one will be created. Otherwise the specified one may be altered and
	 * returned. For optimization reasons, we also consider the
	 * {@link #NULL_SET} to be specified, that is a unmodifiable set containing
	 * exactly one value 'null'.
	 *
	 * @param source the source set to be changed
	 * @param items the items to be added
	 * @return the resulting set, may be the source one or a newly created one
	 * @created 27.05.2012
	 */
	private static Set<Condition> lazyAddAll(Set<Condition> source, Collection<Condition> items) {
		if (items == null || items.isEmpty()) return source;
		if (source == null) return new HashSet<>(items);
		if (source == NULL_SET) {
			source = new HashSet<>(items);
			source.add(null);
			return source;
		}
		source.addAll(items);
		return source;
	}

	private final Map<Condition, Collection<Condition>> extractedOrCache = new HashMap<>();
	private final Map<Condition, Collection<Condition>> negatedExtractedOrCache = new HashMap<>();

	/**
	 * Returns the extracted ors for the condition of a specified xcl relation.
	 * It is only created on demand, otherwise a cached value is returned.
	 */
	private Collection<Condition> getExtractedOrs(XCLRelation r) {
		Condition condition = r.getConditionedFinding();
		Collection<Condition> result = extractedOrCache.get(condition);
		if (result == null) {
			result = new HashSet<>();
			extractOrs(result, condition);
			result = Collections.unmodifiableCollection(result);
			extractedOrCache.put(condition, result);
		}
		return result;
	}

	/**
	 * Returns the extracted ors for the negated condition of a specified xcl
	 * relation. It is only created on demand, otherwise a cached value is
	 * returned.
	 */
	private Collection<Condition> getNegatedExtractedOrs(XCLRelation r) {
		Condition condition = r.getConditionedFinding();
		Collection<Condition> result = negatedExtractedOrCache.get(condition);
		if (result == null) {
			result = new HashSet<>();
			extractOrs(result, new CondNot(condition));
			result = Collections.unmodifiableCollection(result);
			negatedExtractedOrCache.put(condition, result);
		}
		return result;
	}

	@Override
	public Collection<Solution> getUndiscriminatedSolutions(Session session) {
		Collection<Solution> solutions = session.getBlackboard().getSolutions(State.ESTABLISHED);
		if (solutions.isEmpty()) {
			solutions = session.getBlackboard().getSolutions(State.SUGGESTED);
		}
		if (solutions.isEmpty()) {
			solutions = session.getBlackboard().getSolutions(State.UNCLEAR);
		}
		// make a hashset, because we require it for fast access
		// during information gain calculation later on
		// and remove all non-xcl solutions from the set meanwhile
		HashSet<Solution> result = new HashSet<>();
		for (Solution solution : solutions) {
			XCLModel model = solution.getKnowledgeStore().getKnowledge(XCLModel.KNOWLEDGE_KIND);
			if (model != null) {
				result.add(solution);
			}
		}
		return result;
	}
}
