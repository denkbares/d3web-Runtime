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
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.denkbares.collections.ConcatenateIterable;
import com.denkbares.collections.MultiMap;
import com.denkbares.collections.N2MMap;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.Conditions;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.FluxSolver;
import de.d3web.diaFlux.inference.FluxSolver.SuggestMode;
import de.d3web.interview.indication.ActionRepeatedIndication;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.Score;

/**
 * @author Reinhard Hatko
 * @created 04.11.2009
 */
public class DiaFluxCaseObject implements SessionObject {

	private final Session session;
	private final FluxSolver fluxSolver;
	private final List<FlowRun> runs = new ArrayList<>();
	private final Map<SnapshotNode, Long> latestSnapshotTime = new HashMap<>();

	/**
	 * Provide a set of edges from active nodes, but with still undefined conditions, that might
	 * become "true" in the further interview process.
	 */
	private final Set<Edge> undefinedEdges = new HashSet<>();

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
			if ((condition != null && Conditions.isUndefined(condition, session)) ||
					(node instanceof ActionNode && ((ActionNode) node).getAction() instanceof ActionRepeatedIndication)) {
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
		Set<Node> activeNodes = new HashSet<>();
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
			maxTime = Math.max(maxTime, time);
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
		Collection<SnapshotNode> result = new HashSet<>();
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
	 * Returns the solutions that have currently being suspected by the flux solver as the potential
	 * solutions when the diagnosis has finished.
	 *
	 * @return the suspected solutions
	 */
	public Set<Solution> getSuspectedSolutions() {
		return suspectedSolutions;
	}

	/**
	 * Suspects all the potential solutions that might become established in the further interview
	 * process. It also de-suspect all the solutions that are no longer in that list.
	 */
	public void updateSuspectedSolutions() {
		Set<Node> startNodes = new HashSet<>();
		for (Edge edge : undefinedEdges) {
			Node endNode = edge.getEndNode();
			if (endNode != null) { // robustness for incomplete flows
				startNodes.add(endNode);
			}
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
		potentialSuccessors = null;
	}

	private Set<Solution> findPotentialSolutions(Set<Node> startNodes) {
		Set<Node> nodes = findPotentialNodes(startNodes, null);
		Set<Solution> solutions = new HashSet<>();
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

	private Set<Node> findPotentialNodes(Set<Node> openNodes, MultiMap<Node, Node> traversal) {
		Set<Node> closedNodes = new HashSet<>();
		boolean preciseMode = (fluxSolver.getSuggestMode() == SuggestMode.precise);

		loopOpenNodes:
		while (!openNodes.isEmpty()) {
			// select node to process
			Node node = openNodes.iterator().next();
			openNodes.remove(node);
			closedNodes.add(node);

			// if a subflow is called, add the subflow's start node for further processing
			// if the start node has not already been processed or is already active
			if (node instanceof ComposedNode) {
				StartNode calledNode = DiaFluxUtils.getCalledStartNode((ComposedNode) node);
				if (traversal != null) traversal.put(node, calledNode);
				if (calledNode != null && !closedNodes.contains(calledNode) &&
						!FluxSolver.isActiveNode(calledNode, session)) {
					openNodes.add(calledNode);
				}
			}

			if (node instanceof EndNode) {
				for (FlowRun run : runs) {
					for (Node startNode : new ConcatenateIterable<>(run.getStartNodes(), run.getActiveNodes())) {
						if (startNode instanceof ComposedNode
								&& ((ComposedNode) startNode).getCalledFlowName()
								.equals(node.getFlow().getName())) {
							if (traversal != null) traversal.put(node, startNode);
							if (!closedNodes.contains(startNode)) {
								openNodes.add(startNode);
								continue loopOpenNodes;
							}
						}
					}
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
				if (traversal != null) traversal.put(node, next);
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

	private N2MMap<Node, Node> potentialSuccessors = null;

	/**
	 * Returns a lazy created (cached) map of successors, leading from the undefined edges through
	 * the potentially following graph.
	 *
	 * @return the
	 */
	private N2MMap<Node, Node> getPotentialSuccessors() {
		if (potentialSuccessors == null) {
			// we create all the potential nodes
			Set<Node> startNodes = new HashSet<>();
			for (Edge edge : undefinedEdges) {
				startNodes.add(edge.getEndNode());
			}
			potentialSuccessors = new N2MMap<>();
			Set<Node> nodes = findPotentialNodes(startNodes, potentialSuccessors);
		}
		return potentialSuccessors;
	}

	public Collection<Question> getDiscriminatingQuestions(Collection<Solution> solutions) {
		if (Collections.disjoint(suspectedSolutions, solutions)) {
			return Collections.emptySet();
		}

		// from the solutions, go backwards through the successors, collecting all nodes
		Set<Node> targetNodes = solutions.stream()
				.filter(suspectedSolutions::contains)
				.flatMap(solution -> solution.getKnowledgeStore()
						.getKnowledge(FluxSolver.DERIVING_NODES).getNodes().stream())
				.collect(Collectors.toSet());

		// select the questions out of the nodes' incoming edges
		return findPotentialEdgesToNodes(targetNodes)
				// get the conditions and their terminal objects
				.map(Edge::getCondition).filter(Objects::nonNull)
				.map(Condition::getTerminalObjects).flatMap(Collection::stream)
				.filter(Question.class::isInstance)
				.map(Question.class::cast).collect(Collectors.toSet());
	}

	/**
	 * Returns the edges that may be walked through from the current flow run states towards the
	 * specified nodes.
	 */
	private Stream<Edge> findPotentialEdgesToNodes(Collection<Node> targetNodes) {
		// from the target nodes, go backwards through the successors, collecting all nodes
		N2MMap<Node, Node> successors = getPotentialSuccessors();
		Set<Node> traversed = new HashSet<>();
		collectPredecessors(targetNodes, successors::getKeys, traversed);

		// select the questions out of the nodes' incoming edges
		return traversed.stream().map(Node::getIncomingEdges).flatMap(Collection::stream)
				// test if the edge is an undefined one or if the start node is also traversed
				// otherwise the edge is not relevant
				.filter(edge -> undefinedEdges.contains(edge) || traversed.contains(edge.getStartNode()));
	}

	private void collectPredecessors(Collection<Node> nodes, Function<Node, Collection<Node>> predeccessors, Set<Node> result) {
		for (Node node : nodes) {
			if (result.add(node)) {
				collectPredecessors(predeccessors.apply(node), predeccessors, result);
			}
		}
	}

	public double getInformationGain(Set<Question> questions, Collection<Solution> solutions) {
		if (Collections.disjoint(suspectedSolutions, solutions)) {
			return 0;
		}
		// we look backwards from each solution,
		// collection all conditions on the specified questions,
		// and create pots for equal conditions
		// from the solutions, go backwards through the successors, collecting all nodes
		// TODO: should not be calculated by collecting the conditions (leading to inprecise results). Instead the answer combinations leading to the solutions should be used. See XCL for further details
		Map<Set<Condition>, Double> pots = new HashMap<>();
		double undiscriminatedWeight = 0;
		double totalWeight = 0;
		for (Solution solution : solutions) {
			Number apriori = solution.getInfoStore().getValue(BasicProperties.APRIORI);
			double weight = (apriori == null) ? 1.0 : apriori.doubleValue();
			totalWeight += weight;

			Set<Condition> conditions = findOpenConditions(solution, questions);
			if (conditions.isEmpty()) {
				undiscriminatedWeight += weight;
			}
			else {
				pots.merge(conditions, weight, Double::sum);
			}
		}

		// calculate information gain
		// Russel & Norvig p. 805
		double sum = 0;
		for (double weight : pots.values()) {
			double p = (weight + undiscriminatedWeight) / totalWeight;
			sum += (-1) * p * Math.log10(p) / Math.log10(2);
		}
		return sum;
	}

	private Set<Condition> findOpenConditions(Solution solution, Set<Question> questions) {
		if (!suspectedSolutions.contains(solution)) return Collections.emptySet();
		Collection<Node> targetNodes = solution.getKnowledgeStore()
				.getKnowledge(FluxSolver.DERIVING_NODES).getNodes();

		// select the questions out of the nodes' incoming edges
		return findPotentialEdgesToNodes(targetNodes)
				// get the conditions and their terminal objects
				.map(Edge::getCondition).filter(Objects::nonNull)
				.filter(c -> !Collections.disjoint(questions, c.getTerminalObjects()))
				.collect(Collectors.toSet());
	}
}
