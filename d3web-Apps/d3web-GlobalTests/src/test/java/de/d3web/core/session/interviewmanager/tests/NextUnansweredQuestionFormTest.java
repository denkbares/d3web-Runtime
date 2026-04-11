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

import org.junit.Before;
import org.junit.Test;

import com.denkbares.plugin.test.InitPluginManager;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.InterviewObject;
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
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.interview.EmptyForm;
import de.d3web.interview.Form;
import de.d3web.interview.Interview;
import de.d3web.interview.InterviewAgenda;
import de.d3web.interview.NextUnansweredQuestionFormStrategy;

import static junit.framework.Assert.*;

public class NextUnansweredQuestionFormTest {

	KnowledgeBase kb;
	Session session;
	InterviewAgenda agenda;

	QContainer pregnancyQuestions, heightWeightQuestions;
	QuestionOC sex, pregnant, ask_for_pregnancy, initQuestion;
	QuestionNum weight, height;
	ChoiceValue female, male, dont_ask;
	private Interview interview;

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
		sex = new QuestionOC(pregnancyQuestions, "sex", "male", "female");
		female = new ChoiceValue(KnowledgeBaseUtils.findChoice(sex, "female"));
		male = new ChoiceValue(KnowledgeBaseUtils.findChoice(sex, "male"));
		pregnant = new QuestionOC(sex, "pregnant", "yes", "no");
		ask_for_pregnancy = new QuestionOC(pregnancyQuestions, "ask for pregnancy", "yes", "no");

		// Container: heightWeightQuestions = { weight, height } 
		heightWeightQuestions = new QContainer(root, "heightWeightQuestions");
		weight = new QuestionNum(heightWeightQuestions, "weight");
		height = new QuestionNum(heightWeightQuestions, "height");

		initQuestion = new QuestionOC(root, "initQuestion", "all", "pregnacyQuestions",
				"height+weight");
		session = SessionFactory.createSession(kb);
		interview = Interview.get(session);
		interview.setFormStrategy(new NextUnansweredQuestionFormStrategy());
		agenda = interview.getInterviewAgenda();
	}

	@Test
	public void testWithQuestionsOnAgenda() {
		// initially the agenda is empty
		assertTrue(agenda.isEmpty());

		// PUT the questions 'sex' and 'pregnant' onto the agenda
		agenda.append(sex, new Indication(State.INDICATED, 0));
		agenda.append(pregnant, new Indication(State.INDICATED, 1));
		assertFalse(agenda.isEmpty());

		// EXPECT: 'sex' to be the first question
		InterviewObject formQuestions = interview.nextForm().getActiveQuestions().get(0);
		assertEquals(sex, formQuestions);

		// ANSWER: sex=female
		// EXPECT: pregnant to be the next question
		setValue(sex, female);
		formQuestions = interview.nextForm().getActiveQuestions().get(0);
		assertEquals(pregnant, formQuestions);

		// ANSWER: pregnant=no
		// EXPECT: no more questions to ask
		setValue(pregnant, new ChoiceValue(KnowledgeBaseUtils.findChoice(pregnant, "no")));
		Form form = interview.nextForm();
		assertEquals(EmptyForm.getInstance(), form);
	}

	@Test
	public void testWithOneQContainerOnAgenda_WithoutFollowUpQuestions() {
		// initially the agenda is empty
		assertTrue(agenda.isEmpty());
		// Put the QContainer pregnancyQuestions on the agenda
		agenda.append(pregnancyQuestions, new Indication(State.INDICATED, 0));
		assertFalse(agenda.isEmpty());

		// EXPECT the first question 'sex' to be the next question in the form
		InterviewObject nextQuestion = interview.nextForm().getActiveQuestions().get(0);
		assertEquals(sex, nextQuestion);

		// SET question sex=male
		// EXPECT the second question 'ask_for_pregnancy' to be the next
		// question in the form
		setValue(sex, male);
		nextQuestion = interview.nextForm().getActiveQuestions().get(0);
		assertEquals(ask_for_pregnancy, nextQuestion);

		// SET : question ask_for_pregnancy=no
		// EXPECT: since all questions of the qcontainer are answered, we expect
		// no more
		// questions to be asked next, i.e., the EmptyForm singleton is returned
		setValue(ask_for_pregnancy, KnowledgeBaseUtils.findValue(ask_for_pregnancy, "no"));
		assertEquals(EmptyForm.getInstance(), interview.nextForm());
	}

	@Test
	public void testWithOneQContainerOnAgenda_WithFollowUpQuestions() {
		// We need this rule for the later indication of the follow-up question
		// "pregnant"
		// Rule: sex = female => INDICATE ( pregnant )
		RuleFactory.createIndicationRule(pregnant, new CondEqual(sex, female));

		// initially the agenda is empty
		assertTrue(agenda.isEmpty());
		// Put the QContainer pregnancyQuestions on the agenda
		agenda.append(pregnancyQuestions, new Indication(State.INDICATED, 0));
		assertFalse(agenda.isEmpty());

		// EXPECT the first question 'sex' to be the next question in the form
		InterviewObject nextQuestion = interview.nextForm().getActiveQuestions().get(0);
		assertEquals(sex, nextQuestion);

		// SET question sex=female
		// EXPECT the follow-up question 'pregnant' to be the next question in
		// the form
		setValue(sex, female);
		nextQuestion = interview.nextForm().getActiveQuestions().get(0);

		// TODO: overwork FormStrategy to copy with follow-up questions
		assertEquals(pregnant, nextQuestion);
	}

	private void setValue(Question question, Value value) {
		session.getBlackboard().addValueFact(
				FactFactory.createFact(question,
						value, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));
	}
}
