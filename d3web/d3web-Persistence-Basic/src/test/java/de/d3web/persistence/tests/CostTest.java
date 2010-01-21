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

import org.w3c.dom.Document;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.core.kpers.fragments.CostKBHandler;
import de.d3web.core.kpers.utilities.CostObject;
import de.d3web.core.kpers.utilities.Util;
import de.d3web.persistence.tests.utils.XMLTag;


/**
 * @author merz
 */

public class CostTest extends TestCase {

	private CostObject mco1;
	private CostKBHandler ch;
	
	private XMLTag isTag;
	private XMLTag shouldTag;
	
	public CostTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(CostTest.suite());
	}
	
	public static Test suite() {
		return new TestSuite(CostTest.class);
	}	
	
	protected void setUp(){
		
		mco1 = new CostObject("timeexpenditure", "Minuten", "Arztzeit");
		
		ch = new CostKBHandler();
	}
	
	public void testOneCost() throws Exception{
		
		shouldTag = new XMLTag("Cost");	
		shouldTag.addAttribute("ID", "timeexpenditure");
		
//		shouldTag.addAttribute("verbalization", "Minuten");
		XMLTag var1 = new XMLTag("Verbalization");
		var1.setContent("Minuten");
		shouldTag.addChild(var1);

//		shouldTag.addAttribute("unit", "Arztzeit");
		XMLTag var2 = new XMLTag("Unit");
		var2.setContent("Arztzeit");
		shouldTag.addChild(var2);
		
//		String xml = shouldTag.toString();
		
		Document doc = Util.createEmptyDocument();
		isTag = new XMLTag(ch.write(mco1, doc));

		assertEquals("(0)", shouldTag, isTag);	
	}
}
