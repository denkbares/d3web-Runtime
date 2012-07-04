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
import java.util.logging.Logger;

import de.d3web.core.inference.PSMethodInit;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.Conditions;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.utilities.Pair;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;
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

	private static final Logger log = Logger.getLogger(TPHeuristic.class.getName());

	/**
	 * Stores for each CondEqual a Pair of Conditions and QContainers. If the
	 * condition is false when starting a search and none of the QContainers is
	 * in the actual path, the conditions can be added to the condequal if there
	 * are no conflicts (e.g. same termobjects in an CondOr and CondEqual)
	 */
	private Map<Condition, Pair<List<Condition>, Set<QContainer>>> preconditionCache = new HashMap<Condition, Pair<List<Condition>, Set<QContainer>>>();

	/**
	 * Stores a List of Pairs of Conditions and QContainers establishing a state
	 * where the conditions of the targetQContainer is applicable
	 */
	private Map<QContainer, List<Pair<List<Condition>, Set<QContainer>>>> targetCache = new HashMap<QContainer, List<Pair<List<Condition>, Set<QContainer>>>>();

	private Collection<Question> cachedFinalQuestions = new HashSet<Question>();

	@Override
	public void init(SearchModel model) {
		// initgeneral in only called when the kb or the list of cached final
		// questions changes
		if (model.getSession().getKnowledgeBase() != knowledgeBase) {
			initGeneralCache(model);
			// knowledbase gets updated in super.init(model)
			cachedFinalQuestions = calculateAnsweredFinalQuestions(model);
		}
		else {
			HashSet<Question> answeredFinalQuestions = calculateAnsweredFinalQuestions(model);
			if (!answeredFinalQuestions.equals(cachedFinalQuestions)) {
				cachedFinalQuestions = answeredFinalQuestions;
				initGeneralCache(model);
			}
		}
		super.init(model);
		initTargetCache(model);
	}

	private HashSet<Question> calculateAnsweredFinalQuestions(SearchModel model) {
		HashSet<Question> answeredFinalQuestions = new HashSet<Question>();
		for (Question q : model.getSession().getKnowledgeBase().getManager().getQuestions()) {
			if (q.getInfoStore().getValue(PSMethodCostBenefit.FINAL_QUESTION)) {
				// check if q has not the init value
				Value initValue = PSMethodInit.getValue(q,
						q.getInfoStore().getValue(BasicProperties.INIT));
				Value actualValue = model.getSession().getBlackboard().getValue(q);
				// equality has to be checked, it is not sufficient to check the
				// sizes, because another session could have been loaded
				if (!initValue.equals(actualValue)) {
					answeredFinalQuestions.add(q);
				}
			}
		}
		return answeredFinalQuestions;
	}

	/**
	 * Inits preconditions per target.
	 * 
	 * Conditions can be used as Preconditions, if they are of type CondEqual,
	 * they are not already contained in the activation condition of the target
	 * and their term object is not contained in any partial condition of the
	 * activation condition other than CondEqual. They can also be used, if they
	 * are not type of CondEqual and all their term objects are not part of the
	 * activation condition.
	 * 
	 * @created 05.10.2011
	 * @param model {@link SearchModel}
	 */
	private void initTargetCache(SearchModel model) {
		long time = System.currentTimeMillis();
		targetCache.clear();
		Session session = model.getSession();
		for (Target target : model.getTargets()) {
			// iterate over all QContainers of all targets
			for (QContainer qcon : target.getQContainers()) {
				// skip qcontainer if it is already initialized
				if (targetCache.get(qcon) != null) continue;
				StateTransition st = StateTransition.getStateTransition(qcon);
				// qcontainers without Statetransition are handled separately
				if (st == null) continue;
				Condition activationCondition = st.getActivationCondition();
				Collection<TerminologyObject> forbiddenTermObjects = getForbiddenObjects(activationCondition);
				List<Condition> conditions = getPrimitiveConditions(activationCondition);
				List<Pair<List<Condition>, Set<QContainer>>> additionalConditions = new LinkedList<Pair<List<Condition>, Set<QContainer>>>();
				List<Condition> conditionsToExamine = conditions;
				while (!conditionsToExamine.isEmpty()) {
					List<Pair<List<Condition>, Set<QContainer>>> temppairs = getPairs(session,
							conditionsToExamine, conditions,
							activationCondition.getTerminalObjects(), forbiddenTermObjects);
					additionalConditions.addAll(temppairs);
					conditionsToExamine = getPrimitiveConditions(temppairs);
				}
				targetCache.put(qcon, additionalConditions);
			}
		}
		log.info("Target init: " + (System.currentTimeMillis() - time) + "ms");
	}

	private static List<Condition> getPrimitiveConditions(List<Pair<List<Condition>, Set<QContainer>>> temppairs) {
		List<Condition> list = new LinkedList<Condition>();
		for (Pair<List<Condition>, Set<QContainer>> p : temppairs) {
			for (Condition cond : p.getA()) {
				if (cond instanceof CondEqual) {
					list.add(cond);
				}
				else if (cond instanceof CondOr) {
					if (checkCondOr((CondOr) cond)) {
						list.add(cond);
					}
				}
				else if (cond instanceof CondNot) {
					if (checkCondNot((CondNot) cond)) {
						list.add(cond);
					}
				}
			}
		}
		return list;
	}

	private List<Pair<List<Condition>, Set<QContainer>>> getPairs(Session session, List<Condition> targetConditions, List<Condition> originalPrimitiveConditions, Collection<? extends TerminologyObject> collection, Collection<TerminologyObject> forbiddenTermObjects) {
		List<Pair<List<Condition>, Set<QContainer>>> additionalConditions = new LinkedList<Pair<List<Condition>, Set<QContainer>>>();
		for (Condition cond : targetConditions) {
			if (Conditions.isTrue(cond, session)) continue;
			Pair<List<Condition>, Set<QContainer>> generalPair = preconditionCache.get(cond);
			if (generalPair == null) continue;
			List<Condition> checkedConditions = new LinkedList<Condition>();
			for (Condition precondition : generalPair.getA()) {
				if (Conditions.isTrue(precondition, session)) continue;
				if (precondition instanceof CondEqual) {
					CondEqual condEqual = (CondEqual) precondition;
					// condequals must not be already contained and must
					// not have a forbidden termobject
					if (!originalPrimitiveConditions.contains(precondition)
							&& !forbiddenTermObjects.contains(condEqual.getQuestion())) {
						checkedConditions.add(condEqual);
					}
				}
				// all other conditions must not have any term object
				// already contained in the activation condition
				else if (Collections.disjoint(collection,
						precondition.getTerminalObjects())) {
					checkedConditions.add(precondition);
				}
			}
			if (!checkedConditions.isEmpty()) {
				additionalConditions.add(new Pair<List<Condition>, Set<QContainer>>(
						checkedConditions, generalPair.getB()));
			}
		}
		return additionalConditions;
	}

	private void initGeneralCache(SearchModel model) {
		long time = System.currentTimeMillis();
		preconditionCache.clear();
		KnowledgeBase kb = model.getSession().getKnowledgeBase();
		Collection<StateTransition> stateTransitions = new LinkedList<StateTransition>();
		// filter StateTransitions that cannot be applied due to final questions
		for (StateTransition st : kb.getAllKnowledgeSlicesFor(StateTransition.KNOWLEDGE_KIND)) {
			QContainer qcontainer = st.getQcontainer();
			Boolean targetOnly = qcontainer.getInfoStore().getValue(AStar.TARGET_ONLY);
			if (!targetOnly && !PSMethodCostBenefit.isBlockedByFinalQuestions(model.getSession(),
					qcontainer)) {
				stateTransitions.add(st);
			}
		}
		// collect all CondEqual preconditions of all statetransitions (CondAnd
		// are splitted, other condition types can not be used to extend
		// condition
		Set<Condition> precondions = new HashSet<Condition>();
		for (StateTransition st : stateTransitions) {
			precondions.addAll(getPrimitiveConditions(st.getActivationCondition()));
		}
		for (Condition condition : precondions) {
			// collect all conditions of state transitions establishing a state
			// that enables
			// execution of the condEqual
			LinkedList<List<Condition>> neededConditions = new LinkedList<List<Condition>>();
			Set<QContainer> transitionalQContainer = new HashSet<QContainer>();
			for (StateTransition st : stateTransitions) {
				for (ValueTransition vt : st.getPostTransitions()) {
					if (vt.getQuestion() == condition.getTerminalObjects().iterator().next()) {
						for (Value v : vt.calculatePossibleValues()) {
							if (checkValue(condition, v)) {
								neededConditions.add(getConds(st.getActivationCondition()));
								transitionalQContainer.add(st.getQcontainer());
								break;
							}
						}
					}
				}
			}
			// TODO: add a hashmap finalquestion -> conditions which must be
			// recalculated
			// put all common conditions in the cache
			preconditionCache.put(condition, new Pair<List<Condition>, Set<QContainer>>(
					getCommonConditions(neededConditions),
					transitionalQContainer));
		}
		log.info("General init: " + (System.currentTimeMillis() - time) + "ms");
	}

	/**
	 * Checks if the the Value v can be used to fullfill the condition. This
	 * method can only handle primitive conditions @see getPrimitiveConditions
	 * 
	 * @created 04.07.2012
	 * @param condition specified condition
	 * @param v Value
	 * @return true if the value can fullfill the condition
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
			return (cv.getChoiceID() != ((ChoiceValue) condEqual.getValue()).getChoiceID());
		}
		return false;
	}

	private static List<Condition> getCommonConditions(LinkedList<List<Condition>> neededConditions) {
		// take the first list and check if all other lists contain it's
		// conditions
		List<Condition> first = neededConditions.poll();
		List<Condition> commonConditions = new LinkedList<Condition>();
		if (first != null) {
			first: for (Condition c : first) {
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
	 * Returns all primitive subconditions of the specified condition. CondAnd
	 * are splitted. CondOr are defined as primitive, when their subterms are
	 * all CondEqual of QuestionOC. CondNot are primitive, when they have
	 * exactly one CondEqual of a QuestionOC as subcondition. CondEqual are
	 * always primitive.
	 * 
	 * @param cond specified Condition
	 * @return list of primitive conditions
	 */
	public static List<Condition> getPrimitiveConditions(Condition cond) {
		List<Condition> conds = new LinkedList<Condition>();
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
	 * Checks if all terms of the specified CondOr are {@link CondEqual}s of
	 * QuestionOC
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
	 * Checks if the specified {@link CondNot} contains only one CondEqual of a
	 * ChoiceValue
	 * 
	 * @created 22.03.2012
	 */
	private static boolean checkCondNot(CondNot condNot) {
		Condition negatedCondition = condNot.getTerms().get(0);
		if (negatedCondition instanceof CondEqual) {
			CondEqual condEqual = (CondEqual) negatedCondition;
			return (condEqual.getValue() instanceof ChoiceValue);
		}
		return false;
	}

	private static List<ChoiceID> getChoiceIDs(CondOr condOr) {
		List<ChoiceID> choices = new LinkedList<ChoiceID>();
		for (Condition c : condOr.getTerms()) {
			CondEqual condEqual = (CondEqual) c;
			ChoiceValue cv = (ChoiceValue) condEqual.getValue();
			choices.add(cv.getChoiceID());
		}
		return choices;
	}

	private static List<Condition> getConds(Condition cond) {
		List<Condition> conds = new LinkedList<Condition>();
		if (cond instanceof CondAnd) {
			CondAnd condAnd = (CondAnd) cond;
			for (Condition c : condAnd.getTerms()) {
				conds.addAll(getConds(c));
			}
		}
		else if (cond != null) {
			conds.add(cond);
		}
		return conds;
	}

	@Override
	public double getDistance(Path path, State state, QContainer target) {
		StateTransition stateTransition = StateTransition.getStateTransition(target);
		// if there is no condition, the target can be indicated directly
		if (stateTransition == null || stateTransition.getActivationCondition() == null) return 0;
		Condition condition = getTransitiveCondition(path, stateTransition);

		double result = estimatePathCosts(state, condition)
				+ calculateUnusedNegatives(path);

		return result;
	}

	private Condition getTransitiveCondition(Path path, StateTransition stateTransition) {
		Condition precondition = stateTransition.getActivationCondition();
		// use a set to filter duplicated conditions
		Set<Condition> conditions = new HashSet<Condition>();
		for (Pair<List<Condition>, Set<QContainer>> p : targetCache.get(stateTransition.getQcontainer())) {
			// if no qcontainer was on the path, add the conditions
			if (!path.contains(p.getB())) {
				conditions.addAll(p.getA());
			}
		}
		Condition condition;
		if (conditions.size() > 0) {
			List<Condition> conditionsToUse = new LinkedList<Condition>();
			// add the original condition
			conditionsToUse.add(precondition);
			Set<Condition> nonCondEqual = new HashSet<Condition>();
			for (Condition additionalCondition : conditions) {
				if (additionalCondition instanceof CondEqual) {
					conditionsToUse.add(additionalCondition);
				}
				else {
					nonCondEqual.add(additionalCondition);
				}
			}
			Set<Condition> blacklist = new HashSet<Condition>();
			for (Condition additionalCondition : nonCondEqual) {
				if (blacklist.contains(additionalCondition)) {
					continue;
				}
				// using a HashSet to fasten Collections.disjoint
				Set<TerminologyObject> objectsOfAdditionalCondition = new HashSet<TerminologyObject>(
						additionalCondition.getTerminalObjects());
				// the term objects of the other conditions must be disjunct
				boolean disjunct = true;
				// NOTE: Collect all reference TerminologyObjects and doing
				// only one disjoint operation is slower (cannot abort, must
				// Iterate all conditions)
				for (Condition reference : conditions) {
					if (reference != additionalCondition) {
						if (!Collections.disjoint(reference.getTerminalObjects(),
									objectsOfAdditionalCondition)) {
							// adding of this condition could violate the
							// optimism, continue with next condition
							disjunct = false;
							// reference condition cannot be used either
							blacklist.add(reference);
							break;
						}
					}
				}
				// if the terms objects are disjunct from all other
				// additional conditions, the condition can be added
				if (disjunct) {
					conditionsToUse.add(additionalCondition);
				}
			}
			condition = new CondAnd(conditionsToUse);
		}
		else {
			condition = precondition;
		}
		return condition;
	}

	/**
	 * Returns all term objects of a condition, being part of a condition other
	 * than CondEqual or CondAnd
	 * 
	 * @created 05.10.2011
	 * @param condition Condition
	 */
	private static Collection<TerminologyObject> getForbiddenObjects(Condition cond) {
		List<TerminologyObject> terms = new LinkedList<TerminologyObject>();
		if (cond instanceof CondEqual) {
			return terms;
		}
		else if (cond instanceof CondAnd) {
			CondAnd condAnd = (CondAnd) cond;
			for (Condition c : condAnd.getTerms()) {
				terms.addAll(getForbiddenObjects(c));
			}
			return terms;
		}
		else {
			terms.addAll(cond.getTerminalObjects());
			return terms;
		}
	}

	/**
	 * Calculates a transitive condition for the specified target based on the
	 * session.
	 * 
	 * @created 04.07.2012
	 * @param session actual session
	 * @param target specified target
	 * @return transitive condition
	 */
	public static Condition calculateTransitiveCondition(Session session, QContainer target) {
		TPHeuristic heuristic = new TPHeuristic();
		SearchModel model = new SearchModel(session);
		model.addTarget(new Target(target));
		heuristic.init(model);
		StateTransition stateTransition = StateTransition.getStateTransition(target);
		if (stateTransition == null) return new CondAnd(Collections.<Condition> emptyList());
		Path path = new AStarPath(null, null, 0);
		return heuristic.getTransitiveCondition(path, stateTransition);
	}
}
