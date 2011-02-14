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
import de.d3web.core.knowledge.KnowledgeBase;
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

		Question q1 = new QuestionText(new KnowledgeBase(), "q1");

		QuestionHandler qw = new QuestionHandler();

		XMLTag isTag = new XMLTag(qw.write(q1, Util.createEmptyDocument()));

		XMLTag shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("name", "q1");
		shouldTag.addAttribute("type", "Text");
		assertEquals("QuestionText-output not correct (0)", shouldTag, isTag);
	}

	@Test
	public void testQuestionNumOutput() throws Exception {

		Question q1 = new QuestionNum(new KnowledgeBase(), "q1");

		QuestionHandler qw = new QuestionHandler();

		XMLTag isTag = new XMLTag(qw.write(q1, Util.createEmptyDocument()));

		XMLTag shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("name", "q1");
		shouldTag.addAttribute("type", "Num");

		assertEquals("QuestionNum-output not correct (0)", shouldTag, isTag);
	}

	@Test
	public void testQuestionDateOutput() throws Exception {

		Question q1 = new QuestionDate(new KnowledgeBase(), "q1");

		QuestionHandler qw = new QuestionHandler();

		XMLTag isTag = new XMLTag(qw.write(q1, Util.createEmptyDocument()));

		XMLTag shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("name", "q1");
		shouldTag.addAttribute("type", "Date");

		assertEquals("QuestionNum-output not correct (0)", shouldTag, isTag);
	}

	@Test
	public void testQuestionOCOutput() throws Exception {

		QuestionOC q1 = new QuestionOC(new KnowledgeBase(), "q1");
		List<Choice> alternatives = new LinkedList<Choice>();
		Choice a1 = new Choice("q1a1");
		alternatives.add(a1);
		q1.setAlternatives(alternatives);

		QuestionHandler qw = new QuestionHandler();

		XMLTag isTag = new XMLTag(qw.write(q1, Util.createEmptyDocument()));

		XMLTag shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("name", "q1");
		shouldTag.addAttribute("type", "OC");

		XMLTag answersTag = new XMLTag("Answers");
		XMLTag answerTag = new XMLTag("Answer");
		answerTag.addAttribute("name", "q1a1");
		answerTag.addAttribute("type", "AnswerChoice");
		answersTag.addChild(answerTag);
		shouldTag.addChild(answersTag);

		assertEquals("QuestionOC-output not correct (0)", shouldTag, isTag);
	}

	@Test
	public void testQContainerOutput() throws Exception {
		KnowledgeBase kb = new KnowledgeBase();
		QContainer c1 = new QContainer(kb, "c1");

		Question q1 = new QuestionText(kb, "q1");
		c1.addChild(q1);

		QContainerHandler qcw = new QContainerHandler();

		XMLTag isTag = new XMLTag(qcw.write(c1, Util.createEmptyDocument()));

		XMLTag shouldTag = new XMLTag("QContainer");
		shouldTag.addAttribute("name", "c1");

		assertEquals("Qcontainer-output not correct (0)", shouldTag, isTag);

	}

	@Test
	public void testSolutionOutput() throws Exception {
		KnowledgeBase kb = new KnowledgeBase();
		Solution diag = new Solution(kb, "d1");

		Solution diagChild = new Solution(kb, "d11");
		diag.addChild(diagChild);

		SolutionsHandler dw = new SolutionsHandler();

		XMLTag isTag = new XMLTag(dw.write(diag, Util.createEmptyDocument()));

		XMLTag shouldTag = new XMLTag("Diagnosis");
		shouldTag.addAttribute("name", "d1");

		assertEquals("Diagnosis-output not correct (0)", shouldTag, isTag);

	}

}
