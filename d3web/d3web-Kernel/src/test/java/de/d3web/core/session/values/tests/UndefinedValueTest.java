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
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;

/**
 * Unit tests for {@link UndefinedValue}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 25.08.2010
 */
public class UndefinedValueTest {

	UndefinedValue undefinedValue;
	TextValue textValue;

	@Before
	public void setUp() throws Exception {
		undefinedValue = UndefinedValue.getInstance();
		textValue = new TextValue("Not undefined!");
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.values.UndefinedValue#getValue()}.
	 */
	@Test
	public void testGetValue() {
		Object o = undefinedValue.getValue();
		if (!(o instanceof String)) {
			fail("Return type of getValue() is not an instance of type String!");
		}
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.values.UndefinedValue#toString()}.
	 */
	@Test
	public void testToString() {
		String string = undefinedValue.toString();
		assertThat(string, notNullValue());
		assertThat(string.length(), is(not(0)));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.values.UndefinedValue#compareTo(de.d3web.core.session.Value)}
	 * .
	 */
	@Test
	public void testCompareTo() {
		assertThat(undefinedValue.compareTo(null), is(0));
		assertThat(undefinedValue.compareTo(UndefinedValue.getInstance()), is(0));
		assertThat(undefinedValue.compareTo(textValue), is(-1));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.values.UndefinedValue#equals(java.lang.Object)}
	 * .
	 */
	@Test
	public void testEqualsObject() {
		assertThat(undefinedValue.equals(UndefinedValue.getInstance()), is(true));
		assertThat(undefinedValue.equals(textValue), is(false));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.values.UndefinedValue#isUndefinedValue(de.d3web.core.session.Value)}
	 * .
	 */
	@Test
	public void testIsUndefinedValue() {
		assertThat(UndefinedValue.isUndefinedValue(undefinedValue), is(true));
		assertThat(UndefinedValue.isUndefinedValue(textValue), is(false));
		// Additionally check the method hashCode()
		assertEquals(UndefinedValue.UNDEFINED_ID.hashCode(),
				UndefinedValue.getInstance().hashCode());
		assertEquals(UndefinedValue.getInstance().hashCode(),
				UndefinedValue.getInstance().hashCode());
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.values.UndefinedValue#isNotUndefinedValue(de.d3web.core.session.Value)}
	 * .
	 */
	@Test
	public void testIsNotUndefinedValue() {
		assertThat(UndefinedValue.isNotUndefinedValue(undefinedValue), is(false));
		assertThat(UndefinedValue.isNotUndefinedValue(textValue), is(true));
	}

}
