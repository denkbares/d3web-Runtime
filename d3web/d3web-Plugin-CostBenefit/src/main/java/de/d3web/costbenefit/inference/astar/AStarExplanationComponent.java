/*
 * Copyright (C) 2012 denkbares GmbH
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

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.Conditions;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.costbenefit.Util;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;
import de.d3web.costbenefit.inference.PathExtender;
import de.d3web.costbenefit.inference.SearchAlgorithm;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.inference.ValueTransition;
import de.d3web.costbenefit.model.Path;
import de.d3web.costbenefit.model.SearchModel;
import de.d3web.costbenefit.model.Target;
import de.d3web.indication.inference.PSMethodUserSelected;

/**
 * This class can be used to explain the result of an AStar calculation.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 03.07.2012
 */
public class AStarExplanationComponent {

	private final AStar astar;

	public AStarExplanationComponent(AStar astar) {
		this.astar = astar;
	}

	/**
	 * Returns the longest subpath of the specified path reached during the
	 * AStar calculation
	 * 
	 * @created 03.07.2012
	 * @param path specified path
	 * @return subpath of the specified path
	 */
	public AStarPath getLongestSubPathReached(QContainer... path) {
		AStarPath subPath = new AStarPath(null, null, 0);
		Collection<Node> nodes = new LinkedList<Node>(astar.getClosedNodes());
		nodes.addAll(astar.getOpenNodes());
		for (Node n : nodes) {
			List<QContainer> actualPath = n.getPath().getPath();
			if (actualPath.size() > subPath.getPath().size() && actualPath.size() <= path.length) {
				int i = 0;
				boolean isSubPath = true;
				for (QContainer qcon : actualPath) {
					if (qcon != path[i++]) {
						isSubPath = false;
						break;
					}
				}
				if (isSubPath) {
					subPath = n.getPath();
				}
			}
		}
		return subPath;
	}

	/**
	 * Returns the path of the last calculation, having reached most of the
	 * specified QContainers. If two pathes have reached the same amount of
	 * QContainers, the one with the better f Value is returned.
	 * 
	 * @created 03.07.2012
	 * @param path specified QContainers
	 * @return best AStarPath fitting to the defined criterias
	 */
	public AnalysisResult getBestPathContaining(QContainer... path) {
		Node bestNode = null;
		int reached = -1;
		Collection<Node> nodes = new LinkedList<Node>(astar.getClosedNodes());
		nodes.addAll(astar.getOpenNodes());
		for (Node n : nodes) {
			int tempReached = 0;
			for (QContainer qcon : path) {
				if (n.getPath().contains(qcon)) {
					tempReached++;
				}
			}
			if (reached < tempReached) {
				reached = tempReached;
				bestNode = n;
			}
			else if (reached == tempReached) {
				if (bestNode.getfValue() > n.getfValue()) {
					bestNode = n;
				}
			}
		}
		if (bestNode == null) {
			return new AnalysisResult(new AStarPath(null, null, 0), 0, true);
		}
		boolean closed = astar.getClosedNodes().contains(bestNode);
		return new AnalysisResult(bestNode.getPath(), bestNode.getfValue(), closed);
	}

	/**
	 * Returns the f value of the start state of the calculation
	 * 
	 * @created 05.07.2012
	 * @return start f value
	 */
	public double getPredictedPathCostsOnCalculationStart() {
		for (Node n : astar.getClosedNodes()) {
			if (n.getPath().getPath().isEmpty()) {
				return n.getfValue();
			}
		}
		return Double.NaN;
	}

	/**
	 * Returns the path which would have been expanded next, if the calculation
	 * wouldn't have finished/aborted
	 * 
	 * @created 05.07.2012
	 * @return best unexpanded path after calculation
	 */
	public AnalysisResult getBestPathAtCalculationEnd() {
		Node peek = astar.getOpenNodes().peek();
		return new AnalysisResult(peek.getPath(), peek.getfValue(), false);
	}

	/**
	 * Represents a Result of an Analysis containing the path, the f Value and a
	 * boolean, if the path is closed (it's successors have been expanded)
	 * 
	 * @author Markus Friedrich (denkbares GmbH)
	 * @created 04.07.2012
	 */
	public static class AnalysisResult {

		private final AStarPath path;
		private final double fValue;
		private final boolean closed;

		public AnalysisResult(AStarPath path, double fValue, boolean closed) {
			super();
			this.path = path;
			this.fValue = fValue;
			this.closed = closed;
		}

		public AStarPath getPath() {
			return path;
		}

		public double getfValue() {
			return fValue;
		}

		public boolean isClosed() {
			return closed;
		}

		@Override
		public String toString() {
			return path.toString() + ", f-Value: " + fValue + ", closed: " + closed;
		}
	}

