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

import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.FlowRun;
import de.d3web.diaFlux.flow.StartNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Reinhard Hatko
 * @created 03.11.2009
 */
public class CallFlowAction extends PSAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(CallFlowAction.class);

	private final String flowName;
	private final String startNodeName;
	private final ComposedNode sourceNode;

	public CallFlowAction(String flow, String node, ComposedNode sourceNode) {
		this.flowName = flow;
		this.startNodeName = node;
		this.sourceNode = sourceNode;
	}

	@Override
	public void doIt(Session session, Object source, PSMethod psmethod) {

		StartNode startNode = DiaFluxUtils.findStartNode(session.getKnowledgeBase(), flowName,
				startNodeName);

		if (startNode == null) {
			LOGGER.error("Could not find start node '" + startNodeName + "' in flow '" +
					flowName + "'.");
			return;
		}

		FluxSolver.addSupport(startNode, sourceNode, (FlowRun) source, session);

	}

	@Override
	public void undo(Session session, Object source, PSMethod psmethod) {

		StartNode startNode = DiaFluxUtils.findStartNode(session.getKnowledgeBase(), flowName,
				startNodeName);

		if (startNode == null) {
			LOGGER.error("Could not find start node '" + startNodeName + "' in flow '" +
					flowName + "'.");
			return;
		}

		FluxSolver.removeSupport(startNode, sourceNode, (FlowRun) source, session);

	}

	@Override
	public List<TerminologyObject> getBackwardObjects() {
		return new ArrayList<>(0);
	}

	public String getFlowName() {
		return flowName;
	}

	public String getStartNodeName() {
		return startNodeName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CallFlowAction that = (CallFlowAction) o;
		return (flowName != null ? flowName.equals(that.flowName) : that.flowName == null)
				&& (startNodeName != null ? startNodeName.equals(that.startNodeName) : that.startNodeName == null);
	}

	@Override
	public int hashCode() {
		int result = flowName != null ? flowName.hashCode() : 0;
		result = 31 * result + (startNodeName != null ? startNodeName.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "CALL[" + flowName + "(" + startNodeName + ")]";
	}
}
