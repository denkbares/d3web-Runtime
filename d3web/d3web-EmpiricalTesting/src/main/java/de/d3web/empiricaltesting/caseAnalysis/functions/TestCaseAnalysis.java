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
package de.d3web.empiricaltesting.caseAnalysis.functions;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.TestListener;
import de.d3web.empiricaltesting.caseAnalysis.RTCDiff;
import de.d3web.empiricaltesting.caseAnalysis.STCDiff;
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
public class TestCaseAnalysis {

	private final List<TestListener> listeners;

	public TestCaseAnalysis() {
		listeners = new LinkedList<TestListener>();
	}


	/**
	 * Performs a test case analysis on the specified collection of
	 * {@link SequentialTestCase} instances. Each {@link SequentialTestCase}
	 * instance is run by a single {@link Session} and the expected and derived
	 * facts are compared. As the result of the comparison, a
	 * {@link TestCaseAnalysisReport} is returned.
	 * 
	 * @created 28.03.2011
	 * @param suite the specified collection of {@link SequentialTestCase}
	 *        instances.
	 * @return the generated report of differences between expected and derived
	 *         facts
	 */
	public TestCaseAnalysisReport runAndAnalyze(TestCase suite) {
		notifyTestcaseStart(suite);
		TestCaseAnalysisReport result = new AnalysisReport();
		for (SequentialTestCase stc : suite.getRepository()) {
			Diff diff = runAndAnalyze(stc, suite.getKb());
			result.add(diff);
		}
		notifyTestcaseFinished(suite, result);
		return result;
	}

	/**
	 * 
	 * @created 06.09.2011
	 * @param suite
	 * @param result
	 */
	private void notifyTestcaseFinished(TestCase suite, TestCaseAnalysisReport result) {
		for (TestListener listener : this.listeners) {
			listener.testcaseFinished(suite, result);
		}
	}

	/**
	 * Notifies registered listeners about the start of a {@link TestCase}.
	 * 
	 * @created 06.09.2011
	 * @param suite the suite about to be tested
	 */
	private void notifyTestcaseStart(TestCase suite) {
		for (TestListener listener : listeners) {
			listener.testcaseStarting(suite);
		}
	}

	/**
	 * Performs a test case analysis on {@link SequentialTestCase} instances
	 * having the specified names and that are included in the specified
	 * collection of {@link SequentialTestCase} instances. Each
	 * {@link SequentialTestCase} instance is run by a single {@link Session}
	 * and the expected and derived facts are compared. As the result of the
	 * comparison, a {@link TestCaseAnalysisReport} is returned.
	 * 
	 * @created 30.03.2011
	 * @param suite the specified collection of {@link SequentialTestCase}
	 *        instances
	 * @param stcNames the specified names of {@link SequentialTestCase}
	 *        instances you want to analyze
	 * @return a generated report of the differences between expected and
	 *         derived results for the cases with the specified names
	 * @throws IllegalArgumentException
	 */
	public TestCaseAnalysisReport runAndAnalyze(TestCase suite, String... stcNames) throws IllegalArgumentException {
		TestCaseAnalysisReport result = new AnalysisReport();
		for (String stcName : stcNames) {
			SequentialTestCase stc = findByName(stcName, suite);
			Diff diff = runAndAnalyze(stc, suite.getKb());
			result.add(diff);
		}
		return result;
	}

	/**
	 * The specified {@link SequentialTestCase} instance is run by a single
	 * {@link Session} and the expected and derived facts are compared. As the
	 * result of the comparison, a {@link TestCaseAnalysisReport} is returned.
	 * 
	 * @created 28.03.2011
	 * @param stc the specified {@link SequentialTestCase}
	 * @param knowledge the knowledge base used for the {@link Session} run
	 * @return the generated report of differences between expected and derived
	 *         facts
	 */
	public Diff runAndAnalyze(SequentialTestCase stc, KnowledgeBase knowledge) {
		Date creationDate = getCreationDate(stc);
		Session session = SessionFactory.createSession(stc.getName() + now().toString(), knowledge,
				creationDate);
		notifySTCStart(stc, session);
		Diff diff = new STCDiff(stc, session);


		for (RatedTestCase rtc : stc.getCases()) {

			notifyRTCStart(rtc);

			setFindings(session, rtc);
			RTCDiff rtc_diff = compareExpectations(session, rtc);

			if (rtc_diff.hasDifferences()) {
				((STCDiff) diff).add(rtc_diff);
			}
			notifyRTCFinished(rtc, rtc_diff);
		}

		notifySTCFinished(stc, session, diff);
		return diff;
	}

