/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.diaFlux.inference.DiaFluxUtils;

public class DiaFluxUtilsTest extends AbstractDiaFluxTest {

	private static final String FILE = "SnapshotSequenceInSubFlowTest.d3web";

	public DiaFluxUtilsTest() {
		super(FILE);
	}

	@Test
	public void areConnectedNodes() {
		assertTrue(DiaFluxUtils.areConnectedNodes(
				DiaFluxUtils.findStartNode(kb, "Flow1", "Start"),
				DiaFluxUtils.findExitNode(kb, "Flow1", "Exit")));

		assertFalse(DiaFluxUtils.areConnectedNodes(
				DiaFluxUtils.findExitNode(kb, "Flow1", "Exit"),
				DiaFluxUtils.findStartNode(kb, "Flow1", "Start")));
	}

	@Test
	public void findFlow() {
		assertNull(DiaFluxUtils.findFlow(kb, "Flow25"));
		assertNull(DiaFluxUtils.findFlow(KnowledgeBaseUtils.createKnowledgeBase(), "Flow25"));
	}

	@Test
	public void findNode() {
		assertNull(DiaFluxUtils.findStartNode(kb, "Flow1", "Start25"));
	}

	@Test
	public void getCalledStartNode() {
		ComposedNode composedNode = DiaFluxUtils.findFlow(kb, "Flow1").getNodesOfClass(
				ComposedNode.class).iterator().next();
		StartNode startNode = DiaFluxUtils.findStartNode(kb, "Flow3", "start1");
		assertEquals(startNode, DiaFluxUtils.getCalledStartNode(composedNode));
	}

	@Test
	public void getCalledFlow() {
		ComposedNode composedNode = DiaFluxUtils.findFlow(kb, "Flow1").getNodesOfClass(
				ComposedNode.class).iterator().next();
		Flow calledFlow = DiaFluxUtils.findFlow(kb, "Flow3");
		assertEquals(calledFlow, DiaFluxUtils.getCalledFlow(kb, composedNode));
	}

	@Test
	public void getCallingNodes() {
		ComposedNode composedNode3 = DiaFluxUtils.findFlow(kb, "Flow3").getNodesOfClass(
				ComposedNode.class).iterator().next();
		ComposedNode composedNode4 = DiaFluxUtils.findFlow(kb, "Flow4").getNodesOfClass(
				ComposedNode.class).iterator().next();
		assertEquals(Arrays.asList(composedNode3, composedNode4),
				DiaFluxUtils.getCallingNodes(kb, DiaFluxUtils.findFlow(kb, "Flow2")));
	}

	@Test
	public void getCallingNodes2() {
		ComposedNode composedNode3 = DiaFluxUtils.findFlow(kb, "Flow3").getNodesOfClass(
				ComposedNode.class).iterator().next();
		ComposedNode composedNode4 = DiaFluxUtils.findFlow(kb, "Flow4").getNodesOfClass(
				ComposedNode.class).iterator().next();
		assertEquals(Arrays.asList(composedNode3, composedNode4),
				DiaFluxUtils.getCallingNodes(kb, DiaFluxUtils.findStartNode(kb, "Flow2", "start1")));
	}

	@Test
	public void getAutoStartNodes() {
		List<StartNode> autostartNodes = DiaFluxUtils.getAutostartNodes(kb);
		assertEquals(1, autostartNodes.size());
		assertEquals(DiaFluxUtils.findStartNode(kb, "Flow1", "Start"), autostartNodes.get(0));
	}

	@Test
	public void createFlowStructure() {
		Map<Flow, Collection<ComposedNode>> flowStructure = DiaFluxUtils.createFlowStructure(kb);
		Collection<ComposedNode> composedNodes = flowStructure.get(DiaFluxUtils.findFlow(kb,
				"Flow1"));
		assertTrue(composedNodes.containsAll(DiaFluxUtils.findFlow(kb, "Flow1").getNodesOfClass(
				ComposedNode.class)));
	}

}
