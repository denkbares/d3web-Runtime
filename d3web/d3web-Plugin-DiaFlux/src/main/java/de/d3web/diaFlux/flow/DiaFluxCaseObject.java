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
import java.util.List;
import java.util.Map;

import de.d3web.core.session.CaseObjectSource;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.diaFlux.inference.IPath;

/**
 *
 * @author Reinhard Hatko
 *
 *         Created on: 04.11.2009
 */
public class DiaFluxCaseObject extends SessionObject {

	private final Map<Flow, IPath> map;
	private long lastPropagationTime = Long.MIN_VALUE;
	private final List<SnapshotNode> snapshotNodes;

	public DiaFluxCaseObject(CaseObjectSource theSourceObject, Map<Flow, IPath> flowdatas) {
		super(theSourceObject);
		this.map = Collections.unmodifiableMap(flowdatas);
		this.snapshotNodes = new ArrayList<SnapshotNode>();
	}

	public IPath getPath(Flow flow) {
		return map.get(flow);
	}

	/**
	 * Returns an unmodifiable Collection of the currently active pathes
	 *
	 */
	public Collection<IPath> getActivePathes() {
		List<IPath> result = new ArrayList<IPath>();

		for (IPath path : map.values()) {
			if (path.isActive()) result.add(path);
		}

		return result;
	}

	/**
	 * Checks if a new propagation has started by comparing the current
	 * propagation time with the last one. If a new propagation has started the
	 * (new) current propagation time is set.
	 *
	 * Returns, if a new propagation has started.
	 *
	 * @param session the current session
	 */
	public boolean checkPropagationTime(Session session) {

		// first call to propagate from 'init' is not within a propagation
		if (!session.getPropagationManager().isInPropagation()) return false;

		long propagationTime = session.getPropagationManager().getPropagationTime();

		if (propagationTime > lastPropagationTime) {
			lastPropagationTime = propagationTime;
			return true;
		}
		else {
			return false;
		}


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

	public List<SnapshotNode> getRegisteredSnapshots(){
		return Collections.unmodifiableList(snapshotNodes);
	}

	public void clearRegisteredSnapshots() {
		snapshotNodes.clear();
	}


}
