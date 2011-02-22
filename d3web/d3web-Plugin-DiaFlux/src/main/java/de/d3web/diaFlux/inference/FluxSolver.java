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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.PostHookablePSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.DiaFluxCaseObject;
import de.d3web.diaFlux.flow.EdgeMap;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowRun;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.flow.IEdge;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.flow.NodeList;
import de.d3web.diaFlux.flow.SnapshotNode;
import de.d3web.diaFlux.flow.StartNode;

/**
 * 
 * @author Reinhard Hatko
 * @created: 10.09.2009
 * 
 */
public class FluxSolver implements PostHookablePSMethod {

	public static final KnowledgeKind<FlowSet> DIAFLUX = new KnowledgeKind<FlowSet>("DIAFLUX",
			FlowSet.class);
	public static final KnowledgeKind<NodeRegistry> NODE_REGISTRY = new KnowledgeKind<NodeRegistry>(
			"NodeRegistry", NodeRegistry.class);
	public final static KnowledgeKind<EdgeMap> FORWARD = new KnowledgeKind<EdgeMap>(
			"FLUXSOLVER.FORWARD", EdgeMap.class);
	public final static KnowledgeKind<NodeList> BACKWARD = new KnowledgeKind<NodeList>(
			"FLUXSOLVER.BACKWARD", NodeList.class);

	public static final Level LEVEL = Level.INFO;

	static {
		Logger.getLogger(FluxSolver.class.getName()).setLevel(LEVEL);
	}

	public FluxSolver() {
	}

	@Override
	public void init(Session session) {

		if (!DiaFluxUtils.isFlowCase(session)) {
			return;
		}

		Logger.getLogger(FluxSolver.class.getName()).info(
				"Initing FluxSolver with case: " + session);

		try {
			session.getPropagationManager().openPropagation();

			List<StartNode> list = DiaFluxUtils.getAutostartNodes(session.getKnowledgeBase());

			for (StartNode startNode : list) {
				start(session, startNode);
			}

		}
		finally {
			session.getPropagationManager().commitPropagation();
		}

	}

