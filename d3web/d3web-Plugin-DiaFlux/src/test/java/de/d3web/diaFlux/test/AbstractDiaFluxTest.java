/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowRun;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.plugin.test.InitPluginManager;

/**
 * 
 * @author Reinhard Hatko
 * @created 03.12.2010
 */
public abstract class AbstractDiaFluxTest {

	protected static final String PATH = "src/test/resources/";
	protected static final int TORTURE_LIMIT = 10;

	protected final String fileName;
	protected KnowledgeBase kb;
	protected Session session;

	protected QuestionOC quest1;
	protected QuestionOC quest2;
	protected QuestionOC quest3;
	protected QuestionOC quest4;
	protected QuestionOC quest5;

	protected final String answer1 = "Answ1";
	protected final String answer2 = "Answ2";
	protected final String answer3 = "Answ3";

	protected QuestionNum quest6;
	protected QuestionNum quest7;
	protected QuestionNum quest8;
	protected QuestionNum quest9;
	protected QuestionNum quest10;

	// Constants for suggested naming and ID scheme
	// Flow names
	protected final String Flow1 = "Flow1";
	protected final String Flow2 = "Flow2";
	protected final String Flow3 = "Flow3";
	protected final String Flow4 = "Flow4";

	// node ids
	protected final String start1 = "start1";
	protected final String start2 = "start2";
	protected final String start3 = "start3";

	protected final String exit1 = "exit1";
	protected final String exit2 = "exit2";
	protected final String exit3 = "exit3";

	protected final String snapshot1 = "snapshot1";
	protected final String snapshot2 = "snapshot2";
	protected final String snapshot3 = "snapshot3";

	protected final String composed1 = "composed1";
	protected final String composed2 = "composed2";
	protected final String composed3 = "composed3";

	protected final String comment1 = "comment1";
	protected final String comment2 = "comment2";
	protected final String comment3 = "comment3";

	protected final String nodeQ1 = "nodeQ1";
	protected final String nodeQ2 = "nodeQ2";
	protected final String nodeQ3 = "nodeQ3";
	protected final String nodeQ4 = "nodeQ4";
	protected final String nodeQ5 = "nodeQ5";
	protected final String nodeQ6 = "nodeQ6";
	protected final String nodeQ7 = "nodeQ7";
	protected final String nodeQ8 = "nodeQ8";
	protected final String nodeQ9 = "nodeQ9";
	protected final String nodeQ10 = "nodeQ10";

	protected final String nodeQ1_2 = nodeQ1 + "_2";
	protected final String nodeQ2_2 = nodeQ2 + "_2";
	protected final String nodeQ3_2 = nodeQ3 + "_2";
	protected final String nodeQ4_2 = nodeQ4 + "_2";
	protected final String nodeQ5_2 = nodeQ5 + "_2";
	protected final String nodeQ6_2 = nodeQ6 + "_2";
	protected final String nodeQ7_2 = nodeQ7 + "_2";
	protected final String nodeQ8_2 = nodeQ8 + "_2";
	protected final String nodeQ9_2 = nodeQ9 + "_2";
	protected final String nodeQ10_2 = nodeQ10 + "_2";

	AbstractDiaFluxTest(String fileName) {
		this.fileName = fileName;
	}

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();

		File file = new File(PATH + fileName);

		kb = PersistenceManager.getInstance().load(file);

		findQuestions();

