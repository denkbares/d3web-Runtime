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

import java.util.Comparator;

import de.d3web.core.knowledge.terminology.Diagnosis;

public class RatedSolution implements Comparable<RatedSolution> {

	Diagnosis solution;
	Rating rating;

	static class InverseRatingComparator implements Comparator<RatedSolution> {
		@Override
		public int compare(RatedSolution o1, RatedSolution o2) {
			return (o1.compareTo(o2)) * (-1);
		}
	}
	
	public static class RatingComparatorByName implements Comparator<RatedSolution> {
		@Override
		public int compare(RatedSolution o1, RatedSolution o2) {
			return o1.getSolution().getText().compareTo(o2.getSolution().getText());
		}
	}	

	/**
	 * Creates new RatedSolution with committed Diagnosis and Rating
	 * @param solution Diagnosis
	 * @param rating Rating
	 */
	public RatedSolution(Diagnosis solution, Rating rating) {
		this.solution = solution;
		this.rating = rating;
	}

	/**
	 * String representation of this RatedSolution
	 * solution (rating)
	 * @return String representation of this RatedSolution.
	 */
	@Override
	public String toString() {
		return solution + " (" + rating + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rating == null) ? 0 : rating.hashCode());
		result = prime * result
				+ ((solution == null) ? 0 : solution.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof RatedSolution))
			return false;
		RatedSolution other = (RatedSolution) obj;
		if (rating == null) {
			if (other.rating != null)
				return false;
		} else if (!rating.equals(other.rating))
			return false;
		if (solution == null) {
			if (other.solution != null)
				return false;
		} else if (!solution.equals(other.solution))
			return false;
		return true;
	}

	/**
	 * Compares two RatedSolutions by first comparing the ratings 
	 * and then comparing the String names of the solutions (if
	 * ratings are equal).
	 */
	@Override
	public int compareTo(RatedSolution o) {
		int comp = rating.compareTo(o.rating);
		if (comp != 0)
			return comp;
		else
			return solution.getText().compareTo(o.solution.getText());
	}

	/**
	 * Sets Rating of this RatedSolution to rating
	 * @param rating new Rating
	 */
	public void update(Rating rating) {
		this.rating = rating;
	}

	/**
	 * Returns Solution of this RatedSolution
	 * @return solution
	 */
	public synchronized Diagnosis getSolution() {
		return solution;
	}

	/**
	 * Returns Rating of this RatedSolution
	 * @return rating
	 */
	public synchronized Rating getRating() {
		return rating;
	}

}
