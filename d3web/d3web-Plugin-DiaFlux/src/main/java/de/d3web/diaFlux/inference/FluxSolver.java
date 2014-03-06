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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.PostHookablePSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.Conditions;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.DiaFluxCaseObject;
import de.d3web.diaFlux.flow.DiaFluxElement;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.EdgeMap;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowRun;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.NodeList;
import de.d3web.diaFlux.flow.SnapshotNode;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.utils.Log;

/**
 * @author Reinhard Hatko
 * @created: 10.09.2009
 */
public class FluxSolver implements PostHookablePSMethod, SessionObjectSource<DiaFluxCaseObject> {

	public final static KnowledgeKind<EdgeMap> DEPENDANT_EDGES = new KnowledgeKind<EdgeMap>(
			"DEPENDANT_EDGES", EdgeMap.class);
	public final static KnowledgeKind<NodeList> DEPENDANT_NODES = new KnowledgeKind<NodeList>(
			"DEPENDANT_NODES", NodeList.class);
	/**
	 * Nodes that derive a specific terminology object.
	 */
	public final static KnowledgeKind<NodeList> DERIVING_NODES = new KnowledgeKind<NodeList>(
			"DERIVING_NODES", NodeList.class);

	public final static KnowledgeKind<FlowSet> FLOW_SET = new KnowledgeKind<FlowSet>(
			"FLOW_SET", FlowSet.class);

	public static final Object SNAPSHOT_SOURCE = SnapshotNode.class;

	public FluxSolver() {
	}

	@Override
	public void init(Session session) {

		if (!DiaFluxUtils.isFlowCase(session)) {
			return;
		}

		Log.fine("Initing FluxSolver with case: " + session);

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

		Log.fine("Activating startnode '" + startNode.getName() + "' of flow '"
				+ startNode.getFlow().getName() + "'.");

		FlowRun run = new FlowRun();
		run.addStartNode(startNode);
		DiaFluxUtils.getDiaFluxCaseObject(session).addRun(run);
		addSupport(startNode, null, run, session);
	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {

		if (!DiaFluxUtils.isFlowCase(session)) {
			return;
		}

		Logger logger = Log.logger();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Start propagating: " + changes);
		}
		List<FlowRun> runs = DiaFluxUtils.getDiaFluxCaseObject(session).getRuns();
		List<Node> changedNodes = new ArrayList<Node>();
		for (PropagationEntry propagationEntry : changes) {

			// strategic entries do not matter so far...
			if (propagationEntry.isStrategic()) {
				continue;
			}

			TerminologyObject object = propagationEntry.getObject();
			EdgeMap slice = object.getKnowledgeStore().getKnowledge(DEPENDANT_EDGES);

			// TO does not occur in any edge
			if (slice == null) {
				continue;
			}

			// iterate over all edges that contain the changed TO
			for (Edge edge : slice.getEdges()) {
				changedNodes.add(edge.getStartNode());
			}
		}
		for (Node changedNode : changedNodes) {
			for (FlowRun flowRun : runs) {
				checkSuccessors(changedNode, flowRun, session);
			}
		}
		// check backward knowledge: nodes that uses other objects to calculate
		// e.g. a formula
		for (PropagationEntry propagationEntry : changes) {
			TerminologyObject object = propagationEntry.getObject();
			NodeList knowledge =
					object.getKnowledgeStore().getKnowledge(DEPENDANT_NODES);

			if (knowledge == null) continue;

			for (Node node : knowledge) {
				for (FlowRun run : DiaFluxUtils.getDiaFluxCaseObject(session).getRuns()) {
					if (run.isActive(node) && node.isReevaluate(session)) {
						node.execute(session, run);
					}
				}
			}
		}
		logger.fine("Finished propagating.");

	}

	/**
	 * Adds support to a node. If the node is triggered, ie, was not active before, it gets activated.
	 *
	 * @param node    the node to add support to
	 * @param support a node or edge supporting the node
	 * @created 03.09.2013
	 */
	public static void addSupport(Node node, DiaFluxElement support, FlowRun flowRun, Session session) {
		boolean triggered = flowRun.addSupport(node, support);
		if (!triggered) {
			return;
		}
		// if the node was triggered (ie became activated by adding this
		// support), we execute it
		node.execute(session, flowRun);
		// propagating after snapshot nodes starts in postpropagation
		if (!(node instanceof SnapshotNode)) {
			// checkSuccessorsOnActivation(node, flowRun, session);
			checkSuccessors(node, flowRun, session);
		}
	}

	/**
	 * Removes support from a node.
	 *
	 * @param node    the node to remove the support from
	 * @param support a node or edge supporting the node
	 * @created 03.09.2013
	 */
	public static void removeSupport(Node node, DiaFluxElement support, FlowRun flowRun, Session session) {
		boolean stillActive = flowRun.removeSupport(node, support);
		if (stillActive) {
			return;
		}
		node.retract(session, flowRun);
		if (!(node instanceof SnapshotNode)) {
			// checkSuccessorsOnDeactivation(node, flowRun, session);
			checkSuccessors(node, flowRun, session);
		}
	}