	/**
	 * Notifies listeners that a {@link RatedTestCase} has finished.
	 * 
	 * @created 06.09.2011
	 * @param rtc the {@link RatedTestCase}
	 * @param rtc_diff the result of the execution
	 */
	private void notifyRTCFinished(RatedTestCase rtc, RTCDiff rtc_diff) {
		for (TestListener listener : this.listeners) {
			listener.ratedTestcaseFinished(rtc, rtc_diff);
		}
	}

	/**
	 * Notifies registered Listeners that a {@link RatedTestCase} is about to
	 * start.
	 * 
	 * @created 06.09.2011
	 * @param rtc the {@link RatedTestCase} to be executed.
	 */
	private void notifyRTCStart(RatedTestCase rtc) {
		for (TestListener listener : this.listeners) {
			listener.ratedTestcaseStarting(rtc);
		}

	}

	/**
	 * Notifies registered listeners that a {@link SequentialTestCase} was
	 * finished.
	 * 
	 * @created 06.09.2011
	 * @param stc the executed {@link SequentialTestCase}
	 * @param session
	 * @param diff the result of the execution
	 */
	private void notifySTCFinished(SequentialTestCase stc, Session session, Diff diff) {
		for (TestListener listener : this.listeners) {
			listener.sequentialTestcaseFinished(stc, session, diff);
		}
	}

	/**
	 * Notifies registered listeners that a {@link SequentialTestCase} is about
	 * to start.
	 * 
	 * @created 06.09.2011
	 * @param stc the {@link SequentialTestCase}
	 * @param session the {@link Session} that is used to execute stc
	 */
	private void notifySTCStart(SequentialTestCase stc, Session session) {
		for (TestListener listener : this.listeners) {
			listener.sequentialTestcaseStarting(stc, session);
		}
	}

	/**
	 * Finds a {@link SequentialTestCase} instance in the specified suite by the
	 * specified case name.
	 * 
	 * @created 30.03.2011
	 * @param stcName the specified case name
	 * @param suite the specified suite
	 * @return the found {@link SequentialTestCase}
	 * @throws IllegalArgumentException when no {@link SequentialTestCase} with
	 *         the specified name could be found
	 */
	private SequentialTestCase findByName(String stcName, TestCase suite) throws IllegalArgumentException {
		if (stcName == null) {
			throw new IllegalArgumentException("Name of SequentialTestCase not specified.");
		}
		for (SequentialTestCase stc : suite.getRepository()) {
			if (stcName.equals(stc.getName())) {
				return stc;
			}
		}
		throw new IllegalArgumentException("No SequentialTestCase with name " + stcName
				+ " found in suite");
	}

	private void print(String string) {
		System.out.println(string);
	}

	/**
	 * Sets all findings given in the specified {@link RatedTestCase} into the
	 * specified {@link Session}. If a time stamp is given in the
	 * {@link RatedTestCase}, then it is considered. If no time stamp is given,
	 * then the currently valid system time is used.
	 * 
	 * @created 28.03.2011
	 * @param session the specified {@link Session}
	 * @param rtc the specified {@link RatedTestCase}
	 */
	private static void setFindings(Session session, RatedTestCase rtc) {
		Date timestamp = rtc.getTimeStamp();
		if (timestamp == null) {
			timestamp = now();
		}
		long time = timestamp.getTime();
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
	 * Returns the creation date of the sequential test case, i.e., the time
	 * stamp of the first rated test case. If none is available, then we take
	 * the current date.
	 * 
	 * @created 24.03.2011
	 * @param stc the specified sequential test case
	 * @return the creation date of the {@link SequentialTestCase} if available;
	 *         NOW otherwise
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
	 * Returns the creation date of the rated test case, i.e., its time stamp If
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

	/**
	 * The expected solutions/findings of a specified {@link RatedTestCase} are
	 * compared with the corresponding values currently valid in the specified
	 * {@link Session}. A {@link RTCDiff} instance is created to store these
	 * differences.
	 * 
	 * TODO: Currently, only the "expected/not derived" relations are stored,
	 * but not the "derived/but not expected" relations. We should consider this
	 * at least for solutions.
	 * 
	 * @created 28.03.2011
	 * @param session the specified {@link Session}
	 * @param rtc the specified {@link RatedTestCase}
	 * @return the differences between expected and derived facts for the
	 *         specified {@link RatedTestCase}
	 */
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
			if (!expectedRating.equals(derived)) {
				diffs.addExpectedButNotDerived(expected.getSolution(), expectedRating, derived);
			}
		}
		return diffs;
	}

	/**
	 * Convert the {@link de.d3web.empiricaltesting.Rating} known in
	 * EmpiricalTesting into a {@link Rating} known in the d3web Kernel.
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

	public void addTestListener(TestListener listener) {
		this.listeners.add(listener);
	}

	public void removeTestListener(TestListener listener) {
		this.listeners.remove(listener);
	}


}
