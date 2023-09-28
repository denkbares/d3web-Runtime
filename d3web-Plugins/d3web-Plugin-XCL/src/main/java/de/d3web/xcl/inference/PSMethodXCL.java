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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.StrategicSupport;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.ConditionCache;
import de.d3web.core.inference.condition.DefaultConditionCache;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.abnormality.Abnormality;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.xcl.DefaultScoreAlgorithm;
import de.d3web.xcl.InferenceTrace;
import de.d3web.xcl.ScoreAlgorithm;
import de.d3web.xcl.XCLContributedModelSet;
import de.d3web.xcl.XCLModel;
import de.d3web.xcl.XCLRelation;

public final class PSMethodXCL implements PSMethod, StrategicSupport,
		SessionObjectSource<PSMethodXCL.XCLCaseObject> {

	public static final String PLUGIN_ID = "d3web-XCL";
	public static final String EXTENSION_ID = "PSMethodXCL";

	private ScoreAlgorithm scoreAlgorithm = new DefaultScoreAlgorithm();
	private final StrategicSupport strategicSupport = new StrategicSupportXCLCached(1000);

	public PSMethodXCL() {
		super();
	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {

		// find xcl models to be updated (and remember affecting changes)
		Map<XCLModel, List<PropagationEntry>> modelsToUpdate = new HashMap<>();
		List<PropagationEntry> answerChanges = new ArrayList<>();
		for (PropagationEntry change : changes) {
			// do not handle strategic changes
			if (change.isStrategic() || !change.hasChanged()) continue;
			TerminologyObject nob = change.getObject();
			XCLContributedModelSet xclSet = nob.getKnowledgeStore().getKnowledge(XCLContributedModelSet.KNOWLEDGE_KIND);
			if (xclSet != null) {
				// if object is a question, consider updated answer
				if (change.getObject() instanceof Question) {
					answerChanges.add(change);
				}
				// if object is contributing, update the referred models by the change
				for (XCLModel model : xclSet.getModels()) {
					modelsToUpdate.computeIfAbsent(model, k -> new ArrayList<>()).add(change);
				}
			}
		}

		// update total weight of answers
		updateAnsweredWeight(session, answerChanges);

		// update required xcl models / inference traces
		ConditionCache cache = new DefaultConditionCache(session);
		for (XCLModel model : modelsToUpdate.keySet()) {
			List<PropagationEntry> entries = modelsToUpdate.get(model);
			this.scoreAlgorithm.update(model, entries, cache);
		}

		// refresh the solutions states
		this.scoreAlgorithm.refreshStates(modelsToUpdate.keySet(), session);
	}

	private void updateAnsweredWeight(Session session, Collection<PropagationEntry> changes) {
		XCLCaseObject caseObject = session.getSessionObject(this);
		for (PropagationEntry entry : changes) {
			if (entry.getObject() instanceof Question question) {

				// update count of question
				boolean hasOldValue = entry.hasOldValue();
				boolean hasNewValue = entry.hasNewValue();
				if (hasOldValue != hasNewValue) {
					if (hasOldValue) {
//						caseObject.totalAnsweredCount--;
						caseObject.answeredQuestions.remove(question);
					}
					else {
//						caseObject.totalAnsweredCount++;
						caseObject.answeredQuestions.add(question);
					}
				}

				// update abnormalities
				Abnormality abnormality = getAbnormalitySlice(question);
				double oldAbnormality = getAbnormality(abnormality, entry.getOldValue());
				double newAbnormality = getAbnormality(abnormality, entry.getNewValue());
				caseObject.totalAnsweredAbnormality -= oldAbnormality;
				caseObject.totalAnsweredAbnormality += newAbnormality;
			}
		}
	}

	public double getAbnormality(Abnormality abnormality, Object answer) {
		// no answer ==> not abnormal
		if (answer == null || answer instanceof UndefinedValue) {
			return 0.0;
		}
		// no slice ==> every answer is abnormal
		if (abnormality == null || (!(answer instanceof Value))) return 1.0;

		double max;
		max = abnormality.getValue((Value) answer);
		return max;
	}

	public Abnormality getAbnormalitySlice(Question question) {
		Abnormality knowledge = question.getInfoStore().getValue(
				BasicProperties.DEFAULT_ABNORMALITY);
		if (knowledge == null) {
			knowledge = question.getInfoStore().getValue(BasicProperties.ABNORMALITY_NUM);
		}
		return knowledge;
	}

	@Override
	public void init(Session session) {
	}

	public static class XCLCaseObject implements SessionObject {

		private final Set<Question> answeredQuestions = new HashSet<>();
		private double totalAnsweredAbnormality = 0.0;
	}

	@Override
	public XCLCaseObject createSessionObject(Session session) {
		return new XCLCaseObject();
	}

	public Set<Question> getAnsweredQuestions(Session session) {
		return Collections.unmodifiableSet(session.getSessionObject(this).answeredQuestions);
	}

	public int getAnsweredQuestionsCount(Session session) {
		return session.getSessionObject(this).answeredQuestions.size();
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

	@Override
	public Collection<Solution> getUndiscriminatedSolutions(Session session) {
		return strategicSupport.getUndiscriminatedSolutions(session);
	}

	@Override
	public Collection<Question> getDiscriminatingQuestions(Collection<Solution> solutions, Session session) {
		return strategicSupport.getDiscriminatingQuestions(solutions, session);
	}

	@Override
	public double getInformationGain(Collection<? extends QASet> qasets, Collection<Solution> solutions, Session session) {
		return strategicSupport.getInformationGain(qasets, solutions, session);
	}

	@Override
	public boolean hasGroupInformationGain(Collection<? extends QASet> qaSets, Collection<Solution> solutions, Session session) {
		return strategicSupport.hasGroupInformationGain(qaSets, solutions, session);
	}

	@Override
	public Set<TerminologyObject> getPotentialDerivationSources(TerminologyObject derivedObject) {
		Set<TerminologyObject> result = new HashSet<>();
		XCLModel model = derivedObject.getKnowledgeStore().getKnowledge(XCLModel.KNOWLEDGE_KIND);
		if (model == null) return Collections.emptySet();
		addAllObjects(result, model.getRelations());
		addAllObjects(result, model.getNecessaryRelations());
		addAllObjects(result, model.getSufficientRelations());
		return result;
	}

	@Override
	public Set<TerminologyObject> getActiveDerivationSources(TerminologyObject derivedObject, Session session) {
		if (session == null) throw new NullPointerException();
		Set<TerminologyObject> result = new HashSet<>();
		XCLModel model = derivedObject.getKnowledgeStore().getKnowledge(XCLModel.KNOWLEDGE_KIND);
		if (model == null) return Collections.emptySet();
		InferenceTrace trace = model.getInferenceTrace(session);
		addAllObjects(result, trace.getPosRelations());
		addAllObjects(result, trace.getReqPosRelations());
		addAllObjects(result, trace.getSuffRelations());
		return result;
	}

	private void addAllObjects(Set<TerminologyObject> result, Collection<XCLRelation> relations) {
		for (XCLRelation relation : relations) {
			// add precondition values
			Condition condition = relation.getConditionedFinding();
			if (condition != null) {
				result.addAll(condition.getTerminalObjects());
			}
		}
	}
}
