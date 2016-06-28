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
import java.io.OutputStream;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.io.BasicPersistenceHandler;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.DummyProgressListener;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.persistence.tests.utils.XMLTagUtils;
import de.d3web.plugin.test.InitPluginManager;

/**
 * @author merz
 */
public class BasicPersistenceHandlerTest {

	private KnowledgeBase kb;
	private BasicPersistenceHandler basicPersistenceHandler;
	private String xmlcode;

	private Question q1, q2;
	private Solution diag1;

	private XMLTag isTag;
	private XMLTag shouldTag;

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();

		kb = new KnowledgeBase();

		q1 = new QuestionNum(kb, "q1");
		kb.setRootQASet(q1);
		q2 = new QuestionOC(kb, "q2");

		diag1 = new Solution(kb, "d1");
		kb.setRootSolution(diag1);
	}

	@Test
	public void testBasicPersistenceHandler() throws Exception {
		shouldTag = new XMLTag("KnowledgeBase");
		shouldTag.addAttribute("type", "basic");
		shouldTag.addAttribute("system", "d3web");
		shouldTag.addAttribute("id", "");

		this.addInitQuestions();

		XMLTag rootQASetTag = new XMLTag("rootQASet");
		rootQASetTag.setContent("q1");
		shouldTag.addChild(rootQASetTag);

		XMLTag rootSolutionTag = new XMLTag("rootSolution");
		rootSolutionTag.setContent("d1");
		shouldTag.addChild(rootSolutionTag);

		this.addQASets();

		this.addSolutions();

		XMLTag knowledgeSlicesTag = new XMLTag("KnowledgeSlices");
		shouldTag.addChild(knowledgeSlicesTag);

		basicPersistenceHandler = new BasicPersistenceHandler();
		OutputStream stream = new OutputStream() {

			final StringBuffer sb = new StringBuffer();

			@Override
			public void write(int b) throws IOException {
				sb.append((char) b);
			}

			@Override
			public String toString() {
				return sb.toString();
			}
		};
		PersistenceManager pm = PersistenceManager.getInstance();
		basicPersistenceHandler.write(pm, kb, stream, new DummyProgressListener());
		xmlcode = stream.toString();
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "KnowledgeBase", 0));

		assertEquals("(0)", shouldTag, isTag);
	}

	private void addQASets() {
		new QContainer(kb, "qc1");

		new QContainer(kb, "qc2");

		XMLTag qASetTag = new XMLTag("QASets");
		shouldTag.addChild(qASetTag);

		XMLTag questionTag1 = new XMLTag("Question");
		questionTag1.addAttribute("name", "q1");
		questionTag1.addAttribute("type", "Num");
		qASetTag.addChild(questionTag1);

		XMLTag questionTag2 = new XMLTag("Question");
		questionTag2.addAttribute("name", "q2");
		questionTag2.addAttribute("type", "OC");
		XMLTag questionAnswersTag2 = new XMLTag("Answers");
		questionTag2.addChild(questionAnswersTag2);
		qASetTag.addChild(questionTag2);

		XMLTag qContainerTag1 = new XMLTag("QContainer");
		qContainerTag1.addAttribute("name", "qc1");
		qASetTag.addChild(qContainerTag1);

		XMLTag qContainerTag2 = new XMLTag("QContainer");
		qContainerTag2.addAttribute("name", "qc2");
		qASetTag.addChild(qContainerTag2);
	}

	public void addSolutions() {
		new Solution(kb, "d2");

		XMLTag diagnosesTag = new XMLTag("Diagnoses");
		shouldTag.addChild(diagnosesTag);

		XMLTag diagnosisTag1 = new XMLTag("Diagnosis");
		diagnosisTag1.addAttribute("name", "d1");
		diagnosesTag.addChild(diagnosisTag1);

		XMLTag diagnosisTag2 = new XMLTag("Diagnosis");
		diagnosisTag2.addAttribute("name", "d2");
		diagnosesTag.addChild(diagnosisTag2);
	}

	public void addInitQuestions() {
		LinkedList<Question> initList = new LinkedList<>();
		initList.add(q1);
		initList.add(q2);

		kb.setInitQuestions(initList);

		XMLTag initQuestionsTag = new XMLTag("InitQuestions");
		shouldTag.addChild(initQuestionsTag);

		XMLTag initQuestionTag1 = new XMLTag("Question");
		initQuestionTag1.addAttribute("name", "q1");
		initQuestionsTag.addChild(initQuestionTag1);

		XMLTag initQuestionTag2 = new XMLTag("Question");
		initQuestionTag2.addAttribute("name", "q2");
		initQuestionsTag.addChild(initQuestionTag2);
	}

}
