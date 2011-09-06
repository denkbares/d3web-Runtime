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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.costbenefit.inference.ConditionalValueSetter;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.inference.ValueTransition;
import de.d3web.costbenefit.model.SearchModel;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 22.06.2011
 */
public class DividedTransitionHeuristic implements Heuristic {

	/**
	 * Stores the costs of the cheapest state transition per Question and Value
	 * for each target
	 */
	private Map<QContainer, Map<Question, Map<Value, Double>>> costCache;

	/**
	 * Stores the {@link KnowledgeBase} this heuristic is initialized for
	 */
	private KnowledgeBase knowledgeBase;

	/**
	 * Stores all available state transitions of the knowledge base of the
	 * initialized session
	 */
	private Collection<StateTransition> allStateTransitions;

	@Override
	public double getDistance(State state, QContainer target) {
		StateTransition stateTransition = StateTransition.getStateTransition(target);
		// if there is no condition, the target can be indicated directly
		if (stateTransition == null || stateTransition.getActivationCondition() == null) return 0;
		Condition precondition = stateTransition.getActivationCondition();
		return estimatePathCosts(state, precondition, target);
	}

	@Override
	public void init(SearchModel model) {
		// check if no further initialization required
		KnowledgeBase kb = model.getSession().getKnowledgeBase();
		if (this.knowledgeBase == kb) return;

		// otherwise prepare some information
		this.knowledgeBase = kb;
		this.allStateTransitions = kb.getAllKnowledgeSlicesFor(StateTransition.KNOWLEDGE_KIND);
		this.costCache = new HashMap<QContainer, Map<Question, Map<Value, Double>>>();
	}

	private double estimatePathCosts(State state, Condition cond, QContainer target) {
		if (cond instanceof CondAnd) {
			CondAnd cand = (CondAnd) cond;
			double sum = 0;
			for (Condition c : cand.getTerms()) {
				sum += estimatePathCosts(state, c, target);
			}
			return sum;
		}
		else if (cond instanceof CondOr) {
			CondOr cor = (CondOr) cond;
			double cheapest = Double.POSITIVE_INFINITY;
			for (Condition c : cor.getTerms()) {
				// in an or condition, only the cheapest term must be fulfilled
				cheapest = Math.min(cheapest,
						estimatePathCosts(state, c, target));
			}
			return cheapest;
		}
		else if (cond instanceof CondEqual) {
			CondEqual c = (CondEqual) cond;
			QuestionChoice question = (QuestionChoice) c.getQuestion();
			ChoiceValue value = (ChoiceValue) c.getValue();
			if (state.hasValue(question, value)) {
				// condition is fulfilled, no state transition needed => no
				// costs
				return 0;
			}
			Map<Value, Double> map = getCosts(target, question);
			if (map != null) {
				Double costs = map.get(value);
				if (costs != null) return costs;
			}
			return Double.POSITIVE_INFINITY;
		}
		else if (cond instanceof CondNot) {
			CondNot cnot = (CondNot) cond;
			List<Condition> terms = cnot.getTerms();
			if (terms.size() == 1 && terms.get(0) instanceof CondEqual) {
				CondEqual c = (CondEqual) terms.get(0);
				QuestionChoice question = (QuestionChoice) c.getQuestion();
				Value value = c.getValue();
				if (!state.hasValue(question, value)) {
					return 0.0;
				}
				double cheapest = Double.POSITIVE_INFINITY;
				// searches for the cheapest value transition, setting the
				// question to another value
				Map<Value, Double> map = getCosts(target, question);
				if (map != null) {
					for (Entry<Value, Double> e : map.entrySet()) {
						if (!e.getKey().equals(value)) {
							cheapest = Math.min(cheapest, e.getValue());
						}
					}
				}
				return cheapest;
			}
			else {
				throw new IllegalArgumentException("Can only handle CondNot with one CondEqual: "
						+ cond);
			}
		}
		else {
			throw new IllegalArgumentException(
					"Can only handle CondNot, CondAnd, CondOr or CondEqual: "
							+ cond);
		}
	}

	private Map<Value, Double> getCosts(QContainer target, QuestionChoice question) {
		Map<Question, Map<Value, Double>> targetMap = costCache.get(target);
		if (targetMap == null) {
			targetMap = getTargetMap(target);
			costCache.put(target, targetMap);
		}
		return targetMap.get(question);
	}

