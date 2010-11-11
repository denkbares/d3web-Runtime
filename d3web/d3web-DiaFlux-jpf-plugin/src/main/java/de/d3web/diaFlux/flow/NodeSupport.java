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

import java.util.logging.Logger;

import de.d3web.core.session.Session;
import de.d3web.diaFlux.inference.DiaFluxUtils;

/**
 *
 * @author Reinhard Hatko
 *
 *         Created: 20.12.2009
 */
public class NodeSupport implements ISupport {

	private final INode node;

	public NodeSupport(INode node) {
		if (node == null) throw new IllegalArgumentException("node must not be null.");

		this.node = node;
	}

	public boolean isValid(Session theCase) {
		return DiaFluxUtils.getNodeData(node, theCase).isActive();
	}

	@Override
	public void remove(Session session, INodeData nodeData) {
		if (!(nodeData instanceof StartNodeData)) {
			Logger.getLogger(getClass().getName()).warning("*******Unexpected NodeData.");
			return;
		}

		StartNodeData data = (StartNodeData) nodeData;

		// if the startnode is no longer supported by this support,
		// it can no longer be called by this.node
		data.removeCallingNode(DiaFluxUtils.getNodeData(getNode(), session));

	}

	public INode getNode() {
		return node;
	}

	@Override
	public String toString() {
		return super.toString() + "[" + node + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		NodeSupport other = (NodeSupport) obj;
		if (node == null) {
			if (other.node != null) return false;
		}
		else if (!node.equals(other.node)) return false;
		return true;
	}

}
