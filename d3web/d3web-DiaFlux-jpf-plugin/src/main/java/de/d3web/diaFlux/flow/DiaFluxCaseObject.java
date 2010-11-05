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
	private IPath activePath;

	public DiaFluxCaseObject(CaseObjectSource theSourceObject, Map<Flow, IPath> flowdatas) {
		super(theSourceObject);
		this.map = Collections.unmodifiableMap(flowdatas);
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

	public void setActivePath(IPath activePath) {
		this.activePath = activePath;
	}

	public IPath getActivePath() {
		return activePath;
	}

	/**
	 * Adds the provided path to the currently active pathes of the session.
	 *
	 * @param session
	 * @param support
	 * @param path to be add as active Path
	 *
	 * @return true if the path is a new active path (i.e. no path starting at
	 *         the start node of the provided path is already active), false
	 *         otherwise
	 */
	// public boolean addPath(Session session, StartNode startNode, ISupport
	// support) {
	//
	// if (!getNodeData(startNode).isActive()) {
	// throw new IllegalStateException("StartNode '"
	// + startNode + "' does not have support and can not be added.");
	// }
	//
	// IPath path = DiaFluxUtils.getPath(startNode.getFlow(), session);
	//
	// path.activate(startNode, support, session);
	//
	// pathes.add(path);
	// return true;
	//
	// }


	/**
	 *
	 * @param path the path to remove
	 * @return
	 */
	// public boolean removePath(IPath path) {
	//
	// if (path.isEmpty()) {
	// pathes.remove(path);
	// return true;
	// }
	// else {
	//
	// pathes.remove(path);
	// return true;
	//
	// }
	// }

	/**
	 * @param node
	 * @return
	 */
	public INodeData getNodeData(INode node) {
		Flow flow = node.getFlow();
		return getPath(flow).getNodeData(node);
	}

	/**
	 * Checks if a new propagation has started by comparing the current
	 * propagation time with the last one. Returns, if a new propagation has
	 * started.
	 *
	 * @param session the current session
	 */
	public boolean checkPropagationTime(Session session) {

		// first call to propagate from 'init' is not within a propagation

		if (!session.getPropagationManager().isInPropagation()) return false;

		long propagationTime = session.getPropagationManager().getPropagationTime();

		boolean newPropagation;
		if (propagationTime > lastPropagationTime) {
			newPropagation = true;
		}
		else {
			newPropagation = false;
		}

		lastPropagationTime = propagationTime;

		return newPropagation;
	}

	/**
	 *
	 * @param edge
	 * @return
	 */
	public EdgeData getEdgeData(IEdge edge) {
		return null;
	}

}
