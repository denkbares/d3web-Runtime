/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.core.knowledge.terminology;

import java.util.Comparator;

import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;

/**
 * Stores the rating of a {@link Solution} instance.
 * 
 * @author joba, Christian Betz
 * @see Solution
 * @see ScoreRating
 */
public class Rating implements Value {

	/**
	 * The four possible states of a {@link Solution}:
	 * <UL>
	 * <li>ESTABLISHED: categorically derived
	 * <li>SUGGESTED: possibly derived
	 * <li>UNCLEAR: no exact state, similar to {@link UndefinedValue} for
	 * {@link Question}
	 * <li>EXCLUDED: categorically derived as no possible solution
	 * </UL>
	 * 
	 * @author joba (denkbares GmbH)
	 * @created 15.04.2010
	 */
	public enum State {
		EXCLUDED, UNCLEAR, SUGGESTED, ESTABLISHED;
	}

	private final State state;

	/**
	 * Creates a new solution state based on the specified {@link String} value.
	 * The {@link String} value is case insensitive for backward compatibility.
	 * 
	 * @param statename the name of the solution state
	 */
	public Rating(String statename) {
		this(State.valueOf(statename.toUpperCase()));
	}

	/**
	 * Creates a new solution state instance based on the specified
	 * {@link State} instance.
	 * 
	 * @param state the specified state instance
	 */
	public Rating(State state) {
		if (state == null) {
			throw new NullPointerException();
		}
		this.state = state;
	}

	/**
	 * Returns the name of this {@link State} instance.
	 * 
	 * @return the name of this state instance
	 */
	public String getName() {
		return this.state.name();
	}

	/**
	 * Returns the current {@link State} of this instance.
	 * 
	 * @return the current state
	 */
	public State getState() {
		return state;
	}

	/**
	 * Compares the specified {@link State} instance with the {@link State}
	 * instance contained in this instance.
	 * 
	 * @param state the specified state to be compared
	 * @return true, if both states are equal; false otherwise
	 */
	public boolean hasState(State state) {
		return this.state.equals(state);
	}

	/**
	 * Returns whether the solution rated by this rating is relevant to the user
	 * as a possible fault. This means that the rating is suggested or
	 * established.
	 * 
	 * @return true, if the solution has the state suggested or established
	 */
	public boolean isRelevant() {
		return hasState(State.SUGGESTED) || hasState(State.ESTABLISHED);
	}

	/**
	 * Compares this rating with the specified rating for equality.
	 * <p>
	 * <b>Note:</b> any sub-class this method may compare additional (more
	 * detailed) information about the rating, instead of the rating state only,
	 * such as like heuristic score of the rating. Therefore two ratings with
	 * the same state may be considered to be unequal (!).
	 * <p>
	 * This method uses the {@link #compareTo(Rating)} method to check for
	 * equality, therefore subclasses usually have no need to overwrite this
	 * method.
	 * 
	 * @param other the other {@link Rating} to be compared
	 * @return true, when both states are equal; false otherwise
	 */
	// TODO joba: The comment is obviously not right about using compareTo
	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof Rating)) {
			return false;
		}
		return this.state.equals(((Rating) other).state);
	}

	@Override
	public int hashCode() {
		return this.state.hashCode();
	}

	/**
	 * Compares this rating with the specified rating for order. Returns a
	 * negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified rating.
	 * <p>
	 * <b>Note:</b> any sub-class this method may compare additional (more
	 * detailed) information about the rating, instead of the rating state only,
	 * such as like heuristic score of the rating. Therefore two ratings with
	 * the same state may be considered to be unequal.
	 * <p>
	 * If a subclass overwrites this method it must (!) call this method if the
	 * state is compared to a instance with its class being identical to {@link
	 * DiagnosisState#class}. An overwriting method shall call this method if
	 * the state is compared to an instance of a different subclass.
	 * 
	 * @param other another {@link Rating}
	 * @return the comparison result according to the {@link Comparator}
	 *         definition
	 */
	@Override
	public int compareTo(Value other) {
		if (other instanceof Rating) {
			return this.state.ordinal() - ((Rating) other).state.ordinal();
		}
		return -1;
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * @return the {@link State} of this rating value
	 */
	@Override
	public Object getValue() {
		return getState();
	}

}