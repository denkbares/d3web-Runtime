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

import de.d3web.core.inference.PSAction;
import de.d3web.core.session.Session;

/**
 * 
 * @author Reinhard Hatko
 * 
 */
public class EndNode extends ActionNode {

	// TODO action dirty Hack
	public EndNode(String id, String name, PSAction action) {
		super(id, name, action);
	}

	@Override
	protected boolean addOutgoingEdge(IEdge edge) {
		throw new UnsupportedOperationException("can not add outgoing edge to end node");
	}

	@Override
	public void takeSnapshot(Session session, SnapshotNode snapshotNode, List<INode> nodes) {
		// TODO hopefully temporary until FCTerminology is removed
		// this removes the fact, that this endnode has been reached.
		// otherwise this outgoing edge would be taken as soon as the composed
		// node is activated again after as snapshot
		undoAction(session);

		super.takeSnapshot(session, snapshotNode, nodes);
	}

}
