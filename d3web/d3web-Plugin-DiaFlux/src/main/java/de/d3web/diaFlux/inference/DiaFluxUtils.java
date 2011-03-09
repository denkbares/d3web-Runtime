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

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.DiaFluxCaseObject;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.StartNode;

/**
 * @author Reinhard Hatko
 * 
 *         Created: 07.08.2010
 */
public final class DiaFluxUtils {

	private DiaFluxUtils() {
	}

	// TODO cleanup, new FlowSet is created on each call
	public static FlowSet getFlowSet(KnowledgeBase knowledgeBase) {

		List<Flow> objects = knowledgeBase.getManager().getObjects(Flow.class);

		FlowSet set = new FlowSet();

		for (Flow flow : objects) {
			set.put(flow);
		}

		return set;

	}

	public static FlowSet getFlowSet(Session session) {

		return getFlowSet(session.getKnowledgeBase());

	}

	public static boolean isFlowCase(Session session) {

		if (session == null) {
			return false;
		}

		FlowSet flowSet = getFlowSet(session);

		return flowSet != null && !flowSet.getFlows().isEmpty();
	}

	public static DiaFluxCaseObject getDiaFluxCaseObject(Session session) {
		return (DiaFluxCaseObject) session.getCaseObject(session.getPSMethodInstance(FluxSolver.class));
	}

	public static List<StartNode> getAutostartNodes(KnowledgeBase base) {
		List<StartNode> result = new LinkedList<StartNode>();

		for (Flow flow : getFlowSet(base)) {
			if (flow.isAutostart()) {

				result.addAll(flow.getStartNodes());

			}

		}

		return result;

	}

	public static Node findNode(Session session, String flowName, String nodeName) {
		FlowSet flowSet = getFlowSet(session);

		if (flowSet == null) {
			Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("No Flowcharts found in kb."));
		}

		Flow subflow = flowSet.get(flowName);

		if (subflow == null) {
			Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("Flowchart '" + flowName + "' not found."));
			return null;
		}

		List<Node> startNodes = subflow.getNodes();

		for (Node node : startNodes) {
			if (node.getName().equalsIgnoreCase(nodeName)) {
				return node;

			}
		}

		Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("Node '" + nodeName + "' of flow '" + flowName + "' not found."));
		return null;

	}

	/**
	 * returns the StartNode that is called by the supplied action
	 */
	// TODO Cleanup
	public static StartNode findStartNode(Session session, String flowName, String startNodeName) {

		FlowSet flowSet = getFlowSet(session);

		if (flowSet == null) {
			Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("No Flowcharts found in kb."));
		}

		Flow subflow = flowSet.get(flowName);

		if (subflow == null) {
			Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("Flowchart '" + flowName + "' not found."));
			return null;
		}

		List<StartNode> startNodes = subflow.getStartNodes();

		for (StartNode node : startNodes) {
			if (node.getName().equalsIgnoreCase(startNodeName)) {
				return node;

			}
		}

		Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("Startnode '" + startNodeName + "' of flow '" + flowName + "' not found."));
		return null;

	}

	/**
	 * returns the StartNode that is called by the supplied action
	 */
	// TODO Cleanup
	public static EndNode findExitNode(Session session, String flowName, String startNodeName) {

		FlowSet flowSet = getFlowSet(session);

		if (flowSet == null) {
			Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("No Flowcharts found in kb."));
		}

		Flow subflow = flowSet.get(flowName);

		if (subflow == null) {
			Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("Flowchart '" + flowName + "' not found."));
			return null;
		}

		List<EndNode> exitNodes = subflow.getExitNodes();

		for (EndNode node : exitNodes) {
			if (node.getName().equalsIgnoreCase(startNodeName)) {
				return node;

			}
		}

		Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("Exitnode '" + startNodeName + "' of flow '" + flowName + "' not found."));
		return null;

	}

}
