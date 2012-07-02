/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.caseupdate;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.empiricaltesting.RatedSolution;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.StateRating;
import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.TestPersistence;
import de.d3web.empiricaltesting.caseAnalysis.RTCDiff;
import de.d3web.empiricaltesting.caseAnalysis.ValueDiff;
import de.d3web.empiricaltesting.caseAnalysis.functions.Diff;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysisReport;

/**
 * 
 * @author Sebastian Furth
 * @created Nov 23, 2011
 */
public class CaseUpdater {

	/**
	 * Resolves test case failures due to ValueDiffs regarding solutions like:
	 * 
	 * exp: UNCLEAR but was: EXCLUDED
	 * 
	 * by adding new expected solutions with the rating "excluded".
	 * 
	 * @param t
	 * @created Nov 23, 2011
	 * @param result
	 * @param outputFolder path to the output file
	 * @throws MalformedURLException
	 */
	public static void updateExpectedSolutions(TestCase t, TestCaseAnalysisReport result, String output) throws MalformedURLException {

		// go through all STCs...
		for (SequentialTestCase stc : t.getRepository()) {

			// if there are diffs...
			Diff stcDiff = result.getDiffFor(stc);
			if (stcDiff != null) {

				// go through all RTCs...
				for (RatedTestCase rtc : stc.getCases()) {
					RTCDiff rtcDiff = stcDiff.getDiff(rtc);

					// if there are diffs...
					if (rtcDiff != null) {

						for (TerminologyObject to : rtcDiff.getDiffObjects()) {

							// we only want to update solutions...
							if (to instanceof Solution) {

								// check that it is really a diff of the form:
								// UNCLEAR -> EXCLUDED
								ValueDiff valueDiff = rtcDiff.getDiffFor(to);
								if (valueDiff.getDerived().equals(new Rating(Rating.State.EXCLUDED))
										&& valueDiff.getExpected().equals(new Rating(
												Rating.State.UNCLEAR))) {

									// add new rated solution
									rtc.addExpected(new RatedSolution((Solution) to,
											new StateRating((Rating) valueDiff.getDerived())));
								}
							}
						}
					}
				}
			}

		}

		// write case
		URL convertedpath = new File(output).toURI().toURL();
		TestPersistence.getInstance().writeCases(convertedpath, t, false);
	}

}
