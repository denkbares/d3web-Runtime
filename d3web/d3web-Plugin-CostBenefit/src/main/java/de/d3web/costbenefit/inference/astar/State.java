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

	private final Set<Question> questions;
	private final Session session;
	private final int hashCode;

	public State(Session session, Set<Question> preconditionQuestions) {
		this.session = session;
		this.questions = preconditionQuestions;

		int hashCode = 1;
		Blackboard blackboard = session.getBlackboard();
		// we only have to use the covered values, so if the
		// blackboard is an original one, we do not access any value at all
		if (blackboard instanceof DecoratedBlackboard) {
			DecoratedBlackboard decorated = (DecoratedBlackboard) blackboard;
			for (Question q : questions) {
				Value value = decorated.getChangedValue(q);
				if (value != null) {
					hashCode = 31 * hashCode + value.hashCode();
				}
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
		Value storedValue = session.getBlackboard().getValue(question);
		return equals(value, storedValue);
	}

	public final Value getValue(Question question) {
		return session.getBlackboard().getValue(question);
	}

	private static boolean equals(Object o1, Object o2) { // NOSONAR
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
			synchronized (questions) {
				if (otherState.questions.equals(questions)) return false;
				Blackboard thisBoard = session.getBlackboard();
				Blackboard otherBoard = otherState.session.getBlackboard();
				DecoratedBlackboard thisDB = thisBoard instanceof DecoratedBlackboard
						? (DecoratedBlackboard) thisBoard
						: null;
				DecoratedBlackboard otherDB = otherBoard instanceof DecoratedBlackboard
						? (DecoratedBlackboard) otherBoard
						: null;
				// if both blackboards are equal (e.g. both null) return true;
				if (thisDB == otherDB) return true;
				// of only one exists, test if there are no decorated value
				// first try this one is null
				if (thisDB == null) {
					for (Question question : questions) {
						if (otherDB.getChangedValue(question) != null) return false;
					}
					return true;
				}
				// also try other one is null
				if (otherDB == null) {
					for (Question question : questions) {
						if (thisDB.getChangedValue(question) != null) return false;
					}
					return true;
				}
				// if both are decorating, compare their changed values only
				for (Question question : questions) {
					if (!DecoratedBlackboard.hasEqualValue(question, thisDB, otherDB)) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	public Set<Question> getQuestions() {
		return questions;
	}
}
