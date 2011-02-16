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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.test.InitPluginManager;

/**
 * This test is designed to control the setting of abstract multiplechoice
 * questions' values
 * 
 * The tested knowledgebase contains the following terminology objects:
 * 
 * <b>Questions</b> Day [mc] - Workday - Weekend Weekday [mc] <abstract> -
 * Monday - Tuesday - Wednesday - Thursday - Friday - Saturday - Sunday
 * 
 * The problem solving is based on the following <b>Rules</b>:
 * 
 * Day = Workday => Weekday = Monday, Tuesday, Wednesday, Thursday, Friday Day =
 * Weekend => Weekday = Saturday, Sunday Day = Workday, Weekend => Weekday =
 * Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday,
 * 
 * 
 * @author Sebastian Furth
 * 
 */
public class MultipleChoiceAbstractionTest {

	private static KnowledgeBase kb;
	private static Session session;

	@BeforeClass
	public static void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseManagement.createKnowledgeBase();
		addTerminologyObjects();
		addRules();
		session = SessionFactory.createSession(kb);
	}

	private static void addTerminologyObjects() {
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance(kb);
		// Question 'Day'
		String day = "Day";
		String[] dayAlternatives = new String[] {
				"Workday", "Weekend" };
		kbm.createQuestionMC(day, kb.getRootQASet(), dayAlternatives);

		// Question 'Weekday'
		String weekday = "Weekday";
		String[] weekdayAlternatives = new String[] {
				"Monday", "Tuesday", "Wednesday", "Thursday",
				"Friday", "Saturday", "Sunday" };
		Question weekdayQuestion =
				kbm.createQuestionMC(weekday, kb.getRootQASet(),
						weekdayAlternatives);
		weekdayQuestion.getInfoStore().addValue(BasicProperties.ABSTRACTION_QUESTION, Boolean.TRUE);
	}

	private static void addRules() {

		Question weekday = kb.getManager().searchQuestion("Weekday");

		// Day = Workday => Weekday = Monday, Tuesday, Wednesday, Thursday,
		// Friday
		Question day = kb.getManager().searchQuestion("Day");
		Value workday = KnowledgeBaseManagement.findValue(day, "Workday");
		Condition condition = new CondEqual(day, workday);
		List<String> ignoredValues = Arrays.asList("Saturday", "Sunday");
		RuleFactory.createSetValueRule(weekday,
				getWeekdayMCValue(ignoredValues), condition);

		// Day = Weekend => Weekday = Saturday, Sunday
		Value weekend = KnowledgeBaseManagement.findValue(day, "Weekend");
		condition = new CondEqual(day, weekend);
		ignoredValues = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
		RuleFactory.createSetValueRule(weekday,
				getWeekdayMCValue(ignoredValues), condition);

		// Day = Workday, Weekend => Weekday = Monday, Tuesday, Wednesday,
		// Thursday, Friday, Saturday, Sunday
		MultipleChoiceValue weekendAndWorkday = getDayMCValue(Arrays.asList(""));
		condition = new CondEqual(day, weekendAndWorkday);
		RuleFactory.createSetValueRule(weekday, getWeekdayMCValue(null),
				condition);
	}

	/**
	 * Returns a MultipleChoiceValue containing all Weekdays which are not in
	 * the committed List of ignored days.
	 * 
	 * @param ignoredValues the days which will NOT be added to the
	 *        MultipleChoiceValue
	 * @return a MultipleChoiceValue containing the desired weekdays
	 */
	private static MultipleChoiceValue getDayMCValue(List<String> ignoredValues) {
		QuestionChoice day = (QuestionChoice) kb.getManager().searchQuestion("Day");
		return getAllMCValue(day, ignoredValues);
	}

	private static MultipleChoiceValue getWeekdayMCValue(List<String> ignoredValues) {
		QuestionChoice weekday = (QuestionChoice) kb.getManager().searchQuestion("Weekday");
		return getAllMCValue(weekday, ignoredValues);
	}

	private static MultipleChoiceValue getAllMCValue(QuestionChoice question, List<String> ignoredValues) {
		List<Choice> weekdayValues = new ArrayList<Choice>();
		for (Choice c : question.getAllAlternatives()) {
			if (ignoredValues == null || !ignoredValues.contains(c.getName())) weekdayValues.add(c);
		}
		return MultipleChoiceValue.fromChoices(weekdayValues);
	}

	@Test
	public void testTerminlogyObjectExistence() {

		// Question 'Day'
		Question day = kb.getManager().searchQuestion("Day");
		assertNotNull("Question 'Day' isn't in the Knowledgebase.", day);

		// Values of 'Day'
		Value workday = KnowledgeBaseManagement.findValue(day, "Workday");
		assertNotNull("Value 'Workday' for Question 'Day' isn't in the Knowledgebase", workday);
		Value weekend = KnowledgeBaseManagement.findValue(day, "Weekend");
		assertNotNull("Value 'Weekend' for Question 'Day' isn't in the Knowledgebase", weekend);

		// Question 'Weekday'
		Question weekday = kb.getManager().searchQuestion("Weekday");
		assertNotNull("Question 'Weekday' isn't in the Knowledgebase.", weekday);

		// Values of 'Weekday'
		Value monday = KnowledgeBaseManagement.findValue(weekday, "Monday");
		assertNotNull("Value 'Monday' for Question 'Weekday' isn't in the Knowledgebase", monday);
		Value tuesday = KnowledgeBaseManagement.findValue(weekday, "Tuesday");
		assertNotNull("Value 'Tuesday' for Question 'Weekday' isn't in the Knowledgebase", tuesday);
		Value wednesday = KnowledgeBaseManagement.findValue(weekday, "Wednesday");
		assertNotNull("Value 'Wednesday' for Question 'Weekday' isn't in the Knowledgebase",
				wednesday);
		Value thursday = KnowledgeBaseManagement.findValue(weekday, "Thursday");
		assertNotNull("Value 'Thursday' for Question 'Weekday' isn't in the Knowledgebase",
				thursday);
		Value friday = KnowledgeBaseManagement.findValue(weekday, "Friday");
		assertNotNull("Value 'Friday' for Question 'Weekday' isn't in the Knowledgebase", friday);
		Value saturday = KnowledgeBaseManagement.findValue(weekday, "Saturday");
		assertNotNull("Value 'Saturday' for Question 'Weekday' isn't in the Knowledgebase",
				saturday);
		Value sunday = KnowledgeBaseManagement.findValue(weekday, "Sunday");
		assertNotNull("Value 'Sunday' for Question 'Weekday' isn't in the Knowledgebase", sunday);
	}

	@Test
	public void testAbstractionProperty() {

		// TEST Weekday <abstract> ?
		Question weekday = kb.getManager().searchQuestion("Weekday");
		Boolean abstractionProperty = weekday.getInfoStore().getValue(
				BasicProperties.ABSTRACTION_QUESTION);
		assertEquals("Question 'Day' isn't abstract.", Boolean.TRUE, abstractionProperty);
	}

	@Test
	public void testSetAndChangeOneValue() {

		Question day = kb.getManager().searchQuestion("Day");
		Question weekday = kb.getManager().searchQuestion("Weekday");

		// SET 'Day' = 'Workday'
		Value workday = KnowledgeBaseManagement.findValue(day, "Workday");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, day,
						workday, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));

		// TEST 'Day' == 'Workday'
		Value dayValue = session.getBlackboard().getValue(day);
		assertEquals("Question 'Day' has wrong value", workday, dayValue);

		// TEST 'Weekday' == 'Monday, Tuesday, Wednesday, Thursday, Friday'
		Value weekdayValue = session.getBlackboard().getValue(weekday);
		List<String> ignoredValues = Arrays.asList("Saturday", "Sunday");
		MultipleChoiceValue workdayValues = getWeekdayMCValue(ignoredValues);
		assertEquals("Abstract question 'Weekday' has wrong value", workdayValues, weekdayValue);

		// SET 'Day' = 'Weekend'
		Value weekend = KnowledgeBaseManagement.findValue(day, "Weekend");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, day,
						weekend, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));

		// TEST 'Day' == 'Weekend'
		dayValue = session.getBlackboard().getValue(day);
		assertEquals("Question 'Day' has wrong value", weekend, dayValue);

		// TEST 'Weekday' == 'Saturday, Sunday'
		weekdayValue = session.getBlackboard().getValue(weekday);
		ignoredValues = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
		MultipleChoiceValue weekendValues = getWeekdayMCValue(ignoredValues);
		assertEquals("Abstract question 'Weekday' has wrong value", weekendValues, weekdayValue);
	}

	@Test
	public void testSetMultipleChoiceValue() {

		Question day = kb.getManager().searchQuestion("Day");
		Question weekday = kb.getManager().searchQuestion("Weekday");

		// SET 'Day' = 'Weekend, Workday'
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, day,
						getDayMCValue(Arrays.asList("")), PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));

		// TEST 'Day' == 'Weekend, Workday'
		Value dayValue = session.getBlackboard().getValue(day);
		assertEquals("Question 'Day' has wrong value", getDayMCValue(Arrays.asList("")),
				dayValue);

		// TEST 'Weekday' == 'Monday, Tuesday, Wednesday, Thursday, Friday,
		// Saturday, Sunday'
		Value weekdayValue = session.getBlackboard().getValue(weekday);
		MultipleChoiceValue completeWeekValues = getWeekdayMCValue(null);
		assertEquals("Abstract question 'Weekday' has wrong value", completeWeekValues,
				weekdayValue);
	}

	@Test
	public void testSetUndefinedValue() {

		Question day = kb.getManager().searchQuestion("Day");
		Question weekday = kb.getManager().searchQuestion("Weekday");

		// SET 'Day' = 'UNDEFINED'
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, day,
						UndefinedValue.getInstance(), PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));

		// TEST 'Day' == 'UNDEFINED'
		Value dayValue = session.getBlackboard().getValue(day);
		assertEquals("Question 'Day' has wrong value", UndefinedValue.getInstance(), dayValue);

		// TEST 'Weekday' == 'UNDEFINED' ?
		Value weekdayValue = session.getBlackboard().getValue(weekday);
		assertEquals("Abstract question 'Day' has wrong value", UndefinedValue.getInstance(),
				weekdayValue);
	}

}
