/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.kernel.tests;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.interviewmanager.Interview;
import de.d3web.core.session.interviewmanager.InterviewAgenda;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.test.InitPluginManager;

/**
 * This test is designed to control the indication mechanisms (calculation/
 * provision of next questions by Session.getInterviewManager.nextForm())
 * 
 * Test Framework Conditions: One-Question (OQ) Form Strategy, Questions-only
 * i.e., the provided next form always only contains at most one question in its
 * list of indicated questions, and tested is only a knowledge base consisting
 * of some questions and follow-up questions (no questionnaires)
 * 
 * The tested knowledge base contains the following terminology:
 * 
 * <b>Questions</b> Sex [oc] - Female -- Pregnant [y/n] --- Yes --- No - Male
 * Ask_Headache [y/n] - Yes - No Headache [y/n] - Yes - No Nausea [y/n] - Yes -
 * No
 * 
 * The control flow is defined by the following (contra) indication
 * <b>Rules</b>:
 * 
 * <ul>
 * <li>Sex == Female => Pregnant INDICATED (ask Pregnant)
 * <li>Sex == Male => (ask Ask_Headache)
 * <li>Pregnant == Yes => Nausea INSTANT_INDICATED (ask Nausea)
 * <li>Pregnant == No => (ask Ask_Headache)
 * <li>Ask_Headache == No => Headache CONTRA_INDICATED (ask Nausea)
 * <li>Ask_Headache == Yes => (ask Headache)
 * </ul>
 * 
 * @author Martina Freiberg
 * 
 */
public class IndicationOQQuestionsNextFormTest {

	private static KnowledgeBaseManagement kbm;
	private static Session session;
	private static InterviewAgenda agenda;
	private static Interview interview;

	@BeforeClass
	public static void setUp() throws Exception {
		InitPluginManager.init();
		kbm = KnowledgeBaseManagement.createInstance();
		addTerminologyObjects();
		addRules();
		session = SessionFactory.createSession(kbm.getKnowledgeBase());
		agenda = session.getInterview().getInterviewAgenda();
		interview = session.getInterview();
	}

	// add the knowledge base objects, i.e., questions and answers
	private static void addTerminologyObjects() {

		// Question 'Sex'
		String sex = "Sex";
		String[] sexAlternatives = new String[] {
				"Female", "Male" };
		QuestionOC sexQ =
				kbm.createQuestionOC(sex, kbm.getKnowledgeBase().getRootQASet(), sexAlternatives);

		// Question 'Pregnant'
		String pregnant = "Pregnant";
		kbm.createQuestionYN(pregnant, sexQ);

		// Question 'Ask_Headache'
		String askHead = "Ask_Headache";
		kbm.createQuestionYN(askHead, kbm.getKnowledgeBase().getRootQASet());

		// Question 'Headache'
		String headache = "Headache";
		kbm.createQuestionYN(headache, kbm.getKnowledgeBase().getRootQASet());

		// Question 'Nausea'
		String nausea = "Nausea";
		kbm.createQuestionYN(nausea, kbm.getKnowledgeBase().getRootQASet());
	}

	// add the indication rules to the knowledge base
	private static void addRules() {

		Question sex = kbm.findQuestion("Sex");
		Value female = kbm.findValue(sex, "Female");

		Question pregnant = kbm.findQuestion("Pregnant");
		Value yes = kbm.findValue(pregnant, "Yes");

		Question nausea = kbm.findQuestion("Nausea");

		Question askHead = kbm.findQuestion("Ask_Headache");
		Value no = kbm.findValue(askHead, "No");

		Question headache = kbm.findQuestion("Headache");

		// Create indication rule: Sex == Female => Pregnant
		Condition condition = new CondEqual(sex, female);
		RuleFactory.createIndicationRule(pregnant, condition);

		// Create contra_indication rule: Ask_Headache == No => Headache c_i
		condition = new CondEqual(askHead, no);
		RuleFactory.createContraIndicationRule(headache, condition);

		// Create instant_indication rule: Pregnant == Yes => Nausea
		condition = new CondEqual(pregnant, yes);
		RuleFactory.createInstantIndicationRule(nausea, condition);
	}

