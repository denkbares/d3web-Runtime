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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.w3c.dom.Document;

import de.d3web.abstraction.ActionAddValue;
import de.d3web.abstraction.ActionSetValue;
import de.d3web.abstraction.formula.FormulaDateElement;
import de.d3web.abstraction.formula.FormulaDateExpression;
import de.d3web.abstraction.formula.FormulaExpression;
import de.d3web.abstraction.formula.FormulaNumber;
import de.d3web.abstraction.formula.Today;
import de.d3web.core.inference.Rule;
import de.d3web.core.io.fragments.actions.ContraIndicationActionHandler;
import de.d3web.core.io.fragments.actions.HeuristicPSActionHandler;
import de.d3web.core.io.fragments.actions.NextQASetActionHandler;
import de.d3web.core.io.fragments.actions.QuestionSetterActionHandler;
import de.d3web.core.io.fragments.actions.SuppressAnswerActionHandler;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.session.values.AnswerDate;
import de.d3web.core.session.values.AnswerNo;
import de.d3web.core.session.values.AnswerNum;
import de.d3web.core.session.values.AnswerText;
import de.d3web.core.session.values.AnswerYes;
import de.d3web.indication.ActionClarify;
import de.d3web.indication.ActionContraIndication;
import de.d3web.indication.ActionIndication;
import de.d3web.indication.ActionRefine;
import de.d3web.indication.ActionSuppressAnswer;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.Score;

/**
 * @author merz
 */

public class ActionTest extends TestCase {
	
	private Rule rcomp;
	
	private AnswerChoice ac1;
	private AnswerChoice ac2;
	private AnswerText atext1;
	private AnswerNum anum1;
	private AnswerDate adate1;
	private Date date = new Date();
	
	private QuestionChoice quest1;
	private QuestionNum qnum1;
	private QuestionDate qdate1;
	
	private Solution diag1;
	private QContainer qcon1;
	
	private XMLTag isTag;
	private XMLTag shouldTag;
	private XMLTag shouldTagAdd;
	private XMLTag shouldTagSet;
	
	Document doc; 
	
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
		try {
			InitPluginManager.init();
		}
		catch (IOException e1) {
			assertTrue("Error initialising plugin framework", false);
		}
		try {
			doc = Util.createEmptyDocument();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		ac1 = new AnswerNo("ac1-id");
		ac1.setText("a1-text");
		
		ac2 = new AnswerYes("ac2-id");
		ac2.setText("a2-text");
		
		atext1 = new AnswerText();
		atext1.setText("a1-text");
		
		anum1 = new AnswerNum();
		anum1.setValue(new Double (10));
		
		adate1 = new AnswerDate();
		adate1.setValue(date);
		
		quest1 = new QuestionMC("q1-id");
		
		qnum1 = new QuestionNum("qnum1-id");
		
		qdate1 = new QuestionDate("qdate1-id");
		
		diag1 = new Solution("diag1-id");
		diag1.setName("diag1-text");
		
		qcon1 = new QContainer("qcon1-id");
		
		rcomp = new Rule(null);
	}
	
	public void testActionSuppressAnswer() throws Exception{
		
		List<AnswerChoice> suppressList = new LinkedList<AnswerChoice>();
		suppressList.add(ac1);
		suppressList.add(ac2);
		
		ActionSuppressAnswer asa = new ActionSuppressAnswer();
		rcomp.setAction(asa);
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
		
		SuppressAnswerActionHandler asaw = new SuppressAnswerActionHandler();
		isTag= new XMLTag(asaw.write(asa, doc));
		assertEquals("(0)", shouldTag, isTag);
	}

	public void testActionClarification() throws Exception{
		List<QASet> clarifyList = new LinkedList<QASet>();
		clarifyList.add(quest1);
		clarifyList.add(qcon1);
		
		ActionClarify acl = new ActionClarify();
		rcomp.setAction(acl);
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
			
		NextQASetActionHandler acw = new NextQASetActionHandler();
		isTag = new XMLTag(acw.write(acl, doc));
		assertEquals("(1)", shouldTag, isTag);
	}
	
