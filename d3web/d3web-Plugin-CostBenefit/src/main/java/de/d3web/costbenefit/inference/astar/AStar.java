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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.utilities.Pair;
import de.d3web.costbenefit.Util;
import de.d3web.costbenefit.inference.AbortException;
import de.d3web.costbenefit.inference.CostFunction;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.model.Path;
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
			installNode(follower);
			return follower;
		}
	}

	private static final Logger log = Logger.getLogger(AStar.class.getName());

	private final SearchModel model;
	private final Map<Question, Value> usedStateQuestions = new LinkedHashMap<Question, Value>();
	private final Queue<Node> openNodes = new PriorityQueue<Node>();
	private final Collection<Node> closedNodes = new LinkedList<Node>();
	private final Map<State, Node> nodes = new HashMap<State, Node>();
	private final AStarAlgorithm algorithm;
	private final Collection<StateTransition> successors;
	private final CostFunction costFunction;
	private final Session session;
	private final Map<Pair<Path, QContainer>, Double> hValueCache = Collections.synchronizedMap(new HashMap<Pair<Path, QContainer>, Double>());

	public static final Property<Boolean> TARGET_ONLY = Property.getProperty(
			"targetOnly", Boolean.class);

	// some information about the current search
	private final transient long initTime;
	private transient int steps = 0;

	public AStar(Session session, SearchModel model, AStarAlgorithm algorithm) {
		long time = System.currentTimeMillis();
		this.algorithm = algorithm;
		this.session = session;
		this.model = model;
		this.costFunction = session.getPSMethodInstance(PSMethodCostBenefit.class).getCostFunction();

		AStarPath emptyPath = new AStarPath(null, null, 0);
		State startState = computeState(session);
		Node start = new Node(startState, session, emptyPath, 0);
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

	private void checkPathFValues(Node targetNode) {
		for (AStarPath pre = targetNode.getPath(); pre != null; pre = pre.getPredecessor()) {
			for (Node node : nodes.values()) {
				if (node.getPath() == pre) {
					if (node.getfValue() > targetNode.getfValue()) {
						log.severe("Heuristic of " + targetNode.getPath()
								+ " was not optimistic, f Value: " + node.getfValue()
								+ ", max: " + targetNode.getfValue() + ", node: "
								+ node.getPath().getQContainer().getName());
					}
					break;
				}
			}
		}
	}

	private State computeState(Session session) {
		return new State(session, usedStateQuestions);
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

		// clean up targets if it is not expected to require too much time
		// and also expect significant speed optimization during calculation
		if (model.getTargets().size() <= 50) {
			removeInfiniteTargets();
		}

		log.info("Starting calculation, #targets: " + model.getTargets().size());
		searchLoop();
		long time2 = System.currentTimeMillis();
		log.info("A* Calculation " + (model.isAborted() ? "aborted" : "done") + " (" +
				"#steps: " + steps + ", " +
				"time: " + (time2 - time1) + "ms, " +
				"init: " + initTime + "ms, " +
				"#open: " + openNodes.size() + ", " +
				"#closed: " + closedNodes.size() + ")");
	}

	private void removeInfiniteTargets() {
		Node startNode = openNodes.iterator().next();
		for (Target target : new ArrayList<Target>(model.getTargets())) {
			Heuristic heuristic = algorithm.getHeuristic();
			QContainer qcontainer = target.getQContainers().get(0);
			double distance =
					heuristic.getDistance(startNode.getPath(), startNode.getState(), qcontainer);
			if (distance == Double.POSITIVE_INFINITY) {
				model.removeTarget(target);
			}
		}
	}

	private void searchLoop() {
		while (!model.isAborted() && !openNodes.isEmpty()) {
			// check for the next open node to be processed
			Node node = openNodes.poll();
			// System.out.println("Expanding: " + node.getPath().getPath() +
			// ", f-Value:"
			// + node.getfValue());
			if (node.getfValue() == Double.POSITIVE_INFINITY) {
				log.info("All targets are unreachable, calculation aborted");
				break;
			}

			// if a target has been reached and its cost/benefit is better than
			// the optimistic fValue of the best node, terminate the algorithm
			if (model.getBestCostBenefitTarget() != null
					&& model.getBestCostBenefitTarget().getCostBenefit() < node.getfValue()) {
				checkPathFValues(node);
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
				// installNode(newFollower);
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
			model.setAbort(true);
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
		// negative QContainer can only be used once in a path
		if (stateTransition.getCosts() < 0.0
				&& node.getPath().contains(qcontainer)) {
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
		updateTargets(newFollower.getPath());
		Boolean targetOnly = newFollower.getPath().getQContainer().getInfoStore().getValue(
				TARGET_ONLY);
		if (targetOnly) {
			// do not add this node to our pathes, it cannot be reused because
			// the last QContainer can not be used to establish preconditions
			return;
		}
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
				// heuristic was not steady, log that the knowledgebase has to
				// be checked
				if (!hasOpenNode) {
					AStarPath oldPath = follower.getPath();
					AStarPath newPath = newFollower.getPath();
					if (oldPath.getCosts() - newPath.getCosts() > 0.00001) {
						log.severe(newPath + " has lower costs than " + oldPath
								+ ", please check that!");
						checkPathFValues(follower);
						checkPathFValues(newFollower);
					}
				}
				// proceed with usual update
				follower.updatePath(newFollower.getPath());
				follower.setfValue(newFollower.getfValue());
				if (hasOpenNode) openNodes.add(follower);
			}
		}
	}

	private Node applyTransition(Node node, StateTransition stateTransition) {
		Session actualSession = node.getSession();
		QContainer qcontainer = stateTransition.getQcontainer();
		Session copiedSession = Util.createDecoratedSession(actualSession);
		double costs = costFunction.getCosts(qcontainer, copiedSession);
		Util.setNormalValues(copiedSession, qcontainer, this);

		List<Fact> facts = stateTransition.fire(copiedSession);
		State newState;
		if (facts.isEmpty()) {
			// if we have not fired any transitions
			// we should reuse the original state
			// instead of creating a new one
			newState = node.getState();
		}
		else {
			// otherwise we first extends our set of
			// used state questions and create a new state
			synchronized (usedStateQuestions) {
				for (Fact fact : facts) {
					TerminologyObject object = fact.getTerminologyObject();
					if (object instanceof Question) {
						Question question = (Question) object;
						if (!usedStateQuestions.containsKey(question)) {
							Value originalValue = session.getBlackboard().getValue(question);
							usedStateQuestions.put(question, originalValue);
						}
					}
				}
				newState = computeState(copiedSession);
			}
		}

		AStarPath newPath = new AStarPath(qcontainer, node.getPath(), costs);
		double f = calculateFValue(newPath, newState, copiedSession);
		Node newFollower = new Node(newState, copiedSession, newPath, f);
		return newFollower;
	}

	private void updateTargets(AStarPath newPath) {
		QContainer qcontainer = newPath.getQContainer();
		for (Target t : model.getTargets()) {
			// update only if the last qcontainer of the path is contained in
			// the target and if the path contains all targetQContainers
			if (t.getQContainers().contains(qcontainer)
					&& (newPath.containsAll(t.getQContainers()))) {
				// this has to be checked because there
				// can be several nodes to reach a
				// target, one of the other nodes could
				// be cheaper
				synchronized (this) {
					if (t.getMinPath() == null
								|| t.getMinPath().getCosts() > newPath.getCosts()) {
						t.setMinPath(newPath);
						model.checkTarget(t);
					}
				}
			}
		}
	}

	private double calculateFValue(Path path, State state, Session session) {
		// to be removed after evaluation

		double min = Double.POSITIVE_INFINITY;
		double pathCosts = path.getCosts();

		targets: for (Target target : model.getTargets()) {
			double targetCosts = 0;
			double benefit = target.getBenefit();
			// we need only to calculate the heuristic
			// if it is capable to minimize the f-Value
			if (pathCosts / benefit >= min) continue;
			for (QContainer qContainer : target.getQContainers()) {
				double costs = pathCosts;
				// adding the costs of the target
				costs += costFunction.getCosts(qContainer, session);
				// we need only to calculate the heuristic
				// if it is capable to minimize the f-Value
				if (costs / benefit >= min) continue targets;
				// trial: also check cache
				AStarPath prePath = ((AStarPath) path).getPredecessor();
				if (prePath != null) {
					Pair<Path, QContainer> key = new Pair<Path, QContainer>(prePath, qContainer);
					Double preHValue = hValueCache.get(key);
					if (preHValue != null) {
						double costs2 = preHValue + prePath.getCosts();
						if (costs2 / benefit >= min) continue targets;
					}
				}
				// adding the costs calculated by the heuristic
				double distance = algorithm.getHeuristic().getDistance(path, state, qContainer);
				costs += distance;
				Pair<Path, QContainer> key = new Pair<Path, QContainer>(path, qContainer);
				hValueCache.put(key, distance);
				targetCosts = Math.max(targetCosts, costs);
			}
			// dividing the whole costs by the benefit
			targetCosts /= benefit;
			min = Math.min(min, targetCosts);
		}

		return min;
	}
}
