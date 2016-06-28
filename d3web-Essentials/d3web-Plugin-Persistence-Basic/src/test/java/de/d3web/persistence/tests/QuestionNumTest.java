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

import de.d3web.core.io.KnowledgeBasePersistence;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.fragments.NumericalIntervalHandler;
import de.d3web.core.io.fragments.QuestionHandler;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.plugin.test.InitPluginManager;

/**
 * @author merz
 */
public class QuestionNumTest {

	private Question q1;
	private QuestionHandler qw;
	private XMLTag isTag;
	private XMLTag shouldTag;

	private Persistence<KnowledgeBase> persistence;

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = new KnowledgeBase();
		persistence = new KnowledgeBasePersistence(PersistenceManager.getInstance(), kb);

		q1 = new QuestionNum(kb, "q1");

		qw = new QuestionHandler();

		shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("name", "q1");
		shouldTag.addAttribute("type", "Num");
	}

	@Test
	public void testQuestionWithProperties() throws Exception {
		q1.getInfoStore().addValue(BasicProperties.COST, new Double(20));

		// Set propertyKeys = q1.getPropertyKeys();
		// MockPropertyDescriptor mpd = new
		// MockPropertyDescriptor(q1,propertyKeys);

		XMLTag propertiesTag = new XMLTag("infoStore");

		XMLTag propertyTag2 = new XMLTag("entry");
		propertyTag2.addAttribute("property", "cost");
		propertyTag2.setContent("20.0");

		propertiesTag.addChild(propertyTag2);

		shouldTag.addChild(propertiesTag);

		isTag = new XMLTag(new QuestionHandler().write(q1, persistence));

		assertEquals("(2)", shouldTag, isTag);
	}

	@Test
	public void testQuestionNumTestSimple() throws Exception {
		isTag = new XMLTag(qw.write(q1, persistence));

		assertEquals("(0)", shouldTag, isTag);
	}

	@Test
	public void testQuestionWithIntervals() throws Exception {

		List<NumericalInterval> intervals = new LinkedList<>();
		intervals.add(new NumericalInterval(Double.NEGATIVE_INFINITY, 30, true, false));
		intervals.add(new NumericalInterval(30, 300.03, true, true));
		intervals.add(new NumericalInterval(300.03, Double.POSITIVE_INFINITY, false, true));
		((QuestionNum) q1).setValuePartitions(intervals);

		XMLTag intervalsTag = new XMLTag(NumericalIntervalHandler.GROUPTAG);

		XMLTag intervalTag1 = new XMLTag(NumericalIntervalHandler.TAG);
		intervalTag1.addAttribute("lower", "-INFINITY");
		intervalTag1.addAttribute("upper", "30.0");
		intervalTag1.addAttribute("type", "LeftOpenRightClosedInterval");

		XMLTag intervalTag2 = new XMLTag(NumericalIntervalHandler.TAG);
		intervalTag2.addAttribute("lower", "30.0");
		intervalTag2.addAttribute("upper", "300.03");
		intervalTag2.addAttribute("type", "LeftOpenRightOpenInterval");

		XMLTag intervalTag3 = new XMLTag(NumericalIntervalHandler.TAG);
		intervalTag3.addAttribute("lower", "300.03");
		intervalTag3.addAttribute("upper", "+INFINITY");
		intervalTag3.addAttribute("type", "LeftClosedRightOpenInterval");

		intervalsTag.addChild(intervalTag1);
		intervalsTag.addChild(intervalTag2);
		intervalsTag.addChild(intervalTag3);

		shouldTag.addChild(intervalsTag);

		isTag = new XMLTag(qw.write(q1, persistence));
		assertEquals("(intervals)", shouldTag, isTag);
	}
}
