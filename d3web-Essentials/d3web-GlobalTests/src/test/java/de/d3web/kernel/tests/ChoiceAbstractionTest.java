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

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import com.denkbares.plugin.test.InitPluginManager;

/**
 * This test is designed to control the setting of abstract choice questions'
 * values
 * 
 * The tested knowledge base contains the following terminology objects:
 * 
 * <b>Questions</b> Weekday [oc] - Monday - Tuesday - Wednesday - Thursday -
 * Friday - Saturday - Sunday Day [oc] <abstract> - Workday - Weekend
 * 
 * The problem solving is based on the following <b>Rules</b>:
 * 
 * Weekday = Monday => Day = Workday Weekday = Tuesday => Day = Workday Weekday
 * = Wednesday => Day = Workday Weekday = Thursday => Day = Workday Weekday =
 * Friday => Day = Workday Weekday = Saturday => Day = Weekend Weekday = Sunday
 * => Day = Weekend
 * 
 * 
 * @author Sebastian Furth
 * 
 */
public class ChoiceAbstractionTest {

	private static KnowledgeBase kb;
	private static Session session;

	@BeforeClass
	public static void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		addTerminologyObjects();
		addRules();
		session = SessionFactory.createSession(kb);
	}

	private static void addTerminologyObjects() {

		// Question 'Weekday'
		String weekday = "Weekday";
		String[] weekdayAlternatives = new String[] {
				"Monday", "Tuesday", "Wednesday", "Thursday",
				"Friday", "Saturday", "Sunday" };
		new QuestionOC(kb.getRootQASet(), weekday, weekdayAlternatives);

		// Question 'Day'
		String day = "Day";
		String[] dayAlternatives = new String[] {
				"Workday", "Weekend" };
		Question questionDay = new QuestionOC(kb.getRootQASet(), day,
				dayAlternatives);
		questionDay.getInfoStore().addValue(BasicProperties.ABSTRACTION_QUESTION, Boolean.TRUE);
	}

	private static void addRules() {

		Question day = kb.getManager().searchQuestion("Day");
		Value workday = KnowledgeBaseUtils.findValue(day, "Workday");
		Value weekend = KnowledgeBaseUtils.findValue(day, "Weekend");

		Question weekday = kb.getManager().searchQuestion("Weekday");

		// Weekday = Monday => Day = Workday
		Value monday = KnowledgeBaseUtils.findValue(weekday, "Monday");
		Condition condition = new CondEqual(weekday, monday);
		RuleFactory.createSetValueRule(day, workday, condition);

		// Weekday = Tuesday => Day = Workday
		Value tuesday = KnowledgeBaseUtils.findValue(weekday, "Tuesday");
		condition = new CondEqual(weekday, tuesday);
		RuleFactory.createSetValueRule(day, workday, condition);

		// Weekday = Wednesday => Day = Workday
		Value wednesday = KnowledgeBaseUtils.findValue(weekday, "Wednesday");
		condition = new CondEqual(weekday, wednesday);
		RuleFactory.createSetValueRule(day, workday, condition);

		// Weekday = Thursday => Day = Workday
		Value thursday = KnowledgeBaseUtils.findValue(weekday, "Thursday");
		condition = new CondEqual(weekday, thursday);
		RuleFactory.createSetValueRule(day, workday, condition);

		// Weekday = Friday => Day = Workday
		Value friday = KnowledgeBaseUtils.findValue(weekday, "Friday");
		condition = new CondEqual(weekday, friday);
		RuleFactory.createSetValueRule(day, workday, condition);

		// Weekday = Saturday => Day = Weekend
		Value saturday = KnowledgeBaseUtils.findValue(weekday, "Saturday");
		condition = new CondEqual(weekday, saturday);
		RuleFactory.createSetValueRule(day, weekend, condition);

		// Weekday = Sunday => Day = Weekend
		Value sunday = KnowledgeBaseUtils.findValue(weekday, "Sunday");
		condition = new CondEqual(weekday, sunday);
		RuleFactory.createSetValueRule(day, weekend, condition);
	}

	@Test
	public void testTerminlogyObjectExistence() {

		// Question 'Weekday'
		Question weekday = kb.getManager().searchQuestion("Weekday");
		assertNotNull("Question 'Weekday' isn't in the Knowledgebase.", weekday);

		// Values of 'Weekday'
		Value monday = KnowledgeBaseUtils.findValue(weekday, "Monday");
		assertNotNull("Value 'Monday' for Question 'Weekday' isn't in the Knowledgebase", monday);
		Value tuesday = KnowledgeBaseUtils.findValue(weekday, "Tuesday");
		assertNotNull("Value 'Tuesday' for Question 'Weekday' isn't in the Knowledgebase", tuesday);
		Value wednesday = KnowledgeBaseUtils.findValue(weekday, "Wednesday");
		assertNotNull("Value 'Wednesday' for Question 'Weekday' isn't in the Knowledgebase",
				wednesday);
		Value thursday = KnowledgeBaseUtils.findValue(weekday, "Thursday");
		assertNotNull("Value 'Thursday' for Question 'Weekday' isn't in the Knowledgebase",
				thursday);
		Value friday = KnowledgeBaseUtils.findValue(weekday, "Friday");
		assertNotNull("Value 'Friday' for Question 'Weekday' isn't in the Knowledgebase", friday);
		Value saturday = KnowledgeBaseUtils.findValue(weekday, "Saturday");
		assertNotNull("Value 'Saturday' for Question 'Weekday' isn't in the Knowledgebase",
				saturday);
		Value sunday = KnowledgeBaseUtils.findValue(weekday, "Sunday");
		assertNotNull("Value 'Sunday' for Question 'Weekday' isn't in the Knowledgebase", sunday);

		// Question 'Day'
		Question day = kb.getManager().searchQuestion("Day");
		assertNotNull("Question 'Day' isn't in the Knowledgebase.", day);

		// Values of 'Day'
		Value workday = KnowledgeBaseUtils.findValue(day, "Workday");
		assertNotNull("Value 'Workday' for Question 'Day' isn't in the Knowledgebase", workday);
		Value weekend = KnowledgeBaseUtils.findValue(day, "Weekend");
		assertNotNull("Value 'Weekend' for Question 'Day' isn't in the Knowledgebase", weekend);
	}

	@Test
	public void testAbstractionProperty() {

		// TEST 'Day' <abstract> ?
		Question day = kb.getManager().searchQuestion("Day");
		Boolean abstractionProperty = day.getInfoStore().getValue(
				BasicProperties.ABSTRACTION_QUESTION);
		assertEquals("Question 'Day' isn't abstract.", Boolean.TRUE, abstractionProperty);
	}

	@Test
	public void testSetAndChangeValue() {

		Question weekday = kb.getManager().searchQuestion("Weekday");
		Question day = kb.getManager().searchQuestion("Day");

		// SET 'Weekday' = 'Monday'
		Value monday = KnowledgeBaseUtils.findValue(weekday, "Monday");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(weekday, monday,
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		// TEST 'Weekday' == 'Monday'
		Value weekdayValue = session.getBlackboard().getValue(weekday);
		assertEquals("Question 'Weekday' has wrong value", monday, weekdayValue);

		// TEST 'Day' == 'Workday'
		Value workday = KnowledgeBaseUtils.findValue(day, "Workday");
		Value dayValue = session.getBlackboard().getValue(day);
		assertEquals("Abstract question 'Day' has wrong value", workday, dayValue);

		// SET 'Weekday' = 'Saturday'
		Value saturday = KnowledgeBaseUtils.findValue(weekday, "Saturday");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(weekday, saturday,
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		// TEST 'Weekday' == 'Saturday'
		weekdayValue = session.getBlackboard().getValue(weekday);
		assertEquals("Question 'Weekday' has wrong value", saturday, weekdayValue);

		// TEST 'Day' == 'Weekend'
		Value weekend = KnowledgeBaseUtils.findValue(day, "Weekend");
		dayValue = session.getBlackboard().getValue(day);
		assertEquals("Abstract question 'Day' has wrong value", weekend, dayValue);
	}

	@Test
	public void testSetUndefinedValue() {

		Question weekday = kb.getManager().searchQuestion("Weekday");
		Question day = kb.getManager().searchQuestion("Day");

		// SET 'Weekday' = 'UNDEFINED'
		session.getBlackboard().addValueFact(
				FactFactory.createFact(weekday,
						UndefinedValue.getInstance(), PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));

		// TEST 'Weekday' == 'UNDEFINED'
		Value weekdayValue = session.getBlackboard().getValue(weekday);
		assertEquals("Question 'Weekday' has wrong value", UndefinedValue.getInstance(),
				weekdayValue);

		// TEST 'Day' == 'UNDEFINED'
		Value dayValue = session.getBlackboard().getValue(day);
		assertEquals("Abstract question 'Day' has wrong value", UndefinedValue.getInstance(),
				dayValue);
	}

}
