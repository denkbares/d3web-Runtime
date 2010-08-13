/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.diaFlux;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.Rule;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.ISupport;
import de.d3web.diaFlux.flow.RuleSupport;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.FluxSolver;

/**
 *
 * @author Reinhard Hatko
 * Created on: 03.11.2009
 */
public class IndicateFlowAction extends PSAction {

	private final String flowName;
	private final String startNodeName;
	protected ISupport support;

	public IndicateFlowAction(String flowName, String startNodeName) {
		this.flowName = flowName;
		this.startNodeName = startNodeName;
	}

	@Override
	public PSAction copy() {
		return new IndicateFlowAction(flowName, startNodeName);
	}

	@Override
	public void doIt(Session theCase, Object source, PSMethod psmethod) {


		Logger.getLogger(getClass().getName()).log(Level.FINE,
				("Indicating Startnode '" + startNodeName + "' of flow '" + flowName + "'."));

		StartNode startNode = DiaFluxUtils.findStartNode(theCase, flowName, startNodeName);

		if (startNode == null) //start node could not be found. May happen during wiki startup
			return;

		support = new RuleSupport((Rule) source);

		FluxSolver.indicateFlowFromAction(theCase, startNode, support);

	}

	@Override
	public List<? extends NamedObject> getTerminalObjects() {
		return new ArrayList<NamedObject>(0);
	}

	@Override
	public void undo(Session theCase, Object source, PSMethod psmethod) {
		//TODO remove indication of Flow
		System.out.println("+++TODO inIndicateFlowAction.undo()");
		StartNode startNode = DiaFluxUtils.findStartNode(theCase, flowName, startNodeName);

		FluxSolver.removeSupport(theCase, startNode, support);

	}

	public String getFlowName() {
		return flowName;
	}

	public String getStartNodeName() {
		return startNodeName;
	}

}
