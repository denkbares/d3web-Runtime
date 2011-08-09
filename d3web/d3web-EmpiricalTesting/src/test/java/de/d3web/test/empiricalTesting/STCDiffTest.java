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
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.NumValue;
import de.d3web.empiricaltesting.Finding;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.caseAnalysis.RTCDiff;
import de.d3web.empiricaltesting.caseAnalysis.STCDiff;

/**
 * This class tests {@link STCDiff}.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 21.04.2011
 */
public class STCDiffTest {

	private KnowledgeBase knowledge;
	private QuestionNum question;
	private Value q_expected, q_derived;
	private RTCDiff rtcDiff;
	private RatedTestCase rtc;

	@Before
	public void setUp() {
		knowledge = KnowledgeBaseUtils.createKnowledgeBase();
		question = new QuestionNum(knowledge, "questionnum_name");
		q_expected = new NumValue(1.0);
		q_derived = new NumValue(2.0);

		rtc = new RatedTestCase();
		rtc.add(new Finding(question, q_expected));

		rtcDiff = new RTCDiff(rtc);
		rtcDiff.addExpectedButNotDerived(question, q_expected, q_derived);
	}

	@Test
	public void testConstruction() {
		SequentialTestCase stc = new SequentialTestCase();
		Session session = SessionFactory.createSession(knowledge);
		STCDiff diff = new STCDiff(stc, session);

		assertFalse(diff.hasDifferences());
		diff.add(rtcDiff);
		assertTrue(diff.hasDifferences());

		Date date = diff.getAnalysisDate();
		assertNotNull(date);

		RTCDiff rtcDiffi = diff.getDiff(rtc);
		assertEquals(rtcDiff, rtcDiffi);

		Collection<RatedTestCase> casesWithDifference = diff.getCasesWithDifference();
		assertTrue(casesWithDifference.contains(rtc));

		assertTrue(diff.hasDiff(rtc));

		assertTrue(diff.hasDiff(null));

		assertEquals(stc, diff.getCase());

	}

}
