/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.d3web.test.empiricalTesting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.empiricaltesting.CaseUtils;
import de.d3web.empiricaltesting.ScoreRating;
import de.d3web.empiricaltesting.StateRating;
import de.d3web.scoring.Score;

/**
 * 
 * @author jochenreutelshofer
 * @created 22.07.2013
 */
public class CaseUtilsTest {

	@Test
	public void testBadCharsFilter() {
		String badString1 = "a: =()[]{},.?/\\-#'b";
		String removed1 = CaseUtils.getInstance().removeBadChars(badString1);
		assertEquals(removed1, "ab");
	}

	@Test
	public void testPrettify() {
		String badString1 = "a<b>";
		String removed1 = CaseUtils.getInstance().pretty(badString1);
		assertEquals(removed1, "akleinerbgroesser");
	}

	@Test
	public void testScoringState() {
		ScoreRating scoreRating = new ScoreRating(Score.N1);
		StateRating stateRating = new StateRating(new Rating(State.ESTABLISHED));

		Rating r1 = CaseUtils.getState(scoreRating);
		assertEquals(r1.getState(), State.UNCLEAR);

		Rating r2 = CaseUtils.getState(stateRating);
		assertEquals(r2.getState(), State.ESTABLISHED);

		assertTrue(CaseUtils.getState(null) == null);

	}

}
