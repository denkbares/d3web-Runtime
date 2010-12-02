/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.FluxSolver;

/**
 * 
 * @author Reinhard Hatko
 * @created 30.11.2010
 */
public class ComposedNodeData extends NodeData {

	public ComposedNodeData(ComposedNode node) {
		super(node);
	}

	@Override
	public void propagate(Session session) {
		super.propagate(session);

		for (IEdge edge : getNode().getIncomingEdges()) {
			if (DiaFluxUtils.getEdgeData(edge, session).hasFired()) {

				// if one of the incoming edges has fired
				// then the calling start node should be active
				return;
			}

		}

		// no incoming edge has fired -> undoAction
		// check for supported is not enough
		// SSN inside the called flow can maintain support for the CN
		// but the repeated caal to the startnode must be retracted, if no edge
		// supports the re-calling the start node

		// TODO can this trigger a NPE, because the CFA has not been done
		// before? or already been undone?
		// then the support the CFA wants to retract is null

		// this also tries to undo the action after propagating to the exit node
		// of the called flow after taking a snapshot.
		// but then the support can of course not be removed because this has
		// been done while taking the snaphsot
		FluxSolver.undoAction(session, getNode());

	}

}
