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
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.FlowRun;
import de.d3web.diaFlux.flow.StartNode;

/**
 * 
 * @author Reinhard Hatko
 * @created 03.11.2009
 */
public class CallFlowAction extends PSAction {

	private final String flowName;
	private final String startNodeName;

	public CallFlowAction(String flow, String node) {
		this.flowName = flow;
		this.startNodeName = node;
	}

	@Override
	public void doIt(Session session, Object source, PSMethod psmethod) {

		StartNode startNode = DiaFluxUtils.findStartNode(session.getKnowledgeBase(), flowName,
				startNodeName);

		if (startNode == null) {
			Logger.getLogger(CallFlowAction.class.getName()).severe(
					"Could not find start node '" + startNodeName + "' in flow '" +
							flowName + "'.");
			return;
		}

		FluxSolver.activateNode(startNode, (FlowRun) source, session);

	}

	@Override
	public void undo(Session session, Object source, PSMethod psmethod) {

		StartNode startNode = DiaFluxUtils.findStartNode(session.getKnowledgeBase(), flowName,
				startNodeName);

		if (startNode == null) {
			Logger.getLogger(CallFlowAction.class.getName()).severe(
					"Could not find start node '" + startNodeName + "' in flow '" +
							flowName + "'.");
			return;
		}

		FluxSolver.deactivateNode(startNode, (FlowRun) source, session);

	}

	@Override
	public List<TerminologyObject> getBackwardObjects() {
		return new ArrayList<TerminologyObject>(0);
	}

	public String getFlowName() {
		return flowName;
	}

	public String getStartNodeName() {
		return startNodeName;
	}

}
