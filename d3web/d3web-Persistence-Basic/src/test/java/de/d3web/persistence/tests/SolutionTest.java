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
import de.d3web.core.io.fragments.SolutionsHandler;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.scoring.Score;

/**
 * @author merz
 * 
 *         !!! tests for checking prperties missing
 */
public class SolutionTest extends TestCase {

	private Solution diag;
	private SolutionsHandler dh;
	private XMLTag isTag;
	private XMLTag shouldTag;

	/**
	 * Constructor for DiagnosisOutputTest.
	 * 
	 * @param arg0
	 */
	public SolutionTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SolutionTest.suite());
	}

	public static Test suite() {
		return new TestSuite(SolutionTest.class);
	}

	@Override
	protected void setUp() {
		try {
			InitPluginManager.init();
		}
		catch (IOException e1) {
			assertTrue("Error initialising plugin framework", false);
		}
		// create the diagnosis
		diag = new Solution("d1");
		diag.setName("d1-text");

		dh = new SolutionsHandler();

		// first step in building shouldTag: id and text always added in
		// shouldTag
		shouldTag = new XMLTag("Diagnosis");
		shouldTag.addAttribute("ID", "d1");

		XMLTag shouldTextTag = new XMLTag("Text");
		shouldTextTag.setContent("d1-text");
		shouldTag.addChild(shouldTextTag);

	}

	public void testSolutionSimpleState() throws Exception {
		isTag = new XMLTag(dh.write(diag, Util.createEmptyDocument()));

		assertEquals("(0)", shouldTag, isTag);
	}

	public void testSolutionWithApriori() throws Exception {
		diag.setAprioriProbability(Score.N2);

		shouldTag.addAttribute("aPriProb", "N2");

		isTag = new XMLTag(dh.write(diag, Util.createEmptyDocument()));

		assertEquals("(1)", shouldTag, isTag);
	}

	public void testSolutionWithProperties() throws Exception {
		diag.getProperties().setProperty(Property.HIDE_IN_DIALOG, new Boolean(true));
		diag.getProperties().setProperty(Property.COST, new Double(20));

		// Set propertyKeys = diag.getPropertyKeys();
		// MockPropertyDescriptor mpd = new
		// MockPropertyDescriptor(diag,propertyKeys);

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

		isTag = new XMLTag(dh.write(diag, Util.createEmptyDocument()));

		assertEquals("(3)", shouldTag, isTag);
	}
}
