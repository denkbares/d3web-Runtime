/*
 * Copyright (C) 2011 denkbares GmbH, Germany
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
package de.d3web.costbenefit.inference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.PropagationManager;
import de.d3web.core.inference.condition.Conditions;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.costbenefit.blackboard.CostBenefitCaseObject;
import de.d3web.costbenefit.model.SearchModel;
import de.d3web.costbenefit.model.Target;
import de.d3web.costbenefit.session.protocol.ManualTargetSelectionEntry;

/**
 * This class provides utility functions to enable an advanced user mode. Within
 * that mode the user is enabled to also see additional Targets with high
 * benefit (regardless to their costs). The user is also enabled to select on of
 * these targets. If the user has selected on the cost benefit strategic solver
 * is in charge to calculate a new path towards this target.
 * 
 * @author volker_belli
 * @created 07.03.2011
 */
public class ExpertMode implements SessionObject {

	private final Session session;

	/**
	 * @use PSMethodCostBenefit.PERMANENTLY_RELEVANT
	 */
	@Deprecated
	public static final Property<Boolean> PERMANENTLY_RELEVANT = PSMethodCostBenefit.PERMANENTLY_RELEVANT;

	private final static SessionObjectSource<ExpertMode> EXPERT_MODE_SOURCE = new SessionObjectSource<ExpertMode>() {

		@Override
		public ExpertMode createSessionObject(Session session) {
			// check if it is allowed to create such an object
			if (getPSMethodCostBenefit(session) == null) {
				throw new IllegalStateException(
						"ExpertMode cannot be used if session does not contain the cost benefit strategic solver");
			}
			return new ExpertMode(session);
		}
	};

	static final Comparator<Target> BENEFIT_COMPARATOR = new Comparator<Target>() {

		@Override
		public int compare(Target target1, Target target2) {
			// sort by the costs of the target (sort descending)
			return (int) Math.signum(target2.getBenefit() - target1.getBenefit());
		}
	};

	private ExpertMode(Session session) {
		this.session = session;
	}

	/**
	 * Returns the ExportMode object for the specified session. The expert mode
	 * can only be received for sessions that uses a cost benefit strategic
	 * solver. Otherwise an IllegalStateException is thrown.
	 * 
	 * @created 07.03.2011
	 * @param session the session to get the expert mode instance for
	 * @return the ExpertMode instance
	 * @throws AbortException if no path towards the target could be found
	 * @throws IllegalStateException if no cost benefit strategic solver is
	 *         available in the specified session
	 */
	public static ExpertMode getExpertMode(Session session) {
		return session.getSessionObject(EXPERT_MODE_SOURCE);
	}

	/**
	 * Returns a list of all alternative targets that can be suggested to the
	 * user. The list is sorted by the targets benefit, regardless to the costs
	 * they may induce.
	 * <p>
	 * Please note that the calculated information contained in the
	 * {@link Target} (namely the minimal path and the benefit of the target),
	 * are based on the latest search of the cost benefit underlying search
	 * algorithm. Due to already answered test steps the minimal path may have
	 * become no longer applicable.
	 * 
	 * @created 07.03.2011
	 * @return the list of all alternative targets sorted by their benefit
	 */
	public List<Target> getAlternativeTargets() {
		PSMethodCostBenefit psm = getPSMethodCostBenefit();
		CostBenefitCaseObject pso = getCostBenefitCaseObject(psm);

		// create a list of all targets
		List<Target> result = new ArrayList<Target>();
		result.addAll(pso.getDiscriminatingTargets());

		// but without the currently selected one
		Target currentTarget = getCurrentTarget();
		result.remove(currentTarget);

		// sort the list regarding to their benefit to become our result
		Collections.sort(result, BENEFIT_COMPARATOR);
		return Collections.unmodifiableList(result);
	}

	private CostBenefitCaseObject getCostBenefitCaseObject(PSMethodCostBenefit psm) {
		return session.getSessionObject(psm);
	}

	private PSMethodCostBenefit getPSMethodCostBenefit() {
		return getPSMethodCostBenefit(session);
	}

	private static PSMethodCostBenefit getPSMethodCostBenefit(Session session) {
		PSMethodCostBenefit psm = session.getPSMethodInstance(PSMethodCostBenefit.class);
		return psm;
	}

	/**
	 * Selects a new target for the interview strategy. This makes the cost
	 * benefit problem solver to arrange a new questionnaire sequence (path) to
	 * cover the selected target. Because it might be complex to find such a
	 * path (covering the target and preparing its preconditions), this
	 * operation may require a long calculation.
	 * <p>
	 * The selected target and the created path are valid until the user answer
	 * some unexpected value. If he does the path becomes invalid and the cost
	 * benefit strategic solver is in charge to create a new path to the
	 * cost/benefit optimal target (based on the new situation). If this
	 * behavior is not desired a new target has to be selected manually
	 * afterwards, using this method again.
	 * <p>
	 * <b>Note:</b><br>
	 * If the search algorithm is not able to provide such a path or if it
	 * implements an abort strategy that prevents the algorithm from finding
	 * such a path this method results in an {@link AbortException}.
	 * 
	 * @created 07.03.2011
	 * @param target the target to select
	 * @throws AbortException if no path towards the target could be found
	 * @see AbortException
	 * @see AbortStrategy
	 * @see SearchAlgorithm
	 * @see Target
	 */
	public void selectTarget(Target target) throws AbortException {
		PSMethodCostBenefit psm = getPSMethodCostBenefit();
		CostBenefitCaseObject pso = getCostBenefitCaseObject(psm);
		PropagationManager propagationManager = session.getPropagationManager();
		try {
			propagationManager.openPropagation();
			session.getProtocol().addEntry(
					new ManualTargetSelectionEntry(propagationManager.getPropagationTime(),
							target.getQContainers().toArray(
									new QContainer[target.getQContainers().size()])));
			psm.calculateNewPathTo(pso, target);
			psm.activateNextQContainer(pso);
		}
		finally {
			propagationManager.commitPropagation();
		}
	}

