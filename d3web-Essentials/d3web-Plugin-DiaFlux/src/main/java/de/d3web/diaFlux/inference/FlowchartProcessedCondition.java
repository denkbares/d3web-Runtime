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

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowRun;
import de.d3web.diaFlux.flow.Node;
import de.d3web.utils.Log;

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

		Flow flow = DiaFluxUtils.getFlowSet(session).get(flowName);

		if (flow == null) {
			Log.warning("Flowchart '" + flowName + "' not found.");
			return false;
		}

		// if one of the exit nodes is supported, then the flowchart has been
		// processed
		for (Node node : flow.getExitNodes()) {

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

	public String getFlowName() {
		return flowName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getClass().getName().hashCode();
		result = prime * result + ((flowName == null) ? 0 : flowName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		FlowchartProcessedCondition other = (FlowchartProcessedCondition) obj;
		if (flowName == null) {
			if (other.flowName != null) return false;
		}
		else if (!flowName.equals(other.flowName)) return false;
		return true;
	}

}
