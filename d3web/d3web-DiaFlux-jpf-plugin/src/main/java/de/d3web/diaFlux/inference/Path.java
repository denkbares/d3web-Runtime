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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.EdgeSupport;
import de.d3web.diaFlux.flow.IEdge;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.flow.INodeData;
import de.d3web.diaFlux.flow.ISupport;
import de.d3web.diaFlux.flow.StartNode;

/**
 * @author Reinhard Hatko
 * 
 *         Created: 07.08.2010
 */
public class Path implements IPath {

	private final LinkedList<Entry> entries;

	/**
	 * @param startNode
	 * @param session
	 */
	public Path(StartNode startNode, ISupport support) {
		this.entries = new LinkedList<Entry>();
		this.entries.push(startNode.createEntry(null, support));
	}

	/**
	 * Returns the first Node of this path. This has to be either a StartNode or
	 * a SnapshotNode. Null if the path is empty.
	 * 
	 * @return
	 */
	@Override
	public INode getFirstNode() {
		if (isEmpty()) {
			return null;
		}
		else {
			return entries.getFirst().getNode();
		}
	}

	@Override
	public boolean propagate(Session session, Collection<PropagationEntry> changes) {

		// at first propagate changes to all entries
		for (Entry entry : this.entries) {
			// TODO OR of return values?
			entry.propagate(session, changes); // checkSupport(session);
			// DiaFluxUtils.getNodeData(entry.getNode(), session);
		}

		boolean change = false;
		change |= maintainTruth(session, changes);
		change |= flow(session);

		return change;

	}

	private boolean flow(Session session) {

		if (isEmpty()) return false;

		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				("Start flowing from node: " + entries.getLast().getNode()));

		boolean continueFlowing = true;
		boolean change = false;