	/**
	 * Checks, if the states of outgoing edges are correct.
	 */
	public static void checkSuccessors(Node node, FlowRun run, Session session) {
		if (run.isActive(node)) { // node is active...
			for (Edge edge : node.getOutgoingEdges()) {
				// ...now check inactive edges, that eval to true
				if (!run.isActivated(edge) && evalEdge(session, edge)) {
					activateEdge(edge, run, session);
				}
				else if (run.isActivated(edge) && !evalEdge(session, edge)) {
					deactivateEdge(edge, run, session);
				}
			}

		}
		else {// node is inactive...
			for (Edge edge : node.getOutgoingEdges()) {
				// ...deactivate all edges
				if (run.isActivated(edge)) {
					deactivateEdge(edge, run, session);
				}
			}

		}
	}

	@Override
	public DiaFluxCaseObject createSessionObject(Session session) {
		return new DiaFluxCaseObject();
	}

	private static void activateEdge(Edge edge, FlowRun flowRun, Session session) {
		addSupport(edge.getEndNode(), edge, flowRun, session);
	}

	private static void deactivateEdge(Edge edge, FlowRun flowRun, Session session) {
		removeSupport(edge.getEndNode(), edge, flowRun, session);
	}

	/**
	 * Returns whether the specified node is currently active within the specified session. Please note that a node
	 * previously being active and then fixed by a snapshot is not considered to be active any longer, even if its
	 * derived facts still persists.
	 *
	 * @param node    the node to be checked
	 * @param session the session to check the node for
	 * @return if the node is active in the session
	 * @created 11.03.2013
	 */
	public static boolean isActiveNode(Node node, Session session) {
		List<FlowRun> runs = DiaFluxUtils.getDiaFluxCaseObject(session).getRuns();
		for (FlowRun flowRun : runs) {
			if (flowRun.isActive(node)) return true;
		}
		return false;
	}

	/**
	 * @param session
	 * @param edge
	 * @return
	 * @created 17.02.2011
	 */
	public static boolean evalEdge(Session session, Edge edge) {
		if (!Conditions.isTrue(edge.getStartNode().getEdgePrecondition(), session)) return false;
		return Conditions.isTrue(edge.getCondition(), session);
	}

	@Override
	public void postPropagate(Session session, Collection<PropagationEntry> entries) {
		if (!DiaFluxUtils.isFlowCase(session)) {
			return;
		}
		DiaFluxCaseObject caseObject = DiaFluxUtils.getDiaFluxCaseObject(session);

		// get all entered snapshots ES
		// get all flows that entered a snapshot (in ES): SF
		// get all nodes to be snap-shoted (from all flows in SF): SnappyNodes
		// for all n in SnappyNodes
		// ...snapshot n
		// ...deactivate n
		// for all shotshot nodes s in ES
		// ...create new flow with s and parents as start node
		// remove all flows f in SF from the active flows

		Collection<SnapshotNode> enteredSnapshots = caseObject.getActivatedSnapshots(session);
		if (enteredSnapshots.isEmpty()) {
			return;
		}

		Collection<FlowRun> snappyFlows = getFlowRunsWithEnteredSnapshot(
				enteredSnapshots, caseObject);

		// log debug output
		Logger logger = Log.logger();
		if (logger.isLoggable(Level.FINE)) {
			Log.fine("Taking snapshots: " + snappyFlows);
		}

		// Calculate new flow runs (before changing anything in the session)
		Collection<FlowRun> newRuns = new HashSet<FlowRun>();
		for (SnapshotNode snapshotNode : enteredSnapshots) {
			FlowRun run = new FlowRun();
			run.addStartNode(snapshotNode);
			Collection<FlowRun> runsForSnapshot = getFlowRunsWithEnteredSnapshot(Arrays.asList(snapshotNode), caseObject);
			Set<Node> activeNodes = new HashSet<Node>();
			for (FlowRun flowRun : runsForSnapshot) {
				run.addBlockedSnapshot(flowRun, session);
				activeNodes.addAll(flowRun.getActiveNodes());
			}
			addParentsToStartNodes(run, snapshotNode, activeNodes, session);
			newRuns.add(run);
			run.addBlockedSnapshot(snapshotNode, session);
			// inform the flux solver that this snapshot node has been
			// snapshoted
			caseObject.snapshotDone(snapshotNode, session);
			session.getPropagationManager().forcePropagate(snapshotNode);
		}

		// Make snapshot of all related nodes
		for (FlowRun flow : snappyFlows) {
			Collection<Node> activeNodes = flow.getActiveNodes();
			for (Node node : activeNodes) {
				node.takeSnapshot(session);
			}
			for (Node node : activeNodes) {
				node.retract(session, flow);
			}
		}

		// Remove the old/snapshoted flow runs
		for (FlowRun run : snappyFlows) {
			caseObject.removeRun(run);
		}

		// Add and propagate the new flow runs
		for (FlowRun run : newRuns) {
			caseObject.addRun(run);
		}

	}

