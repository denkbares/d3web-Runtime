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

package de.d3web.empiricaltesting;

public class EmpiricalTestingFunctions {

	private static EmpiricalTestingFunctions instance;

	private EmpiricalTestingFunctions() {
	}

	public static EmpiricalTestingFunctions getInstance() {
		if (instance == null) instance = new EmpiricalTestingFunctions();
		return instance;
	}

	// -------Chained Precision--------

	private int wp(int i) {
		return 1;
	}

	/**
	 * Default Precision Calculation. Returns the precision of the derived
	 * solutions.
	 * 
	 * @param stc The SequentialTestCase necessary for the Calculation
	 * @return Precision of the SequentialTestCase
	 */
	public double precision(SequentialTestCase stc) {
		return precision(stc, DerivedSolutionsCalculator.getInstance(), false);
	}

	/**
	 * Returns the Precision for a specified state (e.g. derived solutions,
	 * interview etc.).
	 * 
	 * @param stc The underlying STC
	 * @param state The state for precision calculation
	 * @param nonsequential if true, only the last rtc of each stc is taken into
	 *        account
	 * @return Precision of the SequentialTestCase and state
	 */
	public double precision(SequentialTestCase stc, PrecisionRecallCalculator state, boolean nonsequential) {
		if (stc.getCases().size() == 0) {
			return 0;
		}
		else {
			double numerator = 0;
			double denominator = 0;

			if (nonsequential) {
				numerator += 1 * state.precision(stc.getCases().get(stc.getCases().size() - 1));
				denominator += 1;
			}
			else {
				for (int i = 0; i < stc.getCases().size(); i++) {
					numerator += wp(i) * state.precision(stc.getCases().get(i));
					denominator += wp(i);
				}
			}

			return numerator / denominator;
		}
	}

	// -------Chained Recall--------

	/**
	 * Köööönte man noch iiirgendwann mal inverse annealing machen ;-)
	 */
	private int wr(int i) {
		return 1;
	}

	/**
	 * Default Recall Calculation. Returns the recall of the derived solutions.
	 * 
	 * @param stc The SequentialTestCase necessary for the Calculation
	 * @return Recall of the SequentialTestCase
	 */
	public double recall(SequentialTestCase stc) {
		return recall(stc, DerivedSolutionsCalculator.getInstance(), false);
	}

	/**
	 * Returns the Recall for a specified state (e.g. derived solutions,
	 * interview etc.).
	 * 
	 * @param stc The underlying STC
	 * @param state The state for recall calculation
	 * @param nonsequential if true, only the last rtc of each stc is taken into
	 *        account
	 * @return Recall of the SequentialTestCase and state
	 */
	public double recall(SequentialTestCase stc, PrecisionRecallCalculator state, boolean nonsequential) {
		if (stc.getCases().size() == 0) {
			return 1;
		}
		else {
			double numerator = 0;
			double denominator = 0;

			if (nonsequential) {
				numerator += 1 * state.recall(stc.getCases().get(stc.getCases().size() - 1));
				denominator += 1;
			}
			else {
				for (int i = 0; i < stc.getCases().size(); i++) {
					numerator += wr(i) * state.recall(stc.getCases().get(i));
					denominator += wr(i);
				}
			}
			return numerator / denominator;
		}
	}

	// -------F Measure for SequentialTestCases --------

	/**
	 * Returns the fMeasure of a SequentialTestCase depending on the committed
	 * state.
	 * 
	 * @param beta Beta value for the calculation
	 * @param stc The underlying SequentialTestCase
	 * @param state The state on which the calculation will be based
	 * @return fMeasure of the SequentialTestCase and state
	 */
	public double fMeasure(double beta, SequentialTestCase stc, PrecisionRecallCalculator state) {
		double numerator = 0;
		for (RatedTestCase rtc : stc.getCases())
			numerator += state.fMeasure(beta, rtc);
		double denominator = stc.getCases().size();
		return numerator / denominator;
	}
}