	// tests, whether all kb-terminology objects are contained as hard coded
	@Test
	public void testTerminlogyObjectExistence() {

		// Question 'Sex'
		Question sex = kbm.findQuestion("Sex");
		assertNotNull("Question 'Sex' isn't contained in the knowledge base.", sex);

		// Values of 'Sex'
		Value male = kbm.findValue(sex, "Male");
		assertNotNull("Value 'Male' for Question 'Sex' isn't contained " +
				"in the knowledge base", male);
		Value female = kbm.findValue(sex, "Female");
		assertNotNull("Value 'Female' for Question 'Sex' isn't contained " +
				"in the knowledge base", female);

		// Question 'Pregnant'
		Question pregnant = kbm.findQuestion("Pregnant");
		assertNotNull("Question 'Pregnant' isn't contained in the knowledge " +
				"base.", pregnant);

		// Values of 'Pregnant'
		Value yes = kbm.findValue(pregnant, "Yes");
		assertNotNull("Value 'Yes' for Question 'Pregnant' isn't " +
				"contained in the knowledge base", yes);
		Value no = kbm.findValue(pregnant, "No");
		assertNotNull("Value 'No' for Question 'Pregnant' isn't " +
				"contained in the knowledge base", no);

		// Question 'Ask Headache'
		Question askHead = kbm.findQuestion("Ask_Headache");
		assertNotNull("Question 'Ask_Headache' isn't contained in the knowledge " +
				"base.", askHead);

		// Values of 'Ask Headache'
		yes = kbm.findValue(askHead, "Yes");
		assertNotNull("Value 'Yes' for Question 'Ask_Headache' isn't " +
				"contained in the knowledge base", yes);
		no = kbm.findValue(askHead, "No");
		assertNotNull("Value 'No' for Question 'Ask_Headache' isn't " +
				"contained in the knowledge base", no);

		// Question 'Headache'
		Question headache = kbm.findQuestion("Headache");
		assertNotNull("Question 'Headache' isn't contained in the knowledge " +
				"base.", headache);

		// Values of 'Headache'
		yes = kbm.findValue(headache, "Yes");
		assertNotNull("Value 'Yes' for Question 'Headache' isn't " +
				"contained in the knowledge base", yes);
		no = kbm.findValue(headache, "No");
		assertNotNull("Value 'No' for Question 'Headache' isn't " +
				"contained in the knowledge base", no);

		// Question 'Nausea'
		Question nausea = kbm.findQuestion("Nausea");
		assertNotNull("Question 'Nausea' isn't contained in the knowledge " +
				"base.", nausea);

		// Values of 'Nausea'
		yes = kbm.findValue(nausea, "Yes");
		assertNotNull("Value 'Yes' for Question 'Nausea' isn't " +
				"contained in the knowledge base", yes);
		no = kbm.findValue(nausea, "No");
		assertNotNull("Value 'No' for Question 'Nausea' isn't " +
				"contained in the knowledge base", no);
	}

