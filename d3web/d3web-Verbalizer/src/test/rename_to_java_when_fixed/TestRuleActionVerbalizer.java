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
import java.util.List;

import junit.framework.TestCase;
import de.d3web.ka.data.DataManager;
import de.d3web.ka.factories.ConditionFactory;
import de.d3web.ka.factories.IDFactory;
import de.d3web.ka.factories.RuleToHTML;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.KnowledgeBaseManagement;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.RuleAction;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.RuleFactory;
import de.d3web.kernel.domainModel.Score;
import de.d3web.kernel.domainModel.qasets.QuestionOC;
import de.d3web.kernel.domainModel.qasets.QuestionText;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.verbalizer.VerbalizationManager;
import de.d3web.kernel.verbalizer.VerbalizationManager.RenderingFormat;

public class TestRuleActionVerbalizer extends TestCase {

	private QASet rootQASet;
	private KnowledgeBaseManagement kbm;
	private Diagnosis rootDiagnosis;

	protected void setUp() throws Exception {
		// setup basics
		kbm = KnowledgeBaseManagement.createInstance();
		DataManager.getInstance().createNewKB();
		KnowledgeBase kb = DataManager.getInstance().getBase();
		rootDiagnosis = kb.getRootDiagnosis();
		rootQASet = kb.getRootQASet();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	// compares the output of the RuleActionVerbalizer woth old RuleToHTML
	// output
	public void testVerbalize() {
		// create some conditions:
		QuestionText q1 = kbm.createQuestionText("Testfrageblablubb?", rootQASet);
		AbstractCondition c1 = ConditionFactory.createCondTextContains(q1, "Test");

		QuestionOC q2 = kbm.createQuestionOC("OCFrage", rootQASet, new String[] { "oc-Antwort 1",
				"oc-Antwort 2" });
		AbstractCondition c2 = ConditionFactory.createCondEqual(q2, q2.getAllAlternatives().get(0));

		List<QASet> qasets = new ArrayList<QASet>();
		qasets.add(q1);
		qasets.add(q2);

		// create a Diagnosis
		Diagnosis diag = kbm.createDiagnosis("diagName", rootDiagnosis);
		Score score = Score.P6;

		// ActionHeuristicPS
		String ruleID = IDFactory.createRuleId();
		RuleComplex rule = RuleFactory.createHeuristicPSRule(ruleID, diag, score, c1);
		RuleAction ra1 = rule.getAction();

		assertEquals(RuleToHTML.createHTMLfromAction(ra1, null), VerbalizationManager.getInstance()
				.verbalize(ra1, RenderingFormat.HTML));

		// ActionClarify
		ruleID = IDFactory.createRuleId();
		rule = RuleFactory.createClarificationRule(ruleID, qasets, diag, c2);
		RuleAction ra2 = rule.getAction();
		assertEquals(RuleToHTML.createHTMLfromAction(ra2, null), VerbalizationManager.getInstance()
				.verbalize(ra2, RenderingFormat.HTML));
	}
}
