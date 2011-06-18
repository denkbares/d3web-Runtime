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

package de.d3web.casegeneration;

import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.empiricaltesting.Rating;
import de.d3web.empiricaltesting.ScoreRating;
import de.d3web.empiricaltesting.StateRating;
import de.d3web.scoring.HeuristicRating;

public class DefaultRatingStrategy implements RatingStrategy {

	@Override
	public Rating getRatingFor(Solution solution, Session session) {
		de.d3web.core.knowledge.terminology.Rating state =
				session.getBlackboard().getRating(solution);
		if (state instanceof HeuristicRating) {
			HeuristicRating hr = (HeuristicRating) state;
			return new ScoreRating(hr.getScore());
		}
		else {
			return new StateRating(state);
		}
	}
}