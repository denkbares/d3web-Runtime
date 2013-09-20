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

import de.d3web.abstraction.ActionSetQuestion;
import de.d3web.abstraction.formula.FormulaElement;
import de.d3web.abstraction.formula.FormulaNumber;
import de.d3web.abstraction.inference.PSMethodAbstraction;
import de.d3web.core.inference.Rule;
import de.d3web.core.io.fragments.actions.ContraIndicationActionHandler;
import de.d3web.core.io.fragments.actions.HeuristicPSActionHandler;
import de.d3web.core.io.fragments.actions.NextQASetActionHandler;
import de.d3web.core.io.fragments.actions.QuestionSetterActionHandler;
import de.d3web.core.io.fragments.actions.SuppressAnswerActionHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.AnswerNo;
import de.d3web.core.knowledge.terminology.AnswerYes;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.indication.ActionContraIndication;
import de.d3web.indication.ActionIndication;
import de.d3web.indication.ActionSuppressAnswer;
import de.d3web.indication.inference.PSMethodStrategic;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.Score;
import de.d3web.scoring.inference.PSMethodHeuristic;

/**
 * @author merz
 */

public class ActionTest {

	private Rule rule;

	private Choice answerNo;
	private Choice answerYes;

	private QuestionChoice questionMC;
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
		KnowledgeBase kb = new KnowledgeBase();
		doc = XMLUtil.createEmptyDocument();

		answerNo = new AnswerNo("ac1-name");

		answerYes = new AnswerYes("ac2-name");

		questionMC = new QuestionMC(kb, "q1-name");

		questionDate = new QuestionDate(kb, "qdate1-name");

		solution = new Solution(kb, "diag1-name");

		qContainer = new QContainer(kb, "qcon1-name");

