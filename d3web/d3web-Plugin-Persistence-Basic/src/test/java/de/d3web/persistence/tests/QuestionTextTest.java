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

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.io.fragments.QuestionHandler;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.persistence.tests.utils.XMLTag;

/**
 * @author merz
 */
public class QuestionTextTest {

	private Question q1;
	private QuestionHandler qw;
	private XMLTag isTag;
	private XMLTag shouldTag;

	@Before
	public void setUp() {
		q1 = new QuestionText("q1");
		q1.setName("q1-text");

		qw = new QuestionHandler();

		shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("ID", "q1");
		shouldTag.addAttribute("type", "Text");
		XMLTag child = new XMLTag("Text");
		child.setContent("q1-text");
		shouldTag.addChild(child);
	}

	@Test
	public void testQuestionNumTestSimple() throws Exception {
		isTag = new XMLTag(qw.write(q1, Util.createEmptyDocument()));

		assertEquals("(0)", shouldTag, isTag);
	}
}
