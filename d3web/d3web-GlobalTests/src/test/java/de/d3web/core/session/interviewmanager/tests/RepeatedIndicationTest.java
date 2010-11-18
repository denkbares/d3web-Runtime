/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.core.session.interviewmanager.tests;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.interviewmanager.EmptyForm;
import de.d3web.core.session.interviewmanager.InterviewAgenda;
import de.d3web.core.session.interviewmanager.NextUnansweredQuestionFormStrategy;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Checks, that the repeated indication of some questions is performed
 * correctly.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 18.11.2010
 */
public class RepeatedIndicationTest {

	KnowledgeBaseManagement kbm;
	Session session;
	InterviewAgenda agenda;

	QContainer pregnancyQuestions;
	QuestionOC pregnant, sex, happy;
	ChoiceValue yes, male;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kbm = KnowledgeBaseManagement.createInstance();

		QASet root = kbm.getKnowledgeBase().getRootQASet();

		// root {container}
		// - pregnancyQuestions {container}
		// -- sex [oc]
		// -- pregnant [oc]
		// IF (pregnant = yes && sex = m) THEN repeatIndicate (sex)

		pregnancyQuestions = kbm.createQContainer("pregnancyQuestions", root);

		sex = kbm.createQuestionOC("sex", pregnancyQuestions, new String[] {
				"male", "female" });
		pregnant = kbm.createQuestionOC("pregnant", pregnancyQuestions, new String[] {
				"yes", "no" });
		happy = kbm.createQuestionOC("happy", pregnancyQuestions, new String[] {
				"yes", "no" });

		yes = new ChoiceValue(kbm.findChoice(pregnant, "yes"));
		male = new ChoiceValue(kbm.findChoice(sex, "male"));

		// IF (pregnant = yes && sex = m) THEN repeatIndicate (sex)
		List<Condition> conditions = new ArrayList<Condition>(2);
		conditions.add(new CondEqual(sex, male));
		conditions.add(new CondEqual(pregnant, yes));
		Condition cond = new CondAnd(conditions);
		RuleFactory.createRepeatedIndicationRule("r1", sex, cond);

		kbm.getKnowledgeBase().setInitQuestions(Arrays.asList(new QASet[] { pregnancyQuestions }));

		session = SessionFactory.createSession(kbm.getKnowledgeBase());
		session.getInterview().setFormStrategy(new NextUnansweredQuestionFormStrategy());
		agenda = session.getInterview().getInterviewAgenda();
	}

	@Test
	public void testWithNoRepeatedIndication() {
		// this is the default behavior, when NO repeated indication is
		// performed

		// expect the first question of the init container
		assertEquals(sex, session.getInterview().nextForm().getInterviewObject());
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(), "sex", "female"));
		assertEquals(pregnant, session.getInterview().nextForm().getInterviewObject());
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(), "pregnant", "yes"));
		assertEquals(happy, session.getInterview().nextForm().getInterviewObject());
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(), "happy", "yes"));
		assertEquals(EmptyForm.getInstance(), session.getInterview().nextForm());
	}

	@Test
	public void testWithRepeatedIndication() {
		// for sex=male we expect that a repeated indication is performed

		// expect the first question of the init container
		assertEquals(sex, session.getInterview().nextForm().getInterviewObject());
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(), "sex", "male"));
		assertEquals(pregnant, session.getInterview().nextForm().getInterviewObject());
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(), "pregnant", "yes"));
		assertEquals(happy, session.getInterview().nextForm().getInterviewObject());
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(), "happy", "yes"));

		// ask sex for the second time!
		assertEquals(sex, session.getInterview().nextForm().getInterviewObject());
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(), "sex", "female"));
		assertEquals(EmptyForm.getInstance(), session.getInterview().nextForm());
	}

	@Test
	public void testWithDoubleRepeatedIndication() {
		// for sex=male we expect that a repeated indication is performed, we
		// answer this for one time and then activate the repeated indication
		// rule by answering the question again with the critical value

		// expect the first question of the init container
		assertEquals(sex, session.getInterview().nextForm().getInterviewObject());
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(), "sex", "male"));
		assertEquals(pregnant, session.getInterview().nextForm().getInterviewObject());
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(), "pregnant", "yes"));
		assertEquals(happy, session.getInterview().nextForm().getInterviewObject());
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(), "happy", "yes"));

		// ask sex for the second time!
		assertEquals(sex, session.getInterview().nextForm().getInterviewObject());
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(), "sex", "female"));
		// some new facts to active the rule for the second time
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(), "happy", "yes"));

		// reanswer the question in order to ask for the third time
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(), "sex", "male"));
		assertEquals(sex, session.getInterview().nextForm().getInterviewObject());

		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(), "sex", "female"));

		assertEquals(EmptyForm.getInstance(), session.getInterview().nextForm());
	}

	@Test
	public void testWithRepeatedIndicationAndRetractionOfRule() {
		// for sex=male we expect that a repeated indication is performed

		// expect the first question of the init container
		assertEquals(sex, session.getInterview().nextForm().getInterviewObject());
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(), "sex", "male"));
		assertEquals(pregnant, session.getInterview().nextForm().getInterviewObject());
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(), "pregnant", "yes"));
		assertEquals(happy, session.getInterview().nextForm().getInterviewObject());
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(), "happy", "yes"));

		// ask sex for the second time!
		assertEquals(sex, session.getInterview().nextForm().getInterviewObject());

		// answer a question in order to retract the repeated indication rule
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kbm.getKnowledgeBase(), "pregnant", "no"));
		assertEquals(EmptyForm.getInstance(), session.getInterview().nextForm());
	}

	public void testDummy() {
		assertTrue(session != null);
		assertEquals(true, true);
	}
}
