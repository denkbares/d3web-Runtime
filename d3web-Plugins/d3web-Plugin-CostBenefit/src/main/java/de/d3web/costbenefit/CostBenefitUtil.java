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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Node;

import com.denkbares.strings.NumberAwareComparator;
import com.denkbares.utils.Log;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.StrategicSupport;
import de.d3web.core.inference.condition.Conditions;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionZC;
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
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.costbenefit.blackboard.CopiedSession;
import de.d3web.costbenefit.blackboard.DecoratedSession;
import de.d3web.costbenefit.blackboard.DerivedSession;
import de.d3web.costbenefit.inference.CostBenefitProperties;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.interview.FormStrategy;
import de.d3web.interview.Interview;
import de.d3web.xcl.InferenceTrace;
import de.d3web.xcl.XCLModel;
import de.d3web.xcl.XCLRelation;

/**
 * Provides basic static functions for the CostBenefit package.
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public final class CostBenefitUtil {

	public static final int LOG_THRESHOLD = 5000;

	/**
	 * Avoids the creation of an instance for this class.
	 */
	private CostBenefitUtil() {
	}

	public static Session createDecoratedSession(Session session) {
		return new DecoratedSession(session);
	}

	/**
	 * Creates a session that has the same value facts as the specified session, without any problem or strategic
	 * solver. Neither interview facts, nor {@link SessionObject}s, nor the interview agenda, nor the protocol will be
	 * copied.
	 *
	 * @param session the session to be copied
	 * @return the created copy with value facts only
	 * @created 15.09.2011
	 */
	public static Session createSearchCopy(Session session) {
		Session copy = new CopiedSession(session);
		copy.getBlackboard().setSourceRecording(false);
		Blackboard blackboard = session.getBlackboard();
		List<? extends Question> answeredQuestions = blackboard.getAnsweredQuestions();
		try {
			copy.getPropagationManager().openPropagation();
			for (Question q : answeredQuestions) {
				Fact fact = blackboard.getValueFact(q);
				copy.getBlackboard().addValueFact(fact);
			}
		}
		finally {
			copy.getPropagationManager().commitPropagation();
		}
		return copy;
	}

	/**
	 * Get a sorted list of all QContainers that stand for a test step (contain StateTransition knowledge)
	 *
	 * @param knowledgeBase the knowledge base to get the test steps from
	 * @return a sorted list with all test step QContainers
	 */
	public static List<QContainer> getTestSteps(KnowledgeBase knowledgeBase) {
		return knowledgeBase.getManager()
				.getQContainers()
				.stream()
				.filter(qContainer -> StateTransition.getStateTransition(qContainer) != null)
				.sorted((Comparator.comparing(TerminologyObject::getName, NumberAwareComparator.CASE_INSENSITIVE)))
				.collect(Collectors.toList());
	}

	public static void undo(Session session, Collection<Fact> facts) {
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

	private static Map<Question, Value> getExpectedValues(Session session, QContainer qContainer) {
		// get all one choice questions to set the values for,
		// and build a map of the expected values
		Map<Question, Value> expectedValues = new HashMap<>();
		Blackboard blackboard = session.getBlackboard();
		for (Question question : getFormStrategy(session).getActiveQuestions(qContainer, session)) {
			if (question instanceof QuestionText) {
				expectedValues.put(question, new TextValue("Dummy-Text"));
				continue;
			}
			if (question instanceof QuestionZC) {
				expectedValues.put(question, Unknown.getInstance());
				continue;
			}

			// we expect choice question from here on
			if (!(question instanceof QuestionChoice)) continue;

			Value value = blackboard.getValue(question);
			if (!UndefinedValue.isUndefinedValue(value)) continue;
			QuestionChoice questionChoice = (QuestionChoice) question;

			// if question has only one choice, use the choice
			List<Choice> alternatives = questionChoice.getAllAlternatives();
			if (alternatives.size() == 1) {
				expectedValues.put(questionChoice, new ChoiceValue(alternatives.get(0)));
				continue;
			}

			// if no abnormality is defined, warn and skip
			DefaultAbnormality abnormality = questionChoice.getInfoStore()
					.getValue(BasicProperties.DEFAULT_ABNORMALITY);
			if (abnormality == null) {
				Log.info("no normal value for question " + questionChoice);
				continue;
			}

			// otherwise us the first normal alternative
			for (Choice choice : alternatives) {
				if (abnormality.getValue(choice) == Abnormality.A0) {
					expectedValues.put(questionChoice, new ChoiceValue(choice));
					break;
				}
			}
		}
		return expectedValues;
	}

	/**
	 * Collects a list of all {@link QuestionOC}, being child of the defined QContainer
	 *
	 * @param qContainer defined QContainer
	 * @return List of QuestionOC
	 * @created 30.11.2012
	 */
	public static List<QuestionOC> getQuestionOCs(QContainer qContainer) {
		LinkedList<QuestionOC> result = new LinkedList<>();
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
	 * Ensures that all questions of the given QContainer are answered. For unanswered Questions the expected values are
	 * set. The values are answered by the user-problem-solver.
	 *
	 * @param session    the Session where the values should be set
	 * @param qContainer {@link QContainer}
	 * @return all Facts that are used to set the values
	 */
	@NotNull
	public static List<Fact> setNormalValues(Session session, QContainer qContainer) {
		PSMethod psm = PSMethodUserSelected.getInstance();
		return setNormalValues(session, qContainer, psm, psm);
	}

	/**
	 * Ensures that all questions of the given QContainer are answered. For unanswered Questions the expected values are
	 * set.
	 *
	 * @param session    the Session where the values should be set
	 * @param qContainer {@link QContainer}
	 * @param source     of the created Facts
	 * @return all Facts that are used to set the values
	 */
	@NotNull
	public static List<Fact> setNormalValues(Session session, QContainer qContainer, Object source) {
		PSMethod psm = session.getPSMethodInstance(PSMethodCostBenefit.class);
		return setNormalValues(session, qContainer, source, psm);
	}

	/**
	 * Ensures that all questions of the given QContainer are answered. For unanswered Questions the expected values are
	 * set.
	 *
	 * @param session    the Session where the values should be set
	 * @param qContainer {@link QContainer}
	 * @param source     of the created Facts
	 * @param psm        problemsolver to create the facts for
	 * @return all Facts that are used to set the values
	 */
	@NotNull
	public static List<Fact> setNormalValues(Session session, QContainer qContainer, Object source, PSMethod psm) {
		Map<Question, Value> valuesToSet = getExpectedValues(session, qContainer);
		List<Fact> facts = new LinkedList<>();
		if (psm == null) psm = new PSMethodCostBenefit();
		try {
			session.getPropagationManager().openPropagation();
			for (Question q : valuesToSet.keySet()) {
				Fact fact = FactFactory.createFact(q, valuesToSet.get(q), source, psm);
				session.getBlackboard().addValueFact(fact);
				facts.add(fact);
			}
		}
		finally {
			session.getPropagationManager().commitPropagation();
		}
		return facts;
	}

	/**
	 * Returns the form strategy that is used in the specified session. The session may be a derived session, then the
	 * root form strategy is used.
	 *
	 * @param session the session to get the form strategy for
	 * @return the form strategy to be used
	 */
	public static FormStrategy getFormStrategy(Session session) {
		session = DerivedSession.getRootSession(session);
		Interview interview = Interview.get(session);
		return interview.getFormStrategy();
	}

	public static void addParentContainers(Set<QContainer> targets, TerminologyObject q) {
		for (TerminologyObject qaset : q.getParents()) {
			if (qaset instanceof QContainer) {
				targets.add((QContainer) qaset);
			}
			addParentContainers(targets, qaset);
		}
	}

	/**
	 * Checks, if all questions, contained in the specified {@link QASet} have a value assigned to them in the specified
	 * session.
	 *
	 * @param qaset   the qaset to be checked
	 * @param session the specified session
	 * @return if the qaset is fully answered
	 */
	public static boolean isDone(InterviewObject qaset, Session session) {
		Interview interview = Interview.get(session);
		for (Question q : interview.getFormStrategy().getActiveQuestions(qaset, session)) {
			if (UndefinedValue.isUndefinedValue(session.getBlackboard().getValue(q))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Evaluates if the terminology object has a permanently relevant parent
	 *
	 * @param termObject Terminology Object
	 * @return true if a permanently relevant parent exists, false otherwise
	 * @created 13.11.2013
	 */
	public static boolean hasPermanentlyRelevantParent(TerminologyObject termObject) {
		for (TerminologyObject parent : KnowledgeBaseUtils.getAncestors(termObject)) {
			if ((parent instanceof QContainer) && CostBenefitProperties.isPermanentlyRelevant((QContainer) parent)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Calculates a set of questions, having negative affects on the solutions size of the current solutions
	 *
	 * @created 13.12.2013
	 */
	public static Set<TerminologyObject> calculatePossibleConflictingQuestions(Session session, Collection<Solution> solutions) {
		Set<TerminologyObject> negativeObjects = new HashSet<>();
		// TODO: handle positive relations?
		for (Solution s : solutions) {
			XCLModel model = s.getKnowledgeStore().getKnowledge(XCLModel.KNOWLEDGE_KIND);
			if (model == null) continue;
			InferenceTrace inferenceTrace = model.getInferenceTrace(session);
			addObjectsOfConditions(negativeObjects, inferenceTrace.getNegRelations());
			addObjectsOfConditions(negativeObjects, inferenceTrace.getReqNegRelations());
		}
		return negativeObjects;
	}

	/**
	 * Returns a set of all objects, having a negative influence on the sprint group.
	 * <p>
	 * Note: This method is only updated, if a QContainer is completed.
	 *
	 * @param session actual Session
	 * @return a set of conflicting objects
	 * @created 20.12.2013
	 */
	public static Set<TerminologyObject> getConflictingObjects(Session session) {
		return session.getSessionObject(session.getPSMethodInstance(PSMethodCostBenefit.class))
				.getConflictingObjects();
	}

	private static void addObjectsOfConditions(Set<TerminologyObject> positiveObjects, Collection<XCLRelation> relations) {
		for (XCLRelation r : relations) {
			positiveObjects.addAll(r.getConditionedFinding().getTerminalObjects());
		}
	}

	/**
	 * Checks if the path is applicable in the actual session from the actual position.
	 *
	 * @param path          actual path
	 * @param session       specified session
	 * @param position      actual position in the path
	 * @param sessionIsCopy if the flag is set to true, the specified session is modified in this method, should only be
	 *                      used if an copied session is used
	 * @return true if the path is applicable in the session
	 * @created 26.02.2014
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
	 * @param qcon    QContainer
	 * @param session Session
	 * @return true if the qcontainer is applicable
	 * @created 26.02.2014
	 */
	public static boolean isApplicable(QContainer qcon, Session session) {
		StateTransition stateTransition = StateTransition.getStateTransition(qcon);
		// TODO: consider to check comfort-precondition of test steps, but only (!) if they are added for comfort purposes only (and not if they are required path elements)
		if (stateTransition != null && stateTransition.getActivationCondition() != null) {
			return Conditions.isTrue(stateTransition.getActivationCondition(), session);
		}
		return true;
	}

	public static List<StrategicSupport> getStrategicSupports(Session session) {
		List<StrategicSupport> ret = new ArrayList<>();
		for (PSMethod psm : session.getPSMethods()) {
			if (psm instanceof StrategicSupport) {
				ret.add((StrategicSupport) psm);
			}
		}
		return ret;
	}

	/**
	 * Calculates all values of final questions that can be reached in the actual session by setting the normal values
	 * in a QContainer (if no values are set)
	 *
	 * @param session actual Session
	 * @return Map representing the result
	 * @created 09.09.2014
	 */
	public static Map<Question, Set<Value>> calculateReachableFinalValues(Session session) {
		Map<Question, Set<Value>> result = new HashMap<>();
		Session copiedSession = new CopiedSession(session);
		for (StateTransition st : StateTransition.getAll(session)) {
			// ignore permanently relevant QContainer
			if (CostBenefitProperties.isPermanentlyRelevant(st.getQContainer())) {
				continue;
			}
			setNormalValues(copiedSession, st.getQContainer(), new Object());
			List<Fact> facts = st.fire(copiedSession);
			for (Fact fact : facts) {
				if (CostBenefitProperties.isCheckOnce(fact.getTerminologyObject())) {
					//noinspection SuspiciousMethodCalls
					Set<Value> set = result.get(fact.getTerminologyObject());
					if (set == null) {
						set = new HashSet<>();
						result.put((Question) fact.getTerminologyObject(), set);
					}
					set.add(fact.getValue());
				}
			}
		}
		return result;
	}

	public static void log(long duration, String message) {
		Log.mock(1, duration <= LOG_THRESHOLD ? Level.FINE : Level.INFO, message);
	}
}
