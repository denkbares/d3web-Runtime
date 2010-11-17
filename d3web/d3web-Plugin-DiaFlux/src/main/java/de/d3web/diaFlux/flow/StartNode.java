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
		throw new UnsupportedOperationException("Can not add incoming edge to start node");
	}

	@Override
	public void takeSnapshot(Session session, SnapshotNode snapshotNode, List<INode> nodes) {
		// remember which nodes called this one, before removing the support,
		// what also empties the calling nodes
		StartNodeData data = (StartNodeData) DiaFluxUtils.getNodeData(this, session);
		List<INodeData> callingNodes = data.getCallingNodes();

		super.takeSnapshot(session, snapshotNode, nodes);

		boolean ownPathActive = DiaFluxUtils.getPath(this, session).isActive();

		for (INodeData nodeData : callingNodes) {

			INode composedNode = nodeData.getNode();

			// the calling node has already been snapshotted
			// so do not start snapshot there
			// this is the case if the SS started behind the CN 
			if (nodes.contains(composedNode)) {
				continue;
			}

			// if this path is completely snapshotted,
			// then do not start snapshots on calling flows:
			// in this case it does not contain a SSN. The path on which 
			// the SSN was will continue snapshotting on its own.
			// the other callers should not be 
			if (!ownPathActive) {
				continue;
				
			}
				
			// otherwise take snapshot at the path
			// in this case the composedNode did not start the snapshot at this
			// node, but it was triggered from within the startnodes flowchart
			IPath callingPath = DiaFluxUtils.getPath(composedNode, session);
			callingPath.takeSnapshot(session, snapshotNode, composedNode, nodes);

			// maintain support of calling composedNode in case that the path
			// this startnode is in, is still active. After a SS this should only
			// be the case if an active SSN is contained in this Flowchart.
			// BUT: This is a problem when the flowchart of this node is
			// unconnected.
			// Then, it could contain an unconnected active path, that was not
			// called by the composed
			// node and that is still active. Then, the calling CN would get
			// ValidSupport, though it shouldn't
			
			if (ownPathActive) {
				FluxSolver.addSupport(session, composedNode, new ValidSupport());
			}

		}

	}

	@Override
	public SessionObject createCaseObject(Session session) {
		// Start node needs special NodeData to keep track of calling CNs
		return new StartNodeData(this);
	}
}
