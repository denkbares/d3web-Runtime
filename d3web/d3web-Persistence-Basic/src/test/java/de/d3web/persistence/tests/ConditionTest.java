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
import java.util.LinkedList;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.core.kpers.fragments.conditions.AndConditionHandler;
import de.d3web.core.kpers.fragments.conditions.OrConditionHandler;
import de.d3web.core.kpers.utilities.Util;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerUnknown;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.domainModel.qasets.QuestionOC;
import de.d3web.kernel.domainModel.qasets.QuestionText;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondAnd;
import de.d3web.kernel.domainModel.ruleCondition.CondDState;
import de.d3web.kernel.domainModel.ruleCondition.CondEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondKnown;
import de.d3web.kernel.domainModel.ruleCondition.CondMofN;
import de.d3web.kernel.domainModel.ruleCondition.CondNot;
import de.d3web.kernel.domainModel.ruleCondition.CondNum;
import de.d3web.kernel.domainModel.ruleCondition.CondNumEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondNumGreater;
import de.d3web.kernel.domainModel.ruleCondition.CondNumIn;
import de.d3web.kernel.domainModel.ruleCondition.CondNumLess;
import de.d3web.kernel.domainModel.ruleCondition.CondOr;
import de.d3web.kernel.domainModel.ruleCondition.CondTextContains;
import de.d3web.kernel.domainModel.ruleCondition.CondTextEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondUnknown;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.plugin.test.InitPluginManager;

