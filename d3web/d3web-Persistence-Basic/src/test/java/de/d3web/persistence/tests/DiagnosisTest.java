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

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.core.kpers.fragments.DiagnosisHandler;
import de.d3web.core.kpers.utilities.Util;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.info.Property;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.scoring.Score;

/**
 * @author merz
 *
 * !!! tests for checking prperties missing
 */
public class DiagnosisTest extends TestCase {
	
	private Diagnosis diag;
	private DiagnosisHandler dh;
	private XMLTag isTag;
	private XMLTag shouldTag;
	
	/**
	 * Constructor for DiagnosisOutputTest.
	 * @param arg0
	 */
	public DiagnosisTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DiagnosisTest.suite());
	}
	
	public static Test suite() {
		return new TestSuite(DiagnosisTest.class);
	}
	
	protected void setUp() {
		try {
			InitPluginManager.init();
		}
		catch (IOException e1) {
			assertTrue("Error initialising plugin framework", false);
		}
		//create the diagnosis
		diag = new Diagnosis("d1");
		diag.setText("d1-text");
		
		dh = new DiagnosisHandler();

		// first step in building shouldTag: id and text always added in shouldTag
		shouldTag = new XMLTag("Diagnosis");
		shouldTag.addAttribute("ID", "d1");
		
		XMLTag shouldTextTag = new XMLTag("Text");
		shouldTextTag.setContent("d1-text");
		shouldTag.addChild(shouldTextTag);
		
	}
	
	public void testDiagnosisSimpleState() throws Exception{
		isTag = new XMLTag(dh.write(diag, Util.createEmptyDocument()));
		
		assertEquals("(0)", shouldTag, isTag);
	}
	
	public void testDiagnosisWithApriori() throws Exception{
		diag.setAprioriProbability(Score.N2);
		
		shouldTag.addAttribute("aPriProb", "N2");
		
		isTag = new XMLTag(dh.write(diag, Util.createEmptyDocument()));
		
		assertEquals("(1)", shouldTag, isTag);
	}
	
	//Children are no longer parsed in the handlers
//	public void testDiagnosisWithChildren() throws Exception {
//		// add two children
//		Diagnosis d11 = new Diagnosis("d11");
//		d11.setText("d11-text");
//		d11.addParent(diag);
//		
//		Diagnosis d12 = new Diagnosis("d12");
//		d12.setText("d12-text");
//		d12.addParent(diag);
//		
//		XMLTag shouldChildrenTag = new XMLTag("Children");
//		XMLTag childTag1 = new XMLTag("Child");
//		childTag1.addAttribute("ID", "d11");
//		shouldChildrenTag.addChild(childTag1);
//			
//		XMLTag childTag2 = new XMLTag("Child");
//		childTag2.addAttribute("ID", "d12");
//		shouldChildrenTag.addChild(childTag2);
//		
//		shouldTag.addChild(shouldChildrenTag);
//
//		isTag = new XMLTag(dh.write(diag, Util.createEmptyDocument()));		
//					
//		assertEquals("(2)", shouldTag, isTag);
//	}
	
	public void testDiagnosisWithProperties() throws Exception {
		diag.getProperties().setProperty(Property.HIDE_IN_DIALOG, new Boolean(true));
		diag.getProperties().setProperty(Property.COST, new Double(20));
		
		// Set propertyKeys = diag.getPropertyKeys();
		// MockPropertyDescriptor mpd = new MockPropertyDescriptor(diag,propertyKeys);
		
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
