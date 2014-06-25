/*
 * Copyright (C) 2013 denkbares GmbH
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.empiricaltesting.Finding;
import de.d3web.empiricaltesting.RegexFinding;
import de.d3web.plugin.test.InitPluginManager;

import static org.junit.Assert.*;

/**
 * @author jochenreutelshofer
 * @created 22.07.2013
 */
public class FindingTest {

	@Test
	public void testFinding() throws IOException {
		InitPluginManager.init();
		KnowledgeBase knowledge = KnowledgeBaseUtils.createKnowledgeBase();
		String name = "question name";
		QuestionChoice q = new QuestionOC(knowledge, name);
		Finding f = new Finding(q, name);
		String choiceName = "c1";
		Choice c1 = new Choice(choiceName);
		q.addAlternative(c1);
		ChoiceValue value1 = new ChoiceValue(c1);
		f.setValue(value1);

		assertEquals(f.getQuestionPrompt(), name);
		assertEquals(f.getValuePrompt(), choiceName);

		String name2 = "question name2";
		QuestionChoice q2 = new QuestionMC(knowledge, name2);
		Finding f2 = new Finding(q2, name2);
		String choiceName2 = "c2";
		Choice c2 = new Choice(choiceName2);
		q2.addAlternative(c2);
		List<ChoiceID> choices = new ArrayList<ChoiceID>();
		choices.add(new ChoiceID(c2));
		f2.setValue(new MultipleChoiceValue(choices));
		assertEquals(f2.getValuePrompt(), choiceName2);

		assertTrue(f.equals(f));
		assertFalse(f.equals(f2));
		assertFalse(f.equals(null));
		assertFalse(f.equals(Boolean.FALSE));
		assertTrue(f.compareTo(f2) < 0);

		Finding f3 = new Finding(q, name);
		f3.setValue(value1);
		assertTrue(f.equals(f3));
		assertTrue(f.compareTo(f3) == 0);
	}

	@Test
	public void testRegexFinding() throws IOException {
		InitPluginManager.init();
		KnowledgeBase knowledge = KnowledgeBaseUtils.createKnowledgeBase();
		String name = "question name";
		QuestionDate q = new QuestionDate(knowledge, name);
		RegexFinding f = new RegexFinding(q, name);
		String regex = "2014-05-02 12:00:00";
		f.setRegex(regex);

		assertEquals(f.toString(), name + " = " + regex);
		assertEquals(f.getQuestionPrompt(), name);
		assertEquals(f.getRegex(), regex);

		String name2 = "question name2";
		QuestionText q2 = new QuestionText(knowledge, name2);
		RegexFinding f2 = new RegexFinding(q2, name2);
		String regex2 = "^hello$";
		f2.setRegex(regex2);
		assertEquals(f2.getRegex(), regex2);

		assertTrue(f.equals(f));
		assertFalse(f.equals(f2));
		assertFalse(f.equals(null));
		assertFalse(f.equals(Boolean.FALSE));
		assertTrue(f.compareTo(f2) < 0);

		RegexFinding f3 = new RegexFinding(q, name);
		f3.setRegex(regex);
		assertTrue(f.equals(f3));
		assertTrue(f.compareTo(f3) == 0);

		Set<RegexFinding> hashTest = new HashSet<RegexFinding>();
		hashTest.add(f);
		assertTrue(hashTest.contains(f));
		assertFalse(hashTest.contains(f2));

	}
}
