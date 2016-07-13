/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.costbenefit.ids;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.costbenefit.CostBenefitUtil;
import de.d3web.costbenefit.inference.AbortException;
import de.d3web.costbenefit.inference.AbortStrategy;
import de.d3web.costbenefit.inference.DefaultAbortStrategy;
import de.d3web.costbenefit.model.SearchModel;
import de.d3web.costbenefit.model.Target;
import de.d3web.costbenefit.model.ids.IDSPath;
import de.d3web.costbenefit.model.ids.Node;
import com.denkbares.utils.Log;

/**
 * This IterativeDeepeningSearch is extended by multiple optimizations. It
 * operates on a SearchModel, which provides all used methods
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
class IterativeDeepeningSearch {

	private static final class StaticCostComparator implements Comparator<Node> {

		@Override
		public int compare(Node o1, Node o2) {
			int comparator = (int) (o1.getStaticCosts() - o2.getStaticCosts());
			if (comparator == 0) {
				return -1 * o1.getQContainer().getName().compareTo(o2.getQContainer().getName());
			}
			else {
				return comparator;
			}
		}
	}

	private final Node[] successorNodes;
	private final Node[] finalNodes;
	private final SearchModel model;
	private IDSPath minSearchedPath;
	// TODO move to constructor...
	private AbortStrategy abortStrategy = new DefaultAbortStrategy();
	private final Map<Node, List<Target>> referencingTargets = new HashMap<>();
	private final Map<QContainer, Node> map = new HashMap<>();
	private int countMinPaths = 0;
	private final Session originalSession;

	// some information about the current search
	private final transient long initTime;
	private transient int steps = 0;

	public IterativeDeepeningSearch(SearchModel model) {
		long time = System.currentTimeMillis();
		this.model = model;
		originalSession = model.getSession();
		for (QContainer qcon : originalSession.getKnowledgeBase().getManager().getQContainers()) {
			Node containerNode = new Node(qcon, model);
			map.put(qcon, containerNode);
		}
		for (Target target : model.getTargets()) {
			if (target.getMinPath() != null) countMinPaths++;
			for (QContainer qcon : target.getQContainers()) {
				Node key = map.get(qcon);
				List<Target> refs = this.referencingTargets.get(key);
				if (refs == null) {
					refs = new LinkedList<>();
					this.referencingTargets.put(key, refs);
				}
				refs.add(target);
			}
		}
		// get finalNodes
		List<Target> possibleTargets = new LinkedList<>(model.getTargets());
		Collections.sort(possibleTargets, (o1, o2) -> {
			String name1 = o1.getQContainers().get(0).getName();
			String name2 = o2.getQContainers().get(0).getName();
			return -1 * (name1.compareTo(name2));
		});
		List<Node> temp = new LinkedList<>();
		for (Target t : possibleTargets) {
			for (QContainer qcon : t.getQContainers()) {
				temp.add(map.get(qcon));
			}
		}
		finalNodes = temp.toArray(new Node[temp.size()]);
		Set<Node> nodeList = getNodes();
		HashSet<Node> relevantNodes = new HashSet<>();
		Blackboard blackboard = originalSession.getBlackboard();
		// Nodes without post transitions are not relevant as successors
		// TODO more sophisticated filter of successors (only relevant
		// transitions, maybe backward search)
		for (Node node : nodeList) {
			// contraindicated QContainers must not be used
			if (blackboard.getIndication(node.getQContainer()).isContraIndicated()) continue;
			if (node.getStateTransition() != null
					&& node.getStateTransition().getPostTransitions() != null
					&& !node.getStateTransition().getPostTransitions()
							.isEmpty()) {
				relevantNodes.add(node);
			}
		}
		// remove all Target nodes
		// relevantNodes.removeAll(temp);
		// reenter Target nodes that are used in combined targets
		relevantNodes.addAll(getCombinedTargetsNodes());
		this.successorNodes = relevantNodes.toArray(new Node[relevantNodes
				.size()]);
		// cheaper nodes are tried as successors first
		Arrays.sort(successorNodes, new StaticCostComparator());
		this.initTime = System.currentTimeMillis() - time;
	}

	public AbortStrategy getAbortStrategy() {
		return abortStrategy;
	}

	public void setAbortStrategy(AbortStrategy abortStrategy) {
		this.abortStrategy = abortStrategy;
	}

	/**
	 * Starts the search with depth 1
	 *
	 * @param session the session to be searched
	 */
	public void search(Session session) {
		// Abort if there are no targets in the model
		if (!model.hasTargets()) return;
		long time1 = System.currentTimeMillis();
		abortStrategy.init(model);
		Session testcase = CostBenefitUtil.createSearchCopy(session);
		try {
			search(testcase, 1);
		}
		catch (AbortException e) {
			// we have stopped at the search due to time restrictions.
			// use the best found path till now
			model.setAbort(true);
		}
		long time2 = System.currentTimeMillis();
		Log.info("IDS Calculation " +
				(model.isAborted() ? "aborted" : "done") + " (" +
				"#steps: " + steps + ", " +
				"time: " + (time2 - time1) + "ms, " +
				"init: " + initTime + "ms)");
	}

	private void search(Session testcase, int depth) throws AbortException {
		if ((depth <= 0) || model.getTargets() == null
				|| model.getTargets().isEmpty()) {
			return;
		}
		IDSPath actual = new IDSPath();
		minSearchedPath = null;
		findCheapestPath(actual, depth, testcase);
		double mincosts;
		if (minSearchedPath == null) {
			// if no minSearchedPath is found, all paths have been cut due to
			// high cost/benefit relation
			return;
		}
		else {
			// if a minSearchPath is found, the costs are at least the costs of
			// the minSearchPath plus the cheapest unused teststep
			mincosts = minSearchedPath.getCosts();
			List<Node> nextnodes = new LinkedList<>();
			nextnodes.addAll(Arrays.asList(successorNodes));
			nextnodes.addAll(Arrays.asList(finalNodes));
			Collections.sort(nextnodes, new StaticCostComparator());
			for (Node node : nextnodes) {
				if (!minSearchedPath.contains(node)) {
					mincosts += node.getStaticCosts();
					break;
				}
			}
		}
		if (allTargetsReached()
				|| (model.getBestCostBenefit() < mincosts
						/ model.getBestUnreachedBenefit())) {
			// stop iterative deep search if each target node has been reached
			// or if the minimal costs are to high for even for the most
			// beneficial
			// test step
		}
		else if (depth > successorNodes.length) {
			// stop iterative depth search if all test steps have been used
		}
		else {
			// otherwise try to find solutions with one more iteration
			// if (depth>7) return;
			search(testcase, ++depth);
		}
	}

	private void findCheapestPath(IDSPath actual, int depth, Session session) throws AbortException {

		if (actual.getCosts() / model.getBestBenefit() > model
				.getBestCostBenefit()) { // NOSONAR
			// nothing to do
		}
		else if (depth == 1) {
			for (Node n : finalNodes) {
				if (!isValidSuccessor(actual, n, session)) continue;
				actual.add(n, session);
				nextStep(actual);
				minimizePath(actual);
				actual.pop();
			}
			for (Node n : successorNodes) {
				if (!isValidSuccessor(actual, n, session)) continue;
				actual.add(n, session);
				nextStep(actual);
				if (minSearchedPath == null
						|| actual.getCosts() < minSearchedPath.getCosts()) {
					minSearchedPath = actual.copy();
				}
				actual.pop();
			}
		}
		else {
			// Session testcase = Util.copyCase(session);
			for (Node successor : successorNodes) {
				if (!isValidSuccessor(actual, successor, session)) continue;
				List<Fact> undo = new LinkedList<>();
				actual.add(successor, session);
				nextStep(actual);
				undo.addAll(successor.setNormalValues(session));
				if (successor.getStateTransition() != null) {
					undo.addAll(successor.getStateTransition().fire(session));
				}
				findCheapestPath(actual, depth - 1, session);
				actual.pop();
				CostBenefitUtil.undo(session, undo);
			}
		}
	}

	private void nextStep(IDSPath actual) throws AbortException {
		steps++;
		abortStrategy.nextStep(actual, originalSession);
		// if we are aborted externally, do the same way
		if (model.isAborted()) {
			throw new AbortException();
		}
	}

	private boolean isValidSuccessor(IDSPath actual, Node n, Session session) {
		// skip not applicable successors
		if (!n.isApplicable(session)) return false;
		if (actual.isEmpty()) return true;
		return true;
	}

	/**
	 * Returns all nodes of the graph to be searched.
	 */
	private Set<Node> getNodes() {
		Set<Node> nodeList = new HashSet<>();
		for (Node node : map.values()) {
			if (node.getStateTransition() != null) nodeList.add(node);
		}
		return nodeList;
	}

	/**
	 * Returns all Nodes contained in combined Targets (targets with more than
	 * one target Node)
	 */
	private Collection<? extends Node> getCombinedTargetsNodes() {
		List<Node> list = new LinkedList<>();
		for (Target t : model.getTargets()) {
			List<QContainer> qContainers = t.getQContainers();
			if (qContainers.size() > 1) {
				for (QContainer qcon : qContainers) {
					list.add(map.get(qcon));
				}
			}
		}
		return list;
	}

	/**
	 * Minimizes if necessary the path in all targets which are reached
	 *
	 * @param path the path to be minimized
	 */
	private void minimizePath(IDSPath path) {
		Node node = path.getLastNode();
		List<Target> theTargets = this.referencingTargets.get(node);
		for (Target t : theTargets) {
			if (t.isReached(path)) {
				if (t.getMinPath() == null) countMinPaths++;
				if (t.getMinPath() == null || t.getMinPath().getCosts() > path.getCosts()) {
					t.setMinPath(path.copy());
					model.checkTarget(t);
				}
			}
		}
	}

	/**
	 * Checks if all targets are reached. If this is true, every target has a
	 * minPath.
	 *
	 * @return if all targets are reached
	 */
	public boolean allTargetsReached() {
		return (countMinPaths == model.getTargets().size());
	}
}
