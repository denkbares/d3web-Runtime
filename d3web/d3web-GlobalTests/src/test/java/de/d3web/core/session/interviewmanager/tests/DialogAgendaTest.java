/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.interviewmanager.InterviewAgenda.InterviewState;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.interview.InterviewAgenda;
import de.d3web.interview.inference.PSMethodInterview;
import de.d3web.plugin.test.InitPluginManager;

public class DialogAgendaTest {

	KnowledgeBase kb;
	Session session;
	InterviewAgenda agenda;

	QContainer pregnancyQuestions, heightWeightQuestions;
	QuestionOC sex, pregnant, ask_for_pregnancy, initQuestion;
	QuestionNum weight, height;
	ChoiceValue female, male, dont_ask;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();

		QASet root = kb.getRootQASet();

		// root {container}
		// - pregnancyQuestions {container}
		// - sex [oc]
		// -- pregnant [oc]
		// - ask_for_pregnancy [oc]
		//
		// - heightWeightQuestions {container}
		// - weight [num]
		// - height [num]

		// Container: pregnancyQuestions = { sex {pregnant}, ask_for_pregnancy
		// } 
		pregnancyQuestions = new QContainer(root, "pregnancyQuestions");
		sex = new QuestionOC(pregnancyQuestions, "sex", new String[] {
				"male", "female" });
		female = new ChoiceValue(KnowledgeBaseUtils.findChoice(sex, "female"));
		male = new ChoiceValue(KnowledgeBaseUtils.findChoice(sex, "male"));
		pregnant = new QuestionOC(sex, "pregnant", "yes", "no");
		ask_for_pregnancy = new QuestionOC(pregnancyQuestions, "ask for pregnancy", "yes", "no");

		// Container: heightWeightQuestions = { weight, height } 
		heightWeightQuestions = new QContainer(root, "heightWeightQuestions");
		weight = new QuestionNum(heightWeightQuestions, "weight");
		height = new QuestionNum(heightWeightQuestions, "height");

