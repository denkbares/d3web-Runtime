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

import java.util.HashMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.Score;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.domainModel.ruleCondition.CondDState;
import de.d3web.kernel.domainModel.ruleCondition.CondNumEqual;
import de.d3web.kernel.psMethods.heuristic.ActionHeuristicPS;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.persistence.tests.utils.XMLTagUtils;
import de.d3web.persistence.xml.writers.RuleComplexWriter;
import de.d3web.persistence.xml.writers.conditions.ConditionsPersistenceHandler;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondDStateWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondNumEqualWriter;

/**
 * @author merz
 */
public class RuleComplexTest extends TestCase {
	private RuleComplex rcomp;
	private RuleComplexWriter rcw;
	
	private ActionHeuristicPS ah;
	private Diagnosis diag1;
	private CondNumEqual cNumL1;
	private CondDState cDState1;
	private QuestionNum qnum1;
	
	private String xmlcode;
	
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
		ConditionsPersistenceHandler.getInstance().add(new CondDStateWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondNumEqualWriter());
		
		qnum1 = new QuestionNum();
		qnum1.setId("qnum1-id");
		
		diag1 = new Diagnosis();
		diag1.setId("diag1-id");
		diag1.setText("diag1-text");
		
		cDState1 = new CondDState(diag1, DiagnosisState.EXCLUDED, null);
		
		cNumL1 =  new CondNumEqual(qnum1, new Double(12.7));
			
		rcomp = new RuleComplex();
		rcomp.setId("d1");
		
		ah = new ActionHeuristicPS(rcomp);
		ah.setDiagnosis(diag1);
		ah.setScore(Score.P1);
				
		rcw = new RuleComplexWriter(new HashMap());
	}
	
	public void testRuleComplexSimple() throws Exception{
		
		rcomp = new RuleComplex();
		rcomp.setId("rc-id1");
		rcomp.setCondition(cDState1);
		rcomp.setException(cNumL1);
		rcomp.setAction(ah);
		
		shouldTag = new XMLTag("KnowledgeSlice");
		shouldTag.addAttribute("ID", "rc-id1");
		shouldTag.addAttribute("type", "RuleComplex");
		
		XMLTag actionTag1 = new XMLTag("Action");
		actionTag1.addAttribute("ID", "rc-id1");
		actionTag1.addAttribute("class", "class de.d3web.kernel.psMethods.heuristic.ActionHeuristicPS");
		shouldTag.addChild(actionTag1);
		
		XMLTag conditionTag1 = new XMLTag("Condition");
		conditionTag1.addAttribute("type", "DState");
		conditionTag1.addAttribute("ID", "diag1-id");
		conditionTag1.addAttribute("value", "excluded");
		shouldTag.addChild(conditionTag1);
		
		XMLTag exceptionTag2 = new XMLTag("Exception");
		XMLTag exceptionTag1 = new XMLTag("Condition");
		exceptionTag1.addAttribute("type", "numEqual");
		exceptionTag1.addAttribute("ID","qnum1-id");
		exceptionTag1.addAttribute("value","12.7");
		exceptionTag2.addChild(exceptionTag1);
		shouldTag.addChild(exceptionTag2);
		
		
		xmlcode = rcw.getXMLString(rcomp);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "KnowledgeSlice", 0));
		
		assertEquals("(0)", shouldTag, isTag);
	}
	
	public void testRuleComplexWithoutAction() throws Exception{
		
		rcomp = new RuleComplex();
		rcomp.setId("rc-id1");
		rcomp.setCondition(cDState1);
		rcomp.setException(cNumL1);
		rcomp.setAction(null);
		
		shouldTag = new XMLTag("KnowledgeSlice");
		shouldTag.addAttribute("ID", "rc-id1");
		shouldTag.addAttribute("type", "RuleComplex");
		
		XMLTag conditionTag1 = new XMLTag("Condition");
		conditionTag1.addAttribute("type", "DState");
		conditionTag1.addAttribute("ID", "diag1-id");
		conditionTag1.addAttribute("value", "excluded");
		shouldTag.addChild(conditionTag1);
		
		XMLTag exceptionTag2 = new XMLTag("Exception");
		XMLTag exceptionTag1 = new XMLTag("Condition");
		exceptionTag1.addAttribute("type", "numEqual");
		exceptionTag1.addAttribute("ID","qnum1-id");
		exceptionTag1.addAttribute("value","12.7");
		exceptionTag2.addChild(exceptionTag1);
		shouldTag.addChild(exceptionTag2);
		
		xmlcode = rcw.getXMLString(rcomp);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "KnowledgeSlice", 0));
		
		assertEquals("(1)", shouldTag, isTag);
	}
	
	public void testRuleComplexWithoutCondition() throws Exception{
		
		rcomp = new RuleComplex();
		rcomp.setId("rc-id1");
		rcomp.setCondition(null);
		rcomp.setException(cNumL1);
		rcomp.setAction(ah);
		
		shouldTag = new XMLTag("KnowledgeSlice");
		shouldTag.addAttribute("ID", "rc-id1");
		shouldTag.addAttribute("type", "RuleComplex");
		
		XMLTag actionTag1 = new XMLTag("Action");
		actionTag1.addAttribute("ID", "rc-id1");
		actionTag1.addAttribute("class", "class de.d3web.kernel.psMethods.heuristic.ActionHeuristicPS");
		shouldTag.addChild(actionTag1);
		
		XMLTag exceptionTag2 = new XMLTag("Exception");
		XMLTag exceptionTag1 = new XMLTag("Condition");
		exceptionTag1.addAttribute("type", "numEqual");
		exceptionTag1.addAttribute("ID","qnum1-id");
		exceptionTag1.addAttribute("value","12.7");
		exceptionTag2.addChild(exceptionTag1);
		shouldTag.addChild(exceptionTag2);		
		
		xmlcode = rcw.getXMLString(rcomp);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "KnowledgeSlice", 0));
		
		assertEquals("(2)", shouldTag, isTag);
	}
	
	public void testRuleComplexWithoutException() throws Exception{
		
		rcomp = new RuleComplex();
		rcomp.setId("rc-id1");
		rcomp.setCondition(cDState1);
		rcomp.setException(null);
		rcomp.setAction(ah);
		
		shouldTag = new XMLTag("KnowledgeSlice");
		shouldTag.addAttribute("ID", "rc-id1");
		shouldTag.addAttribute("type", "RuleComplex");
		
		XMLTag actionTag1 = new XMLTag("Action");
		actionTag1.addAttribute("ID", "rc-id1");
		actionTag1.addAttribute("class", "class de.d3web.kernel.psMethods.heuristic.ActionHeuristicPS");
		shouldTag.addChild(actionTag1);
		
		XMLTag conditionTag1 = new XMLTag("Condition");
		conditionTag1.addAttribute("type", "DState");
		conditionTag1.addAttribute("ID", "diag1-id");
		conditionTag1.addAttribute("value", "excluded");
		shouldTag.addChild(conditionTag1);
		
		xmlcode = rcw.getXMLString(rcomp);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "KnowledgeSlice", 0));
		
		assertEquals("(3)", shouldTag, isTag);
	}
}
