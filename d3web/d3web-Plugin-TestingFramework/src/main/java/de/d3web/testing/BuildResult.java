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

package de.d3web.testing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An instance of this class holds the result of a ci build
 * 
 * @author Marc-Oliver Ochlast
 */
public final class BuildResult {

	/**
	 * List of all test results of the executed tests
	 */
	private final List<TestResult> results = new ArrayList<TestResult>();

	/**
	 * time/date of build execution
	 */
	private final Date buildDate;

	/**
	 * Duration how long this build has taken
	 */
	private long buildDuration = 0;

	/**
	 * The number of this build
	 */
	private int buildNumber = 0;

	public BuildResult() {
		this(new Date());
	}

	public BuildResult(Date buildDate) {
		this.buildDate = buildDate;
	}

	/**
	 * Returns the duration this build has required to be performed, given in
	 * milliseconds.
	 * 
	 * @created 03.02.2012
	 * @return in milliseconds
	 */
	public long getBuildDuration() {
		return buildDuration;
	}

	/**
	 * Sets the duration this build has required to be performed, given in
	 * milliseconds.
	 * 
	 * @created 03.02.2012
	 * @param timeSpentForBuild in milliseconds
	 */
	public void setBuildDuration(long timeSpentForBuild) {
		this.buildDuration = timeSpentForBuild;
	}

	/**
	 * Sets the build number of this {@link BuildResult}. Can be used for
	 * distinguishing builds later on, e.g. for rendering.
	 * 
	 * @created 18.09.2012
	 * @param buildNumber
	 */
	public void setBuildNumber(int buildNumber) {
		this.buildNumber = buildNumber;
	}

	/**
	 * Returns the date this build has been started.
	 * 
	 * @created 19.05.2012
	 * @return the build start time
	 */
	public Date getBuildDate() {
		return buildDate;
	}

	@Override
	public int hashCode() {
		List<TestResult> testResults = this.getResults();
		int hashCode = 31;
		for (TestResult testResult : testResults) {
			hashCode += testResult.hashCode();
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj instanceof BuildResult) {
			// build results are considered equal if containing the same test
			// results (disregarding build date, duration, number..)
			BuildResult other = (BuildResult) obj;
			Set<TestResult> otherSet = new HashSet<TestResult>();
			otherSet.addAll(other.getResults());

			Set<TestResult> thisSet = new HashSet<TestResult>();
			thisSet.addAll(this.getResults());

			if (!(thisSet.size() == otherSet.size())) {
				return false;
			}

			thisSet.removeAll(otherSet);
			if (thisSet.size() > 0) {
				return false;
			}
			// sets have equal size and contain same elements
			return true;
		}
		return false;
	}

	/**
	 * Return the build number of this build, or 0 if it is not set via
	 * {@link BuildResult#setBuildNumber(int)}.
	 * 
	 * @created 18.09.2012
	 * @return the build number of this build
	 */
	public int getBuildNumber() {
		return buildNumber;
	}

	/**
	 * Returns the list of all test results of this build.
	 * 
	 * @created 19.05.2012
	 * @return the results of this build
	 */
	public List<TestResult> getResults() {
		return Collections.unmodifiableList(results);
	}

	/**
	 * Computes the overall TestResultType of this resultset, determined by the
	 * "worst" Testresult
	 * 
	 * @created 03.06.2010
	 * @return
	 */
	public Message.Type getOverallResult() {

		Message.Type overallResult = Message.Type.SUCCESS;
		for (TestResult testResult : results) {
			if (testResult != null && testResult.getType().
					compareTo(overallResult) < 0) {
				overallResult = testResult.getType();
			}
		}
		return overallResult;
	}

	public void addTestResult(TestResult testResult) {
		results.add(testResult);
	}

	@Override
	public String toString() {
		StringBuilder build = new StringBuilder();
		build.append("Build #" + getBuildNumber() + ", date: "
				+ getBuildDate() + ", duration: " + buildDuration);
		for (TestResult result : getResults()) {
			build.append("\n    " + result);
		}
		return build.toString();
	}
}
