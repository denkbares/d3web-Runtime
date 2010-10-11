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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.io.progress.DummyProgressListener;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.multimedia.io.ImageQuestionPersistenceHandler;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.persistence.tests.utils.XMLTagUtils;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Functionality test for ImageQuestionPersistenceHandler.
 * 
 * @author Johannes Dienst
 * 
 */
public class ImageQuestionPersistenceHandlerTest {

	private KnowledgeBase kb;
	private Question q1, q2;
	private XMLTag shouldTag;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();

		ImageQuestionPersistenceHandler ph = new ImageQuestionPersistenceHandler();
		kb = new KnowledgeBase();

		q1 = new QuestionNum("QGelenkstatus");
		q1.setName("QGelenkstatus");
		q1.setKnowledgeBase(kb);
		q2 = new QuestionNum("QGelenkstatus2");
		q2.setName("QGelenkstatus2");
		q2.setKnowledgeBase(kb);
		File file = new File("src/test/resources/picturequestions.xml");
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		ph.read(kb, in, new DummyProgressListener());
	}

	@Test
	public void testPropertiesLoading() {

		// Question 1
		Question q1 = kb.searchQuestion("QGelenkstatus");
		List<?> props = (List<?>) q1.getInfoStore().getValue(BasicProperties.IMAGE_QUESTION_INFO);
		String imageName = (String) props.get(0);

		// Image Name right?
		assertEquals("StrichBe.png", imageName);
		List<?> answerRegions = (List<?>) props.get(1);

		// The assertions: AnswerRegion 1
		List<?> attributes = (List<?>) answerRegions.get(0);
		String answerID = (String) attributes.get(0);
		int xStart = Integer.parseInt((String) attributes.get(1));
		int xEnd = Integer.parseInt((String) attributes.get(2));
		int yStart = Integer.parseInt((String) attributes.get(3));
		int yEnd = Integer.parseInt((String) attributes.get(4));
		assertEquals("QGelenkstatusA15", answerID);
		assertEquals(1, xStart);
		assertEquals(2, xEnd);
		assertEquals(3, yStart);
		assertEquals(4, yEnd);

		// Question 1
		// The assertions: AnswerRegion 2
		attributes = (List<?>) answerRegions.get(1);
		answerID = (String) attributes.get(0);
		xStart = Integer.parseInt((String) attributes.get(1));
		xEnd = Integer.parseInt((String) attributes.get(2));
		yStart = Integer.parseInt((String) attributes.get(3));
		yEnd = Integer.parseInt((String) attributes.get(4));
		assertEquals("QGelenkstatusA16", answerID);
		assertEquals(5, xStart);
		assertEquals(6, xEnd);
		assertEquals(7, yStart);
		assertEquals(8, yEnd);

		Question q2 = kb.searchQuestion("QGelenkstatus2");
		props = (List<?>) q2.getInfoStore().getValue(BasicProperties.IMAGE_QUESTION_INFO);
		imageName = (String) props.get(0);

		// Question 2
		// Image Name right?
		assertEquals("StrichBe2.png", imageName);
		answerRegions = (List<?>) props.get(1);

		// The assertions: AnswerRegion 1
		attributes = (List<?>) answerRegions.get(0);
		answerID = (String) attributes.get(0);
		xStart = Integer.parseInt((String) attributes.get(1));
		xEnd = Integer.parseInt((String) attributes.get(2));
		yStart = Integer.parseInt((String) attributes.get(3));
		yEnd = Integer.parseInt((String) attributes.get(4));
		assertEquals("QGelenkstatusA17", answerID);
		assertEquals(1, xStart);
		assertEquals(2, xEnd);
		assertEquals(3, yStart);
		assertEquals(4, yEnd);

		// Question 1
		// The assertions: AnswerRegion 2
		attributes = (List<?>) answerRegions.get(1);
		answerID = (String) attributes.get(0);
		xStart = Integer.parseInt((String) attributes.get(1));
		xEnd = Integer.parseInt((String) attributes.get(2));
		yStart = Integer.parseInt((String) attributes.get(3));
		yEnd = Integer.parseInt((String) attributes.get(4));
		assertEquals("QGelenkstatusA18", answerID);
		assertEquals(5, xStart);
		assertEquals(6, xEnd);
		assertEquals(7, yStart);
		assertEquals(8, yEnd);
	}

	@Test
	public void testWrite() throws Exception {
		shouldTag = new XMLTag("Questions");

		this.buildShouldTag();

		ImageQuestionPersistenceHandler bph = new ImageQuestionPersistenceHandler();
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
		bph.write(kb, stream, new DummyProgressListener());
		String xmlcode = stream.toString();
		XMLTag isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Questions", 0));

		assertEquals("(0)", shouldTag, isTag);
	}

	private void buildShouldTag() {

		// TODO I dont know why,
		// but this has to be in this order
		// Johannes Dienst
		XMLTag question2 = new XMLTag("Question");
		question2.addAttribute("ID", "QGelenkstatus2");
		XMLTag questionImage2 = new XMLTag("QuestionImage");
		questionImage2.addAttribute("file", "StrichBe2.png");
		XMLTag answerRegion21 = new XMLTag("AnswerRegion");
		answerRegion21.addAttribute("answerID", "QGelenkstatusA17");
		answerRegion21.addAttribute("xStart", "1");
		answerRegion21.addAttribute("xEnd", "2");
		answerRegion21.addAttribute("yStart", "3");
		answerRegion21.addAttribute("yEnd", "4");
		questionImage2.addChild(answerRegion21);
		XMLTag answerRegion22 = new XMLTag("AnswerRegion");
		answerRegion22.addAttribute("answerID", "QGelenkstatusA18");
		answerRegion22.addAttribute("xStart", "5");
		answerRegion22.addAttribute("xEnd", "6");
		answerRegion22.addAttribute("yStart", "7");
		answerRegion22.addAttribute("yEnd", "8");
		questionImage2.addChild(answerRegion22);
		question2.addChild(questionImage2);
		shouldTag.addChild(question2);

		XMLTag question1 = new XMLTag("Question");
		question1.addAttribute("ID", "QGelenkstatus");
		XMLTag questionImage1 = new XMLTag("QuestionImage");
		questionImage1.addAttribute("file", "StrichBe.png");
		XMLTag answerRegion1 = new XMLTag("AnswerRegion");
		answerRegion1.addAttribute("answerID", "QGelenkstatusA15");
		answerRegion1.addAttribute("xStart", "1");
		answerRegion1.addAttribute("xEnd", "2");
		answerRegion1.addAttribute("yStart", "3");
		answerRegion1.addAttribute("yEnd", "4");
		questionImage1.addChild(answerRegion1);
		XMLTag answerRegion12 = new XMLTag("AnswerRegion");
		answerRegion12.addAttribute("answerID", "QGelenkstatusA16");
		answerRegion12.addAttribute("xStart", "5");
		answerRegion12.addAttribute("xEnd", "6");
		answerRegion12.addAttribute("yStart", "7");
		answerRegion12.addAttribute("yEnd", "8");
		questionImage1.addChild(answerRegion12);
		question1.addChild(questionImage1);
		shouldTag.addChild(question1);

	}
}
