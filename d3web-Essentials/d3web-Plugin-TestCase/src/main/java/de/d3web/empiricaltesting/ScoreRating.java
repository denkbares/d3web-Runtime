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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.scoring.HeuristicRating;
import de.d3web.scoring.Score;

/**
 * @deprecated use {@link Score} instead
 */
@SuppressWarnings("deprecation")
@Deprecated
public class ScoreRating implements Rating {

	private static final NumberFormat formatter = new DecimalFormat("#######.##");
	private double rating;

	/**
	 * Creates new Rating with committed score
	 *
	 * @param rating committed score
	 */
	public ScoreRating(Score rating) {
		this(rating.getScore());
	}

	/**
	 * Creates new Rating with committed double
	 *
	 * @param rating committed double
	 */
	public ScoreRating(double rating) {
		this.rating = rating;
	}

	@Override
	public String toString() {
		return formatter.format(rating);
	}

	/**
	 * Returns the double value of this Rating.
	 *
	 * @return double value representing the rating.
	 */
	@Override
	public Double getRating() {
		return rating;
	}

	/**
	 * Sets rating to the double value of committed object
	 */
	public void setRating(Object o) {
		if (o instanceof Double) rating = (Double) o;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(rating);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof ScoreRating)) return false;
		ScoreRating otherRating = (ScoreRating) obj;
		return Double.doubleToLongBits(rating) == Double.doubleToLongBits(otherRating.rating);
	}

	@Override
	public int compareTo(Rating o) {
		if (o instanceof ScoreRating) {
			ScoreRating otherRating = (ScoreRating) o;
			if (rating < otherRating.rating) {
				return -1;
			}
			else if (rating > otherRating.rating) {
				return 1;
			}
			else {
				return 0;
			}
		}
		return 0;
	}

	/**
	 * Checks if the score exceeds either 20 or -20 points.
	 */
	@Override
	public boolean isProblemSolvingRelevant() {
		HeuristicRating hr = new HeuristicRating(rating);
		return !(hr.hasState(State.UNCLEAR) || hr.hasState(State.EXCLUDED));
	}

}
