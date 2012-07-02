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
package de.d3web.test.empiricalTesting;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.NumValue;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.caseAnalysis.RTCDiff;
import de.d3web.empiricaltesting.caseAnalysis.ValueDiff;

/**
 * This class tests the methods of {@link RTCDiff}.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 21.04.2011
 */
public class RTCDiffTest {

	private Solution solution;
	private KnowledgeBase knowledge;
	private QuestionNum question;
	private Value q_expected, q_derived;
	private Rating s_expected, s_derived;

	@Before
	public void setUp() {
		knowledge = KnowledgeBaseUtils.createKnowledgeBase();

		solution = new Solution(knowledge, "solution_name");
		question = new QuestionNum(knowledge, "questionnum_name");
		q_expected = new NumValue(1.0);
		q_derived = new NumValue(2.0);

		s_expected = new Rating(State.ESTABLISHED);
		s_derived = new Rating(State.SUGGESTED);
	}

	@Test
	public void testConstruction() {
		RatedTestCase rtc = new RatedTestCase();
		rtc.setName("rtc_name");
		RTCDiff diff = new RTCDiff(rtc);
		assertFalse(diff.hasDifferences());

		diff.addExpectedButNotDerived(question, q_expected, q_derived);
		diff.addExpectedButNotDerived(solution, s_expected, s_derived);
		assertTrue(diff.hasDifferences());

		assertEquals(rtc, diff.getCase());

		Collection<TerminologyObject> diffObjects = diff.getDiffObjects();
		assertTrue(diffObjects.contains(solution));
		assertTrue(diffObjects.contains(question));

		ValueDiff valueDiff = diff.getDiffFor(question);
		assertEquals(q_derived, valueDiff.getDerived());
		assertEquals(q_expected, valueDiff.getExpected());

		Collection<TerminologyObject> correctlyDerived = diff.correctlyDerived();
		assertTrue(correctlyDerived.isEmpty());

		Collection<TerminologyObject> derived = diff.getDerived();
		assertTrue(derived.contains(solution));
		assertTrue(derived.contains(question));

		Collection<TerminologyObject> expected = diff.getExpected();
		assertTrue(expected.isEmpty());

		assertTrue(diff.hasDiff(question));

	}

}
