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
import java.util.Map.Entry;
import java.util.Set;

import com.denkbares.utils.Log;
import com.denkbares.utils.Pair;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.Conditions;
import de.d3web.core.inference.condition.NonTerminalCondition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.abnormality.Abnormality;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.costbenefit.CostBenefitUtil;
import de.d3web.costbenefit.blackboard.DecoratedSession;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.inference.ValueTransition;
import de.d3web.costbenefit.model.Path;
import de.d3web.costbenefit.model.SearchModel;
import de.d3web.costbenefit.model.Target;

/**
 * Uses a "slice model" to calculate a more precise distance
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 30.09.2011
 */
public class TPHeuristic extends DividedTransitionHeuristic {

	public static class TPHeuristicSessionObject extends DividedTransitionHeuristicSessionObject {

		/**
		 * Stores for each CondEqual a Pair of Conditions and QContainers. If the condition is false when starting a
		 * search and none of the QContainers is in the actual path, the conditions can be added to the condequal if
		 * there are no conflicts (e.g. same termobjects in an CondOr and CondEqual)
		 */
		private final Map<Condition, Pair<List<Condition>, Set<QContainer>>> preconditionCache = new HashMap<>();

		private Collection<Question> cachedAbnormalQuestions = new HashSet<>();

		private Collection<QContainer> blockedQContainer = new HashSet<>();
	}

	@Override
	public void init(SearchModel model) {
		TPHeuristicSessionObject sessionObject = (TPHeuristicSessionObject) model.getSession().getSessionObject(this);
		if (sessionObject.initializedModel == model) return;

		// KB has to be remembered before super.init
		KnowledgeBase oldkb = sessionObject.knowledgeBase;
		super.init(model);
		// initgeneral is only called when the kb, the blocked QContainers or
		// the list of cached abnormal
		// questions changes
		if (model.getSession().getKnowledgeBase() != oldkb
				|| !model.getBlockedQContainers().equals(sessionObject.blockedQContainer)) {
			initGeneralCache(sessionObject, model);
			// knowledbase gets updated in super.init(model)
			sessionObject.cachedAbnormalQuestions = calculateAnsweredAbnormalQuestions(model);
			sessionObject.blockedQContainer = model.getBlockedQContainers();
		}
		else {
			Set<Question> answeredAbnormalQuestions = calculateAnsweredAbnormalQuestions(model);
			if (!answeredAbnormalQuestions.equals(sessionObject.cachedAbnormalQuestions)) {
				sessionObject.cachedAbnormalQuestions = answeredAbnormalQuestions;
				initGeneralCache(sessionObject, model);
				sessionObject.blockedQContainer = model.getBlockedQContainers();
			}
		}
	}

	private static Set<Question> calculateAnsweredAbnormalQuestions(SearchModel model) {
		Set<Question> result = new HashSet<>();
		Blackboard blackboard = model.getSession().getBlackboard();
		for (StateTransition st : model.getTransitionalStateTransitions()) {
			for (Question q : CostBenefitUtil.getQuestionOCs(st.getQcontainer())) {
				DefaultAbnormality abnormality = q.getInfoStore().getValue(
						BasicProperties.DEFAULT_ABNORMALITY);
				Value value = blackboard.getValue(q);
				if (UndefinedValue.isNotUndefinedValue(value)) {
					boolean abnormal = (abnormality == null) || abnormality.getValue(value) == Abnormality.A5;
					if (abnormal) {
						result.add(q);
					}
				}
			}
		}
		return result;
	}

