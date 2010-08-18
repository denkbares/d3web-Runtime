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

package de.d3web.diaFlux;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.session.Session;

/**
 * 
 * @author Reinhard Hatko
 * @created 12.08.2010
 */
public class ComposedNodeAction extends IndicateFlowAction {

	public ComposedNodeAction(String flow, String node) {
		super(flow, node);
	}

	@Override
	public void doIt(Session session, Object source, PSMethod psmethod) {

		// Logger.getLogger(getClass().getName()).log(Level.FINE,
		// ("Indicating Startnode '" + getStartNodeName() + "' of flow '" +
		// getFlowName() + "'."));
		//
		// StartNode startNode = DiaFluxUtils.findStartNode(session,
		// getFlowName(), getStartNodeName());
		// INode composedNode = (INode) source;
		// support = new NodeSupport(composedNode);
		//
		// FluxSolver.indicateFlowFromNode(session, composedNode, startNode,
		// support);
	}

}
