/*
 * Copyright (C) 2023 denkbares GmbH, Germany
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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import com.denkbares.collections.DefaultMultiMap;
import com.denkbares.collections.MultiMap;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.costbenefit.model.Target;

/**
 * Helper class to create a transitive hull of state transitions that are capable to support a defined set of target
 * states.
 * <p>
 * NOTE: currently we do not consider any state values, ony if the state question is derived or not. This may be
 * improved in the future.
 *
 * @author Volker Belli, Markus Friedrich (denkbares GmbH)
 * @created 31.10.2023
 */
@SuppressWarnings("JavadocDeclaration")
public class SupportiveStateTransitions {
	private final MultiMap<TerminologyObject, StateTransition> availableByState = new DefaultMultiMap<>();
	private final Collection<TerminologyObject> targetStates = new HashSet<>();

	// the elements that already have been expanded in the transitive hull.
	private final Set<TerminologyObject> processedStates = new HashSet<>();
	private final Set<StateTransition> supportiveTransitions = new HashSet<>();

	public SupportiveStateTransitions(Collection<StateTransition> available, Collection<Target> targetStates) {
		this(available, targetStates.stream()
				.map(Target::getQContainers).flatMap(List::stream)
				.map(StateTransition::getStateTransition).filter(Objects::nonNull)
				.map(StateTransition::getActivationCondition));
	}

	public SupportiveStateTransitions(Collection<StateTransition> available, Stream<Condition> targetConditions) {
		for (var stateTransition : available) {
			for (var valueTransition : stateTransition.getPostTransitions()) {
				availableByState.put(valueTransition.getQuestion(), stateTransition);
			}
		}
		targetConditions.map(this::expandState).forEach(targetStates::addAll);
	}

	/**
	 * Calculates and returns the supportive hull of transitions that are potentially relevant to derive the target
	 * states.
	 *
	 * @return relevant subset of the available transitions
	 */
	public Set<StateTransition> getSupportiveHull() {
		if (!targetStates.isEmpty() && processedStates.isEmpty()) calculate();
		return Collections.unmodifiableSet(supportiveTransitions);
	}

	private void calculate() {
		Collection<TerminologyObject> nextStates = targetStates;
		while (true) {
			// prepare a list of all states, that have not processed yet
			Collection<TerminologyObject> newStates = new ArrayList<>(nextStates.size());
			for (var state : nextStates) {
				if (processedStates.add(state)) newStates.add(state);
			}

			// if no new states are found, terminate
			if (newStates.isEmpty()) break;

			// find all transitions for the new states
			var newTransitions = getSupportiveTransitions(newStates);
			// and create the updated set of new states
			nextStates = new HashSet<>();
			for (var transition : newTransitions) {
				// for each transition that has been added as supportive,
				if (supportiveTransitions.add(transition)) {
					// add its preconditions for the next iterations in this transitive hull calculation
					nextStates.addAll(expandState(transition.getActivationCondition()));
				}
			}
		}
	}

	private Collection<StateTransition> getSupportiveTransitions(Collection<TerminologyObject> targetStates) {
		// for each test step (qcontainer) use only those value transitions,
		// that will activate assuming the current answers, complemented by the normal answers

		// as we use no real states, at the moment, we simply deliver the terminal objects
		var supporters = new HashSet<StateTransition>();
		for (TerminologyObject state : targetStates) {
			supporters.addAll(availableByState.getValues(state));
		}
		return supporters;
	}

	private Collection<TerminologyObject> expandState(@Nullable Condition preconditions) {
		if (preconditions == null) return Collections.emptyList();
		// regardless if the conditions are and/or combinations,
		// simply collect all primitive state comparisons
		// Note that not should be inverted to all possible other values

		// as we use no real states, at the moment, we simply deliver the terminal objects
		return Collections.unmodifiableCollection(preconditions.getTerminalObjects());
	}

//	private static class State {
//		private final Question question;
//		private final Value value;
//
//		public State(Question question, Value value) {
//			this.question = question;
//			this.value = value;
//		}
//
//		public State(Question question, Choice value) {
//			this(question, new ChoiceValue(value));
//		}
//
//		@Override
//		public boolean equals(Object o) {
//			if (this == o) return true;
//			if (!(o instanceof State state)) return false;
//			return Objects.equals(question, state.question) && Objects.equals(value, state.value);
//		}
//
//		@Override
//		public int hashCode() {
//			return Objects.hash(question, value);
//		}
//	}
//
//	private static void extractStates(Condition conditionedFinding, Collection<State> collator) {
//		if (conditionedFinding instanceof CondNot) {
//
//		}
//		else if (conditionedFinding instanceof NonTerminalCondition) {
//			for (Condition condition : ((NonTerminalCondition) conditionedFinding).getTerms()) {
//				extractStates(condition, collator);
//			}
//		}
//		else if (conditionedFinding instanceof CondEqual condEqual) {
//			collator.add(new State(condEqual.getQuestion(), condEqual.getValue()));
//		}
//		else {
//			conds.add(conditionedFinding);
//		}
//	}
}
