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

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.interview.DefaultForm;
import de.d3web.interview.Form;
import de.d3web.interview.Interview;
import de.d3web.interview.InterviewAgenda;
import de.d3web.interview.inference.PSMethodInterview;
import com.denkbares.plugin.test.InitPluginManager;

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

	private static KnowledgeBase kb;
	private static Session session;
	private static InterviewAgenda agenda;
	private static Interview interview;

	@BeforeClass
	public static void initTest() throws IOException {
		InitPluginManager.init();
	}

	@Before
	public void setUp() {
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		addTerminologyObjects();
		addRules();
		session = SessionFactory.createSession(kb);
		interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
		agenda = interview.getInterviewAgenda();
	}

	// add the knowledge base objects, i.e., questions and answers
	private static void addTerminologyObjects() {
		// Question 'Sex'
		String sex = "Sex";
		String[] sexAlternatives = new String[] {
				"Female", "Male" };
		QuestionOC sexQ =
				new QuestionOC(kb.getRootQASet(), sex, sexAlternatives);

		// Question 'Pregnant'
		String pregnant = "Pregnant";
		new QuestionYN(sexQ, pregnant);

		// Question 'Ask_Headache'
		String askHead = "Ask_Headache";
		new QuestionYN(kb.getRootQASet(), askHead);

		// Question 'Headache'
		String headache = "Headache";
		new QuestionYN(kb.getRootQASet(), headache);

		// Question 'Nausea'
		String nausea = "Nausea";
		new QuestionYN(kb.getRootQASet(), nausea);
	}

	// add the indication rules to the knowledge base
	private static void addRules() {

		Question sex = kb.getManager().searchQuestion("Sex");
		Value female = KnowledgeBaseUtils.findValue(sex, "Female");

		Question pregnant = kb.getManager().searchQuestion("Pregnant");
		Value yes = KnowledgeBaseUtils.findValue(pregnant, "Yes");

		Question nausea = kb.getManager().searchQuestion("Nausea");

		Question askHead = kb.getManager().searchQuestion("Ask_Headache");
		Value no = KnowledgeBaseUtils.findValue(askHead, "No");

		Question headache = kb.getManager().searchQuestion("Headache");

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
		Question sex = kb.getManager().searchQuestion("Sex");
		assertNotNull("Question 'Sex' isn't contained in the knowledge base.", sex);

		// Values of 'Sex'
		Value male = KnowledgeBaseUtils.findValue(sex, "Male");
		assertNotNull("Value 'Male' for Question 'Sex' isn't contained " +
				"in the knowledge base", male);
		Value female = KnowledgeBaseUtils.findValue(sex, "Female");
		assertNotNull("Value 'Female' for Question 'Sex' isn't contained " +
				"in the knowledge base", female);

		// Question 'Pregnant'
		Question pregnant = kb.getManager().searchQuestion("Pregnant");
		assertNotNull("Question 'Pregnant' isn't contained in the knowledge " +
				"base.", pregnant);

		// Values of 'Pregnant'
		Value yes = KnowledgeBaseUtils.findValue(pregnant, "Yes");
		assertNotNull("Value 'Yes' for Question 'Pregnant' isn't " +
				"contained in the knowledge base", yes);
		Value no = KnowledgeBaseUtils.findValue(pregnant, "No");
		assertNotNull("Value 'No' for Question 'Pregnant' isn't " +
				"contained in the knowledge base", no);

		// Question 'Ask Headache'
		Question askHead = kb.getManager().searchQuestion("Ask_Headache");
		assertNotNull("Question 'Ask_Headache' isn't contained in the knowledge " +
				"base.", askHead);

		// Values of 'Ask Headache'
		yes = KnowledgeBaseUtils.findValue(askHead, "Yes");
		assertNotNull("Value 'Yes' for Question 'Ask_Headache' isn't " +
				"contained in the knowledge base", yes);
		no = KnowledgeBaseUtils.findValue(askHead, "No");
		assertNotNull("Value 'No' for Question 'Ask_Headache' isn't " +
				"contained in the knowledge base", no);

		// Question 'Headache'
		Question headache = kb.getManager().searchQuestion("Headache");
		assertNotNull("Question 'Headache' isn't contained in the knowledge " +
				"base.", headache);

		// Values of 'Headache'
		yes = KnowledgeBaseUtils.findValue(headache, "Yes");
		assertNotNull("Value 'Yes' for Question 'Headache' isn't " +
				"contained in the knowledge base", yes);
		no = KnowledgeBaseUtils.findValue(headache, "No");
		assertNotNull("Value 'No' for Question 'Headache' isn't " +
				"contained in the knowledge base", no);

		// Question 'Nausea'
		Question nausea = kb.getManager().searchQuestion("Nausea");
		assertNotNull("Question 'Nausea' isn't contained in the knowledge " +
				"base.", nausea);

		// Values of 'Nausea'
		yes = KnowledgeBaseUtils.findValue(nausea, "Yes");
		assertNotNull("Value 'Yes' for Question 'Nausea' isn't " +
				"contained in the knowledge base", yes);
		no = KnowledgeBaseUtils.findValue(nausea, "No");
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
		Question sex = kb.getManager().searchQuestion("Sex");
		Question pregnant = kb.getManager().searchQuestion("Pregnant");
		Question askHead = kb.getManager().searchQuestion("Ask_Headache");

		// only put normal question flow on agenda, no follow-up questions
		agenda.append(sex, new Indication(State.INDICATED, kb.getManager().getTreeIndex(sex)));
		agenda.append(askHead, new Indication(State.INDICATED,
				kb.getManager().getTreeIndex(askHead)));

		// SET Sex == Male and test whether that worked correctly
		Value male = KnowledgeBaseUtils.findValue(sex, "Male");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(sex,
						male, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));
		Value sexValue = session.getBlackboard().getValue(sex);
		assertEquals("Question Sex has wrong value ", male, sexValue);
		// OQ Strategy should return exactly one element here.
		intervObj = interview.nextForm().getActiveQuestions().get(0);
		assertTrue("InterviewManager.nextForm() should have provided one " +
				"next element, but returned.", intervObj != null);

		// Sex == Male => EXPECTED: Ask_Headache
		assertEquals("Answering question Sex with value Male should bring " +
				"up Ask_Headache as next question", askHead, intervObj);

		// EXPECTED: Ask_Headache == NEUTRAL, Pregnant == NEUTRAL
		assertEquals("Question Ask_Headache has wrong indication state ",
				State.NEUTRAL,
				session.getBlackboard().getIndication(askHead).getState());
		assertEquals("Question Pregnant has wrong indication state ",
				State.NEUTRAL,
				session.getBlackboard().getIndication(pregnant).getState());

		// SET Sex == Female and test whether that worked correctly
		Value female = KnowledgeBaseUtils.findValue(sex, "Female");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(sex,
						female, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));
		sexValue = session.getBlackboard().getValue(sex);
		assertEquals("Question Sex has wrong value ", female, sexValue);

		// OQ Strategy should return exactly one element here.
		intervObj = interview.nextForm().getActiveQuestions().get(0);
		assertTrue("InterviewManager.nextForm() should have provided one " +
				"next element.", intervObj != null);

		// Sex == Female => EXPECTED: Pregnant
		assertEquals("Answering question Sex with value Female should bring up " +
				"Pregnant as next question", pregnant, intervObj);

		// EXPECTED: Pregnant == INDICATED
		assertEquals("Question Pregnant has wrong indication state ",
				State.INDICATED,
				session.getBlackboard().getIndication(pregnant).getState());

		// RESET Sex = Male and test whether setting worked
		male = KnowledgeBaseUtils.findValue(sex, "Male");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(sex,
						male, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));
		sexValue = session.getBlackboard().getValue(sex);
		assertEquals("Question Sex has wrong value ", male, sexValue);

		// OQ Strategy should return exactly one element here.
		intervObj = interview.nextForm().getActiveQuestions().get(0);
		assertTrue("InterviewManager.nextForm() should have provided one " +
				"next element, but returned .", intervObj != null);

		// Sex == Male => EXPECTED: Ask_Headache (again now).
		assertEquals("Answering question Sex with value Male should bring up "
				+ "Ask_Headache as next question", askHead, intervObj);

		// EXPECTED: Pregnant == NEUTRAL, Ask_Headache == NEUTRAL
		assertEquals("Question Pregnant has wrong indication state ",
				State.NEUTRAL,
				session.getBlackboard().getIndication(pregnant).getState());
		assertEquals("Question Ask_Headache has wrong indication state ",
				State.NEUTRAL,
				session.getBlackboard().getIndication(askHead).getState());
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
		Question askHead = kb.getManager().searchQuestion("Ask_Headache");
		Question headache = kb.getManager().searchQuestion("Headache");
		Question nausea = kb.getManager().searchQuestion("Nausea");

		agenda.append(headache, new Indication(State.INDICATED, 0));
		agenda.append(nausea, new Indication(State.INDICATED, 0));

		// SET Ask_Headache == Yes and test setting
		Value yes = KnowledgeBaseUtils.findValue(askHead, "Yes");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(askHead,
						yes, PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
		Value askHValue = session.getBlackboard().getValue(askHead);
		assertEquals("Question Ask_Headache has wrong value ", yes, askHValue);

		// OQ Strategy should return exactly one element here.
		intervObj = interview.nextForm().getActiveQuestions().get(0);
		assertTrue("InterviewManager.nextForm() should have provided one " +
				"next element. ", intervObj != null);

		// EXPECTED: Headache == NEUTRAL
		assertEquals("Question Headache has wrong indication state ",
				State.NEUTRAL,
				session.getBlackboard().getIndication(headache).getState());

		// EXPECTED next question: Headache
		assertEquals("Answering question Ask_Headache with value Yes should bring "
				+ "up Headache as next question", headache, intervObj);

		// SET Ask_Headache == No and test setting
		agenda.activate(headache);
		System.out.println(session.getBlackboard().getIndication(headache));
		Value no = KnowledgeBaseUtils.findValue(askHead, "No");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(askHead,
						no, PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
		askHValue = session.getBlackboard().getValue(askHead);
		assertEquals("Question Ask_Headache has wrong value ", no, askHValue);

		// OQ Strategy should return exactly one element here.
		Form nextForm = interview.nextForm();
		assertTrue("Interview.nextFrom() should provide a DefaultForm.",
				nextForm instanceof DefaultForm);
		assertTrue("The Default Form should contain no active quesitons.",
				nextForm.getActiveQuestions().isEmpty());

		// EXPECTED: Headache == CONTRA_INDICATED
		assertEquals("Question Headache has wrong indication state ",
				State.CONTRA_INDICATED,
				session.getBlackboard().getIndication(headache).getState());

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
