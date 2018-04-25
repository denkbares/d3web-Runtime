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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.denkbares.collections.DefaultMultiMap;
import com.denkbares.collections.MultiMap;
import de.d3web.core.inference.PropagationManager;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.Conditions;
import de.d3web.core.inference.condition.NonTerminalCondition;
import de.d3web.core.knowledge.TerminologyManager;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.costbenefit.blackboard.CostBenefitCaseObject;
import de.d3web.costbenefit.model.SearchModel;
import de.d3web.costbenefit.model.Target;
import de.d3web.costbenefit.session.protocol.ManualTargetSelectionEntry;

/**
 * This class provides utility functions to enable an advanced user mode. Within that mode the user is enabled to also
 * see additional Targets with high benefit (regardless to their costs). The user is also enabled to select on of these
 * targets. If the user has selected on the cost benefit strategic solver is in charge to calculate a new path towards
 * this target.
 *
 * @author volker_belli
 * @created 07.03.2011
 */
public class ExpertMode implements SessionObject {

	private final Session session;
	private final PSMethodCostBenefit psm;
	private final CostBenefitCaseObject pso;

	private MultiMap<Question, CondEqual> adapterStates = null;

	static final Comparator<Target> BENEFIT_COMPARATOR = (target1, target2) -> {
		// sort by the costs of the target (sort descending)
		return (int) Math.signum(target2.getBenefit() - target1.getBenefit());
	};

	private final static SessionObjectSource<ExpertMode> EXPERT_MODE_SOURCE = session -> {
		// check if it is allowed to create such an object
		PSMethodCostBenefit psm = session.getPSMethodInstance(PSMethodCostBenefit.class);
		if (psm == null) {
			throw new IllegalStateException(
					"ExpertMode cannot be used if session does not contain the cost benefit strategic solver");
		}
		return new ExpertMode(session, psm);
	};

	private ExpertMode(Session session, PSMethodCostBenefit psm) {
		this.session = session;
		this.psm = psm;
		this.pso = session.getSessionObject(psm);
	}

	/**
	 * Returns the ExportMode object for the specified session. The expert mode can only be received for sessions that
	 * uses a cost benefit strategic solver. Otherwise an IllegalStateException is thrown.
	 *
	 * @param session the session to get the expert mode instance for
	 * @return the ExpertMode instance
	 * @throws IllegalStateException if no cost benefit strategic solver is available in the specified session
	 * @created 07.03.2011
	 */
	public static ExpertMode getExpertMode(Session session) {
		return session.getSessionObject(EXPERT_MODE_SOURCE);
	}

	/**
	 * Returns a list of all alternative targets that can be suggested to the user. The list is sorted by the targets
	 * benefit, regardless to the costs they may induce.
	 * <p>
	 * Please note that the calculated information contained in the {@link Target} (namely the minimal path and the
	 * benefit of the target), are based on the latest search of the cost benefit underlying search algorithm. Due to
	 * already answered test steps the minimal path may have become no longer applicable.
	 *
	 * @return the list of all alternative targets sorted by their benefit
	 * @created 07.03.2011
	 */
	public List<Target> getAlternativeTargets() {
		// create a list of all targets
		List<Target> result = new ArrayList<>(pso.getDiscriminatingTargets());

		// but without the currently selected one
		Target currentTarget = getCurrentTarget();
		result.remove(currentTarget);

		// sort the list regarding to their benefit to become our result
		result.sort(BENEFIT_COMPARATOR);
		return Collections.unmodifiableList(result);
	}

	private void initAdapterStates() {
		if (adapterStates != null) return;

		// TODO: here some hard-coded stuff (convention) is used to detect the adaptation states. Replace by properties
		Pattern devicePattern = Pattern.compile("(target_state_questionnaire#adaptation_\\w+)_X\\d+", Pattern.CASE_INSENSITIVE);
		adapterStates = new DefaultMultiMap<>();
		TerminologyManager manager = session.getKnowledgeBase().getManager();
		QContainer states = manager.searchQContainer("target_state_questionnaire");
		for (TerminologyObject stateObject : states.getChildren()) {
			Question stateQuestion = (Question) stateObject;
			Matcher matcher = devicePattern.matcher(stateQuestion.getName());
			if (matcher.matches()) {
				Value stateValue = KnowledgeBaseUtils.findValue(stateQuestion, stateQuestion.getName() + "#integriert");
				Question adapterQuestion = manager.searchQuestion(matcher.group(1));
				adapterStates.put(adapterQuestion, new CondEqual(stateQuestion, stateValue));
			}
		}
	}

