/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.persistence.tests;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.core.io.fragments.QuestionHandler;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.plugin.test.InitPluginManager;

/**
 * @author merz
 * 
 *         To change this generated comment edit the template variable
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class QuestionChoiceTest extends TestCase {

	private QuestionOC q1;
	private QuestionHandler qw;
	private XMLTag answersTag;

	private XMLTag isTag;
	private XMLTag shouldTag;

	/**
	 * Constructor for QuestionOCTest.
	 * 
	 * @param arg0
	 */
	public QuestionChoiceTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(QuestionChoiceTest.suite());
	}

	public static Test suite() {
		return new TestSuite(QuestionChoiceTest.class);
	}

	protected void setUp() {
		try {
			InitPluginManager.init();
		}
		catch (IOException e1) {
			assertTrue("Error initialising plugin framework", false);
		}
		q1 = new QuestionOC("q1");
		q1.setName("q1-text");

		qw = new QuestionHandler();

		shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("ID", "q1");
		shouldTag.addAttribute("type", "OC");
		XMLTag child = new XMLTag("Text");
		child.setContent("q1-text");
		shouldTag.addChild(child);
	}

	public void testQuestionOCSimple() throws Exception {
		answersTag = new XMLTag("Answers");
		shouldTag.addChild(answersTag);
		isTag = new XMLTag(qw.write(q1, Util.createEmptyDocument()));
		assertEquals("(0)", shouldTag, isTag);
	}

	public void testQuestionOCWithAnswers() throws Exception {
		answersTag = new XMLTag("Answers");
		shouldTag.addChild(answersTag);

		List<Choice> alternatives = new LinkedList<Choice>();

		Choice a1 = new Choice("q1a1");
		a1.setText("q1a1-&text");
		alternatives.add(a1);

		Choice a2 = new Choice("q1a2");
		a2.setText("q1a2-testtext");
		alternatives.add(a2);

		q1.setAlternatives(alternatives);

		XMLTag answerTag1 = new XMLTag("Answer");
		answerTag1.addAttribute("ID", "q1a1");
		answerTag1.addAttribute("type", "AnswerChoice");
		XMLTag answerTextTag1 = new XMLTag("Text");
		String answerText1 = "q1a1-&text";

		answerTextTag1.setContent(answerText1);
		answerTag1.addChild(answerTextTag1);
		answersTag.addChild(answerTag1);

		XMLTag answerTag2 = new XMLTag("Answer");
		answerTag2.addAttribute("ID", "q1a2");
		answerTag2.addAttribute("type", "AnswerChoice");
		XMLTag answerTextTag2 = new XMLTag("Text");
		answerTextTag2.setContent("q1a2-testtext");
		answerTag2.addChild(answerTextTag2);
		answersTag.addChild(answerTag2);

		isTag = new XMLTag(qw.write(q1, Util.createEmptyDocument()));

		assertEquals("(1)", shouldTag, isTag);

		// replace special chars with XML entities:
		// answerText = answerText.replaceAll("&", "&amp;");
		// answerText = answerText.replaceAll("<", "&lt;");
		// answerText = answerText.replaceAll(">", "&gt;");
	}

	public void testQuestionWithProperties() throws Exception {
		q1.getProperties().setProperty(Property.HIDE_IN_DIALOG, new Boolean(true));
		q1.getProperties().setProperty(Property.COST, new Double(20));

		// Set propertyKeys = q1.getPropertyKeys();
		// MockPropertyDescriptor mpd = new
		// MockPropertyDescriptor(q1,propertyKeys);

		XMLTag propertiesTag = new XMLTag("Properties");

		XMLTag propertyTag1 = new XMLTag("Property");
		propertyTag1.addAttribute("name", "hide_in_dialog");
		// old: propertyTag1.addAttribute("descriptor", "hide_in_dialog");
		propertyTag1.addAttribute("class", "java.lang.Boolean");
		propertyTag1.setContent("true");

		XMLTag propertyTag2 = new XMLTag("Property");
		propertyTag2.addAttribute("name", "cost");
		// old: propertyTag2.addAttribute("descriptor", "cost");
		propertyTag2.addAttribute("class", "java.lang.Double");
		propertyTag2.setContent("20.0");

		propertiesTag.addChild(propertyTag1);
		propertiesTag.addChild(propertyTag2);

		shouldTag.addChild(propertiesTag);

		answersTag = new XMLTag("Answers");
		shouldTag.addChild(answersTag);

		isTag = new XMLTag(qw.write(q1, Util.createEmptyDocument()));

		assertEquals("(3)", shouldTag, isTag);
	}

	public void testQuestionWithCosts() throws Exception {
		q1.getProperties().setProperty(Property.TIME, new Double(20));
		q1.getProperties().setProperty(Property.RISK, new Double(50.5));

		XMLTag shouldCostsTag = new XMLTag("Properties");

		XMLTag costTag1 = new XMLTag("Property");
		costTag1.addAttribute("name", "timeexpenditure");
		costTag1.addAttribute("class", Double.class.getName());
		costTag1.setContent(Double.toString(20));
		shouldCostsTag.addChild(costTag1);

		XMLTag costTag2 = new XMLTag("Property");
		costTag2.addAttribute("name", "risk");
		costTag2.addAttribute("class", Double.class.getName());
		costTag2.setContent(Double.toString(50.5));
		shouldCostsTag.addChild(costTag2);

		shouldTag.addChild(shouldCostsTag);

		answersTag = new XMLTag("Answers");
		shouldTag.addChild(answersTag);

		isTag = new XMLTag(qw.write(q1, Util.createEmptyDocument()));

		assertEquals("(4)", shouldTag, isTag);
	}
}
