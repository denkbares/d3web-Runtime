/*
 * Copyright (C) 2010 denkbares GmbH, Wuerzburg
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
package de.d3web.diaFlux.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.diaFlux.ConditionTrue;
import de.d3web.diaFlux.NoopAction;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowFactory;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.flow.IEdge;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.indication.ActionIndication;
import de.d3web.indication.ActionInstantIndication;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.Score;

/**
 * 
 * @author Reinhard Hatko
 * @created 11.11.2010
 */
public class DiaFluxPersistenceTest {

	private static final FlowFactory FF = FlowFactory.getInstance();
	private KnowledgeBaseManagement kbm;
	private Session session;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();

		File file = new File("target/test.zip");
		KnowledgeBase createdKB = createTestKB();
		PersistenceManager.getInstance().save(createdKB, file);

		KnowledgeBase loadedKB = PersistenceManager.getInstance().load(file);

		kbm = KnowledgeBaseManagement.createInstance(loadedKB);

		session = SessionFactory.createSession(kbm.getKnowledgeBase());

		System.out.println(createdKB.equals(loadedKB));

		FlowSet loadedFlowSet = DiaFluxUtils.getFlowSet(loadedKB);

		FlowSet createdFlowSet = DiaFluxUtils.getFlowSet(createdKB);

		System.out.println(loadedFlowSet.equals(createdFlowSet));

		System.out.println("Created: " + createdFlowSet);
		System.out.println();
		System.out.println("Loaded: " + loadedFlowSet);



	}

	@Test
	public void testFluxSolver() {
		Question question = kbm.findQuestion("YesNoQuestion");
		Solution solution = kbm.findSolution("SolutionFoo");

		Rating solutionState = session.getBlackboard().getRating(solution);
		assertTrue("Solution has wrong state. Expected 'UNCLEAR'",
				solutionState.hasState(Rating.State.UNCLEAR));// this is true

		// Answer question with "Yes", this should execute the flow
		Value yes = kbm.findValue(question, "Yes");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(question, yes,
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
		solutionState = session.getBlackboard().getRating(solution);
		assertTrue("Solution has wrong state. Expected 'ESTABLISHED'",
				solutionState.hasState(Rating.State.ESTABLISHED));

		// When Answer "No" is set, the establishment of the solution
		// should be retracted:
		Value no = kbm.findValue(question, "No");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(question, no,
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
		solutionState = session.getBlackboard().getRating(solution);
		assertTrue("Solution has wrong state. Expected 'UNCLEAR'",
				solutionState.hasState(Rating.State.UNCLEAR));
	}


	public static KnowledgeBase createTestKB() {
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance();

		Question questionYN = kbm.createQuestionYN("YesNoQuestion",
				kbm.getKnowledgeBase().getRootQASet());
		Solution solutionFoo = kbm.createSolution("SolutionFoo");

		INode startNode = FF.createStartNode("Start_ID", "Start");
		INode endNode = FF.createEndNode("End_ID", "Ende", NoopAction.INSTANCE);

		List<QASet> qasets = new ArrayList<QASet>();
		qasets.add(questionYN);
		ActionIndication instantIndication = new ActionInstantIndication();
		instantIndication.setQASets(qasets);
		INode questionNode = FF.createActionNode("questionNode_ID", instantIndication);

		ActionHeuristicPS heuristicAction = new ActionHeuristicPS();
		heuristicAction.setScore(Score.P7);
		heuristicAction.setSolution(solutionFoo);
		INode solutionNode = FF.createActionNode("solutionNode_ID", heuristicAction);

		List<INode> nodesList = Arrays.asList(startNode, endNode, questionNode, solutionNode);

		// ---------------------------------

		IEdge startToQuestion = FF.createEdge("startToQuestionEdge_ID", startNode, questionNode,
				ConditionTrue.INSTANCE);

		Value yes = kbm.findValue(questionYN, "Yes");
		Condition yesCondition = new CondEqual(questionYN, yes);

		IEdge questionToSolution = FF.createEdge("questionToSolution_ID", questionNode,
				solutionNode, yesCondition);

		IEdge solutionToEnd = FF.createEdge("solutionToEnd_ID", solutionNode, endNode,
				ConditionTrue.INSTANCE);
		List<IEdge> edgesList = Arrays.asList(startToQuestion, questionToSolution, solutionToEnd);

		// ----------------------------------

		// Create the flowchart...
		Flow testFlow = FF.createFlow("testFlow_ID", "Main", nodesList, edgesList);

		DiaFluxUtils.addFlow(testFlow, kbm.getKnowledgeBase());

		return kbm.getKnowledgeBase();

	}

//	/**
//	 * 
//	 * @throws Exception
//	 * @created 12.11.2010
//	 */
//	@Test
//	public void testCase3() throws Exception {
//
//
//		for (String file : new File(".").list()) {
//			System.out.println(file);
//		}
//		KnowledgeBase base = PersistenceManager.getInstance().load(
//				new File("src/test/resources/Test3_kopic.jar"));
//		FlowSet flowSet = DiaFluxUtils.getFlowSet(base);
//
//		System.out.println(flowSet);
//
//	}

}
