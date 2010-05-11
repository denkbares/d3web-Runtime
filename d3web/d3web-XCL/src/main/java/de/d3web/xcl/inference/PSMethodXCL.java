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

import java.util.Collection;
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
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.DiagnosisState;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.CaseObjectSource;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.shared.AbstractAbnormality;
import de.d3web.shared.PSMethodShared;
import de.d3web.xcl.DefaultScoreAlgorithm;
import de.d3web.xcl.ScoreAlgorithm;
import de.d3web.xcl.XCLContributedModelSet;
import de.d3web.xcl.XCLModel;
import de.d3web.xcl.XCLRelation;

public class PSMethodXCL implements PSMethod, StrategicSupport,
		CaseObjectSource {

	private ScoreAlgorithm scoreAlgorithm = new DefaultScoreAlgorithm();

	public PSMethodXCL() {
		super();
	}

	public void propagate(Session session, Collection<PropagationEntry> changes) {

		// update total weight
		updateAnsweredWeight(session, changes);

		// find xcl models to be updated (and remember affecting changes)
		Map<XCLModel, List<PropagationEntry>> modelsToUpdate = new HashMap<XCLModel, List<PropagationEntry>>();
		for (PropagationEntry change : changes) {
			NamedObject nob = change.getObject();
			KnowledgeSlice ks = nob.getKnowledge(
					PSMethodXCL.class, XCLContributedModelSet.XCL_CONTRIBUTED_MODELS);
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

	private void updateAnsweredWeight(Session theCase,
			Collection<PropagationEntry> changes) {
		XCLCaseObject caseObject = (XCLCaseObject) theCase.getCaseObject(this);
		for (PropagationEntry entry : changes) {
			if (entry.getObject() instanceof Question) {
				Question question = (Question) entry.getObject();

				// update count of question
				if (entry.hasOldValue()) caseObject.totalAnsweredCount--;
				if (entry.hasNewValue()) caseObject.totalAnsweredCount++;

				// update abnormalities
				AbstractAbnormality abnormality = getAbnormalitySlice(question);
				double oldAbnormality = getAbnormality(abnormality, entry.getOldValue());
				double newAbnormality = getAbnormality(abnormality, entry.getNewValue());
				caseObject.totalAnsweredAbnormality -= oldAbnormality;
				caseObject.totalAnsweredAbnormality += newAbnormality;
			}
		}

		// TODO: remove this assert calculation
		List<? extends Question> answeredQuestions = theCase.getBlackboard()
				.getAnsweredQuestions();
		double restWeight = caseObject.totalAnsweredAbnormality;
		for (Question question : answeredQuestions) {
			AbstractAbnormality abnormality = getAbnormalitySlice(question);
			restWeight -= getAbnormality(abnormality,
					theCase.getBlackboard().getValue(question));
		}

		if (Math.abs(restWeight) > 1e-6) {
			throw new AssertionError();
		}
	}

	public double getAbnormality(AbstractAbnormality abnormality, Object answer) {
		// no answer ==> not abnormal
		if (answer == null || answer instanceof UndefinedValue) {
			return 0.0;
		}
		// no slice ==> every answer is abnormal
		if (abnormality == null || (!(answer instanceof Value))) return 1.0;

		double max = 0;
		// TODO: Explicit Handling for MC Answers! (joba, 2010-03-11)
		max = abnormality.getValue((Value) answer);
		// for (Object a : answers) {
		// max = Math.max(max, abnormality.getValue((Answer) a));
		// }
		return max;
	}

	// public double getAbnormality(AbstractAbnormality abnormality, List<?>
	// answers) {
	// // no answer ==> not abnormal
	// if (answers == null || answers.size()==0)
	// return 0.0;
	// // no slice ==> every answer is abnormal
	// if (abnormality == null) return 1.0;
	// double max = 0;
	// for (Object a : answers) {
	// max = Math.max(max, abnormality.getValue((Answer) a));
	// }
	// return max;
	// }

	public AbstractAbnormality getAbnormalitySlice(Question question) {
		try {
			KnowledgeSlice knowledge = question.getKnowledge(PSMethodShared.class,
					PSMethodShared.SHARED_ABNORMALITY);
			if (knowledge == null) return null;
			return (AbstractAbnormality) knowledge;
		}
		catch (IllegalArgumentException e) {
			throw new IllegalStateException(
					"internal error accessing shared knowledge", e);
		}
		catch (SecurityException e) {
			throw new IllegalStateException(
					"internal error accessing shared knowledge", e);
		}
	}

	public double getEntropy(Collection<? extends QASet> qasets,
			Collection<Solution> solutions, Session theCase) {
		Map<Set<Condition>, Float> map = new HashMap<Set<Condition>, Float>();
		float totalweight = 0;
		for (Solution solution : solutions) {
			Set<Condition> pot = new HashSet<Condition>();
			KnowledgeSlice ks = solution.getKnowledge(PSMethodXCL.class,
					XCLModel.XCLMODEL);
			if (ks == null) continue;
			XCLModel model = (XCLModel) ks;
			addRelationConditions(pot, qasets, model);

			Float count = map.get(pot);
			Number apriori = (Number) solution.getProperties().getProperty(
					Property.APRIORI);
			float weight = (apriori == null) ? 1f : apriori.floatValue();
			totalweight += weight;
			if (count == null) {
				map.put(pot, weight);
			}
			else {
				map.put(pot, weight + count);
			}
		}
		// Russel & Norvig p. 805
		double sum = 0;
		for (Float relationcount : map.values()) {
			double p = (double) relationcount / totalweight;
			sum += (-1) * p * Math.log10(p) / Math.log10(2);
		}
		return sum;
	}

	private static void addRelationConditions(Set<Condition> pot,
			Collection<? extends NamedObject> qaset, XCLModel model) {
		for (NamedObject nob : qaset) {
			addRelationConditions(pot, nob, model);
		}
	}

	private static void addRelationConditions(Set<Condition> pot,
			TerminologyObject qaset, XCLModel model) {
		if (qaset instanceof Question) {
			Set<XCLRelation> coveringRelations = model
					.getCoveringRelations((Question) qaset);
			if (coveringRelations != null) {
				for (XCLRelation relation : coveringRelations) {
					pot.add(relation.getConditionedFinding());
				}
			}
		}
		for (TerminologyObject child : qaset.getChildren()) {
			addRelationConditions(pot, child, model);
		}

	}

	public Collection<Solution> getPossibleDiagnoses(Session theCase) {
		List<Solution> solutions = theCase.getSolutions(new DiagnosisState(
				DiagnosisState.State.ESTABLISHED));
		if (solutions.size() > 0) {
			return solutions;
		}
		solutions = theCase.getSolutions(new DiagnosisState(
				DiagnosisState.State.SUGGESTED));
		if (solutions.size() > 0) {
			return solutions;
		}
		return theCase.getSolutions(new DiagnosisState(DiagnosisState.State.UNCLEAR));
	}

	public Collection<Question> getDiscriminatingQuestions(
			Collection<Solution> solutions, Session theCase) {
		Set<Question> coveredSymptoms = new HashSet<Question>();
		for (Solution solution : solutions) {
			KnowledgeSlice ks = solution.getKnowledge(
					PSMethodXCL.class, XCLModel.XCLMODEL);
			if (ks == null) continue;
			XCLModel model = (XCLModel) ks;
			for (NamedObject nob : model.getCoveredSymptoms()) {
				if (nob instanceof Question) {
					coveredSymptoms.add((Question) nob);
				}
			}
		}
		return coveredSymptoms;
	}

	public void init(Session theCase) {
	}

	public boolean isContributingToResult() {
		return true;
	}

	private static class XCLCaseObject extends SessionObject {

		private int totalAnsweredCount = 0;
		private double totalAnsweredAbnormality = 0.0;

		private XCLCaseObject(PSMethodXCL methodXCL) {
			super(methodXCL);
		}
	}

	public SessionObject createCaseObject(Session session) {
		return new XCLCaseObject(this);
	}

	public int getAnsweredQuestionsCount(Session theCase) {
		XCLCaseObject caseObject = (XCLCaseObject) theCase.getCaseObject(this);
		return caseObject.totalAnsweredCount;
	}

	public double getAnsweredQuestionsAbnormality(Session theCase) {
		XCLCaseObject caseObject = (XCLCaseObject) theCase.getCaseObject(this);
		return caseObject.totalAnsweredAbnormality;
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
}
