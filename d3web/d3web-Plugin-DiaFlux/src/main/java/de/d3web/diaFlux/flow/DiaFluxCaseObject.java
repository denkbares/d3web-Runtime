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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.Conditions;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.FluxSolver;
import de.d3web.diaFlux.inference.FluxSolver.SuggestMode;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.Score;

/**
 * @author Reinhard Hatko
 * @created 04.11.2009
 */
public class DiaFluxCaseObject implements SessionObject {

	private final Session session;
	private final FluxSolver fluxSolver;
	private final List<FlowRun> runs = new ArrayList<FlowRun>();
	private final Map<SnapshotNode, Long> latestSnapshotTime = new HashMap<SnapshotNode, Long>();

	/**
	 * Provide a set of edges from active nodes, but with still undefined conditions, that might
	 * become "true" in the further interview process.
	 */
	private final Set<Edge> undefinedEdges = new HashSet<Edge>();

	/**
	 * The internal set of solutions suspected by still open paths
	 */
	private Set<Solution> suspectedSolutions = Collections.emptySet();

	public DiaFluxCaseObject(Session session, FluxSolver fluxSolver) {
		this.session = session;
		this.fluxSolver = fluxSolver;
	}

	/**
	 * Updates the set of undefined edges, after a node has been updated and is active. The method
	 * ensures that all undefined edges of the particular node are used as sources for suggesting
	 * potential solutions.
	 *
	 * @param node the node that is active
	 */
	public void addUndefinedEdges(Node node) {
		if (fluxSolver.getSuggestMode() == SuggestMode.ignore) return;
		// check all outgoing edges whether they are "undefined" or not
		for (Edge edge : node.getOutgoingEdges()) {
			Condition condition = edge.getCondition();
			if (condition != null && Conditions.isUndefined(condition, session)) {
				undefinedEdges.add(edge);
			}
			else {
				undefinedEdges.remove(edge);
			}
		}
	}

	/**
	 * Updates the set of undefined edges, after a node has been updated and is not active. The
	 * method ensures that no edges of the particular node ist used as a source for suggesting
	 * potential solutions.
	 *
	 * @param node the node that is inactive
	 */
	public void removeUndefinedEdges(Node node) {
		if (fluxSolver.getSuggestMode() == SuggestMode.ignore) return;
		// if the node is deactivated, we also do not want the edges
		// in our "potentially becoming active" edge-set.
		undefinedEdges.removeAll(node.getOutgoingEdges());
	}

	/**
	 * Updates the set of undefined edges for the current session from scratch.
	 */
	public void rebuildUndefinedEdges() {
		if (fluxSolver.getSuggestMode() == SuggestMode.ignore) return;
		undefinedEdges.clear();
		for (Node node : getActiveNodes()) {
			addUndefinedEdges(node);
		}
	}

	/**
	 * Returns the set of active nodes of all current flow runs.
	 *
	 * @return all active nodes
	 */
	public Set<Node> getActiveNodes() {
		Set<Node> activeNodes = new HashSet<Node>();
		for (FlowRun run : runs) {
			activeNodes.addAll(run.getActiveNodes());
		}
		return activeNodes;
	}

	/**
	 * Returns all edges of the current session that are at active nodes but being undefined, that
	 * means they may become true in the further interview process. It represents the number of
	 * potentially still open paths of the current interview.
	 * <p/>
	 * Please note that the edge will be empty of the flag "suspectPotentialSolutions" is not active
	 * in this instance.
	 *
	 * @return the start edges of the potentially still open paths
	 */
	public Set<Edge> getUndefinedEdges() {
		return Collections.unmodifiableSet(undefinedEdges);
	}

	/**
	 * Records that a snapshot has been executed for this session in this propagation. This is
	 * required to detect cyclic propagations in flowcharts with one or more snapshots in the
	 * cycle.
	 *
	 * @param node the snapshot node that have been snapshoted
	 * @created 28.02.2011
	 */
	public void snapshotDone(SnapshotNode node) {
		long time = session.getPropagationManager().getPropagationTime();
		this.latestSnapshotTime.put(node, time);
	}

	/**
	 * Returns the latest (most recent) time a snapshot has been taken. If no snapshot has been
	 * taken yet, null is returned.
	 *
	 * @return the most recent snapshot time
	 * @created 01.03.2011
	 */
	public Date getLatestSnaphotTime() {
		Collection<Long> values = this.latestSnapshotTime.values();
		if (values.isEmpty()) return null;

		long maxTime = Long.MIN_VALUE;
		for (Long time : values) {
			maxTime = Math.max(maxTime, time.longValue());
		}
		return new Date(maxTime);
	}

	public void addRun(FlowRun run) {
		this.runs.add(run);
	}

	public void removeRun(FlowRun run) {
		this.runs.remove(run);
	}

	public List<FlowRun> getRuns() {
		return Collections.unmodifiableList(runs);
	}

	/**
	 * Returns all activated snapshots, but remove those that have been detected to be cyclic in
	 * this propagation.
	 *
	 * @return the snapshot nodes to be snapshoted
	 * @created 28.02.2011
	 */
	public Collection<SnapshotNode> getActivatedSnapshots() {
		Collection<SnapshotNode> result = new HashSet<SnapshotNode>();
		for (FlowRun run : this.runs) {
			for (SnapshotNode node : run.getActivatedNodesOfClass(SnapshotNode.class)) {
				if (!run.isSnapshotBlocked(node, session)) {
					result.add(node);
				}
			}
		}
		return Collections.unmodifiableCollection(result);
	}

