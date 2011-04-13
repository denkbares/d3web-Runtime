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

import java.util.List;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.caseAnalysis.RTCDiff;
import de.d3web.empiricaltesting.caseAnalysis.ValueDiff;

/**
 * Print the results of an analysis to a {@link String}.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 24.03.2011
 */
public class STCAnalysisWriter {

	public static String write(List<Diff> diffs) {
		StringBuffer buffy = new StringBuffer();
		int counter = 0;
		for (Diff stcDiff : diffs) {
			if (counter > 0) {
				buffy.append("\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ \n\n");
			}
			buffy.append(write(stcDiff));
			counter++;
		}

		return buffy.toString();
	}

	public static String write(Diff diff) {
		StringBuffer buffy = new StringBuffer();

		if (diff.hasDifferences()) {
			buffy.append("FAILURE: " + diff.getCasesWithDifference().size() +
					" diff(s) in " + diff.getCase().getCases().size() + " sequences\n\n");
		}
		else {
			buffy.append("SUCCESS.\n\n");
		}
		buffy.append("Case: " + diff.getCase().getName() + "\n");
		buffy.append("Date: " + diff.getAnalysisDate() + "\n");

		if (diff.hasDifferences()) {
			int maxSize = getMaxOffset(diff);

			for (RatedTestCase rtc : diff.getCase().getCases()) {
				if (diff.hasDiff(rtc)) {
					RTCDiff rtc_diff = diff.getDiff(rtc);
					buffy.append(write(rtc_diff, getOffset(rtc.getName(), maxSize)));
				}
			}
		}

		return buffy.toString();
	}

	private static String getOffset(String string, int maxSize) {
		String offset = "";
		int stringL = string.length();
		if (stringL < maxSize) {
			int offCounter = maxSize - stringL;
			for (int i = 0; i < offCounter; i++) {
				offset += " ";
			}
		}
		return offset;
	}

	private static int getMaxOffset(Diff diff) {
		int maxOffset = 0;
		for (RatedTestCase rtc : diff.getCasesWithDifference()) {
			int namelength = rtc.getName().length();
			if (namelength > maxOffset) {
				maxOffset = namelength;
			}
		}
		return maxOffset;
	}

	/**
	 * Writes the differences of a rated test case.
	 * 
	 * @created 24.03.2011
	 * @param rtc_diff
	 * @return
	 */
	private static String write(RTCDiff rtc_diff, String offset) {
		StringBuffer buffy = new StringBuffer();

		buffy.append("[" + offset + rtc_diff.getCase().getName() + "] ");
		int diff_counter = 0;
		for (TerminologyObject terminologyObject : rtc_diff.getDiffObjects()) {
			ValueDiff valueDiff = rtc_diff.getDiffFor(terminologyObject);
			if (diff_counter > 0) {
				buffy.append("     ");
			}
			buffy.append("[" + terminologyObject.getName() + "] exp: [" + valueDiff.expected
					+ "] but was: [" + valueDiff.derived + "]\n");
			diff_counter++;
		}

		return buffy.toString();
	}
}
