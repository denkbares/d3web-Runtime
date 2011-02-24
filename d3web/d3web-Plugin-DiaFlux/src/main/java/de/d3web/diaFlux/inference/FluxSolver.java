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
import de.d3web.core.knowledge.TerminologyObject;
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

		FlowRun run = new FlowRun();

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

		for (PropagationEntry propagationEntry : changes) {

			// strategic entries do not matter so far...
			if (propagationEntry.isStrategic()) {
				continue;
			}
			else {

				TerminologyObject object = propagationEntry.getObject();

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
		}
		// check backward knowledge
		for (PropagationEntry propagationEntry : changes) {

			TerminologyObject object = propagationEntry.getObject();
			NodeList knowledge =
					object.getKnowledgeStore().getKnowledge(BACKWARD);

			if (knowledge == null) continue;

			for (INode node : knowledge) {
				for (FlowRun run : DiaFluxUtils.getDiaFluxCaseObject(session).getRuns()) {
					if (run.isActive(node) && node.isReevaluate()) {
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
		active = evalToTrue(session, edge);

		return active;
	}

	public List<IEdge> selectTrueEdges(INode node, Session session) {

		List<IEdge> result = new LinkedList<IEdge>();

		for (IEdge edge : node.getOutgoingEdges()) {
			if (evalToTrue(session, edge)) {
				result.add(edge);
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
						FlowRun newRun = new FlowRun();
						newRun.add(snapshot);
						List<SnapshotNode> currentSnapshots = new LinkedList<SnapshotNode>();
						for (INode n : run.getActiveNodes()) {
							n.takeSnapshot(session, snapshot);
							if (n instanceof SnapshotNode) {
								if (snapshots.contains(n)) {
									n.deactivate(session, run);
									newRun.addStartNode(n);
									currentSnapshots.add((SnapshotNode) n);
									snapshotsToSkip.add((SnapshotNode) n);
								}
							}
						}
						addRecursiveComposedNodes(currentSnapshots, run, newRun, session);
						caseObject.removeRun(run);
						caseObject.addRun(newRun);
						for (SnapshotNode sn : currentSnapshots) {
							checkSuccessorsOnActivation(sn, newRun, session);
						}
					}
				}
				// assure that the snapshot is deactivated
				snapshot.deactivate(session, null);
			}
			snapshots = new ArrayList<SnapshotNode>(caseObject.getRegisteredSnapshots());
		}
	}

	private void addRecursiveComposedNodes(Collection<? extends INode> children, FlowRun oldrun, FlowRun newRun, Session session) {
		Set<INode> foundComposedNodes = new HashSet<INode>();
		for (INode node : oldrun.getActiveNodes()) {
			if (node instanceof ComposedNode) {
				ComposedNode cn = (ComposedNode) node;
				Flow subflow = DiaFluxUtils.getFlowSet(session).getByName(
						cn.getFlowName());
				for (INode child : children) {
					if (subflow.getNodes().contains(child)
							&& (hasIncomingActivation(cn, oldrun, session) || isNotLeftStartNode(
									cn, oldrun, session))) {
						newRun.addStartNode(cn);
						foundComposedNodes.add(cn);
					}
				}
			}
		}
		if (!foundComposedNodes.isEmpty()) {
			addRecursiveComposedNodes(foundComposedNodes, oldrun, newRun, session);
		}
	}

	private boolean hasIncomingActivation(INode child, FlowRun oldrun, Session session) {
		for (IEdge edge : child.getIncomingEdges()) {
			if (oldrun.isActive(edge.getStartNode()) && evalToTrue(session, edge)) {
				return true;
			}
		}
		return false;
	}

	private boolean isNotLeftStartNode(INode node, FlowRun oldRun, Session session) {
		if (oldRun.isStartNode(node)) {
			for (IEdge edge : node.getOutgoingEdges()) {
				if (evalToTrue(session, edge)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private static boolean evalToTrue(Session session, IEdge edge) {
		try {
			return edge.getCondition().eval(session);
		}
		catch (NoAnswerException e) {
			return false;
		}
		catch (UnknownAnswerException e) {
			return false;
		}
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
