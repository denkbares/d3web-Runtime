/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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
package de.d3web.core.inference.condition.tests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondDState;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondKnown;
import de.d3web.core.inference.condition.CondMofN;
import de.d3web.core.inference.condition.CondUnknown;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.scoring.Score;


/**
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 20.08.2010
 */
public class ConditionMiscellaneousTest {

	KnowledgeBaseManagement kbm;
	KnowledgeBase kb;
	QContainer init;
	QuestionOC choiceQuestion1, choiceQuestion2, choiceQuestion3;
	Condition conditionQ1Yes, conditionQ2Yes, conditionQ3No;
	ChoiceValue choiceValueYes, choiceValueNo;
	Condition[] conditions;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kbm = KnowledgeBaseManagement.createInstance();
		kb = kbm.getKnowledgeBase();
		init = kbm.createQContainer("init");
		choiceQuestion1 = kbm.createQuestionOC("choiceQuestion1", init, new String[] {
				"yes", "no" });
		choiceQuestion2 = kbm.createQuestionOC("choiceQuestion2", init, new String[] {
				"yes", "no" });
		choiceQuestion3 = kbm.createQuestionOC("choiceQuestion3", init, new String[] {
				"yes", "no" });

		// two ChoiceValues, representing to two possible answers "yes" and "no"
		// for the above questions
		choiceValueYes = new ChoiceValue(choiceQuestion1.getAlternative(0));
		choiceValueNo = new ChoiceValue(choiceQuestion1.getAlternative(1));

		// these two conditions should both be answered with "yes"
		conditionQ1Yes = new CondEqual(choiceQuestion1, choiceValueYes);
		conditionQ2Yes = new CondEqual(choiceQuestion2, choiceValueYes);
		conditionQ3No = new CondEqual(choiceQuestion3, choiceValueNo);

