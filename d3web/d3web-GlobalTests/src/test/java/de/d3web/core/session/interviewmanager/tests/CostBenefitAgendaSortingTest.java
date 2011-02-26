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

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.interviewmanager.EmptyForm;
import de.d3web.core.session.interviewmanager.InterviewAgenda;
import de.d3web.core.session.interviewmanager.NextUnansweredQuestionFormStrategy;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;
import de.d3web.plugin.test.InitPluginManager;

public class CostBenefitAgendaSortingTest {

	KnowledgeBase kb;
	Session session;
	InterviewAgenda agenda;

	QContainer pregnancyQuestions, heightWeightQuestions;
	QuestionOC sex, pregnant;
	QuestionText name;
	QuestionNum weight, height;
	ChoiceValue female, male, dont_ask;
	PSMethodCostBenefit costBenefit;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();

		QASet root = kb.getRootQASet();

		// root {container}
		// - pregnancyQuestions {container}
		// - sex [oc]
		// - name [text]
		//
		// - heightWeightQuestions {container}
		// - weight [num]
		// - height [num]

		pregnancyQuestions = new QContainer(root, "pregnancyQuestions");
		sex = new QuestionOC(pregnancyQuestions, "sex", new String[] {
				"male", "female" });
		female = new ChoiceValue(KnowledgeBaseUtils.findChoice(sex, "female"));
		male = new ChoiceValue(KnowledgeBaseUtils.findChoice(sex, "male"));
		name = new QuestionText(pregnancyQuestions, "name");

		// Container: heightWeightQuestions = { weight, height } 
		heightWeightQuestions = new QContainer(root, "heightWeightQuestions");
		weight = new QuestionNum(heightWeightQuestions, "weight");
		height = new QuestionNum(heightWeightQuestions, "height");

		session = SessionFactory.createSession(kb);
		costBenefit = new PSMethodCostBenefit();
		costBenefit.init(session);

		session.getInterview().setFormStrategy(new NextUnansweredQuestionFormStrategy());
		agenda = session.getInterview().getInterviewAgenda();
	}

	@Test
	public void simpleIndicationTest() {
		// initially the agenda is empty
		assertTrue(agenda.isEmpty());

		// PUT the containers onto the agenda by indication, order must not
		// change
		Fact factHeig = FactFactory.createFact(session, heightWeightQuestions,
				new Indication(
						State.INDICATED), costBenefit, costBenefit);
		session.getBlackboard().addInterviewFact(factHeig);
		Fact factPreg = FactFactory.createFact(session, pregnancyQuestions,
				new Indication(State.INDICATED), costBenefit, costBenefit);
		session.getBlackboard().addInterviewFact(factPreg);
		assertFalse(agenda.isEmpty());

		// EXPECT: weight is the next question
		assertEquals(weight, session.getInterview().nextForm().getInterviewObject());
		// SET: weight = 80
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(weight, new NumValue(80)));

		// EXPECT: height is the next question
		assertEquals(height, session.getInterview().nextForm().getInterviewObject());
		// SET: height = 180
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(height, new NumValue(180)));

		// EXPECT: all question have been answered, so the QContainer
		// heightWeightQuestion should be removed
		assertFalse(session.getInterview().getInterviewAgenda().onAgenda(heightWeightQuestions));
		// EXPECT: QContainer pregnancyQuestions is still on agenda
		assertTrue(session.getInterview().getInterviewAgenda().onAgenda(pregnancyQuestions));

		// EXPECT: sex is the next question
		assertEquals(sex, session.getInterview().nextForm().getInterviewObject());
		// SET: sex = male
		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(sex, male));

		// EXPECT: name is the next question
		assertEquals(name, session.getInterview().nextForm().getInterviewObject());
		// SET: name = "joba"
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(name, new TextValue("joba")));

		// EXPECT: all question have been answered, so the QContainer
		// pregnancyQuestions should be removed
		assertFalse(session.getInterview().getInterviewAgenda().onAgenda(pregnancyQuestions));

		// EXPECT: the agenda is empty now
		assertEquals(session.getInterview().nextForm(), EmptyForm.getInstance());
	}

}
