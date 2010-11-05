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

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.flow.ISupport;
import de.d3web.diaFlux.flow.SnapshotNode;

/**
 * @author Reinhard Hatko
 * @created: 08.08.2010
 */
public interface Entry {

	/**
	 *
	 * @return the node which was reached with this entry
	 */
	INode getNode();

	/**
	 * @return the support of this entry. The node this entry represents can
	 *         though have other supports
	 */
	ISupport getSupport();

	/**
	 *
	 *
	 * @param session the current session
	 * @param changes the changes of the current propagation
	 * @return
	 */
	boolean propagate(Session session, Collection<PropagationEntry> changes);


	/**
	 *
	 *
	 * @param session
	 * @return true, if the support was successfully removed, false otherwise
	 */
	boolean removeSupport(Session session);

	/**
	 * 
	 * 
	 * @param session the current session
	 * @param node the node, the snapshot starts at
	 * @return returns true
	 */
	boolean takeSnapshot(Session session, SnapshotNode node);

}