	public static void start(Session session, StartNode startNode) {

		Logger.getLogger(FluxSolver.class.getName()).fine(
				"Activating startnode '" + startNode.getName() + "' of flow '"
						+ startNode.getFlow().getName() + "'.");

		FlowRun run = new FlowRun(startNode);

		DiaFluxUtils.getDiaFluxCaseObject(session).addRun(run);
		activateNode(startNode, run, session);

	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {

		if (!DiaFluxUtils.isFlowCase(session)) {
			return;
		}

		Logger.getLogger(FluxSolver.class.getName()).info(
				"Start propagating: " + changes);

		Set<TerminologyObject> objects = new HashSet<TerminologyObject>();
		for (PropagationEntry propagationEntry : changes) {

			// strategic entries do not matter so far...
			if (propagationEntry.isStrategic()) {
				continue;
			}
			else {
				objects.add(propagationEntry.getObject());
			}
		}
		// add all questions that are indicated via repeatedindication by this
		// psm to the list of possible changes
		for (TerminologyObject to : session.getBlackboard().getInterviewObjects()) {
			if (to instanceof Question) {
				Fact fact = session.getBlackboard().getInterviewFact(to);
				if (fact.getPSMethod() == this && fact.getValue() instanceof Indication) {
					Indication indication = (Indication) fact.getValue();
					if (indication.hasState(State.REPEATED_INDICATED)) {
						objects.add(to);
					}
				}
			}
		}

		for (TerminologyObject object : objects) {

			EdgeMap slice = object.getKnowledgeStore().getKnowledge(FORWARD);

			// TO does not occur in any edge
			if (slice == null) {
				continue;
			}

			// iterate over all edges that contain the changed TO
			for (IEdge edge : slice.getEdges()) {

				INode start = edge.getStartNode();
				INode end = edge.getEndNode();

				boolean active = evalEdge(session, edge);
				List<FlowRun> runs = DiaFluxUtils.getDiaFluxCaseObject(session).getRuns();

				if (active) {

					for (FlowRun flowRun : runs) {

						// begin node is not active, do nothing
						if (!flowRun.isActive(start)) {
							continue;
						}
						else {
							// Edge was not active before
							if (!flowRun.isActive(end) || end instanceof SnapshotNode
									|| end instanceof ComposedNode) {
								activateNode(end, flowRun, session);

							}

						}

					}

				}
				else {
					for (FlowRun flowRun : runs) {
						if (flowRun.isActive(end)) {
							boolean support = checkSupport(session, end, flowRun);

							if (support) {
								continue;
							}
							else {
								deactivateNode(end, flowRun, session);
							}

						}
					}

				}

			}

		}
		// check backward knowledge
		for (PropagationEntry propagationEntry : changes) {

			TerminologyObject object = propagationEntry.getObject();
			NodeList knowledge =
					object.getKnowledgeStore().getKnowledge(BACKWARD);

			if (knowledge == null) continue;

			for (INode node : knowledge) {
				for (FlowRun run : DiaFluxUtils.getDiaFluxCaseObject(session).getRuns()) {
					if (run.isActive(node) || node.isReevaluate()) {
						activate(session, node, run);
					}
				}
			}
		}
		Logger.getLogger(FluxSolver.class.getName()).info("Finished propagating.");

	}

	/**
	 * 
	 * @created 17.02.2011
	 * @param session
	 * @param end
	 * @param flowRun
	 */
	public static boolean checkSupport(Session session, INode end, FlowRun flowRun) {
		List<IEdge> inc = end.getIncomingEdges();

		for (IEdge edge2 : inc) {

			if (evalEdge(session, edge2)) {
				if (flowRun.isActive(edge2.getStartNode())) {
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * 
	 * @created 17.02.2011
	 * @param end
	 * @param flowRun
	 * @param session
	 */
	public static void deactivateNode(INode end, FlowRun flowRun, Session session) {
		if (end instanceof SnapshotNode) {
			deactivate(session, end, flowRun);
			return;
		}
		else if (end instanceof ComposedNode) {
			deactivate(session, end, flowRun);
			// do not remove it, when it has an active snapshot in it
			Flow subflow = DiaFluxUtils.getFlowSet(session).getByName(
					((ComposedNode) end).getFlowName());
			for (INode n : subflow.getNodes()) {
				if (n instanceof SnapshotNode && flowRun.isActive(n)) {
					return;
				}
			}

		}
		flowRun.remove(end);
		deactivate(session, end, flowRun);
		checkSuccessorsOnDeactivation(end, flowRun, session);

	}

	/**
	 * 
	 * @created 17.02.2011
	 * @param end
	 * @param flowRun
	 * @param session
	 */
	public static void checkSuccessorsOnDeactivation(INode end, FlowRun flowRun, Session session) {
		for (IEdge out : end.getOutgoingEdges()) {

			if (flowRun.isActive(out.getEndNode())) {

				if (!checkSupport(session, out.getEndNode(), flowRun)) {
					deactivateNode(out.getEndNode(), flowRun, session);
				}

			}

		}
	}

	/**
	 * 
	 * @created 17.02.2011
	 * @param end
	 * @param flowRun
	 * @param session
	 */
	public static void activateNode(INode end, FlowRun flowRun, Session session) {
		flowRun.add(end);
		activate(session, end, flowRun);
		// propagating after snapshot nodes startes in postpropagation
		if (end instanceof SnapshotNode) return;
		checkSuccessorsOnActivation(end, flowRun, session);
	}

	/**
	 * 
	 * @created 17.02.2011
	 * @param end
	 * @param flowRun
	 * @param session
	 */
	public static void checkSuccessorsOnActivation(INode end, FlowRun flowRun, Session session) {
		for (IEdge out : end.getOutgoingEdges()) {

			if (!evalEdge(session, out)) continue;

			if (!flowRun.isActive(out.getEndNode()) || out.getEndNode() instanceof SnapshotNode
					|| out.getEndNode() instanceof ComposedNode) {
				activateNode(out.getEndNode(), flowRun, session);

			}

		}
	}

	/**
	 * 
	 * @created 17.02.2011
	 * @param session
	 * @param edge
	 * @return
	 */
	public static boolean evalEdge(Session session, IEdge edge) {
		boolean active;
		if (!edge.getStartNode().canFireEdges(session)) return false;
		try {
			active = edge.getCondition().eval(session);
		}
		catch (NoAnswerException e) {
			active = false;
		}
		catch (UnknownAnswerException e) {
			active = false;
		}

		return active;
	}

	public List<IEdge> selectTrueEdges(INode node, Session session) {

		List<IEdge> result = new LinkedList<IEdge>();

		for (IEdge edge : node.getOutgoingEdges()) {

			try {

				if (edge.getCondition().eval(session)) {
					result.add(edge);
				}

			}
			catch (NoAnswerException e) {
			}
			catch (UnknownAnswerException e) {
			}
		}

		return result;
	}

	@Override
	public void postPropagate(Session session) {

		if (!DiaFluxUtils.isFlowCase(session)) {
			return;
		}

		DiaFluxCaseObject caseObject = DiaFluxUtils.getDiaFluxCaseObject(session);

		List<SnapshotNode> snapshots = new ArrayList<SnapshotNode>(
				caseObject.getRegisteredSnapshots());

		while (!snapshots.isEmpty()) {
			List<SnapshotNode> snapshotsToSkip = new ArrayList<SnapshotNode>();
			for (SnapshotNode snapshot : snapshots) {
				if (snapshotsToSkip.contains(snapshot)) continue;
				for (FlowRun run : new ArrayList<FlowRun>(caseObject.getRuns())) {
					if (run.isActive(snapshot)) {
						caseObject.removeRun(run);
						snapshot.deactivate(session, run);
						FlowRun newRun = new FlowRun(snapshot);
						newRun.add(snapshot);
						List<SnapshotNode> otherSnaptshots = new LinkedList<SnapshotNode>();
						for (INode n : run) {
							n.takeSnapshot(session, snapshot);
							if (n instanceof ComposedNode) {
								ComposedNode cn = (ComposedNode) n;
								Flow subflow = DiaFluxUtils.getFlowSet(session).getByName(
										cn.getFlowName());
								if (subflow.getNodes().contains(snapshot)) {
									newRun.add(cn);
								}
							}
							else if (n instanceof SnapshotNode) {
								if (snapshots.contains(n)) {
									n.deactivate(session, run);
									newRun.add(n);
									otherSnaptshots.add((SnapshotNode) n);
									snapshotsToSkip.add((SnapshotNode) n);
								}
							}
						}
						caseObject.addRun(newRun);
						checkSuccessorsOnActivation(snapshot, newRun, session);
						for (SnapshotNode sn : otherSnaptshots) {
							checkSuccessorsOnActivation(sn, newRun, session);
						}
					}
				}
				// assure that the snapshot is deactivated
				snapshot.deactivate(session, null);
			}
			snapshots = new ArrayList<SnapshotNode>(caseObject.getRegisteredSnapshots());
		}

		// // the list for all taken snapshots in this postpropagation
		// List<SnapshotNode> takenSnapshots = new ArrayList<SnapshotNode>();
		//
		// // the list of current snapshots to take
		// List<SnapshotNode> currentSnapshots = new ArrayList<SnapshotNode>();
		//
		// do {
		//
		// // do not iterate over returned list, as new nodes can be inserted
		// // while flowing
		// currentSnapshots.addAll(caseObject.getRegisteredSnapshots());
		// currentSnapshots.removeAll(takenSnapshots);
		//
		// // At first:
		// for (SnapshotNode node : currentSnapshots) {
		// // take the snapshot at each registered SSN
		// takeSnapshot(session, node);
		//
		// }
		//
		// // clear the current Snapshots
		// // new ones can be reached during the propagation
		// // starting from the SSNs
		// caseObject.clearRegisteredSnapshots();
		//
		// // Then:
		// for (SnapshotNode node : currentSnapshots) {
		//
		// }
		//
		// // remember the SS that have been taken
		// takenSnapshots.addAll(currentSnapshots);
		// currentSnapshots.clear();
		//
		// } while (!takenSnapshots.containsAll(currentSnapshots));

	}

	public static void deactivate(Session session, INode node, FlowRun flowRun) {
		Logger.getLogger(FluxSolver.class.getName()).fine("Deactivating node: " + node);

		node.deactivate(session, flowRun);
	}

	public static void activate(Session session, INode node, FlowRun flowRun) {
		Logger.getLogger(FluxSolver.class.getName()).fine("Activating node: " + node);

		node.activate(session, flowRun);
	}

	public static void takeSnapshot(Session session, SnapshotNode node) {

	}

	@Override
	public Fact mergeFacts(Fact[] facts) {

		return Facts.getLatestFact(facts);
	}

	@Override
	public boolean hasType(Type type) {
		return type == Type.strategic || type == Type.problem;
	}

	@Override
	public double getPriority() {
		// default priority
		return 5;
	}

}
