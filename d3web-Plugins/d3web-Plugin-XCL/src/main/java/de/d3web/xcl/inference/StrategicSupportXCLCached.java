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

import org.jetbrains.annotations.NotNull;

import com.denkbares.collections.MultiMap;
import de.d3web.core.inference.StrategicSupport;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.ConditionTrue;
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
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.interview.FormStrategy;
import de.d3web.interview.Interview;
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
		if (conditionedFinding instanceof CondNot condNot) {
			Condition subCondition = condNot.getTerms().get(0);
			if (subCondition instanceof CondEqual condEqual) {
				Value value = condEqual.getValue();
				if (condEqual.getQuestion() instanceof QuestionOC oc && value instanceof ChoiceValue cv) {
					List<Condition> terms = new LinkedList<>();
					for (Choice c : oc.getAllAlternatives()) {
						if (!cv.getChoiceID().equals(new ChoiceID(c))) {
							terms.add(new CondEqual(oc, new ChoiceValue(c)));
						}
					}
					conditionedFinding = new CondOr(terms);
				}
			}
			// use De Morgan
			else if (subCondition instanceof CondAnd condAnd) {
				List<Condition> terms = new LinkedList<>();
				for (Condition c : condAnd.getTerms()) {
					terms.add(new CondNot(c));
				}
				conditionedFinding = new CondOr(terms);
			}
			else if (subCondition instanceof CondOr condOr) {
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
		else if (conditionedFinding instanceof CondEqual condEqual) {
			if (isNormalCovering(condEqual)) {
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

	private static boolean isNormalCovering(CondEqual condEqual) {
		DefaultAbnormality store = condEqual.getQuestion().getInfoStore().getValue(BasicProperties.DEFAULT_ABNORMALITY);
		return (store != null) && (store.getValue(condEqual.getValue()) == Abnormality.A0);
	}

	private static Collection<Question> getRelevantQuestions(Collection<? extends QASet> qasets, Session session) {
		Interview interview = Interview.get(session);
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
			XCLModel model = solution.getKnowledgeStore().getKnowledge(XCLModel.KNOWLEDGE_KIND);
			if (model == null) continue;
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
								Set<XCLRelation> conditions = excludingQuestions.computeIfAbsent(q, k -> new HashSet<>());
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
	public boolean hasGroupInformationGain(Collection<? extends QASet> qaSets, Collection<Solution> solutions, Session session) {
		Collection<Question> questions = getRelevantQuestions(qaSets, session);
		if (questions.isEmpty()) return false;

		Map<List<Set<Condition>>, Set<Solution>> groupPots = new HashMap<>();
		Map<Question, Set<XCLRelation>> excludingQuestions = getExcludingQuestion(solutions, questions);

		MultiMap<Solution, Solution> groupToSolutionsOfGroup = KnowledgeBaseUtils.groupSolutions(solutions);

		// More than 20 groups: we are sure there is benefit somewhere (otherwise the kb would be very incomplete)
		// -> Skip costly calculation and return true immediately
		if (groupToSolutionsOfGroup.keySet().size() > 20) return true;

		for (Solution group : groupToSolutionsOfGroup.keySet()) {
			Set<Solution> solutionsOfGroup = groupToSolutionsOfGroup.getValues(group);

			// add all condition sets for the grouping solution
			Set<XCLModel> coveringModels = getCoveringModels(questions, solutionsOfGroup);
			for (XCLModel model : coveringModels) {
				ArrayList<Set<Condition>> conditionsForQuestions = getConditionsForQuestions(questions, excludingQuestions, model);
				groupPots.computeIfAbsent(conditionsForQuestions, k -> new HashSet<>()).add(group);
			}
			if (coveringModels.isEmpty()) {
				groupPots.computeIfAbsent(List.of(), k -> new HashSet<>()).add(group);
			}

			// as soon as there are different sets of solution groups for different
			// conditions pots (answer set combinations), we know that different answers might produce different
			// grouping solutions, meaning we have benefit somewhere
			Set<Set<Solution>> sets = new HashSet<>();
			for (Set<Solution> groups : groupPots.values()) {
				sets.add(groups);
				if (sets.size() > 1) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public double getInformationGain(Collection<? extends QASet> qaSets, Collection<Solution> solutions, Session session) {
		Collection<Question> questions = getRelevantQuestions(qaSets, session);
		if (questions.isEmpty()) return 0;

		InformationPots<Condition> pots = new InformationPots<>();
		Map<Question, Set<XCLRelation>> excludingQuestions = getExcludingQuestion(solutions, questions);

		Set<XCLModel> coveringModels = getCoveringModels(questions, solutions);

		for (XCLModel model : coveringModels) {
			// multiply possible value sets to get pots
			// and add solution probabilities to these pots
			pots.addWeights(model.getSolution(), getConditionsForQuestions(questions, excludingQuestions, model));
		}

		// finally, we add all the solutions that are not covered at all
		pots.addWeights(getTotalWeight(solutions) - pots.getTotalWeight(),
				getConditionsOfUncoveredSolutions(questions, excludingQuestions));

		// calculate information gain
		return pots.getInformationGain();
	}

	@NotNull
	private ArrayList<Set<Condition>> getConditionsOfUncoveredSolutions(Collection<Question> questions, Map<Question, Set<XCLRelation>> excludingQuestions) {
		ArrayList<Set<Condition>> conditionsForQuestions = new ArrayList<>(questions.size());
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
		return conditionsForQuestions;
	}

	@NotNull
	private ArrayList<Set<Condition>> getConditionsForQuestions(Collection<Question> questions, Map<Question, Set<XCLRelation>> excludingQuestions, XCLModel model) {
		ArrayList<Set<Condition>> conditionsForQuestions = new ArrayList<>(questions.size());
		for (Question q : questions) {
			Set<Condition> set = null;
			Set<XCLRelation> coveringRelations = model.getCoveringRelations(q);
			for (XCLRelation r : coveringRelations) {
				if (r.hasType(XCLRelationType.contradicted)) {
					// maybe slightly incorrect, but have a better behaviour for multiple non-covered choices
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
					if (coveringRelations.contains(r)) {
						continue;
					}
					set = lazyAddAll(set, filterForeignConditions(q, getExtractedOrs(r)));
				}
			}
			conditionsForQuestions.add(set);
		}
		return conditionsForQuestions;
	}

	@NotNull
	private Set<XCLModel> getCoveringModels(Collection<Question> questions, Collection<Solution> solutions) {
		Set<XCLModel> coveringModels = new HashSet<>();
		// collect models of the specified solutions, covering the questions
		for (Question question : questions) {
			XCLContributedModelSet knowledge = question.getKnowledgeStore()
					.getKnowledge(XCLContributedModelSet.KNOWLEDGE_KIND);
			if (knowledge == null) continue;
			for (XCLModel model : knowledge.getModels()) {
				if (solutions.contains(model.getSolution())) {
					coveringModels.add(model);
				}
			}
		}
		return coveringModels;
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
			totalWeight += solution.getInfoStore().getValue(BasicProperties.APRIORI);
		}

		// use cache and return
		lastTotalWeightSolutions = solutions;
		lastTotalWeight = totalWeight;
		return totalWeight;
	}

	private static final Set<Condition> NULL_SET = Collections.unmodifiableSet(new HashSet<>(
			Collections.singletonList(null)));

	/**
	 * Adds item to a source set that may be null and returns the resulting set. The specified source set is used
	 * destructively. If the source set is null a new one will be created. Otherwise the specified one may be altered
	 * and returned. For optimization reasons, we also consider the {@link #NULL_SET} to be specified, that is a
	 * unmodifiable set containing exactly one value 'null'.
	 *
	 * @param source the source set to be changed
	 * @param items  the items to be added
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
	 * Returns the extracted ors for the condition of a specified xcl relation. It is only created on demand, otherwise
	 * a cached value is returned.
	 */
	private Collection<Condition> getExtractedOrs(XCLRelation r) {
		Condition condition = r.getConditionedFinding();
		return extractedOrCache.computeIfAbsent(condition, c -> {
			Collection<Condition> result = new HashSet<>();
			extractOrs(result, condition);
			return Collections.unmodifiableCollection(result);
		});
	}

	/**
	 * Returns the extracted ors for the negated condition of a specified xcl relation. It is only created on demand,
	 * otherwise a cached value is returned.
	 */
	private Collection<Condition> getNegatedExtractedOrs(XCLRelation r) {
		return negatedExtractedOrCache.computeIfAbsent(r.getConditionedFinding(), c -> {
			Set<Condition> ors = new HashSet<>(getExtractedOrs(r));
			boolean coversNormal = ors.remove(null);

			// add all non-covered choices (as CondEquals) that are NOT (!) in the extracted ORs
			Set<Condition> result = new HashSet<>();
			c.getTerminalObjects().stream()
					.filter(QuestionChoice.class::isInstance).map(QuestionChoice.class::cast).forEach(question -> {
						for (Choice choice : question.getAllAlternatives()) {
							// skip if choice is in or (use all non-covered choices to create negated covering)
							CondEqual cond = new CondEqual(question, new ChoiceValue(choice));
							if (ors.remove(cond)) continue;
							// skip if normal values are covered and the choice is normal (to create negated covering)
							if (coversNormal && isNormalCovering(cond)) continue;
							// otherwise add the choice to the negated covering
							result.add(cond);
						}
					});

			// additionally add all remaining negated extracted ORs that are not CondEquals of any choices
			// Killt das den fix von 2018 ?!
			for (Condition other : ors) {
				if (other instanceof CondNot) {
					result.add(((CondNot) other).getOperand());
				}
				else {
					result.add(new CondNot(other));
				}
			}

			if (result.isEmpty() && coversNormal) result.add(new CondNot(ConditionTrue.INSTANCE));
			return Collections.unmodifiableCollection(result);
		});
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
