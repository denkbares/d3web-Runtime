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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RatedTestCase {

	/**
	 * The name of the Rated Test Case
	 */
	private String name = "";

	/**
	 * This Rated Testcase's List of Findings
	 */
	private List<Finding> findings;

	/**
	 * This Rated Testcase's List of expected Solutions (will be loaded from
	 * TestCaseRepository)
	 */
	private List<RatedSolution> expectedSolutions;

	/**
	 * List of expected findings (derived by a psmethod)
	 */
	private List<Finding> expectedFindings;

	/**
	 * This Rated Testcase's List of derived Solutions (derived from the
	 * KnowledgeBase while in TestRun)
	 */
	private List<RatedSolution> derivedSolutions;

	/**
	 * After deriving the actual solutions from the knowledge base, this must be
	 * set to true. Every change on this testCase (i.e. adding a finding) will
	 * reset this flag to false.
	 */
	private boolean derivedSolutionsAreUpToDate;

	/**
	 * Indication of when this ratedTestCase was tested
	 */
	private String lastTested;

	/**
	 * If this case has been tested before
	 */
	private boolean wasTestedBefore;

	/**
	 * Propagation time
	 */
	private Date timeStamp;

	/**
	 * Default Constructor.
	 */
	public RatedTestCase() {
		super();
		findings = new ArrayList<Finding>(1); // often only one finding
		// contained in the rtc
		expectedSolutions = new ArrayList<RatedSolution>();
		expectedFindings = new ArrayList<Finding>();
		derivedSolutions = new ArrayList<RatedSolution>();
		derivedSolutionsAreUpToDate = false;
		lastTested = "";
		wasTestedBefore = false;
	}

	/**
	 * Appends a Finding to this RatedTestCase's List of findings.
	 * 
	 * @param findings The Finding to be added
	 * @return True if the Finding was successfully appended
	 */
	public boolean add(Finding finding) {
		boolean result = findings.add(finding);
		if (result) derivedSolutionsAreUpToDate = false;
		return result;
	}

	/**
	 * Appends a whole list of Findings to this RatedTestCase's List of
	 * findings.
	 * 
	 * @param findings The List of Findings to be added
	 * @return True if the Findings were successfully appended
	 */
	public boolean addFindings(List<Finding> findings) {
		boolean result = true;
		for (Finding finding : findings) {
			result = add(finding) && result;
		}
		return result;
	}

	/**
	 * Appends one RatedSolution to this RatedTestCase's List of expected
	 * Solutions.
	 * 
	 * @param solution The RatedSolution to be added
	 * @return True if RatedSolution was successfully appended
	 */
	public boolean addExpected(RatedSolution solution) {
		boolean result = expectedSolutions.add(solution);
		if (result) derivedSolutionsAreUpToDate = false;
		return result;
	}

	/**
	 * Appends a whole list of RatedSolutions to this RatedTestCase's List of
	 * expected Solutions.
	 * 
	 * @param solutions The List of RatedSolutions to be added
	 * @return True if the RatedSolutions were successfully appended
	 */
	public boolean addExpected(List<RatedSolution> solutions) {
		boolean result = true;
		for (RatedSolution ratedSolution : solutions) {
			result = addExpected(ratedSolution) && result;
		}
		return result;
	}

	public boolean addExpectedFinding(Finding finding) {
		return this.expectedFindings.add(finding);
	}

	public boolean addExpectedFindings(Collection<Finding> expectedFindings) {
		return this.expectedFindings.addAll(expectedFindings);
	}

	/**
	 * Appends one RatedSolution to this RatedTestCase´s List of derived
	 * Solutions.
	 * 
	 * @param solution The RatedSolution to be added
	 * @return True if RatedSolution was successfully appended
	 */
	public boolean addDerived(RatedSolution solution) {
		boolean result = derivedSolutions.add(solution);
		if (result) derivedSolutionsAreUpToDate = false;
		return result;
	}

	/**
	 * Appends a whole list of RatedSolutions to this RatedTestCase's List of
	 * derived Solutions.
	 * 
	 * @param solutions The List of RatedSolutions to be added
	 * @return True if the RatedSolutions were successfully appended
	 */
	public boolean addDerived(List<RatedSolution> solutions) {
		boolean result = true;
		for (RatedSolution ratedSolution : solutions) {
			result = addDerived(ratedSolution) && result;
		}
		return result;
	}

	/**
	 * @return the derivedSolutionsAreUpToDate
	 */
	public boolean getDerivedSolutionsAreUpToDate() {
		return derivedSolutionsAreUpToDate;
	}

	/**
	 * @param derivedSolutionsAreUpToDate the derivedSolutionsAreUpToDate to set
	 */
	public void setDerivedSolutionsAreUpToDate(
			boolean derivedSolutionsAreUpToDate) {
		this.derivedSolutionsAreUpToDate = derivedSolutionsAreUpToDate;
	}

	/**
	 * Returns the Date on which this RatedTestCase was last tested.
	 * 
	 * @return the lastTested
	 */
	public String getLastTested() {
		return lastTested;
	}

	/**
	 * Sets TestingDate to a specified date.
	 * 
	 * @param date String formatted date
	 */
	public void setTestingDate(String date) {
		lastTested = date;
	}

	/**
	 * Sets TestingDate to now.
	 */
	public void setTestingDateNow() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HHmm");
		lastTested = df.format(new Date()).toString();
	}

	public void inverseSortSolutions() {
		Collections.sort(expectedSolutions,
				new RatedSolution.InverseRatingComparator());
		Collections.sort(derivedSolutions,
				new RatedSolution.InverseRatingComparator());
	}

	/**
	 * String Representation of this RatedTestCase. <name( findings : Expected:
	 * expectedSolutions; Derived: derivedSolutions; )>
	 */
	@Override
	public String toString() {
		return "<" + name + " (\n\tFindings:" + findings + "; \n\tExpected:"
				+ expectedSolutions + ", " + expectedFindings + "; \n\tDerived:" + derivedSolutions
				+ "; \n\t)>";
	}

	/**
	 * Returns the Findings of this RatedTestCase.
	 * 
	 * @return List of Findings
	 */
	public synchronized List<Finding> getFindings() {
		return findings;
	}

	/**
	 * Returns the ExpectedSolutions of this RatedTestCase.
	 * 
	 * @return List of RatedSolutions
	 */
	public synchronized List<RatedSolution> getExpectedSolutions() {
		return expectedSolutions;
	}

	public synchronized void setExpectedSolutions(List<RatedSolution> expectedSolutions) {
		this.expectedSolutions = expectedSolutions;
	}

	/**
	 * Returns the derivedSolutions of this RatedTestCase.
	 * 
	 * @return List of RatedSolutions
	 */
	public synchronized List<RatedSolution> getDerivedSolutions() {
		return derivedSolutions;
	}

	// @Deprecated
	// public void update(Solution solution, Rating rating) {
	// RatedSolution rsolution = getBySolution(solution);
	// if (rsolution == null) {
	// addExpected(new RatedSolution(solution, rating));
	// }
	// else {
	// rsolution.update(rating);
	// }
	// }
	//
	// @Deprecated
	// private RatedSolution getBySolution(Solution solution) {
	// for (RatedSolution rsol : expectedSolutions) {
	// if (rsol.solution.equals(solution)) return rsol;
	// }
	// return null;
	// }

	/**
	 * A new instance is created and the lists solutions and findings are copied
	 * into the new instance.
	 * 
	 * @return a flat clone of the instance
	 */
	public RatedTestCase flatClone() {
		RatedTestCase newRTC = new RatedTestCase();
		newRTC.name = name;
		newRTC.findings = findings;
		newRTC.expectedSolutions = expectedSolutions;
		newRTC.derivedSolutions = derivedSolutions;
		return newRTC;
	}

	/**
	 * Returns the name of this RatedTestCase.
	 * 
	 * @return String representing the name of this RatedTestCase
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this RatedTestCase.
	 * 
	 * @param name desired name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns true if this RatedTestCase is correct.
	 * 
	 * @return true if RatedTestCase is correct, false if RatedTestCase isn't
	 *         correct
	 */
	public boolean isCorrect() {
		ConfigLoader cf = ConfigLoader.getInstance();
		// EmpiricalTestingFunctions functions =
		// EmpiricalTestingFunctions.getInstance();
		double fMeasureDiff = Double.parseDouble(cf.getProperty("fMeasureDiff"));
		PrecisionRecallCalculator pr = DerivedSolutionsCalculator.getInstance();
		// return functions.fMeasure(1, this) >= (1.0-fMeasureDiff);
		return pr.fMeasure(1, this) >= (1.0 - fMeasureDiff);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((derivedSolutions == null) ? 0 : derivedSolutions.hashCode());
		result = prime
				* result
				+ ((expectedSolutions == null) ? 0 : expectedSolutions
						.hashCode());
		result = prime * result
				+ ((findings == null) ? 0 : findings.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof RatedTestCase)) return false;
		RatedTestCase other = (RatedTestCase) obj;
		if (derivedSolutions == null) {
			if (other.derivedSolutions != null) return false;
		}
		else if (!derivedSolutions.equals(other.derivedSolutions)) return false;
		if (expectedSolutions == null) {
			if (other.expectedSolutions != null) return false;
		}
		else if (!expectedSolutions.equals(other.expectedSolutions)) return false;
		if (findings == null) {
			if (other.findings != null) return false;
		}
		else if (!findings.equals(other.findings)) return false;
		if (name == null) {
			if (other.name != null) return false;
		}
		else if (!name.equals(other.name)) return false;
		return true;
	}

	/**
	 * Returns true if this RatedTestCase was tested before.
	 * 
	 * @return True if this RatedTestCase was tested before. Else false.
	 */
	public boolean wasTestedBefore() {
		return wasTestedBefore;
	}

	/**
	 * Sets if this RatedTestCase was tested before.
	 * 
	 * @param wasTestedBefore Boolean value.
	 */
	public void setWasTestedBefore(boolean wasTestedBefore) {
		this.wasTestedBefore = wasTestedBefore;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Collection<Finding> getExpectedFindings() {
		return Collections.unmodifiableCollection(expectedFindings);
	}
}