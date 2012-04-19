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

package de.d3web.core.inference.condition;

import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;

/**
 * This condition checks, if a specified diagnosis is established or is in a
 * specified state. The composite pattern is used for this. This class is
 * "leaf".
 * 
 * @author Christian Betz
 */
public class CondDState extends TerminalCondition {

	private final Solution solution;
	private final Rating.State solutionState;

	/**
	 * Creates a new CondDState Expression for a specified solution and a
	 * rating-state of a certain state. Please note that only the state of the
	 * rating will be checked.
	 * 
	 * @param diagnose diagnosis to check
	 * @param rating the rating to take the state from
	 */
	public CondDState(Solution solution, Rating rating) {
		this(solution, rating.getState());
	}

	/**
	 * Creates a new CondDState Expression for a specified solution and a state
	 * to be checked.
	 * <p>
	 * Please note: if the rating is null, then this condition always returns
	 * true for that solution.
	 * 
	 * @param solution diagnosis to check
	 * @param solutionState state of the diagnosis to check
	 */
	public CondDState(Solution solution, Rating.State solutionState) {
		super(solution);
		this.solution = solution;
		this.solutionState = solutionState;
	}

	/**
	 * Creates a new CondDState Expression for a specified solution that always
	 * is true.
	 * 
	 * @param solution diagnosis to check
	 * @param solutionState state of the diagnosis to check
	 */
	public CondDState(Solution solution) {
		this(solution, (Rating.State) null);
	}

	/**
	 * This method checks the condition
	 * 
	 * Problem: UNCLEAR is default state of diagnosis. But we need to check for
	 * NoAnswerException, if no rule has ever changed the state of the
	 * diagnosis.
	 */
	@Override
	public boolean eval(Session session) throws NoAnswerException {
		// if we do not have a state this is always true
		if (solutionState == null) return true;
		return session.getBlackboard().getRating(solution).hasState(solutionState);
	}

	public Solution getSolution() {
		return solution;
	}

	public Rating.State getRatingState() {
		return solutionState;
	}

	@Override
	public String toString() {
		return "\u2190 CondDState diagnosis: "
				+ solution.getName()
				+ " value: "
				+ this.getRatingState();
	}

	@Override
	public boolean equals(Object other) {
		if (!super.equals(other)) {
			return false;
		}
		CondDState otherCDS = (CondDState) other;
		boolean test = true;
		if (this.getSolution() != null) {
			test = this.getSolution().equals(
					otherCDS.getSolution())
					&& test;
		}
		else {
			test = (otherCDS.getSolution() == null) && test;
		}

		if (this.getRatingState() != null) {
			test = this.getRatingState().equals(otherCDS.getRatingState())
					&& test;
		}
		else {
			test = (otherCDS.getRatingState() == null) && test;
		}
		return test;
	}

	@Override
	public int hashCode() {

		String str = getClass().toString();

		if (getSolution() != null) {
			str += getSolution().toString();
		}

		if (getRatingState() != null) {
			str += getRatingState().toString();
		}

		if (getTerminalObjects() != null) {
			str += getTerminalObjects().toString();
		}

		return str.hashCode();
	}
}