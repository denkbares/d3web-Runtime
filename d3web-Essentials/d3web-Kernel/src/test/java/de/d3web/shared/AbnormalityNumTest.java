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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.NumericalInterval.IntervalException;
import de.d3web.core.knowledge.terminology.info.abnormality.Abnormality;
import de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityInterval;
import de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityNum;
import de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityUtils;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Unit tests for {@link AbnormalityInterval}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 27.08.2010
 */
public class AbnormalityNumTest {

	AbnormalityNum abnormalityNumUnderTest;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		// AbnormalityIntervals for this test:
		//
		// [-1.7 .. 3.4) [4.1 .. 5.7] (5.7 .. 8.1]
		// ______A3_____ _____A1_____ _____A5_____
		//
		abnormalityNumUnderTest = new AbnormalityNum();
		abnormalityNumUnderTest.addValue(-1.7, 3.4,
				Abnormality.A3, false, true);
		abnormalityNumUnderTest.addValue(4.1, 5.7,
				Abnormality.A1, false, false);
		abnormalityNumUnderTest.addValue(5.7, 8.1,
				Abnormality.A5, true, false);
	}

	/**
	 * Add an overlapping interval: should throw an IntervalException!
	 */
	@Test(expected = IntervalException.class)
	public void addOverlappingInterval() {
		// [-1.7 .. 3.4) [4.1 .. 5.7] (5.7 .. 8.1]
		// _______[3.4 .. 4.1] <-- overlaps because two closed intervals at 4.1
		abnormalityNumUnderTest.addValue(3.4, 4.1, Abnormality.A2, false, false);
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityNum#getValue(de.d3web.core.session.Value)}
	 * .
	 */
	@Test
	public void testGetValueValue() {
		// Summary: Get some values out of the intervals:
		// [-1.7 .. 3.4) [4.1 .. 5.7] (5.7 .. 8.1]
		// ______A3_____ _____A1_____ _____A5_____
		//
		// ______|--> @2.0: should return A3
		assertThat(abnormalityNumUnderTest.getValue(2.0),
				is(equalTo(Abnormality.A3)));
		// [-1.7 .. 3.4) [4.1 .. 5.7] (5.7 .. 8.1]
		// ______A3_____ _____A1_____ _____A5_____
		//
		// _____________|--> @3.71: out of intervals, should return A0
		assertThat(abnormalityNumUnderTest.getValue(3.71),
				is(equalTo(Abnormality.A5)));
		// [-1.7 .. 3.4) [4.1 .. 5.7] (5.7 .. 8.1]
		// ______A3_____ _____A1_____ _____A5_____
		//
		// __________________________|--> @5.7: should return A1
		assertThat(abnormalityNumUnderTest.getValue(new NumValue(5.7)),
				is(equalTo(Abnormality.A1)));
		// [-1.7 .. 3.4) [4.1 .. 5.7] (5.7 .. 8.1]
		// ______A3_____ _____A1_____ _____A5_____
		//
		// ________________________________________|--> @9.0: out of intervals
		assertThat(abnormalityNumUnderTest.getValue(new NumValue(9.0)),
				is(equalTo(Abnormality.A5)));
		// try to get the abnormalityValue for a Value != NumValue, e.g.
		// TextValue:
		assertThat(abnormalityNumUnderTest.getValue(new TextValue("123")),
				is(equalTo(Abnormality.A5)));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityNum#getIntervals()}
	 * .
	 */
	@Test
	public void testGetIntervals() {
		List<AbnormalityInterval> intervals = abnormalityNumUnderTest.getIntervals();
		assertThat(intervals.contains(new AbnormalityInterval(4.1, 5.7,
				Abnormality.A1, false, false)), is(true));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityNum#setAbnormality(de.d3web.core.knowledge.terminology.QuestionNum, double, double, double, boolean, boolean)}
	 * .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testSetAbnormality() {
		QuestionNum qNum = new QuestionNum(new KnowledgeBase(), "qNum");
		// set the AbnormalityInterval (A4): (4.1, 6.9] for the question:
		AbnormalityNum.setAbnormality(qNum, 4.1, 6.9,
				Abnormality.A4, true, false);
		// NumValues out of the interval should return A0-abnormalities:
		assertThat(AbnormalityUtils.getAbnormality(qNum, new NumValue(4.1)),
				is(equalTo(Abnormality.A5)));
		assertThat(AbnormalityUtils.getAbnormality(qNum, new NumValue(7.0)),
				is(equalTo(Abnormality.A5)));
		// a value from within the interval should return the A4 abnormality
		assertThat(AbnormalityUtils.getAbnormality(qNum, new NumValue(5.1)),
				is(equalTo(Abnormality.A4)));
	}

}
