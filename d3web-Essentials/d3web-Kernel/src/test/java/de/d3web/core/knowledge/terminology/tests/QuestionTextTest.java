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

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionText;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit test for {@link QuestionText}
 *
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 24.08.2010
 */
public class QuestionTextTest {

	QuestionText questionText;
	private KnowledgeBase base;

	@Before
	public void setUp() throws Exception {
		this.base = new KnowledgeBase();
		this.questionText = new QuestionText(base, "questionText");
	}

	@Test
	public void testGetterSetterHeightWidth() {
		assertNotNull(base.getManager().searchQuestion("questionText"));
		assertEquals(0, questionText.getParents().length);
	}
}
