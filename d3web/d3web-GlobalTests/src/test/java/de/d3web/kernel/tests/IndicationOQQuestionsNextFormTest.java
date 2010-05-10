/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.kernel.tests;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.plugin.test.InitPluginManager;

/**
 * This test is designed to control the indication mechanisms (calculation/
 * provision of next questions by Session.getInterviewManager.nextForm()) 
 * 
 * Test Framework Conditions:  One-Question (OQ) Form Strategy, Questions-only
 * i.e., the provided next form always only contains at most one question
 * in its list of indicated questions, and tested is only a knowledge base 
 * consisting of some questions and follow-up questions (no questionnaires)
 * 
 * The tested knowledge base contains the following terminology:
 * 
 * <b>Questions</b>
 * Sex [oc]
 * - Female
 * -- Pregnant [y/n]
 * --- Yes
 * --- No
 * - Male
 * Ask_Headache [y/n]
 * - Yes
 * - No
 * Headache [y/n]
 * - Yes
 * - No  
 * Nausea [y/n]
 * - Yes
 * - No
 * 
 * The control flow is defined by the following (contra) indication <b>Rules</b>:
 * 
 * Sex == Female 		=> Pregnant INDICATED  		(ask Pregnant, Ask_Headache...)
 * Sex == Male 			=> 							(ask Ask_Headache...)
 * Pregnant == Yes 		=> Nausea INSTANT_INDICATED	(ask Nausea, Ask_Headache...?!)
 * Pregnant == No 		=>							(ask Ask_Headache...) 
 * Ask_Headache == No 	=> Headache CONTRA_INDICATED (ask Nausea)
 * Ask_Headache == Yes 	=> 							(ask Headache, Nausea...)
 *  
 * @author Martina Freiberg
 *
 */
public class IndicationOQQuestionsNextFormTest {

	private static KnowledgeBaseManagement kbm;
	private static Session session;
	

	@BeforeClass
	public static void setUp() throws Exception {
		InitPluginManager.init();
		kbm = KnowledgeBaseManagement.createInstance();
		addTerminologyObjects();
		addRules();
		session = SessionFactory.createSession(kbm.getKnowledgeBase());
	}
	
	// add the knowledge base objects, i.e., questions and answers
	private static void addTerminologyObjects() {
		
		// Question 'Sex'
		String sex = "Sex";
		String[] sexAlternatives = new String[] {"Female", "Male"};
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
		RuleFactory.createIndicationRule(kbm.createRuleID(), pregnant, condition);

		// Create instant_indication rule: Pregnant == Yes => Nausea
		condition = new CondEqual(pregnant, yes);
		RuleFactory.createInstantIndicationRule(kbm.createRuleID(), nausea, condition);
		
		// Create contra_indication rule: Ask_Headache == No => Headache c_i
		condition = new CondEqual(askHead, no);
		RuleFactory.createIndicationRule(kbm.createRuleID(), headache, condition);
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
		
		List<InterviewObject> intervObjs;
		Question sex = kbm.findQuestion("Sex");
		Question pregnant = kbm.findQuestion("Pregnant");
		Question askHead = kbm.findQuestion("Ask_Headache");
		
		// SET Sex == Male
		Value male = kbm.findValue(sex, "Male");
		session.setValue(sex, male);

		// TEST whether value-setting worked correctly: Sex == Male
		Value sexValue = session.getValue(sex);
		assertEquals("Question Sex has wrong value", male, sexValue);

		// One Question Form Strategy should return exactly one element here.
		intervObjs = session.getInterviewManager().nextForm().getInterviewObjects();
		// assertTrue("InterviewManager.nextForm() should have provided one " +
		// "next element, but returned " + intervObjs.size() +
		// " elements instead", intervObjs.size() == 1);

		// Sex == Male, thus Pregnant is not indicated. First question on
		// indicated list should be Ask_Headache.
		// assertEquals("Answering question Sex with value Male should bring " +
		// "up Ask_Headache as next question", askHead, intervObjs.get(0));

		// EXPECTED: indication state (Ask_Headache) == NEUTRAL
		assertEquals("Question Ask_Headache has wrong indication state", 
				new Indication(State.NEUTRAL),
				session.getBlackboard().getIndication(askHead));


		// SET Sex == Female
		Value female = kbm.findValue(sex, "Female");
		session.setValue(sex, female);
		
		// TEST whether value-setting worked correctly: Sex == Female
		sexValue = session.getValue(sex);
		assertEquals("Question Sex has wrong value", female, sexValue);

		// One Question Form Strategy should return exactly one element here.
		intervObjs = session.getInterviewManager().nextForm().getInterviewObjects();
		assertTrue("InterviewManager.nextForm() should have provided one " +
				"next element, but returned " + intervObjs.size() +
				" elements instead", intervObjs.size() == 1);

		// Sex == Female, thus Pregnant is indicated. First question on
		// indicated list should be Pregnant here.
		// assertEquals("Answering question Sex with value Female should bring up "
		// +
		// "Pregnant as next question", pregnant, intervObjs.get(0));
		
		// EXPECTED: indication state (Pregnant) == INDICATED
		assertEquals("Question Pregnant has wrong indication state",
				new Indication(State.INDICATED),
				session.getBlackboard().getIndication(pregnant));
		

		// RESET Sex = Male
		male = kbm.findValue(sex, "Male");
		session.setValue(sex, male);
		
		// TEST whether value-setting worked correctly: Sex == Male
		sexValue = session.getValue(sex);
		assertEquals("Question Sex has wrong value", male, sexValue);
		
		// One Question Form Strategy should return exactly one element here.
		intervObjs = session.getInterviewManager().nextForm().getInterviewObjects();
		// assertTrue("InterviewManager.nextForm() should have provided one " +
		// "next element, but returned " + intervObjs.size() +
		// " elements instead", intervObjs.size() == 1);

		// Sex == Male, thus Pregnant is not indicated. First question on
		// indicated list should be Ask_Headache (again now).
		// assertEquals("Answering question Sex with value Male should bring up "
		// +
		// "Ask_Headache as next question", askHead, intervObjs.get(0));

		// EXPECTED: indication state (Ask_Headache) == INDICATED
		assertEquals("Question Ask_Headache has wrong indication state",
				new Indication(State.NEUTRAL),
				session.getBlackboard().getIndication(askHead));
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

		// TODO
	}

	
}
