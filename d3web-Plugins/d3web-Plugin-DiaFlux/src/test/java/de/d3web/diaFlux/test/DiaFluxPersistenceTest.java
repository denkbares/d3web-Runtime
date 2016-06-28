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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.ConditionTrue;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.diaFlux.flow.ActionNode;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowFactory;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.diaFlux.inference.DiaFluxUtils;
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

	private KnowledgeBase kb;
	private Session session;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();

		File file = new File("target/test.zip");
		KnowledgeBase createdKB = createTestKB();
		PersistenceManager.getInstance().save(createdKB, file);

		kb = PersistenceManager.getInstance().load(file);
		session = SessionFactory.createSession(kb);

		FlowSet loadedFlowSet = DiaFluxUtils.getFlowSet(kb);
		FlowSet createdFlowSet = DiaFluxUtils.getFlowSet(createdKB);

		System.out.println("Created: " + createdFlowSet);
		System.out.println();
		System.out.println("Loaded: " + loadedFlowSet);
	}

	@Test
	public void testFluxSolver() {
		Question question = kb.getManager().searchQuestion("YesNoQuestion");
		Solution solution = kb.getManager().searchSolution("SolutionFoo");

		Rating solutionState = session.getBlackboard().getRating(solution);
		assertTrue("Solution has wrong state. Expected 'UNCLEAR'",
				solutionState.hasState(Rating.State.UNCLEAR));// this is true

		// Answer question with "Yes", this should execute the flow
		Value yes = KnowledgeBaseUtils.findValue(question, "Yes");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(question,
						yes, PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
		solutionState = session.getBlackboard().getRating(solution);
		assertTrue("Solution has wrong state. Expected 'ESTABLISHED'",
				solutionState.hasState(Rating.State.ESTABLISHED));

		// When Answer "No" is set, the establishment of the solution
		// should be retracted:
		Value no = KnowledgeBaseUtils.findValue(question, "No");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(question,
						no, PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
		solutionState = session.getBlackboard().getRating(solution);
		assertTrue("Solution has wrong state. Expected 'UNCLEAR'",
				solutionState.hasState(Rating.State.UNCLEAR));
	}

	public static KnowledgeBase createTestKB() {
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();

		Question questionYN = new QuestionYN(kb.getRootQASet(), "YesNoQuestion");
		Solution solutionFoo = new Solution(kb.getRootSolution(), "SolutionFoo");

		Node startNode = new StartNode("Start_ID", "Start");
		Node endNode = new EndNode("End_ID", "Ende");

		List<QASet> qasets = new ArrayList<>();
		qasets.add(questionYN);
		ActionInstantIndication instantIndication = new ActionInstantIndication();
		instantIndication.setQASets(qasets);
		Node questionNode = new ActionNode("questionNode_ID", instantIndication);

		ActionHeuristicPS heuristicAction = new ActionHeuristicPS();
		heuristicAction.setScore(Score.P7);
		heuristicAction.setSolution(solutionFoo);
		Node solutionNode = new ActionNode("solutionNode_ID", heuristicAction);

		List<Node> nodesList = Arrays.asList(startNode, endNode, questionNode, solutionNode);

		// ---------------------------------

		Edge startToQuestion = FlowFactory.createEdge("startToQuestionEdge_ID", startNode,
				questionNode,
				ConditionTrue.INSTANCE);

		Value yes = KnowledgeBaseUtils.findValue(questionYN, "Yes");
		Condition yesCondition = new CondEqual(questionYN, yes);

		Edge questionToSolution = FlowFactory.createEdge("questionToSolution_ID", questionNode,
				solutionNode, yesCondition);

		Edge solutionToEnd = FlowFactory.createEdge("solutionToEnd_ID", solutionNode, endNode,
				ConditionTrue.INSTANCE);
		List<Edge> edgesList = Arrays.asList(startToQuestion, questionToSolution, solutionToEnd);

		// ----------------------------------

		// Create the flowchart...
		Flow testFlow = FlowFactory.createFlow(kb, "Main", nodesList, edgesList);
		testFlow.setAutostart(true);

		// add a property to Infostore, to test its persistence
		testFlow.getInfoStore().addValue(MMInfo.DESCRIPTION,
				"infostoretestvalue");

		return kb;

	}

	@Test
	public void testInfoStorePersistence() {
		InfoStore infoStore = DiaFluxUtils.getFlowSet(kb).get("Main").getInfoStore();

		Assert.assertTrue("Infostore does not contain a description",
				infoStore.contains(MMInfo.DESCRIPTION));
		Assert.assertEquals("infostoretestvalue", infoStore.getValue(MMInfo.DESCRIPTION));

	}

}
