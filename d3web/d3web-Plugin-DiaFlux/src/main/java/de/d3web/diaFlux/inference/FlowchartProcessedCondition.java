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
package de.d3web.diaFlux.inference;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowRun;
import de.d3web.diaFlux.flow.INode;

/**
 * 
 * @author Reinhard Hatko
 * @created 21.10.2010
 */
public class FlowchartProcessedCondition implements Condition {

	private final String flowName;

	/**
	 * @param flowName
	 */
	public FlowchartProcessedCondition(String flowName) {
		this.flowName = flowName;
	}

	@Override
	public boolean eval(Session session) throws NoAnswerException, UnknownAnswerException {

		Flow flow = DiaFluxUtils.getFlowSet(session).getByName(flowName);

		if (flow == null) {
			Logger.getLogger(getClass().getName()).warning(
					"Flowchart '" + flowName + "' not found.");
			return false;
		}

		// if one of the exit nodes is supported, then the flowchart has been
		// processed
		for (INode node : flow.getExitNodes()) {

			List<FlowRun> runs = DiaFluxUtils.getDiaFluxCaseObject(session).getRuns();

			for (FlowRun flowRun : runs) {
				if (flowRun.isActive(node)) return true;
			}

		}

		return false;
	}

	@Override
	public List<? extends TerminologyObject> getTerminalObjects() {
		// TODO
		return Collections.emptyList();
	}

	@Override
	public Condition copy() {
		return new FlowchartProcessedCondition(flowName);
	}

	public String getFlowName() {
		return flowName;
	}

}
