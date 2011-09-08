/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.d3web.costbenefit.inference.astar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.costbenefit.Util;
import de.d3web.costbenefit.inference.AbortException;
import de.d3web.costbenefit.inference.AbortStrategy;
import de.d3web.costbenefit.inference.CostFunction;
import de.d3web.costbenefit.inference.DefaultAbortStrategy;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.inference.ValueTransition;
import de.d3web.costbenefit.model.SearchModel;
import de.d3web.costbenefit.model.Target;

/**
 * Algorithm which uses A* to find pathes to the targets
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 22.06.2011
 */
public class AStar {

	/**
	 * Runnable that does the search and handles abort exception. Returns true
	 * if the search has been stopped after finding the best possible result. It
	 * returns false if the search has been aborted before.
	 * 
	 * @author volker_belli
	 * @created 08.09.2011
	 */
	private final class SearchWorker implements Callable<Boolean> {

		@Override
		public Boolean call() {
			try {
				searchLoop();
			}
			catch (AbortException e) {
				// nothing to do
				// we only tell other tasks also to abort
				return false;
			}
			return true;
		}
	}

	private final SearchModel model;
	private final Set<Question> stateQuestions = new HashSet<Question>();
	private final Queue<Node> openNodes = new PriorityQueue<Node>();
	private final Collection<Node> closedNodes = new LinkedList<Node>();
	private final Map<State, Node> nodes = new HashMap<State, Node>();
	private final AStarAlgorithm algorithm;
	private final Collection<StateTransition> successors;
	private final CostFunction costFunction;
	private final AbortStrategy abortStrategy;
	private final Session session;
	private final transient long initTime;

	public AStar(Session session, SearchModel model, AStarAlgorithm algorithm) {
		long time = System.currentTimeMillis();
		this.algorithm = algorithm;
		this.session = session;
		this.model = model;
		if (algorithm.getAbortStrategy() != null) {
			this.abortStrategy = algorithm.getAbortStrategy();
		}
		else {
			this.abortStrategy = new DefaultAbortStrategy(5000, 1);
		}
		this.costFunction = session.getPSMethodInstance(PSMethodCostBenefit.class).getCostFunction();
		for (StateTransition st : session.getKnowledgeBase().getAllKnowledgeSlicesFor(
				StateTransition.KNOWLEDGE_KIND)) {
			if (st.getActivationCondition() != null) {
				for (TerminologyObject object : st.getActivationCondition().getTerminalObjects()) {
					if (object instanceof Question) {
						stateQuestions.add((Question) object);
					}
				}
			}
			for (ValueTransition t : st.getPostTransitions()) {
				stateQuestions.add(t.getQuestion());
			}
		}
		// TODO: add position question to stateQuestions, USE Transition
		// questions
		Node start = new Node(computeState(session), session, new AStarPath(null, null, 0), 0);
		openNodes.add(start);
		successors = session.getKnowledgeBase().getAllKnowledgeSlicesFor(
				StateTransition.KNOWLEDGE_KIND);
		// QContainers without a StateTransition can be executed at any time,
		// but they cannot be used as intermediate steps because they have no
		// transitions, so they are checked as targets before the calculation
		// starts
		for (QContainer qcon : session.getKnowledgeBase().getManager().getQContainers()) {
			if (StateTransition.getStateTransition(qcon) == null) {
				updateTargets(new AStarPath(qcon, null, costFunction.getCosts(qcon, session)));
			}
		}
		this.initTime = System.currentTimeMillis() - time;
	}

	private State computeState(Session session) {
		State state = new State(session, stateQuestions);
		return state;
	}

