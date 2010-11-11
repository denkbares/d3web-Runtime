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

package de.d3web.diaFlux.flow;

import de.d3web.core.session.Session;

/**
 * Interface for different kinds of Support, that can support a node.
 *
 * @author Reinhard Hatko
 *
 *         Created: 20.12.2009
 *
 */
public interface ISupport {

	/**
	 * Checks if this support is valid.
	 *
	 * @param session
	 * @return true, if this support is valid, false otherwise
	 */
	boolean isValid(Session session);

	/**
	 * This method is called, when the support is removed from the node. Can
	 * perform some cleanup.
	 *
	 * @param nodeData the NodeData where this support is being removed from
	 */
	void remove(Session session, INodeData nodeData);

}
