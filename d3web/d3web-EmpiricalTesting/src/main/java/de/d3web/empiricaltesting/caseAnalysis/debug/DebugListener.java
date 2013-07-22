/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.empiricaltesting.caseAnalysis.debug;

import de.d3web.core.session.Session;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.TestListener;
import de.d3web.empiricaltesting.caseAnalysis.RTCDiff;
import de.d3web.empiricaltesting.caseAnalysis.functions.Diff;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysisReport;


/**
 * A Testlistener that prints information about executed tests on System.out.
 * 
 * @author Reinhard Hatko
 * @created 06.09.2011
 */
public class DebugListener implements TestListener {

	@Override
	public void testcaseStarting(TestCase tc) {
		System.out.println("Starting Testcase '" + tc.getName() + "'.");
	}

	@Override
	public void sequentialTestcaseStarting(SequentialTestCase stc, Session session) {
		System.out.print("  Starting SequentialTestcase: " + stc.getName() + " ["
				+ session.getCreationDate()
				+ "]");
	}

	@Override
	public void ratedTestcaseStarting(RatedTestCase rtc) {
		System.out.println("    Starting RatedTestcase: " + rtc.getName());
	}

	@Override
	public void ratedTestcaseFinished(RatedTestCase rtc, RTCDiff rtc_diff) {
		// String result = rtc_diff.hasDifferences() ? " failed." : " passed.";
		// System.out.println("    Finishing RatedTestcase: " + rtc.getName() +
		// " - " + result);

	}

	@Override
	public void sequentialTestcaseFinished(SequentialTestCase stc, Session session, Diff diff) {
		// String result = diff.hasDifferences() ? " failed." : " passed.";
		// System.out.println("  Finishing SequentialTestcase: " + stc.getName()
		// + " - " + result);
	}

	@Override
	public void testcaseFinished(TestCase tc, TestCaseAnalysisReport report) {
		String result = report.hasDiff() ? " failed." : " passed.";
		System.out.println("Finishing Testcase: " + tc.getName() + " - " + result);
	}

}