	private static List<Condition> getPrimitiveConditions(List<Pair<List<Condition>, Set<QContainer>>> temppairs, Set<Condition> alreadyExaminedConditions) {
		List<Condition> list = new LinkedList<>();
		for (Pair<List<Condition>, Set<QContainer>> p : temppairs) {
			for (Condition cond : p.getA()) {
				if (cond instanceof CondEqual) {
					if (!alreadyExaminedConditions.contains(cond)) list.add(cond);
				}
				else if (cond instanceof CondOr) {
					if (checkCondOr((CondOr) cond)) {
						if (!alreadyExaminedConditions.contains(cond)) list.add(cond);
					}
				}
				else if (cond instanceof CondNot) {
					if (checkCondNot((CondNot) cond)) {
						if (!alreadyExaminedConditions.contains(cond)) list.add(cond);
					}
				}
			}
		}
		alreadyExaminedConditions.addAll(list);
		return list;
	}

	private static List<Pair<List<Condition>, Set<QContainer>>> getPairs(TPHeuristicSessionObject sessionObject, Session session, Collection<? extends Condition> targetConditions, Map<Question, Set<Value>> forbiddenValues, boolean skipTrueConds) {
		List<Pair<List<Condition>, Set<QContainer>>> additionalConditions = new LinkedList<>();
		for (Condition cond : targetConditions) {
			if (skipTrueConds && Conditions.isTrue(cond, session)) continue;
			Pair<List<Condition>, Set<QContainer>> generalPair = sessionObject.preconditionCache.get(cond);
			if (generalPair == null) continue;
			List<Condition> checkedConditions = new LinkedList<>();
			for (Condition precondition : generalPair.getA()) {
				if (Conditions.isTrue(precondition, session)) continue;
				if (precondition instanceof CondEqual) {
					CondEqual condEqual = (CondEqual) precondition;
					// condequals must not be already contained and must
					// not have a forbidden termobject
					Set<Value> set = forbiddenValues.get(condEqual.getQuestion());
					if (set == null || !set.contains(condEqual.getValue())) {
						checkedConditions.add(condEqual);
					}
				}
				else {
					Map<Question, Set<Value>> preconditionValues = getCoveredValues(precondition);
					boolean conflicting = false;
					for (Question q : preconditionValues.keySet()) {
						Set<Value> forbiddenSet = forbiddenValues.get(q);
						if (forbiddenSet != null
								&& !Collections.disjoint(forbiddenSet, preconditionValues.get(q))) {
							conflicting = true;
							break;
						}
					}
					if (!conflicting) {
						checkedConditions.add(precondition);
					}
				}
			}
			if (!checkedConditions.isEmpty()) {
				additionalConditions.add(new Pair<>(
						checkedConditions, generalPair.getB()));
			}
		}
		return additionalConditions;
	}

	private static void initGeneralCache(TPHeuristicSessionObject sessionObject, SearchModel model) {
		long time = System.currentTimeMillis();
		sessionObject.preconditionCache.clear();
		KnowledgeBase kb = model.getSession().getKnowledgeBase();
		Collection<StateTransition> transitiveStateTransitions = model.getTransitionalStateTransitions();
		Collection<StateTransition> stateTransitions = new LinkedList<>();
		Set<QContainer> blockedQContainers = model.getBlockedQContainers();
		// filter StateTransitions that cannot be applied due to final questions
		for (StateTransition st : StateTransition.getAll(kb)) {
			QContainer qcontainer = st.getQcontainer();
			if (!blockedQContainers.contains(qcontainer)) {
				stateTransitions.add(st);
			}
		}
		// collect all CondEqual preconditions of all statetransitions (CondAnd
		// are splitted, other condition types can not be used to extend
		// condition
		Set<Condition> preconditions = new HashSet<>();
		for (StateTransition st : stateTransitions) {
			preconditions.addAll(getPrimitiveConditions(st.getActivationCondition()));
		}
		for (Condition condition : preconditions) {
			// collect all conditions of state transitions establishing a state
			// that enables
			// execution of the condEqual
			LinkedList<List<Condition>> neededConditions = new LinkedList<>();
			Set<QContainer> transitionalQContainer = new HashSet<>();
			for (StateTransition st : transitiveStateTransitions) {
				for (ValueTransition vt : st.getPostTransitions()) {
					if (vt.getQuestion() == condition.getTerminalObjects().iterator().next()) {
						Value v = getValue(sessionObject, vt);
						if (checkValue(condition, v)) {
							neededConditions.add(flattenCondAnds(st.getActivationCondition()));
							transitionalQContainer.add(st.getQcontainer());
							break;
						}
					}
				}
			}
			// TODO: add a hashmap finalquestion -> conditions which must be
			// recalculated
			// put all common conditions in the cache
			sessionObject.preconditionCache.put(condition,
					new Pair<>(
							getCommonConditions(neededConditions),
							transitionalQContainer));
		}
		Log.info("General init: " + (System.currentTimeMillis() - time) + "ms");
	}

