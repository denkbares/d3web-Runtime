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
package de.d3web.diaFlux.inference;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.Rule;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.flow.ISupport;
import de.d3web.diaFlux.flow.NodeSupport;
import de.d3web.diaFlux.flow.RuleSupport;
import de.d3web.diaFlux.flow.StartNode;

/**
 * 
 * @author Reinhard Hatko
 * @created 03.11.2009
 */
public class CallFlowAction extends PSAction {

	private final String flowName;
	private final String startNodeName;
	private ISupport support;

	public CallFlowAction(String flow, String node) {
		this.flowName = flow;
		this.startNodeName = node;
	}

	@Override
	public PSAction copy() {
		return new CallFlowAction(flowName, startNodeName);
	}

	@Override
	public void doIt(Session session, Object source, PSMethod psmethod) {

		StartNode startNode = DiaFluxUtils.findStartNode(session, flowName, startNodeName);

		if (startNode == null) {
			Logger.getLogger(CallFlowAction.class.getName()).severe(
					"Could not find start node '" + startNodeName + "' in flow '" +
							flowName + "'.");
		}

		// TODO not very nice
		if (source instanceof Rule) {
			support = new RuleSupport((Rule) source);
		}
		else if (source instanceof INode) {
			support = new NodeSupport((INode) source);
		}
		else {
			throw new UnsupportedOperationException("Unknown source type " + source);
		}

		FluxSolver.activate(session, startNode, support);

	}

	@Override
	public void undo(Session session, Object source, PSMethod psmethod) {

		StartNode startNode = DiaFluxUtils.findStartNode(session, flowName, startNodeName);

		if (startNode == null) {
			Logger.getLogger(CallFlowAction.class.getName()).severe(
					"Could not find start node '" + startNodeName + "' in flow '" +
							flowName + "'.");
		}

		// This action can have already been undone when this composed node
		// starts propagating after the exit node has been reached by ...
		if (support != null) {

			// TODO this is most likely unnecessary, because this node has no
			// support any more
			// then, calling propagate on the startnode, should remove the
			// invalid
			// support by this node
			FluxSolver.removeSupport(session, startNode, support);
			
			
			support = null;
		}

		DiaFluxUtils.getPath(startNode, session).propagate(session, startNode);
	}

	@Override
	public List<NamedObject> getBackwardObjects() {
		return new ArrayList<NamedObject>(0);
	}

	public String getFlowName() {
		return flowName;
	}

	public String getStartNodeName() {
		return startNodeName;
	}

}
