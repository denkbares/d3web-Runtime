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

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.empiricaltesting.SequentialTestCase;

/**
 * An interface to map results from a test case analysis (empirical test runs).
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 25.03.2011
 */
public interface TestCaseAnalysisReport {

	/**
	 * Gives the precision of the analysis result.
	 * 
	 * @created 25.03.2011
	 * @return the precision of the analysis run.
	 */
	double precision();

	/**
	 * Gives the recall of the analysis result.
	 * 
	 * @created 25.03.2011
	 * @return the recall of the analysis run.
	 */
	double recall();

	/**
	 * Retrieves the differences computed during the test case analysis for the
	 * specified {@link SequentialTestCase}.
	 * 
	 * @created 25.03.2011
	 * @param stc the specified {@link SequentialTestCase}
	 * @return the computed differences for the specified case.
	 */
	Diff getDiffFor(SequentialTestCase stc);

	/**
	 * Checks whether the specified {@link SequentialTestCase} has reported some
	 * differences computed during the test case analysis.
	 * 
	 * @created 25.03.2011
	 * @param testCase the specified {@link SequentialTestCase}
	 * @return true, when there are differences; false otherwise.
	 */
	boolean hasDiff(SequentialTestCase testCase);

	/**
	 * Checks whether the entire result contains any differences, that were
	 * reported during the test case analysis.
	 * 
	 * @created 25.03.2011
	 * @return true, when there are differences; false otherwise.
	 */
	boolean hasDiff();

	/**
	 * Gives the recall of the interview agenda expected by the test suite and
	 * derived by the test case analysis run. A specified knowledge base is
	 * used.
	 * 
	 * @created 28.03.2011
	 * @param kb the specified {@link KnowledgeBase}
	 * @return the recall of the expected and derived interview agends
	 */
	double interviewRecall(KnowledgeBase kb);

	/**
	 * Gives the precision of the interview agenda expected by the test suite
	 * and derived by the test case analysis run. A specified knowledge base is
	 * used.
	 * 
	 * @created 28.03.2011
	 * @param kb the specified {@link KnowledgeBase}
	 * @return the precision of the expected and derived interview agends
	 */
	double interviewPrecision(KnowledgeBase kb);

	/**
	 * Appends a new difference report to the result instance. Pleas be aware,
	 * that for ONE specific {@link SequentialTestCase} only a single difference
	 * report can be stored.
	 * 
	 * @created 28.03.2011
	 * @param diff the new difference report
	 */
	void add(Diff diff);

}
