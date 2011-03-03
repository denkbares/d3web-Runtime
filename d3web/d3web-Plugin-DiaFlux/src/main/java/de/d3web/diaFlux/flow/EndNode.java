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

import de.d3web.core.session.Session;
import de.d3web.diaFlux.inference.FluxSolver;

/**
 * 
 * @author Reinhard Hatko
 * 
 */
public class EndNode extends AbstractNode {

	public EndNode(String id, String name) {
		super(id, name);
	}

	@Override
	protected boolean addOutgoingEdge(Edge edge) {
		throw new UnsupportedOperationException("Can not add outgoing edge to exit node.");
	}

	@Override
	public void execute(Session session, FlowRun run) {
		// TODO: vb: only check nodes calling this sheet by
		// Flow.getCallingNodes()
		for (ComposedNode node : run.getActiveNodesOfClass(ComposedNode.class)) {
			FluxSolver.checkSuccessorsOnActivation(node, run, session);
		}
	}

	@Override
	public void retract(Session session, FlowRun run) {
		// TODO: vb: only check nodes calling this sheet by
		// Flow.getCallingNodes()
		for (ComposedNode node : run.getActiveNodesOfClass(ComposedNode.class)) {
			FluxSolver.checkSuccessorsOnDeactivation(node, run, session);
		}
	}

}
