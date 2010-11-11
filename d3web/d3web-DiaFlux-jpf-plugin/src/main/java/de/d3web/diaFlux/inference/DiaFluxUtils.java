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

package de.d3web.diaFlux.inference;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.DiaFluxCaseObject;
import de.d3web.diaFlux.flow.EdgeData;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.flow.IEdge;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.flow.INodeData;
import de.d3web.diaFlux.flow.StartNode;

/**
 * @author Reinhard Hatko
 *
 *         Created: 07.08.2010
 */
public final class DiaFluxUtils {

	private DiaFluxUtils() {
	}

	public static FlowSet getFlowSet(KnowledgeBase knowledgeBase) {

		List knowledge = (List) knowledgeBase.getKnowledge(FluxSolver.class,
				FluxSolver.DIAFLUX);

		if (knowledge == null || knowledge.isEmpty()) return null;

		return (FlowSet) knowledge.get(0);

	}

	public static INodeData getNodeData(INode node, Session session) {
		return getDiaFluxCaseObject(session).getPath(node.getFlow()).getNodeData(node);

	}

	public static EdgeData getEdgeData(IEdge edge, Session session) {
		return getPath(edge, session).getEdgeData(edge);
	}

	public static FlowSet getFlowSet(Session session) {

		return getFlowSet(session.getKnowledgeBase());

	}

	public static IPath getPath(Flow flow, Session session) {
		return getDiaFluxCaseObject(session).getPath(flow);
	}

	public static IPath getPath(INode node, Session session) {
		return getPath(node.getFlow(), session);
	}

	public static IPath getPath(IEdge edge, Session session) {
		return getPath(edge.getStartNode().getFlow(), session);
	}


	public static boolean isFlowCase(Session session) {

		if (session == null) return false;

		FlowSet flowSet = getFlowSet(session);

		return flowSet != null && !flowSet.getFlows().isEmpty();
	}

	public static DiaFluxCaseObject getDiaFluxCaseObject(Session session) {

		FlowSet flowSet = getFlowSet(session);

		return (DiaFluxCaseObject) session.getCaseObject(flowSet);
	}

	public static void addFlow(Flow flow, KnowledgeBase base, String title) {

		List ks = (List) base.getKnowledge(FluxSolver.class, FluxSolver.DIAFLUX);

		FlowSet flowSet;
		if (ks == null) {
			flowSet = new FlowSet(title);
			base.addKnowledge(FluxSolver.class, flowSet, FluxSolver.DIAFLUX);

		}
		else {
			flowSet = (FlowSet) ks.get(0);
		}

		flowSet.put(flow);

	}

	/**
	 * returns the StartNode that is called by the supplied action
	 */
	public static StartNode findStartNode(Session session, String flowName, String startNodeName) {

		FlowSet flowSet = getFlowSet(session);

		Flow subflow = flowSet.getByName(flowName);

		if (subflow == null) {
			Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("Flowchart '" + flowName + "' not found."));
			return null;
		}

		List<StartNode> startNodes = subflow.getStartNodes();

		for (StartNode iNode : startNodes) {
			if (iNode.getName().equalsIgnoreCase(startNodeName)) {
				return iNode;

			}
		}

		Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
				("Startnode '" + startNodeName + "' of flow '" + flowName + "' not found."));
		return null;

	}

}
