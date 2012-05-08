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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PSMethodAdapter;
import de.d3web.core.inference.PSMethodInit;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.StrategicSupport;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.blackboard.Facts;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.costbenefit.Util;
import de.d3web.costbenefit.blackboard.CopiedSession;
import de.d3web.costbenefit.blackboard.CostBenefitCaseObject;
import de.d3web.costbenefit.inference.astar.AStarAlgorithm;
import de.d3web.costbenefit.model.Path;
import de.d3web.costbenefit.model.SearchModel;
import de.d3web.costbenefit.model.Target;
import de.d3web.costbenefit.model.ids.Node;
import de.d3web.costbenefit.session.interviewmanager.CostBenefitAgendaSortingStrategy;

/**
 * The PSMethodCostBenefit indicates QContainer to establish a diagnosis as
 * cheap as possible. This is configurable with a TargetFunction, a CostFunction
 * and a SearchAlgorithm.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class PSMethodCostBenefit extends PSMethodAdapter implements SessionObjectSource<CostBenefitCaseObject> {

	private static final Logger log = Logger.getLogger(PSMethodCostBenefit.class.getName());

	private TargetFunction targetFunction;
	private CostFunction costFunction;
	private SearchAlgorithm searchAlgorithm;
	private double strategicBenefitFactor = 0.0;

	private static final Pattern PATTERN_OK_CHOICE = Pattern.compile("^(.*#)?ok$",
			Pattern.CASE_INSENSITIVE);

	/**
	 * Marks a Question indicating that the value of the question cannot be
	 * changed, once it has left the init value.
	 */
	public static final Property<Boolean> FINAL_QUESTION = Property.getProperty("finalQuestion",
			Boolean.class);

	public double getStrategicBenefitFactor() {
		return strategicBenefitFactor;
	}

	/**
	 * Sets the strategicBenefitFactor, if it is set to 0, no strategic benefit
	 * is added
	 * 
	 * @created 08.05.2012
	 * @param strategicBenefitFactor
	 * @throws IllegalArgumentException if the factor is lower than 0
	 */
	public void setStrategicBenefitFactor(double strategicBenefitFactor) {
		if (strategicBenefitFactor < 0.0) throw new IllegalArgumentException(
				"Strategic benefit factor must be 0 or greater");
		this.strategicBenefitFactor = strategicBenefitFactor;
	}

	public PSMethodCostBenefit(TargetFunction targetFunction,
			CostFunction costFunction, SearchAlgorithm searchAlgorithm) {
		this.targetFunction = targetFunction;
		this.costFunction = costFunction;
		this.searchAlgorithm = searchAlgorithm;
	}

	public PSMethodCostBenefit() {
		this.targetFunction = new DefaultTargetFunction();
		this.costFunction = new DefaultCostFunction();
		this.searchAlgorithm = new AStarAlgorithm();
	}

	@Override
	public void init(Session session) {
		CostBenefitCaseObject caseObject = session.getSessionObject(this);
		session.getInterview().getInterviewAgenda().setAgendaSortingStrategy(
				new CostBenefitAgendaSortingStrategy(caseObject));
		// calculateNewPath(caseObject);
		// activateNextQContainer(caseObject);
	}

	/**
	 * This method is just for refreshing the path of the CaseObject, the Facts
	 * get pushed into the blackboard by calculateNewPath
	 * 
	 * @param caseObject {@link SessionObjectSource}
	 */
	void activateNextQContainer(CostBenefitCaseObject caseObject) {
		QContainer[] currentSequence = caseObject.getCurrentSequence();
		Session session = caseObject.getSession();
		if (currentSequence == null) return;
		// only check if the current one is done
		// (or no current one has been activated yet)
		if (caseObject.getCurrentPathIndex() == -1
					|| Util.isDone(currentSequence[caseObject.getCurrentPathIndex()], session)) {
			caseObject.incCurrentPathIndex();
			if (caseObject.getCurrentPathIndex() >= currentSequence.length) {
				calculateNewPath(caseObject);
				activateNextQContainer(caseObject);
				return;
			}
			QContainer qc = currentSequence[caseObject.getCurrentPathIndex()];
			if (!new Node(qc, null).isApplicable(session)) {
				calculateNewPath(caseObject);
				activateNextQContainer(caseObject);
			}
			// when activating the next qcontainer, which is applicable, check
			// if it is already done and fire state transition and move to next
			// QContainer if necessary
			if (Util.isDone(qc, session)) {
				StateTransition st = StateTransition.getStateTransition(qc);
				if (st != null) st.fire(session);
				// remove indication
				for (Fact fact : caseObject.getIndicatedFacts()) {
					if (fact.getTerminologyObject() == qc) {
						session.getBlackboard().removeInterviewFact(fact);
					}
				}
				activateNextQContainer(caseObject);
			}
		}
	}

	/**
	 * Calculates a new path to a specific target. The method also selects the
	 * calculated path into the interview. The method ensures that the path will
	 * be followed as long as possible.
	 * <p>
	 * Due to changing indications, this method should only be called inside a
	 * propagation frame.
	 * 
	 * @created 08.03.2011
	 * @param caseObject the case object to select the path for
	 * @param target the target for the path to be calculated
	 * @throws AbortException if no path could been established towards the
	 *         specified target
	 */
	void calculateNewPathTo(CostBenefitCaseObject caseObject, Target target) throws AbortException {
		// first reset the search path
		caseObject.resetPath();

		// in contrast to the "usual" path creation we do *not* consider if
		// there are already any other indicated qasets.

		// searching for the best cost/benefit result
		// (only if there is any benefitual target
		initializeSearchModelTo(caseObject, target);
		SearchModel searchModel = caseObject.getSearchModel();
		// when using this method, you want to get that target, beta mode (later
		// there must be a abortion critera to prevent an endless loop if target
		// is unreachable
		// IterativeDeepeningSearchAlgorithm noAbortSearchAlgorithm = new
		// IterativeDeepeningSearchAlgorithm();
		// noAbortSearchAlgorithm.setAbortStrategy(new NoAbortStrategy());
		searchAlgorithm.search(caseObject.getSession(), searchModel);

		// sets the new path based on the result stored in the search model
		// inside the specified case object.
		Target bestTarget = searchModel.getBestCostBenefitTarget();
		if (!(bestTarget == null || bestTarget.getMinPath() == null)) {
			Path minPath = bestTarget.getMinPath();
			log.info(minPath + " --> " + searchModel.getBestCostBenefitTarget());
			activatePath(caseObject, minPath);
		}
		else {
			throw new AbortException();
		}
	}

	private void initializeSearchModelTo(CostBenefitCaseObject caseObject, Target target) {
		Session session = caseObject.getSession();
		SearchModel searchModel = new SearchModel(session);

		searchModel.addTarget(target);
		// initialize benefit in search model to a positive value
		// (use 1 if there is no benefit inside the target)
		searchModel.maximizeBenefit(target, 10000000000.0);

		// set the undiscriminated solution to "null" to indicate that we will
		// not consider them for checking to execute a new search
		// we also leave the "discriminating targets" of the case object
		// untouched because they are still valid.
		caseObject.setUndiscriminatedSolutions(null);
		caseObject.setSearchModel(searchModel);
	}

	void calculateNewPath(CostBenefitCaseObject caseObject) {
		// first reset the search path
		caseObject.resetPath();

		// if there are any other interview objects left (e.g. from other
		// problem solvers), we first are going to answer these before creating
		// a new path
		Session session = caseObject.getSession();
		if (hasUnansweredQuestions(session)) return;

		// searching for the best cost/benefit result
		// (only if there is any benefit target)
		initializeSearchModel(caseObject);
		SearchModel searchModel = caseObject.getSearchModel();
		if (searchModel.getBestBenefit() != 0) {
			searchAlgorithm.search(session, searchModel);
		}
		// sets the new path based on the result stored in the search model
		// inside the specified case object.
		Target bestTarget = searchModel.getBestCostBenefitTarget();
		if (!(bestTarget == null || bestTarget.getMinPath() == null)) {
			Path minPath = bestTarget.getMinPath();
			log.info(minPath + " --> " + searchModel.getBestCostBenefitTarget());
			activatePath(caseObject, minPath);
		}
	}

	private boolean hasUnansweredQuestions(Session session) {
		// return ! session.getInterview().getInterviewAgenda().isEmpty();
		Blackboard blackboard = session.getBlackboard();
		for (InterviewObject interviewObject : blackboard.getInterviewObjects()) {
			if (blackboard.getIndication(interviewObject).isRelevant()) {
				if (!Util.isDone(interviewObject, session)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Initializes the search model of this cost benefit case object to search
	 * for the best cost benefit target. All possible questions to discriminate
	 * are taken into account. Questions (or questionnaires) being
	 * contra-indicated are left out from this search.
	 * 
	 * @created 08.03.2011
	 * @param caseObject the case object to be initialized for searching
	 */
	private void initializeSearchModel(CostBenefitCaseObject caseObject) {
		Session session = caseObject.getSession();
		HashSet<Solution> allSolutions = new HashSet<Solution>();
		HashSet<Target> allTargets = new HashSet<Target>();
		SearchModel searchModel = new SearchModel(session);
		List<StrategicSupport> strategicSupports = getStrategicSupports(session);
		for (StrategicSupport strategicSupport : strategicSupports) {
			// calculate the targets from the strategic support items
			Collection<Solution> solutions = strategicSupport
					.getUndiscriminatedSolutions(session);
			Collection<Question> discriminatingQuestions = strategicSupport
					.getDiscriminatingQuestions(solutions, session);
			Collection<Target> targets = targetFunction.getTargets(session,
					discriminatingQuestions, solutions, strategicSupport);
			for (Target target : targets) {
				boolean skipTarget = false;
				for (QContainer qcontainer : target.getQContainers()) {
					if (session.getBlackboard().getIndication(qcontainer).isContraIndicated()
							|| checkFinalQuestions(session, qcontainer)) {
						skipTarget = true;
						continue;
					}
				}
				if (skipTarget) {
					continue;
				}
				double benefit = strategicSupport.getInformationGain(
						target.getQContainers(), solutions, session);
				if (benefit == 0) continue;
				searchModel.addTarget(target);
				searchModel.maximizeBenefit(target, benefit);
				allTargets.add(target);
			}
			// recall them for storing into the case object
			allSolutions.addAll(solutions);
		}
		caseObject.setUndiscriminatedSolutions(allSolutions);
		caseObject.setSearchModel(searchModel);
		if (strategicBenefitFactor > 0.0) {
			addStrategicBenefit(session, allTargets, searchModel);
		}
		caseObject.setDiscriminatingTargets(allTargets);
	}

	private void addStrategicBenefit(Session session, HashSet<Target> allTargets, SearchModel searchModel) {
		double totalbenefit = 0.0;
		List<Question> finalQuestions = getFinalQuestions(session);
		HashMap<Condition, Double> conditionValueCache = new HashMap<Condition, Double>();

		HashMap<QContainer, Target> targetMap = new HashMap<QContainer, Target>();
		for (Target t : allTargets) {
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
				boolean fullfilled = false;
				try {
					if (condition.eval(session)) {
						fullfilled = true;
					}
				}
				catch (NoAnswerException e) {
					// nothing to do
				}
				catch (UnknownAnswerException e) {
					// nothing to do
				}
				if (!fullfilled) {
					ST: for (StateTransition st : session.getKnowledgeBase().getAllKnowledgeSlicesFor(
							StateTransition.KNOWLEDGE_KIND)) {

						for (ValueTransition vt : st.getPostTransitions()) {
							if (vt.getQuestion() == condEqual.getQuestion()) {
								for (ConditionalValueSetter cvs : vt.getSetters()) {
									if (cvs.getAnswer().equals(condEqual.getValue())) {
										Target target = targetMap.get(st.getQcontainer());
										if (target != null) {
											log.info("Increasing Benefit for " + target
													+ " from " + target.getBenefit() + " to "
													+ (target.getBenefit() + additiveValue));
											searchModel.maximizeBenefit(target, target.getBenefit()
													+ additiveValue);
										}
										else {
											target = new Target(Arrays.asList(st.getQcontainer()));
											target.setBenefit(additiveValue);
											log.info("Creating new target " + target
													+ " with benefit " + target.getBenefit());
											targetMap.put(st.getQcontainer(), target);
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
		log.info("Total Benefit/Cost: " + totalbenefit);
		log.info("BS Zustand Benefit/Cost: " + conditionValueCache);
	}

	private static void fillConditionValueCache(HashMap<Condition, Double> conditionValueCache, Target t) {
		StateTransition stateTransition = StateTransition.getStateTransition(
					t.getQContainers().get(0));
		if (stateTransition == null) return;
		Condition activationCondition = stateTransition.getActivationCondition();
		List<Condition> terms = new LinkedList<Condition>();
		// Expand ands
		if (activationCondition instanceof CondAnd) {
			CondAnd condAnd = (CondAnd) activationCondition;
			terms.addAll(condAnd.getTerms());
		}
		else {
			terms.add(activationCondition);
		}
		// invert ContNots containing a CondEqual of QuestionOCs
		for (Condition c : new LinkedList<Condition>(terms)) {
			if (c instanceof CondNot) {
				CondNot condNot = (CondNot) c;
				if (condNot.getTerms().get(0) instanceof CondEqual) {
					CondEqual condEqual = (CondEqual) condNot.getTerms().get(0);
					if (condEqual.getQuestion() instanceof QuestionOC
								&& condEqual.getValue() instanceof ChoiceValue) {
						ChoiceValue value = (ChoiceValue) condEqual.getValue();
						List<Condition> conds = new LinkedList<Condition>();
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
		for (Condition condition : new LinkedList<Condition>(terms)) {
			if (condition instanceof CondOr) {
				CondOr condOr = (CondOr) condition;
				terms.remove(condOr);
				terms.addAll(condOr.getTerms());
			}
		}
		for (Condition c : terms) {
			Double value = conditionValueCache.get(c);
			if (value == null) {
				conditionValueCache.put(c, t.getBenefit() / t.getCosts());
			}
			else {
				conditionValueCache.put(c, t.getBenefit() / t.getCosts() + value);
			}
		}
	}

	private static List<Question> getFinalQuestions(Session session) {
		List<Question> finalQuestions = new LinkedList<Question>();
		for (Question question : session.getKnowledgeBase().getManager().getQuestions()) {
			Boolean value = question.getInfoStore().getValue(FINAL_QUESTION);
			if (value) {
				finalQuestions.add(question);
			}
		}
		return finalQuestions;
	}

	/**
	 * @created 25.11.2011
	 * @param session Actual Session
	 * @param qContainer {@link QContainer}
	 * @return true when the {@link QContainer} cannot be applied due to
	 *         finalQuestions
	 */
	private boolean checkFinalQuestions(Session session, QContainer qContainer) {
		StateTransition stateTransition = StateTransition.getStateTransition(qContainer);
		if (stateTransition == null) {
			return false;
		}
		Condition activationCondition = stateTransition.getActivationCondition();
		if (activationCondition == null) {
			return false;
		}
		Session emptySession = new CopiedSession(session.getKnowledgeBase());
		for (Question q : session.getKnowledgeBase().getManager().getQuestions()) {
			if (q.getInfoStore().getValue(FINAL_QUESTION)) {
				// check if q has not the init value
				Value initValue = PSMethodInit.getValue(q,
						q.getInfoStore().getValue(BasicProperties.INIT));
				Value actualValue = session.getBlackboard().getValue(q);
				if (!initValue.equals(actualValue)) {
					emptySession.getBlackboard().addValueFact(
							FactFactory.createUserEnteredFact(q, actualValue));
				}
			}
		}
		// now all unmutable facts are added to the emptySession
		try {
			return (!activationCondition.eval(emptySession));
		}
		catch (NoAnswerException e) {
			return false;
		}
		catch (UnknownAnswerException e) {
			return false;
		}
	}

	/**
	 * Activates a ready-made path by indicating its questionnaires and storing
	 * it into the specified case object
	 * 
	 * @created 08.03.2011
	 * @param caseObject the case object for this cost benefit session
	 * @param path the path to be activated
	 */
	private void activatePath(CostBenefitCaseObject caseObject, Path path) {
		Session session = caseObject.getSession();
		Collection<QContainer> qContainers = path.getPath();
		QContainer[] currentSequence = new QContainer[qContainers.size()];
		int i = 0;
		List<Fact> facts = new LinkedList<Fact>();
		for (QContainer qContainer : qContainers) {
			currentSequence[i] = qContainer;
			makeOKQuestionsUndone(currentSequence[i], session);
			i++;
			Fact fact = FactFactory.createFact(session, qContainer,
					new Indication(State.INDICATED), this, this);
			facts.add(fact);
			session.getBlackboard().addInterviewFact(fact);
		}
		caseObject.setCurrentSequence(currentSequence);
		caseObject.setCurrentPathIndex(-1);
		caseObject.setIndicatedFacts(facts);
	}

	private List<StrategicSupport> getStrategicSupports(Session session) {
		List<StrategicSupport> ret = new ArrayList<StrategicSupport>();
		for (PSMethod psm : session.getPSMethods()) {
			if (psm instanceof StrategicSupport) {
				ret.add((StrategicSupport) psm);
			}
		}
		return ret;
	}

	private void makeOKQuestionsUndone(TerminologyObject container, Session session) {
		for (TerminologyObject nob : container.getChildren()) {
			// if ok-question
			if (nob instanceof QuestionOC) {
				QuestionOC qoc = (QuestionOC) nob;
				List<Choice> choices = qoc.getAllAlternatives();
				if (choices.size() == 1
						&& PATTERN_OK_CHOICE.matcher(choices.get(0).getName()).matches()) {
					Blackboard blackboard = session.getBlackboard();
					if (UndefinedValue.isNotUndefinedValue(blackboard.getValue(qoc))) {

						Fact fact = FactFactory.createUserEnteredFact(qoc,
								UndefinedValue.getInstance());

						blackboard.addValueFact(fact);
					}
				}
			}
			makeOKQuestionsUndone(nob, session);
		}
	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {
		CostBenefitCaseObject caseObject = session.getSessionObject(this);
		Set<QContainer> answeredQuestionnaires = new HashSet<QContainer>();
		List<QContainer> sequence = caseObject.getCurrentSequence() != null
				? Arrays.asList(caseObject.getCurrentSequence())
				: new LinkedList<QContainer>();
		for (PropagationEntry entry : changes) {
			TerminologyObject object = entry.getObject();
			if (!entry.isStrategic() && entry.hasChanged() && object instanceof Question) {
				Util.addParentContainers(answeredQuestionnaires, object);
			}
			if (entry.isStrategic() && entry.hasChanged() && object instanceof QContainer) {
				Indication indication = (Indication) entry.getNewValue();
				if (indication.isContraIndicated() && sequence.contains(object)) {
					calculateNewPath(caseObject);
					activateNextQContainer(caseObject);
					return;
				}
			}
		}
		// only proceed if we have received reals answers
		if (answeredQuestionnaires.isEmpty()) return;

		// for every finished QContainter fire its post transitions
		boolean isAnyQuesionnaireDone = false;
		for (QContainer qcon : answeredQuestionnaires) {
			if (Util.isDone(qcon, session)) {
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
			calculateNewPath(caseObject);
			activateNextQContainer(caseObject);
			return;
		}

		// cost benefit only requires to act after at least one indicated
		// questionnaire has been completed
		if (!isAnyQuesionnaireDone) return;

		// 2. check if there are any changed to our remembered solutions
		if (hasChangedUndiscriminatedSolutions(caseObject)) {
			calculateNewPath(caseObject);
			activateNextQContainer(caseObject);
			return;
		}

		// continue with our current sequence
		// if current qcontainer is done
		// recalculate if next step of sequence is not applicable
		activateNextQContainer(caseObject);
	}

	/**
	 * Returns if the undiscriminated solutions have changed since the last use
	 * of the search algorithm. This indicates that a new search should be
	 * performed to adapt to the new diagnostic situation.
	 * 
	 * @created 08.03.2011
	 * @param caseObject the cost benefit case object to be checked for changed
	 *        solutions
	 * @return if the undiscriminated solutions have been changed
	 */
	private boolean hasChangedUndiscriminatedSolutions(CostBenefitCaseObject caseObject) {
		// if the current set of undiscriminated solutions is null
		// this indicated that we will not check for undiscriminated solutions
		// at all
		if (caseObject.getUndiscriminatedSolutions() == null) return false;

		// otherwise calculate the current solution to be discriminated and
		// compare them to the previous ones
		Session session = caseObject.getSession();
		HashSet<Solution> currentSolutions = new HashSet<Solution>();
		for (StrategicSupport strategicSupport : getStrategicSupports(session)) {
			currentSolutions.addAll(strategicSupport.getUndiscriminatedSolutions(session));
		}
		final Set<Solution> previousSolutions = caseObject.getUndiscriminatedSolutions();
		return !previousSolutions.equals(currentSolutions);
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

	public void setSearchAlgorithm(SearchAlgorithm searchAlgorithm) {
		this.searchAlgorithm = searchAlgorithm;
	}

	public void setTargetFunction(TargetFunction targetFunction) {
		this.targetFunction = targetFunction;
	}

	public void setCostFunction(CostFunction costFunction) {
		this.costFunction = costFunction;
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
}
