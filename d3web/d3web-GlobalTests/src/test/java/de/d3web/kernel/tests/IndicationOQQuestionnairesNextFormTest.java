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

import org.junit.BeforeClass;
import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Value;
import de.d3web.plugin.test.InitPluginManager;

/**
 * This test is designed to control the indication mechanisms (calculation/
 * provision of next questions by Session.getInterviewManager.nextForm())
 * 
 * Test Framework Conditions: One-Question (OQ) Form Strategy, Questionnaires
 * i.e., the provided next form contains QASets/QContainers?! ... in its list of
 * indicated questions, and tested is only a knowledge base consisting of some
 * questions and follow-up questions (no questionnaires)
 * 
 * The tested knowledge base contains the following terminology:
 * 
 * <b>Questions</b> Start Questions {QContainer} - Sex [oc] -- Male -- Female
 * --- Pregnant [y/n] ---- Yes ----- Pregnancy Questions ---- No ----- Common
 * Questions Pregnancy Questions {QContainer} - Nausea [y/n] - Month [Num]
 * Common Questions {QContainer} - Headache [y/n] Male Specific - Prostatitis
 * [y/n]
 * 
 * The problem solving is based on the following <b>Rules</b>:
 * 
 * Sex == Female => INDICATE Pregnant (ask Pregnant) Sex == Male =>
 * CONTRA_INDICATE Pregnancy Questions => INSTANT_INDICATE Male Specific (ask
 * Prostatitis) Pregnant == Yes => INDICATE Pregnancy Questions (ask Nausea)
 * Pregnant == No => CONTRA_INDICATE Pregnancy Questions => INDICATE Common
 * Questions (ask Headache)
 * 
 * @author Martina Freiberg
 * 
 */
public class IndicationOQQuestionnairesNextFormTest {

	private static KnowledgeBaseManagement kbm;

	@BeforeClass
	public static void setUp() throws Exception {
		InitPluginManager.init();
		kbm = KnowledgeBaseManagement.createInstance();
		addTerminologyObjects();
		addRules();
	}

	// TODO rework
	// add the knowledge base objects, i.e., questions and answers
	private static void addTerminologyObjects() {

		// QContainer Start Questions

		// Question Sex
		String sex = "Sex";
		String[] sexAlternatives = new String[] {
				"Female", "Male" };
		kbm.createQuestionOC(sex, kbm.getKnowledgeBase().getRootQASet(), sexAlternatives);

		// Question Pregnant
		String pregnant = "Pregnant";
		kbm.createQuestionYN(pregnant, kbm.getKnowledgeBase().getRootQASet());

		// QContainer Pregnancy Problems
		String pregProb = "Pregnancy Problems";
		QASet pregProbQASet = kbm.createQContainer(pregProb);

		// Question Nausea
		String nausea = "Nausea";
		kbm.createQuestionYN(nausea, pregProbQASet);

		// Question Month

		// QContainer Common Questions
		String otherProb = "Common Questions";
		QASet commonQASet = kbm.createQContainer(otherProb);

		// Question 'Headache'
		String headache = "Headache";
		kbm.createQuestionYN(headache, commonQASet);

		// QContainer Male Specific

		// Question Prostatitis

	}

	// TODO: rework, new KB
	private static void addRules() {

		Question sex = kbm.getKnowledgeBase().getManager().searchQuestion("Sex");
		Value male = KnowledgeBaseManagement.findValue(sex, "Male");
		Value female = KnowledgeBaseManagement.findValue(sex, "Female");

		Question pregnant = kbm.getKnowledgeBase().getManager().searchQuestion("Pregnant");
		Value yes = KnowledgeBaseManagement.findValue(pregnant, "Yes");
		Value no = KnowledgeBaseManagement.findValue(pregnant, "No");

		QContainer pregProbs = kbm.getKnowledgeBase().getManager().searchQContainer(
				"Pregnancy Problems");
		QContainer otherProbs = kbm.getKnowledgeBase().getManager().searchQContainer(
				"Other Problems");

		// Create indication rule: Sex == Female => Pregnant
		Condition condition = new CondEqual(sex, female);
		RuleFactory.createIndicationRule(pregnant, condition);

		// Create indication rule: Pregnant == Yes => Pregnancy Problems
		condition = new CondEqual(pregnant, yes);
		RuleFactory.createIndicationRule(pregProbs, condition);

		// Create indication rule: Pregnant == No => Other Problems
		condition = new CondEqual(pregnant, no);
		RuleFactory.createIndicationRule(otherProbs, condition);

		// Create indication rule: Sex == Male => Other Problems
		condition = new CondEqual(sex, male);
		RuleFactory.createIndicationRule(otherProbs, condition);

		// Create the contra-indication rule for question "Pregnant", i.e. the
		// rule that fires, if question "Sex" is answered otherwise by the user
		// Sex = Male => Pregnant = contra-indicated
		/*
		 * condition = new CondEqual(sex, male); List<QASet> contraindicated =
		 * new ArrayList<QASet>(); contraindicated.add(pregnant);
		 * RuleFactory.createContraIndicationRule(kbm.createRuleID(),
		 * contraindicated, condition);
		 */
	}

