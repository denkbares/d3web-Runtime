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

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.utilities.Pair;
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
	private Map<CondEqual, Pair<List<Condition>, Set<QContainer>>> preconditionCache = new HashMap<CondEqual, Pair<List<Condition>, Set<QContainer>>>();

	/**
	 * Stores a List of Pairs of Conditions and QContainers establishing a state
	 * where the conditions of the targetQContainer is applicable
	 */
	private Map<QContainer, List<Pair<List<Condition>, Set<QContainer>>>> targetCache = new HashMap<QContainer, List<Pair<List<Condition>, Set<QContainer>>>>();

	@Override
	public void init(SearchModel model) {
		if (this.knowledgeBase != model.getSession().getKnowledgeBase()) {
			initGeneralCache(model);
		}
		super.init(model);
		initTargetCache(model);
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
				List<CondEqual> condEquals = getCondEquals(activationCondition);
				List<Pair<List<Condition>, Set<QContainer>>> additionalConditions = new LinkedList<Pair<List<Condition>, Set<QContainer>>>();
				List<CondEqual> conditionsToExamine = condEquals;
				while (!conditionsToExamine.isEmpty()) {
					List<Pair<List<Condition>, Set<QContainer>>> temppairs = getPairs(session,
							conditionsToExamine, condEquals,
							activationCondition.getTerminalObjects(), forbiddenTermObjects);
					additionalConditions.addAll(temppairs);
					conditionsToExamine = getCondEquals(temppairs);
				}
				targetCache.put(qcon, additionalConditions);
			}
		}
		log.info("Target init: " + (System.currentTimeMillis() - time) + "ms");
	}

	private List<CondEqual> getCondEquals(List<Pair<List<Condition>, Set<QContainer>>> temppairs) {
		List<CondEqual> list = new LinkedList<CondEqual>();
		for (Pair<List<Condition>, Set<QContainer>> p : temppairs) {
			for (Condition cond : p.getA()) {
				if (cond instanceof CondEqual) {
					list.add((CondEqual) cond);
				}
			}
		}
		return list;
	}

	private List<Pair<List<Condition>, Set<QContainer>>> getPairs(Session session, List<CondEqual> targetConditions, List<CondEqual> originalCondEquals, Collection<? extends TerminologyObject> collection, Collection<TerminologyObject> forbiddenTermObjects) {
		List<Pair<List<Condition>, Set<QContainer>>> additionalConditions = new LinkedList<Pair<List<Condition>, Set<QContainer>>>();
		for (CondEqual cond : targetConditions) {
			// if the condition is fullfilled continue, otherwise add
			// it's preconditions to the list
			try {
				if (cond.eval(session)) {
					continue;
				}
			}
			catch (NoAnswerException e) {
			}
			catch (UnknownAnswerException e) {
			}
			Pair<List<Condition>, Set<QContainer>> generalPair = preconditionCache.get(cond);
			List<Condition> checkedConditions = new LinkedList<Condition>();
			for (Condition precondition : generalPair.getA()) {
				if (precondition instanceof CondEqual) {
					CondEqual condEqual = (CondEqual) precondition;
					// condequals must not be already contained and must
					// not have a forbidden termobject
					if (!originalCondEquals.contains(precondition)
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
		Collection<StateTransition> stateTransitions = kb.getAllKnowledgeSlicesFor(StateTransition.KNOWLEDGE_KIND);
		// collect all CondEqual preconditions of all statetransitions (CondAnd
		// are splitted, other condition types can not be used to extend
		// condition
		Set<CondEqual> precondions = new HashSet<CondEqual>();
		for (StateTransition st : stateTransitions) {
			precondions.addAll(getCondEquals(st.getActivationCondition()));
		}
		for (CondEqual condEqual : precondions) {
			// collect all conditions of state transitions establishing a state
			// that enables
			// execution of the condEqual
			LinkedList<List<Condition>> neededConditions = new LinkedList<List<Condition>>();
			Set<QContainer> transitionalQContainer = new HashSet<QContainer>();
			for (StateTransition st : stateTransitions) {
				for (ValueTransition vt : st.getPostTransitions()) {
					if (vt.getQuestion() == condEqual.getQuestion()) {
						for (Value v : calculatePossibleValues(vt.getSetters())) {
							if (condEqual.getValue().equals(v)) {
								neededConditions.add(getConds(st.getActivationCondition()));
								transitionalQContainer.add(st.getQcontainer());
								break;
							}
						}
					}
				}
			}
			// put all common conditions in the cache
			preconditionCache.put(condEqual, new Pair<List<Condition>, Set<QContainer>>(
					getCommonConditions(neededConditions),
					transitionalQContainer));
		}
		log.info("General init: " + (System.currentTimeMillis() - time) + "ms");
	}

	private List<Condition> getCommonConditions(LinkedList<List<Condition>> neededConditions) {
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

	private List<CondEqual> getCondEquals(Condition cond) {
		List<CondEqual> conds = new LinkedList<CondEqual>();
		if (cond instanceof CondEqual) {
			conds.add((CondEqual) cond);
		}
		else if (cond instanceof CondAnd) {
			CondAnd condAnd = (CondAnd) cond;
			for (Condition c : condAnd.getTerms()) {
				conds.addAll(getCondEquals(c));
			}
		}
		return conds;
	}

	private List<Condition> getConds(Condition cond) {
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
		Condition precondition = stateTransition.getActivationCondition();
		Collection<QContainer> pathContainers = path.getPath();
		// use a set to filter duplicated conditions
		Set<Condition> conditions = new HashSet<Condition>();
		for (Pair<List<Condition>, Set<QContainer>> p : targetCache.get(target)) {
			// if no qcontainer was on the path, add the conditions
			if (Collections.disjoint(pathContainers, p.getB())) {
				conditions.addAll(p.getA());
			}
		}
		Condition condition;
		if (conditions.size() > 0) {
			List<Condition> conditionsToUse = new LinkedList<Condition>();
			// add the original condition
			conditionsToUse.add(precondition);
			for (Condition additionalCondition : conditions) {
				if (additionalCondition instanceof CondEqual) {
					conditionsToUse.add(additionalCondition);
				}
				// the term objects of the other conditions must be disjunct
				else {
					boolean disjunct = true;
					for (Condition reference : conditions) {
						if (reference != additionalCondition) {
							if (!Collections.disjoint(reference.getTerminalObjects(),
									additionalCondition.getTerminalObjects())) {
								// adding of this condition could violate the
								// optimism, continue with next condition
								disjunct = false;
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
			}
			condition = new CondAnd(conditionsToUse);
		}
		else {
			condition = precondition;
		}
		return estimatePathCosts(state, condition, condition) + calculateUnusedNegatives(path);
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
}