	/**
	 * Checks if the the Value v can be used to fullfill the condition. This method can only handle primitive
	 * conditions
	 *
	 * @param condition specified condition
	 * @param v         Value
	 * @return true if the value can fullfill the condition
	 * @created 04.07.2012
	 * @see #getPrimitiveConditions(Condition)
	 */
	public static boolean checkValue(Condition condition, Value v) {
		if (condition instanceof CondEqual) {
			return ((CondEqual) condition).getValue().equals(v);
		}
		else if (condition instanceof CondOr && (v instanceof ChoiceValue)) {
			List<ChoiceID> choiceIDs = getChoiceIDs((CondOr) condition);
			ChoiceValue cv = (ChoiceValue) v;
			return choiceIDs.contains(cv.getChoiceID());
		}
		else if (condition instanceof CondNot && (v instanceof ChoiceValue)) {
			ChoiceValue cv = (ChoiceValue) v;
			CondNot condNot = (CondNot) condition;
			CondEqual condEqual = (CondEqual) condNot.getTerms().get(0);
			return !(cv.getChoiceID().equals(((ChoiceValue) condEqual.getValue()).getChoiceID()));
		}
		return false;
	}

	private static List<Condition> getCommonConditions(LinkedList<List<Condition>> neededConditions) {
		// take the first list and check if all other lists contain it's
		// conditions
		List<Condition> first = neededConditions.poll();
		List<Condition> commonConditions = new LinkedList<>();
		if (first != null) {
			first:
			for (Condition c : first) {
				for (List<Condition> ref : neededConditions) {
					if (!ref.contains(c)) {
						continue first;
					}
				}
				// all reference condition list contain c
				commonConditions.add(c);
			}
		}
		return commonConditions;
	}

	/**
	 * Returns all primitive subconditions of the specified condition. CondAnd are splitted. CondOr are defined as
	 * primitive, when their subterms are all CondEqual of QuestionOC. CondNot are primitive, when they have exactly one
	 * CondEqual of a QuestionOC as subcondition. CondEqual are always primitive.
	 *
	 * @param cond specified Condition
	 * @return list of primitive conditions
	 */
	public static List<Condition> getPrimitiveConditions(Condition cond) {
		List<Condition> conds = new LinkedList<>();
		if (cond instanceof CondEqual) {
			conds.add(cond);
		}
		else if (cond instanceof CondOr) {
			CondOr condOr = (CondOr) cond;
			if (checkCondOr(condOr)) {
				conds.add(condOr);
			}
		}
		else if (cond instanceof CondNot) {
			CondNot condNot = (CondNot) cond;
			if (checkCondNot(condNot)) {
				conds.add(condNot);
			}
		}
		else if (cond instanceof CondAnd) {
			CondAnd condAnd = (CondAnd) cond;
			for (Condition c : condAnd.getTerms()) {
				conds.addAll(getPrimitiveConditions(c));
			}
		}
		return conds;
	}

