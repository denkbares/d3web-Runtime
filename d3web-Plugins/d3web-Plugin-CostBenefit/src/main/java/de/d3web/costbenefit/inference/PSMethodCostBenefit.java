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
package de.d3web.costbenefit.inference;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.denkbares.utils.Log;
import de.d3web.core.inference.PSMethodAdapter;
import de.d3web.core.inference.PSMethodInit;
import de.d3web.core.inference.PostHookablePSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.StrategicSupport;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.Conditions;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.blackboard.Facts;
import de.d3web.core.session.builder.SessionBuilder;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.costbenefit.CostBenefitUtil;
import de.d3web.costbenefit.blackboard.CopiedSession;
import de.d3web.costbenefit.blackboard.CostBenefitCaseObject;
import de.d3web.costbenefit.inference.astar.AStarAlgorithm;
import de.d3web.costbenefit.model.Path;
import de.d3web.costbenefit.model.SearchModel;
import de.d3web.costbenefit.model.Target;
import de.d3web.costbenefit.model.ids.Node;
import de.d3web.costbenefit.session.protocol.CalculatedPathEntry;
import de.d3web.interview.Form;
import de.d3web.interview.Interview;
import de.d3web.interview.inference.PSMethodInterview;

/**
 * The PSMethodCostBenefit indicates QContainer to establish a diagnosis as cheap as possible. This is configurable with
 * a TargetFunction, a CostFunction and a SearchAlgorithm.
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class PSMethodCostBenefit extends PSMethodAdapter implements SessionObjectSource<CostBenefitCaseObject>, PostHookablePSMethod {

	/**
	 * Constant for the benefit of a test step if the user has selected the test steo as the target
	 */
	public static final double USER_SELECTED_BENEFIT = 10000000000.0;

	private TargetFunction targetFunction;
	private CostFunction costFunction;
	private SearchAlgorithm searchAlgorithm;
	private SolutionsRater solutionsRater;
	private double strategicBenefitFactor = 0.0;
	private boolean manualMode = false;
	private WatchSet watchSet;

	private static boolean debugging = false;

	public static void setDebugging(boolean debugging) {
		PSMethodCostBenefit.debugging = debugging;
	}

	public static boolean isDebugging() {
		return debugging;
	}

	public double getStrategicBenefitFactor() {
		return strategicBenefitFactor;
	}

	/**
	 * Sets the strategicBenefitFactor, if it is set to 0, no strategic benefit is added
	 *
	 * @param strategicBenefitFactor the factor to set
	 * @throws IllegalArgumentException if the factor is lower than 0
	 * @created 08.05.2012
	 */
	public void setStrategicBenefitFactor(double strategicBenefitFactor) {
		if (strategicBenefitFactor < 0.0) {
			throw new IllegalArgumentException(
					"Strategic benefit factor must be 0 or greater");
		}
		this.strategicBenefitFactor = strategicBenefitFactor;
	}

	public PSMethodCostBenefit(TargetFunction targetFunction,
							   CostFunction costFunction, SearchAlgorithm searchAlgorithm, SolutionsRater solutionsRater) {
		this.targetFunction = targetFunction;
		this.costFunction = costFunction;
		this.searchAlgorithm = searchAlgorithm;
		this.solutionsRater = solutionsRater;
	}

	public PSMethodCostBenefit() {
		this.targetFunction = new DefaultTargetFunction();
		this.costFunction = new DefaultCostFunction();
		this.searchAlgorithm = new AStarAlgorithm();
		this.solutionsRater = new DefaultSolutionRater();
	}

	@Override
	public void init(Session session) {
		if (watchSet != null) {
			CostBenefitCaseObject caseObject = session.getSessionObject(this);
			for (QContainer qcon : watchSet.getqContainers()) {
				caseObject.addWatch(qcon);
			}
		}
	}

	/**
	 * Calculates a new path to a specific target. The method also selects the calculated path into the interview. The
	 * method ensures that the path will be followed as long as possible.
	 * <p>
	 * Due to changing indications, this method should only be called inside a propagation frame.
	 *
	 * @param caseObject the case object to select the path for
	 * @param targets    the targets for the path to be calculated
	 * @throws AbortException if no path could been established towards the specified target
	 * @created 08.03.2011
	 */
	void calculateNewPathTo(CostBenefitCaseObject caseObject, Target... targets) throws AbortException {
		// first reset the search path
		caseObject.resetPath();

		// in contrast to the "usual" path creation we do *not* consider if
		// there are already any other indicated qasets.

		// searching for the best cost/benefit result
		// (only if there is any benefitual target
		initializeSearchModelTo(caseObject, targets);
		SearchModel searchModel = caseObject.getSearchModel();

		search(caseObject.getSession(), searchModel);

		// sets the new path based on the result stored in the search model
		// inside the specified case object.
		Target bestTarget = searchModel.getBestCostBenefitTarget();
		if (bestTarget == null || bestTarget.getMinPath() == null) {
			caseObject.setAbortedManuallySetTarget(true);
			throw new AbortException();
		}
		else {
			Path minPath = bestTarget.getMinPath();
			Log.info(minPath + " --> " + searchModel.getBestCostBenefitTarget());
			caseObject.activatePath(minPath.getPath(), this);
			caseObject.activateNextQContainer();
		}
	}

	private void initializeSearchModelTo(CostBenefitCaseObject caseObject, Target... targets) {
		Session session = caseObject.getSession();
		SearchModel searchModel = new SearchModel(session);
		for (Target target : targets) {
			boolean hasContraindicatedQContainer = false;
			for (QContainer qContainer : target.getQContainers()) {
				if (isContraIndicated(session, qContainer)) {
					hasContraindicatedQContainer = true;
				}
				else {
					// remove permanently relevant QContainer from blocked
					// qcontainers if it is selected as target manually
					if (CostBenefitProperties.isPermanentlyRelevant(qContainer)) {
						searchModel.getBlockedQContainers().remove(qContainer);
					}
				}
			}
			if (!hasContraindicatedQContainer) {
				searchModel.addTarget(target);
				// initialize benefit in search model to a positive value
				// (use 1 if there is no benefit inside the target)
				searchModel.maximizeBenefit(target, USER_SELECTED_BENEFIT);
			}
		}
		// set the undiscriminated solution to "null" to indicate that we will
		// not consider them for checking to execute a new search
		// we also leave the "discriminating targets" of the case object
		// untouched because they are still valid.
		caseObject.setUndiscriminatedSolutions(null);
		caseObject.setSearchModel(searchModel);
	}

	void calculateNewPath(CostBenefitCaseObject caseObject) {
		// if there are any other interview objects left (e.g. from other
		// problem solvers), we first are going to answer these before creating
		// a new path
		Session session = caseObject.getSession();
		if (hasUnansweredQuestions(session)) return;

		// searching for the best cost/benefit result
		// (only if there is any benefit target)
		initializeSearchModel(caseObject);
		SearchModel searchModel = caseObject.getSearchModel();
		if (searchModel.getBestBenefit() != 0
				&& solutionsRater.check(caseObject.getUndiscriminatedSolutions())) {
			search(session, searchModel);
		}
		// sets the new path based on the result stored in the search model
		// inside the specified case object.
		Target bestTarget = searchModel.getBestCostBenefitTarget();
		if (!(bestTarget == null || bestTarget.getMinPath() == null)) {
			Path minPath = bestTarget.getMinPath();
			Log.info(minPath + " --> " + searchModel.getBestCostBenefitTarget());
			caseObject.activatePath(minPath.getPath(), this);
		}
		caseObject.activateNextQContainer();
	}

	private void search(Session session, SearchModel searchModel) {
		// search and measure time
		long startTime = System.currentTimeMillis();
		searchAlgorithm.search(session, searchModel);
		long duration = System.currentTimeMillis() - startTime;

		// get the path
		List<QContainer> path = Collections.emptyList();
		if (searchModel.getBestCostBenefitTarget() != null
				&& searchModel.getBestCostBenefitTarget().getMinPath() != null) {
			path = searchModel.getBestCostBenefitTarget().getMinPath().getPath();
		}

		// store search result in protocol
		long time = session.getPropagationManager().getPropagationTime();
		session.getProtocol().addEntry(new CalculatedPathEntry(time, path, duration));
	}

	private boolean hasUnansweredQuestions(Session session) {
		Interview interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
		Form nextForm = interview.nextForm();
		return (nextForm != null) && !nextForm.isEmpty();
	}

	/**
	 * Initializes the search model of this cost benefit case object to search for the best cost benefit target. All
	 * possible questions to discriminate are taken into account. Questions (or questionnaires) being contra-indicated
	 * are left out from this search.
	 *
	 * @param caseObject the case object to be initialized for searching
	 * @created 08.03.2011
	 */
	private void initializeSearchModel(CostBenefitCaseObject caseObject) {
		Session session = caseObject.getSession();
		HashSet<Solution> allSolutions = new HashSet<>();
		SearchModel searchModel = new SearchModel(session);
		List<StrategicSupport> strategicSupports = CostBenefitUtil.getStrategicSupports(session);
		for (StrategicSupport strategicSupport : strategicSupports) {
			// calculate the targets from the strategic support items
			Collection<Solution> solutions = strategicSupport.getUndiscriminatedSolutions(session);
			Collection<Question> questions = strategicSupport.getDiscriminatingQuestions(solutions, session);
			Collection<Target> targets = targetFunction.getTargets(session, questions, solutions, strategicSupport);
			Map<QContainer, BlockingReason> blocked = searchModel.getBlockedQContainers();

			nextTarget:
			for (Target target : targets) {
				double benefit = strategicSupport.getInformationGain(target.getQContainers(), solutions, session);
				if (benefit == 0) continue;
				searchModel.addTarget(target);
				searchModel.maximizeBenefit(target, benefit);

				// check if the target is blocked (when any of the QContainers are blocked)
				for (QContainer container : target.getQContainers()) {
					BlockingReason reason = blocked.get(container);
					if (reason != null) {
						searchModel.blockTarget(target, reason);
						continue nextTarget;
					}
				}
			}

			// recall them for storing into the case object
			allSolutions.addAll(solutions);
		}

		caseObject.setUndiscriminatedSolutions(allSolutions);
		caseObject.setSearchModel(searchModel);
		if (strategicBenefitFactor > 0.0) {
			addStrategicBenefit(session, searchModel);
		}
		caseObject.setDiscriminatingTargets(searchModel.getTargets());
	}

	private void addStrategicBenefit(Session session, SearchModel searchModel) {
		double totalbenefit = 0.0;
		List<Question> finalQuestions = getFinalQuestions(session);
		HashMap<Condition, Double> conditionValueCache = new HashMap<>();

		HashMap<QContainer, Target> targetMap = new HashMap<>();
		for (Target t : searchModel.getTargets()) {
			targetMap.put(t.getQContainers().get(0), t);
			totalbenefit += t.getBenefit() / t.getCosts();
			fillConditionValueCache(conditionValueCache, t);
		}
		for (Condition condition : conditionValueCache.keySet()) {
			double additiveValue = strategicBenefitFactor * conditionValueCache.get(condition)
					/ totalbenefit;
			if (condition instanceof CondEqual) {
				CondEqual condEqual = (CondEqual) condition;
				// only inspect conditions of final questions
				if (!finalQuestions.contains(condEqual.getQuestion())) {
					continue;
				}
				if (!Conditions.isTrue(condition, session)) {
					ST:
					for (StateTransition st : StateTransition.getAll(session)) {
						for (ValueTransition vt : st.getPostTransitions()) {
							if (vt.getQuestion() == condEqual.getQuestion()) {
								for (ConditionalValueSetter cvs : vt.getSetters()) {
									boolean answeredQuestion = false;
									for (TerminologyObject object : cvs.getCondition().getTerminalObjects()) {
										if ((object instanceof Question)
												&& UndefinedValue.isNotUndefinedValue(session.getBlackboard()
												.getValue((Question) object))) {
											answeredQuestion = true;
											break;
										}
									}
									if (answeredQuestion) continue;
									if (cvs.getAnswer().equals(condEqual.getValue())) {
										Target target = targetMap.get(st.getQContainer());
										if (target != null) {
											// log.info("Increasing Benefit for " + target + " from " + target.getBenefit() + " to " +
											// (target.getBenefit() + additiveValue));
											searchModel.maximizeBenefit(target, target.getBenefit() + additiveValue);
										}
										else {
											target = new Target(Collections.singletonList(st.getQContainer()));
											target.setBenefit(additiveValue);
											// log.info("Creating new target " + target + " with benefit " + target.getBenefit());
											targetMap.put(st.getQContainer(), target);
											searchModel.addTarget(target);
										}
										continue ST;
									}
								}
							}
						}
					}
				}
			}
		}
		// log.info("Total Benefit/Cost: " + totalbenefit);
		// log.info("BS Zustand Benefit/Cost: " + conditionValueCache);
	}

	private static void fillConditionValueCache(HashMap<Condition, Double> conditionValueCache, Target t) {
		StateTransition stateTransition = StateTransition.getStateTransition(t.getQContainers().get(0));
		if (stateTransition == null) return;
		Condition activationCondition = stateTransition.getActivationCondition();
		List<Condition> terms = new LinkedList<>();
		// Expand ands
		if (activationCondition instanceof CondAnd) {
			CondAnd condAnd = (CondAnd) activationCondition;
			terms.addAll(condAnd.getTerms());
		}
		else {
			terms.add(activationCondition);
		}
		// invert ContNots containing a CondEqual of QuestionOCs
		for (Condition c : new LinkedList<>(terms)) {
			if (c instanceof CondNot) {
				CondNot condNot = (CondNot) c;
				if (condNot.getTerms().get(0) instanceof CondEqual) {
					CondEqual condEqual = (CondEqual) condNot.getTerms().get(0);
					if (condEqual.getQuestion() instanceof QuestionOC
							&& condEqual.getValue() instanceof ChoiceValue) {
						ChoiceValue value = (ChoiceValue) condEqual.getValue();
						List<Condition> conds = new LinkedList<>();
						for (Choice choice : ((QuestionOC) condEqual.getQuestion()).getAllAlternatives()) {
							if (!choice.getName().equals(value.getChoiceID().getText())) {
								conds.add(new CondEqual(condEqual.getQuestion(),
										new ChoiceValue(choice)));
							}
						}
						terms.add(new CondOr(conds));
						terms.remove(condNot);
					}
				}
			}
		}
		// spit ors, each subcondition is added to the list of
		// conditions
		// (ignoring the fact that one condition would be enough)
		for (Condition condition : new LinkedList<>(terms)) {
			if (condition instanceof CondOr) {
				CondOr condOr = (CondOr) condition;
				terms.remove(condOr);
				terms.addAll(condOr.getTerms());
			}
		}
		for (Condition c : terms) {
			conditionValueCache.merge(c, t.getBenefit() / t.getCosts(), Double::sum);
		}
	}

	private static List<Question> getFinalQuestions(Session session) {
		List<Question> finalQuestions = new LinkedList<>();
		for (Question question : session.getKnowledgeBase().getManager().getQuestions()) {
			if (CostBenefitProperties.isCheckOnce(question)) {
				finalQuestions.add(question);
			}
		}
		return finalQuestions;
	}

	/**
	 * Calculates a set of all QContainers, which are blocked by final questions or contra indicated or permanently
	 * relevant
	 *
	 * @param session actual session
	 * @return set of blocked QContainers
	 * @created 24.10.2012
	 */
	public static Map<QContainer, BlockingReason> getBlockedQContainers(Session session) {
		return getBlockedQContainers(session, true, true);
	}

	/**
	 * Calculates a set of all QContainers, which are blocked by final questions. There are two flags to specify, if
	 * contraindicated or permanentlyrelevant TS should also be returned as blocked, even if they are not blocked by
	 * final questions
	 *
	 * @param session                    actual session
	 * @param includeContraindicated     if true, contraindicated QContainers are included in the returned set
	 * @param includePermanentlyRelevant if true, permanently relevant QContainers are included in the returned set
	 * @return set of blocked QContainers, mapped to the blocking reason
	 * @created 01.03.2014
	 */
	public static Map<QContainer, BlockingReason> getBlockedQContainers(Session session, boolean includeContraindicated, boolean includePermanentlyRelevant) {
		Map<QContainer, BlockingReason> result = new HashMap<>();

		// add all unmutable facts to an empty session,
		// to figure out what preconditions will never become true
		Session emptySession = new CopiedSession(session);
		Map<Question, Value> finalValues = getFinalValues(session);
		for (Entry<Question, Value> e : finalValues.entrySet()) {
			emptySession.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(e.getKey(), e.getValue()));
		}

		// check all state transitions if they are potentially available
		for (StateTransition stateTransition : StateTransition.getAll(session)) {
			QContainer container = stateTransition.getQContainer();
			if (includeContraindicated && isContraIndicated(session, container)) {
				result.put(container, BlockingReason.contraIndicated);
				continue;
			}
			if (includePermanentlyRelevant && CostBenefitProperties.isPermanentlyRelevant(container)) {
				result.put(container, BlockingReason.permanentlyRelevant);
				continue;
			}
			Condition activationCondition = stateTransition.getActivationCondition();
			if (activationCondition == null) {
				continue;
			}
			try {
				if (!activationCondition.eval(emptySession)) {
					result.put(container, BlockingReason.checkOnceFalse);
				}
			}
			catch (NoAnswerException | UnknownAnswerException ignored) {
			}
		}
		return result;
	}

	private static boolean isContraIndicated(Session session, QContainer container) {
		return session.getBlackboard().getIndication(container).hasState(State.CONTRA_INDICATED);
	}

	/**
	 * Calculates a map of all values of final questions, being different from their initial value respectively
	 * undefined
	 *
	 * @param session specified session
	 * @return a map of all final questions to their values being set in the specified session
	 * @created 25.09.2012
	 */
	public static Map<Question, Value> getFinalValues(Session session) {
		Map<Question, Value> finalValues = new HashMap<>();
		for (Question q : session.getKnowledgeBase().getManager().getQuestions()) {
			if (CostBenefitProperties.isCheckOnce(q)) {
				// check if q has not the init value
				String initString = q.getInfoStore().getValue(BasicProperties.INIT);
				Value initValue = initString == null
						? UndefinedValue.getInstance()
						: PSMethodInit.getValue(q, initString);
				Value actualValue = session.getBlackboard().getValue(q);
				if (!initValue.equals(actualValue)) {
					finalValues.put(q, actualValue);
				}
			}
		}
		return finalValues;
	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {
		CostBenefitCaseObject caseObject = session.getSessionObject(this);
		Set<QContainer> answeredQuestionnaires = new HashSet<>();
		List<QContainer> sequence = caseObject.getCurrentSequence() != null
				? Arrays.asList(caseObject.getCurrentSequence())
				: new LinkedList<>();
		for (PropagationEntry entry : changes) {
			TerminologyObject object = entry.getObject();
			if (!entry.isStrategic() && entry.hasChanged() && object instanceof Question) {
				CostBenefitUtil.addParentContainers(answeredQuestionnaires, object);
			}
			if (entry.isStrategic() && entry.hasChanged() && object instanceof QContainer) {
				Indication indication = (Indication) entry.getNewValue();
				if (indication.isContraIndicated() && sequence.contains(object)) {
					caseObject.resetPath();
					return;
				}
			}
		}
		// only proceed if we have received reals answers
		if (answeredQuestionnaires.isEmpty()) return;

		boolean isAnyQuesionnaireDone = false;
		for (QContainer qcon : answeredQuestionnaires) {
			if (CostBenefitUtil.isDone(qcon, session) && sequence.contains(qcon)) {
				// mark is the questionnaire is either indicated or in our
				// current sequence
				isAnyQuesionnaireDone = true;
				break;
			}
		}

		// only check if the current path entry is done
		// this is important, because we have to complete a questionnaire to
		// fire its transitions
		// 1. we do not have any sequence yet
		// 2. solutions have changed
		// 3. next questionnaire is no longer applicable

		// 1. we do not have any sequence yet
		// and no other indication is unanswered
		if (!caseObject.hasCurrentSequence() && !hasUnansweredQuestions(session)) {
			// nothing to to, hasUnansweredQuestions will be called in
			// postPropergate again
			return;
		}

		// cost benefit only requires to act after at least one indicated
		// questionnaire has been completed
		if (!isAnyQuesionnaireDone) return;

		// caseObject.activateNextQContainer();
		QContainer qc = caseObject.getCurrentSequence()[caseObject.getCurrentPathIndex()];
		if (!new Node(qc, null).isApplicable(session)) {
			caseObject.resetPath();
		}
	}

	public TargetFunction getTargetFunction() {
		return targetFunction;
	}

	public CostFunction getCostFunction() {
		return costFunction;
	}

	public SearchAlgorithm getSearchAlgorithm() {
		return searchAlgorithm;
	}

	/**
	 * NOTE: Modifying the {@link WatchSet} only affects newly created session, sessions being created before setting
	 * this set are not affected
	 *
	 * @return the actual watchset or null, if none is defined
	 * @created 17.09.2014
	 */
	public WatchSet getWatchSet() {
		return watchSet;
	}

	/**
	 * Sets a set of watched QContainers
	 * <p>
	 * NOTE: This method only affects newly created session, sessions being created before setting this set are not
	 * affected
	 *
	 * @created 17.09.2014
	 */
	public void setWatchSet(WatchSet watchedQContainers) {
		this.watchSet = watchedQContainers;
	}

	public void setSearchAlgorithm(SearchAlgorithm searchAlgorithm) {
		this.searchAlgorithm = searchAlgorithm;
	}

	public void setTargetFunction(TargetFunction targetFunction) {
		this.targetFunction = targetFunction;
	}

	public void setCostFunction(CostFunction costFunction) {
		this.costFunction = costFunction;
	}

	public SolutionsRater getSolutionRater() {
		return solutionsRater;
	}

	public void setSolutionRater(SolutionsRater solutionsRater) {
		this.solutionsRater = solutionsRater;
	}

	/**
	 * Returns true if the manual mode is turned on. In manual mode no paths were calculated automatically. Instead, the
	 * already calculated paths were completed as usual, but requiring to have other strategic solvers to append
	 * additional interview items, or manually starts the calculation for a new path.
	 *
	 * @return if manual mode is turned on
	 */
	public boolean isManualMode() {
		return manualMode;
	}

	/**
	 * Turns on/off the manual mode. In manual mode no paths were calculated automatically. Instead, the already
	 * calculated paths were completed as usual, but requiring to have other strategic solvers to append additional
	 * interview items, or manually starts the calculation for a new path.
	 *
	 * @param manualMode true if manual mode should be turned on
	 */
	public void setManualMode(boolean manualMode) {
		this.manualMode = manualMode;
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		return Facts.mergeIndicationFacts(facts);
	}

	@Override
	public CostBenefitCaseObject createSessionObject(Session session) {
		return new CostBenefitCaseObject(session);
	}

	@Override
	public boolean hasType(Type type) {
		return type == Type.strategic;
	}

	@Override
	public double getPriority() {
		return 6;
	}

	@Override
	public Set<TerminologyObject> getPotentialDerivationSources(TerminologyObject derivedObject) {
		// we cannot provide a better implementation, because it depends on a
		// lot of objects with indirect influences only (e.g. Solutions,
		// Question, ...)
		return Collections.emptySet();
	}

	@Override
	public Set<TerminologyObject> getActiveDerivationSources(TerminologyObject derivedObject, Session session) {
		// we cannot provide a better implementation, because it depends on a
		// lot of objects with indirect influences only (e.g. Solutions,
		// Question, ...)
		return Collections.emptySet();
	}

	@Override
	public void postPropagate(Session session, Collection<PropagationEntry> entries) {
		if (!SessionBuilder.isReplaying(session)) {
			checkPath(session);
		}
	}

	/**
	 * Tests if a new path should be calculated automatically, or if an existing path should be updated automatically,
	 * and calculates if appropriate.
	 */
	public void checkPath(Session session) {
		CostBenefitCaseObject sessionObject = session.getSessionObject(this);
		boolean manualMode = isManualMode() || sessionObject.isManualMode();
		if (!manualMode && !sessionObject.isAbortedManuallySetTarget()) {
			// target was manually set and the path was reset before it was
			// finished -> try to calculate to the target again
			if (sessionObject.getUndiscriminatedSolutions() == null && sessionObject.getUnreachedTarget() != null) {
				try {
					calculateNewPathTo(sessionObject, new Target(sessionObject.getUnreachedTarget()));
				}
				catch (AbortException e) {
					// if no path could be calculated, switch to manual calculation
					sessionObject.resetPath();
					calculateNewPath(sessionObject);
				}
			}
			else {
				calculateNewPath(sessionObject);
			}
		}
	}
}
