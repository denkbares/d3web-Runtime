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
import de.d3web.kernel.supportknowledge.DCElement;
import de.d3web.kernel.supportknowledge.DCMarkup;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.persistence.tests.utils.XMLTagUtils;
import de.d3web.persistence.xml.writers.DCMarkupWriter;

/**
 * @author merz
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DCMarkupTest extends TestCase {

	private DCMarkup markup;
	private DCMarkupWriter dw;
	private String xmlcode;
	
	private XMLTag isTag;
	private XMLTag shouldTag;
	
	
	public DCMarkupTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DCMarkupTest.suite());
	}
	
	public static Test suite() {
		return new TestSuite(DCMarkupTest.class);
	}	
	
		
	protected void setUp(){
		markup = new DCMarkup();
		markup.setContent(DCElement.SOURCE, "value1");
		dw = DCMarkupWriter.getInstance();
	}
	
	public void testSimpleDescriptor() throws Exception{
		
		shouldTag = new XMLTag("DCElement");
		shouldTag.addAttribute("label", DCElement.SOURCE.getLabel());
		shouldTag.setContent("value1");
		
		xmlcode = dw.getXMLString(markup);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "DCElement", 0));

		assertEquals("(0)", shouldTag, isTag);	
	}
}
