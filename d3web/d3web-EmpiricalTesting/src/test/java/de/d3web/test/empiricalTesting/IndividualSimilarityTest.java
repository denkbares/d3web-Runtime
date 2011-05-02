/*
 * Copyright (C) 2011 denkbares GmbH
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

import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.empiricaltesting.IndividualSimilarity;
import de.d3web.empiricaltesting.Rating;
import de.d3web.empiricaltesting.RatingSimilarity;
import de.d3web.empiricaltesting.ScoreRating;
import de.d3web.empiricaltesting.StateRating;

/**
 * Checks the function {@link IndividualSimilarity}.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 02.05.2011
 */
public class IndividualSimilarityTest {

	private static final double EPS = 0.001;
	RatingSimilarity sim;

	@Before
	public void setUp() {
		sim = new IndividualSimilarity();
	}

	@Test
	public void testSimilaritiesForStateRatings() {
		Rating sr_estab = new StateRating(new de.d3web.core.knowledge.terminology.Rating(
				State.ESTABLISHED));
		Rating sr_sugg = new StateRating(new de.d3web.core.knowledge.terminology.Rating(
				State.SUGGESTED));
		Rating sr_excl = new StateRating(new de.d3web.core.knowledge.terminology.Rating(
				State.EXCLUDED));

		// First, test the identity
		assertEquals(1, sim.rsim(sr_sugg, sr_sugg), EPS);
		assertEquals(1, sim.rsim(sr_estab, sr_estab), EPS);
		assertEquals(1, sim.rsim(sr_excl, sr_excl), EPS);

		// Second, test the differing ratings
		assertEquals(0, sim.rsim(sr_sugg, sr_estab), EPS);
		assertEquals(0, sim.rsim(sr_sugg, sr_excl), EPS);
		assertEquals(0, sim.rsim(sr_estab, sr_excl), EPS);
	}

	@Test
	public void testSimilaritiesForScoreRatings() {
		Rating scr_m80 = new ScoreRating(-80);
		Rating scr_20 = new ScoreRating(20);
		Rating scr_40 = new ScoreRating(40);
		Rating scr_80 = new ScoreRating(80);

		// First, test the identity
		assertEquals(1, sim.rsim(scr_m80, scr_m80), EPS);
		assertEquals(1, sim.rsim(scr_20, scr_20), EPS);
		assertEquals(1, sim.rsim(scr_40, scr_40), EPS);
		assertEquals(1, sim.rsim(scr_80, scr_80), EPS);

		// Second, test the differing ratings
		assertEquals(0, sim.rsim(scr_40, scr_m80), EPS);
		assertEquals(0, sim.rsim(scr_40, scr_20), EPS);
		assertEquals(0, sim.rsim(scr_m80, scr_20), EPS);
	}

}