	// TODO rework
	// tests, whether all kb-terminology objects are contained as hard coded
	/*
	 * @Test public void testTerminlogyObjectExistence() {
	 * 
	 * // Question 'Sex' Question sex = kbm.findQuestion("Sex");
	 * assertNotNull("Question 'Sex' isn't contained in the knowledge base.",
	 * sex);
	 * 
	 * // Values of 'Sex' Value male = kbm.findValue(sex, "Male");
	 * assertNotNull("Value 'Male' for Question 'Sex' isn't contained " +
	 * "in the knowledge base", male); Value female = kbm.findValue(sex,
	 * "Female");
	 * assertNotNull("Value 'Female' for Question 'Sex' isn't contained " +
	 * "in the knowledge base", female);
	 * 
	 * // Question 'Pregnant' Question pregnant = kbm.findQuestion("Pregnant");
	 * assertNotNull("Question 'Pregnant' isn't contained in the knowledge " +
	 * "base.", pregnant);
	 * 
	 * // Values of 'Pregnant' Value yes = kbm.findValue(pregnant, "Yes");
	 * assertNotNull("Value 'Yes' for Question 'Pregnant' isn't " +
	 * "contained in the knowledge base", yes); Value no =
	 * kbm.findValue(pregnant, "No");
	 * assertNotNull("Value 'No' for Question 'Pregnant' isn't " +
	 * "contained in the knowledge base", no);
	 * 
	 * // QContainer 'Pregnancy Probelms' QContainer pregProbs =
	 * kbm.findQContainer("Pregnancy Problems");
	 * assertNotNull("QContainer 'Pregnancy Problems' isn't contained in the " +
	 * "knowledge base.", pregProbs);
	 * assertEquals("QContainer 'Pregnancy Problems' does not contain " +
	 * "one question as expected.", 1 , pregProbs.getChildren().length);
	 * 
	 * // Question 'Nausea' Question nausea = kbm.findQuestion("Nausea");
	 * assertNotNull("Question 'Nausea' isn't contained in the knowledge " +
	 * "base.", nausea);
	 * 
	 * // Values of 'Nausea' yes = kbm.findValue(nausea, "Yes");
	 * assertNotNull("Value 'Yes' for Question 'Nausea' isn't " +
	 * "contained in the knowledge base", yes); no = kbm.findValue(nausea,
	 * "No"); assertNotNull("Value 'No' for Question 'Nausea' isn't " +
	 * "contained in the knowledge base", no);
	 * 
	 * // QContainer 'Other Problems' QContainer otherProbs =
	 * kbm.findQContainer("Other Problems");
	 * assertNotNull("QContainer 'Other Problems' isn't contained in the " +
	 * "knowledge base.", otherProbs);
	 * assertEquals("QContainer 'Other Problems' does not contain " +
	 * "two questions as expected.", 2 , otherProbs.getChildren().length);
	 * 
	 * // Question 'Headache' Question headache = kbm.findQuestion("Headache");
	 * assertNotNull("Question 'Headache' isn't contained in the knowledge " +
	 * "base.", headache);
	 * 
	 * // Values of 'Headache' yes = kbm.findValue(headache, "Yes");
	 * assertNotNull("Value 'Yes' for Question 'Headache' isn't " +
	 * "contained in the knowledge base", yes); no = kbm.findValue(headache,
	 * "No"); assertNotNull("Value 'No' for Question 'Headache' isn't " +
	 * "contained in the knowledge base", no);
	 * 
	 * // Question 'Circulatory Problems' Question circu =
	 * kbm.findQuestion("Circulatory Problems");
	 * assertNotNull("Question 'Circulatory Problems' isn't contained " +
	 * "in the knowledge base.", circu);
	 * 
	 * // Values of 'Circulatory Problems' yes = kbm.findValue(circu, "Yes");
	 * assertNotNull("Value 'Yes' for Question 'Circulatory Problems' isn't " +
	 * "contained in the knowledge base", yes); no = kbm.findValue(circu, "No");
	 * assertNotNull("Value 'No' for Question 'Circulatory Problems' isn't " +
	 * "contained in the knowledge base", no); }
	 */

	@Test
	public void testIndication() {

		// TODO
	}

	@Test
	public void testContraIndication() {

		// TODO
	}

	@Test
	public void testInstantIndication() {

		// TODO
	}
}
