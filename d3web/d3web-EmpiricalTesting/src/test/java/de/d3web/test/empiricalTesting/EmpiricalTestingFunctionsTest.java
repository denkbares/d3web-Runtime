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
import static junit.framework.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.values.NumValue;
import de.d3web.empiricaltesting.Finding;
import de.d3web.empiricaltesting.RatedSolution;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.StateRating;
import de.d3web.empiricaltesting.caseAnalysis.RTCDiff;
import de.d3web.empiricaltesting.caseAnalysis.STCDiff;
import de.d3web.empiricaltesting.caseAnalysis.functions.DerivationsCalculator;
import de.d3web.empiricaltesting.caseAnalysis.functions.EmpiricalTestingFunctions;
import de.d3web.plugin.test.InitPluginManager;

/**
 * This class checks the correct computation of the precision/recall of the
 * derived solutions.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 02.05.2011
 */
public class EmpiricalTestingFunctionsTest {

	private static final double EPS = 0.001;
	private SequentialTestCase stc1, stc2;
	private RatedTestCase rtc11, rtc12, rtc21, rtc22;
	private QuestionNum q1, q2;
	private Solution s1, s2;
	private KnowledgeBase knowledge;
	private RatedSolution rs1_e, rs1_s, rs2_e;

	private static final Rating ESTABLISHED = new Rating(State.ESTABLISHED);
	private static final Rating SUGGESTED = new Rating(State.SUGGESTED);
	private Date date;

	@Before
	public void setUp() {
		try {
			InitPluginManager.init();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		knowledge = KnowledgeBaseUtils.createKnowledgeBase();

		q1 = new QuestionNum(knowledge, "q1");
		q2 = new QuestionNum(knowledge, "q2");
		s1 = new Solution(knowledge, "s1");
		s2 = new Solution(knowledge, "s2");

		rs1_e = new RatedSolution(s1, new StateRating(ESTABLISHED));
		rs1_s = new RatedSolution(s1, new StateRating(SUGGESTED));
		rs2_e = new RatedSolution(s2, new StateRating(ESTABLISHED));

		// CREATE SEQUENTIAL TEST CASE "STC1"
		// stc1 is a fully compiliant test case
		// rtc11: q1=0, s1=estab [exp: s1=estab]
		// rtc12: q2=1, s1=estab, s2=estab [exp: s1=estab, s2=estab]
		stc1 = new SequentialTestCase();
		stc1.setName("STC1");
		rtc11 = new RatedTestCase();
		date = new Date();
		rtc11.setTimeStamp(date);
		rtc11.setName("rtc11");
		rtc11.add(new Finding(q1, new NumValue(0.0)));
		rtc11.addDerived(rs1_e);
		rtc11.addExpected(rs1_e);
		stc1.add(rtc11);

		rtc12 = new RatedTestCase();
		rtc12.setName("rtc12");
		// also use addFindings() method for coverage
		Finding finding = new Finding(q2, new NumValue(1.0));
		List<Finding> findings = new ArrayList<Finding>();
		findings.add(finding);
		rtc12.addFindings(findings);
		rtc12.addDerived(rs1_e, rs2_e);
		rtc12.addExpected(rs1_e, rs2_e);
		stc1.add(rtc12);

		// CREATE SEQUENTIAL TEST CASE "STC2"
		// stc2 is a test case with diffs
		// rtc21: q1=0, s1=estab [exp: s1=suggested]
		// rtc22: q2=1, s1=estab, s2=estab [exp: s1=suggested, s2=estab]
		stc2 = new SequentialTestCase();
		stc2.setName("STC2");
		rtc21 = new RatedTestCase();
		rtc21.setName("rtc21");
		rtc21.add(new Finding(q1, new NumValue(0.0)));
		rtc21.addDerived(rs1_e);
		rtc21.addExpected(rs1_s);
		stc2.add(rtc21);

		rtc22 = new RatedTestCase();
		rtc12.setName("rtc22");
		rtc22.add(new Finding(q2, new NumValue(1.0)));
		rtc22.addDerived(rs1_e, rs2_e);
		rtc22.addExpected(rs1_s, rs2_e);
		stc2.add(rtc22);

	}

	@Test
	public void testCreation() {
		// check the creation of the two sequential test cases
		assertNotNull(stc1);
		assertEquals(2, stc1.getCases().size());
		assertNotNull(stc2);
		assertEquals(2, stc2.getCases().size());
		assertEquals(rtc11.getTimeStamp(), date);
	}

	@Test
	public void testPrecisionRecall() {
		// The good case "stc1"

		Session session = SessionFactory.createSession(knowledge);

		STCDiff stc1_diff = new STCDiff(stc1, session);
		assertEquals(1, EmpiricalTestingFunctions.getInstance().precision(stc1_diff,
						DerivationsCalculator.getInstance()), EPS);
		assertEquals(1, EmpiricalTestingFunctions.getInstance().recall(stc1_diff,
				DerivationsCalculator.getInstance()), EPS);
		assertEquals(1, EmpiricalTestingFunctions.getInstance().fMeasure(1.0, stc1_diff,
				DerivationsCalculator.getInstance()), EPS);

		// The differing case "stc2": having prec=0,25
		STCDiff stc2_diff = new STCDiff(stc2, session);
		RTCDiff rtc_diff21 = new RTCDiff(rtc21);
		rtc_diff21.addExpectedButNotDerived(s1, ESTABLISHED, SUGGESTED);
		stc2_diff.add(rtc_diff21);

		RTCDiff rtc_diff22 = new RTCDiff(rtc22);
		rtc_diff22.addExpectedButNotDerived(s1, ESTABLISHED, SUGGESTED);
		stc2_diff.add(rtc_diff22);

		assertEquals(
				0.25,
				EmpiricalTestingFunctions.getInstance().precision(stc2_diff,
						DerivationsCalculator.getInstance()), EPS);
		assertEquals(0.25, EmpiricalTestingFunctions.getInstance().recall(stc2_diff,
				DerivationsCalculator.getInstance()), EPS);
		assertEquals(0.25, EmpiricalTestingFunctions.getInstance().fMeasure(1, stc2_diff,
				DerivationsCalculator.getInstance()), EPS);
	}

}