	/**
	 * Calculates a path to the target, having the cheapest path from the actual
	 * state.
	 * 
	 * @created 07.10.2013
	 * @param qContainers a List of QContainers that should be used as targets
	 * @return the Target being selected by the cost benefit, because it can be
	 *         reached with the cheapest path
	 * @throws AbortException if no path to a target could be calculated
	 */
	public Target selectCheapestTarget(List<QContainer> qContainers) throws AbortException {
		Target[] targets = new Target[qContainers.size()];
		int i = 0;
		for (QContainer qContainer : qContainers) {
			targets[i++] = new Target(qContainer);
		}
		PSMethodCostBenefit psm = getPSMethodCostBenefit();
		CostBenefitCaseObject pso = getCostBenefitCaseObject(psm);
		PropagationManager propagationManager = session.getPropagationManager();
		try {
			propagationManager.openPropagation();
			psm.calculateNewPathTo(pso, targets);
			psm.activateNextQContainer(pso);
		}
		finally {
			propagationManager.commitPropagation();
		}
		return getCurrentTarget();
	}

	/**
	 * Selects a new target for the interview strategy. This makes the cost
	 * benefit problem solver to arrange a new questionnaire sequence (path) to
	 * cover the selected target questionnaire. Because it might be complex to
	 * find such a path (covering the target and preparing its preconditions),
	 * this operation may require a long calculation.
	 * <p>
	 * The selected target questionnaire and the created path are valid until
	 * the user answer some unexpected value. If he does the path becomes
	 * invalid and the cost benefit strategic solver is in charge to create a
	 * new path to the cost/benefit optimal target (based on the new situation).
	 * If this behavior is not desired a new target has to be selected manually
	 * afterwards, using this method again.
	 * <p>
	 * <b>Note:</b><br>
	 * If the search algorithm is not able to provide such a path or if it
	 * implements an abort strategy that prevents the algorithm from finding
	 * such a path this method results in an {@link AbortException}.
	 * 
	 * @created 07.03.2011
	 * @param target the target to select
	 * @throws AbortException if no path towards the target could be found
	 * @see AbortException
	 * @see AbortStrategy
	 * @see SearchAlgorithm
	 * @see Target
	 */
	public void selectTarget(QContainer targetQuestionnaire) throws AbortException {
		Target target = new Target(targetQuestionnaire);
		selectTarget(target);
	}

	/**
	 * Returns the current target. This is the target a path has been calculated
	 * for and selected into the interview agenda. May return null if no current
	 * target is available.
	 * 
	 * @created 08.03.2011
	 * @return the current target of the cost benefit strategic solver
	 */
	public Target getCurrentTarget() {
		PSMethodCostBenefit psm = getPSMethodCostBenefit();
		CostBenefitCaseObject pso = getCostBenefitCaseObject(psm);

		SearchModel searchModel = pso.getSearchModel();
		if (searchModel == null) return null;

		return searchModel.getBestCostBenefitTarget();
	}

	/**
	 * Makes the cost benefit strategic solver to recalculate the possible
	 * targets, search for paths towards these targets and to select the path
	 * with the best cost/benefit ratio.
	 * <p>
	 * It can be used to switch back to the originally selected target after
	 * using {@link #selectTarget(Target)} or {@link #selectTarget(QContainer)}
	 * method. It might also be used to reinitialize the cost benefit solver
	 * after setting a couple of values manually that are not part of the
	 * questionnaires to be answered. (Note that due to performance reasons the
	 * cost benefit solver recalculate the path only automatically after a
	 * relevant/indicated questionnaire has been fully answered.)
	 * <p>
	 * <b>Note:</b><br>
	 * The cost benefit solver does not selects any path if there are still
	 * other questions open to be answered (the are already indicated to the
	 * interview agenda but no answer has given yet). In such a situation, this
	 * method also cancels the currently selected path of the cost benefit
	 * strategic solver and does not calculate a new one. Instead this is done
	 * automatically afterwards when all open indicated questions will have been
	 * answered.
	 * 
	 * @created 09.03.2011
	 */
	public void selectBestSequence() {
		PSMethodCostBenefit psm = getPSMethodCostBenefit();
		CostBenefitCaseObject pso = getCostBenefitCaseObject(psm);
		PropagationManager propagationManager = session.getPropagationManager();
		try {
			propagationManager.openPropagation();
			// if the path is reset, a calculation will be done in
			// postpropergate
			pso.resetPath();
			pso.resetUnreachedTarget();
		}
		finally {
			propagationManager.commitPropagation();
		}
	}

	/**
	 * Returns a collection of all permanently relevant qcontainers with
	 * actually fullfilled preconditions
	 * 
	 * @created 19.06.2013
	 * @return list of applicable permenantly relevant QContainers
	 */
	public Collection<QContainer> getApplicablePermanentlyRelevantQContainers() {
		Collection<QContainer> result = new LinkedList<QContainer>();
		for (QContainer qcontainer : session.getKnowledgeBase().getManager().getQContainers()) {
			if (qcontainer.getInfoStore().getValue(PSMethodCostBenefit.PERMANENTLY_RELEVANT)) {
				StateTransition stateTransition = StateTransition.getStateTransition(qcontainer);
				if (stateTransition == null || stateTransition.getActivationCondition() == null
						|| Conditions.isTrue(stateTransition.getActivationCondition(), session)) {
					result.add(qcontainer);
				}
			}
		}
		return result;
	}
}
