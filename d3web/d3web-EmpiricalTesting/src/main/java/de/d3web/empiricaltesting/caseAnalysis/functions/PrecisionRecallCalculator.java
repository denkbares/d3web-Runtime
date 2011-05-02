/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.empiricaltesting.caseAnalysis.functions;

import de.d3web.empiricaltesting.caseAnalysis.RTCDiff;

public abstract class PrecisionRecallCalculator {

	/**
	 * Returns the precision of a RatedTestCase
	 * 
	 * @param rtcDiff The RatedTestCase necessary for the calculation.
	 * @return precision of the RatedTestCase.
	 */
	public double precision(RTCDiff rtcDiff) {
		if (rtcDiff.hasDifferences()) {
			return prec(rtcDiff);
		}
		else {
			return 1;
		}
	}

	public abstract double prec(RTCDiff rtc);

	/**
	 * Returns the recall of a RatedTestCase
	 * 
	 * @param rtcDiff The RatedTestCase necessary for the calculation.
	 * @return recall of the RatedTestCase.
	 */
	public double recall(RTCDiff rtcDiff) {
		if (rtcDiff.hasDifferences()) {
			return rec(rtcDiff);
		}
		else {
			return 1;
		}
	}

	public abstract double rec(RTCDiff rtcDiff);

	/**
	 * Returns the fMeasure for a RatedTestCase and a specified Beta.
	 */
	public double fMeasure(double beta, RTCDiff rtcDiff) {
		if (rtcDiff == null) {
			return 1;
		}

		double numerator = (Math.pow(beta, 2) + 1) * precision(rtcDiff) * recall(rtcDiff);
		double denominator = Math.pow(beta, 2) * precision(rtcDiff) + recall(rtcDiff);
		if (denominator == 0) {
			return 0;
		}
		else {
			return numerator / denominator;
		}
	}

}
