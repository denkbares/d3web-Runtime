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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.BeforeClass;
import org.junit.Test;

import de.d3web.core.inference.condition.CondDateEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.plugin.test.InitPluginManager;

/**
 * This test is designed to control the setting
 * of abstract choice questions' values based on
 * entered dates
 * 
 * The tested knowledgebase contains the following terminology
 * objects:
 * 
 * <b>Questions</b>
 * Date [date]
 * Event [oc] <abstract>
 * - Fall of the Berlin Wall
 * - German unity
 * 
 * The problem solving is based on the following <b>Rules</b>:
 * 
 * Date = 1989-11-09 => Event = Fall of the Berlin Wall
 * Date = 1990-10-03 => Event = German unity
 * 
 * 
 * @author Sebastian Furth
 *
 */
public class DateAbstractionTest {

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
		
		kbm.createQuestionDate("Date", kbm.getKnowledgeBase().getRootQASet());
		
		String[] eventAlternatives = {"Fall of the Berlin Wall", "German unity"};
		Question event = kbm.createQuestionOC("Event", kbm.getKnowledgeBase().getRootQASet(), eventAlternatives);
		event.getProperties().setProperty(Property.ABSTRACTION_QUESTION, Boolean.TRUE);
	}
	
	private static void addRules() {
		
		Calendar calendar = GregorianCalendar.getInstance();
		QuestionDate date =  (QuestionDate) kbm.findQuestion("Date");
		Question event = kbm.findQuestion("Event");
		
		// Date = 1989-11-09 => Event = Fall of the Berlin Wall
		calendar.set(1989, Calendar.NOVEMBER, 9, 0, 0, 0);
		DateValue fallOfTheWallDate = new DateValue(calendar.getTime());
		Condition fallOfTheWallCondition = new CondDateEqual(date, fallOfTheWallDate);
		Value fallOfTheWall = kbm.findValue(event, "Fall of the Berlin Wall");
		RuleFactory.createSetValueRule(kbm.createRuleID(), event, fallOfTheWall, fallOfTheWallCondition);
		
		// Date = 1990-10-03 => Event = German unity
		calendar.set(1990, Calendar.OCTOBER, 3, 0, 0, 0);
		DateValue germanUnityDate = new DateValue(calendar.getTime());
		Condition germanUnityCondition = new CondDateEqual(date, germanUnityDate);
		Value germanUnity = kbm.findValue(event, "German unity");
		RuleFactory.createSetValueRule(kbm.createRuleID(), event, germanUnity, germanUnityCondition);
	}
	
	@Test
	public void testTerminlogyObjectExistence() {
		
		// Question 'Date'
		Question date = kbm.findQuestion("Date");
		assertNotNull("Question 'Date' isn't in the Knowledgebase.", date);
		
		// Question 'Event'
		Question event = kbm.findQuestion("Event");
		assertNotNull("Question 'Event' isn't in the Knowledgebase.", event);
		
		// Values of 'Event'
		Value fallOfTheWall = kbm.findValue(event, "Fall of the Berlin Wall");
		assertNotNull("Value 'Fall of the Berlin Wall' of Question 'Event' isn't in the Knowledgebase", fallOfTheWall);
		Value germanUnity = kbm.findValue(event, "German unity");
		assertNotNull("Value 'German Unity' of Question 'Event' isn't in the Knowledgebase", germanUnity);
	}
	
	@Test
	public void testAbstractionProperty() {
		
		// Event <abstract> ?
		Question event = kbm.findQuestion("Event");
		Boolean eventAbstractionProperty = (Boolean) event.getProperties().getProperty(Property.ABSTRACTION_QUESTION);
		assertEquals("Question 'BMI' isn't abstract.", Boolean.TRUE, eventAbstractionProperty);
	}

	@Test
	public void testSetAndChangeValue() {
		
		Calendar calendar = GregorianCalendar.getInstance();
		Question date = kbm.findQuestion("Date");
		Question event = kbm.findQuestion("Event");
		
		// SET 'Date' = 1989-11-09
		calendar.set(1989, Calendar.NOVEMBER, 9, 0, 0, 0);
		DateValue fallOfTheWallDate = new DateValue(calendar.getTime());
		session.setValue(date, fallOfTheWallDate);
		
		// TEST 'Date' == 1989-11-09
		Value currentDateValue = session.getValue(date);
		assertEquals("Question 'Date' has wrong value", fallOfTheWallDate, currentDateValue);
		
		// TEST 'Event' == 'Fall of the Berlin Wall'
		Value currentEventValue = session.getValue(event);
		Value fallOfTheWallValue = kbm.findValue(event, "Fall of the Berlin Wall");
		assertEquals("Question 'Event' has wrong value", fallOfTheWallValue, currentEventValue);
		
		// SET 'Date' = 1990-10-03
		calendar.set(1990, Calendar.OCTOBER, 3, 0, 0, 0);
		DateValue germanUnityDate = new DateValue(calendar.getTime());
		session.setValue(date, germanUnityDate);
		
		// TEST 'Date' == 1990-10-03
		currentDateValue = session.getValue(date);
		assertEquals("Question 'Date' has wrong value", germanUnityDate, currentDateValue);
		
		// TEST 'Event' == 'German Unity'
		currentEventValue = session.getValue(event);
		Value germanUnityValue = kbm.findValue(event, "German unity");
		assertEquals("Question 'Event' has wrong value", germanUnityValue, currentEventValue);
	}
	
	@Test
	public void testSetUndefinedValue() {

		Question date = kbm.findQuestion("Date");
		Question event = kbm.findQuestion("Event");
		
		// SET 'Date' = 'UNDEFINED'
		session.setValue(date, UndefinedValue.getInstance());
		
		// TEST 'Date' == 'UNDEFINED'
		Value currentDateValue = session.getValue(date);
		assertEquals("Question 'Date' has wrong value", UndefinedValue.getInstance(), currentDateValue);
		
		// TEST 'Event' == 'UNDEFINED'
		Value currentEventValue = session.getValue(event);
		assertEquals("Question 'Event' has wrong value", UndefinedValue.getInstance(), currentEventValue);
		
	}

}