	/**
	 * Returns a list of all state questions that represents measurement adapters. These questions may be used to get a
	 * number of test steps that measures through the adapter, using {@link #getTargetsForAdapterState(Question)}.
	 */
	public Collection<Question> getAdapterStates() {
		initAdapterStates();
		return Collections.unmodifiableSet(adapterStates.keySet());
	}

	/**
	 * Returns all potential target test stepts that have the adapter integrated, that is referenced by the specified
	 * adapter question. The question is usually one of the questions returned by {@link #getAdapterStates()}. Otherwise
	 * the method returns an empty set of targets.
	 *
	 * @param adapterStateQuestion the adapter integration question, to search the target test steps for
	 * @return the target test steps for the adapter
	 */
	public Set<QContainer> getTargetsForAdapterState(Question adapterStateQuestion) {
		initAdapterStates();
		Set<CondEqual> states = adapterStates.getValues(adapterStateQuestion);
		Set<QContainer> targets = new HashSet<>();
		for (QContainer target : session.getKnowledgeBase().getManager().getQContainers()) {
			StateTransition transition = StateTransition.getStateTransition(target);
			if (transition == null) continue;
			if (hasAnyState(transition.getActivationCondition(), states)) {
				targets.add(target);
			}
		}
		return targets;
	}

	private boolean hasAnyState(Condition condition, Collection<CondEqual> states) {
		//noinspection SuspiciousMethodCalls
		return (condition instanceof NonTerminalCondition)
				? ((NonTerminalCondition) condition).getTerms().stream().anyMatch(c -> hasAnyState(c, states))
				: states.contains(condition);
	}

	/**
	 * Selects a new target for the interview strategy. This makes the cost benefit problem solver to arrange a new
	 * questionnaire sequence (path) to cover the selected target. Because it might be complex to find such a path
	 * (covering the target and preparing its preconditions), this operation may require a long calculation.
	 * <p>
	 * The selected target and the created path are valid until the user answer some unexpected value. If he does the
	 * path becomes invalid and the cost benefit strategic solver is in charge to create a new path to the cost/benefit
	 * optimal target (based on the new situation). If this behavior is not desired a new target has to be selected
	 * manually afterwards, using this method again.
	 *
	 * <b>Note:</b><br>
	 * If the search algorithm is not able to provide such a path or if it implements an abort strategy that prevents
	 * the algorithm from finding such a path this method results in an {@link AbortException}.
	 *
	 * @param target the target to select
	 * @throws AbortException if no path towards the target could be found
	 * @created 07.03.2011
	 * @see AbortException
	 * @see AbortStrategy
	 * @see SearchAlgorithm
	 * @see Target
	 */
	public void selectTarget(Target target) throws AbortException {
		PropagationManager propagationManager = session.getPropagationManager();
		try {
			propagationManager.openPropagation();
			session.getProtocol().addEntry(
					new ManualTargetSelectionEntry(propagationManager.getPropagationTime(), target.getQContainers()));
			psm.calculateNewPathTo(pso, target);
		}
		finally {
			propagationManager.commitPropagation();
		}
	}

	/**
	 * Calculates a path to the target, having the cheapest path from the actual state.
	 *
	 * @param qContainers a List of QContainers that should be used as targets
	 * @return the Target being selected by the cost benefit, because it can be reached with the cheapest path
	 * @throws AbortException if no path to a target could be calculated
	 * @created 07.10.2013
	 */
	public Target selectCheapestTarget(Collection<QContainer> qContainers) throws AbortException {
		Target[] targets = new Target[qContainers.size()];
		int i = 0;
		for (QContainer qContainer : qContainers) {
			targets[i++] = new Target(qContainer);
		}
		PropagationManager propagationManager = session.getPropagationManager();
		try {
			propagationManager.openPropagation();
			psm.calculateNewPathTo(pso, targets);
		}
		finally {
			propagationManager.commitPropagation();
		}
		return getCurrentTarget();
	}

