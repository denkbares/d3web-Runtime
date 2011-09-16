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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.w3c.dom.Node;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.abnormality.Abnormality;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.DefaultBlackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.costbenefit.blackboard.CopiedSession;
import de.d3web.costbenefit.blackboard.DecoratedSession;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;
import de.d3web.indication.inference.PSMethodUserSelected;

/**
 * Provides basic static functions for the CostBenefit package.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public final class Util {

	/**
	 * Avoids the creation of an instance for this class.
	 */
	private Util() {
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
		Session testCase = new CopiedSession(session.getKnowledgeBase());
		((DefaultBlackboard) testCase.getBlackboard()).setAutosaveSource(false);
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
				FactFactory.createFact(session, question1,
						KnowledgeBaseUtils.findValue(question1, answer),
						PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));
		session.getPropagationManager().commitPropagation();
	}

	/**
	 * Returns the expected values of all Questions of the given QContainer
	 * 
	 * @param session {@link Session}
	 * @param qContainer {@link QContainer}
	 * @return Map with Questions as keys and Values as values
	 */
	public static Map<Question, Value> getExpectedValues(Session testCase, QContainer qContainer) {
		return answerGetterAndSetter(testCase, qContainer, false);
	}

	private static Map<Question, Value> answerGetterAndSetter(Session session, QContainer qContainer, boolean set) {
		List<QuestionOC> questions = new LinkedList<QuestionOC>();
		collectQuestions(qContainer, questions);
		Blackboard blackboard = session.getBlackboard();
		Map<Question, Value> valuesToSet = new HashMap<Question, Value>();
		Map<Question, Value> expectedmap = new HashMap<Question, Value>();
		for (QuestionOC q : questions) {
			Value value = blackboard.getValue(q);
			if (UndefinedValue.isUndefinedValue(value)) {
				DefaultAbnormality abnormality = q.getInfoStore().getValue(
						BasicProperties.DEFAULT_ABNORMALITIY);
				if (abnormality == null) {
					if (set) Logger.getLogger(Util.class.getName()).throwing(
							Util.class.getName(),
							"Fehler, kein Normalwert gesetzt: " + q, null);
					continue;
				}
				List<Choice> alternatives = q.getAllAlternatives();
				for (Choice a : alternatives) {
					ChoiceValue avalue = new ChoiceValue(a);
					if (abnormality.getValue(avalue) == Abnormality.A0) {
						if (set) {
							valuesToSet.put(q, avalue);
						}
						else {
							expectedmap.put(q, avalue);
						}
						break;
					}
				}
			}
			else {
				expectedmap.put(q, value);
			}
		}
		if (set) {
			return valuesToSet;
		}
		else {
			return expectedmap;
		}
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

				Fact fact = FactFactory.createFact(session, q, valuesToSet.get(q), source, psMethod);

				session.getBlackboard().addValueFact(fact);
				facts.add(fact);
			}
		}
		finally {
			session.getPropagationManager().commitPropagation();
		}
		return facts;
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
			else {
				addParentContainers(targets, qaset);
			}
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
		if (qaset instanceof Question) {
			Value value = session.getBlackboard().getValue((Question) qaset);
			if (UndefinedValue.isNotUndefinedValue(value)) {
				boolean done = true;
				for (TerminologyObject object : qaset.getChildren()) {
					done = done && isDone((QASet) object, session);
				}
				return done;
			}
			else {
				return false;
			}
		}
		else if (qaset instanceof QContainer) {
			boolean done = true;
			for (TerminologyObject object : qaset.getChildren()) {
				done = done && isDone((QASet) object, session);
			}
			return done;
		}
		return true;
	}
}
