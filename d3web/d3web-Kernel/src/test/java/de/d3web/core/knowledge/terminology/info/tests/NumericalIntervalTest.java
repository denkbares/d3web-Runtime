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

package de.d3web.core.knowledge.terminology.info.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.knowledge.terminology.info.NumericalInterval.IntervalException;

/**
 * Unit test for {@link NumericalInterval}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 27.08.2010
 */
public class NumericalIntervalTest {

	NumericalInterval four_seven_closed_closed;

	@Before
	public void setUp() throws Exception {
		four_seven_closed_closed = new NumericalInterval(4, 7);
	}

	/**
	 * Test method for {@link de.d3web.core.knowledge.terminology.info.NumericalInterval#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		assertThat(four_seven_closed_closed.hashCode(), is(not(0)));
	}

	/**
	 * Test for some invalid NumericalIntervals if {@link IntervalException}s
	 * are thrown
	 */
	@Test(expected = IntervalException.class)
	public void testNumericalIntervalThrowsIntervalException1() {
		new NumericalInterval(1, 1, true, true);
	}

	@Test(expected = IntervalException.class)
	public void testNumericalIntervalThrowsIntervalException2() {
		new NumericalInterval(2, 1, false, true);
	}

	@Test(expected = IntervalException.class)
	public void testNumericalIntervalThrowsIntervalException3() {
		new NumericalInterval(-3, -4, false, false);
	}

	/**
	 * Test method for {@link de.d3web.core.knowledge.terminology.info.NumericalInterval#contains(double)}.
	 */
	@Test
	public void testContains() {
		assertThat(four_seven_closed_closed.contains(5), is(true));
		assertThat(four_seven_closed_closed.contains(4), is(true));
		assertThat(four_seven_closed_closed.contains(7), is(true));
		assertThat(four_seven_closed_closed.contains(1), is(false));
		assertThat(four_seven_closed_closed.contains(9), is(false));
	}

	/**
	 * Test method for {@link NumericalInterval#setLeft(double)} and
	 * {@link NumericalInterval#getLeft()}.
	 */
	@Test
	public void testSetAndGetLeft() {
		assertThat(four_seven_closed_closed.getLeft(), is(4d));
		four_seven_closed_closed.setLeft(3.8);
		assertThat(four_seven_closed_closed.getLeft(), is(3.8));
	}

	/**
	 * Test method for {@link NumericalInterval#getRight(double)} and
	 * {@link NumericalInterval#SetRight()}.
	 */
	@Test
	public void testSetAndGetRight() {
		assertThat(four_seven_closed_closed.getRight(), is(7d));
		four_seven_closed_closed.setRight(6.6);
		assertThat(four_seven_closed_closed.getRight(), is(6.6));
	}

	/**
	 * Test method for {@link NumericalInterval#setLeftOpen(boolean)} and
	 * {@link NumericalInterval#isLeftOpen()}.
	 */
	@Test
	public void testSetAndGetLeftOpen() {
		assertThat(four_seven_closed_closed.isLeftOpen(), is(false));
		four_seven_closed_closed.setLeftOpen(true);
		assertThat(four_seven_closed_closed.isLeftOpen(), is(true));
	}

	/**
	 * Test method for {@link NumericalInterval#setRightOpen(boolean)} and
	 * {@link NumericalInterval#isRightOpen()}.
	 */
	@Test
	public void testSetAndGetRightOpen() {
		assertThat(four_seven_closed_closed.isRightOpen(), is(false));
		four_seven_closed_closed.setRightOpen(true);
		assertThat(four_seven_closed_closed.isRightOpen(), is(true));
	}

	/**
	 * Test method for {@link de.d3web.core.knowledge.terminology.info.NumericalInterval#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		assertThat(four_seven_closed_closed.equals(four_seven_closed_closed), is(true));
		assertThat(four_seven_closed_closed.equals(null), is(false));
		assertThat(four_seven_closed_closed.equals(new Object()), is(false));
		assertThat(four_seven_closed_closed.equals(new NumericalInterval(4, 7)), is(true));
		assertThat(four_seven_closed_closed.equals(new NumericalInterval(4, 7.1)), is(false));
	}

	/**
	 * Test method for {@link de.d3web.core.knowledge.terminology.info.NumericalInterval#intersects(de.d3web.core.knowledge.terminology.info.NumericalInterval)}.
	 */
	@Test
	public void testIntersects() {
		assertThat(four_seven_closed_closed.intersects(
				new NumericalInterval(1, 3)), is(false));
		assertThat(four_seven_closed_closed.intersects(
				new NumericalInterval(1, 4, true, true)), is(false));
		assertThat(four_seven_closed_closed.intersects(
				new NumericalInterval(1, 4)), is(true));
		assertThat(four_seven_closed_closed.intersects(
				new NumericalInterval(8, 9)), is(false));
		assertThat(four_seven_closed_closed.intersects(
				new NumericalInterval(7, 9, true, true)), is(false));
		assertThat(four_seven_closed_closed.intersects(
				new NumericalInterval(7, 9)), is(true));
		assertThat(four_seven_closed_closed.intersects(
				new NumericalInterval(4, 7)), is(true));
	}

	/**
	 * Test method for {@link de.d3web.core.knowledge.terminology.info.NumericalInterval#toString()}.
	 */
	@Test
	public void testToString() {
		assertThat(four_seven_closed_closed.toString(), is("[4.0, 7.0]"));
		assertThat(new NumericalInterval(6.1, 8.3, true, true).toString(), is("(6.1, 8.3)"));
	}

	/**
	 * Test method for {@link de.d3web.core.knowledge.terminology.info.NumericalInterval#compareTo(de.d3web.core.knowledge.terminology.info.NumericalInterval)}.
	 */
	@Test
	public void testCompareTo() {
		assertThat(four_seven_closed_closed.compareTo(new NumericalInterval(5, 8)), is(-1));
		assertThat(four_seven_closed_closed.compareTo(new NumericalInterval(3, 8)), is(1));
		assertThat(four_seven_closed_closed.compareTo(new NumericalInterval(4, 8)), is(-1));
		assertThat(four_seven_closed_closed.compareTo(new NumericalInterval(4, 6)), is(1));
		assertThat(four_seven_closed_closed.compareTo(new NumericalInterval(4, 7)), is(0));
	}

}
