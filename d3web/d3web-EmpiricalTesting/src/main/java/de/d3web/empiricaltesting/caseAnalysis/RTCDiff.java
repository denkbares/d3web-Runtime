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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Value;
import de.d3web.empiricaltesting.Finding;
import de.d3web.empiricaltesting.RatedSolution;
import de.d3web.empiricaltesting.RatedTestCase;

/**
 * Stores the comparison of expected/derived results of a rated test case.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 24.03.2011
 */
public class RTCDiff {

	private final RatedTestCase rtc;
	private final Map<TerminologyObject, ValueDiff> differencesExpectedButNotDerived;
	private final Map<TerminologyObject, ValueDiff> differencesDerivedButNotExpected;

	/**
	 * Creates a new comparison storage for the specified rated test case.
	 * 
	 * @param rtc the specified rated test case.
	 */
	public RTCDiff(RatedTestCase rtc) {
		this.rtc = rtc;
		this.differencesExpectedButNotDerived = new HashMap<TerminologyObject, ValueDiff>();
		this.differencesDerivedButNotExpected = new HashMap<TerminologyObject, ValueDiff>();
	}

	/**
	 * Adds the deviation to the RTCDiff instance.
	 * 
	 * @created 24.03.2011
	 * @param expected
	 * @param derived
	 */
	public void addExpectedButNotDerived(Question question, Value expected, Value derived) {
		this.differencesExpectedButNotDerived.put(question, new ValueDiff(expected, derived));
	}

	/**
	 * Adds the deviation to the RTCDiff instance.
	 * 
	 * @created 24.03.2011
	 * @param expected
	 * @param derived
	 */
	public void addExpectedButNotDerived(Solution solution, Rating expected, Rating derived) {
		this.differencesExpectedButNotDerived.put(solution, new ValueDiff(expected, derived));
	}

	/**
	 * Adds a deviation to the RTCDiff instance, which says that there exists a
	 * derived solution that was not expected.
	 * 
	 * @created Nov 3, 2011
	 * @param solution
	 * @param derived
	 */
	public void addDerivedButNotExpected(Solution solution, Rating derived) {
		this.differencesDerivedButNotExpected.put(solution, new ValueDiff(new Rating(State.UNCLEAR), derived));
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
	public boolean hasDifferencesExpectedButNotDerived() {
		return !differencesExpectedButNotDerived.keySet().isEmpty();
	}

	public Collection<TerminologyObject> getExpectedButNotDerivedDiffObjects() {
		return differencesExpectedButNotDerived.keySet();
	}

	public ValueDiff getExpectedButNotDerivedDiffFor(TerminologyObject terminologyObject) {
		return differencesExpectedButNotDerived.get(terminologyObject);
	}

	public boolean hasExpectedButNotDerivedDiff(TerminologyObject terminologyObject) {
		return (getExpectedButNotDerivedDiffFor(terminologyObject) != null);
	}
	
	public boolean hasDifferencesDerivedButNotExpected() {
		return !differencesDerivedButNotExpected.keySet().isEmpty();
	}

	public Collection<TerminologyObject> getDerivedButNotExpectedDiffObjects() {
		return differencesDerivedButNotExpected.keySet();
	}

	public ValueDiff getDerivedButNotExpectedDiffFor(TerminologyObject terminologyObject) {
		return differencesDerivedButNotExpected.get(terminologyObject);
	}

	public boolean hasDerivedButNotExpectedDiff(TerminologyObject terminologyObject) {
		return (getExpectedButNotDerivedDiffFor(terminologyObject) != null);
	}

	
	
	public boolean hasDifferences() {
		return !differencesDerivedButNotExpected.keySet().isEmpty() || !differencesExpectedButNotDerived.keySet().isEmpty();
	}

	public Collection<TerminologyObject> getDiffObjects() {
		Set<TerminologyObject> result = new HashSet<TerminologyObject>();
		result.addAll(differencesDerivedButNotExpected.keySet());
		result.addAll(differencesExpectedButNotDerived.keySet());
		return result;
	}

	public ValueDiff getDiffFor(TerminologyObject terminologyObject) {
		ValueDiff diff = differencesDerivedButNotExpected.get(terminologyObject);
		if(diff != null) return diff;
		diff = differencesExpectedButNotDerived.get(terminologyObject);
		return diff;
	}

	public boolean hasDiff(TerminologyObject terminologyObject) {
		return (hasExpectedButNotDerivedDiff(terminologyObject)) || (hasDerivedButNotExpectedDiff(terminologyObject));
	}
	
	/**
	 * Returns all questions and solutions, that were derived with the result as
	 * expected.
	 * 
	 * @created 25.03.2011
	 * @return
	 */
	public Collection<TerminologyObject> correctlyDerived() {
		Collection<TerminologyObject> correct = new HashSet<TerminologyObject>();
		for (Finding finding : rtc.getExpectedFindings()) {
			if (!hasDiff(finding.getQuestion())) correct.add(finding.getQuestion());
		}
		for (RatedSolution rsolution : rtc.getExpectedSolutions()) {
			if (!hasDiff(rsolution.getSolution())) correct.add(rsolution.getSolution());
		}
		return correct;
	}

	/**
	 * Returns the positively derived terminology objects.
	 * 
	 * @created 25.03.2011
	 * @return all positively derived terminology objects.
	 */
	public Collection<TerminologyObject> getDerived() {
		Collection<TerminologyObject> all_derived = new HashSet<TerminologyObject>();
		all_derived.addAll(differencesExpectedButNotDerived.keySet());
		all_derived.addAll(correctlyDerived());

		return all_derived;
	}

	/**
	 * All expected questions and solutions of this rated test case.
	 * 
	 * @created 25.03.2011
	 * @return All expected questions and solutions of this rated test case.
	 */
	public Collection<TerminologyObject> getExpected() {
		Collection<TerminologyObject> expected = new HashSet<TerminologyObject>();
		for (Finding finding : getCase().getExpectedFindings()) {
			expected.add(finding.getQuestion());
		}
		for (RatedSolution rsolution : getCase().getExpectedSolutions()) {
			expected.add(rsolution.getSolution());
		}
		return expected;
	}

}
