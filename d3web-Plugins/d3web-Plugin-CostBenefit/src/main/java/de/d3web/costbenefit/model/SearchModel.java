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

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.denkbares.utils.Stopwatch;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.session.Session;
import de.d3web.costbenefit.blackboard.CopiedSession;
import de.d3web.costbenefit.inference.BlockingReason;
import de.d3web.costbenefit.inference.CostBenefitProperties;
import de.d3web.costbenefit.inference.CostFunction;
import de.d3web.costbenefit.inference.DefaultCostFunction;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.inference.SupportiveStateTransitions;

/**
 * This model provides all functions on targets, nodes and paths for the search algorithms. It represents the actual
 * state of a search.
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
@SuppressWarnings("JavadocDeclaration")
public class SearchModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchModel.class);

	private final NavigableSet<Target> targets = new TreeSet<>(new TargetComparator());
	private final Map<Target, BlockingReason> blockedTargets = new TreeMap<>(new TargetComparator());

	private Target bestBenefitTarget;
	private Target bestCostBenefitTarget;
	private final CostFunction costFunction;
	private final Session session;

	private final Map<QContainer, BlockingReason> blockedQContainers;
	private final Set<StateTransition> applicableStateTransitions;
	private Set<StateTransition> usefulStateTransitions = null;

	private boolean aborted = false;
	private long calculationTime = -1;
	private int calculationSteps = -1;

	public SearchModel(Session session) {
		this.session = session;
		Session session2 = session instanceof CopiedSession ? ((CopiedSession) session).getRootSession() : session;
		PSMethodCostBenefit solver = session2.getPSMethodInstance(PSMethodCostBenefit.class);
		if (solver != null) {
			costFunction = solver.getCostFunction();
			costFunction.init(session.getKnowledgeBase());
		}
		else {
			costFunction = new DefaultCostFunction();
			LOGGER.warn("No Costbenefit-PSMethod included in the session, using default cost function.");
		}
		blockedQContainers = PSMethodCostBenefit.getBlockedQContainers(session);

		// filter StateTransitions that cannot be applied due to final questions
		applicableStateTransitions = new HashSet<>();
		for (StateTransition st : StateTransition.getAll(session)) {
			// skip blocked and target-only containers, as their transitions are not taken into consideration
			QContainer container = st.getQContainer();
			if (blockedQContainers.containsKey(container)) continue;
			if (CostBenefitProperties.isTargetOnly(container)) continue;

			// add all remaining state transitions
			applicableStateTransitions.add(st);
		}
	}

	@Override
	@SuppressWarnings("MethodDoesntCallSuperMethod")
	public SearchModel clone() {
		SearchModel copy = new SearchModel(session);
		copy.bestBenefitTarget = this.bestBenefitTarget == null ? null : bestBenefitTarget.clone();
		copy.bestCostBenefitTarget = this.bestCostBenefitTarget == null ? null : bestCostBenefitTarget.clone();
		for (Target target : targets) {
			copy.addTarget(target.clone());
		}
		return copy;
	}

	/**
	 * Merges the information from the specified  into this SearchModel. The specified one is left
	 * untouched. This object is altered and contains the merged results of the two searches.
	 *
	 * @param other the search model to merge into this object
	 * @created 01.09.2011
	 */
	public void merge(SearchModel other) {
		// do all the checks inside this loop
		// to avoid multiple clones for the same target
		for (Target original : other.targets) {
			// create one clone target
			Target copy = original.clone();
			// check for best benefit one
			if (original == other.bestBenefitTarget) {
				checkTarget(copy);
			}
			// check for best cost benefit one
			// (else, because we have no need to check it twice)
			else if (original == other.bestCostBenefitTarget) {
				checkTarget(copy);
			}
			// and replace the one in the targets list
			// if the copied one is better
			Target existing = getExistingTarget(copy);
			if (existing != null && existing.getCosts() > copy.getCosts()) {
				targets.remove(existing);
				targets.add(copy);
			}
		}
	}

	/**
	 * Return the existing target that equals to the specified one. Unfortunately the Set have no such function to
	 * access the existing one.
	 */
	private Target getExistingTarget(Target blueprint) {
		if (targets.contains(blueprint)) {
			for (Target target : targets) {
				if (target.equals(blueprint)) return target;
			}
		}
		return null;
	}

	/**
	 * Adds a new target
	 */
	public void addTarget(Target target) {
		if (targets.add(target)) {
			// if the targets are modified, the state transitions have been newly calculated
			usefulStateTransitions = null;
		}
	}

	/**
	 * Blocks a target for the path calculation. The target is removed from the targets set, and moved to the blocked
	 * targets accordingly.
	 *
	 * @param target the target to be blocked
	 * @param reason some textual reason why this target is blocked (for debug purposes)
	 */
	public void blockTarget(Target target, BlockingReason reason) {
		if (targets.remove(target)) {
			blockedTargets.put(target, reason);
			// update the best benefit target, if that one is removed
			if (Objects.equals(bestBenefitTarget, target)) {
				bestBenefitTarget = targets.isEmpty() ? null : targets.first();
			}
			// update the best cost/benefit target, if that one is removed
			// (should not happen is this method is not called during searching)
			if (Objects.equals(bestCostBenefitTarget, target)) {
				bestCostBenefitTarget = null;
				targets.forEach(this::checkBestCostBenefitTarget);
			}
			// if the targets are modified, the state transitions have been newly calculated
			usefulStateTransitions = null;
		}
	}

	/**
	 * This method signals that a target has been reached by a new or improved path. If updates the results of this
	 * search model if the new or improved path optimizes them.
	 *
	 * @param target the target that has been reached
	 * @created 18.09.2011
	 */
	public void checkTarget(Target target) {
		checkBestBenefitTarget(target);
		checkBestCostBenefitTarget(target);
	}

	private void checkBestBenefitTarget(Target target) {
		if (bestBenefitTarget == null || target.getBenefit() > bestBenefitTarget.getBenefit()) {
			bestBenefitTarget = target;
		}
	}

	private void checkBestCostBenefitTarget(Target target) {
		// only check for best cost/benefit if the target has been reached yet
		if (target.getMinPath() != null) {
			if (bestCostBenefitTarget == null
					|| target.getCostBenefit() < bestCostBenefitTarget.getCostBenefit()) {
				bestCostBenefitTarget = target;
			}
		}
	}

	/**
	 * Maximizes the benefit of a given target.
	 *
	 * @param target  the target to maximize the benefit for
	 * @param benefit the new benefit to maximize with the already defined benefit
	 */
	public void maximizeBenefit(Target target, double benefit) {
		if (target.getBenefit() < benefit) {
			targets.remove(target);
			target.setBenefit(benefit);
			targets.add(target);
			checkTarget(target);
		}
	}

	/**
	 * @return the Target with the best CostBenefit
	 */
	@Nullable
	public Target getBestCostBenefitTarget() {
		return bestCostBenefitTarget;
	}

	/**
	 * @return the Target with the best CostBenefit
	 */
	@Nullable
	public Path getBestCostBenefitPath() {
		return bestCostBenefitTarget == null ? null : bestCostBenefitTarget.getMinPath();
	}

	/**
	 * Returns the benefit of the bestBenefitTarget The best benefit not necessarily is the benefit used for the best
	 * cost/benefit relation.
	 */
	public double getBestBenefit() {
		if (bestBenefitTarget == null) return 0f;
		return bestBenefitTarget.getBenefit();
	}

	/**
	 * Returns the currently used targets for path calculation.
	 */
	public NavigableSet<Target> getTargets() {
		return targets;
	}

	/**
	 * Returns the targets that have benefit, but are blocked for other reasons (usually because any of its QContainers
	 * are identified to be not reachable at all, e.g. some indicator state is violated, or the heuristic tells that the
	 * costs are infinite).
	 */
	public Set<Target> getBlockedTargets() {
		return blockedTargets.keySet();
	}

	/**
	 * Returns a reason (message) why the specified target has been blocked, or null if the target is not blocked at
	 * all.
	 */
	public BlockingReason getBlockingMessage(Target target) {
		return blockedTargets.get(target);
	}

	/**
	 * Returns the CostBenefit of the BestCostBenefitTarget
	 */
	public double getBestCostBenefit() {
		if (bestCostBenefitTarget == null) {
			return Float.MAX_VALUE;
		}
		return bestCostBenefitTarget.getCostBenefit();
	}

	/**
	 * Returns the best unreached benefit. This can be used to calculate the best reachable CostBenefit.
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
	 * Checks if this model has at least one target.
	 */
	public boolean hasTargets() {
		return !targets.isEmpty();
	}

	/**
	 * Returns the currently used CostFunction.
	 */
	public CostFunction getCostFunction() {
		return costFunction;
	}

	/**
	 * Returns if at least one target is reached.
	 */
	public boolean isAnyTargetReached() {
		return (bestCostBenefitTarget != null);
	}

	public Session getSession() {
		return session;
	}

	public Map<QContainer, BlockingReason> getBlockedQContainers() {
		return blockedQContainers;
	}

	/**
	 * Returns a set of all available state transitions of the knowledge base that are allowed to be used to established
	 * required precondition states. These are all transitions, that are neither currently blocked, nor are transitions
	 * of target-only test steps.
	 *
	 * @return the state transitions currently usable for establishing precondition states
	 */
	@NotNull
	public Set<StateTransition> getTransitionalStateTransitions() {
		return applicableStateTransitions;
	}

	/**
	 * Returns a set of all available state transitions of the knowledge base that are allowed to be used to established
	 * required precondition states, AND are helpful to reach any of the targets' preconditions. These are all
	 * transitions, that are neither currently blocked, nor are transitions of target-only test steps, and also
	 * establish any of targets' preconditions, or any precondition of a transition, that is itself helpful to reach any
	 * target (transitive hull of 'helpful' transitions).
	 *
	 * @return the state transitions currently usable AND helpful for establishing precondition states of the targets
	 */
	@NotNull
	public Set<StateTransition> getTransitionalStateTransitionsForTargets() {
		// restrict the active transitions to the transitive hull of those, which derives any required state question
		if (usefulStateTransitions == null) {
			var stopwatch = new Stopwatch().start();
			usefulStateTransitions = new SupportiveStateTransitions(applicableStateTransitions, targets).getSupportiveHull();
			if (stopwatch.pause().getTime() > 5) {
				stopwatch.log(LOGGER, "calculate path on " + usefulStateTransitions.size() + " of " +
						applicableStateTransitions.size() + " transitional test steps");
			}
		}
		return usefulStateTransitions;
	}

	/**
	 * Calculates all unblocked state transitions having a {@link QContainer} being part of an actual target
	 *
	 * @return collection of unblocked target StateTransitions
	 * @created 28.11.2012
	 */
	@NotNull
	public Set<StateTransition> getTargetStateTransitions() {
		HashSet<StateTransition> result = new HashSet<>();
		for (Target t : targets) {
			for (QContainer qcon : t.getQContainers()) {
				if (blockedQContainers.containsKey(qcon)) continue;
				StateTransition stateTransition = StateTransition.getStateTransition(qcon);
				if (stateTransition != null) {
					result.add(stateTransition);
				}
			}
		}
		return result;
	}

	/**
	 * Tells the search algorithm whether to abort the search or not. If the search is already aborted, this method
	 * should do nothing. Please note that each search algorithm may set this flag if its abort strategy tells him to
	 * do.
	 *
	 * @param abort if the search is / shall be aborted
	 * @created 15.09.2011
	 */
	public void setAbort(boolean abort) {
		this.aborted = abort;
	}

	/**
	 * Check if the search has been aborted (externally or by the abort strategy of the search).
	 *
	 * @return if the search has been aborted
	 * @created 14.09.2011
	 */
	public boolean isAborted() {
		return aborted;
	}

	/**
	 * Gets the duration how long the calculation has been processed until it has been stopped or aborted.
	 *
	 * @return the recent calculation time in milliseconds
	 */
	public long getCalculationTime() {
		return calculationTime;
	}

	/**
	 * Sets the duration how long the calculation has been processed until it has been stopped or aborted.
	 *
	 * @param calculationTime the duration to be set in milliseconds
	 */
	public void setCalculationTime(long calculationTime) {
		this.calculationTime = calculationTime;
	}

	/**
	 * Gets the number of path steps the calculation has been processed until it has been stopped or aborted.
	 *
	 * @return the recent calculation time in milliseconds
	 */
	public int getCalculationSteps() {
		return calculationSteps;
	}

	/**
	 * Sets the number of path steps the calculation has been processed until it has been stopped or aborted.
	 *
	 * @param calculationSteps the duration to be set in milliseconds
	 */
	public void setCalculationSteps(int calculationSteps) {
		this.calculationSteps = calculationSteps;
	}

	private static class TargetComparator implements Comparator<Target> {
		@Override
		public int compare(Target o1, Target o2) {
			return o2.compareTo(o1);
		}
	}
}