	/**
	 * Checks if all terms of the specified CondOr are {@link CondEqual}s of QuestionOC
	 *
	 * @created 22.03.2012
	 */
	private static boolean checkCondOr(CondOr condOr) {
		Question question = null;
		boolean accept = true;
		for (Condition c : condOr.getTerms()) {
			if (!(c instanceof CondEqual)) {
				accept = false;
				break;
			}
			CondEqual condEqual = (CondEqual) c;
			if (question == null) {
				question = condEqual.getQuestion();
			}
			else {
				if (question != condEqual.getQuestion()) {
					accept = false;
					break;
				}
			}
			if (!(condEqual.getQuestion() instanceof QuestionOC && condEqual.getValue() instanceof ChoiceValue)) {
				accept = false;
				break;
			}
		}
		return accept;
	}

	/**
	 * Checks if the specified {@link CondNot} contains only one CondEqual of a ChoiceValue
	 *
	 * @created 22.03.2012
	 */
	private static boolean checkCondNot(CondNot condNot) {
		if (condNot.getTerms().size() == 1) {
			Condition negatedCondition = condNot.getTerms().get(0);
			if (negatedCondition instanceof CondEqual) {
				CondEqual condEqual = (CondEqual) negatedCondition;
				return (condEqual.getValue() instanceof ChoiceValue);
			}
		}
		return false;
	}

	private static List<ChoiceID> getChoiceIDs(CondOr condOr) {
		List<ChoiceID> choices = new LinkedList<>();
		for (Condition c : condOr.getTerms()) {
			CondEqual condEqual = (CondEqual) c;
			ChoiceValue cv = (ChoiceValue) condEqual.getValue();
			choices.add(cv.getChoiceID());
		}
		return choices;
	}

	private static List<Condition> flattenCondAnds(Condition cond) {
		List<Condition> conds = new LinkedList<>();
		if (cond instanceof CondAnd) {
			CondAnd condAnd = (CondAnd) cond;
			for (Condition c : condAnd.getTerms()) {
				conds.addAll(flattenCondAnds(c));
			}
		}
		else if (cond != null) {
			conds.add(cond);
		}
		return conds;
	}

	@Override
	public double getDistance(SearchModel model, Path path, State state, Condition target) {
		Condition condition = getTransitiveCondition(model.getSession(), target, state.getSession());
		List<Condition> flattenedConditions = flattenCondAnds(condition);
		condition = new CondAnd(flattenedConditions);
		DividedTransitionHeuristicSessionObject sessionObject = model.getSession().getSessionObject(this);
		return estimatePathCosts(sessionObject, state, condition)
				+ calculateUnusedNegatives(sessionObject, path);

		//		AStarAlgorithm alogrithm = CostBenefitUtil.getAStarAlogrithm(expertMode.getProblemSolver());
//		TPHeuristic heuristic = (TPHeuristic) alogrithm.getHeuristic();
//		DividedTransitionHeuristic.DividedTransitionHeuristicSessionObject sessionObject = session.getSessionObject(heuristic);
//		State startState = alogrithm.getExplanationComponent(session).astar.getStartNode().state;
//		TPHeuristic.estimatePathCosts(sessionObject, startState, new CondEqual(info.question, value))
	}

	/**
	 * Creates a transitive condition for the specified stateTransition.
	 * <p>
	 * Important note: The heuristic has to be initialized based on a search model, containing the qcontainer of the
	 * StateTransition as a target. If no model is created yet, use the method calculateTransitiveCondition(Session,
	 * QContainer)
	 *
	 * @param sessionContainingObject the actual session
	 * @param stateTransition         specified {@link StateTransition}
	 * @return transitive activation condition
	 * @created 05.07.2012
	 */
	public Condition getTransitiveCondition(Session sessionContainingObject, StateTransition stateTransition, Session sessionRepresentingTheActualState) {
		return getTransitiveCondition(sessionContainingObject, stateTransition.getActivationCondition(), sessionRepresentingTheActualState);
	}

