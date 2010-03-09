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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import de.d3web.core.knowledge.terminology.DiagnosisState;
import de.d3web.scoring.Score;

public class ScoreRating implements Rating {
	private static NumberFormat formater = new DecimalFormat("#########");
	private double rating;

	private ConfigLoader config = ConfigLoader.getInstance();
	
	/**
	 * Creates new Rating with committed score
	 * @param rating committed score
	 */
	public ScoreRating(Score rating) {
		this(rating.getScore());
	}

	/**
	 * Creates new Rating with committed double
	 * @param rating committed double
	 */
	public ScoreRating(double rating) {
		this.rating = rating;
	}

	@Override
	public String toString() {
		return formater.format(rating);
	}

	/**
	 * Returns the double value of this Rating.
	 * @return double value representing the rating.
	 */
	public Double getRating() {
		return new Double(rating);
	}

	/**
	 * Sets rating to the double value of committed object
	 */
	public void setRating(Object o) {
		if (o instanceof Double)
			rating = ((Double) o).doubleValue();
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
		String compareOnlySymbolicStates = config.getProperty("compareOnlySymbolicStates");
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ScoreRating))
			return false;
		ScoreRating otherRating = (ScoreRating) obj;
		if(compareOnlySymbolicStates.equals("true")){
			DiagnosisState thisState = DiagnosisState.getState(this.rating);
			DiagnosisState otherState = DiagnosisState.getState(otherRating.rating);
			//System.out.println(this.rating+"-"+otherRating.rating+" <--> "+thisState + "-" + otherState);
			return thisState.equals(otherState);
		}else{
			if (Double.doubleToLongBits(rating) != Double.doubleToLongBits(otherRating.rating))
				return false;			
		}
		return true;
	}

	@Override
	public int compareTo(Rating o) {
		String compareOnlySymbolicStates = config.getProperty("compareOnlySymbolicStates");
		if (o instanceof ScoreRating) {
			ScoreRating otherRating = (ScoreRating) o;
			if(compareOnlySymbolicStates.equals("true")){
				DiagnosisState thisState = DiagnosisState.getState(this.rating);
				DiagnosisState otherState = DiagnosisState.getState(otherRating.rating);
				//System.out.println(this.rating+"-"+otherRating.rating+" <--> "+thisState + "-" + otherState);
				return thisState.compareTo(otherState);
			}else{
				if (rating < otherRating.rating)
					return -1;
				else if (rating > otherRating.rating)
					return 1;
				else
					return 0;				
			}
		}
		return 0;
	}

	@Override
	/**
	 * Checks if the score exceeds either 20 or -20 points.
	 */
	public boolean isProblemSolvingRelevant() {
		DiagnosisState s = DiagnosisState.getState(rating);
		return !(s.equals(DiagnosisState.UNCLEAR) || (s.equals(DiagnosisState.EXCLUDED)));
	}

}
