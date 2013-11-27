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

/*
 * Created on 10.10.2003
 * 
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
package de.d3web.persistence.tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.io.KnowledgeBasePersistence;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.fragments.QuestionHandler;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.persistence.tests.utils.XMLTag;

/**
 * @author vogele
 */
public class QuestionDateTest {

	private QuestionDate q1;
	private QuestionHandler qw;
	private XMLTag isTag;
	private XMLTag shouldTag;

	private Persistence<KnowledgeBase> persistence;

	@Before
	public void setUp() throws IOException {
		KnowledgeBase kb = new KnowledgeBase();
		persistence = new KnowledgeBasePersistence(PersistenceManager.getInstance(), kb);

		q1 = new QuestionDate(kb, "q1");

		qw = new QuestionHandler();

		shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("name", "q1");
		shouldTag.addAttribute("type", "Date");
	}

	@Test
	public void testQuestionDate() throws Exception {
		isTag = new XMLTag(qw.write(q1, persistence));
		assertEquals("(0)", shouldTag, isTag);
	}

}
