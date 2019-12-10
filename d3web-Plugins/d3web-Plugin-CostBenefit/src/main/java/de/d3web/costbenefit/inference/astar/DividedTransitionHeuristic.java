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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.costbenefit.CostBenefitUtil;
import de.d3web.costbenefit.inference.ConditionalValueSetter;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.inference.ValueTransition;
import de.d3web.costbenefit.inference.astar.DividedTransitionHeuristic.DividedTransitionHeuristicSessionObject;
import de.d3web.costbenefit.model.Path;
import de.d3web.costbenefit.model.SearchModel;

/**
 * @author Markus Friedrich (denkbares GmbH)
 * @created 22.06.2011
 */
public class DividedTransitionHeuristic implements Heuristic, SessionObjectSource<DividedTransitionHeuristicSessionObject> {

	public static class DividedTransitionHeuristicSessionObject implements SessionObject {

		/**
		 * Stores the {@link KnowledgeBase} this heuristic is initialized for
		 */
		protected KnowledgeBase knowledgeBase;

		/**
		 * Stores all available state transitions of the knowledge base of the initialized session
		 */
		private Collection<StateTransition> transitionalStateTransitions;
		private double negativeSum;
		private Map<Question, Value> finalValues;
		/**
		 * Stores the costs of the cheapest state transition per Question and Value for each target
		 */
		private final Map<Condition, ActivationCacheEntry> costCache = new ConcurrentHashMap<>();
		private final Map<ValueTransition, Value> valueCache = new ConcurrentHashMap<>();
		;
		private Session answeredSession;
	}

	@Override
	public double getDistance(SearchModel model, Path path, State state, QContainer target) {
		StateTransition stateTransition = StateTransition.getStateTransition(target);
		// if there is no condition, the target can be indicated directly
		if (stateTransition == null || stateTransition.getActivationCondition() == null) return 0;
		Condition precondition = stateTransition.getActivationCondition();
		DividedTransitionHeuristicSessionObject sessionObject = model.getSession().getSessionObject(this);
		return estimatePathCosts(sessionObject, state, precondition)
				+ calculateUnusedNegatives(sessionObject, path);
	}

	protected static double calculateUnusedNegatives(DividedTransitionHeuristicSessionObject sessionObject, Path path) {
		return sessionObject.negativeSum * 0.75 - path.getNegativeCosts();
	}

	@Override
	public void init(SearchModel model) {
		DividedTransitionHeuristicSessionObject sessionObject = model.getSession().getSessionObject(this);
		// check if no further initialization required
		KnowledgeBase kb = model.getSession().getKnowledgeBase();
		sessionObject.finalValues = PSMethodCostBenefit.getFinalValues(model.getSession());
		// otherwise prepare some information
		sessionObject.knowledgeBase = kb;
		sessionObject.transitionalStateTransitions = new LinkedList<>();
		sessionObject.transitionalStateTransitions.addAll(model.getTransitionalStateTransitions());
		sessionObject.answeredSession = CostBenefitUtil.createSearchCopy(model.getSession());
		sessionObject.valueCache.clear();
		// set normal values of all questions in transitional qcontainers
		for (StateTransition st : sessionObject.transitionalStateTransitions) {
			CostBenefitUtil.setNormalValues(sessionObject.answeredSession, st.getQcontainer(), this);
		}

		sessionObject.costCache.clear();
		sessionObject.negativeSum = 0;
		for (QContainer qcon : kb.getManager().getQContainers()) {
			Double costs = qcon.getInfoStore().getValue(BasicProperties.COST);
			if (costs < 0) {
				sessionObject.negativeSum += costs;
			}
		}
	}

	private static final class ActivationCacheEntry {

		private Map<Question, Map<Value, Double>> targetMap;
		private List<TerminologyObject> objects;
		private CompiledCostsFunction costFunction;
	}

	protected static double estimatePathCosts(DividedTransitionHeuristicSessionObject sessionObject, State state, Condition activationCondition) {
		ActivationCacheEntry entry = sessionObject.costCache.get(activationCondition);
		if (entry == null) {
			entry = new ActivationCacheEntry();
			entry.targetMap = getTargetMap(sessionObject, activationCondition);
			entry.objects = new ArrayList<>(
					activationCondition.getTerminalObjects());
			entry.costFunction = compile(activationCondition, entry.objects, entry.targetMap);
			sessionObject.costCache.put(activationCondition, entry);
		}

		ArrayList<Value> key = new ArrayList<>(entry.objects.size());
		for (TerminologyObject object : entry.objects) {
			if (object instanceof Question) {
				key.add(state.getValue((Question) object));
			}
			else {
				key.add(UndefinedValue.getInstance());
			}
		}

		return entry.costFunction.eval(key);
	}

