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
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.Choice;
import de.d3web.core.session.values.ChoiceValue;
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

		// Question 'Day'
		String day = "Day";
		String[] dayAlternatives = new String[] {
				"Workday", "Weekend" };
		kbm.createQuestionMC(day, kbm.getKnowledgeBase().getRootQASet(), dayAlternatives);

		// Question 'Weekday'
		String weekday = "Weekday";
		String[] weekdayAlternatives = new String[] {
				"Monday", "Tuesday", "Wednesday", "Thursday",
				"Friday", "Saturday", "Sunday" };
		Question weekdayQuestion =
				kbm.createQuestionMC(weekday, kbm.getKnowledgeBase().getRootQASet(),
				weekdayAlternatives);
		weekdayQuestion.getProperties().setProperty(Property.ABSTRACTION_QUESTION, Boolean.TRUE);
	}

	private static void addRules() {

		Question weekday = kbm.findQuestion("Weekday");

		// Day = Workday => Weekday = Monday, Tuesday, Wednesday, Thursday,
		// Friday
		Question day = kbm.findQuestion("Day");
		Value workday = kbm.findValue(day, "Workday");
		Condition condition = new CondEqual(day, workday);
		List<String> ignoredValues = Arrays.asList("Saturday", "Sunday");
		RuleFactory.createSetValueRule(kbm.createRuleID(), weekday,
				getWeekdayMCValue(ignoredValues), condition);

		// Day = Weekend => Weekday = Saturday, Sunday
		Value weekend = kbm.findValue(day, "Weekend");
		condition = new CondEqual(day, weekend);
		ignoredValues = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
		RuleFactory.createSetValueRule(kbm.createRuleID(), weekday,
				getWeekdayMCValue(ignoredValues), condition);

		// Day = Workday, Weekend => Weekday = Monday, Tuesday, Wednesday,
		// Thursday, Friday, Saturday, Sunday
		MultipleChoiceValue weekendAndWorkday = getWeekdayMCValue(Arrays.asList(""));
		condition = new CondEqual(day, weekendAndWorkday);
		RuleFactory.createSetValueRule(kbm.createRuleID(), weekday, getWeekdayMCValue(null),
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
	private static MultipleChoiceValue getWeekdayMCValue(List<String> ignoredValues) {
		QuestionChoice weekday = (QuestionChoice) kbm.findQuestion("Weekday");
		List<ChoiceValue> weekdayValues = new ArrayList<ChoiceValue>();
		for (Choice c : weekday.getAllAlternatives()) {
			if (ignoredValues == null || !ignoredValues.contains(c.getName())) weekdayValues.add(new ChoiceValue(
					c));
		}
		return new MultipleChoiceValue(weekdayValues);
	}

	@Test
	public void testTerminlogyObjectExistence() {

		// Question 'Day'
		Question day = kbm.findQuestion("Day");
		assertNotNull("Question 'Day' isn't in the Knowledgebase.", day);

		// Values of 'Day'
		Value workday = kbm.findValue(day, "Workday");
		assertNotNull("Value 'Workday' for Question 'Day' isn't in the Knowledgebase", workday);
		Value weekend = kbm.findValue(day, "Weekend");
		assertNotNull("Value 'Weekend' for Question 'Day' isn't in the Knowledgebase", weekend);

		// Question 'Weekday'
		Question weekday = kbm.findQuestion("Weekday");
		assertNotNull("Question 'Weekday' isn't in the Knowledgebase.", weekday);

		// Values of 'Weekday'
		Value monday = kbm.findValue(weekday, "Monday");
		assertNotNull("Value 'Monday' for Question 'Weekday' isn't in the Knowledgebase", monday);
		Value tuesday = kbm.findValue(weekday, "Tuesday");
		assertNotNull("Value 'Tuesday' for Question 'Weekday' isn't in the Knowledgebase", tuesday);
		Value wednesday = kbm.findValue(weekday, "Wednesday");
		assertNotNull("Value 'Wednesday' for Question 'Weekday' isn't in the Knowledgebase",
				wednesday);
		Value thursday = kbm.findValue(weekday, "Thursday");
		assertNotNull("Value 'Thursday' for Question 'Weekday' isn't in the Knowledgebase",
				thursday);
		Value friday = kbm.findValue(weekday, "Friday");
		assertNotNull("Value 'Friday' for Question 'Weekday' isn't in the Knowledgebase", friday);
		Value saturday = kbm.findValue(weekday, "Saturday");
		assertNotNull("Value 'Saturday' for Question 'Weekday' isn't in the Knowledgebase",
				saturday);
		Value sunday = kbm.findValue(weekday, "Sunday");
		assertNotNull("Value 'Sunday' for Question 'Weekday' isn't in the Knowledgebase", sunday);
	}

	@Test
	public void testAbstractionProperty() {

		// TEST Weekday <abstract> ?
		Question weekday = kbm.findQuestion("Weekday");
		Boolean abstractionProperty = (Boolean) weekday.getProperties().getProperty(
				Property.ABSTRACTION_QUESTION);
		assertEquals("Question 'Day' isn't abstract.", Boolean.TRUE, abstractionProperty);
	}

	@Test
	public void testSetAndChangeOneValue() {

		Question day = kbm.findQuestion("Day");
		Question weekday = kbm.findQuestion("Weekday");

		// SET 'Day' = 'Workday'
		Value workday = kbm.findValue(day, "Workday");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(day, workday,
				PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		// TEST 'Day' == 'Workday'
		Value dayValue = session.getValue(day);
		assertEquals("Question 'Day' has wrong value", workday, dayValue);

		// TEST 'Weekday' == 'Monday, Tuesday, Wednesday, Thursday, Friday'
		Value weekdayValue = session.getValue(weekday);
		List<String> ignoredValues = Arrays.asList("Saturday", "Sunday");
		MultipleChoiceValue workdayValues = getWeekdayMCValue(ignoredValues);
		assertEquals("Abstract question 'Weekday' has wrong value", workdayValues, weekdayValue);

		// SET 'Day' = 'Weekend'
		Value weekend = kbm.findValue(day, "Weekend");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(day, weekend,
				PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		// TEST 'Day' == 'Weekend'
		dayValue = session.getValue(day);
		assertEquals("Question 'Day' has wrong value", weekend, dayValue);

		// TEST 'Weekday' == 'Saturday, Sunday'
		weekdayValue = session.getValue(weekday);
		ignoredValues = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
		MultipleChoiceValue weekendValues = getWeekdayMCValue(ignoredValues);
		assertEquals("Abstract question 'Weekday' has wrong value", weekendValues, weekdayValue);
	}

	@Test
	public void testSetMultipleChoiceValue() {

		Question day = kbm.findQuestion("Day");
		Question weekday = kbm.findQuestion("Weekday");

		// SET 'Day' = 'Weekend, Workday'
		session.getBlackboard().addValueFact(
				FactFactory.createFact(day, getWeekdayMCValue(Arrays.asList("")),
				PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		// TEST 'Day' == 'Weekend, Workday'
		Value dayValue = session.getValue(day);
		assertEquals("Question 'Day' has wrong value", getWeekdayMCValue(Arrays.asList("")),
				dayValue);

		// TEST 'Weekday' == 'Monday, Tuesday, Wednesday, Thursday, Friday,
		// Saturday, Sunday'
		Value weekdayValue = session.getValue(weekday);
		MultipleChoiceValue completeWeekValues = getWeekdayMCValue(null);
		assertEquals("Abstract question 'Weekday' has wrong value", completeWeekValues,
				weekdayValue);
	}

	@Test
	public void testSetUndefinedValue() {

		Question day = kbm.findQuestion("Day");
		Question weekday = kbm.findQuestion("Weekday");

		// SET 'Day' = 'UNDEFINED'
		session.getBlackboard().addValueFact(
				FactFactory.createFact(day, UndefinedValue.getInstance(),
				PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		// TEST 'Day' == 'UNDEFINED'
		Value dayValue = session.getValue(day);
		assertEquals("Question 'Day' has wrong value", UndefinedValue.getInstance(), dayValue);

		// TEST 'Weekday' == 'UNDEFINED' ?
		Value weekdayValue = session.getValue(weekday);
		assertEquals("Abstract question 'Day' has wrong value", UndefinedValue.getInstance(),
				weekdayValue);
	}

}
