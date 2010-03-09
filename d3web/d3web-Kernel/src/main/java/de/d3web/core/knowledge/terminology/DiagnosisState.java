/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.core.knowledge.terminology;

import de.d3web.core.session.Value;
import de.d3web.scoring.DiagnosisScore;
import de.d3web.scoring.HeuristicRating;

/**
 * Stores the state of a diagnosis in context to a problem-solving method. The
 * state is computed with respect to the score a diagnosis.
 * 
 * @author joba, Christian Betz
 * @see Diagnosis
 * @see DiagnosisScore
 */
public class DiagnosisState implements Value {

	public enum State {
		EXCLUDED, UNCLEAR, SUGGESTED, ESTABLISHED;
	}

	/**
	 * The Diagnosis is meant to be excluded.
	 * 
	 * @deprecated you should not use these constants any longer. For checking
	 *             the state use {@link #hasState(State state)} instead.
	 */
	public static DiagnosisState EXCLUDED = new DiagnosisState(State.EXCLUDED);
	/**
	 * The Diagnosis is meant to be unclear.
	 * 
	 * @deprecated you should not use these constants any longer. For checking
	 *             the state use {@link #hasState(State state)} instead.
	 */
	public static DiagnosisState UNCLEAR = new DiagnosisState(State.UNCLEAR);
	/**
	 * The Diagnosis is meant to be suggested (nearly established).
	 * 
	 * @deprecated you should not use these constants any longer. For checking
	 *             the state use {@link #hasState(State state)} instead.
	 */
	public static DiagnosisState SUGGESTED = new DiagnosisState(State.SUGGESTED);
	/**
	 * The Diagnosis is meant to be established.
	 * 
	 * @deprecated you should not use these constants any longer. For checking
	 *             the state use {@link #hasState(State state)} instead.
	 */
	public static DiagnosisState ESTABLISHED = new DiagnosisState(State.ESTABLISHED);

	private final State state;

	/**
	 * Creates a new solution rating value based on the string representation.
	 * The string representation is case insensitive for backward compatibility.
	 * 
	 * @param name
	 *            the name of the rating state
	 */
	public DiagnosisState(String name) {
		this(State.valueOf(name.toUpperCase()));
	}

	/**
	 * Creates a new solution rating value based on the rating state.
	 * 
	 * @param state
	 *            the state of the new rating value
	 */
	public DiagnosisState(State state) {
		if (state == null) throw new NullPointerException();
		this.state = state;
	}

	/**
	 * Returns the state's name of this rating value.
	 * 
	 * @return the state's name
	 */
	public String getName() {
		return this.state.name();
	}

	/**
	 * Returns the current state of this rating value.
	 * 
	 * @return the current state
	 */
	public State getState() {
		return state;
	}

	/**
	 * Returns whether the state of this rating equals to the specified state.
	 * 
	 * @param state
	 *            the state to be checked
	 * @return whether the state is equal to the specified one
	 */
	public boolean hasState(State state) {
		return this.state.equals(state);
	}

	/**
	 * Returns whether the solution rated by this rating is relevant to the user
	 * as a possible fault. This means that the rating is suggested or
	 * established.
	 * 
	 * @return if the solution is suggested or established
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
	 * This method uses the {@link #compareTo(DiagnosisState)} method to check
	 * for equality, therefore subclasses usually have no need to overwrite this
	 * method.
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		if (!(other instanceof DiagnosisState)) return false;
		return this.state.equals(((DiagnosisState) other).state);
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
	 */
	@Override
	public int compareTo(Value other) {
		if (other instanceof DiagnosisState) {
			return this.state.ordinal() - ((DiagnosisState)other).state.ordinal();
		}
		return -1;
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * @return the first status in allStati, for which checkState returns true;
	 * @deprecated new HeuristicRating(score) instead
	 */
	public static DiagnosisState getState(double score) {
		return new HeuristicRating(score);
	}

	/**
	 * @return the first status in allStati, for which checkState returns true
	 *         for the score of the given DiagnosisScore;
	 * @deprecated new HeuristicRating(diagnosisScore.getScore()) instead
	 */
	public static DiagnosisState getState(DiagnosisScore diagnosisScore) {
		if (diagnosisScore == null) {
			return null;
		}
		return getState(diagnosisScore.getScore());
	}

	@Override
	public Object getValue() {
		return getState();
	}

}