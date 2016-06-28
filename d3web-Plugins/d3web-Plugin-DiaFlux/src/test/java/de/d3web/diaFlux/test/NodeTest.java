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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.ConditionTrue;
import de.d3web.diaFlux.flow.AbstractNode;
import de.d3web.diaFlux.flow.DefaultEdge;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowFactory;
import de.d3web.diaFlux.flow.Node;

/**
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 11.11.2010
 */
public class NodeTest {

	/**
	 * Because Node is abstract, we need this Mock to instantiate a plain Node;
	 * 
	 * @author Marc-Oliver Ochlast (denkbares GmbH)
	 * @created 11.11.2010
	 */
	private static class NodeMock extends AbstractNode {

		public NodeMock(String id, String name) {
			super(id, name);
		}

		@Override
		protected boolean addOutgoingEdge(Edge edge) {
			return super.addOutgoingEdge(edge);
		}

		@Override
		protected boolean addIncomingEdge(Edge edge) {
			return super.addIncomingEdge(edge);
		}
	}

	private NodeMock testSubject;

	private NodeMock incomingNode1, incomingNode2;
	private NodeMock outgoingNode1, outgoingNode2;

	private DefaultEdge incomingEdge1, incomingEdge2;
	private DefaultEdge outgoingEdge1, outgoingEdge2;

	/**
	 * 
	 * @created 11.11.2010
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// create test subject
		testSubject = new NodeMock("nodeID", "nodeName");
		// create incoming nodes
		incomingNode1 = new NodeMock("incomingNode1", "incomingNode1");
		incomingNode2 = new NodeMock("incomingNode2", "incomingNode2");
		// create outgoing nodes
		outgoingNode1 = new NodeMock("outgoingNode1", "outgoingNode1");
		outgoingNode2 = new NodeMock("outgoingNode2", "outgoingNode2");
		// create incoming edges
		incomingEdge1 = (DefaultEdge) FlowFactory.createEdge("incomingEdge1", incomingNode1,
				testSubject, ConditionTrue.INSTANCE);
		incomingEdge2 = (DefaultEdge) FlowFactory.createEdge("incomingEdge2", incomingNode2,
				testSubject, ConditionTrue.INSTANCE);
		// create outgoing edges
		outgoingEdge1 = (DefaultEdge) FlowFactory.createEdge("outgoingEdge1", testSubject,
				outgoingNode1, ConditionTrue.INSTANCE);
		outgoingEdge2 = (DefaultEdge) FlowFactory.createEdge("outgoingEdge2", testSubject,
				outgoingNode2, ConditionTrue.INSTANCE);
	}

	/**
	 * Test method for {@link de.d3web.diaFlux.flow.AbstractNode#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		assertThat(testSubject.hashCode(), is(not(0)));
	}

	/**
	 * Test that {@link AbstractNode#addOutgoingEdge(Edge)} throws a
	 * IllegalArgumentException when IEdge parameter is null
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddOutgoingEdgeThrowsIllegalArgumentExceptionWhenParameterIsNull() {
		testSubject.addOutgoingEdge(null);
	}

	/**
	 * Test that {@link AbstractNode#addIncomingEdge(Edge)} throws a
	 * IllegalArgumentException when IEdge parameter is null
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddIncomingEdgeThrowsIllegalArgumentExceptionWhenParameterIsNull() {
		testSubject.addIncomingEdge(null);
	}

	/**
	 * Test that {@link AbstractNode#addOutgoingEdge(Edge)} throws a
	 * IllegalArgumentException when this node is not the start-node of the
	 * IEdge given via parameter
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddOutgoingEdgeThrowsIllegalArgumentExceptionWhenStartNodeIsInvalid() {
		Edge edge = new DefaultEdge("invalidEdge", outgoingNode1, incomingNode1,
				ConditionTrue.INSTANCE);
		incomingNode1.addOutgoingEdge(edge);
	}

	/**
	 * Test that {@link AbstractNode#addIncomingEdge(Edge)} throws a
	 * IllegalArgumentException when this node is not the end-node of the IEdge
	 * given via parameter
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddIncomingEdgeThrowsIllegalArgumentExceptionWhenStartNodeIsInvalid() {
		Edge edge = new DefaultEdge("invalidEdge", outgoingNode1, incomingNode1,
				ConditionTrue.INSTANCE);
		outgoingNode1.addIncomingEdge(edge);
	}

	/**
	 * Test method for {@link AbstractNode#getOutgoingEdges()}.
	 */
	@Test
	public void testGetOutgoingEdges() {
		List<Edge> edges = testSubject.getOutgoingEdges();
		assertThat(edges.contains(incomingEdge1), is(false));
		assertThat(edges.contains(incomingEdge2), is(false));
		assertThat(edges.contains(outgoingEdge1), is(true));
		assertThat(edges.contains(outgoingEdge2), is(true));
	}

