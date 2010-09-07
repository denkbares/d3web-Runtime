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

import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.flow.INodeData;
import de.d3web.diaFlux.flow.ISupport;

public abstract class AbstractEntry implements Entry {

	protected final INode node;
	protected final ISupport support;

	public AbstractEntry(INode node, ISupport support) {
		this.node = node;
		this.support = support;
	}

	@Override
	public INode getNode() {
		return node;
	}

	@Override
	public ISupport getSupport() {
		return support;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + node + "]@"
				+ Integer.toHexString(hashCode());
	}

	/**
	 * Checks if this entry's support is still valid. If the support is no
	 * longer valid, it is removed from the according NodeData. If it is valid,
	 * nothing is done. Returns if the node is still active, i.e. if this entrys
	 * support is no longer valid,it returns if this node has other support.
	 *
	 * @param session
	 * @return if the node is still active
	 */
	protected boolean checkSupport(Session session) {
		INodeData nodeData = DiaFluxUtils.getNodeData(getNode(), session);

		if (!support.isValid(session)) {
			FluxSolver.removeSupport(session, getNode(), getSupport());
		}

		return nodeData.isActive();
	}

	@Override
	public boolean removeSupport(Session session) {
		return FluxSolver.removeSupport(session, getNode(), getSupport());
	}

}