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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.empiricaltesting.Finding;
import de.d3web.plugin.test.InitPluginManager;

/**
 * 
 * @author jochenreutelshofer
 * @created 22.07.2013
 */
public class FindingTest {

	@Test
	public void testFinding() {
		try {
			InitPluginManager.init();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
}
