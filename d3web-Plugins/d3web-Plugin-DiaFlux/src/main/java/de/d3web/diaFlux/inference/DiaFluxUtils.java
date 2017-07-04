/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.diaFlux.inference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.denkbares.utils.Log;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.DiaFluxCaseObject;
import de.d3web.diaFlux.flow.DiaFluxElement;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.StartNode;

/**
 * @author Reinhard Hatko
 *         <p>
 *         Created: 07.08.2010
 */
public final class DiaFluxUtils {

	private DiaFluxUtils() {
	}

	public static final Property<Boolean> FORCE_PROPAGATION = Property.getProperty(
			"forcePropagation", Boolean.class);

	public static FlowSet getFlowSet(KnowledgeBase knowledgeBase) {
		return knowledgeBase.getKnowledgeStore().getKnowledge(FluxSolver.FLOW_SET);
	}

	public static FlowSet getFlowSet(Session session) {
		return getFlowSet(session.getKnowledgeBase());
	}

	public static Collection<Flow> getFlows(KnowledgeBase knowledgeBase) {
		FlowSet flowSet = getFlowSet(knowledgeBase);
		return (flowSet == null) ? Collections.emptyList() : flowSet.getFlows();
	}

	public static Collection<Flow> getFlows(Session session) {
		return getFlows(session.getKnowledgeBase());
	}

	public static boolean isFlowCase(Session session) {
		return session != null && hasFlows(session.getKnowledgeBase());
	}

	public static boolean hasFlows(KnowledgeBase base) {
		FlowSet flowSet = getFlowSet(base);
		return flowSet != null && !flowSet.getFlows().isEmpty();
	}

	public static DiaFluxCaseObject getDiaFluxCaseObject(Session session) {
		return session.getSessionObject(session.getPSMethodInstance(FluxSolver.class));
	}

	public static <C extends Node> Collection<C> getNodesOfClass(KnowledgeBase knowledgeBase, Class<C> clazz) {
		Collection<C> nodes = new ArrayList<>();
		for (Flow flow : DiaFluxUtils.getFlowSet(knowledgeBase).getFlows()) {
			nodes.addAll(flow.getNodesOfClass(clazz));
		}
		return nodes;
	}

	/**
	 * checks if there is a connecting Path from the {@link Node} fromNode to the {@link Node}
	 * toNode. This method does NOT do a "deep" search, it only works for Nodes in the same flow.
	 * <p>
	 * if fromNode and toNode are equal, this method checks for a cycle.
	 *
	 * @created 04.04.2012
	 */
	public static boolean areConnectedNodes(Node fromNode, Node toNode) {
		return fromNode.getFlow() == toNode.getFlow() && areConnectedNodes(fromNode, toNode, new LinkedList<>());
	}

	private static boolean areConnectedNodes(Node fromNode, Node toNode, Collection<Edge> activeEdges) {
		// if from and to are the same node, we try to find a cycle
		if (!activeEdges.isEmpty() && fromNode == toNode) return true;
		for (Edge edge : fromNode.getOutgoingEdges()) {
			if (activeEdges.contains(edge)) continue;
			activeEdges.add(edge);
			if (areConnectedNodes(edge.getEndNode(), toNode, activeEdges)) return true;
			activeEdges.remove(edge);
		}
		return false;
	}

	/**
	 * Returns a set of connected nodes.
	 */
	public static Collection<Node> getReachableNodes(Node node) {
		Collection<Node> result = new HashSet<>();
		getReachableNodes(node, result);
		return result;
	}

	private static void getReachableNodes(Node node, Collection<Node> traversed) {
		if (traversed.contains(node)) return;
		traversed.add(node);
		for (Edge edge : node.getOutgoingEdges()) {
			getReachableNodes(edge.getEndNode(), traversed);
		}
	}

	public static List<StartNode> getAutostartNodes(KnowledgeBase base) {
		List<StartNode> result = new LinkedList<>();

		for (Flow flow : getFlowSet(base)) {
			if (flow.isAutostart()) {
				result.addAll(flow.getStartNodes());
			}
		}

		return result;

	}

	public static Flow findFlow(KnowledgeBase kb, String flowName) {
		FlowSet flowSet = getFlowSet(kb);

		if (flowSet == null) {
			Log.severe(("No Flowcharts found in kb."));
			return null;
		}

		Flow subflow = flowSet.get(flowName);

		if (subflow == null) {
			Log.severe(("Flowchart '" + flowName + "' not found."));
			return null;
		}
		return subflow;
	}

	private static <T extends Node> T findNode(List<T> nodes, String nodeName) {

		for (T node : nodes) {
			if (node.getName().equals(nodeName)) {
				return node;
			}
		}

		Log.severe(("Node '" + nodeName + "' not found."));
		return null;

	}

