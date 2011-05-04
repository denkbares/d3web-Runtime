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

package de.d3web.core.session.values.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.TextValue;

/**
 * Unit tests for {@link DateValue}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 25.08.2010
 */
public class DateValueTest {

	DateValue dateValue;
	TextValue textValue;
	final static Date TIME = new GregorianCalendar(2010, 7, 25).getTime();

	@Before
	public void setUp() throws Exception {
		// 25.08.2010, 00:00:00
		dateValue = new DateValue(TIME);
		textValue = new TextValue("textValue");
	}

	/**
	 * Test method for {@link de.d3web.core.session.values.DateValue#hashCode()}
	 * .
	 */
	@Test
	public void testHashCode() {
		assertThat(dateValue.hashCode(), is(not(0)));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.values.DateValue#DateValue(java.util.Date)}.
	 */
	@Test(expected = NullPointerException.class)
	public void testDateValueThrowsNullPointerException() {
		new DateValue(null);
	}

	/**
	 * Test method for {@link de.d3web.core.session.values.DateValue#getValue()}
	 * .
	 */
	@Test
	public void testGetValue() {
		assertEquals(Date.class, dateValue.getValue().getClass());

		Date dateTime = (Date) (dateValue.getValue());
		assertEquals(TIME, dateTime);
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.values.DateValue#getDateString()}.
	 */
	@Test
	public void testGetDateString() {
		assertThat(dateValue.getDateString(), is("2010-08-25-00-00-00"));
	}

	/**
	 * Test method for {@link de.d3web.core.session.values.DateValue#toString()}
	 * .
	 */
	@Test
	public void testToString() {
		String string = dateValue.toString();
		assertThat(string, notNullValue());
		assertThat(string.length(), is(not(0)));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.values.DateValue#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		assertThat(dateValue.equals(dateValue), is(true));
		assertThat(dateValue.equals(null), is(false));
		assertThat(dateValue.equals(textValue), is(false));

		Date otherDate = new GregorianCalendar(2000, 07, 25, 0, 0, 0).getTime();
		assertThat(dateValue.equals(new DateValue(otherDate)), is(false));// other
																			// YEAR
		otherDate = new GregorianCalendar(2010, 1, 25, 0, 0, 0).getTime();
		assertThat(dateValue.equals(new DateValue(otherDate)), is(false));// other
																			// MONTH
		otherDate = new GregorianCalendar(2010, 7, 2, 0, 0, 0).getTime();
		assertThat(dateValue.equals(new DateValue(otherDate)), is(false));// other
																			// DAY
		otherDate = new GregorianCalendar(2010, 7, 25, 17, 0, 0).getTime();
		assertThat(dateValue.equals(new DateValue(otherDate)), is(false));// other
																			// HOUR
		otherDate = new GregorianCalendar(2010, 7, 25, 0, 9, 0).getTime();
		assertThat(dateValue.equals(new DateValue(otherDate)), is(false));// other
																			// MINUTE
		otherDate = new GregorianCalendar(2010, 7, 25, 0, 0, 55).getTime();
		assertThat(dateValue.equals(new DateValue(otherDate)), is(false));// other
																			// SECOND
		otherDate = new GregorianCalendar(2010, 7, 25, 0, 0, 0).getTime();
		assertThat(dateValue.equals(new DateValue(otherDate)), is(true));// equal!
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.values.DateValue#compareTo(de.d3web.core.session.Value)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCompareToThrowsIllegalArgumentException() {
		dateValue.compareTo(textValue);
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.values.DateValue#compareTo(de.d3web.core.session.Value)}
	 * .
	 */
	@Test
	public void testCompareTo() {
		Date otherDate = new GregorianCalendar(2000, 07, 25, 0, 0, 0).getTime();
		assertThat(dateValue.compareTo(new DateValue(otherDate)) > 0, is(true));
		otherDate = new GregorianCalendar(2010, 07, 25, 0, 0, 0).getTime();
		assertThat(dateValue.compareTo(new DateValue(otherDate)), is(0));
		otherDate = new GregorianCalendar(2011, 07, 30, 0, 5, 13).getTime();
		assertThat(dateValue.compareTo(new DateValue(otherDate)) < 0, is(true));
	}

}
