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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

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
import de.d3web.core.knowledge.terminology.AnswerNo;
import de.d3web.core.knowledge.terminology.AnswerYes;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.Solution;
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

public class ActionTest {

	private Rule rule;

	private Choice answerNo;
	private Choice answerYes;

	private QuestionChoice questionMC;
	private QuestionNum questionNum;
	private QuestionDate questionDate;

	private Solution solution;
	private QContainer qContainer;

	private XMLTag isTag;
	private XMLTag shouldTag;
	private XMLTag shouldTagAdd;
	private XMLTag shouldTagSet;

	Document doc;

	@Before
	public void setUp() throws Exception {

		InitPluginManager.init();

		doc = Util.createEmptyDocument();

		answerNo = new AnswerNo("ac1-id");
		answerNo.setText("a1-text");

		answerYes = new AnswerYes("ac2-id");
		answerYes.setText("a2-text");

		questionMC = new QuestionMC("q1-id");

		questionNum = new QuestionNum("qnum1-id");

		questionDate = new QuestionDate("qdate1-id");

		solution = new Solution("diag1-id");
		solution.setName("diag1-text");

		qContainer = new QContainer("qcon1-id");

		rule = new Rule(null, null);
	}

	@Test
	public void testActionSuppressAnswer() throws IOException {

		List<Choice> suppressList = new LinkedList<Choice>();
		suppressList.add(answerNo);
		suppressList.add(answerYes);

		ActionSuppressAnswer actionSuppressAnswer = new ActionSuppressAnswer();
		rule.setAction(actionSuppressAnswer);
		actionSuppressAnswer.setQuestion(questionMC);
		actionSuppressAnswer.setSuppress(suppressList);

		shouldTag = new XMLTag("Action");
		shouldTag.addAttribute("type", "ActionSuppressAnswer");

		XMLTag question = new XMLTag("Question");
		question.addAttribute("ID", "q1-id");

		XMLTag suppress = new XMLTag("Suppress");
		XMLTag answer1 = new XMLTag("Answer");
		answer1.addAttribute("ID", "ac1-id");
		XMLTag answer2 = new XMLTag("Answer");
		answer2.addAttribute("ID", "ac2-id");
		suppress.addChild(answer1);
		suppress.addChild(answer2);

		shouldTag.addChild(question);
		shouldTag.addChild(suppress);

		SuppressAnswerActionHandler handler = new SuppressAnswerActionHandler();
		isTag = new XMLTag(handler.write(actionSuppressAnswer, doc));
		assertEquals("(0)", shouldTag, isTag);
	}

	@Test
	public void testActionClarification() throws Exception {
		List<QASet> clarifyList = new LinkedList<QASet>();
		clarifyList.add(questionMC);
		clarifyList.add(qContainer);

		ActionClarify acl = new ActionClarify();
		rule.setAction(acl);
		acl.setTarget(solution);
		acl.setQASets(clarifyList);

		shouldTag = new XMLTag("Action");
		shouldTag.addAttribute("type", "ActionClarify");

		XMLTag target = new XMLTag("targetDiagnosis");
		target.addAttribute("ID", "diag1-id");

		XMLTag targets = new XMLTag("TargetQASets");
		XMLTag qa1 = new XMLTag("QASet");
		qa1.addAttribute("ID", "q1-id");
		XMLTag qa2 = new XMLTag("QASet");
		qa2.addAttribute("ID", "qcon1-id");
		targets.addChild(qa1);
		targets.addChild(qa2);

		shouldTag.addChild(target);
		shouldTag.addChild(targets);

		NextQASetActionHandler acw = new NextQASetActionHandler();
		isTag = new XMLTag(acw.write(acl, doc));
		assertEquals("(1)", shouldTag, isTag);
	}

