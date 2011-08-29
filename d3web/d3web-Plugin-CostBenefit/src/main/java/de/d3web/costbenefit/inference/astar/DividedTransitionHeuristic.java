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
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.costbenefit.inference.ConditionalValueSetter;
import de.d3web.costbenefit.inference.CostFunction;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.inference.ValueTransition;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 22.06.2011
 */
public class DividedTransitionHeuristic implements Heuristic {

	// Stores the costs of the cheapest statestransition per Question and Value
	// for each target
	private Map<QContainer, Map<Question, Map<Value, Double>>> stmap = new HashMap<QContainer, Map<Question, Map<Value, Double>>>();

	@Override
	public double getDistance(State state, QContainer target, CostFunction costFunction) {
		StateTransition stateTransition = StateTransition.getStateTransition(target);
		// if there is no condition, the target can be indicated directly
		if (stateTransition == null || stateTransition.getActivationCondition() == null) return 0;
		Condition precondition = stateTransition.getActivationCondition();
		return estimatePathCosts(state, precondition, target);
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
				// in an or condition, only the cheapest term must be fullfilled
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
			double cheapest = Double.POSITIVE_INFINITY;
			if (map != null) {
				Double costs = map.get(value);
				if (costs != null) cheapest = costs;
			}
			return cheapest;
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
		Map<Question, Map<Value, Double>> targetMap = stmap.get(target);
		if (targetMap == null) {
			targetMap = getTargetMap(target);
			stmap.put(target, targetMap);
		}
		return targetMap.get(question);
	}

	// TODO: create other heuristic overwriting this method
	protected double calcualteCosts(StateTransition st, QContainer target) {
		// TODO: Filter position transitions (question not part of any
		// precondition)
		double costs = st.getQcontainer().getInfoStore().getValue(BasicProperties.COST);
		StateTransition sttarget = StateTransition.getStateTransition(target);
		// sttarget cannot be null, AStar handles these targets separately
		Collection<? extends TerminologyObject> terminalObjects = sttarget.getActivationCondition().getTerminalObjects();
		Set<Question> set = new HashSet<Question>();
		for (ValueTransition vt : st.getPostTransitions()) {
			Question question = vt.getQuestion();
			if (terminalObjects.contains(question)) {
				set.add(question);
			}
		}
		if (set.size() == 0) {
			return Double.POSITIVE_INFINITY;
		}
		return costs / set.size();
	}

	private HashMap<Question, Map<Value, Double>> getTargetMap(QContainer qcon) {
		KnowledgeBase kb = qcon.getKnowledgeBase();
		HashMap<Question, Map<Value, Double>> targetMap = new HashMap<Question, Map<Value, Double>>();
		for (StateTransition st : kb.getAllKnowledgeSlicesFor(StateTransition.KNOWLEDGE_KIND)) {
			for (ValueTransition vt : st.getPostTransitions()) {
				Map<Value, Double> questionMap = targetMap.get(vt.getQuestion());
				if (questionMap == null) {
					questionMap = new HashMap<Value, Double>();
					targetMap.put(vt.getQuestion(), questionMap);
				}
				for (ConditionalValueSetter cvs : vt.getSetters()) {
					Double minimum = questionMap.get(cvs.getAnswer());
					double costs = calcualteCosts(st, qcon);
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
