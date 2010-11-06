/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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

import java.util.List;

import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.EdgeData;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.IEdge;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.flow.INodeData;
import de.d3web.diaFlux.flow.ISupport;
import de.d3web.diaFlux.flow.SnapshotNode;
import de.d3web.diaFlux.flow.StartNode;

/**
 *
 * @author Reinhard Hatko
 * @created 12.08.2010
 */
public interface IPath {


	boolean propagate(Session session, INode node);

	boolean takeSnapshot(Session session, SnapshotNode snapshotNode, INode node);

	boolean isEmpty();

	Flow getFlow();

	/**
	 *
	 * @created 05.11.2010
	 * @return
	 */
	List<INode> getActiveNodes();

	/**
	 *
	 * @created 05.11.2010
	 * @return
	 */
	List<IEdge> getActiveEdges();


	boolean isActive();


	EdgeData getEdgeData(IEdge edge);

	INodeData getNodeData(INode node);

	void activate(StartNode startNode, ISupport support, Session session);

}