	@Test
	public void testActionRefine() throws Exception {
		List<QASet> refineList = new LinkedList<QASet>();
		refineList.add(qContainer);
		refineList.add(questionMC);

		ActionRefine are = new ActionRefine();
		rule.setAction(are);
		are.setTarget(solution);
		are.setQASets(refineList);

		shouldTag = new XMLTag("Action");
		shouldTag.addAttribute("type", "ActionRefine");

		XMLTag target = new XMLTag("targetDiagnosis");
		target.addAttribute("ID", "diag1-id");

		XMLTag targets = new XMLTag("TargetQASets");
		XMLTag qa1 = new XMLTag("QASet");
		qa1.addAttribute("ID", "qcon1-id");
		XMLTag qa2 = new XMLTag("QASet");
		qa2.addAttribute("ID", "q1-id");
		targets.addChild(qa1);
		targets.addChild(qa2);

		shouldTag.addChild(target);
		shouldTag.addChild(targets);

		NextQASetActionHandler arw = new NextQASetActionHandler();
		isTag = new XMLTag(arw.write(are, doc));
		assertEquals("(2)", shouldTag, isTag);
	}

	@Test
	public void testActionIndication() throws Exception {
		List<QASet> indicationList = new LinkedList<QASet>();
		indicationList.add(qContainer);
		indicationList.add(questionMC);

		ActionIndication ai = new ActionIndication();
		rule.setAction(ai);
		ai.setQASets(indicationList);

		shouldTag = new XMLTag("Action");
		shouldTag.addAttribute("type", "ActionIndication");

		XMLTag targets = new XMLTag("TargetQASets");
		XMLTag qa1 = new XMLTag("QASet");
		qa1.addAttribute("ID", "qcon1-id");
		XMLTag qa2 = new XMLTag("QASet");
		qa2.addAttribute("ID", "q1-id");
		targets.addChild(qa1);
		targets.addChild(qa2);

		shouldTag.addChild(targets);

		NextQASetActionHandler aiw = new NextQASetActionHandler();
		isTag = new XMLTag(aiw.write(ai, doc));
		assertEquals("(3)", shouldTag, isTag);
	}

	@Test
	public void testActionContraIndication() throws Exception {
		List<QASet> contraindicationList = new LinkedList<QASet>();
		contraindicationList.add(questionMC);
		contraindicationList.add(qContainer);

		ActionContraIndication aci = new ActionContraIndication();
		aci.setQASets(contraindicationList);

		shouldTag = new XMLTag("Action");
		shouldTag.addAttribute("type", "ActionContraIndication");

		XMLTag targets = new XMLTag("TargetQASets");
		XMLTag qa1 = new XMLTag("QASet");
		qa1.addAttribute("ID", "q1-id");
		XMLTag qa2 = new XMLTag("QASet");
		qa2.addAttribute("ID", "qcon1-id");
		targets.addChild(qa1);
		targets.addChild(qa2);

		shouldTag.addChild(targets);

		ContraIndicationActionHandler aciw = new ContraIndicationActionHandler();
		isTag = new XMLTag(aciw.write(aci, doc));
		assertEquals("(4)", shouldTag, isTag);
	}

	@Test
	public void testActionHeuristicPS() throws Exception {
		ActionHeuristicPS ah = new ActionHeuristicPS();
		rule.setAction(ah);
		ah.setSolution(solution);
		ah.setScore(Score.P1);

		shouldTag = new XMLTag("Action");
		shouldTag.addAttribute("type", "ActionHeuristicPS");

		XMLTag score = new XMLTag("Score");
		score.addAttribute("value", "P1");

		XMLTag diagnosis = new XMLTag("Diagnosis");
		diagnosis.addAttribute("ID", "diag1-id");

		shouldTag.addChild(score);
		shouldTag.addChild(diagnosis);

		HeuristicPSActionHandler ahw = new HeuristicPSActionHandler();
		isTag = new XMLTag(ahw.write(ah, doc));
		assertEquals("(5)", shouldTag, isTag);
	}