	/**
	 * Calculate the minimal costs of preparing one (1) single precondition
	 * question of the target {@link QContainer} by a specified
	 * {@link StateTransition}. If the StateTransition is capable to prepare
	 * multiple precondition questions of the target, the costs of the preparing
	 * questionnaire are divided by the number of preconditions to be
	 * established (to have the the cost per precondition question).
	 * 
	 * @created 06.09.2011
	 * @param preparingTransition the state transition possibly used to prepare
	 *        the target
	 * @param target the target QContainer to be prepared
	 * @return the minimal costs per question of the target's precondition
	 */
	private double calculateCosts(StateTransition preparingTransition, QContainer target) {
		// TODO: Filter position transitions (question not part of any
		// precondition)

		// calculate all questions that
		// a) will be set by the preparing state transition
		// b) are relevant in the targets activation condition
		// c) the set values are common with the required values

		// prepare the loop of all set values
		// (targetStateTransition cannot be null, AStar handles that separately)
		StateTransition targetStateTransition = StateTransition.getStateTransition(target);
		Condition activationCondition = targetStateTransition.getActivationCondition();
		Collection<? extends TerminologyObject> terminalObjects = activationCondition.getTerminalObjects();
		Set<Question> set = new HashSet<Question>();
		for (ValueTransition vt : preparingTransition.getPostTransitions()) {
			Question question = vt.getQuestion();
			// the question is relevant (and not yet accepted)
			if (terminalObjects.contains(question) && !set.contains(question)) {
				// check if the values required for that questions
				// matches the values that can be set up
				Set<Value> requiredValues = calculateRequiredValues(question, activationCondition);
				Set<Value> possibleValues = calculatePossibleValues(question, vt.getSetters());
				if (!Collections.disjoint(requiredValues, possibleValues)) {
					// the values that can be set up are common with
					// the required ones, so count that question
					set.add(question);
				}
			}
		}

		// if no question has been found, return infinite costs
		// because this state transition cannot set up the target
		if (set.size() == 0) {
			return Double.POSITIVE_INFINITY;
		}

		// otherwise take the static costs and divide them
		// by the number of relevant questions possible set up
		QContainer preapringContainer = preparingTransition.getQcontainer();
		double costs = preapringContainer.getInfoStore().getValue(BasicProperties.COST);
		return costs / set.size();
	}

	private Set<Value> calculateRequiredValues(Question question, Condition condition) {
		if (condition instanceof CondAnd) {
			CondAnd cand = (CondAnd) condition;
			// there will no two sub-conditions for the same question
			// so use the first one
			for (Condition subCondition : cand.getTerms()) {
				Set<Value> result = calculateRequiredValues(question, subCondition);
				if (!result.isEmpty()) return result;
			}
			return Collections.emptySet();
		}
		else if (condition instanceof CondOr) {
			CondOr cor = (CondOr) condition;
			Set<Value> result = new HashSet<Value>();
			for (Condition subCondition : cor.getTerms()) {
				result.addAll(calculateRequiredValues(question, subCondition));
			}
			return result;
		}
		else if (condition instanceof CondEqual) {
			CondEqual c = (CondEqual) condition;
			if (!c.getQuestion().equals(question)) return Collections.emptySet();
			ChoiceValue value = (ChoiceValue) c.getValue();
			Set<Value> result = new HashSet<Value>();
			result.add(value);
			return result;
		}
		else if (condition instanceof CondNot) {
			CondNot cnot = (CondNot) condition;
			// use all choices as values
			Set<Value> result = new HashSet<Value>();
			for (Choice choice : ((QuestionOC) question).getAllAlternatives()) {
				result.add(new ChoiceValue(choice));
			}
			// but remove those from the child conditions (negated ones)
			Condition subCondition = cnot.getTerms().get(0);
			result.removeAll(calculateRequiredValues(question, subCondition));
			return result;
		}
		else {
			throw new IllegalArgumentException(
					"Can only handle CondNot, CondAnd, CondOr or CondEqual: "
							+ condition);
		}
	}

	private Set<Value> calculatePossibleValues(Question question, List<ConditionalValueSetter> setters) {
		Set<Value> result = new HashSet<Value>();
		for (ConditionalValueSetter setter : setters) {
			result.add(setter.getAnswer());
		}
		return result;
	}

	private HashMap<Question, Map<Value, Double>> getTargetMap(QContainer qcon) {
		HashMap<Question, Map<Value, Double>> targetMap = new HashMap<Question, Map<Value, Double>>();
		for (StateTransition st : allStateTransitions) {
			for (ValueTransition vt : st.getPostTransitions()) {
				Map<Value, Double> questionMap = targetMap.get(vt.getQuestion());
				if (questionMap == null) {
					questionMap = new HashMap<Value, Double>();
					targetMap.put(vt.getQuestion(), questionMap);
				}
				for (ConditionalValueSetter cvs : vt.getSetters()) {
					Double minimum = questionMap.get(cvs.getAnswer());
					double costs = calculateCosts(st, qcon);
					if (minimum == null) {
						questionMap.put(cvs.getAnswer(), costs);
					}
					else if (minimum > costs) {
						questionMap.put(cvs.getAnswer(), costs);
					}
				}
			}
		}
		return targetMap;
	}
}
