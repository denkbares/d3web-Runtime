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

import java.util.Date;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.empiricaltesting.Finding;
import de.d3web.empiricaltesting.RatedSolution;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.ScoreRating;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.StateRating;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.scoring.HeuristicRating;

/**
 * This class is used to run a sequential test case and compare the derived
 * results with the expected ones. For the comparison, we use the expected
 * solutions as well as possibly defined expected findings.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 24.03.2011
 */
public class STCAnalysis {

	/**
	 * Runs the specified test case using the specified knowledge base and
	 * compares the expected solutions/findings with the actually derived facts.
	 * The returned {@link STCDiff} stores the results of this comparison
	 * 
	 * @created 24.03.2011
	 * @param stc the specified sequential test case
	 * @param knowledge the specified knowledge base
	 * @return the results of the comparison
	 */
	public static STCDiff runAndAnalyze(SequentialTestCase stc, KnowledgeBase knowledge) {
		STCDiff diff = new STCDiff(stc);
		Date creationDate = getCreationDate(stc);
		Session session = SessionFactory.createSession(stc.getName() + now().toString(), knowledge,
				creationDate);
		for (RatedTestCase rtc : stc.getCases()) {
			setFindings(session, rtc);
			RTCDiff rtc_diff = compareExpectations(session, rtc);

			if (rtc_diff.hasDifferences()) {
				diff.add(rtc_diff);
			}
		}
		return diff;
	}

	private static void setFindings(Session session, RatedTestCase rtc) {
		long time = rtc.getTimeStamp().getTime();
		session.getPropagationManager().openPropagation(time);
		for (Finding f : rtc.getFindings()) {
			session.getBlackboard().addValueFact(
					new DefaultFact(f.getQuestion(), f.getValue(),
							PSMethodUserSelected.getInstance(),
							PSMethodUserSelected.getInstance()));
		}
		session.getPropagationManager().commitPropagation();
	}

	/**
	 * Returns the creation date of the sequential test case, i.e., the
	 * timestamp of the first rated test case. If none is available, then we
	 * take the current date.
	 * 
	 * @created 24.03.2011
	 * @param stc the specified sequential test case
	 * @return the creation date of the stc if available; NOW otherwise
	 */
	private static Date getCreationDate(SequentialTestCase stc) {
		if (stc.getCases() != null && stc.getCases().get(0) != null) {
			return getCreationDate(stc.getCases().get(0));
		}
		else {
			return now();
		}
	}

	/**
	 * Returns the creation date of the rated test case, i.e., its timestamp If
	 * none is available, then we take the current date.
	 * 
	 * @created 24.03.2011
	 * @param ratedTestCase the specified rated test case
	 * @return the creation date if available; NOW otherwise
	 */
	private static Date getCreationDate(RatedTestCase ratedTestCase) {
		Date date = ratedTestCase.getTimeStamp();
		if (date != null) {
			return date;
		}
		else {
			return now();
		}
	}

	private static RTCDiff compareExpectations(Session session, RatedTestCase rtc) {
		RTCDiff diffs = new RTCDiff(rtc);
		for (Finding expected : rtc.getExpectedFindings()) {
			Value derived = session.getBlackboard().getValue(expected.getQuestion());
			if (!derived.equals(expected.getValue())) {
				diffs.addExpectedButNotDerived(expected.getQuestion(), expected.getValue(), derived);
			}
		}
		for (RatedSolution expected : rtc.getExpectedSolutions()) {
			// Caution: THESE ARE DIFFERENT TYPES OF RATINGS
			Rating derived = session.getBlackboard().getRating(expected.getSolution());
			Rating expectedRating = convert(expected.getRating());
			if (!derived.equals(expectedRating)) {
				diffs.addExpectedButNotDerived(expected.getSolution(), expectedRating, derived);
			}
		}
		return diffs;
	}

	/**
	 * Convert the Rating known in EmpiricalTesting into a Rating known in the
	 * d3web Kernel.
	 * 
	 * @created 24.03.2011
	 * @param rating as known in EmpiricalTesting plugin
	 * @return rating instance as known in d3web core.
	 */
	private static Rating convert(de.d3web.empiricaltesting.Rating rating) {
		if (rating instanceof StateRating) {
			return ((StateRating) rating).getRating();
		}
		else if (rating instanceof ScoreRating) {
			ScoreRating sr = (ScoreRating) rating;
			return new HeuristicRating(sr.getRating());
		}
		else {
			throw new IllegalArgumentException("Unknown type of rating during conversion.");
		}
	}

	private static Date now() {
		return new Date();
	}
}
