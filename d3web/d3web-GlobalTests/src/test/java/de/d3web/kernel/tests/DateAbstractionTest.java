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

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.BeforeClass;
import org.junit.Test;

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondDateAfter;
import de.d3web.core.inference.condition.CondDateBefore;
import de.d3web.core.inference.condition.CondDateEqual;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.test.InitPluginManager;

/**
 * This test is designed to control the setting of abstract choice questions'
 * values based on entered dates
 * 
 * The tested knowledgebase contains the following terminology objects:
 * 
 * <b>Questions</b> Date [date] Is Germany separated? [oc] <abstract> - Yes - No
 * Event [oc] <abstract> - Fall of the Berlin Wall - German unity
 * 
 * The problem solving is based on the following <b>Rules</b>:
 * 
 * Date = 1989-11-09 => Event = Fall of the Berlin Wall Date = 1990-10-03 =>
 * Event = German unity Date > 1949-10-07 AND Date < 1990-10-03 => Germany is
 * separated = Yes Date < 1949-10-07 OR Date > 1990-10-03 => Germany is
 * separated = No
 * 
 * 
 * @author Sebastian Furth
 * 
 */
public class DateAbstractionTest {

	private static KnowledgeBase kb;
	private static Session session;

	@BeforeClass
	public static void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseManagement.createInstance().getKnowledgeBase();
		addTerminologyObjects();
		addRules();
		session = SessionFactory.createSession(kb);
	}

	private static void addTerminologyObjects() {
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance(kb);
		kbm.createQuestionDate("Date", kb.getRootQASet());

		String[] separationAlternatives = {
				"Yes", "No" };
		Question separation =
				kbm.createQuestionOC("Is Germany separated?",
						kb.getRootQASet(), separationAlternatives);
		separation.getInfoStore().addValue(BasicProperties.ABSTRACTION_QUESTION, Boolean.TRUE);

		String[] eventAlternatives = {
				"Fall of the Berlin Wall", "German unity" };
		Question event = kbm.createQuestionOC("Event", kb.getRootQASet(),
				eventAlternatives);
		event.getInfoStore().addValue(BasicProperties.ABSTRACTION_QUESTION, Boolean.TRUE);
	}

	private static void addRules() {

		Calendar calendar = GregorianCalendar.getInstance();
		QuestionDate date = (QuestionDate) kb.getManager().searchQuestion("Date");
		Question event = kb.getManager().searchQuestion("Event");
		Question separation = kb.getManager().searchQuestion("Is Germany separated?");

		// Date = 1989-11-09 => Event = Fall of the Berlin Wall
		calendar.clear();
		calendar.set(1989, Calendar.NOVEMBER, 9, 0, 0, 0);
		DateValue fallOfTheWallDate = new DateValue(calendar.getTime());
		Condition fallOfTheWallCondition = new CondDateEqual(date, fallOfTheWallDate);
		Value fallOfTheWall = KnowledgeBaseManagement.findValue(event, "Fall of the Berlin Wall");
		RuleFactory.createSetValueRule(event, fallOfTheWall,
				fallOfTheWallCondition);

		// Date = 1990-10-03 => Event = German unity
		calendar.clear();
		calendar.set(1990, Calendar.OCTOBER, 3, 0, 0, 0);
		DateValue germanUnityDate = new DateValue(calendar.getTime());
		Condition germanUnityCondition = new CondDateEqual(date, germanUnityDate);
		Value germanUnity = KnowledgeBaseManagement.findValue(event, "German unity");
		RuleFactory.createSetValueRule(event, germanUnity, germanUnityCondition);

		// Date > 1949-10-07 AND Date < 1990-10-03 => Germany is separated = Yes
		calendar.clear();
		calendar.set(1949, Calendar.OCTOBER, 7, 0, 0, 0);
		DateValue beginSeparationDate = new DateValue(calendar.getTime());
		Condition beginSeparationCondition = new CondDateAfter(date, beginSeparationDate);

		calendar.clear();
		calendar.set(1990, Calendar.OCTOBER, 3, 0, 0, 0);
		DateValue endSeparationDate = new DateValue(calendar.getTime());
		Condition endSeparationCondition = new CondDateBefore(date, endSeparationDate);

		Condition separationAndCondition = new CondAnd(Arrays.asList(beginSeparationCondition,
				endSeparationCondition));
		Value yes = KnowledgeBaseManagement.findValue(separation, "Yes");
		RuleFactory.createSetValueRule(separation, yes, separationAndCondition);

		// Date < 1949-10-07 OR Date > 1990-10-03 => Germany is separated = No
		Condition preSeparationCondition = new CondDateBefore(date, beginSeparationDate);
		Condition postSeparationCondition = new CondDateAfter(date, endSeparationDate);
		Condition unityOrCondition = new CondOr(Arrays.asList(preSeparationCondition,
				postSeparationCondition));
		Value no = KnowledgeBaseManagement.findValue(separation, "No");
		RuleFactory.createSetValueRule(separation, no, unityOrCondition);
	}

	@Test
	public void testTerminlogyObjectExistence() {

		// Question 'Date'
		Question date = kb.getManager().searchQuestion("Date");
		assertNotNull("Question 'Date' isn't in the Knowledgebase.", date);

		// Question 'Is Germany separated?'
		Question separation = kb.getManager().searchQuestion("Is Germany separated?");
		assertNotNull("Question 'Is Germany separated?' isn't in the Knowledgebase.", separation);

		// Values of 'Is Germany separated?'
		Value yes = KnowledgeBaseManagement.findValue(separation, "Yes");
		assertNotNull("Value 'Yes' of Question 'Is Germany separated?' isn't in the Knowledgebase",
				yes);
		Value no = KnowledgeBaseManagement.findValue(separation, "No");
		assertNotNull("Value 'No' of Question 'Is Germany separated?' isn't in the Knowledgebase",
				no);

		// Question 'Event'
		Question event = kb.getManager().searchQuestion("Event");
		assertNotNull("Question 'Event' isn't in the Knowledgebase.", event);

		// Values of 'Event'
		Value fallOfTheWall = KnowledgeBaseManagement.findValue(event, "Fall of the Berlin Wall");
		assertNotNull(
				"Value 'Fall of the Berlin Wall' of Question 'Event' isn't in the Knowledgebase",
				fallOfTheWall);
		Value germanUnity = KnowledgeBaseManagement.findValue(event, "German unity");
		assertNotNull("Value 'German Unity' of Question 'Event' isn't in the Knowledgebase",
				germanUnity);
	}

	@Test
	public void testAbstractionProperty() {

		// 'Is Germany separated?' <abstract> ?
		Question separation = kb.getManager().searchQuestion("Is Germany separated?");
		Boolean separationAbstractionProperty = separation.getInfoStore().getValue(
				BasicProperties.ABSTRACTION_QUESTION);
		assertEquals("Question 'Is Germany separated?' isn't abstract.", Boolean.TRUE,
				separationAbstractionProperty);

		// Event <abstract> ?
		Question event = kb.getManager().searchQuestion("Event");
		Boolean eventAbstractionProperty = event.getInfoStore().getValue(
				BasicProperties.ABSTRACTION_QUESTION);
		assertEquals("Question 'BMI' isn't abstract.", Boolean.TRUE, eventAbstractionProperty);
	}

	@Test
	public void testCondDateAfter() {
		Calendar calendar = GregorianCalendar.getInstance();
		Question date = kb.getManager().searchQuestion("Date");
		Question separation = kb.getManager().searchQuestion("Is Germany separated?");

		// SET 'Date' = 1960-01-01
		calendar.clear();
		calendar.set(1960, Calendar.JANUARY, 1, 0, 0, 0);
		DateValue duringSeparationDate = new DateValue(calendar.getTime());
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, date,
						duringSeparationDate, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));

		// TEST 'Date' == 1960-01-01
		Value currentDateValue = session.getBlackboard().getValue(date);
		assertEquals("Question 'Date' has wrong value", duringSeparationDate, currentDateValue);

		// TEST 'Germany is separated?' == 'Yes'
		Value currentSeparationValue = session.getBlackboard().getValue(separation);
		Value yes = KnowledgeBaseManagement.findValue(separation, "Yes");
		assertEquals("Question 'Is Germany separated?' has wrong value", yes,
				currentSeparationValue);
	}

	@Test
	public void testCondDateBefore() {
		Calendar calendar = GregorianCalendar.getInstance();
		Question date = kb.getManager().searchQuestion("Date");
		Question separation = kb.getManager().searchQuestion("Is Germany separated?");

		// SET 'Date' = 1900-01-01
		calendar.clear();
		calendar.set(1900, Calendar.JANUARY, 1, 0, 0, 0);
		DateValue preSeparationDate = new DateValue(calendar.getTime());
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, date,
						preSeparationDate, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));

		// TEST 'Date' == 1900-01-01
		Value currentDateValue = session.getBlackboard().getValue(date);
		assertEquals("Question 'Date' has wrong value", preSeparationDate, currentDateValue);

		// TEST 'Germany is separated?' == 'No'
		Value currentSeparationValue = session.getBlackboard().getValue(separation);
		Value no = KnowledgeBaseManagement.findValue(separation, "No");
		assertEquals("Question 'Is Germany separated?' has wrong value", no, currentSeparationValue);
	}

	@Test
	public void testSetAndChangeValue() {

		Calendar calendar = GregorianCalendar.getInstance();
		Question date = kb.getManager().searchQuestion("Date");
		Question event = kb.getManager().searchQuestion("Event");

		// SET 'Date' = 1989-11-09
		calendar.clear();
		calendar.set(1989, Calendar.NOVEMBER, 9, 0, 0, 0);
		DateValue fallOfTheWallDate = new DateValue(calendar.getTime());
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, date,
						fallOfTheWallDate, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));

		// TEST 'Date' == 1989-11-09
		Value currentDateValue = session.getBlackboard().getValue(date);
		assertEquals("Question 'Date' has wrong value", fallOfTheWallDate, currentDateValue);

		// TEST 'Event' == 'Fall of the Berlin Wall'
		Value currentEventValue = session.getBlackboard().getValue(event);
		Value fallOfTheWallValue = KnowledgeBaseManagement.findValue(event,
				"Fall of the Berlin Wall");
		assertEquals("Question 'Event' has wrong value", fallOfTheWallValue, currentEventValue);

		// SET 'Date' = 1990-10-03
		calendar.clear();
		calendar.set(1990, Calendar.OCTOBER, 3, 0, 0, 0);
		DateValue germanUnityDate = new DateValue(calendar.getTime());
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, date,
						germanUnityDate, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));

		// TEST 'Date' == 1990-10-03
		currentDateValue = session.getBlackboard().getValue(date);
		assertEquals("Question 'Date' has wrong value", germanUnityDate, currentDateValue);

		// TEST 'Event' == 'German Unity'
		currentEventValue = session.getBlackboard().getValue(event);
		Value germanUnityValue = KnowledgeBaseManagement.findValue(event, "German unity");
		assertEquals("Question 'Event' has wrong value", germanUnityValue, currentEventValue);
	}

	@Test
	public void testSetUndefinedValue() {

		Question date = kb.getManager().searchQuestion("Date");
		Question event = kb.getManager().searchQuestion("Event");

		// SET 'Date' = 'UNDEFINED'
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, date,
						UndefinedValue.getInstance(), PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));

		// TEST 'Date' == 'UNDEFINED'
		Value currentDateValue = session.getBlackboard().getValue(date);
		assertEquals("Question 'Date' has wrong value", UndefinedValue.getInstance(),
				currentDateValue);

		// TEST 'Event' == 'UNDEFINED'
		Value currentEventValue = session.getBlackboard().getValue(event);
		assertEquals("Question 'Event' has wrong value", UndefinedValue.getInstance(),
				currentEventValue);

	}

}
