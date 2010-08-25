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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.Unknown;

/**
 * Unit tests for {@link Unknown}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 25.08.2010
 */
public class UnknownValueTest {

	Unknown unknownValue;
	TextValue textValue;

	/**
	 * 
	 * @created 25.08.2010
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		unknownValue = Unknown.getInstance();
		textValue = new TextValue("Not unknown!");
	}

	/**
	 * Test method for {@link de.d3web.core.session.values.Unknown#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		assertThat(unknownValue.hashCode(), is(not(0)));
	}

	/**
	 * Test method for {@link de.d3web.core.session.values.Unknown#assignedTo(de.d3web.core.session.Value)}.
	 */
	@Test
	public void testAssignedTo() {
		assertThat(Unknown.assignedTo(unknownValue), is(true));
		assertThat(Unknown.assignedTo(textValue), is(false));
	}

	/**
	 * Test method for {@link de.d3web.core.session.values.Unknown#getValue()}.
	 */
	@Test
	public void testGetValue() {
		Object o = unknownValue.getValue();
		if (!(o instanceof String)) {
			fail("Return type of getValue() is not an instance of type String!");
		}
	}

	/**
	 * Test method for {@link de.d3web.core.session.values.Unknown#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		assertThat(unknownValue.equals(Unknown.getInstance()), is(true));
		assertThat(unknownValue.equals(textValue), is(false));
	}

	/**
	 * Test method for {@link de.d3web.core.session.values.Unknown#compareTo(de.d3web.core.session.Value)}.
	 */
	@Test
	public void testCompareTo() {
		assertThat(unknownValue.compareTo(Unknown.getInstance()), is(0));
		assertThat(unknownValue.compareTo(textValue), is(-1));
	}

	/**
	 * Test method for {@link de.d3web.core.session.values.Unknown#getId()}.
	 */
	@Test
	public void testGetId() {
		assertThat(unknownValue.getId(), is(equalTo(Unknown.UNKNOWN_ID)));
	}

	/**
	 * Test method for {@link de.d3web.core.session.values.Unknown#getName()}.
	 */
	@Test
	public void testGetName() {
		String name = unknownValue.getName();
		assertThat(name, notNullValue());
		assertThat(name.length(), is(not(0)));
	}

}
