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
import java.util.HashMap;
import java.util.Map;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Value;
import de.d3web.empiricaltesting.RatedTestCase;

/**
 * Stores the comparison of expected/derived results of a rated test case.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 24.03.2011
 */
public class RTCDiff {

	private RatedTestCase rtc;
	private Map<TerminologyObject, ValueDiff> differences;

	/**
	 * Creates a new comparison storage for the specified rated test case.
	 * 
	 * @param rtc the specified rated test case.
	 */
	public RTCDiff(RatedTestCase rtc) {
		this.rtc = rtc;
		this.differences = new HashMap<TerminologyObject, ValueDiff>();
	}

	/**
	 * Adds the deviation to the RTCDiff instance.
	 * 
	 * @created 24.03.2011
	 * @param expected
	 * @param derived
	 */
	public void addExpectedButNotDerived(Question question, Value expected, Value derived) {
		this.differences.put(question, new ValueDiff(expected, derived));
	}

	/**
	 * Adds the deviation to the RTCDiff instance.
	 * 
	 * @created 24.03.2011
	 * @param expected
	 * @param derived
	 */
	public void addExpectedButNotDerived(Solution solution, Rating expected, Rating derived) {
		this.differences.put(solution, new ValueDiff(expected, derived));
	}

	/**
	 * Returns the rated test case, that is compared in this instance.
	 * 
	 * @created 24.03.2011
	 * @return the rated test case under comparison.
	 */
	public RatedTestCase getCase() {
		return this.rtc;
	}

	/**
	 * Evaluates, whether differences are stored in this instance.
	 * 
	 * @created 24.03.2011
	 * @return true, when there are differences stored; false otherwise.
	 */
	public boolean hasDifferences() {
		return !differences.keySet().isEmpty();
	}

	public Collection<TerminologyObject> getDiffObjects() {
		return differences.keySet();
	}

	public ValueDiff getDiffFor(TerminologyObject terminologyObject) {
		return differences.get(terminologyObject);
	}

}
