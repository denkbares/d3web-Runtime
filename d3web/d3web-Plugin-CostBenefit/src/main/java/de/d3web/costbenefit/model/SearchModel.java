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
package de.d3web.costbenefit.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.costbenefit.inference.CostFunction;
import de.d3web.costbenefit.inference.DefaultCostFunction;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;

/**
 * This model provides all functions on targets, nodes and paths for the search
 * algorithms. It represents the actual state of a search.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class SearchModel {

	private final Set<Target> targets = new HashSet<Target>();
	private final Map<Node, List<Target>> referencingTargets = new HashMap<Node, List<Target>>();
	private Target bestBenefitTarget;
	private Target bestCostBenefitTarget;
	private final Map<QContainer, Node> map = new HashMap<QContainer, Node>();
	private final Map<Question, List<Value>> expectedValues = new HashMap<Question, List<Value>>();
	private int countMinPaths = 0;
	private CostFunction costFunction;

	public SearchModel(Session session) {
		PSMethod problemsolver = session.getPSMethodInstance(PSMethodCostBenefit.class);
		PSMethodCostBenefit ps = (PSMethodCostBenefit) problemsolver;
		if (ps != null) {
			costFunction = ps.getCostFunction();
		}
		else {
			costFunction = new DefaultCostFunction();
			Logger.getLogger(this.getClass().getName()).throwing(
					this.getClass().getName(),
					"Kein Costbenefit-Problemlöser im Fall. Es wird die Standartkostenfunktion verwendet.",
					null);
		}
		for (QContainer qcon : session.getKnowledgeBase().getQContainers()) {
			Node containerNode = new Node(qcon, this);
			map.put(qcon, containerNode);
			Map<Question, Value> expected = containerNode.getExpectedValues(session);
			for (Entry<Question, Value> entry : expected.entrySet()) {
				Value answer = entry.getValue();
				List<Value> answers = new LinkedList<Value>();
				answers.add(answer);
				// for (Object a: answers) {
				// if (a instanceof Answer) answersCast.add((Answer) a);
				// }
				expectedValues.put(entry.getKey(), answers);
			}
		}
	}

	public Node getQContainerNode(QContainer qcon) {
		return map.get(qcon);
	}

	/**
	 * Adds a new target
	 * 
	 * @param target
	 */
	public void addTarget(Target target) {
		targets.add(target);
		for (QContainer qcon : target) {
			Node key = getQContainerNode(qcon);
			List<Target> refs = this.referencingTargets.get(key);
			if (refs == null) {
				refs = new LinkedList<Target>();
				this.referencingTargets.put(key, refs);
			}
			refs.add(target);
		}
	}

	/**
	 * Minimizes if necessary the path in all targets which are reached
	 * 
	 * @param path
	 */
	public void minimizePath(Path path) {
		Node node = path.getLastNode();
		List<Target> theTargets = this.referencingTargets.get(node);
		for (Target t : theTargets) {
			if (t.isReached(path)) {
				if (t.getMinPath() == null) countMinPaths++;
				if (t.getMinPath() == null || t.getMinPath().getCosts() > path.getCosts()) {
					t.setMinPath(path.copy());
					checkTarget(t);
				}
			}
		}
	}

	private void checkTarget(Target t) {
		if (bestBenefitTarget == null || t.getBenefit() > bestBenefitTarget.getBenefit()) {
			bestBenefitTarget = t;
		}
		// only check for best cost/benefit it the target has been reached yet
		if (t.getMinPath() != null) {
			if (bestCostBenefitTarget == null
					|| t.getCostBenefit() < bestCostBenefitTarget.getCostBenefit()) {
				bestCostBenefitTarget = t;
			}
		}
	}

	/**
	 * Maximizes the benefit of a target
	 * 
	 * @param t
	 * @param benefit
	 */
	public void maximizeBenefit(Target t, double benefit) {
		if (t.getBenefit() < benefit) {
			t.setBenefit(benefit);
			checkTarget(t);
		}
	}

	/**
	 * @return the Target with the best CostBenefit
	 */
	public Target getBestCostBenefitTarget() {
		return bestCostBenefitTarget;
	}

	/**
	 * Returns the benefit of the bestBenefitTarget The best benefit not
	 * necessarily is the benefit used for the best cost/benefit relation
	 * 
	 * @return
	 */
	public double getBestBenefit() {
		if (bestBenefitTarget == null) return 0f;
		return bestBenefitTarget.getBenefit();
	}

	public Set<Target> getTargets() {
		return targets;
	}

	/**
	 * Returns all nodes of the graph to be searched.
	 * 
	 * @return
	 */
	public Set<Node> getNodes() {
		Set<Node> nodeList = new HashSet<Node>();
		for (Node node : map.values()) {
			if (node.getStateTransition() != null) nodeList.add(node);
		}
		return nodeList;
	}

	/**
	 * Checks if all targets are reached. If this is true, every target has a
	 * minPath.
	 * 
	 * @return
	 */
	public boolean allTargetsReached() {
		return (countMinPaths == targets.size());
	}

	/**
	 * Returns the CostBenefit of the BestCostBenefitTarget
	 * 
	 * @return
	 */
	public double getBestCostBenefit() {
		if (bestCostBenefitTarget == null) {
			return Float.MAX_VALUE;
		}
		return bestCostBenefitTarget.getCostBenefit();
	}

	/**
	 * Returns the best unreached benefit. This can be used to calculate the
	 * best reachable CostBenefit.
	 * 
	 * @return
	 */
	public double getBestUnreachedBenefit() {
		double benefit = 0;
		for (Target t : targets) {
			if (t.getMinPath() == null) {
				benefit = Math.max(benefit, t.getBenefit());
			}
		}
		return benefit;
	}

	/**
	 * Checks if the actual path reaches at least one target.
	 * 
	 * @param actual
	 * @return
	 */
	public boolean isTarget(Path actual) {
		if (actual.isEmpty()) return false;
		for (Target t : targets) {
			if (t.isReached(actual)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns all Nodes contained in combined Targets (targets with more than
	 * one target Node)
	 * 
	 * @return
	 */
	public Collection<? extends Node> getCombinedTargetsNodes() {
		List<Node> list = new LinkedList<Node>();
		for (Target t : targets) {
			if (t.size() > 1) {
				for (QContainer qcon : t) {
					list.add(map.get(qcon));
				}
			}
		}
		return list;
	}

	/**
	 * Checks if the Answer a to the Question q was the expected Answer which
	 * has been used during the search. If not, the user has entered an
	 * unexpected answer and the previously calculated sequence has become
	 * invalid.
	 * 
	 * @param q
	 * @param a
	 * @return
	 */
	public boolean isExpectedAnswer(Question q, Choice a) {
		return a.equals(expectedValues.get(q));
	}

	/**
	 * Checks if this model has at least one target
	 * 
	 * @return
	 */
	public boolean hasTargets() {
		return !targets.isEmpty();
	}

	/**
	 * Returns the CostFunction
	 * 
	 * @return
	 */
	public CostFunction getCostFunction() {
		return costFunction;
	}

	/**
	 * Checks if at least one target is reached.
	 * 
	 * @return
	 */
	public boolean oneTargetReached() {
		return (bestCostBenefitTarget != null);
	}
}
