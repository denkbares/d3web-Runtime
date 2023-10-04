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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.denkbares.collections.DefaultMultiMap;
import com.denkbares.collections.MultiMap;
import com.denkbares.collections.MultiMaps;
import com.denkbares.plugin.Extension;
import com.denkbares.plugin.PluginManager;
import com.denkbares.strings.NumberAwareComparator;
import de.d3web.core.inference.PropagationManager;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.Conditions;
import de.d3web.core.inference.condition.NonTerminalCondition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.AbstractNamedObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionZC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.protocol.ActualQContainerEntry;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.costbenefit.blackboard.CostBenefitCaseObject;
import de.d3web.costbenefit.model.SearchModel;
import de.d3web.costbenefit.model.Target;
import de.d3web.costbenefit.session.protocol.ManualTargetSelectionEntry;
import de.d3web.xcl.XCLModel;

import static de.d3web.core.inference.PSMethod.Type.source;

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

	private static final String PLUGIN_ID = "d3web-CostBenefit";
	private static final String EXTENSION_POINT_COMPARATOR = "AdapterStateTargetComparator";
	private final Session session;
	private final PSMethodCostBenefit psm;
	private final CostBenefitCaseObject pso;

	private MultiMap<Question, CondEqual> adapterStates = null;
	private List<Question> systemStates = null;
	private Comparator<QContainer> adapterStatesTargetComparator;

	/**
	 * sort descending by the benefit of the target, with no regard to the target's costs
	 */
	static final Comparator<Target> BENEFIT_COMPARATOR = (target1, target2) ->
			Double.compare(target2.getBenefit(), target1.getBenefit());

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

	public PSMethodCostBenefit getProblemSolver() {
		return psm;
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
		return getAlternativeTargets(false);
	}

	/**
	 * Returns a list of all alternative targets that can be suggested to the user. The list is sorted by the targets
	 * benefit, regardless to the costs they may induce.
	 * <p>
	 * Please note that the calculated information contained in the {@link Target} (namely the minimal path and the
	 * benefit of the target), are based on the latest search of the cost benefit underlying search algorithm. Due to
	 * already answered test steps the minimal path may have become no longer applicable.
	 *
	 * @param includeCurrent true to also allow the current target to be included (if it is a benefit target)
	 * @return the list of all alternative targets sorted by their benefit
	 * @created 07.03.2011
	 */
	public List<Target> getAlternativeTargets(boolean includeCurrent) {
		// create a list of all targets
		List<Target> result = new ArrayList<>(pso.getDiscriminatingTargets());

		if (!includeCurrent) {
			// but without the currently selected one
			Target currentTarget = getCurrentTarget();
			result.remove(currentTarget);
		}

		// sort the list regarding to their benefit to become our result
		result.sort(BENEFIT_COMPARATOR);
		return Collections.unmodifiableList(result);
	}

	/**
	 * Returns a list of all targets that are used for the recent path calculation. In contrast to {@link
	 * #getAlternativeTargets()} (returning the targets automatically selected by the cost/benefit solver), this method
	 * returns the targets that actually have been used for the recent search, regardless of their benefit (e.g. if the
	 * target was manually selected by the user).
	 * <p>
	 * The list is sorted by the targets benefit, regardless to the costs they may induce. By specifying currentFirst as
	 * true, the current target is moved to the first element of the list, if there is a current target available.
	 * <p>
	 * Please note that the calculated information contained in the {@link Target} (namely the minimal path and the
	 * benefit of the target), are based on the latest search of the cost benefit underlying search algorithm. Due to
	 * already answered test steps the minimal path may have become no longer applicable.
	 *
	 * @param currentFirst if true, moves the current target at the top of the list
	 * @return the list of all alternative targets sorted by their benefit
	 * @created 07.03.2011
	 */
	public List<Target> getCalculatedTargets(boolean currentFirst) {
		SearchModel model = getSearchModel();
		if (model == null) return Collections.emptyList();

		// create a list of all targets, sorted regarding to their benefit to become our result
		List<Target> result = new ArrayList<>(model.getTargets());
		result.addAll(model.getBlockedTargets());
		result.sort(BENEFIT_COMPARATOR);

		// but move the currently selected one if desired
		if (currentFirst) {
			Target currentTarget = getCurrentTarget();
			if (currentTarget != null) {
				result.remove(currentTarget);
				result.add(0, currentTarget);
			}
		}

		return Collections.unmodifiableList(result);
	}

	private void initStates() {
		if (systemStates != null) return;

		systemStates = new ArrayList<>();
		adapterStates = new DefaultMultiMap<>(MultiMaps.linkedFactory(), MultiMaps.linkedFactory());

		for (Question stateQuestion : CostBenefitProperties.getStateQuestions(session.getKnowledgeBase())) {
			switch (CostBenefitProperties.getUUTState(stateQuestion)) {
				// handle device states, mapping the devices to the adapter
				case measurementDevice:
					// the following code only works for KnowledgeDesigner knowledge bases (SGP+CAN)
					Value stateValue = CostBenefitProperties.getIntegratedValue(stateQuestion);
					//noinspection deprecation
					Question adapterQuestion = CostBenefitProperties.getAdapterState(stateQuestion);
					if (adapterQuestion == null) break;
					adapterStates.put(adapterQuestion, new CondEqual(stateQuestion, stateValue));
					break;

				// skip adapters (processed with devices) and mechanical checks
				case mechanicalCheck:
					break;
				case measurementAdapter:
					for (Choice choice : CostBenefitProperties.getAdaptedChoice(stateQuestion)) {
						adapterStates.put(stateQuestion, new CondEqual(stateQuestion, new ChoiceValue(choice)));
					}
					break;

				// otherwise use as normal system state
				case status:
				case checkOnce:
					systemStates.add(stateQuestion);
					break;
			}
		}
	}

	/**
	 * Returns a list of all state questions that represents the states of the "unit under test".
	 */
	public Collection<Question> getSystemStates() {
		initStates();
		return Collections.unmodifiableList(systemStates);
	}

	/**
	 * Returns a set of all state questions that represent measurement adapters. These questions may be used to get a
	 * number of test steps that measure through the adapter, using {@link #getTargetsForAdapterState(Question,
	 * Choice)}.
	 */
	public Collection<Question> getAdapterStates() {
		initStates();
		return Collections.unmodifiableSet(adapterStates.keySet());
	}

	/**
	 * Returns a set of all conditions represent a measurement adapter adapted to a certain socket. These conditions
	 * may be used to get a number of test steps that measure through the adapter, using {@link
	 * #getTargetsForAdapterState(Question, Choice)}.
	 */
	public Collection<CondEqual> getAdaptedConditions() {
		initStates();
		return Collections.unmodifiableSet(adapterStates.valueSet());
	}

	/**
	 * Returns all solutions in the KnowledgeBase that have an XCL model.
	 *
	 * @return collection containing all solutions
	 */
	public Collection<Solution> getXclSolutions() {
		KnowledgeBase kb = session.getKnowledgeBase();
		return kb.getManager()
				.getSolutions()
				.stream()
				.filter(solution -> solution.getKnowledgeStore().getKnowledge(XCLModel.KNOWLEDGE_KIND) != null)
				.collect(Collectors.toList());
	}

	/**
	 * Returns all potential target test steps that have the adapter integrated, that is referenced by the specified
	 * adapter question. The question is usually one of the questions returned by {@link #getAdapterStates()}.
	 * Otherwise, the method returns an empty set of targets.
	 * The returned test steps are grouped into buckets of test steps that contain measurements for the same
	 * adapter-to-MD plug, so they can be targeted together.
	 *
	 * @param adapterStateQuestion the adapter integration question, to search the target test steps for
	 * @param choice               optionally the choice to additionally filter target test steps by
	 * @return the target test steps for the adapter
	 */
	public Set<Set<QASet>> getTargetsForAdapterState(Question adapterStateQuestion, @Nullable Choice choice) {
		initStates();
		Set<CondEqual> states = new LinkedHashSet<>(adapterStates.getValues(adapterStateQuestion));
		if (choice != null) states.removeIf(c -> !usesChoice(c, choice));
		Set<QContainer> targets = new HashSet<>();
		Set<String> visitedQContainers = session.getProtocol()
				.getProtocolHistory()
				.stream()
				.filter(e -> e instanceof ActualQContainerEntry)
				.map(e -> (ActualQContainerEntry) e)
				.map(ActualQContainerEntry::getQContainerName)
				.collect(Collectors.toSet());
		for (QContainer target : new ArrayList<>(session.getKnowledgeBase().getManager().getQContainers())) {
			StateTransition transition = StateTransition.getStateTransition(target);
			if (transition == null) continue;
			if (!hasAnyState(transition.getActivationCondition(), states)) continue;
			if (visitedQContainers.contains(target.getName())) continue;
			if (isDeAdaptation(target)) continue;
			targets.add(target);
		}

		// Use property MEASUREMENT_CONNECTOR to find other test steps marked to be measuring on the same connector.
		// Normally, we would only need this property to find all desired test steps... to stay somewhat backwards
		// compatible to old knowledge bases, we also check using the old methods, but maybe don't find all test steps,
		// e.g. single_adapt measurements.
		if (choice != null) {
			String connector = choice.getInfoStore().getValue(CostBenefitProperties.MEASUREMENT_CONNECTOR);
			if (connector != null) {
				for (QContainer qaSet : session.getKnowledgeBase().getManager().getQContainers()) {
					String otherConnector = qaSet.getInfoStore().getValue(CostBenefitProperties.MEASUREMENT_CONNECTOR);
					if (connector.equals(otherConnector)) {
						targets.add(qaSet);
					}
				}
			}
		}

		// put all targets in buckets that are equal according to the comparator...
		// using a comparator comparing the plugs of the adapter connecting to the MD will group for these plugs
		Comparator<QContainer> comparator = getAdapterStateTargetComparator();
		Set<Set<QASet>> targetBuckets = new HashSet<>();
		targets:
		for (QContainer target : targets) {
			// find matching bucket
			for (Set<QASet> targetBucket : targetBuckets) {
				if (targetBucket.isEmpty()) continue;
				QContainer bucketTarget = (QContainer) targetBucket.iterator().next();
				if (comparator.compare(bucketTarget, target) == 0) {
					targetBucket.add(target);
					continue targets;
				}
			}
			// no matching bucket found, create a new one
			Set<QASet> newBucket = new HashSet<>();
			newBucket.add(target);
			targetBuckets.add(newBucket);
		}
		return targetBuckets;
	}

	private boolean usesChoice(CondEqual condEqual, Choice choice) {
		if (condEqual.getValue() instanceof ChoiceValue) {
			return ((ChoiceValue) condEqual.getValue()).getChoiceID().getText().equals(choice.getName());
		}
		return false;
	}

	private boolean isDeAdaptation(QContainer target) {
		return KnowledgeBaseUtils.getSuccessors(target, QuestionZC.class)
				.stream()
				.map(QuestionZC::getName)
				.anyMatch(n -> n.endsWith(CostBenefitProperties.DE_ADAPTED_CHOICE_NAME));
	}

	private Comparator<QContainer> getAdapterStateTargetComparator() {
		if (this.adapterStatesTargetComparator == null) {
			for (Extension extension : PluginManager.getInstance()
					.getExtensions(PLUGIN_ID, EXTENSION_POINT_COMPARATOR)) {
				Object singleton = extension.getSingleton();
				if (singleton instanceof Comparator) {
					//noinspection unchecked
					adapterStatesTargetComparator = (Comparator<QContainer>) singleton;
					break;
				}
			}
			// no extension found, compare just based on name
			if (this.adapterStatesTargetComparator == null) {
				this.adapterStatesTargetComparator = Comparator.comparing(AbstractNamedObject::getName,
						NumberAwareComparator.CASE_INSENSITIVE);
			}
		}
		return this.adapterStatesTargetComparator;
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

	public void unselectTarget() {
		PropagationManager propagationManager = session.getPropagationManager();
		try {
			propagationManager.openPropagation();
			pso.resetPath();
			pso.resetUnreachedTarget();
			psm.calculateNewPath(pso);
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
	 * Checks whether the currently active/displayed is currently touch by the user or not, meaning that the use has
	 * started answering questions form it.
	 *
	 * @return true if the user started answering the current QContainer, false otherwise
	 */
	public boolean isCurrentQContainerUserTouched() {
		PSMethodCostBenefit costBenefit = session.getPSMethodInstance(PSMethodCostBenefit.class);
		if (costBenefit == null) {
			throw new IllegalStateException("Cannot use expert mode without using cost benefit problem solver");
		}

		CostBenefitCaseObject cbCaseObject = session.getSessionObject(costBenefit);
		QContainer currentQContainer = cbCaseObject.getCurrentQContainer();
		if (currentQContainer == null) {
			// we are currently in the initialization phase of the session, setting system states
			// the user does not answer these questions directly
			return false;
		}

		// check is any source facts are provided for any successor question of the QContainer
		final Blackboard blackboard = session.getBlackboard();
		for (Question child : KnowledgeBaseUtils.getSuccessors(currentQContainer, Question.class)) {
			if (blackboard.getContributingPSMethods(child).stream().anyMatch(psMethod -> psMethod.hasType(source))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the previously used search model, used to calculate the current target.
	 */
	public SearchModel getSearchModel() {
		return pso.getSearchModel();
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
			// if the path is reset, a calculation will be done in postpropergate
			pso.resetPath();
			pso.resetUnreachedTarget();
		}
		finally {
			propagationManager.commitPropagation();
		}
	}

	/**
	 * Repeats the most recent calculation that has been performed, but replaces the abort strategy to allow 2x (up to
	 * 5x) as many calculation steps as previously been searched.
	 *
	 * @throws AbortException if a target was selected, but no path can be established with this calculation
	 */
	public void recalculate() throws AbortException {
		// check if a previous search has been executed
		SearchModel searchModel = pso.getSearchModel();
		if (searchModel == null) return;

		// sets a new abort strategy that allows al least 2x steps to be used (up to 5x steps)
		SearchAlgorithm algorithm = psm.getSearchAlgorithm();
		AbortStrategy existing = algorithm.getAbortStrategy();
		int lastMaxSteps = existing instanceof DefaultAbortStrategy existingDefStrategy ? existingDefStrategy.getMaxSteps() : searchModel.getCalculationSteps();
		algorithm.setAbortStrategy(new DefaultAbortStrategy(lastMaxSteps * 2, 2.5f));

		try {
			// if a manual target has been selected, select a path to that target
			NavigableSet<Target> targets = searchModel.getTargets();
			if (targets.size() == 1) {
				Target target = targets.first();
				if (target.getBenefit() >= PSMethodCostBenefit.USER_SELECTED_BENEFIT) {
					selectTarget(new Target(target.getQContainers()));
					return;
				}
			}

			// otherwise do a normal path calculation
			selectBestSequence();
		}
		finally {
			algorithm.setAbortStrategy(existing);
		}
	}

	/**
	 * Returns a collection of all permanently relevant qcontainers with actually fulfilled preconditions
	 *
	 * @return list of applicable permanently relevant QContainers
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

	/**
	 * Resets the aboard state of CB after a target could not be selected. Use this, if CB should again
	 */
	public void resetAfterAboard() {
		pso.setAbortedManuallySetTarget(false);
	}
}
