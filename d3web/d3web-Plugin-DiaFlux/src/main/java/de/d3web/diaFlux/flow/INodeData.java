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
 * @author Reinhard Hatko
 * @created: 10.09.2009
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
	 * Returns if this node is supported.
	 * 
	 * @created 06.11.2010
	 * @return
	 */
	boolean isSupported();

	/**
	 * Returns the current supports of this node
	 * 
	 * @return s the current supports of this node
	 */
	List<ISupport> getSupports();

	/**
	 * Adds the supplied support to the this NodeData.
	 * 
	 * 
	 * @param session the session
	 * @param support
	 * @return s true if the support could be added, ie if the support was not
	 *         already contained
	 */
	boolean addSupport(Session session, ISupport support);

	/**
	 * Removes the supplied support.
	 * 
	 * @param session the session
	 * @param support the support to be removed
	 * @return true, if the support was contained before and removed
	 */
	boolean removeSupport(Session session, ISupport support);

	/**
	 * This method checks the support of the corresponding node. All invalid
	 * support is removed.
	 * 
	 * @param session the session
	 * @return s if the corresponding node is still supported
	 */
	boolean checkSupport(Session session);

}
