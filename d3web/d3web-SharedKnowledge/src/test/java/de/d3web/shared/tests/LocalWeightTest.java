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

package de.d3web.shared.tests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.shared.LocalWeight;

/**
 * Unit tests for {@link LocalWeight}.
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 28.08.2010
 */
public class LocalWeightTest {

	LocalWeight localWeightUnderTest;
	
	Value valueOne; // should get LocalWeight G2
	Value valueTwo; // should get LocalWeight G5
	Value valueThree; // should get LocalWeight G7

	@Before
	public void setUp() throws Exception {
		localWeightUnderTest = new LocalWeight();

		valueOne = new TextValue("value1"); // should get LocalWeight G2
		valueTwo = new NumValue(5.0); // should get LocalWeight G5
		Choice choice = new Choice("choice");
		choice.setText("choiceText");
		valueThree = new ChoiceValue(choice);

		localWeightUnderTest.setValue(valueOne, LocalWeight.G2);
		localWeightUnderTest.setValue(valueTwo, LocalWeight.G5);
		localWeightUnderTest.setValue(valueThree, LocalWeight.G7);
	}

	/**
	 * Test method for {@link de.d3web.shared.LocalWeight#getValue(de.d3web.core.session.Value)}.
	 */
	@Test
	public void testGetValue() {
		assertThat(localWeightUnderTest.getValue(valueTwo), is(equalTo(LocalWeight.G5)));
		assertThat(localWeightUnderTest.getValue(valueOne), is(equalTo(LocalWeight.G2)));
		assertThat(localWeightUnderTest.getValue(valueThree), is(equalTo(LocalWeight.G7)));
		// a not added Value should return an G0 weight
		assertThat(localWeightUnderTest.getValue(new TextValue("not added")),
				is(equalTo(LocalWeight.G0)));
	}

	/**
	 * Test method for setting, getting and removing of {@link Question}s.
	 */
	@Test
	public void testSetGetAndRemoveQuestion() {
		//getQuestion() returns null if no question was set before
		assertThat(localWeightUnderTest.getQuestion(), is(equalTo(null)));
		//now set a new question and test getQuestion()
		Question questionNum = new QuestionNum("questionNum");
		localWeightUnderTest.setQuestion(questionNum);
		assertThat(localWeightUnderTest.getQuestion(), is(equalTo(questionNum)));
		// and remove it afterwards:
		localWeightUnderTest.remove();
		assertThat(localWeightUnderTest.getQuestion(), is(equalTo(null)));
	}

	/**
	 * Test method for setting, getting and removing of {@link Solution}s.
	 */
	@Test
	public void testSetGetAndRemoveSolution() {
		// getSolution() returns null if no solution was set before
		assertThat(localWeightUnderTest.getSolution(), is(equalTo(null)));
		// now set a new solution and test getSolution()
		Solution solution = new Solution("solutionID");
		localWeightUnderTest.setSolution(solution);
		assertThat(localWeightUnderTest.getSolution(), is(equalTo(solution)));
		// and remove it afterwards:
		localWeightUnderTest.remove();
		assertThat(localWeightUnderTest.getSolution(), is(equalTo(null)));
	}

	/**
	 * Test method for {@link de.d3web.shared.LocalWeight#getId()}.
	 */
	@Test
	public void testGetId() {
		// getID() returns the ID of the question, prepended with "W"
		Question questionNum = new QuestionNum("questionNum");
		localWeightUnderTest.setQuestion(questionNum);
		assertThat(localWeightUnderTest.getId(), is(equalTo("WquestionNum")));
	}

	/**
	 * Test method for {@link de.d3web.shared.LocalWeight#getProblemsolverContext()}.
	 */
	@Test
	public void testGetProblemsolverContext() {
		assertThat(PSMethod.class.isAssignableFrom(localWeightUnderTest.
				getProblemsolverContext()), is(true));
	}

	/**
	 * Test method for {@link de.d3web.shared.LocalWeight#isUsed(de.d3web.core.session.Session)}.
	 */
	@Test
	public void testIsUsed() {
		// returns true in every case!
		assertThat(localWeightUnderTest.isUsed(null), is(true));
	}

