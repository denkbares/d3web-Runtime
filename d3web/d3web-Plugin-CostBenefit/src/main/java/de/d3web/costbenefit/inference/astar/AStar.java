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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	private final SearchModel model;
	private final Set<Question> stateQuestions = new HashSet<Question>();
	private final LinkedList<Node> openNodes = new LinkedList<Node>();
	private final List<Node> closedNodes = new LinkedList<Node>();
	private final Map<State, Node> nodes = new HashMap<State, Node>();
	private final Heuristic heuristic;
	private final Collection<StateTransition> successors;
	private final CostFunction costFunction;
	private final AbortStrategy abortStrategy;
	private final Session session;

	public AStar(Session session, SearchModel model, Heuristic heuristic, AbortStrategy abortStrategy) {
		this.session = session;
		this.model = model;
		this.heuristic = heuristic;
		if (abortStrategy != null) {
			this.abortStrategy = abortStrategy;
		}
		else {
			// 4000 opennodes should be possible with 2gb heapspace
			this.abortStrategy = new DefaultAbortStrategy(50, 1);
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
				updateTargets(qcon, new AStarPath(qcon, null, costFunction.getCosts(qcon, session)));
			}
		}
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
		abortStrategy.init(model);
		try {
			while (!openNodes.isEmpty()) {

				Collections.sort(openNodes);
				Node node = openNodes.pollFirst();
				abortStrategy.nextStep(node.getPath(), session);
				// if a target has been reached and its cost/benefit is better
				// than
				// the optimistic fValue of the best node, terminate the
				// algorithm
				if (model.getBestCostBenefitTarget() != null
						&& model.getBestCostBenefitTarget().getCostBenefit() < node.getfValue()) {
					if (abortStrategy instanceof DefaultAbortStrategy) {
						System.out.println("Calculation done, nodes expanded: "
								+ session.getSessionObject((DefaultAbortStrategy) abortStrategy).getSteps());
					}
					return;
				}
				expandNode(node);
				closedNodes.add(node);
			}
		}
		catch (AbortException e) {
			// nothing to do
			System.out.println("Calculation aborted by AbortStrategy.");
			return;
		}
		if (abortStrategy instanceof DefaultAbortStrategy) {
			System.out.println("Calculation done, all nodes expanded: "
					+ session.getSessionObject((DefaultAbortStrategy) abortStrategy).getSteps());
		}
	}

	private void expandNode(Node node) {
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
					Node follower = nodes.get(newState);
					AStarPath newPath = new AStarPath(qcontainer,
							node.getPath(), costs);
					if (follower == null) {
						double f = calculateFValue(newPath.getCosts(), newState, copiedSession);
						follower = new Node(newState, copiedSession, newPath, f);
						nodes.put(newState, follower);
						openNodes.add(follower);
					}
					else if (follower.getPath().getCosts() > newPath.getCosts()) {
						follower.updatePath(newPath);
						double f = calculateFValue(newPath.getCosts(), newState, copiedSession);
						follower.setfValue(f);
					}
					updateTargets(qcontainer, newPath);
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

	private void updateTargets(QContainer qcontainer, AStarPath newPath) {
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
				costs += heuristic.getDistance(state, qContainer, costFunction);
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