	/**
	 * Creates a transitive condition for the specified condition.
	 * <p>
	 * Important note: The heuristic has to be initialized based on a search model, containing the qcontainer of the
	 * StateTransition as a target. If no model is created yet, use the method calculateTransitiveCondition(Session,
	 * QContainer)
	 *
	 * @param session      the actual session
	 * @param precondition specified {@link StateTransition}
	 * @return transitive activation condition
	 * @created 05.07.2012
	 */
	public Condition getTransitiveCondition(Session session, Condition precondition) {
		Session root = session instanceof DecoratedSession ? ((DecoratedSession) session).getRootSession() : session;
		return getTransitiveCondition(root, precondition, session);
	}

	/**
	 * Creates a transitive condition for the specified stateTransition.
	 * <p>
	 * Important note: The heuristic has to be initialized based on a search model, containing the qcontainer of the
	 * StateTransition as a target. If no model is created yet, use the method calculateTransitiveCondition(Session,
	 * QContainer)
	 *
	 * @param sessionContainingObject the actual session
	 * @param precondition            specified {@link StateTransition}
	 * @return transitive activation condition
	 * @created 05.07.2012
	 */
	public Condition getTransitiveCondition(Session sessionContainingObject, Condition precondition, Session sessionRepresentingTheActualState) {
		TPHeuristicSessionObject sessionObject = (TPHeuristicSessionObject) sessionContainingObject.getSessionObject(this);
		return getTransitiveCondition(sessionObject, precondition, sessionRepresentingTheActualState);
	}