	/**
	 * Creates a collection of all nodes that are active in any flow run that leads towards this snapshot node.
	 *
	 * @param snapshotNode the target snapshot node
	 * @return the list of active snapshots
	 * @created 28.02.2011
	 */
	public static Collection<Node> getActiveNodesLeadingToSnapshopNode(SnapshotNode snapshotNode, Collection<FlowRun> snapshotFlows) {
		Collection<Node> result = new HashSet<Node>();
		for (FlowRun run : snapshotFlows) {
			if (run.isActivated(snapshotNode)) {
				result.addAll(run.getActiveNodes());
			}
		}
		return result;
	}

	private void addParentsToStartNodes(FlowRun run, SnapshotNode snapshotNode, Collection<Node> activeNodes, Session session) {
		// compute parents of snapshotNode
		// compute callstack of these parents
		Collection<Node> parents = new HashSet<Node>();
		computeParentsRecursive(snapshotNode, parents, activeNodes);

		for (Node parent : parents) {
			if (hasIncomingActivation(parent, activeNodes, session)
					|| hasNotLeft(parent, activeNodes, session)) {
				run.addStartNode(parent);
			}
		}
	}

	private boolean hasNotLeft(Node node, Collection<Node> activeNodes, Session session) {
		if (activeNodes.contains(node)) {
			for (Edge edge : node.getOutgoingEdges()) {
				if (Conditions.isTrue(edge.getCondition(), session)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private static boolean hasIncomingActivation(Node child, Collection<Node> activeNodes, Session session) {
		for (Edge edge : child.getIncomingEdges()) {
			if (activeNodes.contains(edge.getStartNode())
					&& Conditions.isTrue(edge.getCondition(), session)) {
				return true;
			}
		}
		return false;
	}

	private static void computeParentsRecursive(Node child, Collection<Node> result, Collection<Node> allNodes) {
		Flow calledFlow = child.getFlow();
		for (Node node : allNodes) {
			if (node instanceof ComposedNode) {
				String calledFlowname = ((ComposedNode) node).getCalledFlowName();
				if (calledFlow.getName().equals(calledFlowname)) {
					result.add(node);
					computeParentsRecursive(node, result, allNodes);
				}
			}
		}
	}

	public static Collection<FlowRun> getFlowRunsWithEnteredSnapshot(Collection<SnapshotNode> enteredSnapshots,
																	 DiaFluxCaseObject caseObject) {
		Set<FlowRun> snappyRuns = new HashSet<FlowRun>();

		for (FlowRun flowRun : caseObject.getRuns()) {
			for (SnapshotNode snapshotNode : enteredSnapshots) {
				if (flowRun.isActive(snapshotNode)) {
					snappyRuns.add(flowRun);
				}
			}
		}
		return snappyRuns;
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		if (facts[0].getValue() instanceof Indication) {
			return Facts.mergeIndicationFacts(facts);
		}
		else {

			for (Fact fact : facts) {
				if (!(fact.getSource() == SNAPSHOT_SOURCE)) {
					return fact;
				}
			}
			return facts[0];
		}
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

	@Override
	public Set<TerminologyObject> getPotentialDerivationSources(TerminologyObject derivedObject) {
		return getSources(derivedObject, null);
	}

	@Override
	public Set<TerminologyObject> getActiveDerivationSources(TerminologyObject derivedObject, Session session) {
		if (session == null) throw new NullPointerException();
		return getSources(derivedObject, session);
	}

	private static Set<TerminologyObject> getSources(TerminologyObject derivedObject, Session session) {
		// similar to rules.
		// returns the incoming edges condition objects
		// and the actions forward objects
		// of all nodes setting the requested object
		//
		// PLEASE NOTE:
		// "snapshot"ed objects are not deliver their deriving
		// objects any longer
		Set<TerminologyObject> result = new HashSet<TerminologyObject>();
		NodeList nodes = derivedObject.getKnowledgeStore().getKnowledge(DERIVING_NODES);
		if (nodes == null) return Collections.emptySet();
		for (Node node : nodes.getNodes()) {
			// if the node is known not to be inactive, ignore it
			if (session != null && !isActiveNode(node, session)) continue;
			// add precondition values of all edges
			for (Edge edge : node.getIncomingEdges()) {
				Condition condition = edge.getCondition();
				if (condition != null) {
					result.addAll(condition.getTerminalObjects());
				}
			}
			// add action formula values
			result.addAll(node.getHookedObjects());
		}
		return result;
	}
}
