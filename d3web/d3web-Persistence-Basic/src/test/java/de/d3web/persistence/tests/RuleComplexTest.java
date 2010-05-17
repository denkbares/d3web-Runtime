/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.persistence.tests;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.CondDState;
import de.d3web.core.inference.condition.CondNumEqual;
import de.d3web.core.io.fragments.RuleHandler;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.Score;

/**
 * @author merz
 */
public class RuleComplexTest extends TestCase {
	private Rule rcomp;
	private RuleHandler rcw;
	
	private ActionHeuristicPS ah;
	private Solution diag1;
	private CondNumEqual cNumL1;
	private CondDState cDState1;
	private QuestionNum qnum1;
	
	
	
	private XMLTag isTag;
	private XMLTag shouldTag;
	/**
	 * Constructor for RuleComplexTest.
	 * @param arg0
	 */
	public RuleComplexTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(RuleComplexTest.suite());
	}
	
	public static Test suite() {
		return new TestSuite(RuleComplexTest.class);
	}
	
	protected void setUp() {
		try {
			InitPluginManager.init();
		}
		catch (IOException e1) {
			assertTrue("Error initialising plugin framework", false);
		}
		qnum1 = new QuestionNum("qnum1-id");
		diag1 = new Solution("diag1-id");
		diag1.setName("diag1-text");
		
		cDState1 = new CondDState(diag1, new Rating(Rating.State.EXCLUDED));
		
		cNumL1 =  new CondNumEqual(qnum1, new Double(12.7));
			
		rcomp = new Rule("d1");
		ah = new ActionHeuristicPS();
		rcomp.setAction(ah);
		ah.setDiagnosis(diag1);
		ah.setScore(Score.P1);
				
		rcw = new RuleHandler();
	}
	
	public void testRuleComplexSimple() throws Exception{
		
		rcomp = new Rule("rc-id1");
		rcomp.setCondition(cDState1);
		rcomp.setException(cNumL1);
		rcomp.setAction(ah);
		
		shouldTag = new XMLTag("KnowledgeSlice");
		shouldTag.addAttribute("ID", "rc-id1");
		shouldTag.addAttribute("type", "RuleComplex");
		
		XMLTag actionTag1 = new XMLTag("Action");
		actionTag1.addAttribute("type", "ActionHeuristicPS");
		XMLTag diagTag = new XMLTag("Diagnosis");
		diagTag.addAttribute("ID", "diag1-id");
		XMLTag scoreTag = new XMLTag("Score");
		scoreTag.addAttribute("value", "P1");
		actionTag1.addChild(diagTag);
		actionTag1.addChild(scoreTag);
		shouldTag.addChild(actionTag1);
		
		XMLTag conditionTag1 = new XMLTag("Condition");
		conditionTag1.addAttribute("type", "DState");
		conditionTag1.addAttribute("ID", "diag1-id");
		conditionTag1.addAttribute("value", "EXCLUDED");
		shouldTag.addChild(conditionTag1);
		
		XMLTag exceptionTag2 = new XMLTag("Exception");
		XMLTag exceptionTag1 = new XMLTag("Condition");
		exceptionTag1.addAttribute("type", "numEqual");
		exceptionTag1.addAttribute("ID","qnum1-id");
		exceptionTag1.addAttribute("value","12.7");
		exceptionTag2.addChild(exceptionTag1);
		shouldTag.addChild(exceptionTag2);
		
		isTag = new XMLTag(rcw.write(rcomp, Util.createEmptyDocument()));
		
		assertEquals("(0)", shouldTag, isTag);
	}
	
	public void testRuleComplexWithoutAction() throws Exception{
		
		rcomp = new Rule("rc-id1");
		rcomp.setCondition(cDState1);
		rcomp.setException(cNumL1);
		rcomp.setAction(null);
		
		shouldTag = new XMLTag("KnowledgeSlice");
		shouldTag.addAttribute("ID", "rc-id1");
		shouldTag.addAttribute("type", "RuleComplex");
		
		XMLTag conditionTag1 = new XMLTag("Condition");
		conditionTag1.addAttribute("type", "DState");
		conditionTag1.addAttribute("ID", "diag1-id");
		conditionTag1.addAttribute("value", "EXCLUDED");
		shouldTag.addChild(conditionTag1);
		
		XMLTag exceptionTag2 = new XMLTag("Exception");
		XMLTag exceptionTag1 = new XMLTag("Condition");
		exceptionTag1.addAttribute("type", "numEqual");
		exceptionTag1.addAttribute("ID","qnum1-id");
		exceptionTag1.addAttribute("value","12.7");
		exceptionTag2.addChild(exceptionTag1);
		shouldTag.addChild(exceptionTag2);
		
		isTag = new XMLTag(rcw.write(rcomp, Util.createEmptyDocument()));
		
		assertEquals("(1)", shouldTag, isTag);
	}
	
	public void testRuleComplexWithoutCondition() throws Exception{
		
		rcomp = new Rule("rc-id1");
		rcomp.setCondition(null);
		rcomp.setException(cNumL1);
		rcomp.setAction(ah);
		
		shouldTag = new XMLTag("KnowledgeSlice");
		shouldTag.addAttribute("ID", "rc-id1");
		shouldTag.addAttribute("type", "RuleComplex");
		
		XMLTag actionTag1 = new XMLTag("Action");
		actionTag1.addAttribute("type", "ActionHeuristicPS");
		XMLTag diagTag = new XMLTag("Diagnosis");
		diagTag.addAttribute("ID", "diag1-id");
		XMLTag scoreTag = new XMLTag("Score");
		scoreTag.addAttribute("value", "P1");
		actionTag1.addChild(diagTag);
		actionTag1.addChild(scoreTag);
		shouldTag.addChild(actionTag1);
		
		XMLTag exceptionTag2 = new XMLTag("Exception");
		XMLTag exceptionTag1 = new XMLTag("Condition");
		exceptionTag1.addAttribute("type", "numEqual");
		exceptionTag1.addAttribute("ID","qnum1-id");
		exceptionTag1.addAttribute("value","12.7");
		exceptionTag2.addChild(exceptionTag1);
		shouldTag.addChild(exceptionTag2);		
		
		isTag = new XMLTag(rcw.write(rcomp, Util.createEmptyDocument()));		
		
		assertEquals("(2)", shouldTag, isTag);
	}
	
	public void testRuleComplexWithoutException() throws Exception{
		
		rcomp = new Rule("rc-id1");
		rcomp.setCondition(cDState1);
		rcomp.setException(null);
		rcomp.setAction(ah);
		
		shouldTag = new XMLTag("KnowledgeSlice");
		shouldTag.addAttribute("ID", "rc-id1");
		shouldTag.addAttribute("type", "RuleComplex");
		
		XMLTag actionTag1 = new XMLTag("Action");
		actionTag1.addAttribute("type", "ActionHeuristicPS");
		XMLTag diagTag = new XMLTag("Diagnosis");
		diagTag.addAttribute("ID", "diag1-id");
		XMLTag scoreTag = new XMLTag("Score");
		scoreTag.addAttribute("value", "P1");
		actionTag1.addChild(diagTag);
		actionTag1.addChild(scoreTag);
		shouldTag.addChild(actionTag1);
		
		XMLTag conditionTag1 = new XMLTag("Condition");
		conditionTag1.addAttribute("type", "DState");
		conditionTag1.addAttribute("ID", "diag1-id");
		conditionTag1.addAttribute("value", "EXCLUDED");
		shouldTag.addChild(conditionTag1);
		
		isTag = new XMLTag(rcw.write(rcomp, Util.createEmptyDocument()));
		
		assertEquals("(3)", shouldTag, isTag);
	}
}
