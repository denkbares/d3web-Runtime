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

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.protocol.DefaultProtocol;
import de.d3web.core.session.protocol.DefaultProtocolEntry;
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

	@Before
	public void setUp() throws Exception {
		QuestionNum questionNum = new QuestionNum("questionNum");
		NumValue numValue = new NumValue(13.8);
		numFact = FactFactory.createUserEnteredFact(questionNum, numValue);

		QuestionText questionText = new QuestionText("questionText");
		TextValue textValue = new TextValue("textValue");
		textFact = FactFactory.createUserEnteredFact(questionText, textValue);

		defaultProtocolUnderTest = new DefaultProtocol();
		defaultProtocolUnderTest.addEntry(numFact);
		defaultProtocolUnderTest.addEntry(textFact);
	}

	/**
	 * Test method for {@link de.d3web.core.session.protocol.DefaultProtocol#getProtocolHistory()}.
	 */
	@Test
	public void testGetProtocolHistory() {
		List<ProtocolEntry> history = defaultProtocolUnderTest.getProtocolHistory();
		assertThat(history.contains(new DefaultProtocolEntry(numFact)), is(true));
		assertThat(history.contains(new DefaultProtocolEntry(textFact)), is(true));
	}
}
