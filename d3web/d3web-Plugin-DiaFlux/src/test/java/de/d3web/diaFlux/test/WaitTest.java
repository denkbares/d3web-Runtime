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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.d3web.abstraction.ActionSetValue;
import de.d3web.abstraction.formula.FormulaNumber;
import de.d3web.abstraction.formula.Operator;
import de.d3web.abstraction.formula.Operator.Operation;
import de.d3web.abstraction.formula.QNumWrapper;
import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.CondNumLessEqual;
import de.d3web.core.inference.condition.ConditionTrue;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.values.NumValue;
import de.d3web.diaFlux.flow.ActionNode;
import de.d3web.diaFlux.flow.CommentNode;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowFactory;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.SnapshotNode;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.diaFlux.inference.NodeActiveCondition;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Creates a "wait" Subtree
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 18.11.2010
 */
public class WaitTest {

	private KnowledgeBase kb;
	private QuestionNum time;

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		time = new QuestionNum(kb.getRootQASet(), "Time");
		// time.getInfoStore().addValue(BasicProperties.INIT, "0");
		// creating wait as a FlowChart
		// starting with the nodes
		Node waitA = new StartNode("Wait_A", "Wait_A");
		Node waitB = new StartNode("Wait_B", "Wait_B");
		Node snapshotA = new SnapshotNode("SS_A", "SS_A");
		Node snapshotB = new SnapshotNode("SS_B", "SS_B");
		ActionSetValue action = new ActionSetValue();
		action.setQuestion(time);
		QNumWrapper qnumWrapper = new QNumWrapper(time);
		action.setValue(new Operator(qnumWrapper, new FormulaNumber(10.0), Operation.Add));
		Node timeSetter = new ActionNode("TimeSetter", action);
		Node waitComment = new CommentNode("WaitComment", "WaitComment");
		Node endWait = new EndNode("EndWait", "EndWait");
		List<Node> waitNodes = Arrays.asList(waitA, waitB, snapshotA, snapshotB, timeSetter,
				waitComment, endWait);
		// and now the edges
		Edge e1 = FlowFactory.createEdge("e1", waitA, snapshotA, ConditionTrue.INSTANCE);
		Edge e2 = FlowFactory.createEdge("e2", waitB, snapshotB, ConditionTrue.INSTANCE);
		Edge e3 = FlowFactory.createEdge("e3", snapshotA, timeSetter, ConditionTrue.INSTANCE);
		Edge e4 = FlowFactory.createEdge("e4", timeSetter, waitComment, ConditionTrue.INSTANCE);
		Edge e5 = FlowFactory.createEdge("e5", snapshotB, waitComment, ConditionTrue.INSTANCE);
		Edge e6 = FlowFactory.createEdge("e6", waitComment, endWait, new CondNumGreater(time, 5.0));
		List<Edge> waitEdges = Arrays.asList(e1, e2, e3, e4, e5, e6);
		// create Flow and add it to the kb
		FlowFactory.createFlow(kb, "waitFlow", waitNodes, waitEdges);

		// creating the loop flowchart
		Node loopStart = new StartNode("loopstart", "loopstart");
		Node dummyStartLoop = new CommentNode("dummyStartLoop", "dummyStartLoop");
		Node loopComposed = new ComposedNode("ComposedLoop", "waitFlow", "Wait_B");
		Edge e7 = FlowFactory.createEdge("e7", loopStart, dummyStartLoop, ConditionTrue.INSTANCE);
		Edge e8 = FlowFactory.createEdge("e8", loopComposed, dummyStartLoop,
				new NodeActiveCondition(
						"waitFlow",
						"EndWait"));
		Edge e15 = FlowFactory.createEdge("e15", dummyStartLoop, loopComposed,
				ConditionTrue.INSTANCE);
		FlowFactory.createFlow(kb, "loopFlow",
				Arrays.asList(loopStart, loopComposed, dummyStartLoop), Arrays.asList(e7, e8, e15));

		// creating the primary flow
		Node primaryStart = new StartNode("startPrimary", "startPrimary");
		Node join = new CommentNode("Join", "Join");
		Node primaryComposed = new ComposedNode("ComposedLoop", "waitFlow", "Wait_A");
		Node split = new CommentNode("split", "split");
		Node primaryEnd = new EndNode("endPrimary", "endPrimary");

		Edge e9 = FlowFactory.createEdge("e9", primaryStart, join, ConditionTrue.INSTANCE);
		Edge e10 = FlowFactory.createEdge("e10", primaryComposed, split, new NodeActiveCondition(
				"waitFlow", "EndWait"));
		Edge e11 = FlowFactory.createEdge("e11", split, join, new CondNumLessEqual(
				time,
				15.0));
		Edge e12 = FlowFactory.createEdge("e12", split, primaryEnd, new CondNumGreater(time, 15.0));
		Edge e16 = FlowFactory.createEdge("e16", join, primaryComposed, ConditionTrue.INSTANCE);
		FlowFactory.createFlow(kb, "primaryFlow",
				Arrays.asList(primaryStart, primaryComposed, split, primaryEnd,
						join),
				Arrays.asList(e9, e10, e11, e12, e16));

		// creating global flow
		Node start = new StartNode("Start", "Start");
		Node start2 = new StartNode("Start2", "Start2");
		ActionSetValue actionSetValue = new ActionSetValue();
		actionSetValue.setQuestion(time);
		actionSetValue.setValue(new NumValue(0));

		Node setTime = new ActionNode("setTime", actionSetValue);
		Node primary = new ComposedNode("primary", "primaryFlow", "startPrimary");
		Node loop = new ComposedNode("loop", "loopFlow", "loopstart");
		Edge e13 = FlowFactory.createEdge("e13", start, setTime, ConditionTrue.INSTANCE);
		Edge e17 = FlowFactory.createEdge("e17", setTime, primary, ConditionTrue.INSTANCE);
		Edge e14 = FlowFactory.createEdge("e14", start2, loop, ConditionTrue.INSTANCE);
		Flow global = new Flow(kb, "Main",
				Arrays.asList(start, primary, loop, setTime, start2),
				Arrays.asList(e13, e14, e17));
		global.setAutostart(true);
	}

	@Test
	public void test() {
		Session session = SessionFactory.createSession(kb);
		// commit some times due to cycle elemination (each cycle is propagated
		// not more than once per propagation frame
		long now = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			session.getPropagationManager().openPropagation(now + i);
			session.getPropagationManager().commitPropagation();
		}
		NumValue value = (NumValue) session.getBlackboard().getValue(time);
		Assert.assertEquals(20.0, value.getDouble().doubleValue(), 0.001);
	}

}
