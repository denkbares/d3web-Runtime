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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.d3web.core.session.CaseObjectSource;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.SessionObject;

/**
 * 
 * @author Reinhard Hatko
 * 
 *         Created on: 04.11.2009
 */
public class DiaFluxCaseObject extends SessionObject {

	private final List<FlowRun> runs;
	private final Map<SnapshotNode, Long> latestSnapshotTime = new HashMap<SnapshotNode, Long>();

	// fields for trace mode
	private static boolean traceMode = false;
	private final Collection<Node> tracedActiveNodes = new HashSet<Node>();
	private final Collection<Edge> tracedActiveEdges = new HashSet<Edge>();

	public DiaFluxCaseObject(CaseObjectSource theSourceObject) {
		super(theSourceObject);
		this.runs = new ArrayList<FlowRun>();
	}

	/**
	 * Enables/disables the trace mode of the flux solver. The trace mode
	 * records information about the state before the last snapshot has been
	 * taken.
	 * 
	 * @created 01.03.2011
	 * @param traceMode the new trace mode state
	 */
	public static void setTraceMode(boolean traceMode) {
		DiaFluxCaseObject.traceMode = traceMode;
	}

	/**
	 * Returns if the flux solver is in trace mode, recording information about
	 * the state before the last snapshot has been taken.
	 * 
	 * @created 01.03.2011
	 * @return if the flux solver is in trace mode
	 */
	public static boolean isTraceMode() {
		return traceMode;
	}

	/**
	 * Clears the trace. All traced information about the last snapshot taken
	 * will be forgotten after this call.
	 * 
	 * @created 01.03.2011
	 */
	public void clearTrace() {
		this.tracedActiveNodes.clear();
		this.tracedActiveEdges.clear();
	}

	/**
	 * Adds a set of edges to the traced edges before the snapshot will be
	 * taken. The traced information are intended to describe the state before
	 * the last snapshot has been taken.
	 * 
	 * @created 01.03.2011
	 * @param edges the edges to be added.
	 */
	public void traceEdges(Collection<Edge> edges) {
		this.tracedActiveEdges.addAll(edges);
	}

	/**
	 * Adds a set of edges to the traced edges before the snapshot will be
	 * taken. The traced information are intended to describe the state before
	 * the last snapshot has been taken.
	 * 
	 * @created 01.03.2011
	 * @param edges the edges to be added.
	 */
	public void traceEdges(Edge... edges) {
		Collections.addAll(this.tracedActiveEdges, edges);
	}

	/**
	 * Adds a set of nodes to the traced nodes before the snapshot will be
	 * taken. The traced information are intended to describe the state before
	 * the last snapshot has been taken.
	 * 
	 * @created 01.03.2011
	 * @param nodes the ndoes to be added.
	 */
	public void traceNodes(Collection<Node> nodes) {
		this.tracedActiveNodes.addAll(nodes);
	}

	/**
	 * Adds a set of nodes to the traced nodes before the snapshot will be
	 * taken. The traced information are intended to describe the state before
	 * the last snapshot has been taken.
	 * 
	 * @created 01.03.2011
	 * @param nodes the nodes to be added.
	 */
	public void traceNodes(Node... nodes) {
		Collections.addAll(this.tracedActiveNodes, nodes);
	}

	/**
	 * Returns the set of traced edges. These are the active edges before the
	 * snapshot has been taken.
	 * 
	 * @created 01.03.2011
	 * @return the traced edges
	 */
	public Collection<Edge> getTracedEdges() {
		return Collections.unmodifiableCollection(this.tracedActiveEdges);
	}

	/**
	 * Returns the set of traced nodes. These are the active nodes before the
	 * snapshot has been taken.
	 * 
	 * @created 01.03.2011
	 * @return the traced nodes
	 */
	public Collection<Node> getTracedNodes() {
		return Collections.unmodifiableCollection(this.tracedActiveNodes);
	}

	/**
	 * Records that a snapshot has been executed for this session in this
	 * propagation. This is required to detect cyclic propagations in flowcharts
	 * with one or more snapshots in the cycle.
	 * 
	 * @created 28.02.2011
	 * @param node the snapshot node that have been snapshoted
	 * @param session the session of this propagation
	 */
	public void snapshotDone(SnapshotNode node, Session session) {
		long time = session.getPropagationManager().getPropagationTime();
		this.latestSnapshotTime.put(node, new Long(time));
	}

	/**
	 * Returns the latest (most recent) time a snapshot has been taken. If no
	 * snapshot has been taken yet, null is returned.
	 * 
	 * @created 01.03.2011
	 * @return the most recent snapshot time
	 */
	public Date getLatestSnaphotTime() {
		Collection<Long> values = this.latestSnapshotTime.values();
		if (values.isEmpty()) return null;

		long maxTime = Long.MIN_VALUE;
		for (Long time : values) {
			maxTime = Math.max(maxTime, time.longValue());
		}
		return new Date(maxTime);
	}

	private boolean snapshotAllowed(SnapshotNode node, Session session) {
		long currentTime = session.getPropagationManager().getPropagationTime();
		Long latestTime = this.latestSnapshotTime.get(node);
		return (latestTime == null) || latestTime.longValue() < currentTime;
	}

	public void addRun(FlowRun run) {
		this.runs.add(run);
	}

	public void removeRun(FlowRun run) {
		this.runs.remove(run);
	}

	public List<FlowRun> getRuns() {
		return Collections.unmodifiableList(runs);
	}

	/**
	 * Returns all activated snapshots, but remove those that have been detected
	 * to be cyclic in this propagation.
	 * 
	 * @created 28.02.2011
	 * @param session the session of this propagation
	 * @return the snapshot nodes to be snapshoted
	 */
	public Collection<SnapshotNode> getActivatedSnapshots(Session session) {
		Collection<SnapshotNode> result = new HashSet<SnapshotNode>();
		for (FlowRun run : this.runs) {
			for (SnapshotNode node : run.getActivatedNodesOfClass(SnapshotNode.class)) {
				if (snapshotAllowed(node, session)) {
					result.add(node);
				}
			}
		}
		return Collections.unmodifiableCollection(result);
	}
}