		session = SessionFactory.createSession(kb);

	}

	private void findQuestions() {
		quest1 = (QuestionOC) kb.getManager().searchQuestion("QuestOC1");
		quest2 = (QuestionOC) kb.getManager().searchQuestion("QuestOC2");
		quest3 = (QuestionOC) kb.getManager().searchQuestion("QuestOC3");
		quest4 = (QuestionOC) kb.getManager().searchQuestion("QuestOC4");
		quest5 = (QuestionOC) kb.getManager().searchQuestion("QuestOC5");
		quest6 = (QuestionNum) kb.getManager().searchQuestion("QuestNum6");
		quest7 = (QuestionNum) kb.getManager().searchQuestion("QuestNum7");
		quest8 = (QuestionNum) kb.getManager().searchQuestion("QuestNum8");
		quest9 = (QuestionNum) kb.getManager().searchQuestion("QuestNum9");
		quest10 = (QuestionNum) kb.getManager().searchQuestion("QuestNum10");

	}

	protected void assertNodeStates(String flowName, String... activeIDs) {

		FlowSet flowSet = DiaFluxUtils.getFlowSet(session);

		Flow flow = flowSet.getByName(flowName);

		List<String> inactiveIDs = new ArrayList<String>();

		nextNode: for (Node node : flow.getNodes()) {
			String nodeID = node.getID();

			for (String id : activeIDs) {
				if (nodeID.equalsIgnoreCase(id)) continue nextNode;
			}

			// node id is not containd in active ids, so add it so inactive
			inactiveIDs.add(nodeID);

		}

		Assert.assertEquals(flow.getNodes().size(), inactiveIDs.size() + activeIDs.length);

		assertActiveNodes(flowName, activeIDs);
		assertInactiveNodes(flowName, inactiveIDs.toArray(new String[inactiveIDs.size()]));

	}

	protected void assertActiveNodes(String flowName, String... activeIDs) {

		FlowSet flowSet = DiaFluxUtils.getFlowSet(session);

		Flow flow = flowSet.getByName(flowName);

		nextNode: for (Node node : flow.getNodes()) {
			boolean supported = isSupported(node);

			for (String id : activeIDs) {
				if (node.getID().equalsIgnoreCase(id)) {

					if (!supported) {
						failWithUnsupportedNode(flowName, id);

					}
					continue nextNode;
				}
			}

		}

	}

	private boolean isSupported(Node node) {
		List<FlowRun> runs = DiaFluxUtils.getDiaFluxCaseObject(session).getRuns();
		for (FlowRun run : runs) {
			if (run.isActive(node)) {
				return true;
			}
		}
		return false;
	}

	private void failWithUnsupportedNode(String flowName, String id) {
		FlowSet flowSet = DiaFluxUtils.getFlowSet(session);

		Flow flow = flowSet.getByName(flowName);
		System.out.print("Supported Nodes in '" + flowName + "': ");
		Set<Node> nodes = new HashSet<Node>();
		for (FlowRun run : DiaFluxUtils.getDiaFluxCaseObject(session).getRuns()) {
			for (Node node : run.getActiveNodes()) {
				nodes.add(node);
			}
		}
		for (Node node : flow.getNodes()) {
			System.out.print(node.getID() + ", ");

		}
		System.out.println();

		Assert.fail("Node '" + id + "' must be active.");

	}

	protected void assertInactiveNodes(String flowName, String... inactiveIDs) {

		FlowSet flowSet = DiaFluxUtils.getFlowSet(session);

		Flow flow = flowSet.getByName(flowName);

		nextNode: for (Node node : flow.getNodes()) {
			boolean supported = isSupported(node);

			for (String id : inactiveIDs) {
				if (node.getID().equalsIgnoreCase(id)) {

					Assert.assertFalse("Node '" + id + "' must be inactive.", supported);
					continue nextNode;
				}
			}

		}

	}

	protected void testNumValue(QuestionNum num, double expected) {
		double actual = ((NumValue) session.getBlackboard().getValue(num)).getDouble().doubleValue();

		Assert.assertEquals("Question '" + num.getName() + "' does not have the expected value.",
				expected, actual, 0.005);

	}

	protected void setChoiceValue(QuestionOC question, String answerName) {
		System.out.println("Setting '" + question.getName() + "' to '" + answerName + "'.");

		Choice choice = KnowledgeBaseUtils.findChoice(question, answerName);

		Fact fact = FactFactory.createUserEnteredFact(question, new ChoiceValue(choice));

		try {
			Thread.sleep(2);
			session.getBlackboard().addValueFact(fact);
			Thread.sleep(2);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}