	public void testActionRefine() throws Exception{
		List<QASet> refineList = new LinkedList<QASet>();
		refineList.add(qcon1);
		refineList.add(quest1);
		
		ActionRefine are = new ActionRefine();
		rcomp.setAction(are);
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
			
		NextQASetActionHandler arw = new NextQASetActionHandler();
		isTag = new XMLTag(arw.write(are, doc));
		assertEquals("(2)", shouldTag, isTag);
	}
	
	public void testActionIndication() throws Exception{
		List<QASet> indicationList = new LinkedList<QASet> ();
		indicationList.add(qcon1);
		indicationList.add(quest1);
		
		ActionIndication ai = new ActionIndication();
		rcomp.setAction(ai);
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
			
		NextQASetActionHandler aiw = new NextQASetActionHandler();
		isTag = new XMLTag(aiw.write(ai, doc));
		assertEquals("(3)", shouldTag, isTag);
	}
	
	public void testActionContraIndication() throws Exception{
		List<QASet>  contraindicationList = new LinkedList<QASet> ();
		contraindicationList.add(quest1);
		contraindicationList.add(qcon1);
		
		ActionContraIndication aci = new ActionContraIndication();
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
			
		ContraIndicationActionHandler aciw = new ContraIndicationActionHandler();
		isTag = new XMLTag(aciw.write(aci, doc));
		assertEquals("(4)", shouldTag, isTag);
	}
	
	public void testActionHeuristicPS() throws Exception{
		ActionHeuristicPS ah = new ActionHeuristicPS();
		rcomp.setAction(ah);
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
			
		HeuristicPSActionHandler ahw = new HeuristicPSActionHandler();
		isTag = new XMLTag(ahw.write(ah, doc));
		assertEquals("(5)", shouldTag, isTag);
	}
	
	public void testActionSetValueAndActionAddValue() throws Exception {
		FormulaNumber fn = new FormulaNumber(new Double(13));	
		FormulaExpression fe = new FormulaExpression(qnum1,fn);
		
		List<Object> setValueList = new LinkedList<Object>();
		setValueList.add(ac1);
		setValueList.add(fe);
		setValueList.add(ac2);
		
		ActionAddValue aav = new ActionAddValue();
		rcomp.setAction(aav);
		aav.setQuestion(quest1);
		aav.setValues(setValueList.toArray());
		
		ActionSetValue asv = new ActionSetValue();
		rcomp.setAction(asv);
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
			
			XMLTag questNumTag = new XMLTag("Question");
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
		
		QuestionSetterActionHandler aavw = new QuestionSetterActionHandler();
		isTag = new XMLTag(aavw.write(aav, doc));
		
		// it is absolute nonsense that xml code must have the same spaces newlines tabs etc.
		// (ok when it's a textnode then it's no nonsense but this did not work because of newlines between tags ...) 
		// assertEquals("(6)", removeAllSpaces(shouldTagAdd), removeAllSpaces(isTag));
		assertEquals("(6)", shouldTagAdd, isTag);
		
		isTag = new XMLTag(aavw.write(asv, doc));
		assertEquals("(7)", shouldTagSet, isTag);
	}
	
	
	
	
	public void testActionSetValueAndActionAddValueDate() throws Exception {
		FormulaDateElement fn = new Today(new FormulaNumber(new Double(13)));	
		FormulaDateExpression fe = new FormulaDateExpression(qdate1,fn);
		
		List<Object> setValueList = new LinkedList<Object>();
		setValueList.add(fe);
		
		ActionAddValue aav = new ActionAddValue();
		rcomp.setAction(aav);
		aav.setQuestion(qdate1);
		aav.setValues(setValueList.toArray());
		
		ActionSetValue asv = new ActionSetValue();
		rcomp.setAction(asv);
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
		
		QuestionSetterActionHandler aavw = new QuestionSetterActionHandler();
		isTag = new XMLTag(aavw.write(aav, doc));
		
		// it is absolute nonsense that xml code must have the same spaces newlines tabs etc.
		// (ok when it's a textnode then it's no nonsense but this did not work because of newlines between tags ...) 
		// assertEquals("(6)", removeAllSpaces(shouldTagAdd), removeAllSpaces(isTag));
		assertEquals("(date1)", shouldTagAdd, isTag);
		
		isTag = new XMLTag(aavw.write(asv, doc));
		assertEquals("(date2)", shouldTagSet, isTag);
	}
}