	/**
	 * Selects a new target for the interview strategy. This makes the cost benefit problem solver to arrange a new
	 * questionnaire sequence (path) to cover the selected target questionnaire. Because it might be complex to find
	 * such a path (covering the target and preparing its preconditions), this operation may require a long
	 * calculation.
	 * <p>
	 * The selected target questionnaire and the created path are valid until the user answer some unexpected value. If
	 * he does the path becomes invalid and the cost benefit strategic solver is in charge to create a new path to the
	 * cost/benefit optimal target (based on the new situation). If this behavior is not desired a new target has to be
	 * selected manually afterwards, using this method again.
	 *
	 * <b>Note:</b><br>
	 * If the search algorithm is not able to provide such a path or if it implements an abort strategy that prevents
	 * the algorithm from finding such a path this method results in an {@link AbortException}.
	 *
	 * @param targetQuestionnaire the target to select
	 * @throws AbortException if no path towards the target could be found
	 * @created 07.03.2011
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
	 * Returns the current target. This is the target a path has been calculated for and selected into the interview
	 * agenda. May return null if no current target is available.
	 *
	 * @return the current target of the cost benefit strategic solver
	 * @created 08.03.2011
	 */
	public Target getCurrentTarget() {
		SearchModel searchModel = pso.getSearchModel();
		return (searchModel == null) ? null : searchModel.getBestCostBenefitTarget();
	}

	/**
	 * Makes the cost benefit strategic solver to recalculate the possible targets, search for paths towards these
	 * targets and to select the path with the best cost/benefit ratio.
	 * <p>
	 * It can be used to switch back to the originally selected target after using {@link #selectTarget(Target)} or
	 * {@link #selectTarget(QContainer)} method. It might also be used to reinitialize the cost benefit solver after
	 * setting a couple of values manually that are not part of the questionnaires to be answered. (Note that due to
	 * performance reasons the cost benefit solver recalculate the path only automatically after a relevant/indicated
	 * questionnaire has been fully answered.)
	 *
	 * <b>Note:</b><br>
	 * The cost benefit solver does not selects any path if there are still other questions open to be answered (the are
	 * already indicated to the interview agenda but no answer has given yet). In such a situation, this method also
	 * cancels the currently selected path of the cost benefit strategic solver and does not calculate a new one.
	 * Instead this is done automatically afterwards when all open indicated questions will have been answered.
	 *
	 * @created 09.03.2011
	 */
	public void selectBestSequence() {
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
	 * Returns a collection of all permanently relevant qcontainers with actually fullfilled preconditions
	 *
	 * @return list of applicable permenantly relevant QContainers
	 * @created 19.06.2013
	 */
	public Collection<QContainer> getApplicablePermanentlyRelevantQContainers() {
		Collection<QContainer> result = new LinkedList<>();
		for (QContainer qcontainer : session.getKnowledgeBase().getManager().getQContainers()) {
			if (qcontainer.getInfoStore().getValue(CostBenefitProperties.PERMANENTLY_RELEVANT)) {
				StateTransition stateTransition = StateTransition.getStateTransition(qcontainer);
				if (stateTransition == null || stateTransition.getActivationCondition() == null
						|| Conditions.isTrue(stateTransition.getActivationCondition(), session)) {
					result.add(qcontainer);
				}
			}
		}
		return result;
	}

	/**
	 * Returns true if the manual mode is turned on. In manual mode no paths were calculated automatically. Instead, the
	 * already calculated paths were completed as usual, but requiring to have other strategic solvers to append
	 * additional interview items, or manually starts the calculation for a new path.
	 *
	 * @return if manual mode is turned on
	 */
	public boolean isManualMode() {
		return psm.isManualMode() || pso.isManualMode();
	}

	/**
	 * Turns on/off the manual mode. In manual mode no paths were calculated automatically. Instead, the already
	 * calculated paths were completed as usual, but requiring to have other strategic solvers to append additional
	 * interview items, or manually starts the calculation for a new path.
	 * <p>
	 * If the man ual mode is turned off (and no path has been selected yet), the method also ensures that a path is
	 * tried to be calculated (immediately, or after the current propagation).
	 *
	 * @param manualMode true if manual mode should be turned on
	 */
	public void setManualMode(boolean manualMode) {
		boolean wasManualMode = isManualMode();
		pso.setManualMode(manualMode);

		// if it has changed from manual to automatic (including considering the psm flag),
		// fire propagation to calculate path if no propagation is already open
		if (wasManualMode && !isManualMode() && !pso.hasCurrentSequence()) {
			psm.calculateNewPath(pso);
		}
	}
}
