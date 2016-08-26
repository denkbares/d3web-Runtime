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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionZC;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link QuestionZC}
 *
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 24.08.2010
 */
public class QuestionZCTest {

	QuestionZC questionZC;

	Logger logger;
	Formatter formatter;
	ByteArrayOutputStream out;
	Handler handler;

	/**
	 * @throws java.lang.Exception
	 * @created 24.08.2010
	 */
	@Before
	public void setUp() throws Exception {
		logger = Logger.getLogger(QuestionZC.class.getName());
		logger.setUseParentHandlers(false);

		formatter = new SimpleFormatter();
		out = new ByteArrayOutputStream();
		handler = new StreamHandler(out, formatter);

		logger.addHandler(handler);
		questionZC = new QuestionZC(new KnowledgeBase(), "questionZC");
	}

	/**
	 * Summary: Assure that getAllAlternatives() returns an empty list
	 *
	 * @created 24.08.2010
	 * @see QuestionZC#getAllAlternatives()
	 */
	@Test
	public void testGetAllAlternativesIsEmpty() {
		List<Choice> alternatives = questionZC.getAllAlternatives();
		assertThat(alternatives.size(), is(0));
	}

	@Test()
	public void testSetEmptyAlternatives() {
		// create a empty list of choices
		List<Choice> choicesList = new ArrayList<>();
		questionZC.setAlternatives(choicesList);// this should NOT log a msg
		assertTrue(questionZC.getAllAlternatives().isEmpty());
	}

	/**
	 * Summary: Assure that setAlternatives(List<Choice>) throws an exception
	 * when the given list is not empty
	 *
	 * @created 24.08.2010
	 * @see QuestionZC#setAlternatives(List)
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testSetAlternativesThrowsException() {
		List<Choice> choicesList = Collections.singletonList(new Choice("choice"));
		questionZC.setAlternatives(choicesList);// this should produce an exception
	}

	/**
	 * Summary: Assure that addAlternative(Choice) throws an exception
	 *
	 * @created 24.08.2010
	 * @see QuestionZC#addAlternative(Choice)
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testAddAlternativeThrowsException() {
		Choice choice = new Choice("choice");
		questionZC.addAlternative(choice);// this should produce log msg
	}
}
