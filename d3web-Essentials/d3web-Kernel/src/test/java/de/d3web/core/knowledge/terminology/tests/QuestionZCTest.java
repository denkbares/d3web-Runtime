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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
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
	 * 
	 * @created 24.08.2010
	 * @throws java.lang.Exception
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
	 * @see QuestionZC#getAllAlternatives()
	 * 
	 * @created 24.08.2010
	 */
	@Test
	public void testGetAllAlternativesIsEmpty() {
		List<Choice> alternatives = questionZC.getAllAlternatives();
		assertThat(alternatives.size(), is(0));
	}

	/**
	 * Summary: Assure that setAlternatives(List<Choice>) logs an error message
	 * when the given list is not empty
	 * 
	 * @see QuestionZC#setAlternatives(List)
	 * 
	 * @created 24.08.2010
	 */
	@Test
	public void testSetAlternativesLogsErrormessage() {

		// create a empry list of choices
		List<Choice> choicesList = new ArrayList<>();
		questionZC.setAlternatives(choicesList);// this should NOT log a msg
		// flush the StreamHandler and retrieve the log Message
		handler.flush();
		String logMsg = out.toString();

		assertThat(logMsg, is(notNullValue()));
		assertThat(logMsg, is(""));

		// now fill the list with a "choice"
		Choice choice = new Choice("choice");
		choicesList.add(choice);
		questionZC.setAlternatives(choicesList);// this should produce log msg
		// flush the StreamHandler and retrieve the log Message
		handler.flush();
		logMsg = out.toString();

		assertThat(logMsg, is(notNullValue()));
		assertThat(logMsg.contains("Tried to set AnswerAlternatives"), is(true));
	}

	/**
	 * Summary: Assure that addAlternative(Choice) logs an error message
	 * 
	 * @see QuestionZC#addAlternative(Choice)
	 * 
	 * @created 24.08.2010
	 */
	@Test
	public void testAddAlternativeLogsErrormessage() {
		Choice choice = new Choice("choice");
		questionZC.addAlternative(choice);// this should produce log msg
		// flush the StreamHandler and retrieve the log Message
		handler.flush();
		String logMsg = out.toString();

		assertThat(logMsg, is(notNullValue()));
		assertThat(logMsg.contains("Tried to add AnswerAlternative"), is(true));
	}
}
