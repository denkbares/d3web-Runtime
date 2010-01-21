/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.kernel.shared.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.kernel.psMethods.shared.Abnormality;
import de.d3web.kernel.psMethods.shared.AbnormalityNum;
public class AbnormalityNumTest extends TestCase {

	/**
	 * Constructor for AbnormalityNumTest.
	 * @param arg0
	 */
	public AbnormalityNumTest(String arg0) {
		super(arg0);
	}

	public static Test suite() {
		return new TestSuite (AbnormalityNumTest.class);
	}
	
	
	public void testConstruction() {
		AbnormalityNum an = new AbnormalityNum();
		
		try {
			an.addValue(1, 0, 0.5, true, true);
			assertTrue("missing: exception", false);
		} catch (NumericalInterval.IntervalException e) {
			System.out.println("" + e);
		}

		try {
			an.addValue(1, 1, 0.5, true, true);
			assertTrue("missing: exception", false);
		} catch (NumericalInterval.IntervalException e) {
			System.out.println("" + e);
		}


		try {
			an.addValue(0, 0, 0.5, true ,true);
			assertTrue("missing: exception", false);
		} catch (NumericalInterval.IntervalException e) {
			System.out.println("" + e);
		}

		try {
			an.addValue(0, 1, 0.5, true, false);
		} catch (NumericalInterval.IntervalException e) {
			assertTrue("exception", false);
		}
		
	}
	
	public void testValues() {
		AbnormalityNum an = new AbnormalityNum();

		try {
			an.addValue(0, 1, 0.1, true, false);
		} catch (NumericalInterval.IntervalException e) {
			assertTrue("exception", false);
		}

		try {
			an.addValue(1, 2, 0.5, false, true);
			assertTrue("missing exception", false);
		} catch (NumericalInterval.IntervalException e) {
			System.out.println("" + e);
		}

		try {
			an.addValue(-1,0, 0.5, false, false);
		} catch (NumericalInterval.IntervalException e) {
			assertTrue("exception", false);
		}

		try {
			an.addValue(- Double.MAX_VALUE, -2, Double.MAX_VALUE, false, false);
		} catch (NumericalInterval.IntervalException e) {
			assertTrue("exception " + e, false);
		} catch (Exception ex) {
			assertTrue("exception " + ex, false);
		}
		
		assertTrue("" + an.getValue(0.5) + "should be 0.1", an.getValue(0.5) == 0.1);
		assertTrue("" + an.getValue(-0.5) + "should be 0.5", an.getValue(-0.5) == 0.5);
		assertTrue("" + an.getValue(0) + "should be 0.5", an.getValue(0) == 0.5);
		assertTrue("" + an.getValue(1) + "should be 0.1", an.getValue(1) == 0.1);
		assertTrue("" + an.getValue(1.1) + "should be A0", an.getValue(1.1) == Abnormality.A0);
		assertTrue("" + an.getValue(-10) + "should be >= A5", an.getValue(-10) >= Abnormality.A5);
		
	}
	
}