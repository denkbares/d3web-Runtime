/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.d3web.core.knowledge;

import de.d3web.core.session.Value;

public class Indication implements Value {

	public enum State {
		/**
		 * An InterviewObject with this state must not be shown to the user
		 */
		CONTRA_INDICATED,
		/**
		 * An InterviewObject with this state is not indicated, it should not be presented to the user
		 */
		NEUTRAL,
		/**
		 * An InterviewObject with this state should be presented to the user, if it's parent is presented to the user
		 */
		RELEVANT,
		/**
		 * An InterviewObject with this state should be presented to the user, even if it is already on the agenda
		 */
		MULTIPLE_INDICATED,
		/**
		 * An InterviewObject with this state should be presented to the user
		 */
		INDICATED,
		/**
		 * An InterviewObject with this state should be presented to the user, it should be shown before elements only
		 * having the state INDICATED
		 */
		INSTANT_INDICATED,
		/**
		 * An InterviewObject with this state should be presented to the user, even if the user has already processed
		 * that element
		 */
		REPEATED_INDICATED

	}

	private final State state;

	private final double sorting;

	/**
	 * Creates a new indication value based on the string representation. The string representation is case insensitive
	 * for backward compatibility.
	 *
	 * @param name the name of the indication state
	 */
	public Indication(String name, double sorting) {
		this(State.valueOf(name.toUpperCase()), sorting);
	}

	/**
	 * Creates a new indication value based on the indication state and sorting rating
	 *
	 * @param state   the state of the new indication value
	 * @param sorting the rating of the sorting of the new indication
	 */
	public Indication(State state, double sorting) {
		this.sorting = sorting;
		if (state == null) {
			throw new NullPointerException();
		}
		this.state = state;
	}

	/**
	 * Returns the state's name of this indication value.
	 *
	 * @return the state's name
	 */
	public String getName() {
		return this.state.name();
	}

	/**
	 * Returns the current {@link State} of this indication value.
	 *
	 * @return the current state
	 */
	public State getState() {
		return state;
	}

	/**
	 * @return the {@link State} of this indication value
	 */
	@Override
	public Object getValue() {
		return getState();
	}

	/**
	 * Returns whether the state of this indication equals to the specified state.
	 *
	 * @param state the state to be checked
	 * @return whether the state is equal to the specified one
	 */
	public boolean hasState(State state) {
		return this.state == state;
	}

	/**
	 * Returns whether the indication state signals that the interview element should be asked to the user or not.
	 *
	 * @return the relevance due to this interview state
	 */
	public boolean isRelevant() {
		return this.state == State.INDICATED
				|| this.state == State.INSTANT_INDICATED
				|| this.state == State.RELEVANT
				|| this.state == State.MULTIPLE_INDICATED
				|| this.state == State.REPEATED_INDICATED;
	}

	/**
	 * Returns whether the indication state signals that the interview element should be asked to the user or not.
	 *
	 * @return the relevance due to this interview state
	 */
	public boolean isIndicated() {
		return this.state == State.INDICATED
				|| this.state == State.INSTANT_INDICATED
				|| this.state == State.MULTIPLE_INDICATED
				|| this.state == State.REPEATED_INDICATED;
	}

	/**
	 * Returns whether the indication state signals that the interview element is excluded to be asked.
	 *
	 * @return whether the interview element is excluded from the interview
	 */
	public boolean isContraIndicated() {
		return this.state == State.CONTRA_INDICATED;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof Indication)) {
			return false;
		}
		return this.state == ((Indication) other).state;
	}

	@Override
	public int hashCode() {
		return this.state.hashCode();
	}

	@Override
	public int compareTo(Value other) {
		if (other == null) {
			throw new NullPointerException();
		}
		if (other instanceof Indication) {
			State otherState = ((Indication) other).state;
			int otherOrdinal = otherState.ordinal();
			int ordinal = state.ordinal();
			if (otherOrdinal == ordinal) {
				return Double.compare(this.sorting, ((Indication) other).sorting);
			}
			else {
				return ordinal - otherOrdinal;
			}
		}
		else {
			return -1;
		}
	}

	@Override
	public String toString() {
		return getName();
	}

	public double getSorting() {
		return sorting;
	}
}
