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

package de.d3web.interview;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.interview.inference.PSMethodInterview;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Unit tests for init-indications.
 * 
 * @author volker_belli
 * @created 10.03.2011
 */
public class InitQuestionnairesOrderTest {

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
	}

	@Test
	public void checkOrderOfInitQuestionnaires() {
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QASet root = kb.getRootQASet();
		// create a huge number of init-questionnaires
		for (int i = 0; i < 100; i++) {
			QContainer qc = new QContainer(root, "qc_" + i);
			new QuestionYN(qc, "only to be non-empty " + i);
			// not all shall become init ones
			if (i % 3 == 0) continue;
			kb.addInitQuestion(qc, (i * 2) % 101);
		}

		// check if init-question on agenda has same order than knowledge base
		Session session = SessionFactory.createSession(kb);
		List<InterviewObject> agenda = session.getSessionObject(
				session.getPSMethodInstance(PSMethodInterview.class)).getInterviewAgenda().getCurrentlyActiveObjects();
		List<QASet> initQuestions = kb.getInitQuestions();
		Assert.assertEquals(
				"order of init questions must be stable",
				initQuestions, agenda);
	}
}
