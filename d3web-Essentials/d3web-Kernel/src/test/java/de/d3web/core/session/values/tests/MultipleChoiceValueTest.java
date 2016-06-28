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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.UndefinedValue;

/**
 * Unit tests for {@link MultipleChoiceValue}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 25.08.2010
 */
public class MultipleChoiceValueTest {

	// One MultipleChoiceValue with its Choices
	MultipleChoiceValue mcValueOne;
	Choice mcValueOneChoiceA;
	Choice mcValueOneChoiceB;
	Choice mcValueOneChoiceC;

	// and another multipleChoiceValue with its values
	MultipleChoiceValue mcValueTwo;
	Choice mcValueTwoChoiceX;
	Choice mcValueTwoChoiceY;

	/**
	 * 
	 * @created 25.08.2010
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// Initialize mcValueOne and its Choices
		mcValueOneChoiceA = new Choice("mcValueOneChoiceA");
		mcValueOneChoiceB = new Choice("mcValueOneChoiceB");
		mcValueOneChoiceC = new Choice("mcValueOneChoiceC");
		List<Choice> choicesList = new LinkedList<>();
		choicesList.add(mcValueOneChoiceA);
		choicesList.add(mcValueOneChoiceB);
		choicesList.add(mcValueOneChoiceC);
		mcValueOne = MultipleChoiceValue.fromChoices(choicesList);

		// Initialize mcValueTwo and its Choices
		mcValueTwoChoiceX = new Choice("mcValueTwoChoiceX");
		mcValueTwoChoiceY = new Choice("mcValueTwoChoiceY");
		choicesList.clear();
		choicesList.add(mcValueTwoChoiceX);
		choicesList.add(mcValueTwoChoiceY);
		mcValueTwo = MultipleChoiceValue.fromChoices(choicesList);
	}

	@Test(expected = NullPointerException.class)
	public void testMultipleChoiceValueThrowsNullPointerException() {
		new MultipleChoiceValue((Collection<ChoiceID>) null);
	}

	/**
	 * Summary: Test the getAnswerChoicesID() method which generated the ID by
	 * accessing the getID() method of all contained {@link ChoiceValue}s and
	 * "gluing" it together with {@link MultipleChoiceValue#ID_SEPARATOR}.
	 * 
	 * Note: An ordering of the contained IDs is not guaranteed!
	 * 
	 * @see MultipleChoiceValue#getAnswerChoicesID()
	 * 
	 * @created 25.08.2010
	 */
	@Test
	public void testGetAnswerChoicesID() {
		String derived = ChoiceID.encodeChoiceIDs(mcValueOne.getChoiceIDs());
		assertThat(derived.contains(mcValueOneChoiceA.getName()), is(true));
		assertThat(derived.contains(mcValueOneChoiceB.getName()), is(true));
		assertThat(derived.contains(mcValueOneChoiceC.getName()), is(true));

		String manual = mcValueOneChoiceA.getName() + ChoiceID.ID_SEPARATOR
				+ mcValueOneChoiceB.getName() + ChoiceID.ID_SEPARATOR
				+ mcValueOneChoiceC.getName();
		assertThat(derived.length(), is(equalTo(manual.length())));

		// test empty list answerChoicesID
		MultipleChoiceValue emptyMCValue = MultipleChoiceValue.fromChoices(new ArrayList<>());
		assertThat(ChoiceID.encodeChoiceIDs(emptyMCValue.getChoiceIDs()), is(""));
	}

	/**
	 * Summary: Assure, that the contains() and containsAll() methods are
	 * working as expected
	 * 
	 * @see MultipleChoiceValue#contains(de.d3web.core.session.Value)
	 * @see MultipleChoiceValue#containsAll(MultipleChoiceValue)
	 * 
	 * @created 25.08.2010
	 */
	@Test
	public void testContainsMethods() {
		// test contains()
		assertThat(mcValueTwo.contains(new ChoiceValue(mcValueTwoChoiceY)), is(true));
		assertThat(mcValueTwo.contains(new ChoiceValue(mcValueOneChoiceA)), is(false));
		// test containsAll() by constructing a new MCValue which contains all
		// the mcValueTwoChoices and some extra ones
		Choice newChoiceOne = new Choice("newChoiceOne");
		Choice newChoiceTwo = new Choice("newChoiceTwo");
		List<Choice> choicesList = new LinkedList<>();
		choicesList.add(newChoiceOne);
		choicesList.add(mcValueTwoChoiceX);
		choicesList.add(mcValueTwoChoiceY);
		choicesList.add(newChoiceTwo);
		MultipleChoiceValue mcValueNew = MultipleChoiceValue.fromChoices(choicesList);
		// now assure that mcValueNew contains all the Values mcValueTwo
		// contains
		assertThat(mcValueNew.containsAll(mcValueTwo), is(true));
		// containsAll() is (in this case) not symetric
		assertThat(mcValueTwo.containsAll(mcValueNew), is(false));
	}