		conditions = new Condition[] {
				conditionQ1Yes, conditionQ2Yes, conditionQ3No };
	}

	// ---------------------
	// M of N - Conditions |
	// --------------------

	@Test(expected = NoAnswerException.class)
	public void testConditionMofN_NoAnswerExceptionThrown() throws NoAnswerException {
		// Summary: Test for a M of N-Condition where no answer in the
		// subconditions is set
		// open up a new session, but enter no answer:
		Session session = SessionFactory.createSession(kbm.getKnowledgeBase());
		Condition conditionMofN = new CondMofN(Arrays.asList(conditions), 1, 2);
		try {
			conditionMofN.eval(session);
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}

	@Test
	public void testConditionMofN() {
		// open up a new session and enter "yes" for the first and "no" for the
		// second question
		Session session = SessionFactory.createSession(kbm.getKnowledgeBase());

		// a M of N condition should evaluate to true, if at least one and
		// not more than two (of the three included) Conditions are true

		CondMofN conditionMofN = new CondMofN(Arrays.asList(conditions), -1, 2);
		// min border is not allowed to be negative. Therefore it should be set
		// to zero:
		assertThat(conditionMofN.getMin(), is(0));

		// now set the min-border to 1:
		conditionMofN.setMin(1);
		assertThat(conditionMofN.getMin(), is(1));

		// Test the toString() method
		String string = conditionMofN.toString();
		assertThat(string, notNullValue());
		assertThat(string.isEmpty(), is(false));

		// try to copy and
		Condition copiedCondition = conditionMofN.copy();
		assertThat(conditionMofN, is(equalTo(copiedCondition)));

		try {
			// iteratively increase the number of answered questions and check
			// if the M of N condition is true or false respectively

			// before answering the first question, the result would be a
			// NoAnswer-Exception. Because of that, we start with answering the
			// first question:
			session.getBlackboard().addValueFact(
					FactFactory.createUserEnteredFact(kb, "choiceQuestion1", choiceValueYes));
			// this answer leads to the successful evaluation of the first
			// condition "conditionQ1Yes". Therefore, the M of N Condition would
			// be evaluated to true as well:
			assertThat(conditionMofN.eval(session), is(true));

			// After answering the second question "correctly", the condition
			// should be true as well:
			session.getBlackboard().addValueFact(
					FactFactory.createUserEnteredFact(kb, "choiceQuestion2", choiceValueYes));
			assertThat(conditionMofN.eval(session), is(true));

			// After the third correctly answered question, the M of N Condition
			// should evaluate to false
			session.getBlackboard().addValueFact(
					FactFactory.createUserEnteredFact(kb, "choiceQuestion3", choiceValueNo));
			assertThat(conditionMofN.eval(session), is(false));
		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}

	// ---------------------
	// Known - Conditions |
	// --------------------

	@Test(expected = NoAnswerException.class)
	public void testConditionKnown_NoAnswerExceptionThrown() throws NoAnswerException {
		// Summary: Test for a KnownCondition where no answer is set
		// open up a new session, but enter no answer:
		Session session = SessionFactory.createSession(kbm.getKnowledgeBase());
		Condition conditionKnown = new CondKnown(choiceQuestion1);
		try {
			conditionKnown.eval(session);
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}
	
	@Test
	public void testConditionKnown() {
		// Summary: Test for a KnownCondition
		Condition conditionKnown = new CondKnown(choiceQuestion1);

		// Test the toString() method
		String string = conditionKnown.toString();
		assertThat(string, notNullValue());
		assertThat(string.isEmpty(), is(false));

		// test the copy method:
		Condition copiedCondition = conditionKnown.copy();
		assertThat(conditionKnown, is(equalTo(copiedCondition)));

		// open up a new session:
		Session session = SessionFactory.createSession(kbm.getKnowledgeBase());

		try {
			// set a unknown value for the question "choiceQuestion1"
			session.getBlackboard().addValueFact(
					FactFactory.createUserEnteredFact(kb, "choiceQuestion1", Unknown.getInstance()));
			// this should evaluate the condition to false:
			assertThat(conditionKnown.eval(session), is(false));

			// now set a "real" value:
			session.getBlackboard().addValueFact(
					FactFactory.createUserEnteredFact(kb, "choiceQuestion1", choiceValueYes));
			// this should evaluate the condition to true:
			assertThat(conditionKnown.eval(session), is(true));

		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}

	// ---------------------
	// Unknown - Conditions |
	// ---------------------

	// @Test(expected = NoAnswerException.class)
	// public void testConditionUnknown_NoAnswerExceptionThrown() throws
	// NoAnswerException {
	// // Summary: Test for a UnknownCondition where no answer is set
	// // open up a new session, but enter no answer:
	// Session session = SessionFactory.createSession(kbm.getKnowledgeBase());
	// Condition conditionUnknown = new CondUnknown(choiceQuestion1);
	// try {
	// conditionUnknown.eval(session);
	// }
	// catch (UnknownAnswerException e) {
	// fail("Unexpected exception thrown: UnknownAnswerException");
	// }
	// }

	@Test
	public void testConditionUnknown() {
		// Summary: Test for a KnownCondition
		Condition conditionUnknown = new CondUnknown(choiceQuestion1);

		// Test the toString() method
		String string = conditionUnknown.toString();
		assertThat(string, notNullValue());
		assertThat(string.isEmpty(), is(false));

		// test the copy method:
		Condition copiedCondition = conditionUnknown.copy();
		assertThat(conditionUnknown, is(equalTo(copiedCondition)));

		// open up a new session:
		Session session = SessionFactory.createSession(kbm.getKnowledgeBase());

		try {
			// set a unknown value for the question "choiceQuestion1"
			session.getBlackboard().addValueFact(
					FactFactory.createUserEnteredFact(kb, "choiceQuestion1", Unknown.getInstance()));
			// this should evaluate the condition to false:
			assertThat(conditionUnknown.eval(session), is(true));

			// now set a "real" value:
			session.getBlackboard().addValueFact(
					FactFactory.createUserEnteredFact(kb, "choiceQuestion1", choiceValueYes));
			// this should evaluate the condition to true:
			assertThat(conditionUnknown.eval(session), is(false));

		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}

	// ----------------------------
	// DiagnosisState - Condition |
	// ----------------------------

	@Test
	public void testConditionDiagnosisState() {
		Solution solution = kbm.createSolution("solutionDiagnosis");
		String ruleID = kbm.createRuleID();
		
		// Create the following rule:
		//
		// IF choiceQuestion1 = "yes"
		// THEN solutionDiagnosis (P7)
		RuleFactory.createHeuristicPSRule(ruleID, solution, Score.P7, conditionQ1Yes);

		// Create new DiagnosisState-Condition and test all getter and setter:
		CondDState conditionDState = new CondDState(null, null);
		conditionDState.setSolution(solution);
		assertThat(conditionDState.getSolution(), is(equalTo(solution)));

		Rating rationEstablished = new Rating(State.ESTABLISHED);
		conditionDState.setStatus(rationEstablished);
		assertThat(conditionDState.getStatus(), is(equalTo(rationEstablished)));

		// Test the toString() method
		String string = conditionDState.toString();
		assertThat(string, notNullValue());
		assertThat(string.isEmpty(), is(false));
		
		/* .equals() Method of CondDState is possibly buggy!!
		Condition copiedCondition = conditionDState.copy();
		assertThat(conditionDState.equals(copiedCondition), is(true));
		*/
		
		assertThat(conditionDState.hashCode(), is(not(0)));

		// open up a new session:
		Session session = SessionFactory.createSession(kbm.getKnowledgeBase());

		try {
			assertThat(conditionDState.eval(session), is(false));

			// now set a "real" value:
			session.getBlackboard().addValueFact(
					FactFactory.createUserEnteredFact(kb, "choiceQuestion1", choiceValueNo));
			// this should evaluate the condition to true:
			assertThat(conditionDState.eval(session), is(false));

			// now set a "real" value:
			session.getBlackboard().addValueFact(
					FactFactory.createUserEnteredFact(kb, "choiceQuestion1", choiceValueYes));
			// this should evaluate the condition to true:
			assertThat(conditionDState.eval(session), is(true));
		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
	}
}
