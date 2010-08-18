/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.kernel.verbalizer.test;

import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.TestCase;
import de.d3web.ka.data.DataManager;
import de.d3web.ka.factories.ConditionFactory;
import de.d3web.ka.factories.RuleToHTML;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.KnowledgeBaseManagement;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.qasets.QuestionOC;
import de.d3web.kernel.domainModel.qasets.QuestionText;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.verbalizer.VerbalizationManager;
import de.d3web.kernel.verbalizer.Verbalizer;
import de.d3web.kernel.verbalizer.VerbalizationManager.RenderingFormat;

public class TestConditionVerbalizer extends TestCase {
	// private Diagnosis rootDiagnosis;
	private QASet rootQASet;
	private KnowledgeBaseManagement kbm;
	
	private static final boolean verbose = true;

	protected void setUp() throws Exception {
		// setup basics
		kbm = KnowledgeBaseManagement.createInstance();
		DataManager.getInstance().createNewKB();
		KnowledgeBase kb = DataManager.getInstance().getBase();
		// rootDiagnosis = kb.getRootDiagnosis();
		rootQASet = kb.getRootQASet();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	// compares the output of the ConditionVerbalizer woth old RuleToHTML output
	public void testVerbalize() {
		// test create some Questions and Conditions
		QuestionText q1 = kbm.createQuestionText("Testfrageblablubb?", rootQASet);
		AbstractCondition c1 = ConditionFactory.createCondTextContains(q1, "Test");

		QuestionOC q2 = kbm.createQuestionOC("OCFrage", rootQASet, new String[] { "oc-Antwort 1", "oc-Antwort 2" });
		AbstractCondition c2 = ConditionFactory.createCondEqual(q2, q2.getAllAlternatives().get(0));

		// Test standard ConditionVerbalization
		assertEquals(VerbalizationManager.getInstance().verbalize(c2, RenderingFormat.HTML), RuleToHTML
				.createHTMLfromCondition(c2));
		assertEquals(VerbalizationManager.getInstance().verbalize(c1, RenderingFormat.HTML), RuleToHTML
				.createHTMLfromCondition(c1));

		// create a Non-Terminal-Condition and Test it
		AbstractCondition c3 = ConditionFactory.createCondTextContains(q1, "Test2");
		ArrayList<AbstractCondition> list = new ArrayList<AbstractCondition>();
		list.add(c1);
		list.add(c2);
		list.add(c3);

		// Test some conditions
		AbstractCondition orCond = ConditionFactory.createCondOr(list);
		assertEquals(VerbalizationManager.getInstance().verbalize(orCond, RenderingFormat.HTML), RuleToHTML
				.createHTMLfromCondition(orCond));

		AbstractCondition andCond = ConditionFactory.createCondAnd(list);
		assertEquals(VerbalizationManager.getInstance().verbalize(andCond, RenderingFormat.HTML), RuleToHTML
				.createHTMLfromCondition(andCond));

		AbstractCondition notCond = ConditionFactory.createCondNot(orCond);
		assertEquals(VerbalizationManager.getInstance().verbalize(notCond, RenderingFormat.HTML), RuleToHTML
				.createHTMLfromCondition(notCond));

		AbstractCondition nOfMCond = ConditionFactory.createCondMofN(list, 2, 3);
		assertEquals(VerbalizationManager.getInstance().verbalize(nOfMCond, RenderingFormat.HTML), RuleToHTML
				.createHTMLfromCondition(nOfMCond));

		// test some parameter
		HashMap<String, Object> parameter = new HashMap<String, Object>();
		parameter.put(Verbalizer.IS_SINGLE_LINE, true);

		assertEquals(VerbalizationManager.getInstance().verbalize(orCond, RenderingFormat.HTML, parameter), RuleToHTML
				.createHTMLfromCondition(orCond, true, false));
		assertEquals(VerbalizationManager.getInstance().verbalize(andCond, RenderingFormat.HTML, parameter), RuleToHTML
				.createHTMLfromCondition(andCond, true, false));
		assertEquals(VerbalizationManager.getInstance().verbalize(nOfMCond, RenderingFormat.HTML, parameter), RuleToHTML
				.createHTMLfromCondition(nOfMCond, true, false));
		assertEquals(VerbalizationManager.getInstance().verbalize(c3, RenderingFormat.HTML, parameter), RuleToHTML
				.createHTMLfromCondition(c3, true, false));
		
		parameter.put(Verbalizer.IS_NEGATIVE, true);
		
		assertEquals(VerbalizationManager.getInstance().verbalize(notCond, RenderingFormat.HTML, parameter), RuleToHTML
				.createHTMLfromCondition(notCond, true, true));
		assertEquals(VerbalizationManager.getInstance().verbalize(orCond, RenderingFormat.HTML, parameter), RuleToHTML
				.createHTMLfromCondition(orCond, true, true));
		assertEquals(VerbalizationManager.getInstance().verbalize(andCond, RenderingFormat.HTML, parameter), RuleToHTML
				.createHTMLfromCondition(andCond, true, true));
		assertEquals(VerbalizationManager.getInstance().verbalize(nOfMCond, RenderingFormat.HTML, parameter), RuleToHTML
				.createHTMLfromCondition(nOfMCond, true, true));
		assertEquals(VerbalizationManager.getInstance().verbalize(c3, RenderingFormat.HTML, parameter), RuleToHTML
				.createHTMLfromCondition(c3, true, true));

	
		if (verbose == true) {
			System.out.println(VerbalizationManager.getInstance().verbalize(notCond, RenderingFormat.HTML, parameter));
			System.out.println("=====");
			System.out.println(VerbalizationManager.getInstance().verbalize(orCond, RenderingFormat.HTML, parameter));
			System.out.println("=====");
			System.out.println(VerbalizationManager.getInstance().verbalize(nOfMCond, RenderingFormat.HTML, parameter));
			System.out.println("=====");
			System.out.println(VerbalizationManager.getInstance().verbalize(c3, RenderingFormat.HTML, parameter));
			System.out.println("=====");
	}
	
	
		
	}
}
