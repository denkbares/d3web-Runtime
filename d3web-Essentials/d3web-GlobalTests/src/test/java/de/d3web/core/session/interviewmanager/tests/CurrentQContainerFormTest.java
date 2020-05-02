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
import de.d3web.core.session.values.NumValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.interview.CurrentQContainerFormStrategy;
import de.d3web.interview.EmptyForm;
import de.d3web.interview.Form;
import de.d3web.interview.Interview;
import de.d3web.interview.InterviewAgenda;

import static junit.framework.Assert.*;

public class CurrentQContainerFormTest {

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

		initQuestion = new QuestionOC(root, "initQuestion", "all", "pregnacyQuestions", "height+weight");
		session = SessionFactory.createSession(kb);
		interview = Interview.get(session);
		interview.setFormStrategy(new CurrentQContainerFormStrategy());
		agenda = interview.getInterviewAgenda();
	}

	@Test
	public void testWithTwoQContainers() {
		// initially the agenda is empty
		assertTrue(agenda.isEmpty());

		// PUT the containers 'pregnancyQuestions' and 'heightWeightQuestions'
		// onto the agenda
		agenda.append(pregnancyQuestions, new Indication(State.INDICATED, 0));
		agenda.append(heightWeightQuestions, new Indication(State.INDICATED, 1));
		assertFalse(agenda.isEmpty());

		// EXPECT: 'pregnancyQuestions' to be the first interview object
		InterviewObject formObject = interview.nextForm().getRoot();
		assertEquals(pregnancyQuestions, formObject);

		// SET : first question of pregnancyQuestions (no follow-up question
		// indicated)
		// EXPECT: pregnancyQuestions should be still active
		setValue(sex, male);
		formObject = interview.nextForm().getRoot();
		assertEquals(pregnancyQuestions, formObject);

		// SET : second question of pregnancyQuestions
		// EXPECT: now 'heightWeightQuestions' should be active
		setValue(ask_for_pregnancy,
				new ChoiceValue(KnowledgeBaseUtils.findChoice(ask_for_pregnancy, "no")));
		formObject = interview.nextForm().getRoot();
		assertEquals(heightWeightQuestions, formObject);

		// SET : first question of 'heightWeightQuestions'
		// EXPECT: now 'heightWeightQuestions' should be still active
		setValue(height, new NumValue(100));
		formObject = interview.nextForm().getRoot();
		assertEquals(heightWeightQuestions, formObject);

		// SET : second question of 'heightWeightQuestions'
		// EXPECT: now we expect an EMPTY_FORM since the agenda should be empty
		// now
		setValue(weight, new NumValue(100));
		assertEquals(EmptyForm.getInstance(), interview.nextForm());
	}

	@Test
	public void testContainersWithFollowUpQuestion() {
		// We need this rule for the later indication of the
		// follow-up question 'pregnant'
		// Rule: sex = female => INDICATE ( pregnant )
		RuleFactory.createIndicationRule(pregnant, new CondEqual(sex, female));

		// initially the agenda is empty
		assertTrue(agenda.isEmpty());

		// PUT the container 'pregnancyQuestions' onto the agenda
		agenda.append(pregnancyQuestions, new Indication(State.INDICATED, 0));
		agenda.append(heightWeightQuestions, new Indication(State.INDICATED, 1));
		assertFalse(agenda.isEmpty());

		// EXPECT: 'pregnancyQuestions' to be the first interview object
		InterviewObject formObject = interview.nextForm().getRoot();
		assertEquals(pregnancyQuestions, formObject);

		// SET : ask_for_pregnancy = no
		// sex=female => follow-up question is indicated
		// EXPECT: pregnancyQuestions should be still active, because of
		// follow-up-questions
		setValue(ask_for_pregnancy,
				new ChoiceValue(KnowledgeBaseUtils.findChoice(ask_for_pregnancy, "no")));
		setValue(sex, female);
		Form form = interview.nextForm();
		assertEquals(pregnancyQuestions, form.getRoot());

		// SET : answer follow-up question 'pregnant=no'
		// EXPECT: no the next qcontainer 'heightWeightQuestions' should be
		// active,
		// since all questions (including follow-ups) have been answered
		setValue(pregnant, new ChoiceValue(KnowledgeBaseUtils.findChoice(pregnant, "no")));
		assertEquals(heightWeightQuestions, interview.nextForm().getRoot());

		// SET : answer the questions 'height' and 'weight'
		// EXPECT: all questions on the agenda are answered, so next form should
		// be empty
		setValue(height, new NumValue(100));
		setValue(weight, new NumValue(100));
		assertEquals(EmptyForm.getInstance(), interview.nextForm());
	}

	private void setValue(Question question, Value value) {
		session.getBlackboard().addValueFact(
				FactFactory.createFact(question,
						value, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));
	}
}
