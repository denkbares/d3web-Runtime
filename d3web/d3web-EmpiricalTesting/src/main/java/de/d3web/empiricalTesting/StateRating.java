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

package de.d3web.empiricalTesting;

import de.d3web.core.knowledge.terminology.DiagnosisState;

public class StateRating implements Rating {

	private DiagnosisState s;

	/**
	 * Creates a new DiagnosisState with committed String and creates a new
	 * StateRating with this new DiagnosisState.
	 * 
	 * @param rating
	 *            String representing the state.
	 */
	public StateRating(String rating) {
		s = new DiagnosisState(rating);
	}

	/**
	 * Creates new StateRating with committed DiagnosisState
	 * 
	 * @param s
	 *            State for this StateRating.
	 */
	public StateRating(DiagnosisState s) {
		this.s = s;
	}

	/**
	 * Returns the DiagnosisState of this Rating
	 * 
	 * @return State of this StateRating.
	 */
	public DiagnosisState getRating() {
		return s;
	}

	/**
	 * Sets DiagnosisState of this Rating to o
	 */
	public void setRating(Object o) {
		if (o instanceof DiagnosisState)
			s = (DiagnosisState) o;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((s == null) ? 0 : s.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof StateRating))
			return false;
		StateRating other = (StateRating) obj;
		if (s == null) {
			if (other.s != null)
				return false;
		}
		else if (!s.equals(other.s))
			return false;
		return true;
	}

	@Override
	public int compareTo(Rating o) {
		if (o instanceof StateRating) {
			return s.compareTo((DiagnosisState) o.getRating());
		}
		return 0;
	}

	@Override
	public String toString() {
		return s.toString();
	}

	@Override
	/**
	 * Checks, whether the rating is not UNCLEAR.
	 * @return true when the rating is not UNCLEAR
	 */
	public boolean isProblemSolvingRelevant() {
		return ((DiagnosisState) s).isRelevant();
	}

}
