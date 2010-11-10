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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.diaFlux.flow.EdgeData;
import de.d3web.diaFlux.flow.EdgeSupport;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.IEdge;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.flow.INodeData;
import de.d3web.diaFlux.flow.ISupport;
import de.d3web.diaFlux.flow.NodeSupport;
import de.d3web.diaFlux.flow.SnapshotNode;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.diaFlux.flow.StartNodeData;

/**
 * @author Reinhard Hatko
 *
 *         Created: 07.08.2010
 */
public class Path extends SessionObject implements IPath {

	private final Map<INode, INodeData> nodeData;
	private final Map<IEdge, EdgeData> edgeData;

	public Path(Flow flow, Map<INode, INodeData> nodeData, Map<IEdge, EdgeData> edgeData) {
		super(flow);
		this.nodeData = Collections.unmodifiableMap(nodeData);
		this.edgeData = Collections.unmodifiableMap(edgeData);

	}

	@Override
	public INodeData getNodeData(INode node) {
		if (!nodeData.containsKey(node)) {
			throw new IllegalArgumentException("Node '" + node
					+ "' not found in flow '" + getSourceObject() + "'.");
		}

		return nodeData.get(node);

	}

	@Override
	public EdgeData getEdgeData(IEdge edge) {
		if (!edgeData.containsKey(edge)) {
			throw new IllegalArgumentException("Edge '" + edge + "' not found in flow '"
					+ getSourceObject() + "'.");
		}
		return edgeData.get(edge);
	}

	@Override
	public Flow getFlow() {
		return (Flow) getSourceObject();
	}


	@Override
	public void activate(StartNode startNode, ISupport support, Session session) {

		StartNodeData nodeData = (StartNodeData) DiaFluxUtils.getNodeData(startNode, session);

		if (support instanceof NodeSupport) {
			INode node = ((NodeSupport) support).getNode();

			nodeData.addCallingNode(DiaFluxUtils.getNodeData(node, session));

		}

		boolean active = nodeData.isActive();

		FluxSolver.addSupport(session, startNode, support);

		// if node was not active before adding support, start flowing
		if (!active) {
			flow(startNode, session);
		}

	}

	/**
	 * Starts propagation beginning at the supplied node.
	 *
	 * @created 05.11.2010
	 * @param session
	 * @param node
	 */
	@Override
	public boolean propagate(Session session, INode node) {

		maintainTruth(node, session);

		if (getNodeData(node).isActive()) {
			flow(node, session);

		}

		return true;

	}

	@Override
	public boolean takeSnapshot(Session session, SnapshotNode snapshotNode, INode node, List<INode> nodes) {

		if (!getFlow().getNodes().contains(node)) {
			throw new IllegalArgumentException("Node '" + node.getName()
					+ "' is not contained in Flow '" + getFlow().getName() + "'.");
		}

		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				("**Taking Snapshot at node: " + node));


		List<IEdge> activeEdges = selectActiveEdges(node.getIncomingEdges(), session);

		nodes.add(node);
		node.takeSnapshot(session, snapshotNode, nodes);


		for (IEdge edge : activeEdges) {

			takeSnapshot(session, snapshotNode, edge.getStartNode(), nodes);

		}


