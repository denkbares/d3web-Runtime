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

package de.d3web.diaFlux.inference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;
import de.d3web.diaFlux.IndicateFlowAction;
import de.d3web.diaFlux.flow.DiaFluxCaseObject;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.flow.INodeData;
import de.d3web.diaFlux.flow.ISupport;
import de.d3web.diaFlux.flow.SnapshotNode;
import de.d3web.diaFlux.flow.StartNode;

/**
 *
 * @author Reinhard Hatko
 * @created: 10.09.2009
 *
 */
public class FluxSolver implements PSMethod {

	public static final MethodKind DIAFLUX = new MethodKind("DIAFLUX");

	public FluxSolver() {
	}

	@Override
	public void init(Session session) {

		if (!DiaFluxUtils.isFlowCase(session)) return;

		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				("Initing FluxSolver with case: " + session));

		Flow flow = DiaFluxUtils.getFlowSet(session).getByName("Main");
		if (flow != null) {

			for (StartNode startNode : flow.getStartNodes()) {

				if (startNode.getName().equals("Start")) {
					Rule rule = new Rule("FCIndication_", FluxSolver.class);
					rule.setAction(new IndicateFlowAction("Main", "Start"));
					rule.setCondition(new CondAnd(new ArrayList<Condition>()));
					rule.check(session);
				}
			}

		}

		// Calling propagate to start flowing from start nodes
		propagate(session, Collections.EMPTY_LIST);

	}

	public static void indicateFlowFromAction(Session session, StartNode startNode, ISupport support) {
		INodeData nodeData = DiaFluxUtils.getNodeData(startNode, session);

		boolean active = nodeData.isActive();

		if (active) {
			FluxSolver.addSupport(session, startNode, support);

		} // if node was not active before adding support, start new path
		else {
			IPath flowData = DiaFluxUtils.getPath(startNode.getFlow(), session);

			flowData.activate(startNode, support, session);


		}

	}

	public static boolean removeSupport(Session session, INode node, ISupport support) {
		INodeData nodeData = DiaFluxUtils.getNodeData(node, session);

		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				"Removing support '" + support + "' from node '" + node + "'.");

		boolean removed = nodeData.removeSupport(support);

		if (!removed) {
			Logger.getLogger(FluxSolver.class.getName()).log(Level.SEVERE,
					"Could not remove support '" + support + "' from node '" + node + "'.");

		}

		return removed;
	}

	public static boolean addSupport(Session session, INode node, ISupport support) {
		INodeData nodeData = DiaFluxUtils.getNodeData(node, session);
		boolean added = nodeData.addSupport(session, support);

		if (!added) {
			Logger.getLogger(FluxSolver.class.getName()).log(Level.SEVERE,
					"Could not add support '" + support + "' to node '" + node + "'.");

		}

		return added;
	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {
		// TODO Reinhard: special handling of strategic entries?

		if (!DiaFluxUtils.isFlowCase(session)) return;

		DiaFluxCaseObject caseObject = DiaFluxUtils.getDiaFluxCaseObject(session);

		if (caseObject.checkPropagationTime(session)) {

		}

		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				"Start propagating: " + changes);

		try {
			session.getPropagationManager().openPropagation();


			for (IPath path : new ArrayList<IPath>(caseObject.getActivePathes())) {


				caseObject.setActivePath(path);

				path.propagate(session, changes);

				caseObject.setActivePath(null);
			}


			Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
					"Finished propagating.");

		}
		finally {
			session.getPropagationManager().commitPropagation();
		}


	}

	public static void undoAction(Session session, INode node) {
		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				("Undoing action of node: " + node));
		node.undoAction(session);
	}

	public static void doAction(Session session, INode node) {
		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				("Doing action of node: " + node));
		node.doAction(session);
	}



	@Override
	public Fact mergeFacts(Fact[] facts) {
		// diaflux does not derive own facts
		return Facts.mergeError(facts);
	}

	public static void takeSnapshot(Session session, IPath path, SnapshotNode node) {
		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				"Start taking snapshot on path: " + path);

		path.takeSnapshot(session, node);

		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				"Finished taking snapshot on path: " + path);

	}

	@Override
	public boolean hasType(Type type) {
		// TODO Reinhard: Check if correct
		return type == Type.strategic;
	}

}
