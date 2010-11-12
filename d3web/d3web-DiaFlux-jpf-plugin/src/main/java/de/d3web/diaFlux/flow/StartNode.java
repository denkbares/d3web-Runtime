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

package de.d3web.diaFlux.flow;

import java.util.List;

import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.FluxSolver;
import de.d3web.diaFlux.inference.IPath;

/**
 *
 * @author Reinhard Hatko
 *
 */
public class StartNode extends Node {

	public StartNode(String id, String name) {
		super(id, name);
	}

	@Override
	protected boolean addIncomingEdge(IEdge edge) {
		throw new UnsupportedOperationException("can not add incoming edge to start node");
	}

	@Override
	public void takeSnapshot(Session session, SnapshotNode snapshotNode, List<INode> nodes) {
		// remeber which nodes called this one, before removing the support,
		// what also empties the calling nodes
		StartNodeData data = (StartNodeData) DiaFluxUtils.getNodeData(this, session);
		List<INodeData> callingNodes = data.getCallingNodes();

		super.takeSnapshot(session, snapshotNode, nodes);


		for (INodeData nodeData : callingNodes) {

			INode composedNode = nodeData.getNode();

			// the calling node has already been snapshotted
			// so do not start snapshot there
			if (nodes.contains(composedNode)) continue;


			// otherwise take snapshot at the path
			// in this case the composedNode did not start the snapshot at this
			// node, but it was triggered from within the startnodes flowchart
			IPath path = DiaFluxUtils.getPath(composedNode, session);
			path.takeSnapshot(session, snapshotNode, composedNode, nodes);

			// maintain support of calling composedNode
			FluxSolver.addSupport(session, composedNode, new ValidSupport());

		}

	}

	@Override
	public SessionObject createCaseObject(Session session) {
		return new StartNodeData(this);
	}
}
