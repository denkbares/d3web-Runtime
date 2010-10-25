/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.core.manage.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.manage.KnowledgeBaseManagement;

/**
 * This test class checks the correct management of init questionnaires as done
 * by a {@link KnowledgeBase} instance.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 25.10.2010
 */
public class InitQuestionnairesTest {

	QContainer qcontainer1, qcontainer2, qcontainer3;
	Question question1, question2, question3;
	KnowledgeBaseManagement kbm;

	@Before
	public void setUp() throws Exception {
		kbm = KnowledgeBaseManagement.createInstance();
		qcontainer1 = kbm.createQContainer("qcontainer1");
		question1 = kbm.createQuestionNum("question1", qcontainer1);

		qcontainer2 = kbm.createQContainer("qcontainer2");
		question2 = kbm.createQuestionNum("question2", qcontainer2);

		qcontainer3 = kbm.createQContainer("qcontainer3");
		question3 = kbm.createQuestionNum("question3", qcontainer3);
	}

	@Test
	public void testKnowledgeBaseCreation() {
		KnowledgeBase knowledge = kbm.getKnowledgeBase();
		List<QContainer> qcontainers = knowledge.getQContainers();

		// including the root there should be 4 qcontainers
		assertThat(qcontainers.size(), is(4));

		QContainer qc1 = knowledge.searchQContainers("qcontainer1");
		assertThat(qc1.getName(), is("qcontainer1"));

		QContainer qc2 = knowledge.searchQContainers("qcontainer2");
		assertThat(qc2.getName(), is("qcontainer2"));

		QContainer qc3 = knowledge.searchQContainers("qcontainer3");
		assertThat(qc3.getName(), is("qcontainer3"));
	}

	@Test
	public void testSettingOfInitQContainers() {
		KnowledgeBase knowledge = kbm.getKnowledgeBase();
		List<QContainer> initQuestions = Arrays.asList(qcontainer3, qcontainer1);
		knowledge.setInitQuestions(initQuestions);

		List<QASet> storedContainers = knowledge.getInitQuestions();
		assertEquals(initQuestions, storedContainers);
	}

	@Test
	public void testAddAndRemoveOfInitQContainers() {
		KnowledgeBase knowledge = kbm.getKnowledgeBase();

		// we want to create the list (qcontainer3,qcontainer1)
		knowledge.addInitQuestion(qcontainer1, 2);
		knowledge.addInitQuestion(qcontainer3, 1);

		List<QContainer> expectedQuestions = Arrays.asList(qcontainer3, qcontainer1);
		List<QASet> storedContainers = knowledge.getInitQuestions();
		assertEquals(expectedQuestions, storedContainers);

		knowledge.removeInitQuestion(qcontainer3);
		expectedQuestions = Arrays.asList(qcontainer1);
		storedContainers = knowledge.getInitQuestions();
		assertEquals(expectedQuestions, storedContainers);

		knowledge.addInitQuestion(qcontainer2, 3);
		expectedQuestions = Arrays.asList(qcontainer1, qcontainer2);
		storedContainers = knowledge.getInitQuestions();
		assertEquals(expectedQuestions, storedContainers);
	}
}