		rule = new Rule(null);
	}

	@Test
	public void testActionSuppressAnswer() throws IOException {
		rule.setProblemsolverContext(PSMethodStrategic.class);
		ActionSuppressAnswer actionSuppressAnswer = new ActionSuppressAnswer();
		rule.setAction(actionSuppressAnswer);
		actionSuppressAnswer.setQuestion(questionMC);
		actionSuppressAnswer.addSuppress(answerNo);
		actionSuppressAnswer.addSuppress(answerYes);

		shouldTag = new XMLTag("Action");
		shouldTag.addAttribute("type", "ActionSuppressAnswer");

		XMLTag question = new XMLTag("Question");
		question.addAttribute("name", "q1-name");

		XMLTag suppress = new XMLTag("Suppress");
		XMLTag answer1 = new XMLTag("Answer");
		answer1.addAttribute("name", "ac1-name");
		XMLTag answer2 = new XMLTag("Answer");
		answer2.addAttribute("name", "ac2-name");
		suppress.addChild(answer1);
		suppress.addChild(answer2);

		shouldTag.addChild(question);
		shouldTag.addChild(suppress);

		SuppressAnswerActionHandler handler = new SuppressAnswerActionHandler();
		isTag = new XMLTag(handler.write(actionSuppressAnswer, doc));
		assertEquals("(0)", shouldTag, isTag);
	}

	@Test
	public void testActionIndication() throws Exception {
		List<QASet> indicationList = new LinkedList<QASet>();
		indicationList.add(qContainer);
		indicationList.add(questionMC);

		rule.setProblemsolverContext(PSMethodStrategic.class);
		ActionIndication ai = new ActionIndication();
		rule.setAction(ai);
		ai.setQASets(indicationList);

		shouldTag = new XMLTag("Action");
		shouldTag.addAttribute("type", "ActionIndication");

		XMLTag targets = new XMLTag("TargetQASets");
		XMLTag qa1 = new XMLTag("QASet");
		qa1.addAttribute("name", "qcon1-name");
		XMLTag qa2 = new XMLTag("QASet");
		qa2.addAttribute("name", "q1-name");
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
		qa1.addAttribute("name", "q1-name");
		XMLTag qa2 = new XMLTag("QASet");
		qa2.addAttribute("name", "qcon1-name");
		targets.addChild(qa1);
		targets.addChild(qa2);

		shouldTag.addChild(targets);

		ContraIndicationActionHandler aciw = new ContraIndicationActionHandler();
		isTag = new XMLTag(aciw.write(aci, doc));
		assertEquals("(4)", shouldTag, isTag);
	}

	@Test
	public void testActionHeuristicPS() throws Exception {
		rule.setProblemsolverContext(PSMethodHeuristic.class);
		ActionHeuristicPS ah = new ActionHeuristicPS();
		rule.setAction(ah);
		ah.setSolution(solution);
		ah.setScore(Score.P1);

		shouldTag = new XMLTag("Action");
		shouldTag.addAttribute("type", "ActionHeuristicPS");

		XMLTag score = new XMLTag("Score");
		score.addAttribute("value", "P1");

		XMLTag diagnosis = new XMLTag("Diagnosis");
		diagnosis.addAttribute("name", "diag1-name");

		shouldTag.addChild(score);
		shouldTag.addChild(diagnosis);

		HeuristicPSActionHandler ahw = new HeuristicPSActionHandler();
		isTag = new XMLTag(ahw.write(ah, doc));
		assertEquals("(5)", shouldTag, isTag);
	}

	@Test
	public void testActionSetQuestionValue() throws Exception {
		FormulaNumber fn = new FormulaNumber(new Double(13));

		List<Object> setValueList = new LinkedList<Object>();
		setValueList.add(answerNo);
		setValueList.add(fn);
		setValueList.add(answerYes);

		rule.setProblemsolverContext(PSMethodAbstraction.class);
		ActionSetQuestion aav = new ActionSetQuestion();
		rule.setAction(aav);
		aav.setQuestion(questionMC);
		aav.setValue(setValueList.toArray());

		ActionSetQuestion asv = new ActionSetQuestion();
		rule.setAction(asv);
		asv.setQuestion(questionMC);
		asv.setValue(setValueList.toArray());

		shouldTagAdd = new XMLTag("Action");
		shouldTagAdd.addAttribute("type", "ActionSetValue");

		shouldTagSet = new XMLTag("Action");
		shouldTagSet.addAttribute("type", "ActionSetValue");

		XMLTag questTag = new XMLTag("Question");
		questTag.addAttribute("name", "q1-name");

		XMLTag valuesTag = new XMLTag("Values");

		// answer-choice1
		XMLTag ac1Tag = new XMLTag("Value");
		ac1Tag.addAttribute("type", "answer");
		ac1Tag.addAttribute("name", "ac1-name");

		// formula-expression
		XMLTag fe1Tag = new XMLTag("Value");
		fe1Tag.addAttribute("type", "evaluatable");

		XMLTag formulaNum = new XMLTag("FormulaPrimitive");
		formulaNum.addAttribute("type", "FormulaNumber");
		XMLTag value = new XMLTag("Value");
		value.setContent("13.0");
		formulaNum.addChild(value);

		fe1Tag.addChild(formulaNum);

		// answer-choice2
		XMLTag ac2Tag = new XMLTag("Value");
		ac2Tag.addAttribute("type", "answer");
		ac2Tag.addAttribute("name", "ac2-name");

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
		FormulaElement fn = new FormulaNumber(new Double(13));

		rule.setProblemsolverContext(PSMethodAbstraction.class);
		ActionSetQuestion aav = new ActionSetQuestion();
		rule.setAction(aav);
		aav.setQuestion(questionDate);
		aav.setValue(fn);

		ActionSetQuestion asv = new ActionSetQuestion();
		rule.setAction(asv);
		asv.setQuestion(questionDate);
		asv.setValue(fn);

		shouldTagAdd = new XMLTag("Action");
		shouldTagAdd.addAttribute("type", "ActionSetValue");

		shouldTagSet = new XMLTag("Action");
		shouldTagSet.addAttribute("type", "ActionSetValue");

		XMLTag questTag = new XMLTag("Question");
		questTag.addAttribute("name", "qdate1-name");

		XMLTag valuesTag = new XMLTag("Values");

		// formula-expression
		XMLTag fe1Tag = new XMLTag("Value");
		fe1Tag.addAttribute("type", "evaluatable");

		XMLTag number = new XMLTag("FormulaPrimitive");
		number.addAttribute("type", "FormulaNumber");
		XMLTag value = new XMLTag("Value");
		value.setContent("13.0");
		number.addChild(value);

		fe1Tag.addChild(number);

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