	/**
	 * Tests, whether rule Sex == Female => INDICATE Pregnant implies the
	 * correct return values in the interview objects list of
	 * InterviewManager.nextForm(). At the same time, also the indication state
	 * of the respective interview object is tested
	 * 
	 * @author Martina Freiberg
	 * @created 10.05.2010
	 */
	@Test
	public void testIndication() {

		InterviewObject intervObj;
		Question sex = kbm.findQuestion("Sex");
		Question pregnant = kbm.findQuestion("Pregnant");
		Question askHead = kbm.findQuestion("Ask_Headache");

		// only put normal question flow on agenda, no follow-up questions
		agenda.append(sex);
		agenda.append(askHead);

		// SET Sex == Male and test whether that worked correctly
		Value male = kbm.findValue(sex, "Male");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, sex,
						male, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));
		Value sexValue = session.getBlackboard().getValue(sex);
		assertEquals("Question Sex has wrong value ", male, sexValue);
		// OQ Strategy should return exactly one element here.
		intervObj = interview.nextForm().getInterviewObject();
		assertTrue("InterviewManager.nextForm() should have provided one " +
				"next element, but returned.", intervObj != null);

		// Sex == Male => EXPECTED: Ask_Headache
		assertEquals("Answering question Sex with value Male should bring " +
				"up Ask_Headache as next question", askHead, intervObj);

		// EXPECTED: Ask_Headache == NEUTRAL, Pregnant == NEUTRAL
		assertEquals("Question Ask_Headache has wrong indication state ",
				new Indication(State.NEUTRAL),
				session.getBlackboard().getIndication(askHead));
		assertEquals("Question Pregnant has wrong indication state ",
				new Indication(State.NEUTRAL),
				session.getBlackboard().getIndication(pregnant));

		// SET Sex == Female and test whether that worked correctly
		Value female = kbm.findValue(sex, "Female");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, sex,
						female, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));
		sexValue = session.getBlackboard().getValue(sex);
		assertEquals("Question Sex has wrong value ", female, sexValue);

		// OQ Strategy should return exactly one element here.
		intervObj = session.getInterview().nextForm().getInterviewObject();
		assertTrue("InterviewManager.nextForm() should have provided one " +
				"next element.", intervObj != null);

		// Sex == Female => EXPECTED: Pregnant
		assertEquals("Answering question Sex with value Female should bring up " +
				"Pregnant as next question", pregnant, intervObj);

		// EXPECTED: Pregnant == INDICATED
		assertEquals("Question Pregnant has wrong indication state ",
				new Indication(State.INDICATED),
				session.getBlackboard().getIndication(pregnant));

		// RESET Sex = Male and test whether setting worked
		male = kbm.findValue(sex, "Male");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, sex,
						male, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));
		sexValue = session.getBlackboard().getValue(sex);
		assertEquals("Question Sex has wrong value ", male, sexValue);

		// OQ Strategy should return exactly one element here.
		intervObj = session.getInterview().nextForm().getInterviewObject();
		assertTrue("InterviewManager.nextForm() should have provided one " +
				"next element, but returned .", intervObj != null);

		// Sex == Male => EXPECTED: Ask_Headache (again now).
		assertEquals("Answering question Sex with value Male should bring up "
				+ "Ask_Headache as next question", askHead, intervObj);

		// EXPECTED: Pregnant == NEUTRAL, Ask_Headache == NEUTRAL
		assertEquals("Question Pregnant has wrong indication state ",
				new Indication(State.NEUTRAL),
				session.getBlackboard().getIndication(pregnant));
		assertEquals("Question Ask_Headache has wrong indication state ",
				new Indication(State.NEUTRAL),
				session.getBlackboard().getIndication(askHead));
	}

	/**
	 * Tests, whether rule Ask_Headache == No => CONTRA_INDICATE Headache
	 * implies the correct return values in the indicated list of
	 * InterviewManager.nextForm()
	 * 
	 * @author Martina Freiberg
	 * @created 10.05.2010
	 */
	@Test
	public void testContraIndication() {

		InterviewObject intervObj;
		Question askHead = kbm.findQuestion("Ask_Headache");
		Question headache = kbm.findQuestion("Headache");
		Question nausea = kbm.findQuestion("Nausea");

		agenda.append(headache);
		agenda.append(nausea);

		// SET Ask_Headache == Yes and test setting
		Value yes = kbm.findValue(askHead, "Yes");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, askHead,
						yes, PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
		Value askHValue = session.getBlackboard().getValue(askHead);
		assertEquals("Question Ask_Headache has wrong value ", yes, askHValue);

		// OQ Strategy should return exactly one element here.
		intervObj = session.getInterview().nextForm().getInterviewObject();
		assertTrue("InterviewManager.nextForm() should have provided one " +
				"next element. ", intervObj != null);

		// EXPECTED: Headache == NEUTRAL
		assertEquals("Question Headache has wrong indication state ",
				new Indication(State.NEUTRAL),
				session.getBlackboard().getIndication(headache));

		// EXPECTED next question: Headache
		assertEquals("Answering question Ask_Headache with value Yes should bring "
				+ "up Headache as next question", headache, intervObj);

		// SET Ask_Headache == No and test setting
		agenda.activate(headache);
		System.out.println(session.getBlackboard().getIndication(headache));
		Value no = kbm.findValue(askHead, "No");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, askHead,
						no, PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
		askHValue = session.getBlackboard().getValue(askHead);
		assertEquals("Question Ask_Headache has wrong value ", no, askHValue);

		// OQ Strategy should return exactly one element here.
		intervObj = session.getInterview().nextForm().getInterviewObject();
		assertTrue("InterviewManager.nextForm() should have provided one " +
				"next element.", intervObj != null);

		// EXPECTED: Headache == CONTRA_INDICATED
		assertEquals("Question Headache has wrong indication state ",
				new Indication(State.CONTRA_INDICATED),
				session.getBlackboard().getIndication(headache));

		// EXPECTED next question: Nausea
		// assertEquals("Answering question Ask_Headache with value No should bring "
		// + "up Nausea as next question", nausea, intervObjs.get(0));
	}

	/**
	 * Tests, whether rule Pregnant == Yes => INSTANT_INDICATE Nausea implies
	 * the correct return values in the indicated list of
	 * InterviewManager.nextForm()
	 * 
	 * @author Martina Freiberg
	 * @created 10.05.2010
	 */
	@Test
	public void testInstantIndication() {

		// TODO
		// Pregnant == Yes => INSTANTINDICATE Nausea
		// Nausea next on the agenda and instant indicated?!
		// Pregnant == No =>
		// Headache next on the agenda, Nausea NEUTRAL
	}

}
