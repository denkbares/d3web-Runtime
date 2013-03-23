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
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.xcl.XCLModel;
import de.d3web.xcl.XCLRelationType;

/**
 * This test is designed to control the solution derivation process of the XCL
 * problem solver.
 * 
 * The tested knowledge base contains the following terminology objects:
 * 
 * <b>Questions</b> Team sport [oc] - Yes - No Health problems [oc] - Yes - No
 * Importance of fun [oc] - important - not important
 * 
 * <b>Solutions</b> Football Swimming
 * 
 * The problem solving is based on the following <b>XCL-Models</b>:
 * 
 * Football { Team sport = Yes [++], Health problems = No, Importance of fun =
 * important }
 * 
 * Swimming { Team sport = No, Health problems = Yes [++], Importance of fun =
 * not important }
 * 
 * 
 * @author Sebastian Furth
 * 
 */
public class XCLSolutionDerivationTest {

	private static KnowledgeBase kb;
	private static Session session;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		addTerminologyObjects();
		addXCLModels();
		session = SessionFactory.createSession(kb);
	}

	private static void addTerminologyObjects() {

		// Question 'Team sport'
		String teamSport = "Team sport";
		String[] teamSportAlternatives = new String[] {
				"Yes", "No" };
		new QuestionOC(kb.getRootQASet(), teamSport,
				teamSportAlternatives);

		// Question 'Health problems'
		String healthProblems = "Health problems";
		String[] healthProblemsAlternatives = new String[] {
				"Yes", "No" };
		new QuestionOC(kb.getRootQASet(), healthProblems,
				healthProblemsAlternatives);

		// Question 'Importance of fun'
		String fun = "Importance of fun";
		String[] funAlternatives = new String[] {
				"important", "not important" };
		new QuestionOC(kb.getRootQASet(), fun,
				funAlternatives);

		// Solution 'Football'
		new Solution(kb.getRootSolution(), "Football");

		// Solution 'Swimming'
		new Solution(kb.getRootSolution(), "Swimming");
	}

	private static void addXCLModels() {

		// Solutions
		Solution football = kb.getManager().searchSolution("Football");
		Solution swimming = kb.getManager().searchSolution("Swimming");

		// Question 'Team sport' and alternatives
		Question teamSport = kb.getManager().searchQuestion("Team sport");
		Value teamSportYes = KnowledgeBaseUtils.findValue(teamSport, "Yes");
		Value teamSportNo = KnowledgeBaseUtils.findValue(teamSport, "No");

		// Question 'Health problems' and alternatives
		Question healthProblems = kb.getManager().searchQuestion(
				"Health problems");
		Value healthProblemsYes = KnowledgeBaseUtils.findValue(healthProblems, "Yes");
		Value healthProblemsNo = KnowledgeBaseUtils.findValue(healthProblems, "No");

		// Question 'Importance of fun' and alternatives
		Question fun = kb.getManager().searchQuestion("Importance of fun");
		Value funImportant = KnowledgeBaseUtils.findValue(fun, "important");
		Value funNotImportant = KnowledgeBaseUtils.findValue(fun, "not important");

		// Model 'Football'

		// Relation 'Team sport' = 'Yes' [++]
		Condition condTeamSportYes = new CondEqual(teamSport, teamSportYes);
		XCLModel.insertXCLRelation(kb, condTeamSportYes, football,
				XCLRelationType.sufficiently);

		// Relation 'Health problems = 'No'
		Condition condHealthProblemsNo = new CondEqual(healthProblems, healthProblemsNo);
		XCLModel.insertXCLRelation(kb, condHealthProblemsNo, football);

		// Relation 'Importance of fun' = 'important'
		Condition condFunImportant = new CondEqual(fun, funImportant);
		XCLModel.insertXCLRelation(kb, condFunImportant, football);

		// Model 'Swimming'

		// Relation 'Team sport' = 'No'
		Condition condTeamSportNo = new CondEqual(teamSport, teamSportNo);
		XCLModel.insertXCLRelation(kb, condTeamSportNo, swimming);

		// Relation 'Health problems' = 'Yes' [++]
		Condition condHealthProblemsYes = new CondEqual(healthProblems, healthProblemsYes);
		XCLModel.insertXCLRelation(kb, condHealthProblemsYes, swimming);

		// Relation 'Importance of fun' = 'not important'
		Condition condFunNotImportant = new CondEqual(fun, funNotImportant);
		XCLModel.insertXCLRelation(kb, condFunNotImportant, swimming);
	}

	@Test
	public void testTerminlogyObjectExistence() {

		// Solution 'Football'
		Solution football = kb.getManager().searchSolution("Football");
		assertNotNull("Solution 'Football' isn't in the Knowledgebase",
				football);

		// Solution 'Swimming'
		Solution swimming = kb.getManager().searchSolution("Swimming");
		assertNotNull("Solution 'Swimming' isn't in the Knowledgebase",
				swimming);

		// Question 'Team sport' and alternatives
		Question teamSport = kb.getManager().searchQuestion("Team sport");
		assertNotNull("Question 'Team sport' isn't in the Knowledgebase",
				teamSport);

		Value teamSportYes = KnowledgeBaseUtils.findValue(teamSport, "Yes");
		assertNotNull("Value 'Yes' of Question 'Team sport' isn't in the Knowledgebase",
				teamSportYes);

		Value teamSportNo = KnowledgeBaseUtils.findValue(teamSport, "No");
		assertNotNull("Value 'No' of Question 'Team sport' isn't in the Knowledgebase",
				teamSportNo);

		// Question 'Health problems' and alternatives
		Question healthProblems = kb.getManager().searchQuestion(
				"Health problems");
		assertNotNull("Question 'Health problems' isn't in the Knowledgebase",
				healthProblems);

		Value healthProblemsYes = KnowledgeBaseUtils.findValue(healthProblems, "Yes");
		assertNotNull("Value 'Yes' of Question 'Health problems' isn't in the Knowledgebase",
				healthProblemsYes);

		Value healthProblemsNo = KnowledgeBaseUtils.findValue(healthProblems, "No");
		assertNotNull("Value 'No' of Question 'Health problems' isn't in the Knowledgebase",
				healthProblemsNo);

		// Question 'Importance of fun' and alternatives
		Question fun = kb.getManager().searchQuestion("Importance of fun");
		assertNotNull("Question 'Importance of fun' isn't in the Knowledgebase",
				fun);

		Value funImportant = KnowledgeBaseUtils.findValue(fun, "important");
		assertNotNull(
				"Value 'important' of Question 'Importance of fun' isn't in the Knowledgebase",
				funImportant);
		Value funNotImportant = KnowledgeBaseUtils.findValue(fun, "not important");
		assertNotNull(
				"Value 'not important' of Question 'Importance of fun' isn't in the Knowledgebase",
				funNotImportant);

	}

	@Test
	public void testSetValue() {

		// Solutions
		Solution football = kb.getManager().searchSolution("Football");
		Solution swimming = kb.getManager().searchSolution("Swimming");

		// Question 'Team sport' and alternatives
		Question teamSport = kb.getManager().searchQuestion("Team sport");
		Value teamSportYes = KnowledgeBaseUtils.findValue(teamSport, "Yes");

		// Question 'Health problems' and alternatives
		Question healthProblems = kb.getManager().searchQuestion(
				"Health problems");
		Value healthProblemsNo = KnowledgeBaseUtils.findValue(healthProblems, "No");

		// Question 'Importance of fun' and alternatives
		Question fun = kb.getManager().searchQuestion("Importance of fun");
		Value funNotImportant = KnowledgeBaseUtils.findValue(fun, "not important");

		// SET 'Health problems' = 'No'
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, healthProblems,
						healthProblemsNo,
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		// TEST 'Health problems' == 'No'
		Value healthProblemsValue = session.getBlackboard().getValue(healthProblems);
		assertEquals("Question 'Health problems' has wrong value", healthProblemsNo,
				healthProblemsValue);

		// TEST 'Football' == ESTABLISHED
		Rating footballState = session.getBlackboard().getRating(football);
		assertTrue("Solution 'Football' has wrong state. Expected 'ESTABLISHED'",
				footballState.hasState(Rating.State.ESTABLISHED));

		// SET 'Importance of fun' = 'not important'
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, fun,
						funNotImportant,
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		// TEST 'Importance of fun' == 'not important'
		Value funValue = session.getBlackboard().getValue(fun);
		assertEquals("Question 'Importance of fun' has wrong value", funNotImportant,
				funValue);

		// TEST 'Football' == SUGGESTED
		footballState = session.getBlackboard().getRating(football);
		assertTrue("Solution 'Football' has wrong state. Expected 'SUGGESTED'",
				footballState.hasState(Rating.State.SUGGESTED));

		// TEST 'Swimming' == SUGGESTED
		Rating swimmingState = session.getBlackboard().getRating(swimming);
		assertTrue("Solution 'Swimming' has wrong state. Expected 'SUGGESTED'",
				swimmingState.hasState(Rating.State.SUGGESTED));

		// SET 'Team sport' = 'Yes'
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, teamSport, teamSportYes,
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		// TEST 'Team sport' == 'Yes'
		Value teamSportValue = session.getBlackboard().getValue(teamSport);
		assertEquals("Question 'Team sport' has wrong value", teamSportYes, teamSportValue);

		// TEST 'Football' == ESTABLISHED
		footballState = session.getBlackboard().getRating(football);
		assertTrue("Solution 'Football' has wrong state. Expected 'ESTABLISHED'",
				footballState.hasState(Rating.State.ESTABLISHED));

		// TEST 'Swimming' == SUGGESTED
		swimmingState = session.getBlackboard().getRating(swimming);
		assertTrue("Solution 'Swimming' has wrong state. Expected 'SUGGESTED'",
				swimmingState.hasState(Rating.State.SUGGESTED));

	}

	@Test
	public void testChangeValue() {

		testSetValue();

		// Solutions
		Solution football = kb.getManager().searchSolution("Football");
		Solution swimming = kb.getManager().searchSolution("Swimming");

		// Question 'Team sport' and alternatives
		Question teamSport = kb.getManager().searchQuestion("Team sport");
		Value teamSportNo = KnowledgeBaseUtils.findValue(teamSport, "No");

		// Question 'Health problems' and alternatives
		Question healthProblems = kb.getManager().searchQuestion(
				"Health problems");
		Value healthProblemsYes = KnowledgeBaseUtils.findValue(healthProblems, "Yes");

		// SET 'Health problems' = 'Yes'
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, healthProblems,
						healthProblemsYes,
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		// TEST 'Health problems' == 'Yes'
		Value healthProblemsValue = session.getBlackboard().getValue(healthProblems);
		assertEquals("Question 'Health problems' has wrong value", healthProblemsYes,
				healthProblemsValue);

		// TEST 'Football' == ESTABLISHED
		Rating footballState = session.getBlackboard().getRating(football);
		assertTrue("Solution 'Football' has wrong state. Expected 'ESTABLISHED'",
				footballState.hasState(Rating.State.ESTABLISHED));

		// TEST 'Swimming' == SUGGESTED
		Rating swimmingState = session.getBlackboard().getRating(swimming);
		assertTrue("Solution 'Swimming' has wrong state. Expected 'SUGGESTED'",
				swimmingState.hasState(Rating.State.SUGGESTED));

		// SET 'Team sport' = 'No'
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, teamSport, teamSportNo,
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		// TEST 'Team sport' == 'No'
		Value teamSportValue = session.getBlackboard().getValue(teamSport);
		assertEquals("Question 'Team sport' has wrong value", teamSportNo, teamSportValue);

		// TEST 'Football' == UNCLEAR
		footballState = session.getBlackboard().getRating(football);
		assertTrue("Solution 'Football' has wrong state. Expected 'UNCLEAR'",
				footballState.hasState(Rating.State.UNCLEAR));

		// TEST 'Swimming' == ESTABLISHED
		swimmingState = session.getBlackboard().getRating(swimming);
		assertTrue("Solution 'Swimming' has wrong state. Expected 'ESTABLISHED'",
				swimmingState.hasState(Rating.State.ESTABLISHED));

	}

	@Test
	public void testSetUndefinedValue() {

		// Solutions
		Solution football = kb.getManager().searchSolution("Football");
		Solution swimming = kb.getManager().searchSolution("Swimming");

		// Question 'Team sport'
		Question teamSport = kb.getManager().searchQuestion("Team sport");

		// Question 'Health problems'
		Question healthProblems = kb.getManager().searchQuestion(
				"Health problems");

		// Question 'Importance of fun'
		Question fun = kb.getManager().searchQuestion("Importance of fun");

		// SET 'Health problems' = 'Undefined'
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, healthProblems,
						UndefinedValue.getInstance(),
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		// TEST 'Health problems' == 'Undefined'
		Value healthProblemsValue = session.getBlackboard().getValue(healthProblems);
		assertEquals("Question 'Health problems' has wrong value", UndefinedValue.getInstance(),
				healthProblemsValue);

		// SET 'Importance of fun' = 'Undefined'
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, fun,
						UndefinedValue.getInstance(),
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		// TEST 'Importance of fun' == 'Undefined'
		Value funValue = session.getBlackboard().getValue(fun);
		assertEquals("Question 'Importance of fun' has wrong value", UndefinedValue.getInstance(),
				funValue);

		// SET 'Team sport' = 'Undefined'
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, teamSport,
						UndefinedValue.getInstance(),
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		// TEST 'Team sport' == 'Undefined'
		Value teamSportValue = session.getBlackboard().getValue(teamSport);
		assertEquals("Question 'Team sport' has wrong value", UndefinedValue.getInstance(),
				teamSportValue);

		// TEST 'Football' == UNCLEAR
		Rating footballState = session.getBlackboard().getRating(football);
		assertTrue("Solution 'Football' has wrong state. Expected 'UNCLEAR'",
				footballState.hasState(Rating.State.UNCLEAR));

		// TEST 'Swimming' == UNCLEAR
		Rating swimmingState = session.getBlackboard().getRating(swimming);
		assertTrue("Solution 'Swimming' has wrong state. Expected 'UNCLEAR'",
				swimmingState.hasState(Rating.State.UNCLEAR));
	}

}
