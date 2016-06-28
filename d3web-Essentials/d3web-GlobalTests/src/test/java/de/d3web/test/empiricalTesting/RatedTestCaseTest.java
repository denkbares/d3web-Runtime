/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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

import java.io.IOException;
import java.util.Collections;

import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.empiricaltesting.Finding;
import de.d3web.empiricaltesting.RatedSolution;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.ScoreRating;
import de.d3web.plugin.test.InitPluginManager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 22.07.2013
 */
@SuppressWarnings("deprecation")
public class RatedTestCaseTest {

	@Test
	public void testEquals() throws IOException {

		InitPluginManager.init();
		KnowledgeBase knowledge = KnowledgeBaseUtils.createKnowledgeBase();
		String name = "solution name";
		Solution solution = new Solution(knowledge, name);

		RatedTestCase rtc1 = new RatedTestCase();
		assertFalse(rtc1.equals(null));
		assertFalse(rtc1.equals(Boolean.FALSE));
		assertTrue(rtc1.equals(rtc1));

		RatedSolution rs = new RatedSolution(solution, new ScoreRating(100));
		rtc1.addExpected(Collections.singletonList(rs));
		assertTrue(rtc1.getExpectedSolutions().contains(rs));

		rtc1.addDerived(Collections.singletonList(rs));
		assertTrue(rtc1.getDerivedSolutions().contains(rs));

		rtc1.setExpectedSolutions(Collections.singletonList(rs));
		assertTrue(rtc1.getDerivedSolutions().contains(rs));


		String qname = "question name";
		QuestionChoice q = new QuestionOC(knowledge, qname);
		Finding f = new Finding(q, qname);
		String choiceName = "c1";
		Choice c1 = new Choice(choiceName);
		q.addAlternative(c1);
		ChoiceValue value1 = new ChoiceValue(c1);
		f.setValue(value1);

		rtc1.addExpectedFindings(Collections.singletonList(f));
		assertTrue(rtc1.getExpectedFindings().contains(f));
	}
}
