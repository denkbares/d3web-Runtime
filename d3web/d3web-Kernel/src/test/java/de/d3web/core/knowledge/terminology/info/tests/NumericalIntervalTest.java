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


	/**
	 * Test method for
	 * {@link de.d3web.core.knowledge.terminology.info.NumericalInterval#hashCode()}
	 */
	@Test
	public void testHashCode() {
		assertThat(parse("[4, 7]").hashCode(), is(not(0)));
	}

	/**
	 * Test for some invalid NumericalIntervals if {@link IntervalException}s
	 * are thrown
	 */
	@Test(expected = IntervalException.class)
	public void testNumericalIntervalThrowsIntervalException1() {
		new NumericalInterval(1, 1, true, true).checkValidity();
	}

	@Test(expected = IntervalException.class)
	public void testNumericalIntervalThrowsIntervalException2() {
		new NumericalInterval(1, 1, false, true).checkValidity();
	}

	@Test(expected = IntervalException.class)
	public void testNumericalIntervalThrowsIntervalException3() {
		new NumericalInterval(1, 1, true, false).checkValidity();
	}

	@Test(expected = IntervalException.class)
	public void testNumericalIntervalThrowsIntervalException4() {
		new NumericalInterval(2, 1, false, true).checkValidity();
	}

	@Test(expected = IntervalException.class)
	public void testNumericalIntervalThrowsIntervalException5() {
		new NumericalInterval(-3, -4, false, false).checkValidity();
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.knowledge.terminology.info.NumericalInterval#contains(double)}
	 * .
	 */
	@Test
	public void testContains() {
		assertThat(parse("[4, 7]").contains(5), is(true));
		assertThat(parse("[4, 7]").contains(4), is(true));
		assertThat(parse("[4, 7]").contains(7), is(true));
		assertThat(parse("[4, 7]").contains(1), is(false));
		assertThat(parse("[4, 7]").contains(9), is(false));
	}

	@Test
	public void testValueOf() {
		assertThat(NumericalInterval.valueOf("[1 2]"),
				is(new NumericalInterval(1, 2, false, false)));

		assertThat(NumericalInterval.valueOf("[1.2 2.3]"),
				is(new NumericalInterval(1.2, 2.3, false, false)));

		assertThat(NumericalInterval.valueOf("]2 3]"),
				is(new NumericalInterval(2, 3, true, false)));

		assertThat(NumericalInterval.valueOf("]4 5["),
				is(new NumericalInterval(4, 5, true, true)));

	}

	/**
	 * Test method for {@link NumericalInterval#setLeft(double)} and
	 * {@link NumericalInterval#getLeft()}.
	 */
	@Test
	public void testGetLeft() {
		assertThat(parse("[4, 7]").getLeft(), is(4d));
	}

	/**
	 * Test method for {@link NumericalInterval#getRight(double)} and
	 * {@link NumericalInterval#SetRight()}.
	 */
	@Test
	public void testGetRight() {
		assertThat(parse("[4, 7]").getRight(), is(7d));
	}

	/**
	 * Test method for {@link NumericalInterval#setLeftOpen(boolean)} and
	 * {@link NumericalInterval#isLeftOpen()}.
	 */
	@Test
	public void testGetLeftOpen() {
		assertThat(parse("[4, 7]").isLeftOpen(), is(false));
	}

	/**
	 * Test method for {@link NumericalInterval#setRightOpen(boolean)} and
	 * {@link NumericalInterval#isRightOpen()}.
	 */
	@Test
	public void testGetRightOpen() {
		assertThat(parse("[4, 7]").isRightOpen(), is(false));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.knowledge.terminology.info.NumericalInterval#equals(java.lang.Object)}
	 */
	@Test
	public void testEqualsObject() {
		assertThat(parse("[4, 7]").equals(parse("[4, 7]")), is(true));
		assertThat(parse("[4, 7]").equals(null), is(false));
		assertThat(parse("[4, 7]").equals(new Object()), is(false));
		assertThat(parse("[4, 7]").equals(parse("[4, 7]")), is(true));
		assertThat(parse("[4, 7]").equals(parse("[4, 7.1]")), is(false));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.knowledge.terminology.info.NumericalInterval#intersects(de.d3web.core.knowledge.terminology.info.NumericalInterval)}
	 */
	@Test
	public void testIntersects() {
		assertThat(parse("[4, 7]").intersects(parse("[1, 3]")), is(false));
		assertThat(parse("[4, 7]").intersects(parse("(1, 4)")), is(false));
		assertThat(parse("[4, 7]").intersects(parse("[1, 4]")), is(true));
		assertThat(parse("[4, 7]").intersects(parse("[8, 9]")), is(false));
		assertThat(parse("[4, 7]").intersects(parse("(7, 9)")), is(false));
		assertThat(parse("[4, 7]").intersects(parse("[7, 9]")), is(true));
		assertThat(parse("[4, 7]").intersects(parse("[4, 7]")), is(true));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.knowledge.terminology.info.NumericalInterval#toString()}
	 */
	@Test
	public void testToString() {
		assertThat(parse("[4, 7]").toString(), is("[4.0 7.0]"));
		assertThat(new NumericalInterval(6.1, 8.3, true, true).toString(), is("]6.1 8.3["));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.knowledge.terminology.info.NumericalInterval#compareTo(de.d3web.core.knowledge.terminology.info.NumericalInterval)}
	 */
	@Test
	public void testCompareTo() {
		assertThat(parse("[4, 7]").compareTo(parse("[5, 8]")), is(-1));
		assertThat(parse("[4, 7]").compareTo(parse("[3, 8]")), is(1));
		assertThat(parse("[4, 7]").compareTo(parse("[4, 8]")), is(-1));
		assertThat(parse("[4, 7]").compareTo(parse("[4, 6]")), is(1));
		assertThat(parse("[4, 7]").compareTo(parse("[4, 7]")), is(0));
		assertThat(parse("[4, 7]").compareTo(parse("(4, 7)")), is(-1));

		assertThat(parse("(4, 7)").compareTo(parse("(4, 7)")), is(0));
		assertThat(parse("[4, 7]").compareTo(parse("(4, 7)")), is(-1));
		assertThat(parse("(4, 7]").compareTo(parse("[4, 7)")), is(1));

		assertThat(parse("[4, 7)").compareTo(parse("[4, 7]")), is(-1));
		assertThat(parse("[4, 7]").compareTo(parse("[4, 7)")), is(1));
	}

	@Test
	public void testContainsInterval() throws Exception {
		// not containing
		assertThat(parse("[4, 7]").contains(parse("[8, 9]")), is(false));
		assertThat(parse("[4, 7]").contains(parse("(2, 3)")), is(false));

		// containing
		assertThat(parse("[4, 7]").contains(parse("[5, 6]")), is(true));
		assertThat(parse("[4, 7]").contains(parse("(5, 6)")), is(true));

		// overlapping
		assertThat(parse("[4, 7]").contains(parse("[3, 6]")), is(false));
		assertThat(parse("[4, 7]").contains(parse("(5, 8)")), is(false));

		// boundary cases
		assertThat(parse("[4, 7]").contains(parse("[4, 7]")), is(true));
		assertThat(parse("[4, 7]").contains(parse("[4, 7)")), is(true));
		assertThat(parse("[4, 7]").contains(parse("(4, 7]")), is(true));
		assertThat(parse("[4, 7]").contains(parse("(4, 7)")), is(true));

		assertThat(parse("[4, 7)").contains(parse("(4, 7)")), is(true));
		assertThat(parse("(4, 7]").contains(parse("(4, 7)")), is(true));
		assertThat(parse("(4, 7)").contains(parse("(4, 7)")), is(true));

		assertThat(parse("(4, 7)").contains(parse("[4, 7]")), is(false));
		assertThat(parse("(4, 7)").contains(parse("[4, 7)")), is(false));
		assertThat(parse("(4, 7)").contains(parse("(4, 7]")), is(false));
		assertThat(parse("(4, 7]").contains(parse("[4, 7)")), is(false));

	}

	@Test
	public void testIsEmpty() throws Exception {
		assertThat(parse("[4, 7]").isEmpty(), is(false));
		assertThat(parse("[4, 4]").isEmpty(), is(false));
		assertThat(parse("[-4, -4]").isEmpty(), is(false));
		assertThat(parse("[-4, -3]").isEmpty(), is(false));
		assertThat(parse("[-Infinity, Infinity]").isEmpty(), is(false));

		assertThat(parse("[4, 4)").isEmpty(), is(true));
		assertThat(parse("(4, 4]").isEmpty(), is(true));
		assertThat(parse("(4, 4)").isEmpty(), is(true));
		assertThat(parse("[Infinity, -Infinity]").isEmpty(), is(true));

	}

	@Test
	public void testIntersect() throws Exception {
		// equal boundaries
		assertThat(parse("[4, 7]").intersect(parse("[4, 7]")), is(parse("[4, 7]")));
		assertThat(parse("(4, 7)").intersect(parse("[4, 7]")), is(parse("(4, 7)")));
		assertThat(parse("[4, 7)").intersect(parse("(4, 7]")), is(parse("(4, 7)")));
		assertThat(parse("(4, 7]").intersect(parse("[4, 7)")), is(parse("(4, 7)")));
		assertThat(parse("(4, 7)").intersect(parse("(4, 7)")), is(parse("(4, 7)")));

		// containing
		assertThat(parse("(4, 7)").intersect(parse("[5, 6]")), is(parse("[5, 6]")));
		assertThat(parse("[4, 7]").intersect(parse("(5, 6)")), is(parse("(5, 6)")));

		// overlapping
		assertThat(parse("(4, 7)").intersect(parse("[5, 8]")), is(parse("[5, 7)")));
		assertThat(parse("(4, 7]").intersect(parse("(5, 8)")), is(parse("(5, 7]")));

		assertThat(parse("(6, 8)").intersect(parse("[7, 9]")), is(parse("[7, 8)")));
		assertThat(parse("(6, 8]").intersect(parse("(7, 9)")), is(parse("(7, 8]")));
	}

	@Test
	public void testParse() throws Exception {
		assertThat(parse("[4, 7]"), is(new NumericalInterval(4, 7)));
		assertThat(parse("[4, 7)"), is(new NumericalInterval(4, 7, false, true)));
		assertThat(parse("(4, 7]"), is(new NumericalInterval(4, 7, true, false)));
		assertThat(parse("(4, 7)"), is(new NumericalInterval(4, 7, true, true)));
		assertThat(parse("[-7, -4]"), is(new NumericalInterval(-7, -4)));

		assertThat(parse("(4.56, 7.89)"), is(new NumericalInterval(4.56, 7.89, true, true)));
	}

	private static NumericalInterval parse(String s) {
		String[] split = s.split(",");
		String leftSide = split[0].trim();
		String rightSide = split[1].trim();
		double left = Double.parseDouble(leftSide.substring(1));
		double right = Double.parseDouble(rightSide.substring(0, rightSide.length() - 1));

		boolean leftOpen;
		if (leftSide.startsWith("(")) leftOpen = true;
		else if (leftSide.startsWith("[")) leftOpen = false;
		else throw new IllegalArgumentException(s);

		boolean rightOpen;
		if (rightSide.endsWith(")")) rightOpen = true;
		else if (rightSide.endsWith("]")) rightOpen = false;
		else throw new IllegalArgumentException(s);

		return new NumericalInterval(left, right, leftOpen, rightOpen);

	}

}