/**
 * @author merz
 * 
 *         To change this generated comment edit the template variable
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class ConditionTest extends TestCase {
	private AbstractCondition ac1, ac21, ac22, ac3;

	private CondDState cDState1;
	private CondEqual cEqual1;
	private CondKnown cKnown1;
	private CondNum cNumE1, cNumG1, cNumIn1, cNumL1;
	private CondTextContains cTextContains1;
	private CondTextEqual cTextEqual1;
	private CondUnknown cUnknown1;

	private Diagnosis d1;
	

	private XMLTag isTag;
	private XMLTag shouldTag;

	public ConditionTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ConditionTest.suite());
	}

	public static Test suite() {
		return new TestSuite(ConditionTest.class);
	}

	protected void setUp() {
		try {
			InitPluginManager.init();
		}
		catch (IOException e1) {
			assertTrue("Error initialising plugin framework", false);
		}
		
		d1 = new Diagnosis("d1-id");
		
		Question qoc1 = new QuestionOC("qoc1-id");
		QuestionNum qnum1 = new QuestionNum("qnum1-id");
		QuestionNum qnum2 = new QuestionNum("qnum2-id");
		QuestionText qt1 = new QuestionText("qt1-id");
		qt1.setText("qt1-text");

		Vector<Answer> val1 = new Vector<Answer>();
		Vector<Answer> val2 = new Vector<Answer>();

		AnswerChoice ach1 = new AnswerChoice("ach1-id");
		ach1.setText("ach1-text");
		val1.add(ach1);

		AnswerChoice ach2 = new AnswerChoice("ach2-id");
		ach2.setText("ach2-text");
		val1.add(ach2);
		val2.add(ach2);

		cDState1 = new CondDState(d1, DiagnosisState.SUGGESTED);

		cEqual1 = new CondEqual(qnum1, new AnswerUnknown());
		cEqual1.setValues(val1);

		cKnown1 = new CondKnown(qnum1);

		cNumL1 = new CondNumEqual(qnum1, new Double(4.5));
		cNumG1 = new CondNumGreater(qnum1, new Double(10));
		cNumIn1 = new CondNumIn(qnum2, new Double(4.0), new Double(12));
		cNumE1 = new CondNumLess(qnum2, new Double(3));

		cTextContains1 = new CondTextContains(qt1, "text");
		cTextEqual1 = new CondTextEqual(qt1, "qt1-text");

		cUnknown1 = new CondUnknown(qoc1);
	}

	public void _testAllCondNums() throws Exception {

		LinkedList<AbstractCondition> l1 = new LinkedList<AbstractCondition>();
		l1.add(cNumL1);
		l1.add(cNumIn1);
		l1.add(cNumG1);
		l1.add(cNumE1);
		ac1 = new CondAnd(l1);

		shouldTag = new XMLTag("Condition");
		shouldTag.addAttribute("type", "and");

		XMLTag condNumE = new XMLTag("Condition");
		condNumE.addAttribute("type", "numEqual");
		condNumE.addAttribute("ID", "qnum1-id");
		condNumE.addAttribute("value", "4.5");

		XMLTag condNumIn = new XMLTag("Condition");
		condNumIn.addAttribute("type", "numIn");
		condNumIn.addAttribute("ID", "qnum2-id");
		condNumIn.addAttribute("minValue", "4.0");
		condNumIn.addAttribute("maxValue", "12.0");

		XMLTag condNumG = new XMLTag("Condition");
		condNumG.addAttribute("type", "numGreater");
		condNumG.addAttribute("ID", "qnum1-id");
		condNumG.addAttribute("value", "10.0");

		XMLTag condNumL = new XMLTag("Condition");
		condNumL.addAttribute("type", "numLess");
		condNumL.addAttribute("ID", "qnum2-id");
		condNumL.addAttribute("value", "3.0");

		shouldTag.addChild(condNumE);
		shouldTag.addChild(condNumIn);
		shouldTag.addChild(condNumG);
		shouldTag.addChild(condNumL);

		isTag = new XMLTag(new AndConditionHandler().write(ac1, Util
				.createEmptyDocument()));

		assertEquals("(0)", shouldTag, isTag);
	}

	public void testAllNonTerminalConditions() throws Exception {

		ac3 = new CondNot(cDState1);

		LinkedList<AbstractCondition> l21 = new LinkedList<AbstractCondition>();
		l21.add(cEqual1);
		l21.add(cNumG1);
		l21.add(ac3);
		ac21 = new CondAnd(l21);

		LinkedList<AbstractCondition> l22 = new LinkedList<AbstractCondition>();
		l22.add(cTextContains1);
		ac22 = new CondMofN(l22, 1, 1);

		LinkedList<AbstractCondition> l1 = new LinkedList<AbstractCondition>();
		l1.add(ac21);
		l1.add(ac22);
		ac1 = new CondOr(l1);

		shouldTag = new XMLTag("Condition");
		shouldTag.addAttribute("type", "or");

		XMLTag andTag1 = new XMLTag("Condition");
		andTag1.addAttribute("type", "and");

		XMLTag equalTag1 = new XMLTag("Condition");
		equalTag1.addAttribute("type", "equal");
		equalTag1.addAttribute("ID", "qnum1-id");
		equalTag1.addAttribute("value", "ach1-id,ach2-id");
		andTag1.addChild(equalTag1);

		XMLTag numGreaterTag1 = new XMLTag("Condition");
		numGreaterTag1.addAttribute("type", "numGreater");
		numGreaterTag1.addAttribute("ID", "qnum1-id");
		numGreaterTag1.addAttribute("value", "10.0");
		andTag1.addChild(numGreaterTag1);

		XMLTag notTag1 = new XMLTag("Condition");
		notTag1.addAttribute("type", "not");

		XMLTag dStateTag1 = new XMLTag("Condition");
		dStateTag1.addAttribute("type", "DState");
		dStateTag1.addAttribute("ID", "d1-id");
		dStateTag1.addAttribute("value", "suggested");
		notTag1.addChild(dStateTag1);

		andTag1.addChild(notTag1);

		shouldTag.addChild(andTag1);

		XMLTag mofnTag1 = new XMLTag("Condition");
		mofnTag1.addAttribute("type", "MofN");
		mofnTag1.addAttribute("min", "1");
		mofnTag1.addAttribute("max", "1");
		mofnTag1.addAttribute("size", "1");

		XMLTag tContainsTag1 = new XMLTag("Condition");
		tContainsTag1.addAttribute("type", "textContains");
		tContainsTag1.addAttribute("ID", "qt1-id");
		// tContainsTag1.addAttribute("value", "qt1-text");
		XMLTag var1 = new XMLTag("Value");
		var1.setContent("text");
		tContainsTag1.addChild(var1);
		mofnTag1.addChild(tContainsTag1);

		shouldTag.addChild(mofnTag1);

		isTag = new XMLTag(new OrConditionHandler().write(ac1, Util.createEmptyDocument()));

		assertEquals("(1)", shouldTag, isTag);
	}

	public void testAllCondText() throws Exception {

		LinkedList<AbstractCondition> l1 = new LinkedList<AbstractCondition>();
		l1.add(cTextContains1);
		l1.add(cTextEqual1);
		ac1 = new CondOr(l1);

		shouldTag = new XMLTag("Condition");
		shouldTag.addAttribute("type", "or");

		XMLTag tContainsTag1 = new XMLTag("Condition");
		tContainsTag1.addAttribute("type", "textContains");
		tContainsTag1.addAttribute("ID", "qt1-id");

		// tContainsTag1.addAttribute("value", "text");
		XMLTag var1 = new XMLTag("Value");
		var1.setContent("text");
		tContainsTag1.addChild(var1);

		shouldTag.addChild(tContainsTag1);

		XMLTag tEqualTag1 = new XMLTag("Condition");
		tEqualTag1.addAttribute("type", "textEqual");
		tEqualTag1.addAttribute("ID", "qt1-id");

		// tEqualTag1.addAttribute("value", "");
		XMLTag var2 = new XMLTag("Value");
		var2.setContent("qt1-text");
		tEqualTag1.addChild(var2);

		shouldTag.addChild(tEqualTag1);

		isTag = new XMLTag(new OrConditionHandler().write(ac1, Util.createEmptyDocument()));

		assertEquals("(2)", shouldTag, isTag);
	}

	public void testCondKnownAndUnknown() throws Exception {

		LinkedList<AbstractCondition> l1 = new LinkedList<AbstractCondition>();
		l1.add(cKnown1);
		l1.add(cUnknown1);
		ac1 = new CondOr(l1);

		shouldTag = new XMLTag("Condition");
		shouldTag.addAttribute("type", "or");

		XMLTag cKnownTag1 = new XMLTag("Condition");
		cKnownTag1.addAttribute("type", "known");
		cKnownTag1.addAttribute("ID", "qnum1-id");
		shouldTag.addChild(cKnownTag1);

		XMLTag cUnKnownTag1 = new XMLTag("Condition");
		cUnKnownTag1.addAttribute("type", "unknown");
		cUnKnownTag1.addAttribute("ID", "qoc1-id");
		shouldTag.addChild(cUnKnownTag1);

		isTag = new XMLTag(new OrConditionHandler().write(ac1, Util.createEmptyDocument()));

		assertEquals("(3)", shouldTag, isTag);
	}
}