	private interface CompiledCostsFunction {

		void setConflicting(int index);

		double eval(ArrayList<Value> values);

		Set<Integer> getIndexes();
	}

	private static final class CompiledCondAnd implements CompiledCostsFunction {

		private final CompiledCostsFunction[] children;

		public CompiledCondAnd(CompiledCostsFunction[] children) { // NOSONAR
			this.children = children;
		}

		@Override
		public double eval(ArrayList<Value> values) {
			double sum = 0;
			for (CompiledCostsFunction child : children) {
				sum += child.eval(values);
				if (sum == Double.POSITIVE_INFINITY) break;
			}
			return sum;
		}

		@Override
		public Set<Integer> getIndexes() {
			Set<Integer> indexes = new HashSet<>();
			for (CompiledCostsFunction child : children) {
				indexes.addAll(child.getIndexes());
			}
			return indexes;
		}

		@Override
		public void setConflicting(int index) {
			for (CompiledCostsFunction child : children) {
				child.setConflicting(index);
			}
		}
	}

	private static final class CompiledCondOr implements CompiledCostsFunction {

		private final CompiledCostsFunction[] children;

		public CompiledCondOr(CompiledCostsFunction[] children) { // NOSONAR
			this.children = children;
		}

		@Override
		public double eval(ArrayList<Value> values) {
			double cheapest = Double.POSITIVE_INFINITY;
			for (CompiledCostsFunction child : children) {
				// in an or condition, only the cheapest term must be fulfilled
				cheapest = Math.min(cheapest, child.eval(values));
				if (cheapest == 0.0) break;
			}
			return cheapest;
		}

		@Override
		public Set<Integer> getIndexes() {
			Set<Integer> indexes = new HashSet<>();
			for (CompiledCostsFunction child : children) {
				indexes.addAll(child.getIndexes());
			}
			return indexes;
		}

		@Override
		public void setConflicting(int index) {
			Set<Integer> indexes = getIndexes();
			if (indexes.size() == 1) {
				for (CompiledCostsFunction child : children) {
					child.setConflicting(index);
				}
			}
		}
	}

	private static final class CompiledCondEqual implements CompiledCostsFunction {

		private final int index;
		private final Value value;
		private final double costs;
		// if there are two cond equal of the same Question as children of a
		// condand, they are conflicting
		private boolean conflicting = false;

		public CompiledCondEqual(Value value, int index, double costs) {
			this.costs = costs;
			this.value = value;
			this.index = index;
		}

		@Override
		public double eval(ArrayList<Value> values) {
			Value value = values.get(index);
			if (this.value.equals(value)) {
				if (!conflicting) {
					return 0.0;
				}
				else {
					return costs;
				}
			}
			return costs;
		}

		@Override
		public Set<Integer> getIndexes() {
			Set<Integer> indexes = new HashSet<>();
			indexes.add(index);
			return indexes;
		}

		@Override
		public void setConflicting(int index) {
			if (index == this.index) {
				conflicting = true;
			}
		}
	}

	private static final class CompiledCondNotEqual implements CompiledCostsFunction {

		private final int index;
		private final Value value;
		private final double costs;
		private boolean conflicting = false;

		public CompiledCondNotEqual(Value value, int index, double costs) {
			this.costs = costs;
			this.value = value;
			this.index = index;
		}

		@Override
		public double eval(ArrayList<Value> values) {
			Value value = values.get(index);
			if (!this.value.equals(value)) {
				if (conflicting) {
					return costs;
				}
				else {
					return 0.0;
				}
			}
			return costs;
		}

		@Override
		public Set<Integer> getIndexes() {
			Set<Integer> indexes = new HashSet<>();
			indexes.add(index);
			return indexes;
		}

		@Override
		public void setConflicting(int index) {
			if (index == this.index) {
				conflicting = true;
			}
		}
	}

