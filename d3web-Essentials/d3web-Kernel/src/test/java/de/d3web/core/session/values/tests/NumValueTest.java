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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;


/**
 * Unit tests for {@link NumValue}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 25.08.2010
 */
public class NumValueTest {

	NumValue numValue;
	TextValue textValue;

	@Before
	public void setUp() throws Exception {
		numValue = new NumValue(19.3d);
		textValue = new TextValue("textValue");
	}

	/**
	 * Test method for {@link de.d3web.core.session.values.NumValue#NumValue(java.lang.Double)}.
	 */
	@Test(expected = NullPointerException.class)
	public void testNumValueDoubleThrowsNullPointerException() {
		new NumValue(null);
	}

	/**
	 * Test method for {@link de.d3web.core.session.values.NumValue#getValue()}.
	 */
	@Test
	public void testGetValue() {
		Object o = numValue.getValue();
		if (!(o instanceof Double)) {
			fail("Return type of getValue() is not an instance of type Double!");
		}
	}

	/**
	 * Test method for {@link de.d3web.core.session.values.NumValue#compareTo(de.d3web.core.session.Value)}.
	 */
	@Test
	public void testCompareTo() {
		assertThat(numValue.compareTo(textValue), is(-1));
		NumValue otherValue = new NumValue(21.3d);
		assertThat(numValue.compareTo(otherValue), is(-1));
		otherValue = new NumValue(7.0d);
		assertThat(numValue.compareTo(otherValue), is(1));
	}

	/**
	 * Test method for {@link de.d3web.core.session.values.NumValue#toString()}.
	 */
	@Test
	public void testToString() {
		String string = numValue.toString();
		assertThat(string, notNullValue());
		assertThat(string.length(), is(not(0)));
	}

	/**
	 * Test method for {@link de.d3web.core.session.values.NumValue#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		assertThat(numValue.equals(numValue), is(true));
		assertThat(numValue.equals(new NumValue(11.2d)), is(false));
		assertThat(numValue.equals(null), is(false));
		assertThat(numValue.equals(new Object()), is(false));
	}

}
