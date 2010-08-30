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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.shared.QuestionWeightValue;
import de.d3web.shared.SolutionWeightValue;
import de.d3web.shared.Weight;


/**
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 30.08.2010
 */
public class WeightTest {

	Weight weightUnderTest;
	// Three different solutions...
	Solution solutionOne;
	Solution solutionTwo;
	Solution solutionThree;
	// ...and their respective solution weights
	SolutionWeightValue solutionWeightValueOne; // should be added with value G4
	SolutionWeightValue solutionWeightValueTwo;// should be added with value G7
	SolutionWeightValue solutionWeightValueThree;// should be added with value
													// G1

	/**
	 * 
	 * @created 30.08.2010
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// the Weight' instance under test
		weightUnderTest = new Weight();

		// Create solutionWeightValueOne, with Weight G4
		solutionOne = new Solution("solutionOne");
		solutionWeightValueOne = new SolutionWeightValue();
		solutionWeightValueOne.setSolution(solutionOne);
		solutionWeightValueOne.setValue(Weight.convertConstantStringToValue("G4"));

		// Create solutionWeightValueTwo, with Weight G7
		solutionTwo = new Solution("solutionTwo");
		solutionWeightValueTwo = new SolutionWeightValue();
		solutionWeightValueTwo.setSolution(solutionTwo);
		solutionWeightValueTwo.setValue(Weight.convertConstantStringToValue("G7"));

		// Create solutionWeightValueThree, with Weight G1
		solutionThree = new Solution("solutionThree");
		solutionWeightValueThree = new SolutionWeightValue();
		solutionWeightValueThree.setSolution(solutionThree);
		solutionWeightValueThree.setValue(Weight.convertConstantStringToValue("G1"));

		// add all the solutionWeightValues to the weightUnderTest
		weightUnderTest.addDiagnosisWeightValue(solutionWeightValueOne);
		weightUnderTest.addDiagnosisWeightValue(solutionWeightValueTwo);
		weightUnderTest.addDiagnosisWeightValue(solutionWeightValueThree);
	}

	/**
	 * Test method for {@link de.d3web.shared.Weight#getProblemsolverContext()}.
	 */
	@Test
	public void testGetProblemsolverContext() {
		assertThat(PSMethod.class.isAssignableFrom(weightUnderTest.
				getProblemsolverContext()), is(true));
	}

	/**
	 * Test method for {@link de.d3web.shared.Weight#isUsed(de.d3web.core.session.Session)}.
	 */
	@Test
	public void testIsUsed() {
		// returns true in every case
		assertThat(weightUnderTest.isUsed(null), is(true));
	}

	/**
	 * Test method for {@link de.d3web.shared.Weight#convertConstantStringToValue(java.lang.String)}.
	 */
	@Test
	public void testConvertConstantStringToValue() {
		assertThat(Weight.convertConstantStringToValue("G0"), is(equalTo(Weight.G0)));
		assertThat(Weight.convertConstantStringToValue("G1"), is(equalTo(Weight.G1)));
		assertThat(Weight.convertConstantStringToValue("G2"), is(equalTo(Weight.G2)));
		assertThat(Weight.convertConstantStringToValue("G3"), is(equalTo(Weight.G3)));
		assertThat(Weight.convertConstantStringToValue("G4"), is(equalTo(Weight.G4)));
		assertThat(Weight.convertConstantStringToValue("G5"), is(equalTo(Weight.G5)));
		assertThat(Weight.convertConstantStringToValue("G6"), is(equalTo(Weight.G6)));
		assertThat(Weight.convertConstantStringToValue("G7"), is(equalTo(Weight.G7)));
		assertThat(Weight.convertConstantStringToValue("xX"), is(equalTo(0)));
	}

	/**
	 * Test method for {@link de.d3web.shared.Weight#convertValueToConstantString(int)}.
	 */
	@Test
	public void testConvertValueToConstantString() {
		assertThat(Weight.convertValueToConstantString(-1), is(equalTo("G0")));
		assertThat(Weight.convertValueToConstantString(1), is(equalTo("G1")));
		assertThat(Weight.convertValueToConstantString(2), is(equalTo("G2")));
		assertThat(Weight.convertValueToConstantString(5), is(equalTo("G3")));
		assertThat(Weight.convertValueToConstantString(14), is(equalTo("G4")));
		assertThat(Weight.convertValueToConstantString(16), is(equalTo("G5")));
		assertThat(Weight.convertValueToConstantString(63), is(equalTo("G6")));
		assertThat(Weight.convertValueToConstantString(64), is(equalTo("G7")));
	}

	/**
	 * Test method for {@link de.d3web.shared.Weight#getSolutionWeightValues()}.
	 */
	@Test
	public void testGetSolutionWeightValues() {
		List<SolutionWeightValue> list = weightUnderTest.getSolutionWeightValues();
		assertThat(list.contains(solutionWeightValueThree), is(true));
		assertThat(list.contains(solutionWeightValueOne), is(true));
		assertThat(list.contains(solutionWeightValueTwo), is(true));
	}

	/**
	 * Test method for {@link de.d3web.shared.Weight#getId()}.
	 */
	@Test
	public void testGetId() {
		// set any (arbitrary) QuestionWeightValue and assure that
		// getId() returns the ID of the question, prepended with "W"
		QuestionWeightValue questionWeight = new QuestionWeightValue();
		Question qNum = new QuestionNum("qNumTest");
		questionWeight.setQuestion(qNum);
		questionWeight.setValue(Weight.G5);
		// set the questionWeight
		weightUnderTest.setQuestionWeightValue(questionWeight);
		// and test getId() again
		assertThat(weightUnderTest.getId(), is("WqNumTest"));
	}

	/**
	 * Test method for {@link de.d3web.shared.Weight#getMaxSolutionWeightValueFromSolutions(java.util.Collection)}.
	 */
	@Test
	public void testGetMaxSolutionWeightValueFromSolutions() {
		List<Solution> diagnoses = new ArrayList<Solution>();
		//empty diagnoses list should return -1
		assertThat(weightUnderTest.getMaxSolutionWeightValueFromSolutions(diagnoses), is(-1));
		// now add some solutions to the list and assure that that the correct
		// result is returned.
		// add soultionOne and Three - their maximum SolutionWeight is G4
		diagnoses.add(solutionOne);
		diagnoses.add(solutionThree);
		assertThat(weightUnderTest.getMaxSolutionWeightValueFromSolutions(diagnoses), is(Weight.G4));
		// now, add solutionTwo. Its weight is G7,
		// and should be returned as new maxSolutionWeight:
		diagnoses.add(solutionTwo);
		assertThat(weightUnderTest.getMaxSolutionWeightValueFromSolutions(diagnoses), is(Weight.G7));
	}
}
