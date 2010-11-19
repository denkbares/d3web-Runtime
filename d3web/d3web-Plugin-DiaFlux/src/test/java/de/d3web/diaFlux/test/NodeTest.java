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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowFactory;
import de.d3web.diaFlux.flow.IEdge;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.inference.ConditionTrue;

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
	private class NodeMock extends Node {

		public NodeMock(String id, String name) {
			super(id, name);
		}

		@Override
		protected boolean addOutgoingEdge(IEdge edge) {
			return super.addOutgoingEdge(edge);
		}

		@Override
		protected boolean addIncomingEdge(IEdge edge) {
			return super.addIncomingEdge(edge);
		}
	}

	private static final FlowFactory FF = FlowFactory.getInstance();

	private NodeMock testSubject;

	private NodeMock incomingNode1, incomingNode2;
	private NodeMock outgoingNode1, outgoingNode2;

	private Edge incomingEdge1, incomingEdge2;
	private Edge outgoingEdge1, outgoingEdge2;

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
		incomingEdge1 = (Edge) FF.createEdge("incomingEdge1", incomingNode1,
				testSubject, ConditionTrue.INSTANCE);
		incomingEdge2 = (Edge) FF.createEdge("incomingEdge2", incomingNode2,
				testSubject, ConditionTrue.INSTANCE);
		// create outgoing edges
		outgoingEdge1 = (Edge) FF.createEdge("outgoingEdge1", testSubject,
				outgoingNode1, ConditionTrue.INSTANCE);
		outgoingEdge2 = (Edge) FF.createEdge("outgoingEdge2", testSubject,
				outgoingNode2, ConditionTrue.INSTANCE);
	}

	/**
	 * Test method for {@link de.d3web.diaFlux.flow.Node#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		assertThat(testSubject.hashCode(), is(not(0)));
	}

	/**
	 * Test that {@link Node#addOutgoingEdge(IEdge)} throws a
	 * IllegalArgumentException when IEdge parameter is null
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddOutgoingEdgeThrowsIllegalArgumentExceptionWhenParameterIsNull() {
		testSubject.addOutgoingEdge(null);
	}

	/**
	 * Test that {@link Node#addIncomingEdge(IEdge)} throws a
	 * IllegalArgumentException when IEdge parameter is null
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddIncomingEdgeThrowsIllegalArgumentExceptionWhenParameterIsNull() {
		testSubject.addIncomingEdge(null);
	}

	/**
	 * Test that {@link Node#addOutgoingEdge(IEdge)} throws a
	 * IllegalArgumentException when this node is not the start-node of the
	 * IEdge given via parameter
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddOutgoingEdgeThrowsIllegalArgumentExceptionWhenStartNodeIsInvalid() {
		IEdge edge = new Edge("invalidEdge", outgoingNode1, incomingNode1, ConditionTrue.INSTANCE);
		incomingNode1.addOutgoingEdge(edge);
	}

	/**
	 * Test that {@link Node#addIncomingEdge(IEdge)} throws a
	 * IllegalArgumentException when this node is not the end-node of the IEdge
	 * given via parameter
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddIncomingEdgeThrowsIllegalArgumentExceptionWhenStartNodeIsInvalid() {
		IEdge edge = new Edge("invalidEdge", outgoingNode1, incomingNode1, ConditionTrue.INSTANCE);
		outgoingNode1.addIncomingEdge(edge);
	}

	/**
	 * Test method for {@link Node#getOutgoingEdges()}.
	 */
	@Test
	public void testGetOutgoingEdges() {
		List<IEdge> edges = testSubject.getOutgoingEdges();
		assertThat(edges.contains(incomingEdge1), is(false));
		assertThat(edges.contains(incomingEdge2), is(false));
		assertThat(edges.contains(outgoingEdge1), is(true));
		assertThat(edges.contains(outgoingEdge2), is(true));
	}

	/**
	 * Test method for {@link Node#getIncomingEdges()}.
	 */
	@Test
	public void testGetIncomingEdges() {
		List<IEdge> edges = testSubject.getIncomingEdges();
		assertThat(edges.contains(incomingEdge1), is(true));
		assertThat(edges.contains(incomingEdge2), is(true));
		assertThat(edges.contains(outgoingEdge1), is(false));
		assertThat(edges.contains(outgoingEdge2), is(false));
	}

	/**
	 * Test method for {@link Node#getFlow()} and {@link Node#setFlow()}.
	 */
	// @Test
	public void testSetAndGetFlow() {
		// TODO fix this
		List<INode> nodeList = Arrays.asList((INode) incomingNode1,
				(INode) incomingNode2, (INode) outgoingNode1, (INode) outgoingNode2);
		List<IEdge> edgeList = Arrays.asList((IEdge) incomingEdge1, (IEdge) incomingEdge2,
				(IEdge) outgoingEdge1, (IEdge) outgoingEdge2);
		Flow testFlow = FF.createFlow("testFlow_ID", "Main", nodeList, edgeList);

		assertThat(testSubject.getFlow(), is(nullValue()));
		testSubject.setFlow(testFlow);
		assertThat(testSubject.getFlow(), is(equalTo(testFlow)));
	}

	/**
	 * Test method for {@link de.d3web.diaFlux.flow.Node#getName()}.
	 */
	@Test
	public void testGetName() {
		assertThat(testSubject.getName(), is(equalTo("nodeName")));
	}

	/**
	 * Test method for {@link de.d3web.diaFlux.flow.Node#createCaseObject(de.d3web.core.session.Session)}.
	 */
	@Test
	public void testCreateCaseObject() {
		// TODO
	}

	/**
	 * Test method for {@link de.d3web.diaFlux.flow.Node#getID()}.
	 */
	@Test
	public void testGetID() {
		assertThat(testSubject.getID(), is(equalTo("nodeID")));
	}

	/**
	 * Test method for {@link de.d3web.diaFlux.flow.Node#takeSnapshot(de.d3web.core.session.Session, de.d3web.diaFlux.flow.SnapshotNode, java.util.List)}.
	 */
	@Test
	public void testTakeSnapshot() {
		// TODO
	}

	/**
	 * Test method for {@link de.d3web.diaFlux.flow.Node#resetNodeData(de.d3web.core.session.Session)}.
	 */
	@Test
	public void testResetNodeData() {
		// TODO
	}

	/**
	 * Test method for {@link de.d3web.diaFlux.flow.Node#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		assertThat(testSubject.equals(testSubject), is(true));
		assertThat(testSubject.equals(null), is(false));
		assertThat(testSubject.equals(new Object()), is(false));

		// TODO
	}

	/**
	 * Test method for {@link de.d3web.diaFlux.flow.Node#toString()}.
	 */
	@Test
	public void testToString() {
		assertThat(testSubject.toString(), is(equalTo("NodeMock[nodeID, nodeName]")));
	}

}
