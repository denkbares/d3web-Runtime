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
package de.d3web.scoring;

import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.scoring.inference.PSMethodHeuristic;

/**
 * This class decorates the standard {@link Rating} of a solution by a numeric
 * score that is used to represent the derivation value of this solution in the
 * context of the problem-solver {@link PSMethodHeuristic}.
 * 
 * @author joba
 * @created 18.08.2010
 */
public class HeuristicRating extends Rating {

	private final double score;

	/**
	 * Creates a neutral (unclear) heuristic rating.
	 * 
	 * @param score the score of this rating
	 */
	public HeuristicRating() {
		super(State.UNCLEAR);
		this.score = 0.0;
	}

	/**
	 * Creates a heuristic rating representing the specified score.
	 * 
	 * @param score the score of this rating
	 */
	public HeuristicRating(Score score) {
		this(score.getScore());
	}

	/**
	 * Creates a heuristic rating representing the specified score.
	 * 
	 * @param score the score of this rating
	 */
	public HeuristicRating(double score) {
		super(scoreToState(score));
		this.score = score;
	}

	private static State scoreToState(double score) {
		if (score >= 42) {
			return State.ESTABLISHED;
		}
		if (score >= 10) {
			return State.SUGGESTED;
		}
		if (score >= -41) {
			return State.UNCLEAR;
		}
		return State.EXCLUDED;
	}

	/**
	 * Returns the score of this heuristic rating.
	 * 
	 * @return the rating score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * Sums up a number of HeuristicRatings, creating a new one that represents
	 * the combined score.
	 * 
	 * @param ratings the ratings to be summed
	 */
	public static HeuristicRating add(Score aprioriScore, HeuristicRating... ratings) {
		double score = 0.0;
		for (HeuristicRating rating : ratings) {
			score += rating.getScore();
		}
		if (aprioriScore != null) {
			if (aprioriScore.getAPriori() > 0) {
				score *= aprioriScore.getAPriori();
			}
			else {
				score += aprioriScore.getAPriori();
			}
		}
		return new HeuristicRating(score);
	}

	/**
	 * @return the score of this heuristic rating value as a double
	 */
	@Override
	public Object getValue() {
		return getScore();
	}
}
