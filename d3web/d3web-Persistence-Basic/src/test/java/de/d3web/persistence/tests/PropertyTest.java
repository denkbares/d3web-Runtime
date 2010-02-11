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
import de.d3web.core.kpers.fragments.PropertiesHandler;
import de.d3web.core.kpers.utilities.Util;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.info.Properties;
import de.d3web.core.terminology.info.Property;
import de.d3web.persistence.tests.utils.XMLTag;

/**
 * @author merz
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class PropertyTest extends TestCase {

	private PropertiesHandler pdh;
	
	private Diagnosis diag;
	
	private XMLTag isTag;
	
	 
	public PropertyTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(PropertyTest.suite());
	}
	
	public static Test suite() {
		return new TestSuite(PropertyTest.class);
	}
	
	protected void setUp() {
		diag = new Diagnosis();
		
		pdh = new PropertiesHandler();
	}
	
	// one one property can be tested, 'cause the propertyDesciptorWriter returns
	// only an serial enumeration from properties, but a root is needed in a tree-structure.
	// therefore, a test is better in diagnosis (for example) because there is also checked whether
	// several properties are read out properly.
	public void testDiagnosisSimpleState() throws Exception{
		diag.getProperties().setProperty(Property.HIDE_IN_DIALOG, new Boolean(true));
		
		Properties props = diag.getProperties();
		
		XMLTag shouldTag = new XMLTag("Property");
		shouldTag.addAttribute("name", "hide_in_dialog");
		// old: shouldTag.addAttribute("descriptor", "hide_in_dialog");
		shouldTag.addAttribute("class", "java.lang.Boolean");
		shouldTag.setContent("true");
		
		isTag = new XMLTag(pdh.write(props, Util.createEmptyDocument()).getFirstChild());
		
		assertEquals("(0)", shouldTag, isTag);
	}
}
