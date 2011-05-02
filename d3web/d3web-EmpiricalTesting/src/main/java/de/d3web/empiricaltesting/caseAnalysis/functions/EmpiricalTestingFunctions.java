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

import java.util.List;

import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.caseAnalysis.STCDiff;

public final class EmpiricalTestingFunctions {

	private static EmpiricalTestingFunctions instance;

	private EmpiricalTestingFunctions() {
	}

	public static EmpiricalTestingFunctions getInstance() {
		if (instance == null) instance = new EmpiricalTestingFunctions();
		return instance;
	}

	// -------Chained Precision--------

	private int wp() {
		return 1;
	}

	/**
	 * Computes the precision for a specified strategy, e.g. derived
	 * solutions/findings, interview agenda etc.
	 * 
	 * @param stcDiff the differences computed during the test case analysis
	 * @param strategy the strategy for precision calculation
	 * @return precision of the SequentialTestCase and the specified strategy
	 */
	public double precision(Diff stcDiff, PrecisionRecallCalculator strategy) {
		if (stcDiff.getCase().getCases().size() == 0) {
			return 0;
		}
		else if (!stcDiff.hasDifferences()) {
			return 1;
		}
		else {
			double numerator = 0;
			double denominator = 0;
			List<RatedTestCase> cases = stcDiff.getCase().getCases();

			for (int i = 0; i < cases.size(); i++) {
				RatedTestCase rtc = cases.get(i);
				if (stcDiff.hasDiff(rtc)) {
					numerator += wp() * strategy.precision(stcDiff.getDiff(rtc));
				}
				else {
					numerator += wp();
				}
				denominator += wp();
			}
			return numerator / denominator;
		}
	}

	// -------Chained Recall--------

	/**
	 * Assume a constant weight.
	 */
	private int wr() {
		return 1;
	}

	/**
	 * Computes the recall of the test case analysis using the specified
	 * strategy, e.g. derived solutions/findings, interview agenda.
	 * 
	 * @param stcDiff the differences computed during the test case analysis.
	 * @param strategy the strategy for recall calculation
	 * @return Recall of the SequentialTestCase and state
	 */
	public double recall(Diff stcDiff, PrecisionRecallCalculator strategy) {
		if (stcDiff.getCase().getCases().size() == 0) {
			return 1;
		}
		else if (!stcDiff.hasDifferences()) {
			return 1;
		}
		else {
			double numerator = 0;
			double denominator = 0;
			List<RatedTestCase> cases = stcDiff.getCase().getCases();

			for (int i = 0; i < cases.size(); i++) {
				RatedTestCase rtc = cases.get(i);
				if (stcDiff.hasDiff(rtc)) {
					numerator += wr() * strategy.recall(stcDiff.getDiff(rtc));
				}
				else {
					numerator += wr();
				}
				denominator += wr();
			}
			return numerator / denominator;
		}
	}

	// -------F Measure for SequentialTestCases --------

	/**
	 * Returns the fMeasure of a {@link SequentialTestCase} depending on the
	 * specified strategy.
	 * 
	 * @param beta beta value for the calculation
	 * @param stcDiff the differences computed during the test case analysis
	 * @param strategy the algorithm strategy on which the calculation will be
	 *        based
	 * @return fMeasure of the {@link SequentialTestCase} computed using the
	 *         specified strategy
	 */
	public double fMeasure(double beta, STCDiff stcDiff, PrecisionRecallCalculator strategy) {
		double numerator = 0;
		for (RatedTestCase rtc : stcDiff.getCase().getCases()) {
			numerator += strategy.fMeasure(beta, stcDiff.getDiff(rtc));
		}
		double denominator = stcDiff.getCase().getCases().size();
		return numerator / denominator;
	}
}
