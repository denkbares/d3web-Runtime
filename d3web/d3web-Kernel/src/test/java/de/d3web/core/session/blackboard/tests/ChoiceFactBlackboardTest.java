/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.core.session.blackboard.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.test.InitPluginManager;

/**
 * This test class simulates the functionality of the {@link Blackboard} without
 * any problem-solver activity.
 * 
 * ChoiceValues are added to, removed from and merged within the
 * {@link Blackboard}.
 * 
 * @author joba (denkbares GmbH)
 * 
 */
public class ChoiceFactBlackboardTest {

	private static Blackboard blackboard;
	private static KnowledgeBaseManagement kbm;

	// the question 'weekday' with some values
	private static QuestionOC weekdayQuestion;
	private static QuestionMC colors;

	// sources for setting the facts
	private static final Object defaultSource = PSMethodUserSelected.getInstance();
	private static final Object alternativeSource = new Object();

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kbm = KnowledgeBaseManagement.createInstance();
		addTerminologyObjects();
		Session session = SessionFactory.createSession(kbm.getKnowledgeBase());
		blackboard = session.getBlackboard();
	}

	/**
	 * For one-choice question 'weekday' the values 'monday' and 'tuesday' are
	 * set subsequently. Different sources are used for this, so after
	 * retracting 'tuesday', the older value 'monday' should be valid again.
	 * 
	 * 1) :: weekday = undefined? 2) set weekday = monday (source1) :: weekday =
	 * monday? 3) set weekday = tuesday (source2) :: weekday = tuesday? 4)
	 * retract weekday = tuesday :: weekday = monday?
	 */
	@Test
	public void testOneChoiceValueFactDifferentSource() {

		// Check, whether the blackboard returns 'undefined' when no fact
		// available
		Value noFactValue = blackboard.getValue(weekdayQuestion);
		assertEquals(noFactValue, UndefinedValue.getInstance());

		// Assign the value 'monday' to 'weekday'
		Choice mondayChoice = kbm.findChoice(weekdayQuestion, "Monday");
		assertNotNull("Choice 'Monday' not found in question 'Weekday'", mondayChoice);
		Fact weekday_is_monday = createFact(weekdayQuestion, new ChoiceValue(mondayChoice));
		blackboard.addValueFact(weekday_is_monday);

		Value storedValue = blackboard.getValue(weekdayQuestion);
		assertEquals(new ChoiceValue(mondayChoice), storedValue);

		// Assign another value 'tuesday' to 'weekday'; same source, should
		// overwrite 'monday'
		Choice tuesdayChoice = kbm.findChoice(weekdayQuestion, "Tuesday");
		assertNotNull("Choice 'Tuesday' not found in question 'Weekday'", tuesdayChoice);
		Fact weekday_is_tuesday = createFact(weekdayQuestion,
				new ChoiceValue(tuesdayChoice), alternativeSource);
		blackboard.addValueFact(weekday_is_tuesday);

		storedValue = blackboard.getValue(weekdayQuestion);
		assertEquals(new ChoiceValue(tuesdayChoice), storedValue);

		// Remove the last fact from the blackboard:
		// due different sources, the value should be 'Monday' again
		blackboard.removeValueFact(weekday_is_tuesday);
		storedValue = blackboard.getValue(weekdayQuestion);
		assertEquals(new ChoiceValue(mondayChoice), storedValue);
	}

	/**
	 * For one-choice question 'weekday' the values 'Monday' and 'Tuesday' are
	 * set subsequently. The same sources are used for this, so the first value
	 * is overwritten and after retracting 'Tuesday', the value of the question
	 * should be Undefined again.
	 * 
	 * 1) :: weekday = undefined? 2) set weekday = monday :: weekday = monday?
	 * 3) set weekday = tuesday :: weekday = tuesday? 4) retract weekday =
	 * tuesday :: weekday = undefined?
	 */
	@Test
	public void testOneChoiceValueFactSameSource() {

		// Check, whether the blackboard returns 'undefined' when no fact
		// available
		Value noFactValue = blackboard.getValue(weekdayQuestion);
		assertEquals(noFactValue, UndefinedValue.getInstance());

		// Assign the value 'monday' to 'weekday'
		Choice mondayChoice = kbm.findChoice(weekdayQuestion, "Monday");
		assertNotNull("Choice 'Monday' not found in question 'Weekday'", mondayChoice);
		Fact weekday_is_monday = createFact(weekdayQuestion, new ChoiceValue(mondayChoice));
		blackboard.addValueFact(weekday_is_monday);

		Value storedValue = blackboard.getValue(weekdayQuestion);
		assertEquals(new ChoiceValue(mondayChoice), storedValue);

		// Assign another value 'tuesday' to 'weekday'; same source, should
		// overwrite 'monday'
		Choice tuesdayChoice = kbm.findChoice(weekdayQuestion, "Tuesday");
		assertNotNull("Choice 'Tuesday' not found in question 'Weekday'", tuesdayChoice);
		Fact weekday_is_tuesday = createFact(weekdayQuestion, new ChoiceValue(tuesdayChoice));
		blackboard.addValueFact(weekday_is_tuesday);

		storedValue = blackboard.getValue(weekdayQuestion);
		assertEquals(new ChoiceValue(tuesdayChoice), storedValue);

		// Remove the last fact from the blackboard:
		// due to same source, the value should be 'undefined' again
		blackboard.removeValueFact(weekday_is_tuesday);
		storedValue = blackboard.getValue(weekdayQuestion);
		assertEquals(UndefinedValue.getInstance(), storedValue);
	}

	/**
	 * For multiple-choice question 'colors' the values 'red' and 'red+green'
	 * are set subsequently. The same sources are used for this, so the first
	 * value is overwritten and after retracting 'red+green', the value of the
	 * question should be Undefined again.
	 * 
	 * 1) :: check color = undefined ? 2) set color = red :: check: red? 3) set
	 * color = red+green :: check: red+green? 4) retract color = red+green ::
	 * check: undefined?
	 */
	@Test
	public void testMultipleChoiceValueFactSameSource() {
		// Check, whether the blackboard returns 'undefined' when no fact
		// available
		Value noFactValue = blackboard.getValue(colors);
		assertEquals(noFactValue, UndefinedValue.getInstance());

		// Assign 'red' to the question 'colors'
		Choice redChoice = kbm.findChoice(colors, "red");
		assertNotNull("Choice 'red' not found in knowledge base.", redChoice);
		Value red = new MultipleChoiceValue(Arrays.asList(new ChoiceValue(redChoice)));
		blackboard.addValueFact(createFact(colors, red));
		assertEquals(red, blackboard.getValue(colors));

		// Assign 'red+green' to the question 'colors'
		Choice greenChoice = kbm.findChoice(colors, "green");
		assertNotNull("Choice 'red' not found in knowledge base.", redChoice);
		Value red_and_green = new MultipleChoiceValue(
				Arrays.asList(new ChoiceValue(redChoice), new ChoiceValue(greenChoice)));
		Fact color_is_red_and_green = createFact(colors, red_and_green);
		blackboard.addValueFact(color_is_red_and_green);
		assertEquals(red_and_green, blackboard.getValue(colors));

		// Retract last fact, then 'undefined' should be valid again, due to
		// same source
		blackboard.removeValueFact(color_is_red_and_green);
		assertEquals(UndefinedValue.getInstance(), blackboard.getValue(colors));
	}

	/**
	 * For multiple-choice question 'colors' the values 'red' and 'red+green'
	 * are set subsequently. Different sources are used for this, so after
	 * retracting 'red+green', the value of the question should be 'red' again.
	 * 
	 * 1) :: check color = undefined ? 2) set color = red (source1) :: check:
	 * red? 3) set color = red+green (source2) :: check: red+green? 4) retract
	 * color = red+green (source2) :: check: red?
	 * 
	 */
	@Test
	public void testMultipleChoiceValueFactDifferentSources() {
		// Check, whether the blackboard returns 'undefined' when no fact
		// available
		Value noFactValue = blackboard.getValue(colors);
		assertEquals(noFactValue, UndefinedValue.getInstance());

		// Assign 'red' to the question 'colors'
		Choice redChoice = kbm.findChoice(colors, "red");
		assertNotNull("Choice 'red' not found in knowledge base.", redChoice);
		Value red = new MultipleChoiceValue(Arrays.asList(new ChoiceValue(redChoice)));
		Fact color_is_red = createFact(colors, red);
		blackboard.addValueFact(color_is_red);
		assertEquals(red, blackboard.getValue(colors));

		// Assign 'red+green' to the question 'colors'
		Choice greenChoice = kbm.findChoice(colors, "green");
		assertNotNull("Choice 'red' not found in knowledge base.", redChoice);
		Value red_and_green = new MultipleChoiceValue(
				Arrays.asList(new ChoiceValue(redChoice), new ChoiceValue(greenChoice)));
		Fact color_is_red_and_green = createFact(colors, red_and_green, alternativeSource);
		blackboard.addValueFact(color_is_red_and_green);
		assertEquals(red_and_green, blackboard.getValue(colors));

		// Retract last fact 'red+green', then 'red' should be valid
		// again, due to different sources
		blackboard.removeValueFact(color_is_red_and_green);
		assertEquals(red, blackboard.getValue(colors));
	}

	private Fact createFact(Question question, Value value, Object source) {
		return new DefaultFact(question, value, source, PSMethodUserSelected.getInstance());
	}

	private Fact createFact(Question question, Value value) {
		return createFact(question, value, defaultSource);
	}

	private static void addTerminologyObjects() {
		// Question 'weekday'
		String[] weekdayAlternatives = new String[] {
				"Monday", "Tuesday",
				"Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
		weekdayQuestion = kbm.createQuestionOC("Weekday",
				kbm.getKnowledgeBase().getRootQASet(),
				weekdayAlternatives);

		// question 'colors'
		colors = kbm.createQuestionMC("Colors",
				kbm.getKnowledgeBase().getRootQASet(),
				new String[] {
				"red", "green", "blue" });
	}
}
