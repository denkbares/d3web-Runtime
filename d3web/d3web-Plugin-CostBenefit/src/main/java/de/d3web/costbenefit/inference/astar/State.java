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

import java.util.Set;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;

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

	private final Set<Question> questions;
	// private final Value[] values;
	private final Session session;
	private final int hashCode;

	public State(Session session, Set<Question> preconditionQuestions) {
		this.session = session;
		this.questions = preconditionQuestions;

		int hashCode = 1;
		Blackboard blackboard = session.getBlackboard();
		// this.values = new Value[questions.size()];
		// int index = 0;
		for (Question q : questions) {
			Value value = blackboard.getValue(q);
			hashCode = 31 * hashCode + value.hashCode();
			// values[index++] = value;
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
		Value storedValue = session.getBlackboard().getValue(question);
		return equals(value, storedValue);
	}

	public final Value getValue(Question question) {
		return session.getBlackboard().getValue(question);
	}

	private boolean equals(Object o1, Object o2) { // NOSONAR
		if (o1 == o2) return true;
		if (o1 == null) return false;
		if (o2 == null) return false;
		return o2.equals(o1);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o instanceof State) {
			State otherState = (State) o;
			if (otherState.hashCode != hashCode) return false;
			// return Arrays.equals(otherState.values, values);
			if (!otherState.questions.equals(questions)) return false;
			for (Question question : questions) {
				Value value1 = session.getBlackboard().getValue(question);
				Value value2 = otherState.session.getBlackboard().getValue(question);
				if (!equals(value1, value2)) return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}
