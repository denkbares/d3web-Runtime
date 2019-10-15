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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.condition.Condition;
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
	 */
	public static Flow createFlow(KnowledgeBase knowledgeBase, String name, List<Node> nodes, List<Edge> edges) {

		// create the flow and add them to the knowledge base
		Flow flow = new Flow(knowledgeBase, name, nodes, edges);

		// link the nodes and edges to all objects that are involved
		flow.getEdges().forEach(FlowFactory::linkEdge);
		flow.getNodes().forEach(FlowFactory::linkNode);

		checkFlowConsistency(flow);
		insertIntoKB(flow);
		return flow;
	}

	/**
	 * Checks if the specified flow is consistent, so that all connected nodes and edges of the flow are also located
	 * within the flow. If not, an IllegalArgumentException is thrown. You may use this method when creating flows
	 * programmatically to check that your code is doing well.
	 *
	 * @param flow the flow to be checked
	 * @throws IllegalArgumentException if the flow is not consistent
	 */
	public static void checkFlowConsistency(Flow flow) throws IllegalArgumentException {
		Set<Node> nodes = new HashSet<>(flow.getNodes());
		Set<Edge> edges = new HashSet<>(flow.getEdges());

		for (Edge edge : edges) {
			if (edge.getFlow() != flow) {
				throw new IllegalArgumentException("The edge '" + edge + " ' returns an invalid flow instance: " + edge.getFlow());
			}

			if (!nodes.contains(edge.getStartNode())) {
				throw new IllegalArgumentException("Start node '" + edge.getStartNode()
						+ "' of edge '" + edge + "' is not contained in list of nodes.");
			}
			if (!nodes.contains(edge.getEndNode())) {
				throw new IllegalArgumentException("End node '" + edge.getEndNode() + "' of edge '"
						+ edge + "' is not contained in list of nodes.");
			}
		}

		for (Node node : nodes) {
			if (node.getFlow() != flow) {
				throw new IllegalArgumentException("The node '" + node + " ' returns an invalid flow instance: " + node.getFlow());
			}

			for (Edge edge : node.getIncomingEdges()) {
				if (!edges.contains(edge)) {
					throw new IllegalArgumentException("Incoming edge '" + edge
							+ "' of node '" + node + "' is not contained in list of edges.");
				}
			}

			for (Edge edge : node.getOutgoingEdges()) {
				if (!edges.contains(edge)) {
					throw new IllegalArgumentException("Outgoing edge '" + edge
							+ "' of node '" + node + "' is not contained in list of edges.");
				}
			}
		}
	}

	/**
	 * Adds the Flow to the FlowSet KnowledgeSlice
	 */
	private static void insertIntoKB(Flow flow) {
		flow.getKnowledgeBase().getKnowledgeStore().computeIfAbsent(FluxSolver.FLOW_SET, FlowSet::new).addFlow(flow);
	}


	private static void linkEdge(Edge edge) {
		// index them at the NamedObjects their condition contains
		for (TerminologyObject nobject : edge.getCondition().getTerminalObjects()) {
			nobject.getKnowledgeStore().computeIfAbsent(FluxSolver.DEPENDANT_EDGES, EdgeList::new).addEdge(edge);
		}
	}

	private static void unlinkEdge(Edge edge) {
		// index them at the NamedObjects their condition contains
		for (TerminologyObject nobject : edge.getCondition().getTerminalObjects()) {
			EdgeList slice = nobject.getKnowledgeStore().getKnowledge(FluxSolver.DEPENDANT_EDGES);
			if (slice != null) {
				slice.removeEdge(edge);
				if (slice.getEdges().isEmpty()) {
					nobject.getKnowledgeStore().removeKnowledge(FluxSolver.DEPENDANT_EDGES, slice);
				}
			}
		}
	}

	private static void linkNode(Node node) {
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

	private static void unlinkNode(Node node) {
		// index nodes to the objects their condition contains
		List<? extends TerminologyObject> hookedObjects = node.getHookedObjects();
		for (TerminologyObject object : hookedObjects) {
			unlinkNodeFrom(FluxSolver.DEPENDANT_NODES, node, object);
		}
		// index nodes to the objects to be modified
		if (node instanceof ActionNode) {
			PSAction action = ((ActionNode) node).getAction();
			if (action != null) {
				List<? extends TerminologyObject> derivedObjects = action.getBackwardObjects();
				for (TerminologyObject object : derivedObjects) {
					unlinkNodeFrom(FluxSolver.DERIVING_NODES, node, object);
				}
			}
		}
	}

	private static void linkNodeTo(KnowledgeKind<NodeList> kind, Node node, TerminologyObject object) {
		object.getKnowledgeStore().computeIfAbsent(kind, NodeList::new).addNode(node);
	}

	private static void unlinkNodeFrom(KnowledgeKind<NodeList> kind, Node node, TerminologyObject object) {
		NodeList slice = object.getKnowledgeStore().getKnowledge(kind);
		if (slice != null) {
			slice.removeNode(node);
			if (slice.getNodes().isEmpty()) {
				object.getKnowledgeStore().removeKnowledge(kind, slice);
			}
		}
	}

	/**
	 * Creates an edge between the specified nodes. Note that the edge is not automatically added to a flow, and the
	 * edge is also not automatically linked to its terminology objects.
	 *
	 * @param id        the id if the edge
	 * @param startNode the node the edge starts from
	 * @param endNode   the node the edge directs to
	 * @param condition the guard of the edge
	 * @return the created edge
	 */
	public static Edge createEdge(String id, Node startNode, Node endNode, Condition condition) {
		DefaultEdge edge = new DefaultEdge(id, startNode, endNode, condition);

		((AbstractNode) startNode).addOutgoingEdge(edge);
		((AbstractNode) endNode).addIncomingEdge(edge);

		return edge;
	}

	/**
	 * Adds an edge from the flow and to the connected nodes. The method also automatically links the edge from all
	 * terminology objects.
	 * <p>
	 * Note that an edge must not been added to a flow, unless both, the start and end node has been added to the flow
	 *
	 * @param edge the edge to be added
	 */
	public static void addEdge(Edge edge) {
		linkEdge(edge);
		((AbstractNode) edge.getStartNode()).addOutgoingEdge(edge);
		((AbstractNode) edge.getEndNode()).addIncomingEdge(edge);
		edge.getFlow().addEdge(edge);
	}

	/**
	 * Removes an edge from the flow and from the connected nodes. The method also automatically unlinks the edge from
	 * all terminology objects.
	 * <p>
	 * Note that an edge must been removed from a flow at a time where both, start node and end node are still added to
	 * the flow.
	 *
	 * @param edge the edge to be removed
	 */
	public static void removeEdge(Edge edge) {
		unlinkEdge(edge);
		((AbstractNode) edge.getStartNode()).removeOutgoingEdge(edge);
		((AbstractNode) edge.getEndNode()).removeIncomingEdge(edge);
		Flow flow = edge.getFlow();
		if (flow != null) {
			flow.removeEdge(edge);
		}
	}

	/**
	 * Adds a node from the flow. The method also automatically links the node to all terminology objects.
	 * <p>
	 * Note that while adding/removing the nodes of a flow, the flow should not been added to a {@link FlowSet}, because
	 * otherwise the caches of the FlowSet may not update correctly.
	 *
	 * @param flow the flow to add the node to
	 * @param node the node to be added
	 */
	public static void addNode(Flow flow, Node node) {
		linkNode(node);
		flow.addNode(node);
		node.setFlow(flow);
	}

	/**
	 * Removes a node from the flow. The method also automatically unlinks the node from all terminology objects.
	 * <p>
	 * Note that while adding/removing the nodes of a flow, the flow should not been added to a {@link FlowSet}, because
	 * otherwise the caches of the FlowSet may not update correctly.
	 *
	 * @param node the node to be removed
	 */
	public static void removeNode(Node node) {
		unlinkNode(node);
		Flow flow = node.getFlow();
		if (flow != null) {
			flow.removeNode(node);
			node.setFlow(null);
		}
	}

	/**
	 * Adds all the specified nodes to the specified flowchart. After that, it also adds all edges that are connected to
	 * any of these nodes to the flow. Both, nodes and edges, will be linked to all relevant terminology objects.
	 * <p>
	 * Note that while adding/removing the nodes of a flow, the flow should not been added to a {@link FlowSet}, because
	 * otherwise the caches of the FlowSet may not update correctly.
	 *
	 * @param nodes the nodes to remove from the flow
	 */
	public static void addNodesAndConnectedEdges(Flow flow, Collection<? extends Node> nodes) {
		nodes.forEach(node -> addNode(flow, node));
		nodes.stream()
				.flatMap(node -> Stream.concat(node.getIncomingEdges().stream(), node.getOutgoingEdges().stream()))
				.distinct().forEach(FlowFactory::addEdge);
	}

	/**
	 * Removes all the specified nodes from the flowchart the nodes are added to. Before, it also removes all edges that
	 * are connected to any of these nodes from the flow. Both, nodes and edges, will be unlinked from all terminology
	 * objects before they are removed.
	 * <p>
	 * Note that while adding/removing the nodes of a flow, the flow should not been added to a {@link FlowSet}, because
	 * otherwise the caches of the FlowSet may not update correctly.
	 *
	 * @param nodesToRemove the nodes to remove from the flow
	 */
	public static void removeNodesAndConnectedEdges(Collection<? extends Node> nodesToRemove) {
		// use a copy of nodes and edges to avoid concurrent modification
		Set<Node> nodes = new HashSet<>(nodesToRemove);
		Set<Edge> edges = new HashSet<>();
		for (Node node : nodes) {
			edges.addAll(node.getIncomingEdges());
			edges.addAll(node.getOutgoingEdges());
		}
		edges.forEach(FlowFactory::removeEdge);
		nodes.forEach(FlowFactory::removeNode);
	}

	/**
	 * Removes all the nodes and edges from the specified flowchart. Both, nodes and edges, will be unlinked from all
	 * terminology objects before they are removed. After that, the flow is empty.
	 * <p>
	 * Note that while adding/removing the nodes of a flow, the flow should not been added to a {@link FlowSet}, because
	 * otherwise the caches of the FlowSet may not update correctly.
	 *
	 * @param flow the flow to remove all nodes and edges from
	 */
	public static void removeAllNodesAndEdges(Flow flow) {
		removeNodesAndConnectedEdges(flow.getNodes());
		assert flow.getNodes().isEmpty();
		assert flow.getEdges().isEmpty();
	}
}
