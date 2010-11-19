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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.io.fragments.QContainerHandler;
import de.d3web.core.io.fragments.QuestionHandler;
import de.d3web.core.io.fragments.SolutionsHandler;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.plugin.test.InitPluginManager;

/**
 * @author bates
 */
public class KnowledgeBaseExportTest {

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();
	}

	@Test
	public void testQuestionTextOutput() throws Exception {

		Question q1 = new QuestionText("q1");
		q1.setName("q1-text");

		QuestionHandler qw = new QuestionHandler();

		XMLTag isTag = new XMLTag(qw.write(q1, Util.createEmptyDocument()));

		XMLTag shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("ID", "q1");
		shouldTag.addAttribute("type", "Text");
		XMLTag child = new XMLTag("Text");
		child.setContent("q1-text");
		shouldTag.addChild(child);

		assertEquals("QuestionText-output not correct (0)", shouldTag, isTag);
	}

	@Test
	public void testQuestionNumOutput() throws Exception {

		Question q1 = new QuestionNum("q1");
		q1.setName("q1-text");

		QuestionHandler qw = new QuestionHandler();

		XMLTag isTag = new XMLTag(qw.write(q1, Util.createEmptyDocument()));

		XMLTag shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("ID", "q1");
		shouldTag.addAttribute("type", "Num");
		XMLTag child = new XMLTag("Text");
		child.setContent("q1-text");
		shouldTag.addChild(child);

		assertEquals("QuestionNum-output not correct (0)", shouldTag, isTag);
	}

	@Test
	public void testQuestionDateOutput() throws Exception {

		Question q1 = new QuestionDate("q1");
		q1.setName("q1-text");

		QuestionHandler qw = new QuestionHandler();

		XMLTag isTag = new XMLTag(qw.write(q1, Util.createEmptyDocument()));

		XMLTag shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("ID", "q1");
		shouldTag.addAttribute("type", "Date");
		XMLTag child = new XMLTag("Text");
		child.setContent("q1-text");
		shouldTag.addChild(child);

		assertEquals("QuestionNum-output not correct (0)", shouldTag, isTag);
	}

	@Test
	public void testQuestionOCOutput() throws Exception {

		QuestionOC q1 = new QuestionOC("q1");
		q1.setName("q1-text");
		List<Choice> alternatives = new LinkedList<Choice>();
		Choice a1 = new Choice("q1a1");
		a1.setText("q1a1-text");
		alternatives.add(a1);
		q1.setAlternatives(alternatives);

		QuestionHandler qw = new QuestionHandler();

		XMLTag isTag = new XMLTag(qw.write(q1, Util.createEmptyDocument()));

		XMLTag shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("ID", "q1");
		shouldTag.addAttribute("type", "OC");
		XMLTag child = new XMLTag("Text");
		child.setContent("q1-text");
		shouldTag.addChild(child);

		XMLTag answersTag = new XMLTag("Answers");
		XMLTag answerTag = new XMLTag("Answer");
		answerTag.addAttribute("ID", "q1a1");
		answerTag.addAttribute("type", "AnswerChoice");
		XMLTag answerTextTag = new XMLTag("Text");
		answerTextTag.setContent("q1a1-text");
		answerTag.addChild(answerTextTag);
		answersTag.addChild(answerTag);
		shouldTag.addChild(answersTag);

		assertEquals("QuestionOC-output not correct (0)", shouldTag, isTag);
	}

	@Test
	public void testQContainerOutput() throws Exception {
		QContainer c1 = new QContainer("c1");
		c1.setName("c1-text");

		Question q1 = new QuestionText("q1");
		q1.setName("q1-text");
		q1.addParent(c1);

		QContainerHandler qcw = new QContainerHandler();

		XMLTag isTag = new XMLTag(qcw.write(c1, Util.createEmptyDocument()));

		XMLTag shouldTag = new XMLTag("QContainer");
		shouldTag.addAttribute("ID", "c1");
		XMLTag shouldTextTag = new XMLTag("Text");
		shouldTextTag.setContent("c1-text");
		shouldTag.addChild(shouldTextTag);

		assertEquals("Qcontainer-output not correct (0)", shouldTag, isTag);

	}

	@Test
	public void testSolutionOutput() throws Exception {
		Solution diag = new Solution("d1");
		diag.setName("d1-text");

		Solution diagChild = new Solution("d11");
		diagChild.setName("d11-text");
		diagChild.addParent(diag);

		SolutionsHandler dw = new SolutionsHandler();

		XMLTag isTag = new XMLTag(dw.write(diag, Util.createEmptyDocument()));

		XMLTag shouldTag = new XMLTag("Diagnosis");
		shouldTag.addAttribute("ID", "d1");
		XMLTag shouldTextTag = new XMLTag("Text");
		shouldTextTag.setContent("d1-text");
		shouldTag.addChild(shouldTextTag);

		assertEquals("Diagnosis-output not correct (0)", shouldTag, isTag);

	}

}