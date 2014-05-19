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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import de.d3web.utils.Log;

/**
 * @author Reinhard Hatko
 * 
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

	public static boolean isFlowCase(Session session) {

		if (session == null) {
			return false;
		}

		return hasFlows(session.getKnowledgeBase());
	}
	
	public static boolean hasFlows(KnowledgeBase base) {
		
		FlowSet flowSet = getFlowSet(base);
		
		return flowSet != null && !flowSet.getFlows().isEmpty();
	}

	public static DiaFluxCaseObject getDiaFluxCaseObject(Session session) {
		return session.getSessionObject(session.getPSMethodInstance(FluxSolver.class));
	}

	/**
	 * checks if there is a connecting Path from the {@link Node} fromNode to
	 * the {@link Node} toNode. This method does NOT do a "deep" search, it only
	 * works for Nodes in the same flow.
	 * 
	 * if fromNode and toNode are equal, this method checks for a cycle.
	 * 
	 * @created 04.04.2012
	 * @param fromNode
	 * @param toNode
	 * @return
	 */
	public static boolean areConnectedNodes(Node fromNode, Node toNode) {
		if (fromNode.getFlow() != toNode.getFlow()) return false;
		return areConnectedNodes(fromNode, toNode, new LinkedList<Edge>());
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
		Collection<Node> result = new HashSet<Node>();
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
		List<StartNode> result = new LinkedList<StartNode>();

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
			if (node.getName().equalsIgnoreCase(nodeName)) {
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
	 * @param kb
	 * @param flowName
	 * @param startNodeName
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
	 * @param kb
	 * @param flowName
	 * @param startNodeName
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
	 * Returns the startnode that is called by the composed node. May return
	 * null, if no such flow exists.
	 * 
	 * @param kb
	 * @param flowName
	 * @param startNodeName
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
	 * @created 08.02.2012
	 * @return s the flow containing the called start node, or null, if the
	 *         start node does not exist
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
	 * @created 15.03.2012
	 * @param flow
	 * @return a List containing all the ComposedNodes
	 */
	public static List<ComposedNode> getCallingNodes(Flow calledFlow) {
		List<ComposedNode> result = new LinkedList<ComposedNode>();

		for (Flow flow : getFlowSet(calledFlow.getKnowledgeBase())) {
			Collection<ComposedNode> composedNodes = flow.getNodesOfClass(ComposedNode.class);

			for (ComposedNode composedNode : composedNodes) {
				if (getCalledFlow(composedNode) == calledFlow) {
					result.add(composedNode);
				}
			}
		}

		return result;
	}

	/**
	 * Returns all {@link ComposedNode}s, that call the supplied
	 * {@link StartNode}.
	 * 
	 * @created 15.03.2012
	 * @param kb
	 * @param flow
	 * @return a List containing all the ComposedNodes
	 */
	public static List<ComposedNode> getCallingNodes(KnowledgeBase kb, StartNode startNode) {
		List<ComposedNode> result = new LinkedList<ComposedNode>();

		for (Flow flow : getFlowSet(kb)) {
			Collection<ComposedNode> composedNodes = flow.getNodesOfClass(ComposedNode.class);

			for (ComposedNode composedNode : composedNodes) {
				if (getCalledStartNode(composedNode) == startNode) {
					result.add(composedNode);
				}
			}
		}

		return result;
	}

	/**
	 * Returns a DFS mapping of Flow -> contained ComposedNodes. If a flow is called
	 * from multiple flows, the only first one according to a DF search is
	 * contained.
	 * 
	 * @created 14.03.2013
	 * @param kb
	 * @return
	 */
	public static Map<Flow, Collection<ComposedNode>> createFlowStructure(KnowledgeBase kb) {
		List<StartNode> nodes = DiaFluxUtils.getAutostartNodes(kb);
		assert nodes.size() == 1; // TODO for now works only with 1

		Flow callingFlow = nodes.get(0).getFlow();
		return createFlowStructure(kb, new HashMap<Flow, Collection<ComposedNode>>(), callingFlow);

	}

	private static Map<Flow, Collection<ComposedNode>> createFlowStructure(KnowledgeBase base, Map<Flow, Collection<ComposedNode>> result, Flow callingFlow) {
		Collection<ComposedNode> composed = callingFlow.getNodesOfClass(ComposedNode.class);
		for (ComposedNode composedNode : composed) {
			Flow calledFlow = DiaFluxUtils.getCalledFlow(composedNode);
			addFlow(result, callingFlow);
			addFlow(result, calledFlow);
			addCall(result, callingFlow, composedNode);
			createFlowStructure(base, result, calledFlow);
		}

		return result;

	}

	private static void addFlow(Map<Flow, Collection<ComposedNode>> result, Flow calledFlow) {
		Collection<ComposedNode> flows = result.get(calledFlow);
		if (flows == null) {
			flows = new HashSet<ComposedNode>();
			result.put(calledFlow, flows);
		}
	}

	private static void addCall(Map<Flow, Collection<ComposedNode>> result, Flow callingFlow, ComposedNode calledNode) {
		Collection<ComposedNode> flows = result.get(callingFlow);
		flows.add(calledNode);
	}

}
