/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.persistence.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.KnowledgeBaseManagement;
import de.d3web.kernel.domainModel.PriorityGroup;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.supportknowledge.Property;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.persistence.tests.utils.XMLTagUtils;
import de.d3web.persistence.xml.writers.PriorityGroupWriter;

/**
 * @author merz
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class PriorityGroupTest extends TestCase {

	private PriorityGroup pg;
	private PriorityGroupWriter pgw;
	private String xmlcode;

	private KnowledgeBase kb;
	private QContainer qc, qc1, qc2;

	private XMLTag isTag;
	private XMLTag shouldTag;

	public PriorityGroupTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(PriorityGroupTest.suite());
	}

	public static Test suite() {
		return new TestSuite(PriorityGroupTest.class);
	}

	protected void setUp() {
		kb = new KnowledgeBase();

		qc = new QContainer();
		qc.setId("qcId");
		qc.setKnowledgeBase(kb);

		pg = new PriorityGroup();
		pg.setKnowledgeBase(kb);
		pg.setId("id1");
		pg.setText("text1");

		pgw = new PriorityGroupWriter();

		shouldTag = new XMLTag("PriorityGroup");
		shouldTag.addAttribute("ID", "id1");

		XMLTag shouldTextTag = new XMLTag("Text");
		shouldTextTag.setContent("text1");
		shouldTag.addChild(shouldTextTag);
	}

	public void testPriorityGroupSimpleState() throws Exception {
		xmlcode = pgw.getXMLString(pg);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "PriorityGroup", 0));

		assertEquals("(0)", shouldTag, isTag);
	}

	public void testPriorityGroupWithChildren() throws Exception {
				
		qc1 = new QContainer("qc1ID");
		qc1.setId("qc1ID");
		qc1.setKnowledgeBase(kb);
		qc1.setPriority(1);
		pg.addChild(qc1);

		qc2 = new QContainer("qc2ID");
		qc2.setId("qc2ID");
		qc2.setKnowledgeBase(kb);
		qc2.setPriority(1);
		pg.addChild(qc2);

		XMLTag children = new XMLTag("Children");

		XMLTag child1 = new XMLTag("Child");
		child1.addAttribute("ID", "qc1ID");
		children.addChild(child1);

		XMLTag child2 = new XMLTag("Child");
		child2.addAttribute("ID", "qc2ID");
		children.addChild(child2);

		shouldTag.addChild(children);

		xmlcode = pgw.getXMLString(pg);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "PriorityGroup", 0));

		assertEquals("(1)", shouldTag, isTag);
	}

	public void testPriorityGroupWithValues() throws Exception {
		pg.setMinLevel(new Integer(1));
		pg.setMaxLevel(new Integer(10));

		XMLTag minValue = new XMLTag("MinLevel");
		minValue.addAttribute("value", "1");
		shouldTag.addChild(minValue);

		XMLTag maxValue = new XMLTag("MaxLevel");
		maxValue.addAttribute("value", "10");
		shouldTag.addChild(maxValue);

		xmlcode = pgw.getXMLString(pg);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "PriorityGroup", 0));

		assertEquals("(2)", shouldTag, isTag);
	}

	public void testPriorityGroupWithProperties() throws Exception {
		pg.getProperties().setProperty(Property.HIDE_IN_DIALOG, new Boolean(true));
		pg.getProperties().setProperty(Property.COST, new Double(20));

		// Set propertyKeys = pg.getPropertyKeys();
		// MockPropertyDescriptor mpd = new MockPropertyDescriptor(pg,propertyKeys);

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

		xmlcode = pgw.getXMLString(pg);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "PriorityGroup", 0));

		assertEquals("(3)", shouldTag, isTag);
	}
}
