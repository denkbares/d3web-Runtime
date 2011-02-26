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

import de.d3web.core.inference.condition.CondTextContains;
import de.d3web.core.inference.condition.CondTextEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.plugin.test.InitPluginManager;

/**
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 18.08.2010
 */
public class ConditionTextTest {

	KnowledgeBase kb;
	Session session;
	QContainer init;
	QuestionText textQuestion;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		init = new QContainer(kb.getRootQASet(), "init");
		textQuestion = new QuestionText(init, "textQuestion");
	}

	@Test(expected = NoAnswerException.class)
	public void noAnswerExceptionThrown() throws NoAnswerException {
		// Summary: Test for a Condition where no answer is set
		Session session = SessionFactory.createSession(kb);
		Condition condition = new CondTextEqual(textQuestion, "Some Question Text...");
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
		Condition condition = new CondTextEqual(textQuestion, "Some Question Text...");

		// open up a new session and enter a unknown value for the dateQuestion
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "textQuestion", Unknown.getInstance()));

		try {
			condition.eval(session);
		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
	}

	@Test
	public void conditionTextual() {
		// Summary: Test the behavior of textual conditions

		// At first, create two textual conditions on the same question, but
		// with different value
		Condition firstCondition = new CondTextEqual(textQuestion, "Some Question Text...");
		Condition secondCondition = new CondTextEqual(textQuestion, "Another question text!");
		// these two conditions should not be equal
		assertThat(firstCondition, is(not(equalTo(secondCondition))));
	}

	@Test
	public void conditionTextualEquality() {
		// Summary: Test the (in-)equality of textual conditions

		// At first, create two textual conditions on the same question, but
		// with different text-value
		Condition conditionEqual = new CondTextEqual(textQuestion, "Some Question Text...");
		Condition conditionNotEqual = new CondTextEqual(textQuestion, "Another question text!");

		// Test the toString() method
		String string = conditionEqual.toString();
		assertThat(string, notNullValue());
		assertThat(string.isEmpty(), is(false));

		// copy the first condition and check if the copied condition is equal
		// to the first condition
		Condition copiedCondition = conditionEqual.copy();
		assertThat(copiedCondition, is(equalTo(conditionEqual)));

		// open up a new session and enter a fact which should match the
		// first condition, but not the second
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "textQuestion",
						new TextValue("Some Question Text...")));
		try {
			// the first condition should evaluate to true
			assertThat(conditionEqual.eval(session), is(true));
			// and the second condition to false
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
	public void conditionTextContains() {
		// Summary: Test the CondTextContains, which evaluate to true when the
		// textual answer of the question is contained in the condition

		// At first, create two textual conditions on the same question, but
		// with different text-value
		Condition conditionContains = new CondTextContains(textQuestion,
				"some Question Text");
		Condition conditionContainsNot = new CondTextContains(textQuestion,
				"another question text!");

		// Test the toString() method
		String string = conditionContains.toString();
		assertThat(string, notNullValue());
		assertThat(string.isEmpty(), is(false));

		// copy the first condition and check if the copied condition is equal
		// to the first condition
		Condition copiedCondition = conditionContains.copy();
		assertThat(copiedCondition, is(equalTo(conditionContains)));

		// open up a new session and enter a fact which should match the
		// first condition, but not the second
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "textQuestion",
						new TextValue("This is some Question Text with dots!")));
		try {
			// the first condition should evaluate to true
			assertThat(conditionContains.eval(session), is(true));
			// and the second condition to false
			assertThat(conditionContainsNot.eval(session), is(false));
		}
		catch (NoAnswerException e) {
			fail("Unexpected exception thrown: NoAnswerException");
		}
		catch (UnknownAnswerException e) {
			fail("Unexpected exception thrown: UnknownAnswerException");
		}
	}

}
