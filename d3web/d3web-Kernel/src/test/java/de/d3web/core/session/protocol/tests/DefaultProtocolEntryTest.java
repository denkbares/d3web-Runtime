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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.protocol.FactProtocolEntry;
import de.d3web.core.session.protocol.ProtocolConversion;
import de.d3web.core.session.values.NumValue;

/**
 * Unit test for {@link FactProtocolEntry}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 03.09.2010
 */
public class DefaultProtocolEntryTest {

	// underlying Fact-instance
	Fact protocolFact;
	KnowledgeBase knowledgeBase;
	// the DefaultProtocolEntry under testt
	FactProtocolEntry defaultProtocolEntryUnderTest;

	@Before
	public void setUp() throws Exception {
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance();
		knowledgeBase = kbm.getKnowledgeBase();
		QuestionNum questionNum = kbm.createQuestionNum("questionNum", null);
		NumValue numValue = new NumValue(1.9);
		protocolFact = FactFactory.createUserEnteredFact(questionNum, numValue);

		defaultProtocolEntryUnderTest = new FactProtocolEntry(new Date(), protocolFact);
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.protocol.FactProtocolEntry#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		assertThat(defaultProtocolEntryUnderTest.hashCode(), is(not(0)));
		// test that hastCode behaves well for equal but not identical instances
		assertThat(new FactProtocolEntry(new Date(0), "", "", new NumValue(0)).hashCode(),
				is(new FactProtocolEntry(new Date(0), "", "", new NumValue(0)).hashCode()));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.protocol.FactProtocolEntry#equals(java.lang.Object)}
	 * .
	 */
	@Test
	public void testEqualsObject() {
		// DefaultProtocolEntry(protocolFact) vs.
		// DefaultProtocolEntry(protocolFact)
		assertThat(defaultProtocolEntryUnderTest.equals(defaultProtocolEntryUnderTest), is(true));
		// DefaultProtocolEntry(protocolFact) vs. null
		assertThat(defaultProtocolEntryUnderTest.equals(null), is(false));
		// DefaultProtocolEntry(protocolFact) vs. protocolFact
		assertThat(defaultProtocolEntryUnderTest.equals(protocolFact), is(false));
		// DefaultProtocolEntry(protocolFact) vs. DefaultProtocolEntry(null)
		assertThat(
				defaultProtocolEntryUnderTest.equals(
						new FactProtocolEntry(new Date(0), "", "", new NumValue(0))),
				is(false));
		// DefaultProtocolEntry(null) vs. DefaultProtocolEntry(protocolFact)
		assertThat((new FactProtocolEntry(new Date(0), "", "", new NumValue(0))).equals(
				new FactProtocolEntry(new Date(), protocolFact)), is(false));
		// DefaultProtocolEntry(null) vs. DefaultProtocolEntry(null)
		assertThat((new FactProtocolEntry(new Date(0), "", "", new NumValue(0))).equals(
				new FactProtocolEntry(new Date(0), "", "", new NumValue(0))), is(true));

	}

	/**
	 * Test method for {@link FactProtocolEntry#getFact()}
	 */
	@Test
	public void testGetFact() {
		Fact fact = ProtocolConversion.createFact(knowledgeBase, defaultProtocolEntryUnderTest);
		assertThat(fact.getTerminologyObject(), is(equalTo(protocolFact.getTerminologyObject())));
		assertThat(fact.getPSMethod(), is(equalTo(protocolFact.getPSMethod())));
		assertThat(fact.getValue(), is(equalTo(protocolFact.getValue())));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.protocol.FactProtocolEntry#toString()}.
	 */
	@Test
	public void testToString() {
		String string = defaultProtocolEntryUnderTest.toString();
		assertThat(string, is(notNullValue()));
		assertThat(string.length(), is(not(0)));
	}

}
