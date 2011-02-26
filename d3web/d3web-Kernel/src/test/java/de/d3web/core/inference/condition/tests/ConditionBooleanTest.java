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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.plugin.test.InitPluginManager;

/**
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 20.08.2010
 */
public class ConditionBooleanTest {

	KnowledgeBase kb;
	QContainer init;
	QuestionOC choiceQuestion1, choiceQuestion2;
	Condition conditionQ1Yes, conditionQ2Yes;
	ChoiceValue choiceValueYes, choiceValueNo;
	Condition[] conditions;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		init = new QContainer(kb.getRootQASet(), "init");
		choiceQuestion1 = new QuestionOC(init, "choiceQuestion1", new String[] {
				"yes", "no" });
		choiceQuestion2 = new QuestionOC(init, "choiceQuestion2", new String[] {
				"yes", "no" });

		// two ChoiceValues, representing to two possible answers "yes" and "no"
		// for the above questions
		choiceValueYes = new ChoiceValue(choiceQuestion1.getAlternative(0));
		choiceValueNo = new ChoiceValue(choiceQuestion1.getAlternative(1));

		// these two conditions should both be answered with "yes"
		conditionQ1Yes = new CondEqual(choiceQuestion1, choiceValueYes);
		conditionQ2Yes = new CondEqual(choiceQuestion2, choiceValueYes);

		conditions = new Condition[] {
				conditionQ1Yes, conditionQ2Yes };
	}

	// ------------------
	// AND - Conditions |
	// ------------------

	@Test(expected = NoAnswerException.class)
	public void testBooleanConditionAnd_NoAnswerExceptionThrown() throws NoAnswerException {
		// Summary: Test for a Condition where no answer is set
		// open up a new session, but enter no answers
		Session session = SessionFactory.createSession(kb);
		Condition conditionAnd = new CondAnd(Arrays.asList(conditions));
		try {
			conditionAnd.eval(session);
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}

	@Test(expected = UnknownAnswerException.class)
	public void testBooleanConditionAnd_UnknownAnswerExceptionThrown() throws UnknownAnswerException {
		// Summary: Test for a And-Condition where an unknown answer is set

		// open up a new session and enter a unknown value for the
		// choiceQuestion1
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "choiceQuestion1", Unknown.getInstance()));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "choiceQuestion2", Unknown.getInstance()));
		Condition conditionAnd = new CondAnd(Arrays.asList(conditions));
		try {
			conditionAnd.eval(session);
		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
	}

	@Test
	public void testBooleanConditionAnd() {
		// Summary: Test the correct functionality of AND-Conditions, where
		// every sub-condition has to be true

		// open up a new session and enter "yes" for the first and "no" for the
		// second question
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "choiceQuestion1", choiceValueYes));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "choiceQuestion2", choiceValueNo));

		// conditionQ1Yes should evaluate to true and
		// conditionQ2Yes should evaluate to false
		// The new AND condition should therefore evaluate to false
		Condition conditionAnd = new CondAnd(Arrays.asList(conditions));

		// Test the toString() method
		String string = conditionAnd.toString();
		assertThat(string, notNullValue());
		assertThat(string.isEmpty(), is(false));

		try {
			assertThat(conditionAnd.eval(session), is(false));
		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}

	// -----------------
	// OR - Conditions |
	// -----------------

	@Test(expected = NoAnswerException.class)
	public void testBooleanConditionOr_NoAnswerExceptionThrown() throws NoAnswerException {
		// Summary: Test for a OR-Condition where no answer is set
		// open up a new session, but enter no answers
		Session session = SessionFactory.createSession(kb);
		Condition conditionOr = new CondOr(Arrays.asList(conditions));
		try {
			conditionOr.eval(session);
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}

	@Test(expected = UnknownAnswerException.class)
	public void testBooleanConditionOr_UnknownAnswerExceptionThrown() throws UnknownAnswerException {
		// Summary: Test for a Or-Condition where an unknown answer is set

		// open up a new session and enter a unknown value for the
		// choiceQuestion1
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "choiceQuestion1", Unknown.getInstance()));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "choiceQuestion2", Unknown.getInstance()));
		Condition conditionOr = new CondOr(Arrays.asList(conditions));
		try {
			conditionOr.eval(session);
		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
	}

	@Test
	public void testBooleanConditionOr() {
		// open up a new session and enter "yes" for the first and "no" for the
		// second question
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "choiceQuestion1", choiceValueYes));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "choiceQuestion2", choiceValueNo));

		// conditionQ1Yes should evaluate to true and
		// conditionQ2Yes should evaluate to false
		Condition[] conditions = {
				conditionQ1Yes, conditionQ2Yes };

		// should evaluate to false
		Condition conditionOr = new CondOr(Arrays.asList(conditions));

		// Test the toString() method
		String string = conditionOr.toString();
		assertThat(string, notNullValue());
		assertThat(string.isEmpty(), is(false));

		try {
			assertThat(conditionOr.eval(session), is(true));
		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}

	// ------------------
	// NOT - Conditions |
	// ------------------

	@Test(expected = NoAnswerException.class)
	public void testBooleanConditionNot_NoAnswerExceptionThrown() throws NoAnswerException {
		// Summary: Test for a Not-Condition where no answer is set
		// open up a new session, but enter no answers
		Session session = SessionFactory.createSession(kb);
		Condition conditionNot = new CondNot(conditionQ1Yes);
		try {
			conditionNot.eval(session);
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}

	@Test(expected = UnknownAnswerException.class)
	public void testBooleanConditionNot_UnknownAnswerExceptionThrown() throws UnknownAnswerException {
		// Summary: Test for a Not-Condition where an unknown answer is set

		// open up a new session and enter a unknown value for the
		// choiceQuestion1
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "choiceQuestion1", Unknown.getInstance()));
		Condition conditionNot = new CondNot(conditionQ1Yes);
		try {
			conditionNot.eval(session);
		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
	}

	@Test
	public void testBooleanConditionNot() {

		// open up a new session and enter "yes" for the first and "no" for the
		// second question
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "choiceQuestion1", choiceValueYes));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "choiceQuestion2", choiceValueNo));

		// conditionQ2Yes should evaluate to false
		// conditionNot should evaluate to true
		Condition conditionNot = new CondNot(conditionQ2Yes);

		// Test the toString() method
		String string = conditionNot.toString();
		assertThat(string, notNullValue());
		assertThat(string.isEmpty(), is(false));

		try {
			assertThat(conditionNot.eval(session), is(true));
		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}
}