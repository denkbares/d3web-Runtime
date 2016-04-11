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

package de.d3web.shared;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.info.abnormality.Abnormality;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;

/**
 * Unit test for {@link DefaultAbnormality}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 26.08.2010
 */
public class AbnormalityTest {

	DefaultAbnormality abnormality;

	Value valueOne;
	Value valueTwo;
	Value valueThree;

	/**
	 * 
	 * @created 26.08.2010
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		abnormality = new DefaultAbnormality();

		valueOne = new TextValue("textValue");
		valueTwo = new NumValue(0.49);

		Choice choice = new Choice("choiceID");
		valueThree = new ChoiceValue(choice);
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality#getValue(de.d3web.core.session.Value)}
	 * .
	 */
	@Test
	public void testAddGetValue() {
		// before setting the values, A5 should be returned as abnormality
		assertThat(abnormality.getValue(valueOne), is(Abnormality.A5));
		assertThat(abnormality.getValue(valueTwo), is(Abnormality.A5));
		assertThat(abnormality.getValue(valueThree), is(Abnormality.A5));
		// now set the abnormality values, for safety in mixed order
		abnormality.addValue(valueTwo, Abnormality.A2);
		abnormality.addValue(valueOne, Abnormality.A1);
		abnormality.addValue(valueThree, Abnormality.A3);
		// now retrieve the abnormality values
		assertThat(abnormality.getValue(valueThree), is(Abnormality.A3));
		assertThat(abnormality.getValue(valueOne), is(Abnormality.A1));
		assertThat(abnormality.getValue(valueTwo), is(Abnormality.A2));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality#isSet(de.d3web.core.session.Value)}
	 * and
	 * {@link de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality#getAnswerSet()}
	 * .
	 */
	@Test
	public void testIsSetAndAnswerEnumeration() {
		// the values shouldn't be set at the beginning
		assertThat(abnormality.isSet(valueOne), is(false));
		assertThat(abnormality.isSet(valueTwo), is(false));
		assertThat(abnormality.isSet(valueThree), is(false));
		// now set the abnormality values, for safety in mixed order
		abnormality.addValue(valueTwo, Abnormality.A2);
		abnormality.addValue(valueOne, Abnormality.A1);
		abnormality.addValue(valueThree, Abnormality.A3);
		// now test again if the values are set
		assertThat(abnormality.isSet(valueThree), is(true));
		assertThat(abnormality.isSet(valueOne), is(true));
		assertThat(abnormality.isSet(valueTwo), is(true));
		// now where all the values are set, assert the abnormalitySet
		Set<Value> abnormalitySet = abnormality.getAnswerSet();
		assertThat(abnormalitySet.contains(valueOne), is(true));
		assertThat(abnormalitySet.contains(valueThree), is(true));
		assertThat(abnormalitySet.contains(valueTwo), is(true));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality#setAbnormality(de.d3web.core.knowledge.terminology.Question, de.d3web.core.session.Value, double)}
	 * .
	 */
	@Test
	public void testSetAbnormality() {
		// TODO
	}

}
