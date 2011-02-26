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
import de.d3web.diaFlux.inference.CallFlowAction;
import de.d3web.diaFlux.inference.FluxSolver;

/**
 * 
 * @author Reinhard Hatko
 * @created 10.08.10
 */
public class ComposedNode extends Node {

	private final CallFlowAction action;

	public ComposedNode(String id, String name, CallFlowAction action) {
		super(id, name);
		this.action = action;
	}

	@Override
	public void activate(Session session, FlowRun run) {
		action.doIt(session, run, session.getPSMethodInstance(FluxSolver.class));

	}

	@Override
	public void deactivate(Session session, FlowRun run) {
		action.undo(session, run, session.getPSMethodInstance(FluxSolver.class));
	}

	// TODO: vb: rename to getCalledFlowName
	// TODO: vb: add method getCalledFlow() -> Flow
	public String getFlowName() {
		return action.getFlowName();
	}

	// TODO: vb: rename to getCalledStartNodeName
	// TODO: vb: add method getCalledStartNode -> StartNode
	public String getStartNodeName() {
		return action.getStartNodeName();
	}

}