		initQuestion = new QuestionOC(root, "all", "pregnacyQuestions", "height+weight");
		session = SessionFactory.createSession(kb);
		agenda = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class)).getInterviewAgenda();
	}

	@Test
	public void testEmptynessOfAgenda() {
		// initially the agenda is empty
		assertTrue(agenda.isEmpty());

		// put the questionnaire 'pregnancyQuestions' onto the agenda
		agenda.append(pregnancyQuestions, new Indication(State.INDICATED, 0));
		assertFalse(agenda.isEmpty());

		// deactivate the questionnaire 'pregnancyQuestions', so agenda should
		// be "empty" again
		agenda.deactivate(pregnancyQuestions);
		assertTrue(agenda.isEmpty());

		// put the questionnaire 'heightWeightQuestions' onto the agenda: thus,
		// agenda is not empty
		agenda.append(heightWeightQuestions, new Indication(State.INDICATED, 0));
		assertFalse(agenda.isEmpty());
	}

	@Test
	public void testAgendaSortingForQContainers() {
		// initially the agenda is empty
		assertTrue(agenda.isEmpty());

		// ADD: qcontainers in 'wrong' order
		agenda.append(heightWeightQuestions, new Indication(State.INDICATED,
				kb.getManager().getTreeIndex(heightWeightQuestions)));
		agenda.append(pregnancyQuestions, new Indication(State.INDICATED,
				kb.getManager().getTreeIndex(pregnancyQuestions)));
		// EXPECT 1: both are on the agenda
		assertTrue(agenda.onAgenda(heightWeightQuestions));
		assertTrue(agenda.onAgenda(pregnancyQuestions));
		// EXPECT 2: the order as in the tree:
		// [pregnancyQuestions,heightWeightQuestions]
		int posPreg = agenda.getCurrentlyActiveObjects().indexOf(pregnancyQuestions);
		int posHeig = agenda.getCurrentlyActiveObjects().indexOf(heightWeightQuestions);
		assertTrue(posPreg < posHeig);
	}

	@Test
	public void testAgendaSortingForQuestions() {
		// initially the agenda is empty
		assertTrue(agenda.isEmpty());

		// ADD: questions in arbitrary order
		agenda.append(height, new Indication(State.INDICATED, kb.getManager().getTreeIndex(height)));
		agenda.append(sex, new Indication(State.INDICATED, kb.getManager().getTreeIndex(sex)));
		agenda.append(weight, new Indication(State.INDICATED, kb.getManager().getTreeIndex(weight)));
		agenda.append(pregnant,
				new Indication(State.INDICATED, kb.getManager().getTreeIndex(pregnant)));
		agenda.append(ask_for_pregnancy, new Indication(State.INDICATED,
				kb.getManager().getTreeIndex(ask_for_pregnancy)));

		// EXPECT 1: all are on the agenda
		assertTrue(agenda.onAgenda(height));
		assertTrue(agenda.onAgenda(sex));
		assertTrue(agenda.onAgenda(pregnant));
		assertTrue(agenda.onAgenda(weight));
		assertTrue(agenda.onAgenda(ask_for_pregnancy));
		// EXPECT 2: the order as in the tree: [sex, pregnant,
		// ask_for_pregnancy, weight, height]
		List<Question> expectedAgenda = Arrays.asList(sex, pregnant, ask_for_pregnancy, weight,
				height);
		assertEquals(expectedAgenda, agenda.getCurrentlyActiveObjects());
	}

	@Test
	public void testDeactivationOfQuestions() {
		// initially the agenda is empty
		assertTrue(agenda.isEmpty());
		// put two questions onto the agenda, both should be ACTIVE
		agenda.append(sex, new Indication(State.INDICATED, 0));
		agenda.append(height, new Indication(State.INDICATED, 0));
		assertFalse(agenda.isEmpty());
		assertTrue(agenda.hasState(sex, InterviewState.ACTIVE));
		assertTrue(agenda.hasState(height, InterviewState.ACTIVE));

		// SET: sex = female
		// EXPECT: sex is INACTVE on the agenda
		setValue(sex, female);
		assertTrue(agenda.hasState(sex, InterviewState.INACTIVE));

		// SET: sex = undefined
		// EXPECT: sex is active on the agenda again
		setValue(sex, UndefinedValue.getInstance());
		assertTrue(agenda.hasState(sex, InterviewState.ACTIVE));
	}

	@Test
	public void testIndicationOfOneQContainer() {
		// We need this rule for the later indication of the follow-up question
		// "pregnant"
		// Rule: sex = female => INDICATE ( pregnant )
		RuleFactory.createIndicationRule(pregnant, new CondEqual(sex, female));

		// initially the agenda is empty
		assertTrue(agenda.isEmpty());

		// put one qcontainer on the agenda, it should be active
		agenda.append(pregnancyQuestions, new Indication(State.INDICATED, 0));
		assertFalse(agenda.isEmpty());
		assertTrue(agenda.hasState(pregnancyQuestions, InterviewState.ACTIVE));

		// Answer the first question in the qcontainer, should be still active
		setValue(sex, male);
		assertTrue(agenda.hasState(pregnancyQuestions, InterviewState.ACTIVE));

		// Answer the second & last question in the qcontainer, so the
		// qcontainer should be INACTIVE
		setValue(ask_for_pregnancy,
				new ChoiceValue(KnowledgeBaseUtils.findChoice(ask_for_pregnancy, "no")));
		assertTrue(agenda.hasState(pregnancyQuestions, InterviewState.INACTIVE));

		// Set the first answer to undefined, so the qcontainer should be ACTIVE
		// again
		setValue(sex, UndefinedValue.getInstance());
		assertTrue(agenda.hasState(pregnancyQuestions, InterviewState.ACTIVE));

		// Set the Question SEX=female
		// EXPECT 1) the qcontainer should be still ACTIVE due to indication of
		// follow-up question 'pregnant'
		// 2) the follow-up question "pregnant" should be put onto the agenda
		setValue(sex, female);
		assertTrue(agenda.hasState(pregnancyQuestions, InterviewState.ACTIVE));
		assertTrue(agenda.onAgenda(pregnant));
		assertTrue(agenda.hasState(pregnant, InterviewState.ACTIVE));
	}

	private void setValue(Question question, Value value) {
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, question,
						value, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));
	}

}
