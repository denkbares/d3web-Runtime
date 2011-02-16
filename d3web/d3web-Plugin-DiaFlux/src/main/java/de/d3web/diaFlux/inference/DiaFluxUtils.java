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

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.DiaFluxCaseObject;
import de.d3web.diaFlux.flow.EdgeData;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.flow.IEdge;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.flow.INodeData;
import de.d3web.diaFlux.flow.StartNode;

/**
 * @author Reinhard Hatko
 * 
 *         Created: 07.08.2010
 */
public final class DiaFluxUtils {

	private DiaFluxUtils() {
	}

	public static FlowSet getFlowSet(KnowledgeBase knowledgeBase) {

		KnowledgeSlice knowledge = knowledgeBase.getKnowledgeStore().getKnowledge(
				FluxSolver.DIAFLUX);

		return (FlowSet) knowledge;

	}

	public static INodeData getNodeData(INode node, Session session) {
		return getDiaFluxCaseObject(session).getPath(node.getFlow()).getNodeData(node);

	}

	public static EdgeData getEdgeData(IEdge edge, Session session) {
		return getPath(edge, session).getEdgeData(edge);
	}

	public static FlowSet getFlowSet(Session session) {

		return getFlowSet(session.getKnowledgeBase());

	}

	public static IPath getPath(Flow flow, Session session) {
		return getDiaFluxCaseObject(session).getPath(flow);
	}

	public static IPath getPath(INode node, Session session) {
		return getPath(node.getFlow(), session);
	}

	public static IPath getPath(IEdge edge, Session session) {
		return getPath(edge.getStartNode().getFlow(), session);
	}

	public static boolean isFlowCase(Session session) {

		if (session == null) {
			return false;
		}

		FlowSet flowSet = getFlowSet(session);

		return flowSet != null && !flowSet.getFlows().isEmpty();
	}

	public static DiaFluxCaseObject getDiaFluxCaseObject(Session session) {

		FlowSet flowSet = getFlowSet(session);

		return (DiaFluxCaseObject) session.getCaseObject(flowSet);
	}

	/**
	 * Adds the supplied flow to the knowledge base.
	 * 
	 * @param flow
	 * @param base
	 */
	public static void addFlow(Flow flow, KnowledgeBase base) {

		FlowSet flowSet = base.getKnowledgeStore().getKnowledge(FluxSolver.DIAFLUX);
		;
		if (flowSet == null) {
			flowSet = new FlowSet();
			base.getKnowledgeStore().addKnowledge(FluxSolver.DIAFLUX, flowSet);

		}
		flowSet.put(flow);

		registerComposedNodes(flow, base);

	}

	/**
	 * 
	 * @param flow
	 * @param base
	 */
	private static void registerComposedNodes(Flow flow, KnowledgeBase base) {
		NodeRegistry registry = getNodeRegistry(base);

		for (INode node : flow.getNodes()) {

			if (node instanceof ComposedNode) {

				String flowName = ((ComposedNode) node).getFlowName();

				for (IEdge edge : node.getOutgoingEdges()) {

					Condition condition = edge.getCondition();

					if (condition instanceof NodeActiveCondition) {
						String exitNodeName = ((NodeActiveCondition) condition).getNodeName();

						registry.registerNode(flowName, exitNodeName, node);

					}
					else if (condition instanceof FlowchartProcessedCondition) {
						registry.registerFlow(flowName, node);
					}

				}

			}

		}

	}

	/**
	 * 
	 * @param base
	 * @return s the NodeRegistry to look up nodes by Name
	 */
	public static NodeRegistry getNodeRegistry(KnowledgeBase base) {
		NodeRegistry registry = base.getKnowledgeStore().getKnowledge(FluxSolver.NODE_REGISTRY);

		if (registry == null) {
			registry = new NodeRegistry();
			base.getKnowledgeStore().addKnowledge(FluxSolver.NODE_REGISTRY,
					registry);

		}
		return registry;
	}

	public static NodeRegistry getNodeRegistry(Session session) {
		return getNodeRegistry(session.getKnowledgeBase());
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

	public static INode findNode(Session session, String flowName, String nodeName) {
		FlowSet flowSet = getFlowSet(session);

		if (flowSet == null) {
			Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("No Flowcharts found in kb."));
		}

		Flow subflow = flowSet.getByName(flowName);

		if (subflow == null) {
			Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("Flowchart '" + flowName + "' not found."));
			return null;
		}

		List<INode> startNodes = subflow.getNodes();

		for (INode node : startNodes) {
			if (node.getName().equalsIgnoreCase(nodeName)) {
				return node;

			}
		}

		Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("Node '" + nodeName + "' of flow '" + flowName + "' not found."));
		return null;

	}

	/**
	 * returns the StartNode that is called by the supplied action
	 */
	// TODO Cleanup
	public static StartNode findStartNode(Session session, String flowName, String startNodeName) {

		FlowSet flowSet = getFlowSet(session);

		if (flowSet == null) {
			Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("No Flowcharts found in kb."));
		}

		Flow subflow = flowSet.getByName(flowName);

		if (subflow == null) {
			Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("Flowchart '" + flowName + "' not found."));
			return null;
		}

		List<StartNode> startNodes = subflow.getStartNodes();

		for (StartNode node : startNodes) {
			if (node.getName().equalsIgnoreCase(startNodeName)) {
				return node;

			}
		}

		Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("Startnode '" + startNodeName + "' of flow '" + flowName + "' not found."));
		return null;

	}

	/**
	 * returns the StartNode that is called by the supplied action
	 */
	// TODO Cleanup
	public static EndNode findExitNode(Session session, String flowName, String startNodeName) {

		FlowSet flowSet = getFlowSet(session);

		if (flowSet == null) {
			Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("No Flowcharts found in kb."));
		}

		Flow subflow = flowSet.getByName(flowName);

		if (subflow == null) {
			Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("Flowchart '" + flowName + "' not found."));
			return null;
		}

		List<EndNode> exitNodes = subflow.getExitNodes();

		for (EndNode node : exitNodes) {
			if (node.getName().equalsIgnoreCase(startNodeName)) {
				return node;

			}
		}

		Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("Exitnode '" + startNodeName + "' of flow '" + flowName + "' not found."));
		return null;

	}

}
