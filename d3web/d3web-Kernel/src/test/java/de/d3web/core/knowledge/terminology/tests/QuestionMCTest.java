/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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

package de.d3web.core.knowledge.terminology.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.MultipleChoiceValue;

/**
 * Unit test for {@link QuestionMC}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 24.08.2010
 */
public class QuestionMCTest {

	QuestionMC questionMC, qMC2;
	KnowledgeBase knowledge;

	@Before
	public void setUp() throws Exception {
		knowledge = KnowledgeBaseUtils.createKnowledgeBase();

		questionMC = new QuestionMC(knowledge, "questionMCtext");
	}

	@Test
	public void testMultipleChoiceValue() {
		qMC2 = new QuestionMC(knowledge, "qMC2");
		Choice c1 = new Choice("c1");
		Choice c2 = new Choice("c2");
		Choice c3 = new Choice("c3");
		qMC2.addAlternative(c1);
		qMC2.addAlternative(c2);
		qMC2.addAlternative(c3);

		List<String> valueNames = new ArrayList<String>(2);
		valueNames.add("c2");
		valueNames.add("c3");

		// find an existing combination of values
		MultipleChoiceValue expected = new MultipleChoiceValue(new ChoiceID(c2), new ChoiceID(c3));
		MultipleChoiceValue value = KnowledgeBaseUtils.findMultipleChoiceValue(qMC2, valueNames);
		assertEquals(expected, value);
	}

	/**
	 * Tests the getAlternatives() method of QuestionMC
	 * 
	 * @see QuestionMC#getAllAlternatives()
	 * 
	 * @created 24.08.2010
	 */
	@Test
	public void testGetAlternatives() {
		// alternatives is null ==> empty list is returned:
		assertThat(questionMC.getAllAlternatives().size(), is(0));
		// now add a new answer alternative
		Choice choice = new Choice("choice");
		questionMC.addAlternative(choice);
		// and assure that getAlternatives() is 1
		assertThat(questionMC.getAllAlternatives().size(), is(1));
	}

	@Test
	public void testToString() {
		// Test the toString() method
		String string = questionMC.toString();
		assertThat(string, notNullValue());
		assertThat(string.length(), is(not(0)));
	}
}