	/**
	 * Test method for {@link de.d3web.shared.LocalWeight#convertConstantStringToValue(java.lang.String)}.
	 */
	@Test
	public void testConvertConstantStringToValue() {
		assertThat(LocalWeight.convertConstantStringToValue("G0"), is(equalTo(LocalWeight.G0)));
		assertThat(LocalWeight.convertConstantStringToValue("G1"), is(equalTo(LocalWeight.G1)));
		assertThat(LocalWeight.convertConstantStringToValue("G2"), is(equalTo(LocalWeight.G2)));
		assertThat(LocalWeight.convertConstantStringToValue("G3"), is(equalTo(LocalWeight.G3)));
		assertThat(LocalWeight.convertConstantStringToValue("G4"), is(equalTo(LocalWeight.G4)));
		assertThat(LocalWeight.convertConstantStringToValue("G5"), is(equalTo(LocalWeight.G5)));
		assertThat(LocalWeight.convertConstantStringToValue("G6"), is(equalTo(LocalWeight.G6)));
		assertThat(LocalWeight.convertConstantStringToValue("G7"), is(equalTo(LocalWeight.G7)));
		assertThat(LocalWeight.convertConstantStringToValue("xX"), is(equalTo(0.0)));
	}

	/**
	 * Test method for {@link de.d3web.shared.LocalWeight#convertValueToConstantString(double)}.
	 */
	@Test
	public void testConvertValueToConstantString() {
		assertThat(LocalWeight.convertValueToConstantString(-1.0), is(equalTo("G0")));
		assertThat(LocalWeight.convertValueToConstantString(1.5), is(equalTo("G1")));
		assertThat(LocalWeight.convertValueToConstantString(2.1), is(equalTo("G2")));
		assertThat(LocalWeight.convertValueToConstantString(5.7), is(equalTo("G3")));
		assertThat(LocalWeight.convertValueToConstantString(14.22), is(equalTo("G4")));
		assertThat(LocalWeight.convertValueToConstantString(16), is(equalTo("G5")));
		assertThat(LocalWeight.convertValueToConstantString(63.99), is(equalTo("G6")));
		assertThat(LocalWeight.convertValueToConstantString(64), is(equalTo("G7")));
	}

	/**
	 * Test method for {@link de.d3web.shared.LocalWeight#getAnswerSet()}.
	 */
	@Test
	public void testGetAnswerSet() {
		Set<Value> valueSet = localWeightUnderTest.getAnswerSet();
		assertThat(valueSet.contains(valueOne), is(true));
		assertThat(valueSet.contains(valueThree), is(true));
		assertThat(valueSet.contains(valueTwo), is(true));
	}

	/**
	 * Test method for {@link de.d3web.shared.LocalWeight#getLocalWeight(de.d3web.core.knowledge.TerminologyObject, de.d3web.core.session.Value)}.
	 */
	@Test
	public void testGetLocalWeight() {
		// this question-value pair should be set with a weight of G4
		Question question = new QuestionNum("question");
		Value numValue = new NumValue(3.6);
		// before setting this pair, the default weight G0 should be retrieved
		assertThat(LocalWeight.getLocalWeight(question, numValue), is(LocalWeight.G0));
		// now set the weight G4
		LocalWeight.set(question, numValue, LocalWeight.G4);

		// this solution-rating pair should be set with a weight of G6
		Solution solution = new Solution("solution");
		Value rating = new Rating("ESTABLISHED");
		// before setting this pair, the default weight G0 should be retrieved
		assertThat(LocalWeight.getLocalWeight(solution, rating), is(LocalWeight.G0));
		// now set the weight G6 for this pair
		LocalWeight.set(solution, rating, LocalWeight.G6);
		//
		// now retrieve the local weight for these two pairs and assure that the
		// correct weight was set
		//
		assertThat(LocalWeight.getLocalWeight(question, numValue), is(LocalWeight.G4));
		assertThat(LocalWeight.getLocalWeight(solution, rating), is(LocalWeight.G6));
	}

	/**
	 * Test that
	 * {@link LocalWeight#set(de.d3web.core.knowledge.TerminologyObject, Value, double)}
	 * throws an {@link IllegalArgumentException} if object is not a
	 * {@link Question} or a {@link Solution}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetThrowsIllegalArgumentException() {
		LocalWeight.set(new QContainer("container"), new NumValue(1.1), LocalWeight.G1);
	}

}