	/**
	 * Suspects all the potential solutions that might become established in the further interview
	 * process. It also de-suspect all the solutions that are no longer in that list.
	 */
	public void updateSuspectedSolutions() {
		Set<Node> startNodes = new HashSet<Node>();
		for (Edge edge : undefinedEdges) {
			startNodes.add(edge.getEndNode());
		}

		Set<Solution> solutions = findPotentialSolutions(startNodes);
		Rating suggest = new Rating(Rating.State.SUGGESTED);

		// remove facts for no longer suspected solutions
		for (Solution solution : suspectedSolutions) {
			if (!solutions.contains(solution)) {
				Fact fact = FactFactory.createFact(solution, suggest, FluxSolver.SUGGEST_SOURCE,
						fluxSolver);
				session.getBlackboard().removeValueFact(fact);
			}
		}

		// add facts for newly suspected solutions
		for (Solution solution : solutions) {
			if (!suspectedSolutions.contains(solution)) {
				Fact fact = FactFactory.createFact(solution, suggest, FluxSolver.SUGGEST_SOURCE,
						fluxSolver);
				session.getBlackboard().addValueFact(fact);
			}
		}

		// store the new set of solutions
		suspectedSolutions = solutions;
	}

	private Set<Solution> findPotentialSolutions(Set<Node> startNodes) {
		Set<Node> nodes = findPotentialNodes(startNodes);
		Set<Solution> solutions = new HashSet<Solution>();
		for (Node node : nodes) {
			// if node rates a solution positively, add the solution,
			// otherwise continue to next node
			if (node instanceof ActionNode) {
				PSAction action = ((ActionNode) node).getAction();
				if (action instanceof ActionHeuristicPS) {
					ActionHeuristicPS heuristic = (ActionHeuristicPS) action;
					Score score = heuristic.getScore();
					if (score.getScore() > 0) {
						solutions.add(heuristic.getSolution());
					}
				}
			}
		}
		return solutions;
	}

	private Set<Node> findPotentialNodes(Set<Node> openNodes) {
		Set<Node> closedNodes = new HashSet<Node>();
		boolean preciseMode = (fluxSolver.getSuggestMode() == SuggestMode.precise);

		while (!openNodes.isEmpty()) {
			// select node to process
			Node node = openNodes.iterator().next();
			openNodes.remove(node);
			closedNodes.add(node);

			// if a subflow is called, add the subflow's start node for further processing
			// if the start node has not already been processed or is already active
			if (node instanceof ComposedNode) {
				StartNode calledNode = DiaFluxUtils.getCalledStartNode((ComposedNode) node);
				if (!closedNodes.contains(calledNode) &&
						!FluxSolver.isActiveNode(calledNode, session)) {
					openNodes.add(calledNode);
				}
			}

			// TODO: process differently if "node instanceof ComposedNode" in preciseMode
			// process other nodes first and the follow only those outgoing edges
			// that are connected to the active or visited exit nodes
			// (isActive in this session or in either the open nodes or closed nodes list)
			// consider NodeActiveCondition and Not(NodeActiveCondition)
			// -->
			// This may be done by collecting the edges with NodeActiveCondition in a separate map
			// and add the end-nodes of these edges to the open nodes
			// when the exit nodes are processed

			// add all sub-sequential nodes the the open node list
			for (Edge edge : node.getOutgoingEdges()) {
				// skip next node if already processed
				Node next = edge.getEndNode();
				if (closedNodes.contains(next)) continue;

				// also skip all nodes that are already active
				if (FluxSolver.isActiveNode(next, session)) continue;

				// check if the edges shall be checked
				// (in this case we also check if the node is already open,
				// to minimize condition evaluation)
				if (preciseMode && !openNodes.contains(next)) {
					Condition condition = edge.getCondition();
					// if the edge has a condition and the condition is false,
					// skip it
					if (condition != null && Conditions.isFalse(condition, session)) {
						continue;
					}
				}

				// add all next nodes, if the edges are accepted
				openNodes.add(next);
			}
		}

		return closedNodes;
	}

	/**
	 * Returns whether the specified node is currently active within this session. Please note that
	 * a node previously being active and then fixed by a snapshot is not considered to be active
	 * any longer, even if its derived facts still persists.
	 *
	 * @param node the node to be checked
	 * @return if the node is active in the session
	 * @created 11.03.2013
	 */
	public boolean isActiveNode(Node node) {
		for (FlowRun flowRun : runs) {
			if (flowRun.isActive(node)) return true;
		}
		return false;
	}

	/**
	 * Returns whether the specified edge is currently active within this session. Please note that
	 * a edge previously being active and then fixed by a snapshot is not considered to be active
	 * any longer, even if its derived facts still persists.
	 *
	 * @param edge the Edge to be checked
	 * @return if the edge is active in the session
	 * @created 11.03.2013
	 */
	public boolean isActiveEdge(Edge edge) {
		for (FlowRun flowRun : runs) {
			if (flowRun.isActivated(edge)) return true;
		}
		return false;
	}
}