		return true;
	}

	/**
	 * Continues to flow from the current end node of this path.
	 *
	 *
	 * @param session
	 * @return s true, if there are changes to this path (ie. at least one new
	 *         node could be reached), false otherwise.
	 */
	private void flow(INode node, Session session) {

		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				("Start flowing from node: " + node));

		List<IEdge> edges = selectInactiveTrueEdges(node, session);

		if (edges.isEmpty()) { // no edge to take
			Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
					("Staying in Node: " + node));
		}
		else {

			for (IEdge edge : edges) {

				INode nextNode = followEdge(session, edge);

				if (nextNode != null) {
					flow(nextNode, session);
				}

			}

		}

	}

	/**
	 * Takes the given edge to reach its end node. Adds support to the reached
	 * node. If the node was not yet active, its action is done, otherwise
	 * nothing is done.
	 *
	 * @param session the current session
	 * @param edge the egde to take
	 * @return the newly reached node, of it was not active before, null
	 *         otherwise
	 */
	private INode followEdge(Session session, IEdge edge) {

		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				("Following edge '" + edge + "'."));

		getEdgeData(edge).setHasFired(true);

		INode nextNode = edge.getEndNode();
		INodeData nextNodeData = DiaFluxUtils.getNodeData(nextNode, session);

		ISupport support = new EdgeSupport(edge);

		boolean active = nextNodeData.isActive();

		FluxSolver.addSupport(session, nextNode, support);

		// if the next node is a SSN, then it does not matter, if it is active
		// or not
		// its action must be done anyway:
		// In a cycle with just one SSN it has to be activated anyway
		if (nextNode instanceof SnapshotNode) {
			// registers the snapshot
			FluxSolver.doAction(session, nextNode);

			// but do not continue flowing beyond the SSN
			return null;

		}

		// node was already active before adding the new support

		if (active && !(nextNode instanceof SnapshotNode)) {

			// so do nothing
			Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
						"Node is already active: " + nextNode);

			return null;

		}// node was not active or is an active SSN, so do its action
		else {

			FluxSolver.doAction(session, nextNode);

			return nextNode;
		}



	}

	/**
	 * Selects those edges that start at node that have not yeet fired, but
	 * whose guards are true.
	 *
	 * @param node
	 * @param session
	 * @return
	 */
	public List<IEdge> selectInactiveTrueEdges(INode node, Session session) {

		List<IEdge> result = new LinkedList<IEdge>();

		for (IEdge edge : node.getOutgoingEdges()) {

			try {

				if (!getEdgeData(edge).hasFired()) {

					if (edge.getCondition().eval(session)) {
						result.add(edge);
					}

				}
			}
			catch (NoAnswerException e) {
			}
			catch (UnknownAnswerException e) {
			}
		}

		return result;
	}

	/**
	 * Selects those edges starting at node that have fired, but whose guards
	 * are no longer true.
	 *
	 * @param node
	 * @param session
	 * @return
	 */
	public List<IEdge> selectActiveFalseEdges(INode node, Session session) {

		List<IEdge> result = new LinkedList<IEdge>();

		Iterator<IEdge> edges = node.getOutgoingEdges().iterator();

		while (edges.hasNext()) {

			IEdge edge = edges.next();

			try {

				if (getEdgeData(edge).hasFired()) {

					if (!edge.getCondition().eval(session)) {
						result.add(edge);
					}

				}
			}
			catch (NoAnswerException e) {
				// Edge is also not true, when no answer is given...
				result.add(edge);
			}
			catch (UnknownAnswerException e) {
				// or when the answer is unknown
				result.add(edge);
			}
		}

		return result;
	}



	// TMS
	private void maintainTruth(INode node, Session session) {

		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				"Maintaining truth at node '" + node.getName() + "'.");

		INodeData data = getNodeData(node);

		// checks the nodes support
		data.propagate(session);

		List<IEdge> edges;

		// if node is active...
		if (data.isActive()) {
			// ...only the edges that became false have to be undone
			edges = selectActiveFalseEdges(node, session);
		}
		else {// ...otherwise (node is no longer supported):
			// all activeedges have to be undone
			edges = selectActiveEdges(node.getOutgoingEdges(), session);

		}

		for (IEdge edge : edges) {

			getEdgeData(edge).setHasFired(false);

			INode endNode = edge.getEndNode();

			maintainTruth(endNode, session);

		}

	}



	/**
	 *
	 * @param node
	 * @param session
	 * @return
	 */
	public List<IEdge> selectActiveEdges(List<IEdge> edges, Session session) {

		List<IEdge> result = new LinkedList<IEdge>();

		for (IEdge edge : edges) {
			if (getEdgeData(edge).hasFired()) {
				result.add(edge);

			}
		}

		return result;
	}


	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean isActive() {

		for (INode node : getFlow().getNodes()) {
			if (getNodeData(node).isActive()) {
				return true;
			}
		}

		return false;

	}

	@Override
	public List<INode> getActiveNodes() {
		List<INode> result = new ArrayList<INode>();

		for (INode node : getFlow().getNodes()) {
			if (getNodeData(node).isActive()) {
				result.add(node);
			}
		}

		return result;

	}

	@Override
	public List<IEdge> getActiveEdges() {
		List<IEdge> result = new ArrayList<IEdge>();

		for (IEdge edge : getFlow().getEdges()) {
			if (getEdgeData(edge).hasFired()) {
				result.add(edge);
			}
		}

		return result;

	}

	@Override
	public String toString() {
		return super.toString() + "[" + getFlow() + "]";
	}

}
