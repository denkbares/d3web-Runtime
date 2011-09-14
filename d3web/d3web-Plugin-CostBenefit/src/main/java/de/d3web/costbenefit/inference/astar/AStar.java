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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.costbenefit.Util;
import de.d3web.costbenefit.inference.AbortException;
import de.d3web.costbenefit.inference.CostFunction;
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
	 * Expands a node with a specified StateTransition and creates a new
	 * follower node.
	 * 
	 * @author volker_belli
	 * @created 09.09.2011
	 */
	private final class NodeExpander implements Callable<Node> {

		private final Node sourceNode;
		private final StateTransition stateTransition;

		public NodeExpander(Node sourceNode, StateTransition stateTransition) {
			this.sourceNode = sourceNode;
			this.stateTransition = stateTransition;
		}

		@Override
		public Node call() {
			Node follower = applyTransition(sourceNode, stateTransition);
			// installNode(follower);
			return follower;
		}
	}

	private static final Logger log = Logger.getLogger(IterableExecutor.class.getName());

	private final SearchModel model;
	private final Set<Question> stateQuestions = new HashSet<Question>();
	private final Queue<Node> openNodes = new PriorityQueue<Node>();
	private final Collection<Node> closedNodes = new LinkedList<Node>();
	private final Map<State, Node> nodes = new HashMap<State, Node>();
	private final AStarAlgorithm algorithm;
	private final Collection<StateTransition> successors;
	private final CostFunction costFunction;
	private final Session session;

	// some information about the current search
	private final transient long initTime;
	private transient int steps = 0;

	public AStar(Session session, SearchModel model, AStarAlgorithm algorithm) {
		long time = System.currentTimeMillis();
		this.algorithm = algorithm;
		this.session = session;
		this.model = model;
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
		algorithm.getAbortStrategy().init(model);
		algorithm.getHeuristic().init(model);
		searchLoop();
		long time2 = System.currentTimeMillis();
		log.info("A* Calculation " + (model.isAborted() ? "aborted" : "done") + " (" +
				"#steps: " + steps + ", " +
				"time: " + (time2 - time1) + "ms, " +
				"init: " + initTime + "ms, " +
				"#open: " + openNodes.size() + ", " +
				"#closed: " + closedNodes.size() + ")");
	}

	private void searchLoop() {
		while (!model.isAborted() && !openNodes.isEmpty()) {
			// check for the next open node to be processed
			Node node = openNodes.poll();

			// if a target has been reached and its cost/benefit is better than
			// the optimistic fValue of the best node, terminate the algorithm
			if (model.getBestCostBenefitTarget() != null
					&& model.getBestCostBenefitTarget().getCostBenefit() < node.getfValue()) {
				break;
			}

			// then we expand the node without focusing to revisions
			if (algorithm.isMultiCore()) {
				expandNodeMultiThreaded(node);
			}
			else {
				expandNodeSingleThreaded(node);
			}

			// and mark the node as finished
			// TODO: due to neg.costs nodes shall be optimized by shorter paths?
			closedNodes.add(node);
		}
	}

	private void expandNodeSingleThreaded(Node node) {
		for (StateTransition st : successors) {
			if (canApplyTransition(node, st)) {
				Node newFollower = applyTransition(node, st);
				installNode(newFollower);
				stepCompleted(newFollower);
			}
		}
	}

	private void expandNodeMultiThreaded(Node node) {
		// we split the search into two main tasks:
		// 1) expand all nodes in many threads
		// 2) install every expanded node as it is expanded

		// first expand all nodes asynchronously
		// using our iterable executor
		IterableExecutor<Node> exec = IterableExecutor.createExecutor();
		for (StateTransition st : successors) {
			if (canApplyTransition(node, st)) {
				exec.submit(new NodeExpander(node, st));
			}
		}

		// then iterate through all expanded nodes
		// and install them as they come in (synchronous install)
		// this does not really matter, because installing is very quick
		// and is done while the thread pool is still expanding
		try {
			for (Future<Node> future : exec) {
				Node newFollower = future.get();
				installNode(newFollower);
				stepCompleted(newFollower);
			}
		}
		catch (InterruptedException e) {
			// re-throw exception of asynchronous thread
			throw new IllegalStateException(e);
		}
		catch (ExecutionException e) {
			// re-throw exception of asynchronous thread
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}
			throw new IllegalStateException(cause);
		}
	}

	/**
	 * Notifies that a search step has been completed.
	 * 
	 * @created 11.09.2011
	 * @param node the node expanded by the current step
	 */
	private void stepCompleted(Node node) {
		try {
			steps++;
			algorithm.getAbortStrategy().nextStep(node.getPath(), session);
		}
		catch (AbortException e) {
			// record that abort is requested, but do noting special
			model.abort();
		}
	}

	/**
	 * Returns if a state transition can be used to create following up nodes
	 * for a specific node.
	 * 
	 * @created 09.09.2011
	 * @param node the node to apply the state transition to
	 * @param stateTransition the state transition to be applied
	 * @return
	 */
	private boolean canApplyTransition(Node node, StateTransition stateTransition) {
		Session actualSession = node.getSession();
		QContainer qcontainer = stateTransition.getQcontainer();
		// do not repeat qcontainers in a row
		if (node.getPath().getQContainer() == qcontainer) {
			return false;
		}
		// only use qcontainers that are not excluded
		if (actualSession.getBlackboard().getIndication(qcontainer).isContraIndicated()) {
			return false;
		}
		try {
			// check if the precondition does hold
			return stateTransition.getActivationCondition() == null
					|| stateTransition.getActivationCondition().eval(actualSession);
		}
		catch (NoAnswerException e) {
			// does not apply because of missing values
			return false;
		}
		catch (UnknownAnswerException e) {
			// does not apply because of unknown values
			return false;
		}
	}

	private void installNode(Node newFollower) {
		synchronized (this) {
			Node follower = nodes.get(newFollower.getState());
			if (follower == null) {
				// store the new one, because it does not exist
				nodes.put(newFollower.getState(), newFollower);
				openNodes.add(newFollower);
				// System.out.println("\tnode added");
			}
			else if (follower.getPath().getCosts() > newFollower.getPath().getCosts()) {
				// update existing node for the state
				// for open nodes remove and add again to preserve ordering
				boolean hasOpenNode = openNodes.remove(follower);
				// due to negative costs it might be possible
				// that the found node is still closed
				// in this case we might in need to update all existing nodes
				// TODO: handle negative costs more sophisticated?
				if (!hasOpenNode) {
					AStarPath oldPath = follower.getPath();
					AStarPath newPath = newFollower.getPath();
					replacePaths(oldPath, newPath);
				}
				// proceed with usual update
				follower.updatePath(newFollower.getPath());
				follower.setfValue(newFollower.getfValue());
				if (hasOpenNode) openNodes.add(follower);
			}
		}
		updateTargets(newFollower.getPath());
	}

	private void replacePaths(AStarPath oldPath, AStarPath newPath) {
		// update all successors based on the old
		// path to be based on the new path for now
		System.out.println("A* violation due to negative costs, re-checking affected nodes");
		for (Node checked : this.openNodes) {
			replaceNodePath(checked, oldPath, newPath);
		}
		for (Node checked : this.closedNodes) {
			replaceNodePath(checked, oldPath, newPath);
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
		if (!hasPrefix) return;

		// if yes, we create the prefix-replaced path
		// and update the calculated costs of the targets
		AStarPath createdPath = checked.getPath().replacePrefix(oldPath, newPath);
		double f = calculateFValue(createdPath.getCosts(), checked.getState(), checked.getSession());
		checked.updatePath(createdPath);
		checked.setfValue(f);
		updateTargets(createdPath);
	}

	private Node applyTransition(Node node, StateTransition stateTransition) {
		Session actualSession = node.getSession();
		QContainer qcontainer = stateTransition.getQcontainer();
		Session copiedSession = Util.copyCase(actualSession);
		double costs = costFunction.getCosts(qcontainer, copiedSession);
		Util.setNormalValues(copiedSession, qcontainer, this);
		stateTransition.fire(copiedSession);
		State newState = computeState(copiedSession);
		AStarPath newPath = new AStarPath(qcontainer, node.getPath(), costs);
		double f = calculateFValue(newPath.getCosts(), newState, copiedSession);
		Node newFollower = new Node(newState, copiedSession, newPath, f);
		return newFollower;
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
		// TODO: subtract all negative costs to preserve optimistic heuristic?
		return min;
	}

}
