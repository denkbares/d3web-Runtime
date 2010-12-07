/*
 * Copyright (C) 2010 denkbares GmbH
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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.abstraction.ActionSetValue;
import de.d3web.abstraction.formula.FormulaNumber;
import de.d3web.abstraction.formula.Operator;
import de.d3web.abstraction.formula.Operator.Operation;
import de.d3web.abstraction.formula.QNumWrapper;
import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.CondNumLessEqual;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowFactory;
import de.d3web.diaFlux.flow.IEdge;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.inference.ConditionTrue;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.NodeActiveCondition;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Creates a "wait" Subtree
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 18.11.2010
 */
public class WaitTest {

	private KnowledgeBaseManagement kbm;
	private static final FlowFactory FF = FlowFactory.getInstance();
	private QuestionNum time;

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();
		kbm = KnowledgeBaseManagement.createInstance();
		time = kbm.createQuestionNum("Time", kbm.getKnowledgeBase().getRootQASet());
		time.getInfoStore().addValue(BasicProperties.INIT, "0");
		// creating wait as a FlowChart
		// starting with the nodes
		INode waitA = FF.createStartNode("Wait_A", "Wait_A");
		INode waitB = FF.createStartNode("Wait_B", "Wait_B");
		INode snapshotA = FF.createSnapshotNode("SS_A", "SS_A");
		INode snapshotB = FF.createSnapshotNode("SS_B", "SS_B");
		ActionSetValue action = new ActionSetValue();
		action.setQuestion(time);
		QNumWrapper qnumWrapper = new QNumWrapper(time);
		action.setValue(new Operator(qnumWrapper, new FormulaNumber(10.0), Operation.Add));
		INode timeSetter = FF.createActionNode("TimeSetter", action);
		INode waitComment = FF.createCommentNode("WaitComment", "WaitComment");
		INode endWait = FF.createEndNode("EndWait", "EndWait");
		List<INode> waitNodes = Arrays.asList(waitA, waitB, snapshotA, snapshotB, timeSetter,
				waitComment, endWait);
		// and now the edges
		IEdge e1 = FF.createEdge("e1", waitA, snapshotA, ConditionTrue.INSTANCE);
		IEdge e2 = FF.createEdge("e2", waitB, snapshotB, ConditionTrue.INSTANCE);
		IEdge e3 = FF.createEdge("e3", snapshotA, timeSetter, ConditionTrue.INSTANCE);
		IEdge e4 = FF.createEdge("e4", timeSetter, waitComment, ConditionTrue.INSTANCE);
		IEdge e5 = FF.createEdge("e5", snapshotB, waitComment, ConditionTrue.INSTANCE);
		IEdge e6 = FF.createEdge("e6", waitComment, endWait, new CondNumGreater(time, 5.0));
		List<IEdge> waitEdges = Arrays.asList(e1, e2, e3, e4, e5, e6);
		// create Flow and add it to the kb
		Flow waitFlow = FF.createFlow("waitFlow", "waitFlow", waitNodes, waitEdges);
		DiaFluxUtils.addFlow(waitFlow, kbm.getKnowledgeBase());

		// creating the loop flowchart
		INode loopStart = FF.createStartNode("loopstart", "loopstart");
		INode dummyStartLoop = FF.createCommentNode("dummyStartLoop", "dummyStartLoop");
		INode loopComposed = FF.createComposedNode("ComposedLoop", "waitFlow", "Wait_B");
		IEdge e7 = FF.createEdge("e7", loopStart, dummyStartLoop, ConditionTrue.INSTANCE);
		IEdge e8 = FF.createEdge("e8", loopComposed, dummyStartLoop, new NodeActiveCondition(
				"waitFlow",
				"EndWait"));
		IEdge e15 = FF.createEdge("e15", dummyStartLoop, loopComposed, ConditionTrue.INSTANCE);
		Flow loopFlow = FF.createFlow("loopFlow", "loopFlow",
				Arrays.asList(loopStart, loopComposed, dummyStartLoop), Arrays.asList(e7, e8, e15));
		DiaFluxUtils.addFlow(loopFlow, kbm.getKnowledgeBase());

		// creating the primary flow
		INode primaryStart = FF.createStartNode("startPrimary", "startPrimary");
		INode dummyStartPrimary = FF.createCommentNode("dummyStartPrimary", "dummyStartPrimary");
		INode primaryComposed = FF.createComposedNode("ComposedLoop", "waitFlow", "Wait_A");
		INode primaryComment = FF.createCommentNode("primaryComment", "primaryComment");
		INode primaryEnd = FF.createEndNode("endPrimary", "endPrimary");
		IEdge e9 = FF.createEdge("e9", primaryStart, dummyStartPrimary, ConditionTrue.INSTANCE);
		IEdge e10 = FF.createEdge("e10", primaryComposed, primaryComment, new NodeActiveCondition(
				"waitFlow", "EndWait"));
		IEdge e11 = FF.createEdge("e11", primaryComment, dummyStartPrimary, new CondNumLessEqual(
				time,
				15.0));
		IEdge e12 = FF.createEdge("e12", primaryComment, primaryEnd, new CondNumGreater(time, 15.0));
		IEdge e16 = FF.createEdge("e16", dummyStartPrimary, primaryComposed, ConditionTrue.INSTANCE);
		Flow primaryFlow = FF.createFlow("primaryFlow", "primaryFlow",
				Arrays.asList(primaryStart, primaryComposed, primaryComment, primaryEnd,
						dummyStartPrimary),
				Arrays.asList(e9, e10, e11, e12, e16));
		DiaFluxUtils.addFlow(primaryFlow, kbm.getKnowledgeBase());

		// creating global flow
		INode start = FF.createStartNode("Start", "Start");
		INode primary = FF.createComposedNode("primary", "primaryFlow", "startPrimary");
		INode loop = FF.createComposedNode("loop", "loopFlow", "loopstart");
		IEdge e13 = FF.createEdge("e13", start, primary, ConditionTrue.INSTANCE);
		IEdge e14 = FF.createEdge("e14", start, loop, ConditionTrue.INSTANCE);
		Flow global = FF.createFlow("Main", "Main", Arrays.asList(start, primary, loop),
				Arrays.asList(e13, e14));
		global.setAutostart(true);
		DiaFluxUtils.addFlow(global, kbm.getKnowledgeBase());
	}

	@Test
	public void test() {
		Session session = SessionFactory.createSession(kbm.getKnowledgeBase());
		System.out.println(session.getBlackboard().getValue(time));
	}

}
