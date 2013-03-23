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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.scoring.Score;

/**
 * This test is designed to control the solution derivation process of the
 * heuristic problem solver.
 * 
 * The tested knowledge base contains the following terminology objects:
 * 
 * <b>Questions</b> Exhaust fumes [oc] - black - blue - invisible Fuel [oc] -
 * unleaded gasoline - diesel
 * 
 * <b>Solutions</b> Clogged air filter
 * 
 * The problem solving is based on the following <b>Rules</b>:
 * 
 * Exhaust fumes = black => Clogged air filter = P3 Fuel = unleaded gasoline =>
 * Clogged air filter = P5
 * 
 * 
 * @author Sebastian Furth
 * 
 */
public class HeuristicSolutionDerivationTest {

	private static KnowledgeBase kb;
	private static Session session;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		addTerminologyObjects();
		addRules();
		session = SessionFactory.createSession(kb);
	}

	private static void addTerminologyObjects() {

		// Question 'Exhaust fumes'
		String exhaustFumes = "Exhaust fumes";
		String[] exhaustFumesAlternatives = new String[] {
				"black", "blue", "invisible" };
		new QuestionOC(kb.getRootQASet(), exhaustFumes,
				exhaustFumesAlternatives);

		// Question 'Fuel'
		String fuel = "Fuel";
		String[] fuelAlternatives = new String[] {
				"diesel", "unleaded gasoline" };
		new QuestionOC(kb.getRootQASet(), fuel,
				fuelAlternatives);

		// Solution 'Clogged air filter'
		new Solution(kb.getRootSolution(), "Clogged air filter");
	}

	private static void addRules() {

		Solution cloggedAirFilter = kb.getManager().searchSolution(
				"Clogged air filter");

		// Exhaust fumes = black => Clogged air filter = P3
		Question exhaustFumes = kb.getManager().searchQuestion("Exhaust fumes");
		Value black = KnowledgeBaseUtils.findValue(exhaustFumes, "black");
		Condition condition = new CondEqual(exhaustFumes, black);
		RuleFactory.createHeuristicPSRule(cloggedAirFilter, Score.P3,
				condition);

		// Fuel = unleaded gasoline => Clogged air filter = P5
		Question fuel = kb.getManager().searchQuestion("Fuel");
		Value unleadedGasoline = KnowledgeBaseUtils.findValue(fuel, "unleaded gasoline");
		condition = new CondEqual(fuel, unleadedGasoline);
		RuleFactory.createHeuristicPSRule(cloggedAirFilter, Score.P5,
				condition);
	}

	@Test
	public void testTerminlogyObjectExistence() {

		// Question 'Exhaust fumes'
		Question exhaustFumes = kb.getManager().searchQuestion("Exhaust fumes");
		assertNotNull("Question 'Exhaust fumes' isn't in the Knowledgebase.",
				exhaustFumes);

		// Values of 'Exhaust fumes'
		Value black = KnowledgeBaseUtils.findValue(exhaustFumes, "black");
		assertNotNull(
				"Value 'black' for Question 'Exhaust fumes' isn't in the Knowledgebase",
				black);
		Value blue = KnowledgeBaseUtils.findValue(exhaustFumes, "blue");
		assertNotNull(
				"Value 'blue' for Question 'Exhaust fumes' isn't in the Knowledgebase",
				blue);
		Value invisible = KnowledgeBaseUtils.findValue(exhaustFumes, "invisible");
		assertNotNull(
				"Value 'blue' for Question 'Exhaust fumes' isn't in the Knowledgebase",
				invisible);

		// Question 'Fuel'
		Question fuel = kb.getManager().searchQuestion("Fuel");
		assertNotNull("Question 'Fuel' isn't in the Knowledgebase.", fuel);

		// Values of 'Fuel'
		Value unleadedGasoline = KnowledgeBaseUtils.findValue(fuel, "unleaded gasoline");
		assertNotNull(
				"Value 'unleaded gasoline' for Question 'Fuel' isn't in the Knowledgebase",
				unleadedGasoline);
		Value diesel = KnowledgeBaseUtils.findValue(fuel, "diesel");
		assertNotNull("Value 'diesel' for Question 'Fuel' isn't in the Knowledgebase",
				diesel);

		// Solution 'Clogged air filter'
		Solution cloggedAirFilter = kb.getManager().searchSolution(
				"Clogged air filter");
		assertNotNull("Solution 'Clogged air filter' isn't in the Knowledgebase",
				cloggedAirFilter);
	}

	// Exhaust fumes = black => Clogged air filter = P3
	// Fuel = unleaded gasoline => Clogged air filter = P5
	@Test
	public void testSetValue() {

		Question exhaustFumes = kb.getManager().searchQuestion("Exhaust fumes");
		Question fuel = kb.getManager().searchQuestion("Fuel");
		Solution solution = kb.getManager().searchSolution("Clogged air filter");

		// SET 'Exhaust fumes' = 'black'
		Value black = KnowledgeBaseUtils.findValue(exhaustFumes, "black");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, exhaustFumes,
						black, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));

		// TEST 'Exhaust fumes' == 'black'
		Value exhaustFumesValue = session.getBlackboard().getValue(exhaustFumes);
		assertEquals("Question 'Exhaust fumes' has wrong value", black, exhaustFumesValue);

		// TEST 'Clogged air filter' == SUGGESTED
		Rating cloggedAirFilterState = session.getBlackboard().getRating(solution);
		assertTrue("Solution 'Clogged air filter' has wrong state. Expected 'SUGGESTED'",
				cloggedAirFilterState.hasState(Rating.State.SUGGESTED));

		// SET 'Fuel' = 'unleaded gasoline'
		Value unleadedGasoline = KnowledgeBaseUtils.findValue(fuel, "unleaded gasoline");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, fuel,
						unleadedGasoline, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));

		// TEST 'Fuel' == 'unleaded gasoline'
		Value fuelValue = session.getBlackboard().getValue(fuel);
		assertEquals("Question 'Fuel' has wrong value", unleadedGasoline, fuelValue);

		// TEST 'Clogged air filter' == ESTABLISHED
		cloggedAirFilterState = session.getBlackboard().getRating(solution);
		assertTrue(
				"Solution 'Clogged air filter' has wrong state. Expected 'ESTABLISHED'",
				cloggedAirFilterState.hasState(Rating.State.ESTABLISHED));
	}

	// Exhaust fumes = black => Clogged air filter = P3
	// Fuel = unleaded gasoline => Clogged air filter = P5
	@Test
	public void testChangeValue() {

		Question fuel = kb.getManager().searchQuestion("Fuel");
		Question exhaustFumes = kb.getManager().searchQuestion("Exhaust fumes");
		Solution cloggedAirFilter = kb.getManager().searchSolution(
				"Clogged air filter");

		// SET 'Fuel' = 'diesel'
		Value diesel = KnowledgeBaseUtils.findValue(fuel, "diesel");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, fuel,
						diesel, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));

		// TEST 'Fuel' == 'diesel'
		Value fuelValue = session.getBlackboard().getValue(fuel);
		assertEquals("Question 'Fuel' has wrong value", diesel, fuelValue);

		// TEST 'Clogged air filter' == SUGGESTED
		Rating cloggedAirFilterState = session.getBlackboard().getRating(
				cloggedAirFilter);
		assertTrue("Solution 'Clogged air filter' has wrong state. Expected 'UNCLEAR'",
				cloggedAirFilterState.hasState(Rating.State.UNCLEAR));

		// SET 'Exhaust fumes' = 'blue'
		Value blue = KnowledgeBaseUtils.findValue(exhaustFumes, "blue");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, exhaustFumes,
						blue, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));

		// TEST 'Exhaust fumes' == 'blue'
		Value exhaustFumesValue = session.getBlackboard().getValue(exhaustFumes);
		assertEquals("Question 'Exhaust fumes' has wrong value", blue, exhaustFumesValue);

		// TEST 'Clogged air filter' == UNCLEAR
		cloggedAirFilterState = session.getBlackboard().getRating(cloggedAirFilter);
		assertTrue("Solution 'Clogged air filter' has wrong state. Expected 'UNCLEAR'",
				cloggedAirFilterState.hasState(Rating.State.UNCLEAR));
	}

	@Test
	public void testSetUndefinedValue() {

		Question exhaustFumes = kb.getManager().searchQuestion("Exhaust fumes");
		Solution cloggedAirFilter = kb.getManager().searchSolution(
				"Clogged air filter");

		// SET 'Exhaust fumes' = 'black'
		Value black = KnowledgeBaseUtils.findValue(exhaustFumes, "black");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, exhaustFumes,
						black, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));

		// TEST 'Exhaust fumes' == 'black'
		Value exhaustFumesValue = session.getBlackboard().getValue(exhaustFumes);
		assertEquals("Question 'Exhaust fumes' has wrong value", black, exhaustFumesValue);

		// TEST 'Clogged air filter' == SUGGESTED
		Rating cloggedAirFilterState = session.getBlackboard().getRating(
				cloggedAirFilter);
		assertTrue("Solution 'Clogged air filter' has wrong state. Expected 'SUGGESTED'",
				cloggedAirFilterState.hasState(Rating.State.SUGGESTED));

		// SET 'Exhaust fumes' = 'UNDEFINED'
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, exhaustFumes,
						UndefinedValue.getInstance(), PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));

		// TEST 'Exhaust fumes' == 'UNDEFINED'
		exhaustFumesValue = session.getBlackboard().getValue(exhaustFumes);
		assertEquals("Question 'Exhaust fumes' has wrong value",
				UndefinedValue.getInstance(), exhaustFumesValue);

		// TEST 'Clogged air filter' == UNCLEAR
		cloggedAirFilterState = session.getBlackboard().getRating(cloggedAirFilter);
		assertTrue("Solution 'Clogged air filter' has wrong state. Expected 'UNCLEAR'",
				cloggedAirFilterState.hasState(Rating.State.UNCLEAR));
	}

}
