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
import de.d3web.interview.Form;
import de.d3web.interview.FormStrategy;
import de.d3web.interview.Interview;
import de.d3web.interview.inference.PSMethodInterview;
import de.d3web.xcl.XCLContributedModelSet;
import de.d3web.xcl.XCLModel;
import de.d3web.xcl.XCLRelation;
import de.d3web.xcl.XCLRelationType;

/**
 * 
 * @author volker_belli
 * @created 28.05.2012
 */
public class StrategicSupportXCL implements StrategicSupport {

	@Override
	public double getInformationGain(Collection<? extends QASet> qasets,
			Collection<Solution> solutions, Session session) {
		Map<List<Condition>, Float> map = new HashMap<>();
		Collection<Question> questions = new HashSet<>();
		Interview interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
		FormStrategy formStrategy = interview.getFormStrategy();
		for (QASet qaSet : qasets) {
			Form form = formStrategy.getForm(qaSet, session);
			questions.addAll(form.getActiveQuestions());
		}

		Map<Question, Set<Condition>> excludingQuestions =
				getExcludingQuestion(solutions, questions);

		float totalweight = 0;
		for (Solution solution : solutions) {
			XCLModel model = solution.getKnowledgeStore().getKnowledge(XCLModel.KNOWLEDGE_KIND);
			if (model == null) continue;
			LinkedList<Set<Condition>> conditionsForQuestions = new LinkedList<>();
			for (Question q : questions) {
				Set<Condition> set = new HashSet<>();
				Set<XCLRelation> coveringRelations = model.getCoveringRelations(q);
				Set<Condition> conditions = excludingQuestions.get(q) == null
						? new HashSet<>()
						: new HashSet<>(excludingQuestions.get(q));
				for (XCLRelation r : coveringRelations) {
					if (r.hasType(XCLRelationType.contradicted)) {
						extractOrs(set, new CondNot(r.getConditionedFinding()));
						conditions.remove(r.getConditionedFinding());
					}
					else {
						extractOrs(set, r.getConditionedFinding());
					}
				}
				if (set.isEmpty()) {
					set.add(null);
				}
				// cover all conditions used in contrarelations of other
				// XCLModels
				for (Condition c : conditions) {
					extractOrs(set, c);
				}
				conditionsForQuestions.add(set);
			}

			// multiply possible value sets to get pots
			// and add solution probabilities to these pots
			List<List<Condition>> combinations = getCombinations(new LinkedList<>(
					conditionsForQuestions));
			Number apriori = solution.getInfoStore().getValue(BasicProperties.APRIORI);
			float weight = (apriori == null) ? 1f : apriori.floatValue();
			totalweight += weight;
			for (List<Condition> pot : combinations) {
				Float count = map.get(pot);
				if (count == null) {
					map.put(pot, weight);
				}
				else {
					map.put(pot, weight + count);
				}

			}
		}

		// calculate information gain
		// Russel & Norvig p. 805
		double sum = 0;
		for (Float relationcount : map.values()) {
			double p = (double) relationcount / totalweight;
			sum += (-1) * p * Math.log10(p) / Math.log10(2);
		}
		return sum;
	}

	private Map<Question, Set<Condition>> getExcludingQuestion(Collection<Solution> solutions, Collection<Question> questions) {
		Map<Question, Set<Condition>> excludingQuestions = new HashMap<>();
		for (Question q : questions) {
			XCLContributedModelSet knowledge = q.getKnowledgeStore().getKnowledge(
					XCLContributedModelSet.KNOWLEDGE_KIND);
			if (knowledge != null) {
				for (XCLModel model : knowledge.getModels()) {
					if (solutions.contains(model.getSolution())) {
						for (XCLRelation relation : model.getContradictingRelations()) {
							Collection<? extends TerminologyObject> relationObjects =
									relation.getConditionedFinding().getTerminalObjects();
							if (relationObjects.contains(q)) {
								Set<Condition> conditions = excludingQuestions.get(q);
								if (conditions == null) {
									conditions = new HashSet<>();
									excludingQuestions.put(q, conditions);
								}
								conditions.add(relation.getConditionedFinding());
							}
						}
					}
				}
			}
		}

		return excludingQuestions;
	}

	private static List<List<Condition>> getCombinations(LinkedList<Set<Condition>> conditionsForQuestions) {
		List<List<Condition>> result = new LinkedList<>();
		if (conditionsForQuestions.isEmpty()) {
			result.add(Collections.emptyList());
			return result;
		}
		Set<Condition> first = conditionsForQuestions.poll();
		List<List<Condition>> restresult = getCombinations(conditionsForQuestions);
		for (List<Condition> list : restresult) {
			for (Condition condition : first) {
				List<Condition> item = new LinkedList<>();
				item.add(condition);
				item.addAll(list);
				result.add(item);
			}
		}
		return result;
	}

	private static void extractOrs(Collection<Condition> conds, Condition conditionedFinding) {
		// if the condition is a condnot, try to replace it with condors or
		// condands
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
					BasicProperties.DEFAULT_ABNORMALITIY);
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

	@Override
	public Collection<Solution> getUndiscriminatedSolutions(Session session) {
		List<Solution> solutions = session.getBlackboard().getSolutions(State.ESTABLISHED);
		if (solutions.size() > 0) {
			return new HashSet<>(solutions);
		}
		solutions = session.getBlackboard().getSolutions(State.SUGGESTED);
		if (solutions.size() > 0) {
			return new HashSet<>(solutions);
		}
		solutions = session.getBlackboard().getSolutions(State.UNCLEAR);
		return new HashSet<>(solutions);
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

}
