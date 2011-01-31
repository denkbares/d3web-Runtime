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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import de.d3web.core.inference.condition.CondTextEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.test.InitPluginManager;

/**
 * This test is designed to control the setting of abstract choice questions'
 * values based on text questions' values
 * 
 * The tested knowledgebase contains the following terminology objects:
 * 
 * <b>Questions</b> Emoticon [text] Feeling [oc] <abstract> - Happiness -
 * Sadness
 * 
 * The problem solving is based on the following <b>Rules</b>:
 * 
 * Emoticon = ":-)" => Feeling = Happiness Emoticon = ":-(" => Feeling = Sadness
 * 
 * 
 * @author Sebastian Furth
 * 
 */
public class TextAbstractionTest {

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

	private static void addTerminologyObjects() {

		kbm.createQuestionText("Emoticon", kbm.getKnowledgeBase().getRootQASet());

		String[] eventAlternatives = {
				"Happiness", "Sadness" };
		Question event = kbm.createQuestionOC("Feeling", kbm.getKnowledgeBase().getRootQASet(),
				eventAlternatives);
		event.getInfoStore().addValue(BasicProperties.ABSTRACTION_QUESTION, Boolean.TRUE);
	}

	private static void addRules() {

		QuestionText emoticon = (QuestionText) kbm.findQuestion("Emoticon");
		Question feeling = kbm.findQuestion("Feeling");

		// Emoticon = ":-)" => Feeling = Happiness
		Condition happinessCondition = new CondTextEqual(emoticon, ":-)");
		Value happiness = kbm.findValue(feeling, "Happiness");
		RuleFactory.createSetValueRule(feeling, happiness, happinessCondition);

		// Emoticon = ":-(" => Feeling = Sadness
		Condition sadnessCondition = new CondTextEqual(emoticon, ":-(");
		Value sadness = kbm.findValue(feeling, "Sadness");
		RuleFactory.createSetValueRule(feeling, sadness, sadnessCondition);
	}

	@Test
	public void testTerminlogyObjectExistence() {

		// Question 'Emoticon'
		Question emoticon = kbm.findQuestion("Emoticon");
		assertNotNull("Question 'Emoticon' isn't in the Knowledgebase.", emoticon);

		// Question 'Feeling'
		Question feeling = kbm.findQuestion("Feeling");
		assertNotNull("Question 'Feeling' isn't in the Knowledgebase.", feeling);

		// Values of 'Feeling'
		Value happiness = kbm.findValue(feeling, "Happiness");
		assertNotNull("Value 'Happiness' of Question 'Feeling' isn't in the Knowledgebase",
				happiness);
		Value sadness = kbm.findValue(feeling, "Sadness");
		assertNotNull("Value 'Sadness' of Question 'Feeling' isn't in the Knowledgebase", sadness);
	}

	@Test
	public void testAbstractionProperty() {

		// Feeling <abstract> ?
		Question feeling = kbm.findQuestion("Feeling");
		Boolean feelingAbstractionProperty = feeling.getInfoStore().getValue(
				BasicProperties.ABSTRACTION_QUESTION);
		assertEquals("Question 'BMI' isn't abstract.", Boolean.TRUE, feelingAbstractionProperty);
	}

	@Test
	public void testSetAndChangeValue() {

		Question emoticon = kbm.findQuestion("Emoticon");
		Question feeling = kbm.findQuestion("Feeling");

		// SET 'Emoticon' = ':-)'
		TextValue happinessIcon = new TextValue(":-)");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, emoticon,
						happinessIcon, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));

		// TEST 'Emoticon' == ':-)'
		Value currentEmoticonValue = session.getBlackboard().getValue(emoticon);
		assertEquals("Question 'Emoticon' has wrong value", happinessIcon, currentEmoticonValue);

		// TEST 'Feeling' == 'Happiness'
		Value currentFeelingValue = session.getBlackboard().getValue(feeling);
		Value happiness = kbm.findValue(feeling, "Happiness");
		assertEquals("Question 'Feeling' has wrong value", happiness, currentFeelingValue);

		// SET 'Emoticon' = ':-('
		TextValue sadnessIcon = new TextValue(":-(");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, emoticon,
						sadnessIcon, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));

		// TEST 'Emoticon' == ':-('
		currentEmoticonValue = session.getBlackboard().getValue(emoticon);
		assertEquals("Question 'Emoticon' has wrong value", sadnessIcon, currentEmoticonValue);

		// TEST 'Feeling' == 'Sadness'
		currentFeelingValue = session.getBlackboard().getValue(feeling);
		Value sadness = kbm.findValue(feeling, "Sadness");
		assertEquals("Question 'Feeling' has wrong value", sadness, currentFeelingValue);
	}

	@Test
	public void testSetUndefinedValue() {

		Question emoticon = kbm.findQuestion("Emoticon");
		Question feeling = kbm.findQuestion("Feeling");

		// SET 'Emoticon' = 'UNDEFINED'
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, emoticon,
						UndefinedValue.getInstance(), PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));

		// TEST 'Emoticon' == 'UNDEFINED'
		Value currentEmoticonValue = session.getBlackboard().getValue(emoticon);
		assertEquals("Question 'Emoticon' has wrong value", UndefinedValue.getInstance(),
				currentEmoticonValue);

		// TEST 'Feeling' == 'UNDEFINED'
		Value currentFeelingValue = session.getBlackboard().getValue(feeling);
		assertEquals("Question 'Feeling' has wrong value", UndefinedValue.getInstance(),
				currentFeelingValue);

	}

}
