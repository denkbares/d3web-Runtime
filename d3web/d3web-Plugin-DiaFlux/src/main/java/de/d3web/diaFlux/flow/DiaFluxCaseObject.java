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
import de.d3web.diaFlux.inference.FluxSolver;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.Score;

/**
 * @author Reinhard Hatko
 *         <p/>
 *         Created on: 04.11.2009
 */
public class DiaFluxCaseObject implements SessionObject {

	private final Session session;
	private final FluxSolver fluxSolver;
	private final boolean suspectPotentialSolutions;
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

	public DiaFluxCaseObject(Session session, FluxSolver fluxSolver, boolean suspectPotentialSolutions) {
		this.session = session;
		this.fluxSolver = fluxSolver;
		this.suspectPotentialSolutions = suspectPotentialSolutions;
	}

	/**
	 * Updates the set of undefined edges, after a node has been updated.
	 *
	 * @param node the node that has been updated
	 * @param isActive true, if the node is now active or false otherwise
	 */
	public void updateUndefinedEdges(Node node, boolean isActive) {
		if (!suspectPotentialSolutions) return;
		if (isActive) {
			// if the node is active, we have to check all outgoing
			// edges whether they are "undefined" or not
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
		else {
			// if the node is deactivated, we also do not want the edges
			// in our "potentially becoming active" edge-set.
			undefinedEdges.removeAll(node.getOutgoingEdges());
		}
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
				Fact fact = FactFactory.createFact(solution, suggest, fluxSolver, fluxSolver);
				session.getBlackboard().removeValueFact(fact);
			}
		}

		// add facts for newly suspected solutions
		for (Solution solution : solutions) {
			if (!suspectedSolutions.contains(solution)) {
				Fact fact = FactFactory.createFact(solution, suggest, fluxSolver, fluxSolver);
				session.getBlackboard().removeValueFact(fact);
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
		Set<Solution> solutions = new HashSet<Solution>();

		while (!openNodes.isEmpty()) {
			// select node to process
			Node node = openNodes.iterator().next();
			openNodes.remove(node);
			closedNodes.add(node);

			// add all subsequential nodes
			for (Edge edge : node.getOutgoingEdges()) {
				Node next = edge.getEndNode();
				if (!closedNodes.contains(next)) {
					openNodes.add(next);
				}
			}
		}

		return closedNodes;
	}
}
