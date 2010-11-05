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
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.FluxSolver;
import de.d3web.diaFlux.inference.IPath;

/**
 *
 * @author Reinhard Hatko
 * @created 10.08.10
 */
public class ComposedNode extends Node {

	private final String flowName;
	private final String startNodeName;
	private IPath path;

	public ComposedNode(String id, String flowName, String startNodeName) {
		super(id, flowName);
		this.flowName = flowName;
		this.startNodeName = startNodeName;
	}

	@Override
	public void doAction(Session session) {

		StartNode startNode = DiaFluxUtils.findStartNode(session, flowName, startNodeName);

		FluxSolver.indicateFlowFromAction(session, startNode, new NodeSupport(this));

		// this node now supports the startnode
//		FluxSolver.addSupport(session, startNode, new NodeSupport(this));
	}

	@Override
	public void undoAction(Session session) {
		StartNode startNode = DiaFluxUtils.findStartNode(session, flowName, startNodeName);

		FluxSolver.removeSupport(session, startNode, new NodeSupport(this));
	}

	@Override
	public SessionObject createCaseObject(Session session) {
		StartNode startNode = DiaFluxUtils.findStartNode(session, flowName, startNodeName);

		return new ComposedNodeData(this, startNode);

// StartNode startNode = DiaFluxUtils.findStartNode(session, flowName,
		// startNodeName);
		// path = new Path(startNode, support);
		//
		// DiaFluxUtils.getFlowData(session).addPathRef(new
		// PathReference(path));
		//
		// return new PathEntry(path, this, support);
	}

	@Override
	public void takeSnapshot(Session session) {

	}

}
