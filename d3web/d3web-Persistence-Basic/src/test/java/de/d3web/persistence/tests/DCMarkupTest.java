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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.core.io.fragments.DCMarkupHandler;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.knowledge.terminology.info.DCElement;
import de.d3web.core.knowledge.terminology.info.DCMarkup;
import de.d3web.persistence.tests.utils.XMLTag;

/**
 * @author merz
 * 
 *         To change this generated comment edit the template variable
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class DCMarkupTest extends TestCase {

	private DCMarkup markup;
	private DCMarkupHandler dh;

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

	protected void setUp() {
		markup = new DCMarkup();
		markup.setContent(DCElement.SOURCE, "value1");
		dh = new DCMarkupHandler();
	}

	public void testSimpleDescriptor() throws Exception {

		shouldTag = new XMLTag("meta");
		shouldTag.addAttribute("name", DCElement.SOURCE.getLabel());
		shouldTag.addAttribute("content", "value1");

		isTag = new XMLTag(dh.write(markup, Util.createEmptyDocument()).getFirstChild());

		assertEquals("(0)", shouldTag, isTag);
	}
}
