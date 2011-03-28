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
import java.util.HashMap;
import java.util.Map;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.empiricaltesting.SequentialTestCase;

/**
 * A container for all results of a test case analysis run.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 25.03.2011
 */
public class AnalysisReport implements TestCaseAnalysisReport {

	private static EmpiricalTestingFunctions functions = EmpiricalTestingFunctions.getInstance();
	private Map<SequentialTestCase, Diff> diffs = new HashMap<SequentialTestCase, Diff>();

	@Override
	public void add(Diff diff) {
		this.diffs.put(diff.getCase(), diff);
	}

	@Override
	public Diff getDiffFor(SequentialTestCase stc) {
		return diffs.get(stc);
	}

	@Override
	public boolean hasDiff(SequentialTestCase stc) {
		return getDiffFor(stc) != null;
	}

	@Override
	public boolean hasDiff() {
		for (Diff diff : getDiffs()) {
			if (diff.hasDifferences()) return true;
		}
		return false;
	}

	@Override
	public double precision() {
		double prec = 0;
		for (Diff diff : getDiffs()) {
			prec += functions.precision(diff, DerivationsCalculator.getInstance());
		}
		prec /= getDiffs().size();
		return prec;
	}

	@Override
	public double recall() {
		double rec = 0;
		for (Diff diff : getDiffs()) {
			rec += functions.recall(diff, DerivationsCalculator.getInstance());
		}
		rec /= getDiffs().size();
		return rec;
	}

	@Override
	public double interviewPrecision(KnowledgeBase kb) {
		double prec = 0;
		for (Diff diff : getDiffs()) {
			prec += functions.precision(diff, new InterviewCalculator(kb));
		}
		prec /= getDiffs().size();
		return prec;
	}

	@Override
	public double interviewRecall(KnowledgeBase kb) {
		double rec = 0;
		for (Diff diff : getDiffs()) {
			rec += functions.recall(diff, new InterviewCalculator(kb));
		}
		rec /= getDiffs().size();
		return rec;
	}

	private Collection<Diff> getDiffs() {
		return diffs.values();
	}
}
