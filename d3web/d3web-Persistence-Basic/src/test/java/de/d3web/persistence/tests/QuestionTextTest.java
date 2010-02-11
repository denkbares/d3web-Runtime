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
import de.d3web.core.kpers.fragments.QuestionHandler;
import de.d3web.core.kpers.utilities.Util;
import de.d3web.core.terminology.Question;
import de.d3web.core.terminology.QuestionText;
import de.d3web.persistence.tests.utils.XMLTag;

/**
 * @author merz
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class QuestionTextTest extends TestCase {

	private Question q1;
	private QuestionHandler qw;
	private XMLTag isTag;
	private XMLTag shouldTag;

	/**
	 * Constructor for QuestionTextTest.
	 * @param arg0
	 */
	public QuestionTextTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(QuestionTextTest.suite());
	}

	public static Test suite() {
		return new TestSuite(QuestionTextTest.class);
	}

	protected void setUp() {
		q1 = new QuestionText("q1");
		q1.setText("q1-text");
		
		qw = new QuestionHandler();

		shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("ID", "q1");
		shouldTag.addAttribute("type", "Text");
		XMLTag child = new XMLTag("Text");
		child.setContent("q1-text");
		shouldTag.addChild(child);
	}

	public void testQuestionNumTestSimple() throws Exception {
		isTag = new XMLTag(qw.write(q1, Util.createEmptyDocument()));

		assertEquals("(0)", shouldTag, isTag);
	}
}