	/**
	 * Creates a transitive condition for the specified stateTransition.
	 * <p>
	 * Important note: The heuristic has to be initialized based on a search model, containing the qcontainer of the
	 * StateTransition as a target. If no model is created yet, use the method calculateTransitiveCondition(Session,
	 * QContainer)
	 *
	 * @param sessionObject the actual session object of this heuristic
	 * @param precondition  specified {@link StateTransition}
	 * @return transitive activation condition
	 * @created 05.07.2012
	 */
	private Condition getTransitiveCondition(TPHeuristicSessionObject sessionObject, Condition precondition, Session session) {
		if (precondition == null) return new CondAnd();
		Map<Question, Condition> originalFullfilledConditions = new HashMap<>();
		Map<Question, Condition> originalUnFullfilledConditions = new HashMap<>();

		// use a set to filter duplicated conditions
		List<Condition> originalPrimitiveConditions = getPrimitiveConditions(precondition);
		Map<Question, Set<Value>> forbiddenValues = getCoveredValues(precondition);
		List<Condition> conditionsToExamine = new LinkedList<>();

		for (Condition condition : originalPrimitiveConditions) {
			boolean conditionFullfilled = Conditions.isTrue(condition, session);
			if (condition.getTerminalObjects().size() == 1) {
				TerminologyObject object = condition.getTerminalObjects().iterator().next();
				if (object instanceof Question) {
					if (conditionFullfilled) {
						originalFullfilledConditions.put((Question) object, condition);
					}
					else {
						originalUnFullfilledConditions.put((Question) object, condition);
					}
				}
			}
			if (!conditionFullfilled) {
				conditionsToExamine.add(condition);
			}
		}
		Set<Condition> conditions = getPreparingConditions(session,
				sessionObject, forbiddenValues, conditionsToExamine);

		Condition condition;
		if (conditions.isEmpty()) {
			condition = precondition;
		}
		else {
			List<Condition> conditionsToUse = new LinkedList<>();
			// add the original condition
			conditionsToUse.add(precondition);
			Set<Condition> conflictingfullfilledOriginalConds = new HashSet<>();
			Set<Condition> conflictingUnfullfilledOriginalConds = new HashSet<>();
			addConditionsNotConflicting(session, sessionObject,
					conditions, conditionsToUse, originalFullfilledConditions,
					conflictingfullfilledOriginalConds, originalUnFullfilledConditions,
					conflictingUnfullfilledOriginalConds);
			condition = new CondAnd(conditionsToUse);
			// if a original precondition is fullfilled, but we have expanded a
			// condition conflicting with it, it will be destroyed during the
			// path, so we have to add its preparing transitions again (if not
			// fullfilled)
			if (!conflictingfullfilledOriginalConds.isEmpty()) {
				Set<Condition> conditionsPreparingConflictingOriginalConditions = getPreparingConditions(
						session, sessionObject,
						getCoveredValues(condition),
						conflictingfullfilledOriginalConds);
				addConditionsNotConflicting(session, sessionObject,
						conditionsPreparingConflictingOriginalConditions,
						conditionsToUse, null, null, originalUnFullfilledConditions,
						conflictingUnfullfilledOriginalConds);
				condition = new CondAnd(conditionsToUse);
			}
			// add fullfilled conditions preparing conflicting original
			// conditions (Reason: if F1=C is part of the original condition, we
			// expand F1=A and F1=B is needed to prepare F1=C, then F1=B can
			// also be added).
			if (!conflictingUnfullfilledOriginalConds.isEmpty()) {
				Map<Question, Set<Value>> coveredValueMap = getCoveredValues(condition);
				for (Condition conflicting : conflictingUnfullfilledOriginalConds) {
					Pair<List<Condition>, Set<QContainer>> pair = sessionObject.preconditionCache.get(conflicting);
					Question question = (Question) conflicting.getTerminalObjects().iterator().next();
					for (Condition candidate : pair.getA()) {
						if (candidate.getTerminalObjects().size() == 1
								&& Conditions.isTrue(candidate, session)) {
							TerminologyObject candidateQuestion = candidate.getTerminalObjects().iterator().next();
							if (question != candidateQuestion) {
								// actually all candidateQuestions not being
								// identically to the question should be
								// skipped, but only those get skipped
								// conflicting to original unfullfilled
								// conditions => CAUTION: the optimistic of the
								// heuristic can be harmed by having this
								// condition
								if (originalUnFullfilledConditions.get(candidateQuestion) != null) {
									continue;
								}
							}
							Set<Value> coveredValues = coveredValueMap.get(candidateQuestion);
							Set<Value> coveredCandidateValues = getCoveredValues(candidate).get(candidateQuestion);
							if (coveredValues == null
									|| Collections.disjoint(coveredValues, coveredCandidateValues)) {
								conditionsToUse.add(candidate);
								// this is very rare in one call, so we can
								// directly update the condition and the covered
								// values
								condition = new CondAnd(conditionsToUse);
								coveredValueMap = getCoveredValues(condition);
								// even try adding recursively more conditions
								// not covering already covered values, in this
								// case, the candidate must refer to the same
								// question (otherwise the fullfilled condition
								// could be fullfilled during the complete path,
								// so it does not have to be established again)
								if (question == candidateQuestion) {
									coveredValues = coveredValueMap.get(question);
									LinkedList<Condition> found = new
											LinkedList<>();
									found.add(candidate);
									while (!found.isEmpty()) {
										Pair<List<Condition>, Set<QContainer>> recursivePair =
												sessionObject.preconditionCache.get(found.pop());
										for (Condition recursiveCandidate : recursivePair.getA()) {
											if (recursiveCandidate.getTerminalObjects().size()
													== 1
													&&
													recursiveCandidate.getTerminalObjects().iterator().next()
															== question) {
												Set<Value> coveredRecursiveValues =
														getCoveredValues(
																recursiveCandidate).get(question);
												if (Collections.disjoint(coveredValues,
														coveredRecursiveValues)) {
													conditionsToUse.add(recursiveCandidate);
													// this is very, very rare
													// in
													// one call, so we can
													// directly update the
													// condition
													// and the covered
													// values
													condition = new CondAnd(conditionsToUse);
													coveredValueMap =
															getCoveredValues(condition);
													coveredValues =
															coveredValueMap.get(question);
													found.add(recursiveCandidate);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return condition;
	}

	/**
	 * Adds conditions not conflicting with other potential conditions to conditionToUse
	 *
	 * @created 30.01.2013
	 */
	private void addConditionsNotConflicting(Session sessionRepresentingTheActualState, TPHeuristicSessionObject sessionObject, Set<Condition> conditions, List<Condition> conditionsToUse, Map<Question, Condition> originalFullfilledConditions, Set<Condition> conflictingFullfilledOriginalConds, Map<Question, Condition> originalUnFullfilledConditions, Set<Condition> conflictingUnfullfilledOriginalConds) {
		Set<Condition> nonCondEqual = new HashSet<>();
		for (Condition additionalCondition : conditions) {
			if (Conditions.isTrue(additionalCondition, sessionRepresentingTheActualState)) continue;
			// adding condequal is preferred, because they are less
			// conflicting (covering only one value)
			if (additionalCondition instanceof CondEqual) {
				conditionsToUse.add(additionalCondition);
				Question question = ((CondEqual) additionalCondition).getQuestion();
				updateConflictingConditions(originalFullfilledConditions,
						conflictingFullfilledOriginalConds, originalUnFullfilledConditions,
						conflictingUnfullfilledOriginalConds, question);
			}
			else {
				nonCondEqual.add(additionalCondition);
			}
		}
		Set<Condition> blacklist = new HashSet<>();
		additional:
		for (Condition additionalCondition : nonCondEqual) {
			if (blacklist.contains(additionalCondition)) {
				continue;
			}
			Map<Question, Set<Value>> coveredValues = getCoveredValues(additionalCondition);
			// the term objects of the other conditions must be disjunct
			boolean disjunct = true;
			// NOTE: usually there is only one entry
			for (Entry<Question, Set<Value>> entry : coveredValues.entrySet()) {
				for (Condition reference : conditions) {
					if (reference != additionalCondition) {
						Map<Question, Set<Value>> reverenceCoveredValues = getCoveredValues(
								reference);
						Set<Value> referenceValues = reverenceCoveredValues.get(entry.getKey());
						if (referenceValues != null) {
							if (!Collections.disjoint(entry.getValue(), referenceValues)) {
								// adding of this condition could violate
								// the
								// optimism, continue with next condition
								// reference condition cannot be used either
								blacklist.add(reference);
								continue additional;
							}
						}
					}
				}
			}
			// if the terms objects are disjunct from all other
			// additional conditions, the condition can be added
			if (disjunct) {
				conditionsToUse.add(additionalCondition);
				if (additionalCondition.getTerminalObjects().size() == 1) {
					TerminologyObject object = additionalCondition.getTerminalObjects().iterator().next();
					if (object instanceof Question) {
						Question question = (Question) object;
						updateConflictingConditions(originalFullfilledConditions,
								conflictingFullfilledOriginalConds, originalUnFullfilledConditions,
								conflictingUnfullfilledOriginalConds, question);
					}
				}
			}
		}
	}

	private void updateConflictingConditions(Map<Question, Condition> originalFullfilledConditions, Set<Condition> conflictingFullfilledOriginalConds, Map<Question, Condition> originalUnFullfilledConditions, Set<Condition> conflictingUnfullfilledOriginalConds, Question question) {
		if (originalFullfilledConditions != null) {
			Condition conflicting = originalFullfilledConditions.get(question);
			if (conflicting != null) {
				conflictingFullfilledOriginalConds.add(conflicting);
			}
		}
		if (originalUnFullfilledConditions != null) {
			Condition conflicting = originalUnFullfilledConditions.get(question);
			if (conflicting != null) {
				conflictingUnfullfilledOriginalConds.add(conflicting);
			}
		}
	}

	private Set<Condition> getPreparingConditions(Session sessionRepresentingTheActualState, TPHeuristicSessionObject sessionObject, Map<Question, Set<Value>> forbiddenValues, Collection<Condition> conditionsToExamine) {
		Set<Condition> conditions = new HashSet<>();
		Set<Condition> alreadyExaminedConditions = new HashSet<>();
		while (!conditionsToExamine.isEmpty()) {
			// all conditions in conditionToExamine are false or conflicting
			// original conditions, it doesn't
			// have to be checked again
			List<Pair<List<Condition>, Set<QContainer>>> temppairs = getPairs(
					sessionObject, sessionRepresentingTheActualState,
					conditionsToExamine, forbiddenValues,
					false);
			for (Pair<List<Condition>, Set<QContainer>> pair : temppairs) {
				conditions.addAll(pair.getA());
			}
			conditionsToExamine = getPrimitiveConditions(temppairs,
					alreadyExaminedConditions);
		}
		return conditions;
	}

	/**
	 * Returns the questions of the specified condition, together with all values that potentially may (not necessarily
	 * will) bring the condition to true.
	 *
	 * @param condition the condition to be examined
	 * @return the questions and their positively covered values
	 */
	public static Map<Question, Set<Value>> getCoveredValues(Condition condition) {
		Map<Question, Set<Value>> result = new HashMap<>();
		getCoveredValues(condition, result);
		return result;
	}

	private static void getCoveredValues(Condition condition, Map<Question, Set<Value>> collator) {
		if (condition instanceof CondEqual) {
			CondEqual condEqual = (CondEqual) condition;
			getSet(collator, condEqual.getQuestion()).add(condEqual.getValue());
		}
		else if (condition instanceof CondNot) {
			List<Condition> terms = ((CondNot) condition).getTerms();
			if (checkCondNot((CondNot) condition)) {
				CondEqual negatedCondition = (CondEqual) terms.get(0);
				Set<Value> set = getSet(collator, negatedCondition.getQuestion());
				for (ChoiceValue cv : getAllChoiceValues((QuestionChoice) negatedCondition.getQuestion())) {
					if (!cv.equals(negatedCondition.getValue())) {
						set.add(cv);
					}
				}
			}
			else {
				for (TerminologyObject to : condition.getTerminalObjects()) {
					if (to instanceof QuestionChoice) {
						Set<Value> set = getSet(collator, (Question) to);
						set.addAll(getAllChoiceValues((QuestionChoice) to));
					}
				}
			}
		}
		else if (condition instanceof CondOr || condition instanceof CondAnd) {
			for (Condition subcondition : ((NonTerminalCondition) condition).getTerms()) {
				getCoveredValues(subcondition, collator);
			}
		}
		else {
			for (TerminologyObject to : condition.getTerminalObjects()) {
				if (to instanceof QuestionChoice) {
					Set<Value> set = getSet(collator, (Question) to);
					set.addAll(getAllChoiceValues((QuestionChoice) to));
				}
			}
		}
	}

	private static Set<Value> getSet(Map<Question, Set<Value>> forbiddenValues, Question question) {
		return forbiddenValues.computeIfAbsent(question, k -> new HashSet<>());
	}

	private static List<ChoiceValue> getAllChoiceValues(QuestionChoice qc) {
		List<ChoiceValue> values = new LinkedList<>();
		for (Choice c : qc.getAllAlternatives()) {
			values.add(new ChoiceValue(c));
		}
		return values;
	}

	/**
	 * Calculates a transitive condition for the specified target based on the session.
	 *
	 * @param session actual session
	 * @param target  specified target
	 * @return transitive condition
	 * @created 04.07.2012
	 */
	public static Condition calculateTransitiveCondition(Session session, QContainer target) {
		TPHeuristic heuristic = new TPHeuristic();
		SearchModel model = new SearchModel(session);
		model.addTarget(new Target(target));
		heuristic.init(model);
		StateTransition stateTransition = StateTransition.getStateTransition(target);
		if (stateTransition == null) return new CondAnd();
		return heuristic.getTransitiveCondition(session, stateTransition.getActivationCondition());
	}

	@Override
	public TPHeuristicSessionObject createSessionObject(Session session) {
		return new TPHeuristicSessionObject();
	}
}
