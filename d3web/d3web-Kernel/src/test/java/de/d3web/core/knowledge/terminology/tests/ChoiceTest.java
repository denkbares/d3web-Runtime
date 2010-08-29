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

package de.d3web.core.knowledge.terminology.tests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.terminology.AnswerNo;
import de.d3web.core.knowledge.terminology.AnswerYes;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.Properties;
import de.d3web.core.knowledge.terminology.info.Property;

/**
 * This class tests the "Choices" which utilize the answer alternatives for
 * choice questions.
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 24.08.2010
 */
public class ChoiceTest {

	/**
	 * The tested instance of class Choice
	 */
	Choice choice;

	@Before
	public void setUp() throws Exception {
		choice = new Choice("choice");
		choice.setText("text");
	}

	/**
	 * Summary: Tests the getter and setter of text/name
	 * 
	 * @see Choice#setText(String)
	 * @see Choice#getName()
	 * @see Choice#toString()
	 * 
	 * @created 24.08.2010
	 */
	@Test
	public void testTextGetterSetter() {
		choice.setText("text to set");
		assertThat(choice.getName(), is("text to set"));
		assertThat(choice.toString(), is("text to set"));
	}

	/**
	 * Summary: Assure that this Choice instance is not an Yes or No Answer
	 * 
	 * @see Choice#isAnswerNo()
	 * @see Choice#isAnswerYes()
	 * 
	 * @created 24.08.2010
	 */
	@Test
	public void testChoiceAnswerYesNo() {
		assertThat(choice.isAnswerNo(), is(false));
		assertThat(choice.isAnswerYes(), is(false));
	}

	/**
	 * This tests the compareTo() method of Choices, which take the "distance"
	 * of the answer alternatives in the question into account
	 * 
	 * @see Choice#compareTo(Choice)
	 * 
	 * @created 24.08.2010
	 */
	@Test
	public void testCompareTo() {
		Choice choiceOne = new Choice("choiceOne");
		Choice choiceTwo = new Choice("choiceTwo");
		Choice choiceThree = new Choice("choiceThree");

		List<Choice> choicesList = new ArrayList<Choice>();
		choicesList.add(choiceOne);
		choicesList.add(choiceTwo);
		choicesList.add(choiceThree);

		QuestionOC question = new QuestionOC("id");
		question.setAlternatives(choicesList);

		// choiceOne is two answer alternatives in front of choiceThree
		assertThat(choiceOne.compareTo(choiceThree), is(-2));
		// choiceOne is directly in front of choiceThree
		assertThat(choiceTwo.compareTo(choiceThree), is(-1));
		// choiceThree is two answer alternatives behind choiceOne
		assertThat(choiceThree.compareTo(choiceOne), is(2));
		// choiceThree is directly behind choiceTwo
		assertThat(choiceThree.compareTo(choiceTwo), is(1));
		// choiceTwo equals choiceTwo
		assertThat(choiceTwo.compareTo(choiceTwo), is(0));
	}

	/**
	 * Summary: Tests the get and setProperties methods of the class Choice.
	 * While handling Property and Properties, some methods of these classes are
	 * tested also.
	 * 
	 * @see Choice#setProperties(Properties)
	 * @see Choice#getProperties()
	 * @see Properties#isEmpty()
	 * @see Properties#getKeys()
	 * 
	 * @created 24.08.2010
	 */
	@Test
	public void testPropertiesGetterSetter() {
		// create a new Property value
		String propString = new String("propString");
		// create new Properties
		Properties props = new Properties();
		assertThat(props.isEmpty(), is(true));
		// set the new property into the properties set
		props.setProperty(Property.MY_NEW_PROPERTY, propString);
		// assure that the properties map is correctly initialized
		assertThat(props.getKeys().size(), is(1));
		assertThat(props.isEmpty(), is(false));

		// Test the toString() method of Properties
		String string = props.toString();
		assertThat(string, notNullValue());
		assertThat(string.length(), is(not(0)));

		// set the new properties and retrieve them. check equalsTo()
		choice.setProperties(props);
		assertThat(choice.getProperties(), is(equalTo(props)));
	}

	/**
	 * Summary: Tests the hashCode method of the class Choice by simply checking
	 * if its value is not 0.
	 * 
	 * @created 24.08.2010
	 */
	@Test
	public void testHashCode() {
		int hashCode = choice.hashCode();
		assertThat(hashCode, is(not(0)));
	}
	
	/**
	 * Summary: Tests the subclass of Choice: AnswerNo
	 * 
	 * @see AnswerNo
	 * 
	 * @created 24.08.2010
	 */
	@Test
	public void testClassAnswerNo() {
		AnswerNo answerNo = new AnswerNo("answerNo");
		assertThat(answerNo.isAnswerNo(), is(true));
		assertThat(answerNo.isAnswerYes(), is(false));
	}

	/**
	 * Summary: Tests the subclass of Choice: AnswerYes
	 * 
	 * @see AnswerYes
	 * 
	 * @created 24.08.2010
	 */
	@Test
	public void testClassAnswerYes() {
		AnswerYes answerYes = new AnswerYes("answerNo");
		assertThat(answerYes.isAnswerNo(), is(false));
		assertThat(answerYes.isAnswerYes(), is(true));
	}
}
