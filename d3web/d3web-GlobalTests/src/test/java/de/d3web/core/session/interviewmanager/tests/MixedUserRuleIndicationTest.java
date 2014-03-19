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
package de.d3web.core.session.interviewmanager.tests;

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.interview.Interview;
import de.d3web.interview.inference.PSMethodInterview;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Purpose of this test:
 * 
 * Puts, questions on the agenda, that are a) indicated by rules and b)
 * indicated by the user. The user indication should be handled before the rule
 * indication, thus user indicated questions should be always returned first as
 * questions to be asked.
 * 
 * @author joba
 * @created 12.08.2010
 */
public class MixedUserRuleIndicationTest {

	KnowledgeBase kb;
	Session session;
	QuestionNum weight, abnormalWeight, height, importantQuestion;
	QContainer init, qc;
	private Interview interview;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();

		init = new QContainer(kb.getRootQASet(), "init");
		qc = new QContainer(kb.getRootQASet(), "qc");

		// THE QUESTION TREE
		// init {container}
		// - weight [num]
		// -- abnormalWeight [num]
		// - height [num]
		// qc {container}
		// - importantQuestions

		weight = new QuestionNum(init, "weight");
		abnormalWeight = new QuestionNum(weight, "abnormalWeight");
		height = new QuestionNum(init, "height");
		importantQuestion = new QuestionNum(qc, "importantQuestion");

		kb.setInitQuestions(Arrays.asList(init));

		// Define rule for follow-up question
		// r1: weight > 120 => indicate abnormalWeight
		RuleFactory.createIndicationRule(abnormalWeight, new CondNumGreater(weight, 120.0));
		session = SessionFactory.createSession(kb);
		interview = session.getSessionObject(
				session.getPSMethodInstance(PSMethodInterview.class));
	}

	@Test
	public void testStandardIndicationWithoutFollowQuestion() {
		// Summary: First question is answered, but follow-up indication rule
		// does not fire, so the second question is presented next

		// expect the first question of the init container
		assertEquals(weight, interview.nextForm().getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(kb, "weight", 100.0));

		// expect the second question "height" and not the follow-up question,
		// since the rule did not fire
		assertEquals(height, interview.nextForm().getActiveQuestions().get(0));
	}

	@Test
	public void testStandardIndicationWithFollowQuestion() {
		// Summary: first question of the questionnaire is answered, then its
		// follow-up question should be indicated

		// expect the first question of the init container
		assertEquals(weight, interview.nextForm().getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(kb, "weight", 140.0));

		// expect the follow-up question, since the rule should have fired
		assertEquals(abnormalWeight, interview.nextForm().getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(kb, "abnormalWeight", 100.0));

		// expect the second question "height" as the last question
		assertEquals(height, interview.nextForm().getActiveQuestions().get(0));
	}

	@Test
	public void testStandardIndicationWithFollowUpQuestionAndUserIndication() {
		// Summary: A question is selected by the user, which is located in the
		// same container

		// expect the first question of the init container
		assertEquals(weight, interview.nextForm().getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(kb, "weight", 140.0));

		// now the follow-up question is indicated, but we also indicate the
		// question "height" manually (which should be stronger)
		session.getBlackboard().addInterviewFact(
				FactFactory.createFact(height,
						new Indication(State.INSTANT_INDICATED,
								kb.getManager().getTreeIndex(height)),
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		assertEquals(height, interview.nextForm().getActiveQuestions().get(0));
	}

	@Test
	public void testStandardIndicationWithFollowUpQuestionAndUserIndication2() {
		// Summary: A question is selected by the user, which is located in
		// another container

		// expect the first question of the init container
		assertEquals(weight, interview.nextForm().getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(kb, "weight", 140.0));

		// now the follow-up question is indicated, but we also indicate the
		// question "height" manually (which should be stronger)
		session.getBlackboard().addInterviewFact(
				FactFactory.createFact(
						importantQuestion,
						new Indication(State.INSTANT_INDICATED, kb.getManager().getTreeIndex(
								importantQuestion)),
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		assertEquals(importantQuestion, interview.nextForm().getActiveQuestions().get(0));
	}

	@Test
	public void testStandardIndicationWithFollowUpQuestionAndUserIndication3() {
		// Summary: A questionnaire is selected by the user, we expect the first
		// question of this container "qc" to be the next question to be asked

		// expect the first question of the init container
		assertEquals(weight, interview.nextForm().getActiveQuestions().get(0));
		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(kb, "weight", 140.0));

		// now the follow-up question is indicated, but we also indicate the
		// questionnaire "qc" manually (which should be stronger, this the first
		// question there "importantQuestion" should be indicated)
		session.getBlackboard().addInterviewFact(
				FactFactory.createFact(qc,
						new Indication(State.INSTANT_INDICATED, kb.getManager().getTreeIndex(qc)),
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		assertEquals(importantQuestion, interview.nextForm().getActiveQuestions().get(0));
	}

}
