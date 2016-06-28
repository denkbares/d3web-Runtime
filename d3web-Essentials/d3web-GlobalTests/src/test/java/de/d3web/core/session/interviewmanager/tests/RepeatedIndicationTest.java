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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.PSMethod.Type;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.indication.inference.PSMethodStrategic;
import de.d3web.interview.EmptyForm;
import de.d3web.interview.Interview;
import de.d3web.interview.InterviewAgenda;
import de.d3web.interview.NextUnansweredQuestionFormStrategy;
import de.d3web.interview.indication.ActionRepeatedIndication;
import de.d3web.interview.inference.PSMethodInterview;
import de.d3web.plugin.test.InitPluginManager;

import static junit.framework.Assert.*;

/**
 * Checks, that the repeated indication of some questions is performed
 * correctly.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 18.11.2010
 */
public class RepeatedIndicationTest {

	KnowledgeBase kb;
	Session session;
	InterviewAgenda agenda;

	QContainer pregnancyQuestions;
	QuestionOC pregnant, sex, happy;
	ChoiceValue yes, male;
	private Interview interview;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();

		QASet root = kb.getRootQASet();

		// root {container}
		// - pregnancyQuestions {container}
		// -- sex [oc]
		// -- pregnant [oc]
		// IF (pregnant = yes && sex = m) THEN repeatIndicate (sex)

		pregnancyQuestions = new QContainer(root, "pregnancyQuestions");

		sex = new QuestionOC(pregnancyQuestions, "sex", "male", "female");
		pregnant = new QuestionOC(pregnancyQuestions, "pregnant", "yes", "no");
		happy = new QuestionOC(pregnancyQuestions, "happy", "yes", "no");

		yes = new ChoiceValue(KnowledgeBaseUtils.findChoice(pregnant, "yes"));
		male = new ChoiceValue(KnowledgeBaseUtils.findChoice(sex, "male"));

		// IF (pregnant = yes && sex = m) THEN repeatIndicate (sex)
		List<Condition> conditions = new ArrayList<>(2);
		conditions.add(new CondEqual(sex, male));
		conditions.add(new CondEqual(pregnant, yes));
		Condition cond = new CondAnd(conditions);
		ActionRepeatedIndication action = new ActionRepeatedIndication();
		action.setQASets(sex);
		RuleFactory.createStrategicRule(action, cond);

		kb.setInitQuestions(Arrays.asList(new QASet[] { pregnancyQuestions }));

		session = SessionFactory.createSession(kb);
		interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
		interview.setFormStrategy(new NextUnansweredQuestionFormStrategy());
		agenda = interview.getInterviewAgenda();
	}

	@Test
	public void testWithNoRepeatedIndication() {
		// this is the default behavior, when NO repeated indication is
		// performed

		// expect the first question of the init container
		assertEquals(sex, interview.nextForm().getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "sex", "female"));
		assertEquals(pregnant, interview.nextForm().getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "pregnant", "yes"));
		assertEquals(happy, interview.nextForm().getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "happy", "yes"));
		assertEquals(EmptyForm.getInstance(), interview.nextForm());
	}

	@Test
	public void testWithRepeatedIndication() {
		// for sex=male we expect that a repeated indication is performed

		// expect the first question of the init container
		assertEquals(sex, interview.nextForm().getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "sex", "male"));
		assertEquals(pregnant, interview.nextForm().getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "pregnant", "yes"));
		assertEquals(happy, interview.nextForm().getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "happy", "yes"));

		// ask sex for the second time!
		assertEquals(sex, interview.nextForm().getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "sex", "female"));
		assertEquals(EmptyForm.getInstance(), interview.nextForm());
	}

	@Test
	public void testWithDoubleRepeatedIndication() {
		// for sex=male we expect that a repeated indication is performed, we
		// answer this for one time and then activate the repeated indication
		// rule by answering the question again with the critical value

		// expect the first question of the init container
		assertEquals(sex, interview.nextForm().getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "sex", "male"));
		assertEquals(pregnant, interview.nextForm().getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "pregnant", "yes"));
		assertEquals(happy, interview.nextForm().getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "happy", "yes"));

		// ask sex for the second time!
		assertEquals(sex, interview.nextForm().getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "sex", "female"));
		// some new facts to active the rule for the second time
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "happy", "yes"));

		// reanswer the question in order to ask for the third time
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "sex", "male"));
		assertEquals(sex, interview.nextForm().getActiveQuestions().get(0));

		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "sex", "female"));

		assertEquals(EmptyForm.getInstance(), interview.nextForm());
	}

	@Test
	public void testWithRepeatedIndicationAndRetractionOfRule() {
		// for sex=male we expect that a repeated indication is performed

		// expect the first question of the init container
		assertEquals(sex, interview.nextForm().getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "sex", "male"));
		assertEquals(pregnant, interview.nextForm().getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "pregnant", "yes"));
		assertEquals(happy, interview.nextForm().getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "happy", "yes"));

		// ask sex for the second time!
		assertEquals(sex, interview.nextForm().getActiveQuestions().get(0));

		// answer a question in order to retract the repeated indication rule
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "pregnant", "no"));
		assertEquals(EmptyForm.getInstance(), interview.nextForm());
	}

	@Test
	public void testPSType() {
		assertNotNull(session);
		assertTrue(session.getPSMethodInstance(PSMethodStrategic.class).hasType(Type.strategic));
	}
}
