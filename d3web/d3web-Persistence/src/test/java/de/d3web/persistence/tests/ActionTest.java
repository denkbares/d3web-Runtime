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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.Score;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerDate;
import de.d3web.kernel.domainModel.answers.AnswerNo;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.answers.AnswerText;
import de.d3web.kernel.domainModel.answers.AnswerYes;
import de.d3web.kernel.domainModel.formula.FormulaDateElement;
import de.d3web.kernel.domainModel.formula.FormulaDateExpression;
import de.d3web.kernel.domainModel.formula.FormulaExpression;
import de.d3web.kernel.domainModel.formula.FormulaNumber;
import de.d3web.kernel.domainModel.formula.Today;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.kernel.domainModel.qasets.QuestionDate;
import de.d3web.kernel.domainModel.qasets.QuestionMC;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.psMethods.contraIndication.ActionContraIndication;
import de.d3web.kernel.psMethods.heuristic.ActionHeuristicPS;
import de.d3web.kernel.psMethods.nextQASet.ActionClarify;
import de.d3web.kernel.psMethods.nextQASet.ActionIndication;
import de.d3web.kernel.psMethods.nextQASet.ActionRefine;
import de.d3web.kernel.psMethods.questionSetter.ActionAddValue;
import de.d3web.kernel.psMethods.questionSetter.ActionSetValue;
import de.d3web.kernel.psMethods.suppressAnswer.ActionSuppressAnswer;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.persistence.tests.utils.XMLTagUtils;
import de.d3web.persistence.xml.writers.actions.ActionAddValueWriter;
import de.d3web.persistence.xml.writers.actions.ActionClarificationWriter;
import de.d3web.persistence.xml.writers.actions.ActionContraIndicationWriter;
import de.d3web.persistence.xml.writers.actions.ActionHeuristicPSWriter;
import de.d3web.persistence.xml.writers.actions.ActionIndicationWriter;
import de.d3web.persistence.xml.writers.actions.ActionRefineWriter;
import de.d3web.persistence.xml.writers.actions.ActionSetValueWriter;
import de.d3web.persistence.xml.writers.actions.ActionSuppressAnswerWriter;

/**
 * @author merz
 */

public class ActionTest extends TestCase {
	
	private RuleComplex rcomp;
	
	private AnswerChoice ac1;
	private AnswerChoice ac2;
	private AnswerText atext1;
	private AnswerNum anum1;
	private AnswerDate adate1;
	private Date date = new Date();
	
	private QuestionChoice quest1;
	private QuestionNum qnum1;
	private QuestionDate qdate1;
	
	private Diagnosis diag1;
	private QContainer qcon1;
	
	private String xmlcode;
	
	private XMLTag isTag;
	private XMLTag shouldTag;
	private XMLTag shouldTagAdd;
	private XMLTag shouldTagSet;
	