	private static CompiledCostsFunction compile(Condition cond, List<TerminologyObject> objects, Map<Question, Map<Value, Double>> targetMap) {
		if (cond instanceof CondAnd) {
			CompiledCostsFunction[] children = getCompiledChildren((CondAnd) cond, objects, targetMap);
			// mark conds of the same question as conflicting
			Map<Integer, CompiledCostsFunction> map = new HashMap<>();
			for (CompiledCostsFunction function : children) {
				Set<Integer> indexes = function.getIndexes();
				// only for condand more than one index is allowed, but condand
				// are flattend by tpheuristic
				if (indexes.size() == 1) {
					Integer index = indexes.iterator().next();
					CompiledCostsFunction sameQuestion = map.get(index);
					if (sameQuestion != null) {
						sameQuestion.setConflicting(index);
						function.setConflicting(index);
					}
					else {
						map.put(index, function);
					}
				}
			}
			if (children.length == 1) return children[0];
			return new CompiledCondAnd(children);
		}
		else if (cond instanceof CondOr) {
			CompiledCostsFunction[] children =
					getCompiledChildren((CondOr) cond, objects, targetMap);
			if (children.length == 1) return children[0];
			return new CompiledCondOr(children);
		}
		else if (cond instanceof CondEqual) {
			CondEqual c = (CondEqual) cond;
			QuestionChoice question = (QuestionChoice) c.getQuestion();
			ChoiceValue value = (ChoiceValue) c.getValue();
			Map<Value, Double> map = getCosts(targetMap, question);
			int index = objects.indexOf(question);
			double valueCosts = Double.POSITIVE_INFINITY;
			if (map != null) {
				Double costs = map.get(value);
				if (costs != null) valueCosts = costs;
			}
			return new CompiledCondEqual(value, index, valueCosts);
		}
		else if (cond instanceof CondNot) {
			CondNot cnot = (CondNot) cond;
			List<Condition> terms = cnot.getTerms();
			if (terms.size() == 1 && terms.get(0) instanceof CondEqual) {
				CondEqual c = (CondEqual) terms.get(0);
				QuestionChoice question = (QuestionChoice) c.getQuestion();
				Value value = c.getValue();
				Map<Value, Double> map = getCosts(targetMap, question);
				int index = objects.indexOf(question);
				// searches for the cheapest value transition, setting the
				// question to another value
				double cheapest = Double.POSITIVE_INFINITY;
				if (map != null) {
					for (Entry<Value, Double> e : map.entrySet()) {
						if (!e.getKey().equals(value)) {
							cheapest = Math.min(cheapest, e.getValue());
						}
					}
				}
				return new CompiledCondNotEqual(value, index, cheapest);
			}
			else {
				throw new IllegalArgumentException("Can only handle CondNot with one CondEqual: " + cond);
			}
		}
		else {
			throw new IllegalArgumentException("Can only handle CondNot, CondAnd, CondOr or CondEqual: " + cond);
		}
	}

	private static CompiledCostsFunction[] getCompiledChildren(NonTerminalCondition cond, List<TerminologyObject> objects, Map<Question, Map<Value, Double>> targetMap) {
		List<Condition> terms = cond.getTerms();
		CompiledCostsFunction[] children = new CompiledCostsFunction[terms.size()];
		int index = 0;
		for (Condition term : terms) {
			children[index++] = compile(term, objects, targetMap);
		}
		return children;
	}

	private static Map<Value, Double> getCosts(Map<Question, Map<Value, Double>> targetMap, QuestionChoice question) {
		return targetMap.get(question);
	}

	/**
	 * Calculate the minimal costs of preparing one (1) single precondition question of the target {@link QContainer} by
	 * a specified {@link StateTransition}. If the StateTransition is capable to prepare multiple precondition questions
	 * of the target, the costs of the preparing questionnaire are divided by the number of preconditions to be
	 * established (to have the cost per precondition question).
	 *
	 * @param preparingTransition the state transition possibly used to prepare the target
	 * @param stateQuestion       the question represents the state to be estimated
	 * @return the minimal costs per question of the target's precondition
	 * @created 06.09.2011
	 */
	private static double calculateCosts(StateTransition preparingTransition, Set<Question> set, Question stateQuestion) {

		// if no question has been found, return infinite costs
		// because this state transition cannot set up the target
		if (set.isEmpty()) {
			return Double.POSITIVE_INFINITY;
		}

		// if the specified question is not part of the set,
		// we can assume "infinity" because it cannot be reached
		if (!set.contains(stateQuestion)) {
			return Double.POSITIVE_INFINITY;
		}

		// otherwise take the static costs and divide them
		// by the number of relevant questions possible set up
		double costs = preparingTransition.getCosts();
		if (costs > 0.0) {
			return costs / set.size();
		}
		else {
			// the costs of all negative teststeps get substracted anyway
			return 0;
		}
	}

