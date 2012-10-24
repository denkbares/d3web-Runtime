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

package de.d3web.core.session.protocol.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.protocol.DefaultProtocol;
import de.d3web.core.session.protocol.FactProtocolEntry;
import de.d3web.core.session.protocol.ProtocolEntry;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;

/**
 * Unit test for {@link DefaultProtocol}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 03.09.2010
 */
public class DefaultProtocolTest {

	Fact numFact;
	Fact textFact;

	DefaultProtocol defaultProtocolUnderTest;
	Date now;

	@Before
	public void setUp() throws Exception {
		KnowledgeBase kb = new KnowledgeBase();
		QuestionNum questionNum = new QuestionNum(kb, "questionNum");
		NumValue numValue = new NumValue(13.8);
		numFact = FactFactory.createUserEnteredFact(questionNum, numValue);
		now = new Date();

		QuestionText questionText = new QuestionText(kb, "questionText");
		TextValue textValue = new TextValue("textValue");
		textFact = FactFactory.createUserEnteredFact(questionText, textValue);

		defaultProtocolUnderTest = new DefaultProtocol();
		defaultProtocolUnderTest.addEntry(new FactProtocolEntry(now, numFact));
		defaultProtocolUnderTest.addEntry(new FactProtocolEntry(now, textFact));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.protocol.DefaultProtocol#getProtocolHistory()}
	 * .
	 */
	@Test
	public void testGetProtocolHistory() {
		List<ProtocolEntry> history = defaultProtocolUnderTest.getProtocolHistory();
		assertThat(history.contains(new FactProtocolEntry(now, numFact)), is(true));
		assertThat(history.contains(new FactProtocolEntry(now, textFact)), is(true));
	}

	@Test
	public void testDeletion() {
		assertThat(defaultProtocolUnderTest.getProtocolHistory().size(), is(2));
		FactProtocolEntry entry = new FactProtocolEntry(now, textFact);
		assertThat(defaultProtocolUnderTest.getProtocolHistory().contains(entry), is(true));
		assertThat(defaultProtocolUnderTest.removeEntry(entry), is(true));
		assertThat(defaultProtocolUnderTest.getProtocolHistory().size(), is(1));
		assertThat(defaultProtocolUnderTest.getProtocolHistory().contains(entry), is(false));
	}
}
