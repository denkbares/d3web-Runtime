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

import java.util.ArrayList;
import java.util.List;

public class TestCaseComparator {

	List<SequentialTestCase> cases1, cases2;

	public List<SequentialTestCase> intersectingCases;
	public List<SequentialTestCase> onlyInCase1;
	List<SequentialTestCase> onlyInCase2;

	/**
	 * Default Constructor
	 * 
	 * @param cases1 first SequentialTestCase
	 * @param cases2 second SequentialTestCase
	 */
	public TestCaseComparator(List<SequentialTestCase> cases1,
			List<SequentialTestCase> cases2) {
		setCases1(cases1);
		setCases2(cases2);
	}

	/**
	 * Sets the first SequentialTestCase.
	 * 
	 * @param cases1 first case
	 */
	public void setCases1(List<SequentialTestCase> cases1) {
		this.cases1 = cases1;
		performComparison();
	}

	/**
	 * Sets the second SequentialTestCase.
	 * 
	 * @param cases2 second case
	 */
	public void setCases2(List<SequentialTestCase> cases2) {
		this.cases2 = cases2;
		performComparison();
	}

	/**
	 * Compares the SequentialTestCases.
	 */
	private void performComparison() {
		if (cases1 == null || cases2 == null) return;
		performFuzzyComparison();
	}

	private void performFuzzyComparison() {
		intersectingCases = new ArrayList<SequentialTestCase>();
		onlyInCase1 = new ArrayList<SequentialTestCase>();
		onlyInCase2 = new ArrayList<SequentialTestCase>();
		for (SequentialTestCase stc : cases1) {
			if (contains(cases2, stc)) intersectingCases.add(stc);
			else onlyInCase1.add(stc);
		}
		for (SequentialTestCase stc : cases2) {
			if (!contains(cases1, stc)) onlyInCase2.add(stc);
		}
	}

	/**
	 * Checks if the SequentialTestCases contain the same RatedTestCases
	 * 
	 * @return True if RatedTestCases are equal. Else false.
	 */
	public boolean haveSameRatedTestCases() {
		return cases1.containsAll(cases2);
	}

	/**
	 * Returns true if the RatedTestCases have the same findings and the same
	 * expected solutions
	 * 
	 * @param rtc1 First RatedTestCase.
	 * @param rtc2 Second RatedTestCase
	 * @return true if RatedTestCases are equal. Else false.
	 */
	public static boolean equals(RatedTestCase rtc1, RatedTestCase rtc2) {
		// return rtc1.getFindings().equals(rtc2.getFindings()) &&
		// rtc1.getSolutions().equals(rtc2.getSolutions());
		if (!rtc1.getFindings().equals(rtc2.getFindings())) return false;
		for (RatedSolution rs : rtc1.getExpectedSolutions()) {
			if (!rtc2.getExpectedSolutions().contains(rs)) return false;
		}
		return true;
	}

	/**
	 * Returns true if SequentialTestCase contains specified RatedTestCase.
	 * 
	 * @param stc The SequentialTestCase which will be tested.
	 * @param rtc The searched RatedTestCase.
	 * @return true if STC contains RTC. Else false.
	 */
	public static boolean contains(SequentialTestCase stc, RatedTestCase rtc) {
		for (RatedTestCase tc : stc.getCases()) {
			if (equals(tc, rtc)) return true;
		}
		return false;
	}

	/**
	 * Returns true if two SequentialTestCases have the same RatedTestCases.
	 * 
	 * @param stc1 First SequentialTestCase.
	 * @param stc2 Second SequentialTestCase.
	 * @return True if SequentialTestCases are equal. Else false.
	 */
	public static boolean equals(SequentialTestCase stc1,
			SequentialTestCase stc2) {
		if (stc1.getCases().size() != stc2.getCases().size()) return false;
		for (RatedTestCase tc : stc1.getCases()) {
			if (!contains(stc2, tc)) return false;
		}
		return true;
	}

	/**
	 * Returns true if one SequentialTestCase in a list of SequentialTestCases
	 * equals a specified SequentialTestCase
	 * 
	 * @param cases List of SequentialTestCases.
	 * @param stc The searched SequentialTestCase.
	 * @return True if list contains searched STC. Else false.
	 */
	public static boolean contains(List<SequentialTestCase> cases,
			SequentialTestCase stc) {
		for (SequentialTestCase sequentialTestCase : cases) {
			if (equals(stc, sequentialTestCase)) return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Intersecting: " + intersectingCases.size() + "  onlyIn1: "
				+ onlyInCase1.size() + " onlyIn2: " + onlyInCase2.size();
	}
}