	@Test
	public void testActionSetValueValue() throws Exception {
		FormulaNumber fn = new FormulaNumber(new Double(13));
		FormulaExpression fe = new FormulaExpression(questionNum, fn);

		List<Object> setValueList = new LinkedList<Object>();
		setValueList.add(answerNo);
		setValueList.add(fe);
		setValueList.add(answerYes);

		ActionSetValue aav = new ActionSetValue();
		rule.setAction(aav);
		aav.setQuestion(questionMC);
		aav.setValue(setValueList.toArray());

		ActionSetValue asv = new ActionSetValue();
		rule.setAction(asv);
		asv.setQuestion(questionMC);
		asv.setValue(setValueList.toArray());

		shouldTagAdd = new XMLTag("Action");
		shouldTagAdd.addAttribute("type", "ActionSetValue");

		shouldTagSet = new XMLTag("Action");
		shouldTagSet.addAttribute("type", "ActionSetValue");

		XMLTag questTag = new XMLTag("Question");
		questTag.addAttribute("ID", "q1-id");

		XMLTag valuesTag = new XMLTag("Values");

		// answer-choice1
		XMLTag ac1Tag = new XMLTag("Value");
		ac1Tag.addAttribute("type", "answer");
		ac1Tag.addAttribute("ID", "ac1-id");

		// formula-expression
		XMLTag fe1Tag = new XMLTag("Value");
		fe1Tag.addAttribute("type", "evaluatable");

		XMLTag formulaExp = new XMLTag("FormulaExpression");

		XMLTag questNumTag = new XMLTag("Question");
		questNumTag.addAttribute("ID", "qnum1-id");

		XMLTag formulaNum = new XMLTag("FormulaPrimitive");
		formulaNum.addAttribute("type", "FormulaNumber");
		XMLTag value = new XMLTag("Value");
		value.setContent("13.0");
		formulaNum.addChild(value);

		formulaExp.addChild(questNumTag);
		formulaExp.addChild(formulaNum);

		fe1Tag.addChild(formulaExp);

		// answer-choice2
		XMLTag ac2Tag = new XMLTag("Value");
		ac2Tag.addAttribute("type", "answer");
		ac2Tag.addAttribute("ID", "ac2-id");

		valuesTag.addChild(ac1Tag);
		valuesTag.addChild(fe1Tag);
		valuesTag.addChild(ac2Tag);

		shouldTagAdd.addChild(questTag);
		shouldTagAdd.addChild(valuesTag);

		shouldTagSet.addChild(questTag);
		shouldTagSet.addChild(valuesTag);

		QuestionSetterActionHandler aavw = new QuestionSetterActionHandler();
		isTag = new XMLTag(aavw.write(aav, doc));

		// it is absolute nonsense that xml code must have the same spaces
		// newlines tabs etc.
		// (ok when it's a textnode then it's no nonsense but this did not work
		// because of newlines between tags ...)
		// assertEquals("(6)", removeAllSpaces(shouldTagAdd),
		// removeAllSpaces(isTag));
		assertEquals("(6)", shouldTagAdd, isTag);

		isTag = new XMLTag(aavw.write(asv, doc));
		assertEquals("(7)", shouldTagSet, isTag);
	}

	@Test
	public void testActionSetValueAndActionAddValueDate() throws Exception {
		FormulaDateElement fn = new Today(new FormulaNumber(new Double(13)));
		FormulaDateExpression fe = new FormulaDateExpression(questionDate, fn);

		ActionSetValue aav = new ActionSetValue();
		rule.setAction(aav);
		aav.setQuestion(questionDate);
		aav.setValue(fe);

		ActionSetValue asv = new ActionSetValue();
		rule.setAction(asv);
		asv.setQuestion(questionDate);
		asv.setValue(fe);

		shouldTagAdd = new XMLTag("Action");
		shouldTagAdd.addAttribute("type", "ActionSetValue");

		shouldTagSet = new XMLTag("Action");
		shouldTagSet.addAttribute("type", "ActionSetValue");

		XMLTag questTag = new XMLTag("Question");
		questTag.addAttribute("ID", "qdate1-id");

		XMLTag valuesTag = new XMLTag("Values");

		// formula-expression
		XMLTag fe1Tag = new XMLTag("Value");
		fe1Tag.addAttribute("type", "evaluatable");

		XMLTag formulaExp = new XMLTag("FormulaDateExpression");

		XMLTag questDateTag = new XMLTag("Question");
		questDateTag.addAttribute("ID", "qdate1-id");

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

		// it is absolute nonsense that xml code must have the same spaces
		// newlines tabs etc.
		// (ok when it's a textnode then it's no nonsense but this did not work
		// because of newlines between tags ...)
		// assertEquals("(6)", removeAllSpaces(shouldTagAdd),
		// removeAllSpaces(isTag));
		assertEquals("(date1)", shouldTagAdd, isTag);

		isTag = new XMLTag(aavw.write(asv, doc));
		assertEquals("(date2)", shouldTagSet, isTag);
	}
}
