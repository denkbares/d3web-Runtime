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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.Unknown;

/**
 * Unit tests for {@link ChoiceValue} and {@link ChoiceID}.
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 25.08.2010
 */
public class ChoiceValueTest {

	ChoiceValue choiceValue;
	private ChoiceID c1;
	private ChoiceID c2;

	/**
	 * 
	 * @created 25.08.2010
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Choice choice = new Choice("choiceText");
		c1 = new ChoiceID(choice);
		c2 = new ChoiceID(new Choice("choiceText2"));
		choiceValue = new ChoiceValue(choice);
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.values.ChoiceValue#ChoiceValue(Choice)}.
	 */
	@Test(expected = NullPointerException.class)
	public void testChoiceValueChoiceThrowsNullPointerException() {
		new ChoiceValue((Choice) null);
	}

	@Test(expected = NullPointerException.class)
	public void testChoiceValueChoiceIDThrowsNullPointerException() {
		new ChoiceValue((ChoiceID) null);
	}

	@Test(expected = NullPointerException.class)
	public void testChoiceIDThrowsNullPointerException() {
		new ChoiceID((String) null);
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.values.ChoiceValue#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		assertThat(choiceValue.hashCode(), is(not(0)));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.values.ChoiceValue#getValue()}.
	 */
	@Test
	public void testGetValue() {
		Object o = choiceValue.getValue();
		if (!(o instanceof ChoiceID)) {
			fail("Return type of getValue() is not an instance of type Choice!");
		}
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.values.ChoiceValue#getAnswerChoiceID()}.
	 */
	@Test
	public void testGetAnswerChoiceID() {
		String answerChoiceID = choiceValue.getAnswerChoiceID();
		assertThat(answerChoiceID, is("choiceText"));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.values.ChoiceValue#toString()}.
	 */
	@Test
	public void testToString() {
		String string = choiceValue.toString();
		assertThat(string, is("choiceText"));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.values.ChoiceValue#equals(java.lang.Object)}
	 * .
	 */
	@Test
	public void testEqualsObject() {
		assertThat(choiceValue.equals(choiceValue), is(true));
		assertThat(choiceValue.equals(null), is(false));
		assertThat(choiceValue.equals(new Object()), is(false));
		ChoiceValue newChoiceValue = new ChoiceValue(new Choice("choice"));
		assertThat(choiceValue.equals(newChoiceValue), is(false));
	}

	@Test
	public void testEqualsWithMC() {
		// for a MultipleChoiceValue with only one and the same choice, the
		// equals test should return true
		MultipleChoiceValue mcv = new MultipleChoiceValue(c1);
		assertThat(choiceValue.equals(mcv), is(true));

		// for a MCValue with more values, it should always return false
		mcv = new MultipleChoiceValue(c1, c2);
		assertThat(choiceValue.equals(mcv), is(false));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.values.ChoiceValue#compareTo(de.d3web.core.session.Value)}
	 * .
	 */
	@Test
	public void testCompareTo() {
		assertThat(choiceValue.compareTo(choiceValue), is(0));
		assertThat(choiceValue.compareTo(null), is(0));
		assertThat(choiceValue.compareTo(Unknown.getInstance()), is(0));
	}

}