	/**
	 * Calculates all conditions that must be fullfilled to be enable to execute
	 * the target and a set of QContainers per target, enabling the condition.
	 * 
	 * @created 04.07.2012
	 * @param session actual session
	 * @param target specified target
	 * @return a map containing the calculated conditions and its preparing
	 *         QContainers
	 */
	public static Map<Condition, Set<QContainer>> findVariations(Session session, Condition transitiveCondition) {
		List<Condition> primitiveTransitiveConditions = TPHeuristic.getPrimitiveConditions(transitiveCondition);
		KnowledgeBase kb = session.getKnowledgeBase();
		Collection<StateTransition> stateTransitions = new LinkedList<StateTransition>();
		// filter StateTransitions that cannot be applied due to final questions
		Set<QContainer> blockedQContainers = PSMethodCostBenefit.getBlockedQContainers(session);
		for (StateTransition st : kb.getAllKnowledgeSlicesFor(StateTransition.KNOWLEDGE_KIND)) {
			QContainer qcontainer = st.getQcontainer();
			Boolean targetOnly = qcontainer.getInfoStore().getValue(AStar.TARGET_ONLY);
			if (!targetOnly && !blockedQContainers.contains(qcontainer)) {
				stateTransitions.add(st);
			}
		}
		Map<Condition, Set<QContainer>> result = new HashMap<Condition, Set<QContainer>>();
		for (Condition condition : primitiveTransitiveConditions) {
			if (!Conditions.isTrue(condition, session)) {
				Set<QContainer> transitionalQContainer = new HashSet<QContainer>();
				for (StateTransition st : stateTransitions) {
					for (ValueTransition vt : st.getPostTransitions()) {
						if (vt.getQuestion() == condition.getTerminalObjects().iterator().next()) {
							for (Value v : vt.calculatePossibleValues()) {
								if (TPHeuristic.checkValue(condition, v)) {
									transitionalQContainer.add(st.getQcontainer());
									break;
								}
							}
						}
					}
				}
				result.put(condition, transitionalQContainer);
			}
		}
		return result;
	}

	/**
	 * Calculates all QContainers of the path to the last calculated target, not
	 * needed to establish a transitive precondition of the chosen target
	 * 
	 * @created 05.07.2012
	 * @return list of unexpected QContainers
	 */
	public Set<QContainer> getUnexpectedQContainers() {
		SearchModel model = astar.getModel();
		Target target = model.getBestCostBenefitTarget();
		if (target == null) {
			return Collections.emptySet();
		}
		if (target.getQContainers().size() != 1) {
			throw new IllegalArgumentException("this method only works with single targets.");
		}
		StateTransition stateTransition = StateTransition.getStateTransition(target.getQContainers().get(
				0));
		if (stateTransition == null) {
			return Collections.emptySet();
		}
		Condition transitiveCondition = getAndInitTPHeuristic(model).getTransitiveCondition(
				new AStarPath(null, null, 0),
				stateTransition);

		List<QContainer> path = new LinkedList<QContainer>(target.getMinPath().getPath());
		path.removeAll(target.getQContainers());
		return getUnexpectedQContainers(path, model.getSession(), transitiveCondition);
	}

	/**
	 * Calculates all QContainers of the path, not needed to establish a
	 * transitive precondition
	 * 
	 * @created 05.07.2012
	 * @param path specified Path
	 * @param session actual session
	 * @param transitiveCondition transitive activation condition of the target
	 * @return list of unexpected QContainers
	 */
	public static Set<QContainer> getUnexpectedQContainers(List<QContainer> path, Session session, Condition transitiveCondition) {
		Set<QContainer> result = new HashSet<QContainer>(path);
		Map<Condition, Set<QContainer>> variations = findVariations(session, transitiveCondition);
		for (Set<QContainer> qcons : variations.values()) {
			result.removeAll(qcons);
		}
		return result;
	}

	private static TPHeuristic getAndInitTPHeuristic(SearchModel model) {
		Session session = model.getSession();
		PSMethodCostBenefit cb = session.getPSMethodInstance(PSMethodCostBenefit.class);
		SearchAlgorithm searchAlgorithm = cb.getSearchAlgorithm();
		if (searchAlgorithm instanceof PathExtender) {
			searchAlgorithm = ((PathExtender) searchAlgorithm).getSubalgorithm();
		}
		if (searchAlgorithm instanceof AStarAlgorithm) {
			Heuristic heuristic = ((AStarAlgorithm) searchAlgorithm).getHeuristic();
			if (heuristic instanceof TPHeuristic) {
				heuristic.init(model);
				return (TPHeuristic) heuristic;
			}
		}
		// if another heuristic is used, create and init a new one
		TPHeuristic tpHeuristic = new TPHeuristic();
		tpHeuristic.init(model);
		return tpHeuristic;
	}

	/**
	 * Returns the path of the best cost benefit target or null if no path could
	 * be found
	 * 
	 * @created 05.07.2012
	 * @return path to target
	 */
	public Path getBestCostBenefit() {
		Target bestCostBenefitTarget = astar.getModel().getBestCostBenefitTarget();
		if (bestCostBenefitTarget == null) {
			return null;
		}
		return bestCostBenefitTarget.getMinPath();
	}

	/**
	 * Calculates the primitive conditions, which are not fullfilled after or
	 * while the specified path is processed
	 * 
	 * @created 05.07.2012
	 * @param path specified path
	 * @param transitiveCondition Condition to be fullfilled
	 * @param session actual session
	 * @return a set of unfullfilled (sub) conditions
	 */
	public static Set<Condition> getUnfullfilledConditions(Path path, Condition transitiveCondition, Session session) {
		Set<Condition> result = new HashSet<Condition>(
				TPHeuristic.getPrimitiveConditions(transitiveCondition));
		Set<Condition> fullfilledConditions = new HashSet<Condition>();
		Session copiedSession = Util.createSearchCopy(session);
		addFullfilledConditions(result, copiedSession, fullfilledConditions);
		for (QContainer qcon : path.getPath()) {
			StateTransition stateTransition = StateTransition.getStateTransition(qcon);
			if (stateTransition != null) {
				Util.setNormalValues(copiedSession, qcon, PSMethodUserSelected.getInstance());
				stateTransition.fire(copiedSession);
				addFullfilledConditions(result, copiedSession, fullfilledConditions);
			}
		}
		result.removeAll(fullfilledConditions);
		return result;
	}

	private static void addFullfilledConditions(Collection<Condition> conditions, Session session, Collection<Condition> fullfilledConditions) {
		for (Condition condition : conditions) {
			if (Conditions.isTrue(condition, session)) {
				fullfilledConditions.add(condition);
			}
		}
	}
}