	/**
	 * calculate all questions that
	 * <p>
	 * a) will be set by the preparing state transition
	 * <p>
	 * b) are relevant in the targets activation condition
	 * <p>
	 * c) the set values are common with the required values
	 *
	 * @created 23.05.2012
	 */
	private static Map<Question, Value> getQuestionSet(DividedTransitionHeuristicSessionObject sessionObject, StateTransition preparingTransition, Condition activationCondition) {
		Collection<? extends TerminologyObject> terminalObjects = activationCondition.getTerminalObjects();
		Map<Question, Value> set = new HashMap<>();
		for (ValueTransition vt : preparingTransition.getPostTransitions()) {
			Question question = vt.getQuestion();
			// the question is relevant
			if (terminalObjects.contains(question)) {
				// check if the values required for that questions
				// matches the values that can be set up
				Set<Value> requiredValues = calculateRequiredValues(question, activationCondition);
				Value value = getValue(sessionObject, vt);
				// values that are possible, but are already finally set, can be
				// ignored -> heuristic gets more precise
				Value finalValue = sessionObject.finalValues.get(vt.getQuestion());
				if (value != null && finalValue == null && requiredValues.contains(value)) {
					set.put(question, value);
				}
			}
		}
		return set;
	}

	protected static Value getValue(DividedTransitionHeuristicSessionObject sessionObject, ValueTransition vt) {
		Value value = sessionObject.valueCache.get(vt);
		if (value != null) {
			return value;
		}
		List<ConditionalValueSetter> setters = vt.getSetters();
		for (ConditionalValueSetter cvs : setters) {
			if (cvs == null) continue;
			Condition condition = cvs.getCondition();
			if (condition == null
					|| Conditions.isTrue(cvs.getCondition(), sessionObject.answeredSession)) {
				sessionObject.valueCache.put(vt, cvs.getAnswer());
				return cvs.getAnswer();
			}
		}
		return null;
	}

	private static Set<Value> calculateRequiredValues(Question question, Condition condition) {
		if (condition instanceof CondAnd) {
			CondAnd cand = (CondAnd) condition;
			Set<Value> result = new HashSet<>();
			for (Condition subCondition : cand.getTerms()) {
				result.addAll(calculateRequiredValues(question, subCondition));
			}
			return result;
		}
		else if (condition instanceof CondOr) {
			CondOr cor = (CondOr) condition;
			Set<Value> result = new HashSet<>();
			for (Condition subCondition : cor.getTerms()) {
				result.addAll(calculateRequiredValues(question, subCondition));
			}
			return result;
		}
		else if (condition instanceof CondEqual) {
			CondEqual c = (CondEqual) condition;
			if (!c.getQuestion().equals(question)) return Collections.emptySet();
			ChoiceValue value = (ChoiceValue) c.getValue();
			Set<Value> result = new HashSet<>();
			result.add(value);
			return result;
		}
		else if (condition instanceof CondNot) {
			CondNot cnot = (CondNot) condition;
			if (condition.getTerminalObjects().size() != 1) {
				throw new IllegalArgumentException(
						"Can only handle CondNot with one question: "
								+ condition);
			}
			if (!cnot.getTerminalObjects().iterator().next().equals(question)) return Collections.emptySet();
			// use all choices as values
			Set<Value> result = new HashSet<>();
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

	private static HashMap<Question, Map<Value, Double>> getTargetMap(DividedTransitionHeuristicSessionObject sessionObject, Condition activationCondition) {
		HashMap<Question, Map<Value, Double>> targetMap = new HashMap<>();
		for (StateTransition st : sessionObject.transitionalStateTransitions) {
			for (ValueTransition vt : st.getPostTransitions()) {
				Question stateQuestion = vt.getQuestion();
				// is already finally set, cannot be changed
				if (sessionObject.finalValues.containsKey(stateQuestion)) continue;
				Map<Value, Double> questionMap = targetMap.computeIfAbsent(stateQuestion, k -> new HashMap<>());
				Map<Question, Value> set = getQuestionSet(sessionObject, st, activationCondition);
				double costs = calculateCosts(st, set.keySet(), stateQuestion);
				for (Entry<Question, Value> entry : set.entrySet()) {
					Value stateValue = entry.getValue();
					Double minimum = questionMap.get(stateValue);
					if (minimum == null || minimum > costs) {
						questionMap.put(stateValue, costs);
					}
				}
			}
		}
		for (Entry<Question, Value> entry : sessionObject.finalValues.entrySet()) {
			Map<Value, Double> valueMap = new HashMap<>();
			valueMap.put(entry.getValue(), 0.0);
			targetMap.put(entry.getKey(), valueMap);
		}

		return targetMap;
	}

	@Override
	public DividedTransitionHeuristicSessionObject createSessionObject(Session session) {
		return new DividedTransitionHeuristicSessionObject();
	}
}
