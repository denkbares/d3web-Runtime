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
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.EndNode;

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
		EndNode exitNode = DiaFluxUtils.findExitNode(session, flowName, nodeName);

		return DiaFluxUtils.getNodeData(exitNode, session).isSupported();
	}

	@Override
	public List<? extends NamedObject> getTerminalObjects() {
		// TODO
		return Collections.emptyList();
	}

	@Override
	public Condition copy() {
		return new NodeActiveCondition(flowName, nodeName);
	}

	public String getFlowName() {
		return flowName;
	}

	public String getNodeName() {
		return nodeName;
	}

}
