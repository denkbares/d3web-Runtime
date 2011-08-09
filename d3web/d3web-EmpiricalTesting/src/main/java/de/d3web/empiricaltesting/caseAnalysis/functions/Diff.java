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

import java.util.Collection;
import java.util.Date;

import de.d3web.core.session.Session;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.caseAnalysis.RTCDiff;

/**
 * Captures the differences of a {@link SequentialTestCase} instance observed
 * during a test case analysis run.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 25.03.2011
 */
public interface Diff {

	/**
	 * Gives the observed {@link SequentialTestCase}.
	 * 
	 * @created 25.03.2011
	 * @return the considered {@link SequentialTestCase}
	 */
	SequentialTestCase getCase();

	/**
	 * Reports, if there were any differences observed.
	 * 
	 * @created 25.03.2011
	 * @return true, when there exists at least one difference.
	 */
	boolean hasDifferences();

	/**
	 * Checks, whether the specified {@link RatedTestCase} (to be included in
	 * the considered {@link SequentialTestCase}) has reported differences.
	 * 
	 * @created 25.03.2011
	 * @param rtc the specified {@link RatedTestCase}
	 * @return true, when the specified {@link RatedTestCase} reported
	 *         differences; false otherwise.
	 */
	boolean hasDiff(RatedTestCase rtc);

	/**
	 * Gives the exact differences of the specified {@link RatedTestCase}
	 * instance.
	 * 
	 * @created 25.03.2011
	 * @param rtc the specified {@link RatedTestCase} instance
	 * @return the exact differences
	 */
	RTCDiff getDiff(RatedTestCase rtc);

	/**
	 * Gives the time stamp of the performed analysis run.
	 * 
	 * @created 25.03.2011
	 * @return time stamp of the analysis run.
	 */
	Date getAnalysisDate();

	/**
	 * Gives all {@link RatedTestCase} instances included in the considered
	 * {@link SequentialTestCase}, that have reported differences.
	 * 
	 * @created 25.03.2011
	 * @return all {@link RatedTestCase} instances with reported differences.
	 */
	Collection<RatedTestCase> getCasesWithDifference();

	/**
	 * Returns the session that was used to executed the {@link SequentialTestCase}
	 * 
	 * @created 09.08.2011
	 * @return the {@link Session} that was used to execute the
	 *         {@link SequentialTestCase}.
	 */
	Session getSession();
}