	/**
	 * Starts the search
	 * 
	 * @created 22.06.2011
	 */
	public void search() {
		long time1 = System.currentTimeMillis();
		abortStrategy.init(model);
		algorithm.getHeuristic().init(model);

		boolean succeeded;
		int threadCount = algorithm.getThreadCount();
		if (threadCount == 1) {
			succeeded = searchSingleThreaded();
		}
		else {
			succeeded = searchMultiThreaded(threadCount);
		}

		long time2 = System.currentTimeMillis();
		if (abortStrategy instanceof DefaultAbortStrategy) {
			System.out.println("A* Calculation " +
					(succeeded ? "done" : "aborted") + " (" +
					"#steps: " + ((DefaultAbortStrategy) abortStrategy).getSteps(session) + ", " +
					"time: " + (time2 - time1) + "ms, " +
					"init: " + initTime + "ms, " +
					"#open: " + openNodes.size() + ", " +
					"#closed: " + closedNodes.size() + ")");
		}
	}

	/**
	 * Returns true if the search has been stopped after finding the best
	 * possible result. It returns false if the search has been aborted before.
	 * 
	 * @created 08.09.2011
	 * @return if the search could been completed
	 */
	private boolean searchSingleThreaded() {
		// simply call the search "in-thread"
		return new SearchWorker().call();
	}

