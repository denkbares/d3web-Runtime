/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.d3web.empiricaltesting.caseAnalysis;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.caseAnalysis.functions.Diff;

/**
 * This class stores the differences between the expected and derived results of
 * a sequential test case.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 24.03.2011
 */
public class STCDiff implements Diff {

	private Map<RatedTestCase, RTCDiff> rtc_diffs;
	private SequentialTestCase stc;
	private Date analysisDate;

	/**
	 * Creates a new differences storage for the specified
	 * {@link SequentialTestCase} instance. Now is used for the date of the
	 * analysis run.
	 * 
	 * @param stc the specified {@link SequentialTestCase} instance
	 */
	public STCDiff(SequentialTestCase stc) {
		this(stc, new Date());
	}

	/**
	 * Creates a new differences storage for the specified
	 * {@link SequentialTestCase} instance. The specified date is used as the
	 * analysis date of the empirical test run.
	 * 
	 * @param stc the specified {@link SequentialTestCase} instance
	 * @param analysisDate the specified analysis date of the test run
	 */
	public STCDiff(SequentialTestCase stc, Date analysisDate) {
		this.stc = stc;
		this.rtc_diffs = new HashMap<RatedTestCase, RTCDiff>();
		this.analysisDate = analysisDate;
	}

	/**
	 * Adds a new difference wrt a {@link RatedTestCase} to this instance.
	 * 
	 * @created 24.03.2011
	 * @param rtc_diff a new difference
	 */
	public void add(RTCDiff rtc_diff) {
		this.rtc_diffs.put(rtc_diff.getCase(), rtc_diff);
	}

	/**
	 * Checks, whether the specified {@link RatedTestCase} as stored some
	 * differences in this instance.
	 * 
	 * @created 24.03.2011
	 * @param rtc the specified rated test case
	 * @return true, when differences are stores; false otherwise.
	 */
	@Override
	public boolean hasDiff(RatedTestCase rtc) {
		if (rtc == null) {
			return true;
		}

		RTCDiff rtcDiff = this.rtc_diffs.get(rtc);
		if (rtcDiff == null || !rtcDiff.hasDifferences()) {
			return false;
		}
		else {
			return true;
		}
	}

	/**
	 * Retrieves the differences stored for the specified rated test case.
	 * 
	 * @created 24.03.2011
	 * @param rtc the specified rated test case
	 * @return the stored differences
	 */
	@Override
	public RTCDiff getDiff(RatedTestCase rtc) {
		return this.rtc_diffs.get(rtc);
	}

	/**
	 * Evaluates, whether differences are stored in this instance.
	 * 
	 * @created 24.03.2011
	 * @return true, when there are differences stored; false otherwise.
	 */
	@Override
	public boolean hasDifferences() {
		return !rtc_diffs.keySet().isEmpty();
	}

	/**
	 * Returns the {@link SequentialTestCase} instance, that was analyzed.
	 * 
	 * @created 24.03.2011
	 * @return the analyzed sequential test case
	 */
	@Override
	public SequentialTestCase getCase() {
		return this.stc;
	}

	/**
	 * Return the date, when the analysis was started.
	 * 
	 * @created 24.03.2011
	 * @return the date of this analysis
	 */
	@Override
	public Date getAnalysisDate() {
		return this.analysisDate;
	}

	/**
	 * Returns all {@link RatedTestCase} instances of the sequential test case,
	 * that reported differences during the analysis.
	 * 
	 * @created 24.03.2011
	 * @return all rated test cases with differences
	 */
	@Override
	public Collection<RatedTestCase> getCasesWithDifference() {
		return rtc_diffs.keySet();
	}
}
