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

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.terminology.info.NumericalInterval.IntervalException;
import de.d3web.core.knowledge.terminology.info.abnormality.Abnormality;
import de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityInterval;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 27.08.2010
 */
public class AbnormalityIntervalTest {

	AbnormalityInterval abnormalityInterval;

	@Before
	public void setUp() throws Exception {
		abnormalityInterval = new AbnormalityInterval(3, 7, Abnormality.A2, false, false);
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityInterval#toString()}
	 * .
	 */
	@Test
	public void testToString() {
		assertThat(abnormalityInterval.toString(), is("[3.0 7.0] : A2"));
		AbnormalityInterval openIntervals = new AbnormalityInterval(8.9, 11.3,
				Abnormality.A4, true, true);
		assertThat(openIntervals.toString(), is("(8.9 11.3) : A4"));
	}

	/**
	 * Test method for {@link de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityInterval#AbnormalityInterval(double,
	 * double, double, boolean, boolean)} .
	 */
	@Test(expected = IntervalException.class)
	public void testAbnormalityIntervalThrowsIntervalException() {
		new AbnormalityInterval(3, 3, Abnormality.A5, true, true);
	}

	/**
	 * Test method for {@link AbnormalityInterval#getValue()}.
	 */
	@Test
	public void testGetAndSetValue() {
		assertThat(abnormalityInterval.getValue(), is(equalTo(Abnormality.A2)));
	}
}