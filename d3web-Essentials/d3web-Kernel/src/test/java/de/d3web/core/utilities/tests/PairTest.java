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

package de.d3web.core.utilities.tests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.d3web.utils.Pair;

/**
 * Unit test for {@link Pair}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 03.09.2010
 */
public class PairTest {

	String stringObjectOne;
	Double doubleObjectTwo;

	Pair<String, Double> pairUnderTest;

	@Before
	public void setUp() throws Exception {
		stringObjectOne = new String("stringObjectOne");
		doubleObjectTwo = new Double(2.0);

		pairUnderTest = new Pair<String, Double>(stringObjectOne, doubleObjectTwo);
	}

	/**
	 * Test method for {@link de.d3web.utils.Pair#getA()}.
	 */
	@Test
	public void testGetA() {
		String objectA = pairUnderTest.getA();
		assertThat(objectA, is(equalTo(stringObjectOne)));
	}

	/**
	 * Test method for {@link de.d3web.utils.Pair#getB()}.
	 */
	@Test
	public void testGetB() {
		Double objectB = pairUnderTest.getB();
		assertThat(objectB, is(equalTo(doubleObjectTwo)));
	}

	/**
	 * Test method for {@link de.d3web.utils.Pair#toString()}.
	 */
	@Test
	public void testToString() {
		String string = pairUnderTest.toString();
		assertThat(string, is("#Pair[stringObjectOne; 2.0]"));
	}

}
