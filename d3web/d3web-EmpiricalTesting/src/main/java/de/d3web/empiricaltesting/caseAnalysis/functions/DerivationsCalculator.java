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

import java.util.Collection;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.caseAnalysis.RTCDiff;

public final class DerivationsCalculator extends PrecisionRecallCalculator {

	private static DerivationsCalculator instance = new DerivationsCalculator();

	private DerivationsCalculator() {
	}

	/**
	 * Returns an instance of DerivationsCalculator.
	 * 
	 * @return singleton instance of DerivationsCalculator
	 */
	public static PrecisionRecallCalculator getInstance() {
		return instance;
	}

	/**
	 * Calculates the precision of the specified differences.
	 * 
	 * @param rtcDiff the specified differences of a {@link RatedTestCase}.
	 * @return precision of the differences.
	 */
	@Override
	public double prec(RTCDiff rtcDiff) {
		Collection<TerminologyObject> derived = rtcDiff.getDerived();
		if (!derived.isEmpty()) {
			Collection<TerminologyObject> expected_intersect_derived = rtcDiff.correctlyDerived();
			return (1.0 * expected_intersect_derived.size()) / derived.size();
		}
		else if (derived.isEmpty() && rtcDiff.getExpected().isEmpty()) {
			return 1;
		}
		else {
			return 0;

		}
	}

	/**
	 * Calculates the recall of the specified differences.
	 * 
	 * @param rtcDiff the specified differences of a {@link RatedTestCase}.
	 * @return recall of the differences.
	 */
	@Override
	public double rec(RTCDiff rtcDiff) {
		Collection<TerminologyObject> expected = rtcDiff.getExpected();
		if (expected.isEmpty()) {
			return 1;
		}
		else {
			Collection<TerminologyObject> expected_intersect_derived = rtcDiff.correctlyDerived();
			return (1.0 * expected_intersect_derived.size()) / expected.size();
		}
	}

}
