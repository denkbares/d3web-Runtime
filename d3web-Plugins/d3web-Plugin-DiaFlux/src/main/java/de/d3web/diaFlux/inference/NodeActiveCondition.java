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
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.FlowRun;

/**
 * 
 * @author Reinhard Hatko
 * @created 21.10.2010
 */
public class NodeActiveCondition implements Condition {

	private final String flowName;
	private final String nodeName;

	/**
	 * @param flowName
	 * @param nodeName
	 */
	public NodeActiveCondition(String flowName, String nodeName) {
		this.flowName = flowName;
		this.nodeName = nodeName;
	}

	@Override
	public boolean eval(Session session) throws NoAnswerException, UnknownAnswerException {

		// TODO ATM this is only necessary for exit nodes
		// But this type of condition per se allows to check for every node
		EndNode exitNode = DiaFluxUtils.findExitNode(session.getKnowledgeBase(), flowName, nodeName);

		for (FlowRun run : DiaFluxUtils.getDiaFluxCaseObject(session).getRuns()) {

			if (run.isActive(exitNode)) return true;
		}

		return false;
		// return DiaFluxUtils.getNodeData(exitNode, session).isSupported();
	}

	@Override
	public List<? extends TerminologyObject> getTerminalObjects() {
		// TODO
		return Collections.emptyList();
	}

	public String getFlowName() {
		return flowName;
	}

	public String getNodeName() {
		return nodeName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((flowName == null) ? 0 : flowName.hashCode());
		result = prime * result + ((nodeName == null) ? 0 : nodeName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		NodeActiveCondition other = (NodeActiveCondition) obj;
		if (flowName == null) {
			if (other.flowName != null) return false;
		}
		else if (!flowName.equals(other.flowName)) return false;
		if (nodeName == null) {
			if (other.nodeName != null) return false;
		}
		else if (!nodeName.equals(other.nodeName)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "NodeActiveCondition [flowName=" + flowName + ", nodeName=" + nodeName + "]";
	}

}
