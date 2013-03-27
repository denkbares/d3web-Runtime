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
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.DiaFluxCaseObject;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.StartNode;

/**
 * @author Reinhard Hatko
 * 
 *         Created: 07.08.2010
 */
public final class DiaFluxUtils {

	private DiaFluxUtils() {
	}

	// TODO cleanup, new FlowSet is created on each call
	public static FlowSet getFlowSet(KnowledgeBase knowledgeBase) {

		List<Flow> objects = knowledgeBase.getManager().getObjects(Flow.class);

		FlowSet set = new FlowSet();

		for (Flow flow : objects) {
			set.put(flow);
		}

		return set;

	}

	public static FlowSet getFlowSet(Session session) {
		return getFlowSet(session.getKnowledgeBase());
	}

	public static boolean isFlowCase(Session session) {

		if (session == null) {
			return false;
		}

		FlowSet flowSet = getFlowSet(session);

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
		if (fromNode == toNode) return true;
		for (Edge edge : fromNode.getOutgoingEdges()) {
			if (activeEdges.contains(edge)) continue;
			activeEdges.add(edge);
			if (areConnectedNodes(edge.getEndNode(), toNode, activeEdges)) return true;
			activeEdges.remove(edge);
		}
		return false;
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
			Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("No Flowcharts found in kb."));
			return null;
		}

		Flow subflow = flowSet.get(flowName);

		if (subflow == null) {
			Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("Flowchart '" + flowName + "' not found."));
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

		Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
				("Node '" + nodeName + "' not found."));
		return null;

	}

	public static StartNode findStartNode(KnowledgeBase kb, String flowName, String startNodeName) {

		Flow flow = findFlow(kb, flowName);
		if (flow == null) return null;
		List<StartNode> startNodes = flow.getStartNodes();
		return findNode(startNodes, startNodeName);

	}

	public static EndNode findExitNode(KnowledgeBase kb, NodeActiveCondition condition) {
		return findExitNode(kb, condition.getFlowName(), condition.getNodeName());
	}

	public static EndNode findExitNode(KnowledgeBase kb, String flowName, String endNodeName) {

		Flow flow = findFlow(kb, flowName);
		if (flow == null) return null;
		List<EndNode> exitNodes = flow.getExitNodes();
		return findNode(exitNodes, endNodeName);
	}

	public static StartNode getCalledStartNode(KnowledgeBase kb, ComposedNode composedNode) {
		return findStartNode(kb, composedNode.getCalledFlowName(),
				composedNode.getCalledStartNodeName());
	}

	/**
	 * Returns the {@link Flow} that is called by composedNode
	 * 
	 * @created 08.02.2012
	 */
	public static Flow getCalledFlow(KnowledgeBase kb, ComposedNode composedNode) {
		return getCalledStartNode(kb, composedNode).getFlow();
	}

	/**
	 * Returns all {@link ComposedNode}s, that call the supplied {@link Flow}.
	 * 
	 * @created 15.03.2012
	 * @param kb
	 * @param flow
	 * @return a List containing all the ComposedNodes
	 */
	public static List<ComposedNode> getCallingNodes(KnowledgeBase kb, Flow calledFlow) {
		List<ComposedNode> result = new LinkedList<ComposedNode>();

		for (Flow flow : getFlowSet(kb)) {
			Collection<ComposedNode> composedNodes = flow.getNodesOfClass(ComposedNode.class);

			for (ComposedNode composedNode : composedNodes) {
				if (getCalledFlow(kb, composedNode) == calledFlow) {
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
				if (getCalledStartNode(kb, composedNode) == startNode) {
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
			Flow calledFlow = DiaFluxUtils.getCalledFlow(base, composedNode);
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
