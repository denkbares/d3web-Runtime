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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.diaFlux.flow.EdgeData;
import de.d3web.diaFlux.flow.EdgeMap;
import de.d3web.diaFlux.flow.EdgeSupport;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.IEdge;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.flow.INodeData;
import de.d3web.diaFlux.flow.ISupport;
import de.d3web.diaFlux.flow.SnapshotNode;
import de.d3web.diaFlux.flow.StartNode;

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
		INodeData data = getNodeData(startNode);
		data.addSupport(session, support);
		flow(startNode, session);

	}

	@Override
	public boolean propagate(Session session, Collection<PropagationEntry> changes) {

		for (PropagationEntry propagationEntry : changes) {
			TerminologyObject object = propagationEntry.getObject();
			EdgeMap slice = (EdgeMap) ((NamedObject) object).getKnowledge(FluxSolver.class,
					MethodKind.FORWARD);

			if (slice == null) continue;

			for (IEdge edge : slice.getEdges(getFlow())) {

				INode node = edge.getStartNode();

				if (getNodeData(node).isActive()) {

					propagate(session, node);
				}

			}

		}

		return true;

	}

	/**
	 *
	 * @created 05.11.2010
	 * @param session
	 * @param node
	 */
	public void propagate(Session session, INode node) {
		maintainTruth(node, session);

		flow(node, session);
	}

	@Override
	public boolean takeSnapshot(Session session, SnapshotNode node) {


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

		if (!DiaFluxUtils.getNodeData(node, session).isActive()) {
			throw new IllegalStateException("Node '" + node + "' is not active.");
		}


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
	 * Selects appropriate successor of {@code node} according to the current
	 * state of the case.
	 *
	 * @param node
	 *
	 * @param session
	 *
	 * @return the first edge of this path's current end node whose guard
	 *         evaluates to true. Returns 'null' if no guard is true.
	 */
	private List<IEdge> selectInactiveTrueEdges(INode node, Session session) {

		return selectEdges(true, node, session);
	}

	private List<IEdge> selectActiveFalseEdges(INode node, Session session) {

		return selectEdges(false, node, session);
	}

	/**
	 *
	 * @param state
	 * @param node
	 * @param session
	 * @return
	 */
	private List<IEdge> selectEdges(boolean state, INode node, Session session) {
		List<IEdge> result = new LinkedList<IEdge>();

		Iterator<IEdge> edges = node.getOutgoingEdges().iterator();

		while (edges.hasNext()) {

			IEdge edge = edges.next();

			try {

				if (getEdgeData(edge).hasFired() != state) {

					if (edge.getCondition().eval(session) == state) {
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
	 * Takes the given edge to reach its end node. If the node was already
	 * active, new support for edge is added and false is returned.
	 *
	 * If the node was not yet active, its action is done, then a new Entry for
	 * the reached node is added.
	 *
	 *
	 * @param session the current session
	 * @param edge the egde to take
	 * @return true if the end node of edge was activated, ie it had no support
	 *         before
	 */
	private INode followEdge(Session session, IEdge edge) {

		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				("Following edge '" + edge + "'."));


		getEdgeData(edge).setHasFired(true);

		INode nextNode = edge.getEndNode();
		INodeData nextNodeData = DiaFluxUtils.getNodeData(nextNode, session);

		ISupport support;

		// if (nextNode instanceof SnapshotNode) {
		//
		// support = new ValidSupport();
		//
		// FluxSolver.addSupport(session, nextNode, support);
		//
		// FluxSolver.doAction(session, nextNode);
		//
		// return nextNode;
		//
		// }

		support = new EdgeSupport(edge);

		if (nextNodeData.isActive()) {

			Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
						("Node is already active: " + nextNode));
			FluxSolver.addSupport(session, nextNode, support);

			return null;

		}

		// Which to perform first? Doing the action or adding the entry for the
		// new node?
		// Doing the action first: When ComposedNode is reached, then the
		// NodeSupport for the called startnode is not yet valid, because the
		// ComposedNode has not yet support by the taken edge
		//
		// Adding the path entry first: Could be easier to take snapshot, if
		// entry for snapshot node is not yet added.
		// Or at first, add support, then do action, then create entry??

		FluxSolver.addSupport(session, nextNode, support);

		FluxSolver.doAction(session, nextNode);

		return nextNode;

	}



	// TMS
	private void maintainTruth(INode node, Session session) {

		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				"Maintaining truth at node '" + node.getName() + "'.");

		INodeData data = getNodeData(node);
		data.propagate(session);

		boolean active = data.isActive();

		List<IEdge> edges;

		// if node is active...
		if (active) {
			// ...only the edges that became false have to be undone
			edges = selectActiveFalseEdges(node, session);
		}
		else {// ...otherwise (node is no longer supported):
				// all edges have to be undone
			edges = selectActiveEdges(node, session);

		}

		for (IEdge edge : edges) {

			getEdgeData(edge).setHasFired(false);

			INode endNode = edge.getEndNode();

			// Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
			// "Node is no longer supported: " + node);

			maintainTruth(endNode, session);

		}

	}



	/**
	 *
	 * @created 05.11.2010
	 * @param node
	 * @param session
	 * @return
	 */
	private List<IEdge> selectActiveEdges(INode node, Session session) {

		List<IEdge> result = new LinkedList<IEdge>();

		for (IEdge edge : node.getOutgoingEdges()) {
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
		if (isEmpty()) {
			return "empty path";
		}
		else {
			return "Path ";
		}
	}

}
