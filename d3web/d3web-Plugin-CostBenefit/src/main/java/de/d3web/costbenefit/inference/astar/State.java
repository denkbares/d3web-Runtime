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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;

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

	private final Map<Question, Value> findings = new HashMap<Question, Value>();

	public State(Session session, Set<Question> preconditionQuestions) {
		for (Question q : preconditionQuestions) {
			findings.put(q, session.getBlackboard().getValue(q));
		}
		// System.out.println("\tstate created: " + findings.size() +
		// " entries");
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
		Value storedValue = findings.get(question);
		if (storedValue == null && value == null) {
			return true;
		}
		else if (storedValue != null) {
			return storedValue.equals(value);
		}
		else {
			return false;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof State) {
			State otherState = (State) o;
			return findings.equals(otherState.findings);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return findings.hashCode();
	}
}
