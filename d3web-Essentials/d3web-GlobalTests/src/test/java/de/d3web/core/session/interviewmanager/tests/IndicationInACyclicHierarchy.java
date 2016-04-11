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

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.knowledge.InterviewObject;
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
import de.d3web.interview.EmptyForm;
import de.d3web.interview.Interview;
import de.d3web.interview.InterviewAgenda;
import de.d3web.interview.NextUnansweredQuestionFormStrategy;
import de.d3web.interview.inference.PSMethodInterview;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Checks, that the indication of follow-up questions is correctly performed
 * even for cyclic hierarchies.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 30.09.2010
 */
public class IndicationInACyclicHierarchy {

	KnowledgeBase kb;
	Session session;
	InterviewAgenda agenda;

	QContainer pregnancyQuestions;
	QuestionOC pregnant, pregnancyTest;
	ChoiceValue certain, dontKnow, yes;
	private Interview interview;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();

		QASet root = kb.getRootQASet();

		// root {container}
		// - pregnancyQuestions {container}
		// -- pregnant [oc]
		// --- pregnancyTest [oc]
		// --- ->pregnant [oc]
		// if pregnant=don't know, then indicate pregancyQuestions again and the
		// other question 'pregancyTest'

		pregnancyQuestions = new QContainer(root, "pregnancyQuestions");
		pregnant = new QuestionOC(pregnancyQuestions, "pregnant", "certain", "dontKnow");
		pregnancyTest = new QuestionOC(pregnant, "pregnancyTest", "yes", "no");
		// insert cyclic child relation
		pregnancyTest.addChild(pregnant);

		certain = new ChoiceValue(KnowledgeBaseUtils.findChoice(pregnant, "certain"));
		dontKnow = new ChoiceValue(KnowledgeBaseUtils.findChoice(pregnant, "dontKnow"));
		yes = new ChoiceValue(KnowledgeBaseUtils.findChoice(pregnancyTest, "yes"));

		RuleFactory.createIndicationRule(pregnancyQuestions, new CondEqual(pregnant,
				dontKnow));
		RuleFactory.createIndicationRule(pregnancyTest,
				new CondEqual(pregnant, dontKnow));

		kb.setInitQuestions(Arrays.asList(new QASet[] { pregnancyQuestions }));

		session = SessionFactory.createSession(kb);
		interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
		interview.setFormStrategy(new NextUnansweredQuestionFormStrategy());
		agenda = interview.getInterviewAgenda();
	}

	@Test
	public void testWithNoCyclicCall() {
		// the cyclic indication is not activated for pregnant=certain
		InterviewObject nextObjectOnAgenda = interview.nextForm().getActiveQuestions().get(0);
		assertEquals(pregnant, nextObjectOnAgenda);

		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(pregnant, certain));
		assertEquals(EmptyForm.getInstance(), interview.nextForm());
	}

	@Test
	public void testWithCyclicCall() {
		// the cyclic indication is not activated for pregnant=certain
		InterviewObject nextObjectOnAgenda = interview.nextForm().getActiveQuestions().get(0);
		assertEquals(pregnant, nextObjectOnAgenda);

		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(pregnant, dontKnow));
		nextObjectOnAgenda = interview.nextForm().getActiveQuestions().get(0);
		assertEquals(pregnancyTest, nextObjectOnAgenda);

		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(pregnancyTest, yes));
		assertEquals(EmptyForm.getInstance(), interview.nextForm());
	}
}
