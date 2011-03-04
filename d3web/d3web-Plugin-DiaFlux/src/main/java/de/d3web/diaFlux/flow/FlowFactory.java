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

import java.util.Arrays;
import java.util.List;

import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.diaFlux.inference.CallFlowAction;
import de.d3web.diaFlux.inference.ConditionTrue;
import de.d3web.diaFlux.inference.FluxSolver;

/**
 * 
 * @author Reinhard Hatko
 * 
 */
public final class FlowFactory {

	private static final FlowFactory INSTANCE;

	static {
		INSTANCE = new FlowFactory();
	}

	public static FlowFactory getInstance() {
		return INSTANCE;
	}

	private FlowFactory() {

	}

	/**
	 * Creates a Flow instance with the supplied nodes and edges. Furthermore
	 * creates the EdgeMap KnowledgeSlices and attaches them to the according
	 * TerminologyObjects.
	 * 
	 * @param id
	 * @param name
	 * @param nodes
	 * @param edges
	 * @return
	 */
	public Flow createFlow(String id, String name, List<Node> nodes, List<Edge> edges) {

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

		Flow flow = new Flow(id, name, nodes, edges);

		createEdgeMaps(flow);
		createNodeLists(flow);

		return flow;

	}

	private void createEdgeMaps(Flow flow) {

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
					slice = new EdgeMap("EdgeMap" + nobject.getName());
					nobject.getKnowledgeStore().addKnowledge(FluxSolver.DEPENDANT_EDGES,
							slice);
				}

				slice.addEdge(edge);
			}

		}
	}

	private void createNodeLists(Flow flow) {

		for (Node node : flow.getNodes()) {

			List<? extends TerminologyObject> list = node.getHookedObjects();

			// For all other edges:
			// index them at the NamedObjects their condition contains
			for (TerminologyObject nobject : list) {
				NodeList slice = nobject.getKnowledgeStore().getKnowledge(
						FluxSolver.DEPENDANT_NODES);

				if (slice == null) {
					slice = new NodeList();
					nobject.getKnowledgeStore().addKnowledge(
							FluxSolver.DEPENDANT_NODES,
							slice);
				}

				slice.addNode(node);
			}

		}
	}

	public Node createActionNode(String id, PSAction action) {
		return new ActionNode(id, action.toString(), action);

	}

	public Edge createEdge(String id, Node startNode, Node endNode, Condition condition) {
		DefaultEdge edge = new DefaultEdge(id, startNode, endNode, condition);

		Condition defaultCondition = startNode.getEdgePrecondition();

		// If the node specifies a default condition, it is AND'ed with the
		// condition on the edge
		if (defaultCondition != null) {
			condition = new CondAnd(Arrays.asList(defaultCondition, condition));
		}

		((AbstractNode) startNode).addOutgoingEdge(edge);
		((AbstractNode) endNode).addIncomingEdge(edge);

		return edge;
	}

	public Node createStartNode(String id, String name) {
		return new StartNode(id, name);
	}

	public Node createEndNode(String id, String name) {

		return new EndNode(id, name);
	}

	public Node createComposedNode(String id, String flowName, String startNodeName) {

		CallFlowAction action = new CallFlowAction(flowName, startNodeName);

		String name = "CALL[" + flowName + "(" + startNodeName + ")]";

		return new ComposedNode(id, name, action);
	}

	public Node createCommentNode(String id, String name) {

		return new CommentNode(id, name);
	}

	public Node createSnapshotNode(String id, String name) {
		return new SnapshotNode(id, name);
	}

}
