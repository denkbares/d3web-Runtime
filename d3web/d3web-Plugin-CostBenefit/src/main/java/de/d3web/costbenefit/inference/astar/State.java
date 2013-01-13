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

import java.util.BitSet;
import java.util.Map;
import java.util.Map.Entry;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.costbenefit.blackboard.DecoratedBlackboard;

/**
 * The State class represents a compact description of the state of a session.
 * This includes all answered questions that are used in any precondition or
 * state transition.
 * <p>
 * If two paths reaches the same state (values for the state questions), they
 * reference the identical node in the search graph. Therefore the State is the
 * key identifier for a search node.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 22.06.2011
 */
public class State {

	private final Map<Question, Value> usedQuestions;
	private final Session session;
	private final int hashCode;
	private final BitSet changedQuestions;

	public State(Session session, Map<Question, Value> usedPreconditionQuestions) {
		this.session = session;
		this.usedQuestions = usedPreconditionQuestions;
		this.changedQuestions = new BitSet();

		int hashCode = 1;
		Blackboard blackboard = session.getBlackboard();
		// we only have to use the covered values, so if the
		// blackboard is an original one, we do not access any value at all
		if (blackboard instanceof DecoratedBlackboard) {
			DecoratedBlackboard decorated = (DecoratedBlackboard) blackboard;
			int bitIndex = 0;
			for (Entry<Question, Value> entry : usedQuestions.entrySet()) {
				Value value = decorated.getDecoratedValue(entry.getKey());
				if (value != null && !value.equals(entry.getValue())) {
					hashCode = 31 * hashCode + value.hashCode();
					changedQuestions.set(bitIndex);
				}
				bitIndex++;
			}
		}
		this.hashCode = hashCode;
	}

	/**
	 * Checks if the specified state {@link Question} has the specified
	 * {@link Value} in this state.
	 * 
	 * @created 24.06.2011
	 * @param question Question
	 * @param value Value
	 * @return true, if the Question q has the Value v, false otherwise
	 */
	public boolean hasValue(Question question, Value value) {
		Value storedValue = getSession().getBlackboard().getValue(question);
		return equals(value, storedValue);
	}

	public final Value getValue(Question question) {
		return getSession().getBlackboard().getValue(question);
	}

	private boolean equals(Value o1, Value o2) { // NOSONAR
		if (o1 == o2) return true;
		return o2.equals(o1);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o instanceof State) {
			State otherState = (State) o;
			if (otherState.hashCode != hashCode) return false;
			if (!this.changedQuestions.equals(otherState.changedQuestions)) return false;
			synchronized (usedQuestions) {
				if (!otherState.usedQuestions.equals(usedQuestions)) return false;
				int bitIndex = 0;
				for (Question question : usedQuestions.keySet()) {
					// only check if the bit for this question is set
					if (!changedQuestions.get(bitIndex++)) continue;
					Value value1 = getSession().getBlackboard().getValue(question);
					Value value2 = otherState.getSession().getBlackboard().getValue(question);
					if (!equals(value1, value2)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	public Session getSession() {
		return session;
	}
}
