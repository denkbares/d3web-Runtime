/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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

package de.d3web.empiricaltesting;

import de.d3web.core.knowledge.terminology.Rating;

/**
 * @deprecated use {@link de.d3web.scoring.HeuristicRating} instead
 */
@SuppressWarnings("deprecation")
@Deprecated
public class StateRating implements de.d3web.empiricaltesting.Rating {

	private Rating rating;

	/**
	 * Creates a new DiagnosisState with committed String and creates a new
	 * StateRating with this new DiagnosisState.
	 * 
	 * @param state String representing the state.
	 */
	public StateRating(String state) {
		rating = new Rating(state);
	}

	/**
	 * Creates new StateRating with committed DiagnosisState
	 * 
	 * @param rating State for this StateRating.
	 */
	public StateRating(Rating rating) {
		this.rating = rating;
	}

	/**
	 * Returns the DiagnosisState of this Rating
	 * 
	 * @return State of this StateRating.
	 */
	@Override
	public Rating getRating() {
		return rating;
	}

	/**
	 * Sets DiagnosisState of this Rating to o
	 * 
	 * @deprecated no longer use this method, it will be removed with the next
	 *             release
	 */
	@Deprecated
	public void setRating(Object o) {
		if (o instanceof Rating) {
			rating = (Rating) o;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rating == null) ? 0 : rating.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof StateRating)) return false;
		StateRating other = (StateRating) obj;
		if (rating == null) {
			if (other.rating != null) return false;
		}
		else if (!rating.equals(other.rating)) return false;
		return true;
	}

	@Override
	public int compareTo(de.d3web.empiricaltesting.Rating o) {
		if (o instanceof StateRating) {
			return rating.compareTo((Rating) o.getRating());
		}
		return 0;
	}

	@Override
	public String toString() {
		return rating.toString();
	}

	@Override
	/**
	 * Checks, whether the rating is not UNCLEAR.
	 * @return true when the rating is not UNCLEAR
	 */
	public boolean isProblemSolvingRelevant() {
		return rating.isRelevant();
	}

}
