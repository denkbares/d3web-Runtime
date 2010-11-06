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
import de.d3web.diaFlux.CallFlowAction;
import de.d3web.diaFlux.inference.DiaFluxUtils;

/**
 *
 * @author Reinhard Hatko
 * @created 10.08.10
 */
public class ComposedNode extends ActionNode {

	public ComposedNode(String id, String name, CallFlowAction action) {
		super(id, name, action);
	}


	@Override
	public void takeSnapshot(Session session, SnapshotNode snapshotNode) {
		super.takeSnapshot(session, snapshotNode);
		CallFlowAction action = (CallFlowAction) getAction();
		StartNode startNode = DiaFluxUtils.findStartNode(session, action.getFlowName(),
				action.getStartNodeName());

		// TODO this will need some special treatment
		// as taking the snapshot usually does not start at the start node
		startNode.takeSnapshot(session, snapshotNode);

	}

}
