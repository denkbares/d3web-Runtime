/*
 * Copyright (C) 2010 denkbares GmbH, Wuerzburg
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
package de.d3web.diaFlux.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowFactory;
import de.d3web.diaFlux.flow.IEdge;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.inference.ConditionTrue;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.NodeActiveCondition;
import de.d3web.indication.ActionInstantIndication;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.Score;

/**
 * First (small) test of FluxProblemSolver
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 10.11.2010
 */
public class UseFluxProblemSolverTest {

	private static KnowledgeBase kb;
	private static Session session;
	private static INode questionNode;
	private static INode solutionNode;
	private static Question questionYN;
	private static Solution solutionFoo;
	private static Solution solutionFoo2;
	private static Solution solutionFoo3;
	private static Solution solutionFoo4;

	private static final FlowFactory FF = FlowFactory.getInstance();
	private List<IEdge> edgesList;
	private List<INode> nodesList;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		setUpFlux();

	}

	/**
	 * This method sets up the following trivial FlowChart
	 * 
	 * (true) YesNoQuestion = "Yes" (true) Start -------> YesNoQuestion
	 * -----------------------> solutionFoo ---------> Ende [yn] = P7
	 * 
	 * @created 11.11.2010
	 */
	private void setUpFlux() {

		questionYN = new QuestionYN(kb.getRootQASet(), "YesNoQuestion");
		solutionFoo = new Solution(kb.getRootSolution(), "SolutionFoo");
		solutionFoo2 = new Solution(kb.getRootSolution(), "SolutionFoo2");
		solutionFoo3 = new Solution(kb.getRootSolution(), "SolutionFoo3");
		solutionFoo4 = new Solution(kb.getRootSolution(), "SolutionFoo4");

		INode startNode = FF.createStartNode("Start_ID", "Start");
		INode endNode = FF.createEndNode("End_ID", "Ende");

		List<QASet> qasets = new ArrayList<QASet>();
		qasets.add(questionYN);
		ActionInstantIndication instantIndication = new ActionInstantIndication();
		instantIndication.setQASets(qasets);
		questionNode = FF.createActionNode("questionNode_ID", instantIndication);

		ActionHeuristicPS heuristicAction = new ActionHeuristicPS();
		heuristicAction.setScore(Score.P7);
		heuristicAction.setSolution(solutionFoo);
		solutionNode = FF.createActionNode("solutionNode_ID", heuristicAction);

		nodesList = new LinkedList<INode>(Arrays.asList(startNode, endNode, questionNode,
				solutionNode));

		// ---------------------------------

		IEdge startToQuestion = FF.createEdge("startToQuestionEdge_ID", startNode, questionNode,
				ConditionTrue.INSTANCE);

		Value yes = KnowledgeBaseUtils.findValue(questionYN, "Yes");
		Condition yesCondition = new CondEqual(questionYN, yes);

		IEdge questionToSolution = FF.createEdge("questionToSolution_ID", questionNode,
				solutionNode, yesCondition);

		IEdge solutionToEnd = FF.createEdge("solutionToEnd_ID", solutionNode, endNode,
				ConditionTrue.INSTANCE);
		edgesList = new LinkedList<IEdge>(Arrays.asList(startToQuestion, questionToSolution,
				solutionToEnd));

		// ----------------------------------

	}

	@Test
	public void testFluxSolver() {
		// Create the flowchart...
		Flow testFlow = FF.createFlow("testFlow_ID", "Main", nodesList, edgesList);
		testFlow.setAutostart(true);
		DiaFluxUtils.addFlow(testFlow, kb);
		session = SessionFactory.createSession(kb);
		testBasicFlow();
		testDeactivation();
	}

	private void testBasicFlow() {
		Rating solutionState = session.getBlackboard().getRating(solutionFoo);
		assertTrue("Solution has wrong state. Expected 'UNCLEAR'",
				solutionState.hasState(Rating.State.UNCLEAR));// this is true
		List<InterviewObject> currentlyActiveObjects = session.getInterview().getInterviewAgenda().getCurrentlyActiveObjects();
		assertTrue("YesNoQuestion should be on Agenda", currentlyActiveObjects.contains(questionYN));
		assertTrue("YesNoQuestion should be the next form",
				session.getInterview().nextForm().getInterviewObject() == questionYN);
		// Answer question with "Yes", this should execute the flow
		Value yes = KnowledgeBaseUtils.findValue(questionYN, "Yes");
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(questionYN, yes));
		solutionState = session.getBlackboard().getRating(solutionFoo);
		assertTrue("Solution has wrong state. Expected 'ESTABLISHED'",
				solutionState.hasState(Rating.State.ESTABLISHED));
	}

	private void testDeactivation() {
		// When Answer "No" is set, the establishment of the solution
		// should be retracted:
		Value no = KnowledgeBaseUtils.findValue(questionYN, "No");
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(questionYN, no));
		Rating solutionState = session.getBlackboard().getRating(solutionFoo);
		assertTrue("Solution has wrong state. Expected 'UNCLEAR'",
				solutionState.hasState(Rating.State.UNCLEAR));
	}

	@Test
	public void testSubFlows() {
		// create inner flowchart
		Flow innerFlow = FF.createFlow("Flow1", "innerFlow", nodesList, edgesList);
		DiaFluxUtils.addFlow(innerFlow, kb);
		// create surrounding flowchart
		INode startNode = FF.createStartNode("Start_ID2", "Start");
		INode innerFlowNode = FF.createComposedNode("Flow1", "innerFlow", "Start");
		IEdge e1 = FF.createEdge("e1", startNode, innerFlowNode, ConditionTrue.INSTANCE);
		ActionHeuristicPS heuristicAction = new ActionHeuristicPS();
		heuristicAction.setScore(Score.P7);
		heuristicAction.setSolution(solutionFoo2);
		INode setterNode = FF.createActionNode("foo2setter", heuristicAction);
		IEdge e2 = FF.createEdge("e2", innerFlowNode, setterNode, new NodeActiveCondition(
				"innerFlow", "Ende"));
		List<INode> nodesList = Arrays.asList(startNode, innerFlowNode, setterNode);
		List<IEdge> edgeList = Arrays.asList(e1, e2);
		Flow outerFlow = FF.createFlow("Flow2", "Main", nodesList, edgeList);
		outerFlow.setAutostart(true);
		DiaFluxUtils.addFlow(outerFlow, kb);
		session = SessionFactory.createSession(kb);
		testBasicFlow();
		assertTrue(session.getBlackboard().getRating(solutionFoo2).hasState(
				Rating.State.ESTABLISHED));
		testDeactivation();
		assertTrue(session.getBlackboard().getRating(solutionFoo2).hasState(
				Rating.State.UNCLEAR));
	}

	@Test
	public void testSnapshot() {
		// create nodes for the other solutions
		ActionHeuristicPS heuristicAction2 = new ActionHeuristicPS();
		heuristicAction2.setScore(Score.P7);
		heuristicAction2.setSolution(solutionFoo2);
		INode solutionNode2 = FF.createActionNode("solutionNode_ID2", heuristicAction2);
		nodesList.add(solutionNode2);

		ActionHeuristicPS heuristicAction3 = new ActionHeuristicPS();
		heuristicAction3.setScore(Score.P7);
		heuristicAction3.setSolution(solutionFoo3);
		INode solutionNode3 = FF.createActionNode("solutionNode_ID3", heuristicAction3);
		nodesList.add(solutionNode3);

		ActionHeuristicPS heuristicAction4 = new ActionHeuristicPS();
		heuristicAction4.setScore(Score.P7);
		heuristicAction4.setSolution(solutionFoo4);
		INode solutionNode4 = FF.createActionNode("solutionNode_ID4", heuristicAction4);
		nodesList.add(solutionNode4);

		// create edges to Solutions
		Value no = KnowledgeBaseUtils.findValue(questionYN, "No");
		Condition noCondition = new CondEqual(questionYN, no);

		IEdge questionToSolution2 = FF.createEdge("questionToSolution_ID2", questionNode,
				solutionNode2, noCondition);
		edgesList.add(questionToSolution2);

		IEdge questionToSolution3 = FF.createEdge("questionToSolution_ID3", questionNode,
				solutionNode3, noCondition);
		edgesList.add(questionToSolution3);

		IEdge solutionToSolution4 = FF.createEdge("solutionToSolution4_ID", solutionNode,
				solutionNode4, ConditionTrue.INSTANCE);
		edgesList.add(solutionToSolution4);

		// create Snapshot Node
		INode snapshotNode = FF.createSnapshotNode("Snapshot1", "Snapshot");
		nodesList.add(snapshotNode);

		// create edge to Snapshot Node
		IEdge solution3ToSnapshot = FF.createEdge("e2", solutionNode3, snapshotNode,
				ConditionTrue.INSTANCE);
		edgesList.add(solution3ToSnapshot);

		Flow testFlow = FF.createFlow("testFlow_Snapshot", "Main", nodesList, edgesList);
		testFlow.setAutostart(true);
		DiaFluxUtils.addFlow(testFlow, kb);
		session = SessionFactory.createSession(kb);
		testBasicFlow();
		// Solution 1 and 4 should be established (solution 1 is tested in
		// testBasicFlow)
		List<Solution> solutions = session.getBlackboard().getSolutions(State.ESTABLISHED);
		Assert.assertEquals(2, solutions.size());
		assertTrue(solutions.contains(solutionFoo4));
		testDeactivation();
		solutions = session.getBlackboard().getSolutions(State.ESTABLISHED);
		// Solution 2 and 3 should be established
		Assert.assertEquals(2, solutions.size());
		assertTrue(solutions.contains(solutionFoo2));
		assertTrue(solutions.contains(solutionFoo3));
		Value yes = KnowledgeBaseUtils.findValue(questionYN, "Yes");
		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(questionYN, yes));
		// Solution 1 and 4 should be established
		Assert.assertEquals(2, solutions.size());
		assertTrue(solutions.contains(solutionFoo2));
		assertTrue(solutions.contains(solutionFoo3));
	}
}
