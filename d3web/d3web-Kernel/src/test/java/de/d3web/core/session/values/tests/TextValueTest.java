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

import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;

/**
 * Unit tests for {@link TextValue}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 25.08.2010
 */
public class TextValueTest {

	TextValue textValueOne;
	TextValue textValueTwo;
	final static String TEXT_VALUE_ONE = "textValueOne";

	@Before
	public void setUp() throws Exception {
		textValueOne = new TextValue(TEXT_VALUE_ONE);
		textValueTwo = new TextValue("textValueTwo");
	}

	/**
	 * Summary: Test method for {@link TextValue#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		assertThat(textValueOne.hashCode(), is(not(0)));
		assertThat((new TextValue("")).hashCode(), is(31));
	}

	/**
	 * Summary: Test method for {@link TextValue#TextValue(java.lang.String)}.
	 */
	@Test(expected = NullPointerException.class)
	public void testTextValueThrowsNullPointerException() {
		new TextValue(null);
	}

	/**
	 * Summary: Test method for {@link TextValue#getValue()}.
	 */
	@Test
	public void testGetValue() {
		Object o = textValueOne.getValue();
		if (!(o instanceof String)) {
			fail("Return type of getValue() is not an instance of type String!");
		}

		assertThat(textValueOne.getText(), is(TEXT_VALUE_ONE));
	}

	/**
	 * Summary: Test method for
	 * {@link TextValue#compareTo(de.d3web.core.session.Value)}.
	 * 
	 * @see String#compareTo(String)
	 */
	@Test
	public void testCompareTo() {
		assertThat(textValueOne.compareTo(UndefinedValue.getInstance()), is(-1));
		assertThat(textValueOne.compareTo(textValueOne), is(0));
		// Letter O is five positions in front of the letter T
		assertThat(textValueOne.compareTo(textValueTwo), is(-5));
	}

	/**
	 * Summary: Test method for {@link TextValue#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		assertThat(textValueOne.equals(textValueOne), is(true));
		assertThat(textValueOne.equals(null), is(false));
		assertThat(textValueOne.equals(new Object()), is(false));
		assertThat(textValueOne.equals(textValueTwo), is(false));
		assertThat(textValueOne.equals(new TextValue(TEXT_VALUE_ONE)), is(true));
	}

	/**
	 * Summary: Test method for {@link TextValue#toString()}.
	 */
	@Test
	public void testToString() {
		String string = textValueOne.toString();
		assertThat(string, notNullValue());
		assertThat(string.length(), is(not(0)));
	}

}
