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
package de.d3web.costBenefit.ids;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.costBenefit.Util;
import de.d3web.costBenefit.inference.AbortException;
import de.d3web.costBenefit.inference.AbortStrategy;
import de.d3web.costBenefit.inference.DefaultAbortStrategy;
import de.d3web.costBenefit.model.Node;
import de.d3web.costBenefit.model.Path;
import de.d3web.costBenefit.model.SearchModel;
import de.d3web.costBenefit.model.Target;

/**
 * This IterativeDeepeningSearch is extended by multiple optimizations. It
 * operates on a SearchModel, wich provides all used methods
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
class IterativeDeepeningSearch {

	private final Node[] successorNodes;
	private final Node[] finalNodes;
	public long count = 0;
	private final SearchModel model;
	private Path minSearchedPath;
	// TODO move to constructor...
	private AbortStrategy abortStrategy = new DefaultAbortStrategy();

	public IterativeDeepeningSearch(SearchModel model) {
		this.model = model;
		// get finalNodes
		Set<Target> possibleTargets = model.getTargets();
		List<Node> temp = new LinkedList<Node>();
		for (Target t : possibleTargets) {
			for (QContainer qcon : t) {
				temp.add(model.getQContainerNode(qcon));
			}
		}
		finalNodes = temp.toArray(new Node[temp.size()]);
		Set<Node> nodeList = model.getNodes();
		HashSet<Node> relevantNodes = new HashSet<Node>();
		// Nodes without post transitions are not relevant as successors
		// TODO more sophisticated filter of successors (only relevant
		// transitions, maybe backward search)
		for (Node node : nodeList) {
			if (node.getStateTransition() != null
					&& node.getStateTransition().getPostTransitions() != null
					&& !node.getStateTransition().getPostTransitions()
					.isEmpty()) {
				relevantNodes.add(node);
			}
		}
		// remove all Target nodes
		relevantNodes.removeAll(temp);
		// reenter Target nodes that are used in combined targets
		relevantNodes.addAll(model.getCombinedTargetsNodes());
		this.successorNodes = relevantNodes.toArray(new Node[relevantNodes
				.size()]);
		// cheaper nodes are tried as successors first
		Arrays.sort(successorNodes, new Comparator<Node>() {

			@Override
			public int compare(Node o1, Node o2) {
				return (int) (o1.getStaticCosts() - o2.getStaticCosts());
			}
		});

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
	 * @param theCase
	 */
	public void search(Session theCase) {
		// Abort if there are no targets in the model
		if (!model.hasTargets()) return;
		abortStrategy.init(model);
		Session testcase = Util.copyCase(theCase);
		try {
			search(testcase, 1);
		}
		catch (AbortException e) {
			// we have stopped at the search due to time restrictions.
			// use the best found path till now
		}
	}

	private void search(Session testcase, int depth) throws AbortException {
		if ((depth <= 0) || model.getTargets() == null
				|| model.getTargets().size() == 0) return;
		Path actual = new Path();
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
			for (Node node : successorNodes) {
				if (!minSearchedPath.contains(node)) {
					mincosts += node.getStaticCosts();
					break;
				}
			}
		}
		if (model.allTargetsReached()
				|| (model.getBestCostBenefit() < mincosts
				/ model.getBestUnreachedBenefit())) {
			// stop iterative deep search if each target node has been reached
			// or if the minimal costs are to high for even for the most
			// beneficial
			// test step
			return;
		}
		else if (depth > successorNodes.length) {
			// stop iterative depth search if all test steps have been used
			return;
		}
		else {
			// otherwise try to find solutions with one more iteration
			// if (depth>7) return;
			search(testcase, ++depth);
		}
	}

	private void findCheapestPath(Path actual, int depth, Session theCase)
			throws AbortException {

		if (actual.getCosts() / model.getBestBenefit() > model
				.getBestCostBenefit()) {
			// nothing to do
		}
		else if (depth == 1) {
			for (Node n : finalNodes) {
				if (!isValidSuccessor(actual, n, theCase)) continue;
				actual.add(n, theCase);
				abortStrategy.nextStep(actual);
				model.minimizePath(actual);
				actual.pop();
			}
			for (Node n : successorNodes) {
				if (!isValidSuccessor(actual, n, theCase)) continue;
				actual.add(n, theCase);
				abortStrategy.nextStep(actual);
				if (minSearchedPath == null
						|| actual.getCosts() < minSearchedPath.getCosts()) {
					minSearchedPath = actual.copy();
				}
				actual.pop();
			}
		}
		else {
			// Session testcase = Util.copyCase(theCase);
			for (Node successor : successorNodes) {
				if (!isValidSuccessor(actual, successor, theCase)) continue;
				List<Fact> undo = new LinkedList<Fact>();
				actual.add(successor, theCase);
				abortStrategy.nextStep(actual);
				undo.addAll(successor.setNormalValues(theCase));
				undo.addAll(successor.getStateTransition().fire(theCase));
				findCheapestPath(actual, depth - 1, theCase);
				count += undo.size();
				actual.pop();
				Util.undo(theCase, undo);
			}
		}
	}

	private boolean isValidSuccessor(Path actual, Node n, Session theCase) {
		// skip not applicable successors
		if (!n.isApplicable(theCase)) return false;
		if (actual.isEmpty()) return true;
		return true;
	}

}
