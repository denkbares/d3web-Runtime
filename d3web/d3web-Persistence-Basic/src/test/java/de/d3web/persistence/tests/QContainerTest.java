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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.core.io.fragments.QContainerHandler;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.plugin.test.InitPluginManager;

/**
 * @author merz
 * 
 *         !!! property-test missing !!!
 */
public class QContainerTest extends TestCase {

	private QContainer qc1;
	private QContainerHandler qcw;
	private XMLTag isTag;
	private XMLTag shouldTag;

	/**
	 * Constructor for QContainerOutputTest.
	 * 
	 * @param arg0
	 */
	public QContainerTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(QContainerTest.suite());
	}

	public static Test suite() {
		return new TestSuite(QContainerTest.class);
	}

	protected void setUp() {
		try {
			InitPluginManager.init();
		}
		catch (IOException e1) {
			assertTrue("Error initialising plugin framework", false);
		}
		qc1 = new QContainer("c1");
		qc1.setName("c1-text");

		shouldTag = new XMLTag("QContainer");
		shouldTag.addAttribute("ID", "c1");

		XMLTag shouldTextTag = new XMLTag("Text");
		shouldTextTag.setContent("c1-text");
		shouldTag.addChild(shouldTextTag);

		qcw = new QContainerHandler();

	}

	public void testQContainerSimple() throws Exception {
		isTag = new XMLTag(qcw.write(qc1, Util.createEmptyDocument()));

		assertEquals("(0)", shouldTag, isTag);
	}

	public void testQContainerWithPriority() throws Exception {
		qc1.setPriority(new Integer(1));
		shouldTag.addAttribute("priority", "1");

		isTag = new XMLTag(qcw.write(qc1, Util.createEmptyDocument()));

		assertEquals("(1)", shouldTag, isTag);
	}

	public void testQContainerWithChildren() throws Exception {
		Question q1 = new QuestionText("q1");
		q1.setName("q1-text");
		q1.addParent(qc1);

		Question q2 = new QuestionText("q2");
		q2.setName("q2-text");
		q2.addParent(qc1);

		isTag = new XMLTag(qcw.write(qc1, Util.createEmptyDocument()));

		assertEquals("(2)", shouldTag, isTag);
	}

	public void testQContainerWithCosts() throws Exception {
		qc1.getProperties().setProperty(Property.TIME, new Double(20));
		qc1.getProperties().setProperty(Property.RISK, new Double(50.5));

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

		isTag = new XMLTag(qcw.write(qc1, Util.createEmptyDocument()));

		assertEquals("(3)", shouldTag, isTag);
	}

	public void testQContainerWithProperties() throws Exception {
		qc1.getProperties().setProperty(Property.HIDE_IN_DIALOG, new Boolean(true));
		qc1.getProperties().setProperty(Property.COST, new Double(20));

		// Set propertyKeys = qc1.getPropertyKeys();
		// MockPropertyDescriptor mpd = new
		// MockPropertyDescriptor(qc1,propertyKeys);

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

		isTag = new XMLTag(qcw.write(qc1, Util.createEmptyDocument()));

		assertEquals("(4)", shouldTag, isTag);
	}
}
