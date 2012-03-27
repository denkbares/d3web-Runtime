/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.StrategicSupport;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.NonTerminalCondition;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.abnormality.Abnormality;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.xcl.DefaultScoreAlgorithm;
import de.d3web.xcl.ScoreAlgorithm;
import de.d3web.xcl.XCLContributedModelSet;
import de.d3web.xcl.XCLModel;
import de.d3web.xcl.XCLRelation;

public final class PSMethodXCL implements PSMethod, StrategicSupport,
		SessionObjectSource<PSMethodXCL.XCLCaseObject> {

	public static final String PLUGIN_ID = "d3web-XCL";
	public static final String EXTENSION_ID = "PSMethodXCL";

	private ScoreAlgorithm scoreAlgorithm = new DefaultScoreAlgorithm();

	public PSMethodXCL() {
		super();
	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {

		// update total weight
		updateAnsweredWeight(session, changes);

		// find xcl models to be updated (and remember affecting changes)
		Map<XCLModel, List<PropagationEntry>> modelsToUpdate = new HashMap<XCLModel, List<PropagationEntry>>();
		for (PropagationEntry change : changes) {
			// do not handle strategic changes
			if (change.isStrategic() || !change.hasChanged()) continue;
			TerminologyObject nob = change.getObject();
			KnowledgeSlice ks = nob.getKnowledgeStore().getKnowledge(
					XCLContributedModelSet.KNOWLEDGE_KIND);
			if (ks != null) {
				XCLContributedModelSet ms = (XCLContributedModelSet) ks;
				for (XCLModel model : ms.getModels()) {
					List<PropagationEntry> entries = modelsToUpdate.get(model);
					if (entries == null) {
						entries = new LinkedList<PropagationEntry>();
						modelsToUpdate.put(model, entries);
					}
					entries.add(change);
				}
			}
		}

		// update required xcl models / inference traces
		for (XCLModel model : modelsToUpdate.keySet()) {
			List<PropagationEntry> entries = modelsToUpdate.get(model);
			this.scoreAlgorithm.update(model, entries, session);
		}

		// refresh the solutions states
		this.scoreAlgorithm.refreshStates(modelsToUpdate.keySet(), session);
	}

	private void updateAnsweredWeight(Session session,
			Collection<PropagationEntry> changes) {
		XCLCaseObject caseObject = session.getSessionObject(this);
		for (PropagationEntry entry : changes) {
			if (entry.getObject() instanceof Question) {
				Question question = (Question) entry.getObject();

				// update count of question
				if (entry.hasOldValue()) caseObject.totalAnsweredCount--;
				if (entry.hasNewValue()) caseObject.totalAnsweredCount++;

				// update abnormalities
				Abnormality abnormality = getAbnormalitySlice(question);
				double oldAbnormality = getAbnormality(abnormality, entry.getOldValue());
				double newAbnormality = getAbnormality(abnormality, entry.getNewValue());
				caseObject.totalAnsweredAbnormality -= oldAbnormality;
				caseObject.totalAnsweredAbnormality += newAbnormality;
			}
		}

		// TODO: remove this assert calculation
		List<? extends Question> answeredQuestions = session.getBlackboard()
				.getAnsweredQuestions();
		double restWeight = caseObject.totalAnsweredAbnormality;
		for (Question question : answeredQuestions) {
			Abnormality abnormality = getAbnormalitySlice(question);
			restWeight -= getAbnormality(abnormality,
					session.getBlackboard().getValue(question));
		}

		if (Math.abs(restWeight) > 1e-6) {
			throw new AssertionError();
		}
	}

	public double getAbnormality(Abnormality abnormality, Object answer) {
		// no answer ==> not abnormal
		if (answer == null || answer instanceof UndefinedValue) {
			return 0.0;
		}
		// no slice ==> every answer is abnormal
		if (abnormality == null || (!(answer instanceof Value))) return 1.0;

		double max = 0;
		// TODO: Explicit handling for MC Answers! (joba, 2010-03-11)
		max = abnormality.getValue((Value) answer);
		// for (Object a : answers) {
		// max = Math.max(max, abnormality.getValue((Answer) a));
		// }
		return max;
	}

	public Abnormality getAbnormalitySlice(Question question) {
		Abnormality knowledge = question.getInfoStore().getValue(
					BasicProperties.DEFAULT_ABNORMALITIY);
		if (knowledge == null) {
			knowledge = question.getInfoStore().getValue(BasicProperties.ABNORMALITIY_NUM);
		}
		return knowledge;
	}

	@Override
	public double getInformationGain(Collection<? extends QASet> qasets,
			Collection<Solution> solutions, Session session) {
		Map<List<Condition>, Float> map = new HashMap<List<Condition>, Float>();
		LinkedList<Question> questions = new LinkedList<Question>();
		LinkedList<TerminologyObject> tos = new LinkedList<TerminologyObject>();
		tos.addAll(qasets);
		flattenQASets(tos, questions);
		float totalweight = 0;
		for (Solution solution : solutions) {
			XCLModel model = solution.getKnowledgeStore().getKnowledge(XCLModel.KNOWLEDGE_KIND);
			if (model == null) continue;
			LinkedList<Set<Condition>> conditionsForQuestions = new LinkedList<Set<Condition>>();
			for (Question q : questions) {
				Set<Condition> set = new HashSet<Condition>();
				Set<XCLRelation> coveringRelations = model.getCoveringRelations(q);
				if (coveringRelations != null) {
					for (XCLRelation r : coveringRelations) {
						extractOrs(set, r.getConditionedFinding(), false);
					}
				}
				coveringRelations = model.getNegativeCoveringRelations(q);
				if (coveringRelations != null) {
					for (XCLRelation r : coveringRelations) {
						extractOrs(set, r.getConditionedFinding(), true);
					}
				}
				if (set.isEmpty()) set.add(null);
				conditionsForQuestions.add(set);
			}

			// multiply possible value sets to get pots
			// and add solution probabilities to these pots
			List<List<Condition>> combinations = getCombinations(new LinkedList<Set<Condition>>(
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

	private static List<List<Condition>> getCombinations(LinkedList<Set<Condition>> conditionsForQuestions) {
		List<List<Condition>> result = new LinkedList<List<Condition>>();
		if (conditionsForQuestions.isEmpty()) {
			result.add(Collections.<Condition> emptyList());
			return result;
		}
		Set<Condition> first = conditionsForQuestions.poll();
		List<List<Condition>> restresult = getCombinations(conditionsForQuestions);
		for (List<Condition> list : restresult) {
			for (Condition condition : first) {
				List<Condition> item = new LinkedList<Condition>();
				item.add(condition);
				item.addAll(list);
				result.add(item);
			}
		}
		return result;
	}

	private static void flattenQASets(List<? extends TerminologyObject> qasets, List<Question> questions) {
		for (TerminologyObject qaset : qasets) {
			if (qaset instanceof Question) {
				questions.add((Question) qaset);
			}
			TerminologyObject[] children = qaset.getChildren();
			flattenQASets(Arrays.asList(children), questions);
		}
	}

	private static void extractOrs(Collection<Condition> conds, Condition conditionedFinding, boolean contra) {
		if (conditionedFinding instanceof CondOr) {
			for (Condition condition : ((CondOr) conditionedFinding).getTerms()) {
				extractOrs(conds, condition, contra);
			}
		}
		else if ((conditionedFinding instanceof CondAnd)
				&& ((CondAnd) conditionedFinding).getTerms().size() == 1) {
			extractOrs(conds, ((CondAnd) conditionedFinding).getTerms().get(0), contra);
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
			if (contra) {
				conds.add(new ContraCondition(condEqual));
			}
			else {
				if (abnormality == Abnormality.A0) {
					conds.add(null);
				}
				else {
					conds.add(condEqual);
				}
			}
		}
		else {
			if (contra) {
				conds.add(new ContraCondition(conditionedFinding));
			}
			else {
				conds.add(conditionedFinding);
			}
		}

	}

	@Override
	public Collection<Solution> getUndiscriminatedSolutions(Session session) {
		List<Solution> solutions = session.getBlackboard().getSolutions(State.ESTABLISHED);
		if (solutions.size() > 0) {
			return solutions;
		}
		solutions = session.getBlackboard().getSolutions(State.SUGGESTED);
		if (solutions.size() > 0) {
			return solutions;
		}
		return session.getBlackboard().getSolutions(State.UNCLEAR);
	}

	@Override
	public Collection<Question> getDiscriminatingQuestions(
			Collection<Solution> solutions, Session session) {
		Set<Question> coveredSymptoms = new HashSet<Question>();
		for (Solution solution : solutions) {
			KnowledgeSlice ks = solution.getKnowledgeStore().getKnowledge(XCLModel.KNOWLEDGE_KIND);
			if (ks == null) continue;
			XCLModel model = (XCLModel) ks;
			for (TerminologyObject nob : model.getCoveredSymptoms()) {
				if (nob instanceof Question) {
					coveredSymptoms.add((Question) nob);
				}
			}
			for (TerminologyObject nob : model.getNegativeCoveredSymptoms()) {
				if (nob instanceof Question) {
					coveredSymptoms.add((Question) nob);
				}
			}
		}
		return coveredSymptoms;
	}

	@Override
	public void init(Session session) {
	}

	public static class XCLCaseObject implements SessionObject {

		private int totalAnsweredCount = 0;
		private double totalAnsweredAbnormality = 0.0;
	}

	@Override
	public XCLCaseObject createSessionObject(Session session) {
		return new XCLCaseObject();
	}

	public int getAnsweredQuestionsCount(Session session) {
		return session.getSessionObject(this).totalAnsweredCount;
	}

	public double getAnsweredQuestionsAbnormality(Session session) {
		return session.getSessionObject(this).totalAnsweredAbnormality;
	}

	/**
	 * @param scoreAlgorithm the scoreAlgorithm to set
	 */
	public void setScoreAlgorithm(ScoreAlgorithm scoreAlgorithm) {
		this.scoreAlgorithm = scoreAlgorithm;
	}

	/**
	 * @return the scoreAlgorithm
	 */
	public ScoreAlgorithm getScoreAlgorithm() {
		return scoreAlgorithm;
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		return Facts.mergeSolutionFacts(facts);
	}

	@Override
	public boolean hasType(Type type) {
		return type == Type.problem;
	}

	@Override
	public double getPriority() {
		// default priority
		return 5;
	}

	private static class ContraCondition extends NonTerminalCondition {

		public ContraCondition(Condition condition) {
			super(Arrays.asList(condition));
		}

		@Override
		public boolean eval(Session session) throws NoAnswerException, UnknownAnswerException {
			// is not called
			throw new IllegalAccessError();
		}

	}
}
