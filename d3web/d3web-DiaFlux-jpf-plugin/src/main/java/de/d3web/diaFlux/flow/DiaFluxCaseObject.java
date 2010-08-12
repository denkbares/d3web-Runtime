/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
import de.d3web.diaFlux.inference.Path;
import de.d3web.diaFlux.inference.PathReference;

/**
 *
 * @author Reinhard Hatko
 * Created on: 04.11.2009
 */
public class DiaFluxCaseObject extends SessionObject {

	private final Map<Flow, FlowData> map;
	private final List<IPath> pathes;


	public DiaFluxCaseObject(CaseObjectSource theSourceObject, Map<Flow, FlowData> flowdatas) {
		super(theSourceObject);
		this.map = Collections.unmodifiableMap(flowdatas);
		this.pathes = new ArrayList<IPath>(5);
	}




	public FlowData getFlowData(Flow flow) {
		return map.get(flow);
	}


	/**
	 * Returns an unmodifiable Collection of the current pathes
	 *
	 */
	public Collection<IPath> getPathes() {
		return Collections.unmodifiableCollection(pathes);
	}


	/**
	 * Adds the provided path to the currently active pathes of the session.
	 * If a path starting at the StartNode of the path is already contained,
	 * the provided path is NOT added as an active Path.
	 * @param session
	 * @param support
	 * @param path to be add as active Path
	 *
	 * @return true if the path is a new active path (i.e. no path starting at the start
	 * node of the provided path is already active), false otherwise
	 */
	public boolean addPath(Session session, StartNode startNode, ISupport support) {

		if (!getNodeData(startNode).isActive())
			throw new IllegalStateException("StartNode '" + startNode + "' does not have support and can not be added.");

		Path path = new Path(startNode, support);

		pathes.add(path);
		return true;

	}


	public void addPathRef(PathReference reference) {
		pathes.add(reference);

	}

	/**
	 *
	 * @param path the path to remove
	 * @return
	 */
	public boolean removePath(IPath path) {

		if (path.isEmpty()) {
			pathes.remove(path);
			return true;
		} else {

			INode firstNode = path.getFirstNode();
			INodeData nodeData = getNodeData(firstNode);

			if (nodeData.isActive())
				throw new IllegalStateException("Path '" + path + "' is can not be removed as its first node '" + firstNode + "' is still active.");

			pathes.remove(path);
			return true;

		}
	}




	/**
	 * @param node
	 * @return
	 */
	public INodeData getNodeData(INode node) {
		Flow flow = node.getFlow();
		return getFlowData(flow).getNodeData(node);
	}


}
