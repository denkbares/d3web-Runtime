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

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.CondDState;
import de.d3web.core.inference.condition.CondNumEqual;
import de.d3web.core.io.KnowledgeBasePersistence;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.fragments.RuleHandler;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.Score;
import de.d3web.scoring.inference.PSMethodHeuristic;

import static org.junit.Assert.assertEquals;

/**
 * @author merz
 */
public class RuleComplexTest {

	private Rule rcomp;
	private RuleHandler rcw;

	private ActionHeuristicPS ah;
	private Solution diag1;
	private CondNumEqual cNumL1;
	private CondDState cDState1;
	private QuestionNum qnum1;

	private XMLTag isTag;
	private XMLTag shouldTag;

	private Persistence<KnowledgeBase> persistence;

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = new KnowledgeBase();
		persistence = new KnowledgeBasePersistence(PersistenceManager.getInstance(), kb);

		qnum1 = new QuestionNum(kb, "qnum1-id");
		diag1 = new Solution(kb, "diag1-id");

		cDState1 = new CondDState(diag1, new Rating(Rating.State.EXCLUDED));

		cNumL1 = new CondNumEqual(qnum1, 12.7);

		rcomp = new Rule(PSMethodHeuristic.class);
		ah = new ActionHeuristicPS();
		rcomp.setAction(ah);
		ah.setSolution(diag1);
		ah.setScore(Score.P1);

		rcw = new RuleHandler();
	}

	@Test
	public void testRuleComplexSimple() throws Exception {

		rcomp = new Rule(PSMethodHeuristic.class);
		rcomp.setCondition(cDState1);
		rcomp.setException(cNumL1);
		rcomp.setAction(ah);

		shouldTag = new XMLTag("KnowledgeSlice");
		shouldTag.addAttribute("type", "RuleComplex");

		XMLTag actionTag1 = new XMLTag("Action");
		actionTag1.addAttribute("type", "ActionHeuristicPS");
		XMLTag diagTag = new XMLTag("Diagnosis");
		diagTag.addAttribute("name", "diag1-id");
		XMLTag scoreTag = new XMLTag("Score");
		scoreTag.addAttribute("value", "P1");
		actionTag1.addChild(diagTag);
		actionTag1.addChild(scoreTag);
		shouldTag.addChild(actionTag1);

		XMLTag conditionTag1 = new XMLTag("Condition");
		conditionTag1.addAttribute("type", "DState");
		conditionTag1.addAttribute("name", "diag1-id");
		conditionTag1.addAttribute("value", "EXCLUDED");
		shouldTag.addChild(conditionTag1);

		XMLTag exceptionTag2 = new XMLTag("Exception");
		XMLTag exceptionTag1 = new XMLTag("Condition");
		exceptionTag1.addAttribute("type", "numEqual");
		exceptionTag1.addAttribute("name", "qnum1-id");
		exceptionTag1.addAttribute("value", "12.7");
		exceptionTag2.addChild(exceptionTag1);
		shouldTag.addChild(exceptionTag2);

		isTag = new XMLTag(rcw.write(rcomp, persistence));

		assertEquals("(0)", shouldTag, isTag);
	}

	@Test
	public void testRuleComplexWithoutAction() throws Exception {

		rcomp = new Rule(null);
		rcomp.setCondition(cDState1);
		rcomp.setException(cNumL1);
		rcomp.setAction(null);

		shouldTag = new XMLTag("KnowledgeSlice");
		shouldTag.addAttribute("type", "RuleComplex");

		XMLTag conditionTag1 = new XMLTag("Condition");
		conditionTag1.addAttribute("type", "DState");
		conditionTag1.addAttribute("name", "diag1-id");
		conditionTag1.addAttribute("value", "EXCLUDED");
		shouldTag.addChild(conditionTag1);

		XMLTag exceptionTag2 = new XMLTag("Exception");
		XMLTag exceptionTag1 = new XMLTag("Condition");
		exceptionTag1.addAttribute("type", "numEqual");
		exceptionTag1.addAttribute("name", "qnum1-id");
		exceptionTag1.addAttribute("value", "12.7");
		exceptionTag2.addChild(exceptionTag1);
		shouldTag.addChild(exceptionTag2);

		isTag = new XMLTag(rcw.write(rcomp, persistence));

		assertEquals("(1)", shouldTag, isTag);
	}

	@Test
	public void testRuleComplexWithoutCondition() throws Exception {

		rcomp = new Rule(PSMethodHeuristic.class);
		rcomp.setCondition(null);
		rcomp.setException(cNumL1);
		rcomp.setAction(ah);

		shouldTag = new XMLTag("KnowledgeSlice");
		shouldTag.addAttribute("type", "RuleComplex");

		XMLTag actionTag1 = new XMLTag("Action");
		actionTag1.addAttribute("type", "ActionHeuristicPS");
		XMLTag diagTag = new XMLTag("Diagnosis");
		diagTag.addAttribute("name", "diag1-id");
		XMLTag scoreTag = new XMLTag("Score");
		scoreTag.addAttribute("value", "P1");
		actionTag1.addChild(diagTag);
		actionTag1.addChild(scoreTag);
		shouldTag.addChild(actionTag1);

		XMLTag exceptionTag2 = new XMLTag("Exception");
		XMLTag exceptionTag1 = new XMLTag("Condition");
		exceptionTag1.addAttribute("type", "numEqual");
		exceptionTag1.addAttribute("name", "qnum1-id");
		exceptionTag1.addAttribute("value", "12.7");
		exceptionTag2.addChild(exceptionTag1);
		shouldTag.addChild(exceptionTag2);

		isTag = new XMLTag(rcw.write(rcomp, persistence));

		assertEquals("(2)", shouldTag, isTag);
	}

	@Test
	public void testRuleComplexWithoutException() throws Exception {

		rcomp = new Rule(PSMethodHeuristic.class);
		rcomp.setCondition(cDState1);
		rcomp.setException(null);
		rcomp.setAction(ah);

		shouldTag = new XMLTag("KnowledgeSlice");
		shouldTag.addAttribute("type", "RuleComplex");

		XMLTag actionTag1 = new XMLTag("Action");
		actionTag1.addAttribute("type", "ActionHeuristicPS");
		XMLTag diagTag = new XMLTag("Diagnosis");
		diagTag.addAttribute("name", "diag1-id");
		XMLTag scoreTag = new XMLTag("Score");
		scoreTag.addAttribute("value", "P1");
		actionTag1.addChild(diagTag);
		actionTag1.addChild(scoreTag);
		shouldTag.addChild(actionTag1);

		XMLTag conditionTag1 = new XMLTag("Condition");
		conditionTag1.addAttribute("type", "DState");
		conditionTag1.addAttribute("name", "diag1-id");
		conditionTag1.addAttribute("value", "EXCLUDED");
		shouldTag.addChild(conditionTag1);

		isTag = new XMLTag(rcw.write(rcomp, persistence));

		assertEquals("(3)", shouldTag, isTag);
	}
}