	/**
	 * Returns true if the search has been stopped after finding the best
	 * possible result. It returns false if the search has been aborted before.
	 * 
	 * @created 08.09.2011
	 * @return if the search could been completed
	 */
	private boolean searchMultiThreaded(int threadCount) {
		ExecutorService service = algorithm.getExecutorService();
		List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
		for (int i = 0; i < threadCount; i++) {
			Future<Boolean> submit = service.submit(new SearchWorker());
			futures.add(submit);
		}
		boolean anySucceeded = false;
		for (Future<Boolean> future : futures) {
			try {
				// await termination of every future
				anySucceeded |= future.get();
			}
			catch (InterruptedException e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE,
						"error in cost/benefit search thread", e);
			}
			catch (ExecutionException e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE,
						"error in cost/benefit search thread", e);
			}
		}
		return anySucceeded;
	}

	private void searchLoop() throws AbortException {
		while (true) {
			// check for the next open node to be processed
			// due to parallelism, we do this synchronized
			// until we are sure to have a node
			// and have its current revision before starting expanding
			Node node;
			AStarPath nodePath;
			synchronized (this) {
				node = openNodes.poll();
				if (node == null) break;
				nodePath = node.getPath();
			}
			// if a target has been reached and its cost/benefit is better than
			// the optimistic fValue of the best node, terminate the algorithm
			if (model.getBestCostBenefitTarget() != null
					&& model.getBestCostBenefitTarget().getCostBenefit() < node.getfValue()) {
				break;
			}

			// then we expand the node without focusing to revisions
			expandNode(node);

			// afterwards we check, if the expanded node has been updated,
			// e.g. by an other node expanding thread
			// (finding a shorter path to the currently expanded node)
			synchronized (this) {
				AStarPath newPath = node.getPath();
				// we are updated if the path has changed
				if (!newPath.equals(nodePath)) {
					// update all successors based on the old
					// path to be based on the new path for now
					System.out.println("A* violation due to parallelism, re-checking affected nodes");
					for (Node checked : this.openNodes) {
						replaceNodePath(checked, nodePath, newPath);
					}
					for (Node checked : this.closedNodes) {
						replaceNodePath(checked, nodePath, newPath);
					}
				}

				closedNodes.add(node);
			}
			// System.out.println("\tnode closed");
		}
	}

	/**
	 * Checks if the specified node relies on the old path. If yes, the path
	 * will be replaced by the new path, including optimization of optimal paths
	 * in the search model.
	 * 
	 * @created 08.09.2011
	 * @param checked the node to be checked
	 * @param oldPath the original path prefix to be replaced
	 * @param newPath the new path prefix replacing the old one
	 */
	private void replaceNodePath(Node checked, AStarPath oldPath, AStarPath newPath) {
		// check if this node hat the required prefix to be replaced
		boolean hasPrefix = checked.getPath().hasPrefix(oldPath);
		System.out.println(
				"prefix " + hasPrefix + " of " +
						checked.getPath().getPath() + " and " + oldPath.getPath());
		if (!hasPrefix) return;

		// if yes, we create the prefix-replaced path
		// and update the calculated costs of the targets
		AStarPath createdPath = checked.getPath().replacePrefix(oldPath, newPath);
		double f = calculateFValue(createdPath.getCosts(), checked.getState(), checked.getSession());
		checked.updatePath(createdPath);
		checked.setfValue(f);
		updateTargets(createdPath);
	}

	private void expandNode(Node node) throws AbortException {
		Session actualSession = node.getSession();
		for (StateTransition st : successors) {
			QContainer qcontainer = st.getQcontainer();
			List<QContainer> path = node.getPath().getPath();
			// do not repeat qcontainers in a row
			if (path.size() > 0 && path.get(path.size() - 1) == qcontainer) continue;
			if (actualSession.getBlackboard().getIndication(qcontainer).isContraIndicated()) continue;
			try {
				if (st.getActivationCondition() == null
						|| st.getActivationCondition().eval(actualSession)) {
					Session copiedSession = Util.copyCase(actualSession);
					double costs = costFunction.getCosts(qcontainer, copiedSession);
					Util.setNormalValues(copiedSession, qcontainer, this);
					st.fire(copiedSession);
					State newState = computeState(copiedSession);
					AStarPath newPath = new AStarPath(qcontainer, node.getPath(), costs);
					synchronized (this) {
						Node follower = nodes.get(newState);
						if (follower == null) {
							// create a new one, because it does not exist
							double f = calculateFValue(newPath.getCosts(), newState, copiedSession);
							follower = new Node(newState, copiedSession, newPath, f);
							nodes.put(newState, follower);
							openNodes.add(follower);
							// System.out.println("\tnode added");
						}
						else if (follower.getPath().getCosts() > newPath.getCosts()) {
							// remove, update, add again to preserve ordering
							double f = calculateFValue(newPath.getCosts(), newState, copiedSession);
							openNodes.remove(follower);
							follower.updatePath(newPath);
							follower.setfValue(f);
							openNodes.add(follower);
							// System.out.println("\tnode updated");
						}
					}
					updateTargets(newPath);
					abortStrategy.nextStep(node.getPath(), session);
				}
			}
			catch (NoAnswerException e) {
				// do nothing
			}
			catch (UnknownAnswerException e) {
				// do nothing
			}
		}
	}

	private void updateTargets(AStarPath newPath) {
		QContainer qcontainer = newPath.getQContainer();
		for (Target t : model.getTargets()) {
			if (t.getQContainers().size() == 1) {
				if (t.getQContainers().get(0) == qcontainer) {
					// this has to be checked because there
					// can be several nodes to reach a
					// target, one of the other nodes could
					// be cheaper
					if (t.getMinPath() == null
							|| t.getMinPath().getCosts() > newPath.getCosts()) {
						t.setMinPath(newPath);
						model.checkTarget(t);
					}
				}
			}
			// TODO: Multitarget?
		}
	}

	/**
	 * 
	 * @created 27.06.2011
	 * @param costs
	 * @param newState
	 * @return
	 */
	private double calculateFValue(double pathcosts, State state, Session session) {
		double min = Double.POSITIVE_INFINITY;
		for (Target target : model.getTargets()) {
			double costs = pathcosts;
			if (target.getQContainers().size() == 1) {
				QContainer qContainer = target.getQContainers().get(0);
				// adding the costs calculated by the heuristic
				costs += algorithm.getHeuristic().getDistance(state, qContainer);
				// adding the costs of the target
				costs += costFunction.getCosts(qContainer, session);
				// dividing the whole costs by the benefit
				costs /= target.getBenefit();
				min = Math.min(min, costs);
			}
			// TODO: Multitarget?
		}
		return min;
	}

}
