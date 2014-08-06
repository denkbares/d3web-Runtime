/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.costbenefit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Node;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.StrategicSupport;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.Conditions;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.abnormality.Abnormality;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.costbenefit.blackboard.CopiedSession;
import de.d3web.costbenefit.blackboard.DecoratedSession;
import de.d3web.costbenefit.inference.ComfortBenefit;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;
import de.d3web.costbenefit.inference.PathExtender;
import de.d3web.costbenefit.inference.SearchAlgorithm;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.inference.astar.AStarAlgorithm;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.interview.Form;
import de.d3web.interview.FormStrategy;
import de.d3web.interview.Interview;
import de.d3web.interview.inference.PSMethodInterview;
import de.d3web.utils.Log;
import de.d3web.xcl.InferenceTrace;
import de.d3web.xcl.XCLModel;
import de.d3web.xcl.XCLRelation;

/**
 * Provides basic static functions for the CostBenefit package.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public final class CostBenefitUtil {

	/**
	 * Avoids the creation of an instance for this class.
	 */
	private CostBenefitUtil() {
	}

	public static Session createDecoratedSession(Session session) {
		return new DecoratedSession(session);
	}

	/**
	 * Creates a session that has the same value facts as the specified session,
	 * without any problem or strategic solver. Neither interview facts, nor
	 * {@link SessionObject}s, nor the interview agenda, nor the protocol will
	 * be copied.
	 * 
	 * @created 15.09.2011
	 * @param session the session to be copied
	 * @return the created copy with value facts only
	 */
	public static Session createSearchCopy(Session session) {
		Session testCase = new CopiedSession(session);
		testCase.getBlackboard().setSourceRecording(false);
		Blackboard blackboard = session.getBlackboard();
		List<? extends Question> answeredQuestions = blackboard.getAnsweredQuestions();
		try {
			testCase.getPropagationManager().openPropagation();
			for (Question q : answeredQuestions) {
				Fact fact = blackboard.getValueFact(q);
				testCase.getBlackboard().addValueFact(fact);
			}
		}
		finally {
			testCase.getPropagationManager().commitPropagation();
		}
		return testCase;
	}

	public static void undo(Session session, List<Fact> facts) {
		try {
			session.getPropagationManager().openPropagation();
			for (Fact fact : facts) {
				session.getBlackboard().removeValueFact(fact);
			}
		}
		finally {
			session.getPropagationManager().commitPropagation();
		}
	}

	public static String getAttribute(String name, Node node) {
		if ((node != null) && (node.getAttributes() != null)) {
			Node namedItem = node.getAttributes().getNamedItem(name);
			if (namedItem != null) {
				return namedItem.getNodeValue();
			}
		}
		return null;
	}

	public static void setQuestion(Session session, String question, String answer) {
		KnowledgeBase kb = session.getKnowledgeBase();
		session.getPropagationManager().openPropagation();
		QuestionOC question1 = (QuestionOC) kb.getManager().searchQuestion(question);
		session.getBlackboard().addValueFact(
				FactFactory.createFact(question1,
						KnowledgeBaseUtils.findValue(question1, answer),
						PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));
		session.getPropagationManager().commitPropagation();
	}

	/**
	 * @param set if true, the default values of all unanswered questions are
	 *        returned, if false additionally the values of the answered
	 *        questions from the blackboard are returned
	 */
	private static Map<Question, Value> answerGetterAndSetter(Session session, QContainer qContainer, boolean set) {
		List<QuestionOC> questions = new LinkedList<QuestionOC>();
		FormStrategy formStrategy = getFormStrategy(session);
		for (Question q : formStrategy.getForm(qContainer, session).getActiveQuestions()) {
			if (q instanceof QuestionOC) {
				questions.add((QuestionOC) q);
			}
		}
		Blackboard blackboard = session.getBlackboard();
		Map<Question, Value> expectedmap = new HashMap<Question, Value>();
		for (QuestionOC q : questions) {
			Value value = blackboard.getValue(q);
			if (UndefinedValue.isUndefinedValue(value)) {
				DefaultAbnormality abnormality = q.getInfoStore().getValue(
						BasicProperties.DEFAULT_ABNORMALITIY);
				if (abnormality == null) {
					if (set) {
						Log.info("no normal value for question " + q);
					}
					continue;
				}
				List<Choice> alternatives = q.getAllAlternatives();
				for (Choice a : alternatives) {
					ChoiceValue avalue = new ChoiceValue(a);
					if (abnormality.getValue(avalue) == Abnormality.A0) {
						expectedmap.put(q, avalue);
						break;
					}
				}
			}
			else {
				if (!set) {
					expectedmap.put(q, value);
				}
			}
		}
		return expectedmap;
	}

	/**
	 * Collects a list of all {@link QuestionOC}, being child of the defined
	 * QContainer
	 * 
	 * @created 30.11.2012
	 * @param qContainer defined QContainer
	 * @return List of QuestionOC
	 */
	public static List<QuestionOC> getQuestionOCs(QContainer qContainer) {
		LinkedList<QuestionOC> result = new LinkedList<QuestionOC>();
		collectQuestions(qContainer, result);
		return result;
	}

	private static void collectQuestions(TerminologyObject namedObject, List<QuestionOC> result) {
		if (namedObject instanceof QuestionOC && !result.contains(namedObject)) {
			result.add((QuestionOC) namedObject);
		}
		for (TerminologyObject child : namedObject.getChildren()) {
			collectQuestions(child, result);
		}
	}

	/**
	 * Ensures that all questions of the given QContainer are answered. For
	 * unanswered Questions the expected values are set.
	 * 
	 * @param session the Session where the values should be set
	 * @param qContainer {@link QContainer}
	 * @param source of the created Facts
	 * @return all Facts that are used to set the values
	 */
	public static List<Fact> setNormalValues(Session session, QContainer qContainer, Object source) {
		Map<Question, Value> valuesToSet = answerGetterAndSetter(session, qContainer, true);
		List<Fact> facts = new LinkedList<Fact>();
		PSMethod psmCostBenefit = session.getPSMethodInstance(PSMethodCostBenefit.class);
		try {
			session.getPropagationManager().openPropagation();
			for (Question q : valuesToSet.keySet()) {
				// Fact fact = new DefaultFact(q, valuesToSet.get(q), this,
				// (psmCostBenefit == null) ? new PSMethodCostBenefit() :
				// psmCostBenefit);

				PSMethod psMethod = (psmCostBenefit == null)
						? new PSMethodCostBenefit()
						: psmCostBenefit;

				Fact fact = FactFactory.createFact(q, valuesToSet.get(q), source, psMethod);

				session.getBlackboard().addValueFact(fact);
				facts.add(fact);
			}
		}
		finally {
			session.getPropagationManager().commitPropagation();
		}
		return facts;
	}

	private static FormStrategy getFormStrategy(Session session) {
		if (session instanceof DecoratedSession) {
			DecoratedSession ds = (DecoratedSession) session;
			return getFormStrategy(ds.getRootSession());
		}
		else if (session instanceof CopiedSession) {
			CopiedSession cs = (CopiedSession) session;
			return getFormStrategy(cs.getOriginalSession());
		}
		Interview interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
		return interview.getFormStrategy();
	}

	/*
	 * private static class CoveringSession extends DefaultSession {
	 * 
	 * public CoveringSession(DefaultSession coveredSession) { super(null,
	 * coveredSession.getKnowledgeBase(), new Date(), false, false);
	 * DefaultBlackboard coveredBlackboard = (DefaultBlackboard)
	 * coveredSession.getBlackboard(); setBlackboard(new
	 * CoveringBlackboard(this, coveredBlackboard)); } }
	 * 
	 * private static class CoveringBlackboard extends DefaultBlackboard {
	 * 
	 * public CoveringBlackboard(CoveringSession thisSession, DefaultBlackboard
	 * coveredBlackboard) { super(thisSession, new
	 * CoveringFactStorage(coveredBlackboard.getValueStorage()), new
	 * CoveringFactStorage(coveredBlackboard.getInterviewStorage())); } }
	 * 
	 * private static class CoveringFactStorage extends FactStorage {
	 * 
	 * public CoveringFactStorage(FactStorage factStorage) { super(); } }
	 */
	public static void addParentContainers(Set<QContainer> targets,
			TerminologyObject q) {
		for (TerminologyObject qaset : q.getParents()) {
			if (qaset instanceof QContainer) {
				targets.add((QContainer) qaset);
			}
			addParentContainers(targets, qaset);
		}

	}

	/**
	 * Checks, if all questions, contained in the specified {@link QASet} have a
	 * value assigned to them in the specified session.
	 * 
	 * @param qaset the qaset to be checked
	 * @param session the specified session
	 * @return if the qaset is fully answered
	 */
	public static boolean isDone(InterviewObject qaset, Session session) {
		Interview interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
		Form form = interview.getFormStrategy().getForm(qaset, session);
		for (Question q : form.getActiveQuestions()) {
			if (UndefinedValue.isUndefinedValue(session.getBlackboard().getValue(q))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Offers easy access to the AStarAlgorithm even if it is capsuled with the
	 * path extender. If no AStar is used in the {@link PSMethodCostBenefit},
	 * null is returned
	 * 
	 * @created 04.12.2012
	 * @param psMethodCostBenefit specified PSMethod
	 * @return configured instance of AStarAlgorithm or null, if another
	 *         algorithm is used.
	 */
	public static AStarAlgorithm getAStarAlogrithm(PSMethodCostBenefit psMethodCostBenefit) {
		SearchAlgorithm searchAlgorithm = psMethodCostBenefit.getSearchAlgorithm();
		AStarAlgorithm aStar = null;
		if (searchAlgorithm instanceof PathExtender) {
			PathExtender extender = (PathExtender) searchAlgorithm;
			if (extender.getSubalgorithm() instanceof AStarAlgorithm) {
				aStar = (AStarAlgorithm) extender.getSubalgorithm();
			}
		}
		else if (searchAlgorithm instanceof AStarAlgorithm) {
			aStar = (AStarAlgorithm) searchAlgorithm;
		}
		return aStar;
	}

	/**
	 * Evaluates if the terminology object has a permanently relevant parent
	 * 
	 * @created 13.11.2013
	 * @param termObject Terminology Object
	 * @return true if a permanently relevant parent exists, false otherwise
	 */
	public static boolean hasPermanentlyRelevantParent(TerminologyObject termObject) {
		for (TerminologyObject parent : KnowledgeBaseUtils.getAncestors(termObject)) {
			if (parent.getInfoStore().getValue(PSMethodCostBenefit.PERMANENTLY_RELEVANT)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Calculates a set of questions, having negative affects on the solutions
	 * size of the current solutions
	 * 
	 * @created 13.12.2013
	 * @param solutions
	 */
	public static Set<TerminologyObject> calculatePossibleConflictingQuestions(Session session, Collection<Solution> solutions) {
		Set<TerminologyObject> negativeObjects = new HashSet<TerminologyObject>();
		// TODO: handle positive relations?
		for (Solution s : solutions) {
			XCLModel model = s.getKnowledgeStore().getKnowledge(XCLModel.KNOWLEDGE_KIND);
			InferenceTrace inferenceTrace = model.getInferenceTrace(session);
			addObjectsOfConditions(negativeObjects, inferenceTrace.getNegRelations());
			addObjectsOfConditions(negativeObjects, inferenceTrace.getReqNegRelations());
		}
		return negativeObjects;
	}

	/**
	 * Returns a set of all objects, having a negative influence on the sprint
	 * group.
	 * 
	 * Note: This method is only updated, if a QContainer is completed.
	 * 
	 * @created 20.12.2013
	 * @param session actual Session
	 * @return a set of conflicting objects
	 */
	public static Set<TerminologyObject> getConflictingObjects(Session session) {
		return session.getSessionObject(session.getPSMethodInstance(PSMethodCostBenefit.class)).getConflictingObjects();
	}

	private static void addObjectsOfConditions(Set<TerminologyObject> positiveObjects, Collection<XCLRelation> relations) {
		for (XCLRelation r : relations) {
			positiveObjects.addAll(r.getConditionedFinding().getTerminalObjects());
		}
	}

	/**
	 * Checks if the path is applicable in the actual session from the actual
	 * position.
	 * 
	 * @created 26.02.2014
	 * @param path actual path
	 * @param session specified session
	 * @param position actual position in the path
	 * @param sessionIsCopy if the flag is set to true, the specified session is
	 *        modified in this method, should only be used if an copied session
	 *        is used
	 * @return true if the path is applicable in the session
	 */
	public static boolean checkPath(List<QContainer> path, Session session, int position, boolean sessionIsCopy) {
		if (!sessionIsCopy) session = createSearchCopy(session);
		Object dummySource = new Object();
		for (int i = position; i < path.size(); i++) {
			QContainer qContainer = path.get(i);
			if (!CostBenefitUtil.isApplicable(qContainer, session)) {
				return false;
			}
			setNormalValues(session, qContainer, dummySource);
			StateTransition stateTransition = StateTransition.getStateTransition(qContainer);
			if (stateTransition != null) stateTransition.fire(session);
		}
		return true;
	}

	/**
	 * Checks if a qcontainer is applicable in the actual session
	 * 
	 * @created 26.02.2014
	 * @param qcon QContainer
	 * @param session Session
	 * @return true if the qcontainer is applicable
	 */
	public static boolean isApplicable(QContainer qcon, Session session) {
		StateTransition stateTransition = StateTransition.getStateTransition(qcon);
		ComfortBenefit comfort = qcon.getKnowledgeStore().getKnowledge(
				ComfortBenefit.KNOWLEDGE_KIND);
		Condition condition = null;
		if (stateTransition != null && stateTransition.getActivationCondition() != null
				&& comfort != null) {
			List<Condition> conds = new LinkedList<Condition>();
			conds.add(stateTransition.getActivationCondition());
			conds.add(comfort.getCondition());
			condition = new CondAnd(conds);
		}
		else if (comfort != null) {
			condition = comfort.getCondition();
		}
		else if (stateTransition != null && stateTransition.getActivationCondition() != null) {
			condition = stateTransition.getActivationCondition();
		}
		if (condition != null) {
			return Conditions.isTrue(condition, session);
		}
		else {
			return true;
		}
	}

	public static List<StrategicSupport> getStrategicSupports(Session session) {
		List<StrategicSupport> ret = new ArrayList<StrategicSupport>();
		for (PSMethod psm : session.getPSMethods()) {
			if (psm instanceof StrategicSupport) {
				ret.add((StrategicSupport) psm);
			}
		}
		return ret;
	}
}
