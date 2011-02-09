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

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.io.fragments.QContainerHandler;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.plugin.test.InitPluginManager;

/**
 * @author merz
 * 
 *         !!! property-test missing !!!
 */
public class QContainerTest {

	private QContainer qc1;
	private QContainerHandler qcw;
	private XMLTag isTag;
	private XMLTag shouldTag;

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();

		qc1 = new QContainer(new KnowledgeBase(), "c1");

		shouldTag = new XMLTag("QContainer");
		shouldTag.addAttribute("name", "c1");

		qcw = new QContainerHandler();
	}

	@Test
	public void testQContainerSimple() throws Exception {
		isTag = new XMLTag(qcw.write(qc1, Util.createEmptyDocument()));

		assertEquals("(0)", shouldTag, isTag);
	}

	@Test
	public void testQContainerWithChildren() throws Exception {
		KnowledgeBase kb = new KnowledgeBase();
		Question q1 = new QuestionText(kb, "q1");
		q1.addParent(qc1);

		Question q2 = new QuestionText(kb, "q2");
		q2.addParent(qc1);

		isTag = new XMLTag(qcw.write(qc1, Util.createEmptyDocument()));

		assertEquals("(2)", shouldTag, isTag);
	}

	@Test
	public void testQContainerWithProperties() throws Exception {
		qc1.getInfoStore().addValue(BasicProperties.COST, new Double(20));

		// Set propertyKeys = qc1.getPropertyKeys();
		// MockPropertyDescriptor mpd = new
		// MockPropertyDescriptor(qc1,propertyKeys);

		XMLTag propertiesTag = new XMLTag("infoStore");

		XMLTag propertyTag2 = new XMLTag("entry");
		propertyTag2.addAttribute("property", "cost");
		propertyTag2.setContent("20.0");

		propertiesTag.addChild(propertyTag2);

		shouldTag.addChild(propertiesTag);

		isTag = new XMLTag(qcw.write(qc1, Util.createEmptyDocument()));

		assertEquals("(4)", shouldTag, isTag);
	}
}
