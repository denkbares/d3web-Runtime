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

import org.jetbrains.annotations.NotNull;

import de.d3web.core.inference.condition.Condition;

/**
 * Represents a directed connection between two node instances of the same flow. If is optionally fitted with a
 * condition as a guard, indicating when the edge is activated after the start node is already active.
 *
 * @author Reinhard Hatko
 */
public interface Edge extends DiaFluxElement {

	/**
	 * Returns the node where this edge is starting from. This edge is in that node's {@link Node#getOutgoingEdges()}
	 * list.
	 *
	 * @return the node this edge starts at
	 */
	@NotNull
	Node getStartNode();

	/**
	 * Returns the node this edge is pointing towards. This edge is in that node's {@link Node#getIncomingEdges()}
	 * list.
	 *
	 * @return the node this edge ends at
	 */
	@NotNull
	Node getEndNode();

	/**
	 * The guard of the edge, to indicate than this edge can be followed. The guard is only relevant if the start node
	 * is already active.
	 *
	 * @return the edges guard
	 */
	Condition getCondition();
}