	/**
	 * Summary: Test the hashCode() method by executing it on empty and
	 * non-empty {@link MultipleChoiceValue}s
	 * 
	 * @see MultipleChoiceValue#hashCode()
	 * 
	 * @created 25.08.2010
	 */
	@Test
	public void testHashCode() {
		assertThat(mcValueOne.hashCode(), is(not(0)));
		assertThat(mcValueTwo.hashCode(), is(not(0)));

		MultipleChoiceValue mcValueEmpty = new MultipleChoiceValue(new ArrayList<>());
		assertThat(mcValueEmpty.hashCode(), is(1));
	}

	/**
	 * Summary: Tests the equals() method
	 * 
	 * @see MultipleChoiceValue#equals(Object)
	 * 
	 * @created 25.08.2010
	 */
	@Test
	public void testEquals() {
		assertThat(mcValueOne.equals(mcValueOne), is(true));
		assertThat(mcValueOne.equals(null), is(false));
		assertThat(mcValueOne.equals(new Object()), is(false));
		assertThat(mcValueOne.equals(mcValueTwo), is(false));
	}

	/**
	 * Summary: The method asChoiceList() enables users to retrieve the
	 * contained {@link ChoiceValue}s of this {@link MultipleChoiceValue}
	 * instance as a list of {@link Choice}s.
	 * 
	 * @see MultipleChoiceValue#asChoiceList()
	 * 
	 * @created 25.08.2010
	 */
	@Test
	public void testAsChoiceList() {
		ChoiceID newChoiceOne = new ChoiceID("newChoiceOne");
		ChoiceID newChoiceTwo = new ChoiceID("newChoiceTwo");
		List<ChoiceID> choicesList = new LinkedList<>();
		choicesList.add(newChoiceOne);
		choicesList.add(newChoiceTwo);
		MultipleChoiceValue newMCValue = new MultipleChoiceValue(choicesList);
		assertThat(newMCValue.getChoiceIDs().containsAll(choicesList), is(true));
		assertThat(choicesList.containsAll(newMCValue.getChoiceIDs()), is(true));
	}

	/**
	 * Summary: Tests toString() and getName() method be checking the returned
	 * Strings
	 * 
	 * @created 25.08.2010
	 */
	@Test
	public void testToStringAndGetName() {
		// Test toString()
		String string = mcValueOne.toString();
		assertThat(string, notNullValue());
		assertThat(string.length(), is(not(0)));
		// Test getName()
		string = mcValueOne.getName();
		assertThat(string, notNullValue());
		assertThat(string.length(), is(not(0)));
	}

	/**
	 * Summary: Assure that compareTo(null) throws an NullPointerException
	 * 
	 * @created 25.08.2010
	 */
	@Test(expected = NullPointerException.class)
	public void testCompareToNullThrowsNullPointerException() {
		mcValueOne.compareTo(null);
	}

	/**
	 * Summary: Tests the getValue() and different cases of the compareTo()
	 * method.
	 * 
	 * @created 25.08.2010
	 */
	@Test
	public void testCompareToAndGetValue() {
		Object o = mcValueOne.getValue();
		if (!(o instanceof Collection<?>)) {
			fail("Return type of getValue() is not an instance of Collection<?>");
		}

		assertThat(mcValueOne.compareTo(UndefinedValue.getInstance()), is(-1));
		assertThat(mcValueOne.compareTo(mcValueOne), is(0));
		assertThat(mcValueOne.compareTo(mcValueTwo), is(1));
	}
}