	public ActionTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ActionTest.suite());
	}
	
	public static Test suite() {
		return new TestSuite(ActionTest.class);
	}
	
	protected void setUp() {
		
		ac1 = new AnswerNo();
		ac1.setText("a1-text");
		ac1.setId("ac1-id");
		
		ac2 = new AnswerYes();
		ac2.setText("a2-text");
		ac2.setId("ac2-id");
		
		atext1 = new AnswerText();
		atext1.setText("a1-text");
		
		anum1 = new AnswerNum();
		anum1.setValue(new Double (10));
		
		adate1 = new AnswerDate();
		adate1.setValue(date);
		
		quest1 = new QuestionMC();
		quest1.setId("q1-id");
		
		qnum1 = new QuestionNum();
		qnum1.setId("qnum1-id");

		qdate1 = new QuestionDate();
		qdate1.setId("qdate1-id");

		diag1 = new Diagnosis();
		diag1.setId("diag1-id");
		diag1.setText("diag1-text");
		
		qcon1 = new QContainer();
		qcon1.setId("qcon1-id");
		
		rcomp = new RuleComplex();
	}
	
	public void testActionSuppressAnswer() throws Exception{
		
		List suppressList = new LinkedList();
		suppressList.add(ac1);
		suppressList.add(ac2);
		
		ActionSuppressAnswer asa = new ActionSuppressAnswer(rcomp);
		asa.setQuestion(quest1);
		asa.setSuppress(suppressList);
	
		
		shouldTag = new XMLTag("Action");
		shouldTag.addAttribute("type", "ActionSuppressAnswer");
		
		XMLTag question = new XMLTag("Question");
		question.addAttribute("ID","q1-id");
		
		XMLTag suppress = new XMLTag("Suppress");
		XMLTag answer1 = new XMLTag("Answer");
		answer1.addAttribute("ID","ac1-id");
		XMLTag answer2 = new XMLTag("Answer");
		answer2.addAttribute("ID","ac2-id");
		suppress.addChild(answer1);
		suppress.addChild(answer2);
		
		shouldTag.addChild(question);
		shouldTag.addChild(suppress);
		
		ActionSuppressAnswerWriter asaw = new ActionSuppressAnswerWriter();
		xmlcode = asaw.getXMLString(asa);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Action", 0));
		assertEquals("(0)", shouldTag, isTag);
	}

	public void testActionClarification() throws Exception{
		List clarifyList = new LinkedList();
		clarifyList.add(quest1);
		clarifyList.add(qcon1);
		
		ActionClarify acl = new ActionClarify(rcomp);
		acl.setTarget(diag1);
		acl.setQASets(clarifyList);
		
		
		shouldTag = new XMLTag("Action");
		shouldTag.addAttribute("type", "ActionClarify");
		
		XMLTag target = new XMLTag("targetDiagnosis");
		target.addAttribute("ID","diag1-id");
		
		XMLTag targets = new XMLTag("TargetQASets");
		XMLTag qa1 = new XMLTag("QASet");
		qa1.addAttribute("ID","q1-id");
		XMLTag qa2 = new XMLTag("QASet");
		qa2.addAttribute("ID","qcon1-id");
		targets.addChild(qa1);
		targets.addChild(qa2);
		
		shouldTag.addChild(target);
		shouldTag.addChild(targets);
			
		ActionClarificationWriter acw = new ActionClarificationWriter();
		xmlcode = acw.getXMLString(acl);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Action", 0));
		assertEquals("(1)", shouldTag, isTag);
	}
	
	public void testActionRefine() throws Exception{
		List refineList = new LinkedList();
		refineList.add(qcon1);
		refineList.add(quest1);
		
		ActionRefine are = new ActionRefine(rcomp);
		are.setTarget(diag1);
		are.setQASets(refineList);
		
		shouldTag = new XMLTag("Action");
		shouldTag.addAttribute("type", "ActionRefine");
		
		XMLTag target = new XMLTag("targetDiagnosis");
		target.addAttribute("ID","diag1-id");
		
		XMLTag targets = new XMLTag("TargetQASets");
		XMLTag qa1 = new XMLTag("QASet");
		qa1.addAttribute("ID","qcon1-id");
		XMLTag qa2 = new XMLTag("QASet");
		qa2.addAttribute("ID","q1-id");
		targets.addChild(qa1);
		targets.addChild(qa2);
		
		shouldTag.addChild(target);
		shouldTag.addChild(targets);
			
		ActionRefineWriter arw = new ActionRefineWriter();
		xmlcode = arw.getXMLString(are);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Action", 0));
		assertEquals("(2)", shouldTag, isTag);
	}
	
	public void testActionIndication() throws Exception{
		List indicationList = new LinkedList();
		indicationList.add(qcon1);
		indicationList.add(quest1);
		
		ActionIndication ai = new ActionIndication(rcomp);
		ai.setQASets(indicationList);
		
		shouldTag = new XMLTag("Action");
		shouldTag.addAttribute("type", "ActionIndication");		
		
		XMLTag targets = new XMLTag("TargetQASets");
		XMLTag qa1 = new XMLTag("QASet");
		qa1.addAttribute("ID","qcon1-id");
		XMLTag qa2 = new XMLTag("QASet");
		qa2.addAttribute("ID","q1-id");
		targets.addChild(qa1);
		targets.addChild(qa2);
		
		shouldTag.addChild(targets);
			
		ActionIndicationWriter aiw = new ActionIndicationWriter();
		xmlcode = aiw.getXMLString(ai);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Action", 0));
		assertEquals("(3)", shouldTag, isTag);
	}
	
	public void testActionContraIndication() throws Exception{
		List contraindicationList = new LinkedList();
		contraindicationList.add(quest1);
		contraindicationList.add(qcon1);
		
		ActionContraIndication aci = new ActionContraIndication(rcomp);
		aci.setQASets(contraindicationList);
		
		shouldTag = new XMLTag("Action");
		shouldTag.addAttribute("type", "ActionContraIndication");		
		
		XMLTag targets = new XMLTag("TargetQASets");
		XMLTag qa1 = new XMLTag("QASet");
		qa1.addAttribute("ID","q1-id");
		XMLTag qa2 = new XMLTag("QASet");
		qa2.addAttribute("ID","qcon1-id");
		targets.addChild(qa1);
		targets.addChild(qa2);
		
		shouldTag.addChild(targets);
			
		ActionContraIndicationWriter aciw = new ActionContraIndicationWriter();
		xmlcode = aciw.getXMLString(aci);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Action", 0));
		assertEquals("(4)", shouldTag, isTag);
	}
	
	public void testActionHeuristicPS() throws Exception{
		ActionHeuristicPS ah = new ActionHeuristicPS(rcomp);
		ah.setDiagnosis(diag1);
		ah.setScore(Score.P1);
		
		shouldTag = new XMLTag("Action");
		shouldTag.addAttribute("type", "ActionHeuristicPS");		
		
		XMLTag score = new XMLTag("Score");
		score.addAttribute("value","P1");
		
		XMLTag diagnosis = new XMLTag("Diagnosis");
		diagnosis.addAttribute("ID","diag1-id");
		
		shouldTag.addChild(score);
		shouldTag.addChild(diagnosis);
			
		ActionHeuristicPSWriter ahw = new ActionHeuristicPSWriter();
		xmlcode = ahw.getXMLString(ah);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Action", 0));
		assertEquals("(5)", shouldTag, isTag);
	}
	
	public void testActionSetValueAndActionAddValue() throws Exception {
		FormulaNumber fn = new FormulaNumber(new Double(13));	
		FormulaExpression fe = new FormulaExpression(qnum1,fn);
		
		List setValueList = new LinkedList();
		setValueList.add(ac1);
		setValueList.add(fe);
		setValueList.add(ac2);
		
		ActionAddValue aav = new ActionAddValue(rcomp);
		aav.setQuestion(quest1);
		aav.setValues(setValueList.toArray());
		
		ActionSetValue asv = new ActionSetValue(rcomp);
		asv.setQuestion(quest1);
		asv.setValues(setValueList.toArray());				
		
		shouldTagAdd = new XMLTag("Action");
		shouldTagAdd.addAttribute("type", "ActionAddValue");		
		
		shouldTagSet = new XMLTag("Action");
		shouldTagSet.addAttribute("type", "ActionSetValue");
		
		XMLTag questTag = new XMLTag("Question");
		questTag.addAttribute("ID","q1-id");
		
		XMLTag valuesTag = new XMLTag("Values");
			
			// answer-choice1
			XMLTag ac1Tag = new XMLTag("Value");
			ac1Tag.addAttribute("type","answer");
			ac1Tag.addAttribute("ID","ac1-id");
			
			// formula-expression
			XMLTag fe1Tag = new XMLTag("Value");
			fe1Tag.addAttribute("type","evaluatable");
			
			XMLTag formulaExp = new XMLTag("FormulaExpression");
			
			XMLTag questNumTag = new XMLTag("QuestionNum");
			questNumTag.addAttribute("ID","qnum1-id");
			
			XMLTag formulaNum = new XMLTag("FormulaPrimitive");
			formulaNum.addAttribute("type","FormulaNumber");
			XMLTag value = new XMLTag("Value");
			value.setContent("13.0");
			formulaNum.addChild(value);
			
			formulaExp.addChild(questNumTag);
			formulaExp.addChild(formulaNum);
			
			fe1Tag.addChild(formulaExp);
			
			// answer-choice2
			XMLTag ac2Tag = new XMLTag("Value");
			ac2Tag.addAttribute("type","answer");
			ac2Tag.addAttribute("ID","ac2-id");
			
		valuesTag.addChild(ac1Tag);
		valuesTag.addChild(fe1Tag);
		valuesTag.addChild(ac2Tag);

		shouldTagAdd.addChild(questTag);
		shouldTagAdd.addChild(valuesTag);
		
		shouldTagSet.addChild(questTag);
		shouldTagSet.addChild(valuesTag);
		
		ActionAddValueWriter aavw = new ActionAddValueWriter();
		xmlcode = aavw.getXMLString(aav);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Action", 0));
		
		// it is absolute nonsense that xml code must have the same spaces newlines tabs etc.
		// (ok when it's a textnode then it's no nonsense but this did not work because of newlines between tags ...) 
		// assertEquals("(6)", removeAllSpaces(shouldTagAdd), removeAllSpaces(isTag));
		assertEquals("(6)", shouldTagAdd, isTag);
		
		ActionSetValueWriter asvw = new ActionSetValueWriter();
		xmlcode = asvw.getXMLString(asv);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Action", 0));
		assertEquals("(7)", shouldTagSet, isTag);
	}
	
	
	
	
	public void testActionSetValueAndActionAddValueDate() throws Exception {
		FormulaDateElement fn = new Today(new FormulaNumber(new Double(13)));	
		FormulaDateExpression fe = new FormulaDateExpression(qdate1,fn);
		
		List setValueList = new LinkedList();
		setValueList.add(fe);
		
		ActionAddValue aav = new ActionAddValue(rcomp);
		aav.setQuestion(qdate1);
		aav.setValues(setValueList.toArray());
		
		ActionSetValue asv = new ActionSetValue(rcomp);
		asv.setQuestion(qdate1);
		asv.setValues(setValueList.toArray());				
		
		shouldTagAdd = new XMLTag("Action");
		shouldTagAdd.addAttribute("type", "ActionAddValue");		
		
		shouldTagSet = new XMLTag("Action");
		shouldTagSet.addAttribute("type", "ActionSetValue");
		
		XMLTag questTag = new XMLTag("Question");
		questTag.addAttribute("ID","qdate1-id");
		
		XMLTag valuesTag = new XMLTag("Values");
				
			// formula-expression
			XMLTag fe1Tag = new XMLTag("Value");
			fe1Tag.addAttribute("type","evaluatable");
			
			XMLTag formulaExp = new XMLTag("FormulaDateExpression");
			
			XMLTag questDateTag = new XMLTag("Question");
			questDateTag.addAttribute("ID","qdate1-id");
			
			XMLTag today = new XMLTag("Today");
			XMLTag number = new XMLTag("FormulaPrimitive");
			number.addAttribute("type", "FormulaNumber");
			XMLTag value = new XMLTag("Value");
			value.setContent("13.0");
			number.addChild(value);
			today.addChild(number);
			
			formulaExp.addChild(questDateTag);
			formulaExp.addChild(today);
			
			fe1Tag.addChild(formulaExp);
			
		valuesTag.addChild(fe1Tag);

		shouldTagAdd.addChild(questTag);
		shouldTagAdd.addChild(valuesTag);
		
		shouldTagSet.addChild(questTag);
		shouldTagSet.addChild(valuesTag);
		
		ActionAddValueWriter aavw = new ActionAddValueWriter();
		xmlcode = aavw.getXMLString(aav);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Action", 0));
		
		// it is absolute nonsense that xml code must have the same spaces newlines tabs etc.
		// (ok when it's a textnode then it's no nonsense but this did not work because of newlines between tags ...) 
		// assertEquals("(6)", removeAllSpaces(shouldTagAdd), removeAllSpaces(isTag));
		assertEquals("(date1)", shouldTagAdd, isTag);
		
		ActionSetValueWriter asvw = new ActionSetValueWriter();
		xmlcode = asvw.getXMLString(asv);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Action", 0));
		assertEquals("(date2)", shouldTagSet, isTag);
	}	
	
	/**
	 * Method removeAllSpaces.
	 * @param isTag
	 * @return Object
	 */
	private Object removeAllSpaces(XMLTag isTag) {
		String result = isTag.toString();
		result = result.replaceAll("\\s", "");
		return result;
	}
}
