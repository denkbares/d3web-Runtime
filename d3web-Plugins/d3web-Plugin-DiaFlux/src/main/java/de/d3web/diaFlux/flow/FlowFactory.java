/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.diaFlux.flow;

import java.util.List;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.ConditionTrue;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.diaFlux.inference.FluxSolver;

/**
 * @author Reinhard Hatko
 */
public final class FlowFactory {

	private FlowFactory() {

	}

	/**
	 * Creates a Flow instance with the supplied nodes and edges. Furthermore creates the EdgeMap KnowledgeSlices and
	 * attaches them to the according TerminologyObjects.
	 *
	 * @param knowledgeBase
	 * @param name
	 * @param nodes
	 * @param edges
	 * @return
	 */
	public static Flow createFlow(KnowledgeBase knowledgeBase, String name, List<Node> nodes, List<Edge> edges) {

		for (Edge edge : edges) {
			if (!nodes.contains(edge.getStartNode())) {
				throw new IllegalArgumentException("Start node '" + edge.getStartNode()
						+ "' of edge '" + edge + " 'is not contained in list of nodes.");
			}
			if (!nodes.contains(edge.getEndNode())) {
				throw new IllegalArgumentException("End node '" + edge.getEndNode() + "' of edge '"
						+ edge + " 'is not contained in list of nodes.");
			}
		}

		Flow flow = new Flow(knowledgeBase, name, nodes, edges);
		insertIntoKB(flow);
		createEdgeMaps(flow);
		createNodeLists(flow);

		return flow;
	}

	/**
	 * Adds the Flow to the FlowSet KnowledgeSlice
	 *
	 * @param flow
	 * @created 16.05.2013
	 */
	private static void insertIntoKB(Flow flow) {
		KnowledgeBase kb = flow.getKnowledgeBase();
		FlowSet flowSet = kb.getKnowledgeStore().getKnowledge(FluxSolver.FLOW_SET);
		if (flowSet == null) {
			flowSet = new FlowSet();
			kb.getKnowledgeStore().addKnowledge(FluxSolver.FLOW_SET, flowSet);
		}
		flowSet.put(flow);
	}

	private static void createEdgeMaps(Flow flow) {

		for (Edge edge : flow.getEdges()) {

			Condition condition = edge.getCondition();

			// no need to put edges with ConditionTrue in edgemap. flowing will
			// continue there when they are reached
			if (condition == ConditionTrue.INSTANCE) continue;

			// For all other edges:
			// index them at the NamedObjects their condition contains
			for (TerminologyObject nobject : condition.getTerminalObjects()) {

				EdgeMap slice = nobject.getKnowledgeStore().getKnowledge(
						FluxSolver.DEPENDANT_EDGES);

				if (slice == null) {
					slice = new EdgeMap();
					nobject.getKnowledgeStore().addKnowledge(FluxSolver.DEPENDANT_EDGES,
							slice);
				}

				slice.addEdge(edge);
			}

		}
	}

	private static void createNodeLists(Flow flow) {

		// add the node to all objects that are
		// used to derive the nodes state or value
		for (Node node : flow.getNodes()) {
			// index nodes to the objects their condition contains
			List<? extends TerminologyObject> hookedObjects = node.getHookedObjects();
			for (TerminologyObject object : hookedObjects) {
				linkNodeTo(FluxSolver.DEPENDANT_NODES, node, object);
			}
			// index nodes to the objects to be modified
			if (node instanceof ActionNode) {
				PSAction action = ((ActionNode) node).getAction();
				if (action != null) {
					List<? extends TerminologyObject> derivedObjects = action.getBackwardObjects();
					for (TerminologyObject object : derivedObjects) {
						linkNodeTo(FluxSolver.DERIVING_NODES, node, object);
					}
				}
			}
		}
	}

	private static void linkNodeTo(KnowledgeKind<NodeList> kind, Node node, TerminologyObject object) {
		NodeList slice = object.getKnowledgeStore().getKnowledge(kind);
		if (slice == null) {
			slice = new NodeList();
			object.getKnowledgeStore().addKnowledge(kind, slice);
		}
		slice.addNode(node);
	}

	public static Edge createEdge(String id, Node startNode, Node endNode, Condition condition) {
		DefaultEdge edge = new DefaultEdge(id, startNode, endNode, condition);

		((AbstractNode) startNode).addOutgoingEdge(edge);
		((AbstractNode) endNode).addIncomingEdge(edge);

		return edge;
	}
}
