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

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondDateAfter;
import de.d3web.core.inference.condition.CondDateBefore;
import de.d3web.core.inference.condition.CondDateEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Purpose of this test: Check the different types of date-conditions of
 * questions whether they act as expected
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 17.08.2010
 */
public class ConditionDateTest {

	KnowledgeBase kb;
	Session session;
	QContainer init;
	QuestionDate dateQuestion;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		init = new QContainer(kb.getRootQASet(), "init");
		dateQuestion = new QuestionDate(init, "dateQuestion");
	}

	@Test(expected = NoAnswerException.class)
	public void noAnswerExceptionThrown() throws NoAnswerException {
		// Summary: Test for a Condition where no answer is set
		Session session = SessionFactory.createSession(kb);
		Condition condition = new CondDateEqual(dateQuestion, new DateValue(new Date()));
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
		Condition condition = new CondDateEqual(dateQuestion, new DateValue(new Date()));

		// open up a new session and enter a unknown value for the dateQuestion
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "dateQuestion", Unknown.getInstance()));

		try {
			condition.eval(session);
		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
	}

	@Test
	public void conditionDate() {
		// Summary: Test the behavior of date-conditions

		// At first, create two date-conditions on the same question, but
		// with different value
		Date firstDate = new Date();
		Date secondDate = new Date(firstDate.getTime() + 1000);// 1 second later

		Condition firstDateCondition = new CondDateEqual(dateQuestion, new DateValue(firstDate));
		Condition secondDateCondition = new CondDateEqual(dateQuestion, new DateValue(secondDate));

		// these two conditions should not be equal
		assertThat(firstDateCondition, is(not(equalTo(secondDateCondition))));
	}

	@Test
	public void conditionDateEquality() {
		// Summary: Test the (in-)equality of date-conditions

		// At first, create two date-conditions on the same question, but
		// with different value
		Date firstDate = new Date();
		Date secondDate = new Date(firstDate.getTime() + 1000);// 1 second later

		Condition conditionEqual = new CondDateEqual(dateQuestion, new DateValue(firstDate));
		Condition conditionNotEqual = new CondDateEqual(dateQuestion, new DateValue(secondDate));

		// Test the toString() method
		String string = conditionEqual.toString();
		assertThat(string, notNullValue());
		assertThat(string.isEmpty(), is(false));

		// copy the first condition and check if the copied condition is equal
		// to the first condition
		Condition copiedCondition = new CondDateEqual(dateQuestion, new DateValue(firstDate));
		assertThat(copiedCondition, is(equalTo(conditionEqual)));

		// open up a new session and enter a fact which should match the
		// first condition, but not the second
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "dateQuestion",
						new DateValue(firstDate)));
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
	public void conditionNumericalAfter() {
		// Summary: This tests the CondDateAfter, where the date-value of the
		// question has to be later than the value of the condition

		// At first, create three conditions: before the (later set) value
		// of the question, equal to the value of the question and later than
		// the value of the question
		long now = System.currentTimeMillis();

		Condition conditionBeforeValue = new CondDateAfter(dateQuestion,
				new DateValue(new Date(now - 1000)));
		Condition conditionSimultaneouslyValue = new CondDateAfter(dateQuestion,
				new DateValue(new Date(now)));
		Condition conditionAfterValue = new CondDateAfter(dateQuestion,
				new DateValue(new Date(now + 1000)));

		// Test the toString() method
		String string = conditionBeforeValue.toString();
		assertThat(string, notNullValue());
		assertThat(string.isEmpty(), is(false));

		// copy the first condition and check if the copied condition is equal
		// to the first condition
		Condition conditionCopied = new CondDateAfter(dateQuestion,
				new DateValue(new Date(now - 1000)));
		assertThat(conditionCopied, is(equalTo(conditionBeforeValue)));

		// open up a new session and set "Now" as value for the dateQuestion
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "dateQuestion",
						new DateValue(new Date(now))));
		try {
			// now, the first condition should evaluate to true,
			assertThat(conditionBeforeValue.eval(session), is(true));
			// but the second one to false
			assertThat(conditionSimultaneouslyValue.eval(session), is(false));
			// and the third one to false also
			assertThat(conditionAfterValue.eval(session), is(false));
		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}

	@Test
	public void conditionNumericalBefore() {
		// Summary: This tests the CondDateBefore, where the date-value of the
		// question has to be earlier than the value of the condition

		// At first, create three conditions: before the (later set) value
		// of the question, equal to the value of the question and later than
		// the value of the question
		long now = System.currentTimeMillis();

		Condition conditionBeforeValue = new CondDateBefore(dateQuestion,
				new DateValue(new Date(now - 1000)));
		Condition conditionSimultaneouslyValue = new CondDateBefore(dateQuestion,
				new DateValue(new Date(now)));
		Condition conditionAfterValue = new CondDateBefore(dateQuestion,
				new DateValue(new Date(now + 1000)));

		// Test the toString() method
		String string = conditionBeforeValue.toString();
		assertThat(string, notNullValue());
		assertThat(string.isEmpty(), is(false));

		// copy the first condition and check if the copied condition is equal
		// to the first condition
		Condition conditionCopied = new CondDateBefore(dateQuestion,
				new DateValue(new Date(now - 1000)));
		assertThat(conditionCopied, is(equalTo(conditionBeforeValue)));

		// open up a new session and set "Now" as value for the dateQuestion
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "dateQuestion",
						new DateValue(new Date(now))));
		try {
			// now, the first condition should evaluate to false
			assertThat(conditionBeforeValue.eval(session), is(false));
			// the second one to false,
			assertThat(conditionSimultaneouslyValue.eval(session), is(false));
			// but the third one to true
			assertThat(conditionAfterValue.eval(session), is(true));
		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}
}
