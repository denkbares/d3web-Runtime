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

import java.util.List;

import de.d3web.core.session.Session;

/**
 *
 * @author Reinhard Hatko Created: 10.09.2009
 *
 */
public interface INodeData {

	/**
	 * Returns the corresponding node of this NodeData instance.
	 *
	 * @return
	 */
	INode getNode();

	/**
	 * Returns if this node is active, ie if it has support.
	 *
	 * @created 06.11.2010
	 * @return
	 */
	boolean isActive();

	/**
	 * Adds the suplied support to the this NodeData.
	 *
	 *
	 * @param session
	 * @param support
	 * @return s true if the support could be added, ie if the support was not
	 *         already contained
	 */
	boolean addSupport(Session session, ISupport support);

	/**
	 * 1. Check Support
	 *
	 * 2. Remove invalid support
	 *
	 * 3. Undo Action if not supported
	 *
	 *
	 * @param session
	 */
	void propagate(Session session);

	/**
	 * Removes the supplied support.
	 *
	 * @param support
	 * @return true, if the support was contained before and removed
	 */
	boolean removeSupport(ISupport support);

	// TODO remove
	List<ISupport> getSupports();

	/**
	 * This method is only to be called during taking a snapshot. It removes all
	 * support from this node.
	 *
	 * @param session
	 */
	// public void reset(Session session);



}
