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

import org.junit.Before;
import org.junit.Test;

import de.d3web.empiricaltesting.ScoreRating;

import static junit.framework.Assert.*;

/**
 * Basic unit test for the class {@link ScoreRating}.
 * 
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 02.05.2011
 */
public class ScoreRatingTest {

	private static final double EPS = 0.001;

	private ScoreRating scr_m80;
	private ScoreRating scr_20;
	private ScoreRating scr_40;
	private ScoreRating scr_80;
	private ScoreRating scr_9;

	@Before
	public void setUp() {
		scr_80 = new ScoreRating(80.0);
		scr_m80 = new ScoreRating(-80.0);
		scr_9 = new ScoreRating(9.0);
		scr_20 = new ScoreRating(20.0);
		scr_40 = new ScoreRating(40.0);
	}

	@Test
	public void testSetterGetter() {
		assertEquals(-80.0, scr_m80.getRating(), EPS);
		assertEquals(20.0, scr_20.getRating(), EPS);

		scr_m80.setRating(40.0);
		assertEquals(40, scr_m80.getRating(), EPS);
	}

	@Test
	public void testHashCodeEquals() {
		assertEquals(scr_20, scr_20);
		assertEquals(scr_20.hashCode(), scr_20.hashCode());
		assertTrue(scr_20.equals(scr_20));

		assertFalse(scr_20.hashCode() == scr_40.hashCode());
		assertFalse(scr_20.equals(scr_40));
		scr_40.setRating(20.0);
		assertTrue(scr_20.hashCode() == scr_40.hashCode());
		assertTrue(scr_20.equals(scr_40));
	}

	@Test
	public void testIsProblemsolvingRelevant() {
		assertTrue(scr_80.isProblemSolvingRelevant());
		assertTrue(scr_40.isProblemSolvingRelevant());
		assertTrue(scr_20.isProblemSolvingRelevant());
		assertFalse(scr_9.isProblemSolvingRelevant());
		assertFalse(scr_m80.isProblemSolvingRelevant());
	}

}
