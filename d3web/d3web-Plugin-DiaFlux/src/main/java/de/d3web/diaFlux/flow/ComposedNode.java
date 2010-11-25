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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.inference.CallFlowAction;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.NodeActiveCondition;

/**
 *
 * @author Reinhard Hatko
 * @created 10.08.10
 */
public class ComposedNode extends ActionNode {


	public ComposedNode(String id, String name, CallFlowAction action) {
		super(id, name, action);
	}


	@Override
	public void takeSnapshot(Session session, SnapshotNode snapshotNode, List<INode> nodes) {


		// collects all exit nodes in the called flow that match an
		// active outgoing egde's condition
		// need to do this before the super call -> this resets the edges
		Collection<INode> exitNodes = findActiveExitNodes(session);

		super.takeSnapshot(session, snapshotNode, nodes);

		// if the start this CN calls is already snapshotted
		// then do nothing
		// TODO need some more sophisticated test here
		// could be a problem with some weird unconnected flows
		// or even with subflows that contain an SSN

		 CallFlowAction action = (CallFlowAction) getAction();
		 StartNode startNode = DiaFluxUtils.findStartNode(session,
		 action.getFlowName(), action.getStartNodeName());
		
		 if (nodes.contains(startNode)) {
			 return;
		 }

		for (INode exitNode : exitNodes) {
			DiaFluxUtils.getPath(exitNode, session).takeSnapshot(session, snapshotNode, exitNode, nodes);

		}


	}

	@Override
	public boolean couldActivate(Session session) {

		//TODO better check would be nice
		for (IEdge edge : getIncomingEdges()) {
			if (DiaFluxUtils.getEdgeData(edge, session).hasFired()) {
				
				// if one of the incoming edges has fired
				// then the calling start node must be active
				return false;
			}
			
		}
		
		CallFlowAction action = (CallFlowAction) this.action;

		// get the called startnode
		StartNode startNode = DiaFluxUtils.findStartNode(session, action.getFlowName(),
				action.getStartNodeName());

		if (startNode == null) {
			return false;
			// throw new NullPointerException("Startnode '" +
			// action.getStartNodeName()
			// + "' in flow '" + action.getFlowName() + "' not found.");
		}

		// this node can be activated, if the called StartNode can be activated
		return startNode.couldActivate(session);
	}

	public String getFlowName() {
		return ((CallFlowAction) getAction()).getFlowName();
	}

	public String getStartNodeName() {
		return ((CallFlowAction) getAction()).getStartNodeName();
	}


	private Collection<INode> findActiveExitNodes(Session session) {

		// A combination of IS_ACTIVE and PROCESSED Conditions could lead to
		// duplicated entries, so use set
		Collection<INode> result = new HashSet<INode>();

		for (IEdge edge : getOutgoingEdges()) {


			// if the edge has not fired, the exit node is not active
			// But: the edge could also be resetted already, so do not do this
			// check
			// EdgeData edgeData = DiaFluxUtils.getEdgeData(edge, session);
			// if (!edgeData.hasFired()) continue;

			Condition condition = edge.getCondition();

			if (condition instanceof NodeActiveCondition) {
				NodeActiveCondition nodeActiveCondition = (NodeActiveCondition) condition;
				String flowName = nodeActiveCondition.getFlowName();
				String nodeName = nodeActiveCondition.getNodeName();

				EndNode node = DiaFluxUtils.findExitNode(session, flowName, nodeName);

				if (DiaFluxUtils.getNodeData(node, session).isSupported()) {
					result.add(node);
				}

			}
			// TODO add all active exit nodes and return when such a condition
			// is used!
			// else if (condition instanceof FlowchartProcessedCondition)



		}

		return result;

	}



}
