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

	private Solution solution;
	private Rating solutionState;

	/**
	 * Creates a new CondDState Expression:
	 * 
	 * @param diagnose
	 *            diagnosis to check
	 * @param solutionState
	 *            state of the diagnosis to check
	 * @param context
	 *            the context in which the diagnosis has the state
	 */
	public CondDState(
			Solution diagnosis,
			Rating solutionState) {
		super(diagnosis);
		this.solution = diagnosis;
		this.solutionState = solutionState;
	}

	/**
	 * This method checks the condition
	 * 
	 * Problem: UNCLEAR is default state of diagnosis. But we need to check for
	 * NoAnswerException, if no rule has ever changed the state of the
	 * diagnosis.
	 */
	public boolean eval(Session session) throws NoAnswerException {
		return solutionState.equals(session.getBlackboard().getRating(solution));
	}

	public Solution getDiagnosis() {
		return solution;
	}

	public Rating getStatus() {
		return solutionState;
	}

	public void setDiagnosis(Solution newDiagnosis) {
		solution = newDiagnosis;
	}

	public void setStatus(Rating newStatus) {
		solutionState = newStatus;
	}

	@Override
	public String toString() {
		return "\u2190 CondDState diagnosis: "
				+ solution.getId()
				+ " value: "
				+ this.getStatus();
	}

	@Override
	public boolean equals(Object other) {
		if (!super.equals(other)) return false;
		CondDState otherCDS = (CondDState) other;
		boolean test = true;
		if (this.getDiagnosis() != null) test = this.getDiagnosis().equals(
				otherCDS.getDiagnosis())
				&& test;
		else // == null
		test = (otherCDS.getDiagnosis() == null) && test;

		if (this.getStatus() != null) test = this.getStatus().equals(otherCDS.getStatus())
				&& test;
		else test = (otherCDS.getStatus() == null) && test;
		return test;
	}

	@Override
	public int hashCode() {

		String str = getClass().toString();

		if (getDiagnosis() != null)
			str += getDiagnosis().toString();

		if (getStatus() != null)
			str += getStatus().toString();

		if (getTerminalObjects() != null)
			str += getTerminalObjects().toString();

		return str.hashCode();
	}

	@Override
	public Condition copy() {
		return new CondDState(getDiagnosis(), getStatus());
	}

}