	/**
	 * Test method for {@link AbstractNode#getIncomingEdges()}.
	 */
	@Test
	public void testGetIncomingEdges() {
		List<Edge> edges = testSubject.getIncomingEdges();
		assertThat(edges.contains(incomingEdge1), is(true));
		assertThat(edges.contains(incomingEdge2), is(true));
		assertThat(edges.contains(outgoingEdge1), is(false));
		assertThat(edges.contains(outgoingEdge2), is(false));
	}

	/**
	 * Test method for {@link AbstractNode#getFlow()} and
	 * {@link AbstractNode#setFlow()}.
	 */
	// @Test
	public void testSetAndGetFlow() {
		// TODO fix this
		List<Node> nodeList = Arrays.asList((Node) incomingNode1,
				(Node) incomingNode2, (Node) outgoingNode1, (Node) outgoingNode2);
		List<Edge> edgeList = Arrays.asList((Edge) incomingEdge1, (Edge) incomingEdge2,
				(Edge) outgoingEdge1, (Edge) outgoingEdge2);
		Flow testFlow = FlowFactory.createFlow(null, "Main", nodeList, edgeList);
		testFlow.setAutostart(true);

		assertThat(testSubject.getFlow(), is(nullValue()));
		testSubject.setFlow(testFlow);
		assertThat(testSubject.getFlow(), is(equalTo(testFlow)));
	}

	/**
	 * Test method for {@link de.d3web.diaFlux.flow.AbstractNode#getName()}.
	 */
	@Test
	public void testGetName() {
		assertThat(testSubject.getName(), is(equalTo("nodeName")));
	}

	/**
	 * Test method for
	 * {@link de.d3web.diaFlux.flow.AbstractNode#createSessionObject(de.d3web.core.session.Session)}
	 * .
	 */
	@Test
	public void testCreateCaseObject() {
		// TODO
	}

	/**
	 * Test method for {@link de.d3web.diaFlux.flow.AbstractNode#getID()}.
	 */
	@Test
	public void testGetID() {
		assertThat(testSubject.getID(), is(equalTo("nodeID")));
	}

	/**
	 * Test method for
	 * {@link de.d3web.diaFlux.flow.AbstractNode#takeSnapshot(de.d3web.core.session.Session)}
	 * .
	 */
	@Test
	public void testTakeSnapshot() {
		// TODO
	}

	/**
	 * Test method for
	 * {@link de.d3web.diaFlux.flow.AbstractNode#resetNodeData(de.d3web.core.session.Session)}
	 * .
	 */
	@Test
	public void testResetNodeData() {
		// TODO
	}

	/**
	 * Test method for
	 * {@link de.d3web.diaFlux.flow.AbstractNode#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		assertThat(testSubject.equals(testSubject), is(true));
		assertThat(testSubject.equals(null), is(false));
		assertThat(testSubject.equals(new Object()), is(false));

		// TODO
	}

	/**
	 * Test method for {@link de.d3web.diaFlux.flow.AbstractNode#toString()}.
	 */
	@Test
	public void testToString() {
		assertThat(testSubject.toString(), is(equalTo("NodeMock[nodeID, nodeName]")));
	}

}
