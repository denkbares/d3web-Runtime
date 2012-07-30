package de.d3web.test.tests;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyManager;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.test.EmptyQuestionnaireTest;
import de.d3web.testing.Message;

/*
 * Copyright (C) 2012 denkbares GmbH
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

/**
 * Test the behvavior of the class EmptyQuestionnaireTest
 * 
 * @author jochenreutelshofer
 * @created 30.07.2012 
 */
public class EmptyQuestionnaireTestTester {
	

	@Test
	public void testEmptyQuestionnaireFalse() {
		KnowledgeBase kb = createKB();
		
		TerminologyManager mgr = new TerminologyManager(kb);
		QContainer nonEmptyQContainer = new QContainer(kb.getRootQASet(),"Non Empty Questionnaire");
		mgr.putTerminologyObject(nonEmptyQContainer);
		Question q = new QuestionNum(nonEmptyQContainer,"qnum");
		mgr.putTerminologyObject(q);
		
		EmptyQuestionnaireTest test = new EmptyQuestionnaireTest();
		Message execute = test.execute(kb, new String[]{});
		
		assertTrue(execute.getType().equals(Message.Type.SUCCESS));
	
	}

	@Test
	public void testEmptyQuestionnaireTrue() {
		KnowledgeBase kb = createKB();
		
		TerminologyManager mgr = new TerminologyManager(kb);
		mgr.putTerminologyObject(new QContainer(kb.getRootQASet(),"Empty Questionnaire"));
		
		EmptyQuestionnaireTest test = new EmptyQuestionnaireTest();
		Message execute = test.execute(kb, new String[]{});
		
		assertTrue(execute.getType().equals(Message.Type.FAILURE));
	
	}

	private KnowledgeBase createKB() {
		KnowledgeBase kb = new KnowledgeBase();
		QContainer root = new QContainer(kb, "q000");
		kb.setRootQASet(root);
		return kb;
	}
	
}
