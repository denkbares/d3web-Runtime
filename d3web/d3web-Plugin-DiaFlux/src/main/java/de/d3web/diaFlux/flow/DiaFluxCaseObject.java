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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	private final Set<SnapshotNode> snapshotNodes;
	private final List<FlowRun> runs;

	public DiaFluxCaseObject(CaseObjectSource theSourceObject) {
		super(theSourceObject);
		this.snapshotNodes = new HashSet<SnapshotNode>();
		this.runs = new ArrayList<FlowRun>();
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
	 * Registers the supplied snapshot node for taking a snapshot. The snapshot
	 * is taking after the current propagation is finished, unless the snapshot
	 * is unregistered before that.
	 * 
	 * The snapshot can not be taken immediately at reaching the SnapshotNode,
	 * because it could loose its support during the same propagation cycle.
	 * 
	 * 
	 * @param node
	 * @param session
	 */
	public void registerSnapshot(SnapshotNode node, Session session) {
		this.snapshotNodes.add(node);

	}

	public void unregisterSnapshot(SnapshotNode node, Session session) {
		this.snapshotNodes.remove(node);

	}

	public Set<SnapshotNode> getRegisteredSnapshots() {
		return Collections.unmodifiableSet(snapshotNodes);
	}

	public void clearRegisteredSnapshots() {
		snapshotNodes.clear();
	}

}