	public static DiaFluxElement findObjectById(Flow flow, String id) {
		for (Node node : flow.getNodes()) {
			if (node.getID().equals(id)) return node;
		}

		for (Edge edge : flow.getEdges()) {
			if (edge.getID().equals(id)) return edge;
		}
		return null;
	}

	/**
	 * Searches a startnode by its name and the name of the containing flow.
	 *
	 * @return s the start node, or null, if the flow or startnode do not exist
	 */
	public static StartNode findStartNode(KnowledgeBase kb, String flowName, String startNodeName) {

		Flow flow = findFlow(kb, flowName);
		if (flow == null) return null;
		List<StartNode> startNodes = flow.getStartNodes();
		return findNode(startNodes, startNodeName);

	}

	/**
	 * Searches an exit node by its name and the name of the containing flow.
	 *
	 * @return s the exit node, or null, if the flow or exit node do not exist
	 */
	public static EndNode findExitNode(KnowledgeBase kb, NodeActiveCondition condition) {
		return findExitNode(kb, condition.getFlowName(), condition.getNodeName());
	}

	public static EndNode findExitNode(KnowledgeBase kb, String flowName, String endNodeName) {

		Flow flow = findFlow(kb, flowName);
		if (flow == null) return null;
		List<EndNode> exitNodes = flow.getExitNodes();
		return findNode(exitNodes, endNodeName);
	}

	/**
	 * Returns the startnode that is called by the composed node. May return null, if no such flow
	 * exists.
	 *
	 * @return s the start node, or null, if the called startnode does not exist
	 */
	public static StartNode getCalledStartNode(ComposedNode composedNode) {
		return findStartNode(composedNode.getFlow().getKnowledgeBase(),
				composedNode.getCalledFlowName(),
				composedNode.getCalledStartNodeName());
	}

	/**
	 * Returns the {@link Flow} that is called by composedNode
	 *
	 * @return s the flow containing the called start node, or null, if the start node does not
	 * exist
	 * @created 08.02.2012
	 */
	public static Flow getCalledFlow(ComposedNode composedNode) {
		StartNode calledStartNode = getCalledStartNode(composedNode);
		if (calledStartNode != null) {
			return calledStartNode.getFlow();
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all {@link ComposedNode}s, that call the supplied {@link Flow}.
	 *
	 * @return a List containing all the ComposedNodes
	 * @created 15.03.2012
	 */
	public static Collection<ComposedNode> getCallingNodes(Flow calledFlow) {
		return getFlowSet(calledFlow.getKnowledgeBase()).getNodesCalling(calledFlow.getName());
	}

	/**
	 * Returns all {@link ComposedNode}s, that call the supplied {@link StartNode}.
	 *
	 * @return a List containing all the ComposedNodes
	 * @created 15.03.2012
	 */
	public static List<ComposedNode> getCallingNodes(KnowledgeBase kb, StartNode startNode) {
		List<ComposedNode> result = new ArrayList<>();

		for (ComposedNode composedNode : getFlowSet(kb).getNodesCalling(startNode.getFlow()
				.getName())) {
			if (composedNode.getCalledStartNodeName().equals(startNode.getName())) {
				result.add(composedNode);
			}
		}
		return result;
	}

	/**
	 * Returns a DFS mapping of Flow -> contained ComposedNodes. If a flow is called from multiple
	 * flows, the only first one according to a DF search is contained.
	 *
	 * @created 14.03.2013
	 */
	public static Map<Flow, Collection<ComposedNode>> createFlowStructure(KnowledgeBase kb) {
		List<StartNode> nodes = DiaFluxUtils.getAutostartNodes(kb);
		assert nodes.size() == 1; // TODO for now works only with 1

		Flow callingFlow = nodes.get(0).getFlow();
		return createFlowStructure(new HashMap<>(), callingFlow);

	}

	private static Map<Flow, Collection<ComposedNode>> createFlowStructure(Map<Flow, Collection<ComposedNode>> result, Flow callingFlow) {
		Collection<ComposedNode> composed = callingFlow.getNodesOfClass(ComposedNode.class);
		for (ComposedNode composedNode : composed) {
			Flow calledFlow = DiaFluxUtils.getCalledFlow(composedNode);
			addFlow(result, callingFlow);
			addFlow(result, calledFlow);
			addCall(result, callingFlow, composedNode);
			createFlowStructure(result, calledFlow);
		}

		return result;

	}

	private static void addFlow(Map<Flow, Collection<ComposedNode>> result, Flow calledFlow) {
		Collection<ComposedNode> flows = result.computeIfAbsent(calledFlow, k -> new HashSet<>());
	}

	private static void addCall(Map<Flow, Collection<ComposedNode>> result, Flow callingFlow, ComposedNode calledNode) {
		Collection<ComposedNode> flows = result.get(callingFlow);
		flows.add(calledNode);
	}

}
