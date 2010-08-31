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

		q1 = new QuestionNum("q1");
		q1.setName("q1-text");
		q1.setKnowledgeBase(kb);

		q2 = new QuestionOC("q2");
		q2.setName("q2-text");
		q2.setKnowledgeBase(kb);

		diag1 = new Solution("d1");
		diag1.setName("d1-text");
	}

	@Test
	public void testBasicPersistenceHandler() throws Exception {
		shouldTag = new XMLTag("KnowledgeBase");
		shouldTag.addAttribute("type", "basic");
		shouldTag.addAttribute("system", "d3web");
		shouldTag.addAttribute("id", "");

		this.addInitQuestions();

		this.addCosts();

		this.addQASets();

		this.addSolutions();

		XMLTag knowledgeSlicesTag = new XMLTag("KnowledgeSlices");
		shouldTag.addChild(knowledgeSlicesTag);

		basicPersistenceHandler = new BasicPersistenceHandler();
		OutputStream stream = new OutputStream() {

			StringBuffer sb = new StringBuffer();

			@Override
			public void write(int b) throws IOException {
				sb.append((char) b);
			}

			@Override
			public String toString() {
				return sb.toString();
			}
		};
		basicPersistenceHandler.write(kb, stream, new DummyProgressListener());
		xmlcode = stream.toString();
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "KnowledgeBase", 0));

		assertEquals("(0)", shouldTag, isTag);
	}

	private void addCosts() {
		kb.setCostUnit("timeexpenditure-id", "Minuten");
		kb.setCostVerbalization("timeexpenditure-id", "Arztzeit");

		kb.setCostUnit("risk-id", "Punkte");
		kb.setCostVerbalization("risk-id", "Patientenbelastung");

		XMLTag costsTag = new XMLTag("Costs");
		shouldTag.addChild(costsTag);

		XMLTag costTag1 = new XMLTag("Cost");
		costTag1.addAttribute("ID", "risk-id");
		XMLTag verb1 = new XMLTag("Verbalization");
		verb1.setContent("Patientenbelastung");
		XMLTag unit1 = new XMLTag("Unit");
		unit1.setContent("Punkte");
		costTag1.addChild(verb1);
		costTag1.addChild(unit1);
		costsTag.addChild(costTag1);

		XMLTag costTag2 = new XMLTag("Cost");
		costTag2.addAttribute("ID", "timeexpenditure-id");
		XMLTag verb2 = new XMLTag("Verbalization");
		verb2.setContent("Arztzeit");
		XMLTag unit2 = new XMLTag("Unit");
		unit2.setContent("Minuten");
		costTag2.addChild(verb2);
		costTag2.addChild(unit2);
		costsTag.addChild(costTag2);
	}

	private void addQASets() {
		QContainer qc1 = new QContainer("qc1");
		qc1.setName("qc1-text");

		QContainer qc2 = new QContainer("qc2");
		qc2.setName("qc2-text");

		kb.add(qc1);
		kb.add(qc2);

		XMLTag qASetTag = new XMLTag("QASets");
		shouldTag.addChild(qASetTag);

		XMLTag questionTag1 = new XMLTag("Question");
		questionTag1.addAttribute("ID", "q1");
		questionTag1.addAttribute("type", "Num");
		XMLTag questionTextTag1 = new XMLTag("Text");
		questionTextTag1.setContent("q1-text");
		questionTag1.addChild(questionTextTag1);
		qASetTag.addChild(questionTag1);

		XMLTag questionTag2 = new XMLTag("Question");
		questionTag2.addAttribute("ID", "q2");
		questionTag2.addAttribute("type", "OC");
		XMLTag questionTextTag2 = new XMLTag("Text");
		questionTextTag2.setContent("q2-text");
		questionTag2.addChild(questionTextTag2);
		XMLTag questionAnswersTag2 = new XMLTag("Answers");
		questionTag2.addChild(questionAnswersTag2);
		qASetTag.addChild(questionTag2);

		XMLTag qContainerTag1 = new XMLTag("QContainer");
		qContainerTag1.addAttribute("ID", "qc1");
		XMLTag qContainerTextTag1 = new XMLTag("Text");
		qContainerTextTag1.setContent("qc1-text");
		qContainerTag1.addChild(qContainerTextTag1);
		qASetTag.addChild(qContainerTag1);

		XMLTag qContainerTag2 = new XMLTag("QContainer");
		qContainerTag2.addAttribute("ID", "qc2");
		XMLTag qContainerTextTag2 = new XMLTag("Text");
		qContainerTextTag2.setContent("qc2-text");
		qContainerTag2.addChild(qContainerTextTag2);
		qASetTag.addChild(qContainerTag2);
	}

	public void addSolutions() {
		kb.add(diag1);

		Solution diag2 = new Solution("d2");
		diag2.setName("d2-text");
		kb.add(diag2);

		XMLTag diagnosesTag = new XMLTag("Diagnoses");
		shouldTag.addChild(diagnosesTag);

		XMLTag diagnosisTag1 = new XMLTag("Diagnosis");
		diagnosisTag1.addAttribute("ID", "d1");
		XMLTag diagnosisTagTextTag1 = new XMLTag("Text");
		diagnosisTagTextTag1.setContent("d1-text");
		diagnosisTag1.addChild(diagnosisTagTextTag1);
		diagnosesTag.addChild(diagnosisTag1);

		XMLTag diagnosisTag2 = new XMLTag("Diagnosis");
		diagnosisTag2.addAttribute("ID", "d2");
		XMLTag diagnosisTagTextTag2 = new XMLTag("Text");
		diagnosisTagTextTag2.setContent("d2-text");
		diagnosisTag2.addChild(diagnosisTagTextTag2);
		diagnosesTag.addChild(diagnosisTag2);
	}

	public void addInitQuestions() {
		LinkedList<Question> initList = new LinkedList<Question>();
		initList.add(q1);
		initList.add(q2);

		kb.setInitQuestions(initList);

		XMLTag initQuestionsTag = new XMLTag("InitQuestions");
		shouldTag.addChild(initQuestionsTag);

		XMLTag initQuestionTag1 = new XMLTag("Question");
		initQuestionTag1.addAttribute("ID", "q1");
		initQuestionsTag.addChild(initQuestionTag1);

		XMLTag initQuestionTag2 = new XMLTag("Question");
		initQuestionTag2.addAttribute("ID", "q2");
		initQuestionsTag.addChild(initQuestionTag2);
	}

}
