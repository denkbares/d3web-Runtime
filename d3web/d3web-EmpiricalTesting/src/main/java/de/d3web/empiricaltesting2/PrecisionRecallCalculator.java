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

package de.d3web.empiricaltesting2;

public abstract class PrecisionRecallCalculator {
	
	/**
	 * Returns the precision of a RatedTestCase
	 * @param rtc The RatedTestCase necessary for the calculation.
	 * @return precision of the RatedTestCase.
	 */
	public double precision(RatedTestCase rtc) {
		if (rtc.getExpectedSolutions().size() > 0)
			return prec(rtc);
		else
			return 1;
	}
	
	public abstract double prec(RatedTestCase rtc);
	
	/**
	 * Returns the recall of a RatedTestCase
	 * @param rtc The RatedTestCase necessary for the calculation.
	 * @return recall of the RatedTestCase.
	 */
	public double recall(RatedTestCase rtc) {
		if (rtc.getExpectedSolutions().size() > 0)
			return rec(rtc);
		else
			return 1;
	}
	
	public abstract double rec(RatedTestCase rtc);

	/**
	 * Returns the fMeasure for a RatedTestCase and
	 * a specified Beta.
	 */
	public double fMeasure(double beta, RatedTestCase rtc) {
		double numerator = (Math.pow(beta, 2) + 1) * precision(rtc)
				* recall(rtc);
		double denominator = Math.pow(beta, 2) * precision(rtc) + recall(rtc);
		return numerator / denominator;
	}

}
