/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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

package de.d3web.core.knowledge.terminology.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionText;

/**
 * Unit test for {@link QuestionText}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 24.08.2010
 */
public class QuestionTextTest {

	QuestionText questionText;

	@Before
	public void setUp() throws Exception {
		questionText = new QuestionText(new KnowledgeBase(), "questionText");
	}

	@Test
	public void testGetterSetterHeightWidth() {
		// width and height are not initialized by the constructor
		assertThat(questionText.getHeight(), is(0));
		assertThat(questionText.getWidth(), is(0));
		// height and width cannot be set to negative values
		questionText.setHeight(-217);
		questionText.setWidth(-264);
		assertThat(questionText.getHeight(), is(0));
		assertThat(questionText.getWidth(), is(0));
		// set them, now to positive values
		questionText.setHeight(217);
		questionText.setWidth(264);
		// now utilize the getters
		assertThat(questionText.getHeight(), is(217));
		assertThat(questionText.getWidth(), is(264));
	}

}
