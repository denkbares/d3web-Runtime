/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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

package de.d3web.core.knowledge.terminology.tests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.session.values.UndefinedValue;

/**
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 24.08.2010
 */
public class RatingTest {

	Rating EXCLUDED, UNCLEAR, SUGGESTED, ESTABLISHED;

	@Before
	public void setUp() throws Exception {
		EXCLUDED = new Rating("EXCLUDED");
		UNCLEAR = new Rating("unclear");
		SUGGESTED = new Rating(Rating.State.SUGGESTED);
		ESTABLISHED = new Rating("esTabLIShed");
	}

	/**
	 * Summary: Only {@link State} values are allowed to create a new Rating
	 * 
	 * @created 24.08.2010
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalRating() {
		@SuppressWarnings("unused")
		Rating illegalRating = new Rating("illegalRating");
	}

	/**
	 * Summary: new Rating(null) should throw a NullPointerException
	 * 
	 * @created 24.08.2010
	 */
	@Test(expected = NullPointerException.class)
	public void testNullRating() {
		State nullState = null;
		@SuppressWarnings("unused")
		Rating nullRating = new Rating(nullState);
	}

	/**
	 * Summary: Tests all the methods of the class Rating (combined for the sake
	 * of easiness)
	 * 
	 * @created 24.08.2010
	 */
	@Test
	public void testRatingMethods() {
		// test getState() and getValue()
		assertThat(EXCLUDED.getState(), is(equalTo(Rating.State.EXCLUDED)));
		assertThat((State) EXCLUDED.getValue(), is(equalTo(Rating.State.EXCLUDED)));
		// test hasState(State)
		assertThat(SUGGESTED.hasState(Rating.State.SUGGESTED), is(true));
		assertThat(UNCLEAR.hasState(Rating.State.ESTABLISHED), is(false));
		// test isRelevant()
		assertThat(EXCLUDED.isRelevant(), is(false));
		assertThat(UNCLEAR.isRelevant(), is(false));
		assertThat(SUGGESTED.isRelevant(), is(true));
		assertThat(ESTABLISHED.isRelevant(), is(true));
		// test equals()
		assertThat(EXCLUDED.equals(null), is(false));
		assertThat(EXCLUDED.equals(new Object()), is(false));
		assertThat(EXCLUDED.equals(new Rating("eXcLuDeD")), is(true));
		// test hashCode
		assertThat(ESTABLISHED.hashCode(), is(not(0)));
		// test compareTo()
		assertThat(SUGGESTED.compareTo(UndefinedValue.getInstance()), is(-1));
		assertThat(SUGGESTED.compareTo(ESTABLISHED), is(-1));
		assertThat(ESTABLISHED.compareTo(SUGGESTED), is(1));
		assertThat(EXCLUDED.compareTo(ESTABLISHED), is(-3));
		assertThat(ESTABLISHED.compareTo(EXCLUDED), is(3));

	}
}