		while (continueFlowing) {

			IEdge edge = selectNextEdge(session);

			if (edge == null) { // no edge to take
				Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
						("Staying in Node: " + this.entries.getLast().getNode()));
				return change;
			}

			Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
					("Following edge '" + edge + "'."));

			continueFlowing = followEdge(session, edge);
			change |= continueFlowing;

		}
		return change;

	}

	/**
	 * Selects appropriate successor of {@code node} according to the current
	 * state of the case.
	 * 
	 * @param session
	 * 
	 * @return
	 */
	private IEdge selectNextEdge(Session session) {

		INode node = entries.getLast().getNode();

		Iterator<IEdge> edges = node.getOutgoingEdges().iterator();

		while (edges.hasNext()) {

			IEdge edge = edges.next();

			try {
				if (edge.getCondition().eval(session)) {
					return edge;
				}
			}
			catch (NoAnswerException e) {
			}
			catch (UnknownAnswerException e) {
			}
		}

		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				("No edge to take from node:" + node));
		return null;

	}

	/**
	 * Activates the given node coming from the given {@link NodeEntry}. Steps:
	 * 1. Sets the node to active. 2. Conducts its action 3. Add
	 * {@link NodeEntry} for node.
	 * 
	 * @param session
	 * @param currentNode
	 * @param entry the pathentry from where to activate the node
	 * @param edge the egde to take
	 * @return nextNode
	 */
	private boolean followEdge(Session session, IEdge edge) {

		INode currentNode = this.entries.getLast().getNode();

		if (currentNode != edge.getStartNode()) throw new IllegalStateException(
				"Not in the expected Node");

		INode nextNode = edge.getEndNode();
		INodeData nextNodeData = DiaFluxUtils.getNodeData(nextNode, session);

		EdgeSupport support = new EdgeSupport(edge);

		if (nextNodeData.isActive()) {
			Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
					("Node is already active: " + nextNode));
			FluxSolver.addSupport(session, nextNode, support);

			// nextNodeData.addSupport(session, support); //add support from
			// current path
			return false; // TODO correct?
		}

		addPathEntryForNode(session, nextNode, support);

		doAction(session, nextNode);
		return true;

	}

	/**
	 * Adds a path entry for the current node. Predecessor's entry is removed by
	 * this method.
	 * 
	 * @param session
	 * @param currentEntry
	 * @param nextNode
	 * @param currentNode
	 * @return the new {@link NodeEntry} for node
	 */
	private Entry addPathEntryForNode(Session session, INode nextNode, ISupport support) {

		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				"Adding PathEntry for Node' " + nextNode + "' as successor of '"
						+ this.entries.getLast().getNode() + "'.");

		// at first: add support to the node
		FluxSolver.addSupport(session, nextNode, support);

		// then create entry
		Entry newEntry = nextNode.createEntry(session, support);

		entries.add(newEntry);

		return newEntry;

	}

	// TMS

	private boolean maintainTruth(Session session, Collection<PropagationEntry> changes) {

		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO, "Start maintaining truth.");

		List<int[]> wrongSubPathes = new LinkedList<int[]>();

		for (int i = 0; i < this.entries.size(); i++) {

			Entry entry = this.entries.get(i);
			// boolean support = entry.checkSupport(session);
			boolean support = DiaFluxUtils.getNodeData(entry.getNode(), session).isActive();

			if (!support) {
				INode node = entry.getNode();
				Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
						("Node is no longer supported: " + node));

				int lastIndex = findWrongSubpath(session, i);
				wrongSubPathes.add(0, new int[] {
						i, lastIndex }); // insert first, to iterate in reverse
											// order later
				i = lastIndex; // TODO oder lastIndex + 1??

			}

		}

		if (wrongSubPathes.isEmpty()) {
			Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO, "No TMS necessary");
			return false;
		}
		else {

			for (int[] is : wrongSubPathes) {
				collapseSubpath(is[0], is[1], session);

			}
			Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
					"Finished maintaining truth.");
			return true;
		}

	}

	/**
	 * Collapses an unsupported subpath, starting from {@code from}, until
	 * {@code to}. Every Node on this subpath must not be active, otherwise a
	 * {@link IllegalStateException} is thrown.
	 * 
	 * @param from the index to start collapsing
	 * @param to the index to stop collapsing
	 * @param session the current session
	 */
	private void collapseSubpath(int from, int to, Session session) {

		if (to < from) throw new IllegalArgumentException("Can not collapse path from " + from
				+ " to " + to);

		INode fromNode = entries.get(from).getNode();
		INode toNode = entries.get(to).getNode();

		String msg = "Collapsing path from '" + fromNode.getName() + "' to '" + toNode.getName()
				+ "'.";
		Logger.getLogger(getClass().getName()).log(Level.INFO, msg);
		System.out.println(msg);

		for (int i = to; i >= from; i--) {
			Entry entry = entries.get(i); // TODO can the entry also be removed
											// before calling undo??
			INode node = entry.getNode();

			INodeData data = DiaFluxUtils.getNodeData(entry.getNode(), session);

			if (data.isActive()) { // the support of this node
				throw new IllegalStateException("Node '" + node
						+ "' is still active and can not be collapsed.");
			}

			undoAction(session, node);
			this.entries.remove(i);

		}

	}

	/**
	 * Searches for the longest unsupported subpath starting from index {@code
	 * firstindex} on. The longest unsupported subpath is that subpath that
	 * consists of entrys that have no other support than their own. Starting
	 * from {@code firstindex + 1} every Entry removes its support. If its node
	 * is still active (i.e. has other support than just by this entry) the
	 * search ends. Otherwise the search continues, until the path ends, or an
	 * entry is reached which has support.
	 * 
	 * @param session the current session
	 * @param firstIndex the index of the entry at which the search along the
	 *        current path starts to find the unsupported subpath
	 * @return
	 */
	private int findWrongSubpath(Session session, int firstIndex) {
		// sanity check: entry at firstIndex has to be unsupported
		Entry startEntry = this.entries.get(firstIndex);

		INode startingNode = startEntry.getNode();
		boolean active = DiaFluxUtils.getNodeData(startingNode, session).isActive();

		if (active) throw new IllegalStateException("Can not collapse Path of active Node: "
				+ startingNode);
		//

		int lastIndex;

		for (lastIndex = firstIndex; lastIndex < this.entries.size(); lastIndex++) {

			Entry entry = this.entries.get(lastIndex);

			// remove support (is called for the second time, for entry, for
			// which the incoming edge is no longer valid)
			boolean removeSupport = entry.removeSupport(session);

			boolean supported = DiaFluxUtils.getNodeData(entry.getNode(), session).isActive();

			if (supported) { // stop, if node is still supported
				Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
						("Found node with support during TMS: " + entry));
				return lastIndex;

			}

		}
		return lastIndex - 1; // reached end -> lastIndex is one off

	}

	private void doAction(Session session, INode node) {
		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				("Doing action of node: " + node));
		node.doAction(session);
	}

	private void undoAction(Session session, INode node) {
		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				("Undoing action of node: " + node));
		node.undoAction(session);
	}

	@Override
	public boolean isEmpty() {
		return entries.isEmpty();
	}

	@Override
	public Iterator<? extends Entry> iterator() {
		return entries.iterator();
	}

	@Override
	public String toString() {
		if (isEmpty()) {
			return "empty path";
		}
		else {
			INode firstNode = getFirstNode();
			String name = firstNode.getFlow().getName();
			return "Path in flow '" + name + "' starting at '" + firstNode.getName() + "'";
		}
	}

}
