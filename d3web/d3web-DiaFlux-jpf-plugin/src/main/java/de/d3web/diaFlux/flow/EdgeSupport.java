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

import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.inference.DiaFluxUtils;

/**
 *
 * @author Reinhard Hatko
 *
 *         Created: 20.12.2009
 */
public class EdgeSupport implements ISupport {

	private final IEdge edge;

	public EdgeSupport(IEdge edge) {
		if (edge == null) throw new IllegalArgumentException("node must not be null.");

		this.edge = edge;
	}

	public boolean isValid(Session session) {

		INode startNode = edge.getStartNode();

		boolean active = DiaFluxUtils.getNodeData(startNode, session).isActive();

		// if the starting node is not supported, this support is also not valid
		if (!active) {
			return false;
		} // starting node is supported, now it depends on the condition
		else {
			try {
				return edge.getCondition().eval(session);

			}
			catch (NoAnswerException e) {
				return false;
			}
			catch (UnknownAnswerException e) {
				return false;
			}
		}

	}

	/**
	 * @return the edge
	 */
	public IEdge getEdge() {
		return edge;
	}

	@Override
	public String toString() {
		return "EdgeSupport: " + edge;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edge == null) ? 0 : edge.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		EdgeSupport other = (EdgeSupport) obj;
		if (edge == null) {
			if (other.edge != null) return false;
		}
		else if (!edge.equals(other.edge)) return false;
		return true;
	}

}
