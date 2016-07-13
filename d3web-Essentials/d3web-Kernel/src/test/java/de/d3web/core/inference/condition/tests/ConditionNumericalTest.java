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

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondNumEqual;
import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.CondNumGreaterEqual;
import de.d3web.core.inference.condition.CondNumIn;
import de.d3web.core.inference.condition.CondNumLess;
import de.d3web.core.inference.condition.CondNumLessEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.Unknown;
import com.denkbares.plugin.test.InitPluginManager;

/**
 * Purpose of this test: Check the different types of numerical conditions of
 * questions whether they act as expected
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 16.08.2010
 */
public class ConditionNumericalTest {

	KnowledgeBase kb;
	Session session;
	QContainer init;
	QuestionNum numericalQuestion;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		init = new QContainer(kb.getRootQASet(), "init");
		numericalQuestion = new QuestionNum(init, "numericalQuestion");
	}

	@Test(expected = NoAnswerException.class)
	public void noAnswerExceptionThrown() throws NoAnswerException {
		// Summary: Test for a Condition where no answer is set
		Session session = SessionFactory.createSession(kb);
		Condition condition = new CondNumEqual(numericalQuestion, 10.0);
		try {
			condition.eval(session);
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}

	@Test(expected = UnknownAnswerException.class)
	public void unknownAnswerExceptionThrown() throws UnknownAnswerException {
		// Summary: Test for a Condition where an unknown answer is set
		Condition condition = new CondNumEqual(numericalQuestion, 10.0);

		// open up a new session and enter a unknown value for the
		// numericalQuestion
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "numericalQuestion", Unknown.getInstance()));

		try {
			condition.eval(session);
		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
	}

	@Test
	public void conditionNumerical() {
		// Summary: Test the behavior of numerical conditions

		// At first, create two numerical conditions on the same question, but
		// with different value
		Condition firstCondition = new CondNumEqual(numericalQuestion, 10.0);
		Condition secondCondition = new CondNumEqual(numericalQuestion, 50.0);
		// these two conditions should not be equal
		assertThat(firstCondition, is(not(equalTo(secondCondition))));
	}

	@Test
	public void conditionNumericalEquality() {
		// Summary: Test the (in-)equality of numerical conditions

		// At first, create two numerical conditions on the same question, but
		// with different value
		Condition conditionEqual = new CondNumEqual(numericalQuestion, 10.0);
		Condition conditionNotEqual = new CondNumEqual(numericalQuestion, 50.0);

		// Test the toString() method
		String string = conditionEqual.toString();
		assertThat(string, notNullValue());
		assertThat(string.length(), is(not(0)));

		// copy the first condition and check if the copied condition is equal
		// to the first condition
		Condition copiedCondition = new CondNumEqual(numericalQuestion, 10.0);
		assertThat(copiedCondition, is(equalTo(conditionEqual)));

		// open up a new session and enter a fact which should match the
		// first condition, but not the second
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "numericalQuestion", 10.0));
		try {
			// the first condition should evaluate to true and
			// the second condition to false
			assertThat(conditionEqual.eval(session), is(true));
			assertThat(conditionNotEqual.eval(session), is(false));
		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}

	@Test
	public void conditionNumericalGreater() {
		// Summary: This tests the CondNumGreater, where the value of the
		// question has to be greater than the value of the condition

		// At first, create three conditions: Smaller than the (later set) value
		// of the question, equal to the value of the question and greater than
		// the value of the question
		Condition conditionSmallerThanValue = new CondNumGreater(numericalQuestion, 10.0);
		Condition conditionEqualThanValue = new CondNumGreater(numericalQuestion, 50.0);
		Condition conditionGreaterThanValue = new CondNumGreater(numericalQuestion, 100.0);

		// Test the toString() method
		String string = conditionSmallerThanValue.toString();
		assertThat(string, notNullValue());
		assertThat(string.length(), is(not(0)));

		// copy the first condition and check if the copied condition is equal
		// to the first condition
		Condition conditionCopied = new CondNumGreater(numericalQuestion, 10.0);
		assertThat(conditionCopied, is(equalTo(conditionSmallerThanValue)));

		// open up a new session and enter a value of 50.0 for the
		// numericalQuestion
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "numericalQuestion", 50.0));
		try {
			// now, the first condition should evaluate to true (50 > 10),
			assertThat(conditionSmallerThanValue.eval(session), is(true));
			// but the second one to false (50 !> 50)
			assertThat(conditionEqualThanValue.eval(session), is(false));
			// and the third one to false also (50 !> 100)
			assertThat(conditionGreaterThanValue.eval(session), is(false));
		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}

	@Test
	public void conditionNumericalGreaterEqual() {
		// Summary: This tests the CondNumGreaterEqual, where the value of the
		// question has to be greater than or equal to the value of the
		// condition

		// At first, create three conditions: Smaller than the (later set) value
		// of the question, equal to the value of the question and greater than
		// the value of the question
		Condition conditionSmallerThanValue = new CondNumGreaterEqual(numericalQuestion, 10.0);
		Condition conditionEqualThanValue = new CondNumGreaterEqual(numericalQuestion, 50.0);
		Condition conditionGreaterThanValue = new CondNumGreaterEqual(numericalQuestion, 100.0);

		// Test the toString() method
		String string = conditionSmallerThanValue.toString();
		assertThat(string, notNullValue());
		assertThat(string.length(), is(not(0)));

		// copy the first condition and check if the copied condition is equal
		// to the first condition
		Condition conditionCopied = new CondNumGreaterEqual(numericalQuestion, 10.0);
		assertThat(conditionCopied, is(equalTo(conditionSmallerThanValue)));

		// open up a new session and enter a value of 50.0 for the
		// numericalQuestion
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "numericalQuestion", 50.0));
		try {
			// now, the first condition should evaluate to true (50 >= 10),
			assertThat(conditionSmallerThanValue.eval(session), is(true));
			// the second one to true also (50 >= 50),
			assertThat(conditionEqualThanValue.eval(session), is(true));
			// but the third one to false (50 !>= 100)
			assertThat(conditionGreaterThanValue.eval(session), is(false));
		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}

	@Test
	public void conditionNumericalLess() {
		// Summary: This tests the CondNumLess, where the value of the
		// question has to be smaller than the value of the condition

		// At first, create three conditions: Smaller than the (later set) value
		// of the question, equal to the value of the question and greater than
		// the value of the question
		Condition conditionSmallerThanValue = new CondNumLess(numericalQuestion, 10.0);
		Condition conditionEqualThanValue = new CondNumLess(numericalQuestion, 50.0);
		Condition conditionGreaterThanValue = new CondNumLess(numericalQuestion, 100.0);

		// Test the toString() method
		String string = conditionSmallerThanValue.toString();
		assertThat(string, notNullValue());
		assertThat(string.length(), is(not(0)));

		// copy the first condition and check if the copied condition is equal
		// to the first condition
		Condition conditionCopied = new CondNumLess(numericalQuestion, 10.0);
		assertThat(conditionCopied, is(equalTo(conditionSmallerThanValue)));

		// open up a new session and enter a value of 50.0 for the
		// numericalQuestion
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "numericalQuestion", 50.0));
		try {
			// now, the first condition should evaluate to false (50 !< 10),
			assertThat(conditionSmallerThanValue.eval(session), is(false));
			// the second one to false also (50 !< 50),
			assertThat(conditionEqualThanValue.eval(session), is(false));
			// but the third one to true (50 < 100)
			assertThat(conditionGreaterThanValue.eval(session), is(true));
		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}

	@Test
	public void conditionNumericalLessEqual() {
		// Summary: This tests the CondNumLessEqual, where the value of the
		// question has to be smaller or equal than the value of the condition

		// At first, create three conditions: Smaller than the (later set) value
		// of the question, equal to the value of the question and greater than
		// the value of the question
		Condition conditionSmallerThanValue = new CondNumLessEqual(numericalQuestion, 10.0);
		Condition conditionEqualThanValue = new CondNumLessEqual(numericalQuestion, 50.0);
		Condition conditionGreaterThanValue = new CondNumLessEqual(numericalQuestion, 100.0);

		// Test the toString() method
		String string = conditionSmallerThanValue.toString();
		assertThat(string, notNullValue());
		assertThat(string.length(), is(not(0)));

		// copy the first condition and check if the copied condition is equal
		// to the first condition
		Condition conditionCopied = new CondNumLessEqual(numericalQuestion, 10.0);
		assertThat(conditionCopied, is(equalTo(conditionSmallerThanValue)));

		// open up a new session and enter a value of 50.0 for the
		// numericalQuestion
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "numericalQuestion", 50.0));
		try {
			// now, the first condition should evaluate to false (50 !<= 10),
			assertThat(conditionSmallerThanValue.eval(session), is(false));
			// but the second one to true (50 <= 50),
			assertThat(conditionEqualThanValue.eval(session), is(true));
			// and the third one to true also (50 <= 100)
			assertThat(conditionGreaterThanValue.eval(session), is(true));
		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}

	@Test
	public void conditionNumericalInRange() {
		// Summary: This tests the CondNumIn, where the value of the
		// question has to be in the range of a (closed) interval

		// At first, create three conditions: An interval which is smaller
		// than the (later set) value of the question, an interval which matches
		// the value of the question an an interval where the value is too small
		Condition conditionRangeSmaller = new CondNumIn(numericalQuestion, 10.0, 45.0);
		Condition conditionRangeIn = new CondNumIn(numericalQuestion, 45.0, 55.0);
		Condition conditionRangeGreater = new CondNumIn(numericalQuestion, 55.0, 100.0);

		// two additional interval-conditions where the value of the question is
		// a border value of the interval
		Condition conditionRangeOnBorder1 = new CondNumIn(numericalQuestion, 35.0, 50.0);
		Condition conditionRangeOnBorder2 = new CondNumIn(numericalQuestion, 50.0, 65.0);

		// Test the toString() method
		String string = conditionRangeIn.toString();
		assertThat(string, notNullValue());
		assertThat(string.length(), is(not(0)));

		// Test the getValue Method
		string = ((CondNumIn) conditionRangeIn).getValue();
		assertThat(string, notNullValue());
		assertThat(string.length(), is(not(0)));

		// copy the first condition and check if the copied condition is equal
		// to the first condition
		Condition conditionCopied = new CondNumIn(numericalQuestion, 45.0, 55.0);
		assertThat(conditionCopied, is(equalTo(conditionRangeIn)));

		// open up a new session and enter a value of 50.0 for the
		// numericalQuestion
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "numericalQuestion", 50.0));
		try {
			// the first condition should evaluate to false ([10...45] < 50)),
			assertThat(conditionRangeSmaller.eval(session), is(false));
			// but the second one to true (50.0 in [45...55]]),
			assertThat(conditionRangeIn.eval(session), is(true));
			// and the third one to false (50 < [55...100])
			assertThat(conditionRangeGreater.eval(session), is(false));

			// due to the closed interval, border values should evaluate to
			// true (because they are included in the interval):
			assertThat(conditionRangeOnBorder1.eval(session), is(true));
			assertThat(conditionRangeOnBorder2.eval(session), is(true));
		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}
}
