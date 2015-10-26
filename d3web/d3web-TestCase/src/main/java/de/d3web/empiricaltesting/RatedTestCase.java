/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.d3web.collections.DefaultMultiMap;
import de.d3web.collections.MultiMaps;
import de.d3web.testcase.model.Check;
import de.d3web.testcase.model.Finding;

public class RatedTestCase {

	/**
	 * The name of the Rated Test Case
	 */
	private String name = null;

	/**
	 * A note related to this Rated Test Case. The note may be used if some
	 * additional description is required for that case.
	 */
	private String note = null;

	/**
	 * Propagation time
	 */
	private Date timeStamp;

	private DefaultMultiMap<Class<? extends Finding>, Finding> findingsMap = new DefaultMultiMap<>(
			MultiMaps.linkedFactory(),
			MultiMaps.linkedFactory());

	private DefaultMultiMap<Class<? extends Check>, Check> checksMap = new DefaultMultiMap<>(
			MultiMaps.linkedFactory(),
			MultiMaps.linkedFactory());


	public RatedTestCase() {
	}

	public void addFinding(Finding... findings) {
		for (Finding finding : findings) {
			findingsMap.put(finding.getClass(), finding);
		}
	}

	public void addCheck(Check... checks) {
		for (Check check : checks) {
			checksMap.put(check.getClass(), check);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getNote() {
		return note;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RatedTestCase that = (RatedTestCase) o;

		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (note != null ? !note.equals(that.note) : that.note != null) return false;
		if (!findingsMap.equals(that.findingsMap)) return false;
		if (!checksMap.equals(that.checksMap)) return false;
		return !(timeStamp != null ? !timeStamp.equals(that.timeStamp) : that.timeStamp != null);

	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (note != null ? note.hashCode() : 0);
		result = 31 * result + findingsMap.hashCode();
		result = 31 * result + checksMap.hashCode();
		result = 31 * result + (timeStamp != null ? timeStamp.hashCode() : 0);
		return result;
	}

	/**
	 * String Representation of this RatedTestCase.
	 */
	@Override
	public String toString() {
		return name + ": \n"
				+ "\tFindings: " + findingsMap.valueSet() + "\n"
				+ "\tChecks: " + checksMap.valueSet() + "\n"
				+ "\tDerived: " + derivedSolutions + "\n";
	}

	/**
	 * A new instance is created and the lists solutions and findings are copied
	 * into the new instance.
	 *
	 * @return a flat clone of the instance
	 */
	public RatedTestCase flatClone() {
		RatedTestCase newRTC = new RatedTestCase();
		newRTC.name = this.name;
		newRTC.note = this.note;
		newRTC.timeStamp = this.timeStamp;
		newRTC.findingsMap = this.findingsMap;
		newRTC.checksMap = this.checksMap;
		return newRTC;
	}

	/////////////////////////////////////////////////////////////////////////////////
	// Below you will find all the now deprecated methods to add and retrieve the  //
	// deprecated case objects (and some other stuff that should not be here).     //
	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * Indication of when this ratedTestCase was tested
	 */
	private String lastTested = "";

	/**
	 * If this case has been tested before
	 */
	private boolean wasTestedBefore = false;

	/**
	 * This Rated Testcase's List of derived Solutions (derived from the
	 * KnowledgeBase while in TestRun)
	 */
	private List<RatedSolution> derivedSolutions = new ArrayList<>();

	/**
	 * Returns true if this RatedTestCase was tested before.
	 *
	 * @return True if this RatedTestCase was tested before. Else false.
	 */
	@Deprecated
	public boolean wasTestedBefore() {
		return wasTestedBefore;
	}

	/**
	 * Sets if this RatedTestCase was tested before.
	 *
	 * @param wasTestedBefore Boolean value.
	 */
	@Deprecated
	public void setWasTestedBefore(boolean wasTestedBefore) {
		this.wasTestedBefore = wasTestedBefore;
	}

	/**
	 * Returns the Date on which this RatedTestCase was last tested.
	 *
	 * @return the lastTested
	 */
	@Deprecated
	public String getLastTested() {
		return lastTested;
	}

	/**
	 * Sets TestingDate to a specified date.
	 *
	 * @param date String formatted date
	 */
	@Deprecated
	public void setTestingDate(String date) {
		lastTested = date;
	}

	/**
	 * @deprecated no longer implemented
	 */
	@Deprecated
	public void inverseSortSolutions() {

	}

	/**
	 * Appends a Finding to this RatedTestCase's List of findings.
	 *
	 * @param finding The Finding to be added
	 * @return true if the Finding was successfully appended
	 */
	@Deprecated
	public boolean add(de.d3web.empiricaltesting.Finding finding) {
		return findingsMap.put(finding.getClass(), finding);
	}

	/**
	 * Appends a whole list of Findings to this RatedTestCase's List of
	 * findings.
	 *
	 * @param findings The List of Findings to be added
	 * @return True if the Findings were successfully appended
	 */
	@Deprecated
	public boolean addFindings(List<de.d3web.empiricaltesting.Finding> findings) {
		boolean result = true;
		for (de.d3web.empiricaltesting.Finding finding : findings) {
			result = add(finding) && result;
		}
		return result;
	}

	/**
	 * Appends some RatedSolution instances to this RatedTestCase's List of
	 * expected solutions.
	 *
	 * @param solutions The RatedSolution instances to be added
	 * @return True if the solutions were successfully appended
	 */
	@Deprecated
	public boolean addExpected(de.d3web.empiricaltesting.RatedSolution... solutions) {
		boolean result = true;
		for (RatedSolution solution : solutions) {
			result = result && checksMap.put(solution.getClass(), solution);
		}
		return result;
	}

	/**
	 * Appends a whole list of RatedSolutions to this RatedTestCase's List of
	 * expected Solutions.
	 *
	 * @param solutions The List of RatedSolutions to be added
	 * @return True if the RatedSolutions were successfully appended
	 */
	@Deprecated
	public boolean addExpected(List<de.d3web.empiricaltesting.RatedSolution> solutions) {
		boolean result = true;
		for (RatedSolution ratedSolution : solutions) {
			result = addExpected(ratedSolution) && result;
		}
		return result;
	}

	@Deprecated
	public boolean addExpectedFinding(de.d3web.empiricaltesting.Finding... findings) {
		boolean allAdded = true;
		for (de.d3web.empiricaltesting.Finding finding : findings) {
			allAdded = allAdded && this.checksMap.put(finding.getClass(), finding);
		}
		return allAdded;
	}

	@Deprecated
	public boolean addExpectedRegexFinding(RegexFinding... findings) {
		boolean allAdded = true;
		for (RegexFinding finding : findings) {
			allAdded = allAdded && this.checksMap.put(finding.getClass(), finding);
		}
		return allAdded;
	}

	@Deprecated
	public boolean addExpectedFindings(Collection<de.d3web.empiricaltesting.Finding> expectedFindings) {
		return addExpectedFinding((de.d3web.empiricaltesting.Finding[]) expectedFindings.toArray());
	}

	/**
	 * Appends some RatedSolution instances to this RatedTestCase´s List of
	 * derived solutions.
	 *
	 * @param solutions The RatedSolution instances to be added
	 * @return True if RatedSolution was successfully appended
	 */
	@Deprecated
	public boolean addDerived(RatedSolution... solutions) {
		boolean result = true;
		for (RatedSolution solution : solutions) {
			result = result && derivedSolutions.add(solution);
		}
		return result;
	}

	/**
	 * Appends a whole list of RatedSolutions to this RatedTestCase's List of
	 * derived Solutions.
	 *
	 * @param solutions The List of RatedSolutions to be added
	 * @return True if the RatedSolutions were successfully appended
	 * @deprecated no longer use this method, it will be removed with the next
	 * release
	 */
	@Deprecated
	public boolean addDerived(List<RatedSolution> solutions) {
		boolean result = true;
		for (RatedSolution ratedSolution : solutions) {
			result = addDerived(ratedSolution) && result;
		}
		return result;
	}

	/**
	 * Returns the Findings of this RatedTestCase.
	 *
	 * @return List of Findings
	 */
	@Deprecated
	public synchronized List<de.d3web.empiricaltesting.Finding> getFindings() {
		//noinspection unchecked
		return new ArrayList(findingsMap.getValues(de.d3web.empiricaltesting.Finding.class));
	}

	/**
	 * Returns the ExpectedSolutions of this RatedTestCase.
	 *
	 * @return List of RatedSolutions
	 */
	@Deprecated
	public synchronized List<RatedSolution> getExpectedSolutions() {
		//noinspection unchecked
		return new ArrayList(checksMap.getValues(RatedSolution.class));
	}

	@Deprecated
	public synchronized void setExpectedSolutions(List<RatedSolution> expectedSolutions) {
		checksMap.removeKey(RatedSolution.class);
		addExpected(expectedSolutions);
	}

	/**
	 * Returns the derivedSolutions of this RatedTestCase.
	 *
	 * @return List of RatedSolutions
	 */
	@Deprecated
	public synchronized List<RatedSolution> getDerivedSolutions() {
		return derivedSolutions;
	}

	@Deprecated
	public Collection<de.d3web.empiricaltesting.Finding> getExpectedFindings() {
		//noinspection unchecked
		return new ArrayList(checksMap.getValues(de.d3web.empiricaltesting.Finding.class));
	}

	@Deprecated
	public Collection<RegexFinding> getExpectedRegexFindings() {
		//noinspection unchecked
		return new ArrayList(checksMap.getValues(RegexFinding.class));
	}

}