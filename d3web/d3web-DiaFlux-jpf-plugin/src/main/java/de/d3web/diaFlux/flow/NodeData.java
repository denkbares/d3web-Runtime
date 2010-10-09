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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.SessionObject;

public class NodeData extends SessionObject implements INodeData {

	private final List<ISupport> supports;

	public NodeData(INode node) {
		super(node);

		supports = new ArrayList<ISupport>(2);
	}

	@Override
	public INode getNode() {
		return (INode) getSourceObject();
	}

	@Override
	public boolean isActive() {
		return !supports.isEmpty();
	}

	@Override
	public boolean addSupport(Session session, ISupport support) {
		if (!support.isValid(session)) throw new IllegalStateException(
				"Can not add invalid support '" + support + "'.");

		if (supports.contains(support)) {
			String msg = "Support '" + support + "' is already contained in Node '" + getNode()
					+ "'.";
			Logger.getLogger(getClass().getName()).log(Level.WARNING, msg);
			return false;
		}

		return supports.add(support);
	}

	@Override
	public boolean removeSupport(ISupport support) {
		if (!supports.contains(support)) {
			return false;
		}
		return supports.remove(support);
	}

	@Override
	public List<ISupport> getSupports() {
		return Collections.unmodifiableList(supports);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getNode() + ", active=" + isActive()
				+ ", support=" + supports.size() + "]" + Integer.toHexString(hashCode());
	}

}
