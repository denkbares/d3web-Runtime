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
package de.d3web.test.empiricalTesting;

import org.junit.Test;

import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.empiricaltesting.ScoreRating;
import de.d3web.empiricaltesting.StateRating;
import de.d3web.scoring.Score;
import de.d3web.testcase.TestCaseUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 22.07.2013
 */
public class CaseUtilsTest {

	@Test
	public void testScoringState() {
		ScoreRating scoreRating = new ScoreRating(Score.N1);
		StateRating stateRating = new StateRating(new Rating(State.ESTABLISHED));

		Rating r1 = TestCaseUtils.toRating(scoreRating);
		assertEquals(r1.getState(), State.UNCLEAR);

		Rating r2 = TestCaseUtils.toRating(stateRating);
		assertEquals(r2.getState(), State.ESTABLISHED);

		assertTrue(TestCaseUtils.toRating(null) == null);

	}

}
