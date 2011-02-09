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
import java.util.Vector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondDState;
import de.d3web.core.inference.condition.CondKnown;
import de.d3web.core.inference.condition.CondMofN;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.CondNum;
import de.d3web.core.inference.condition.CondNumEqual;
import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.CondNumIn;
import de.d3web.core.inference.condition.CondNumLess;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.CondSolutionConfirmed;
import de.d3web.core.inference.condition.CondSolutionRejected;
import de.d3web.core.inference.condition.CondTextContains;
import de.d3web.core.inference.condition.CondTextEqual;
import de.d3web.core.inference.condition.CondUnknown;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.io.fragments.conditions.AndConditionHandler;
import de.d3web.core.io.fragments.conditions.ConditionSolutionConfirmedHandler;
import de.d3web.core.io.fragments.conditions.ConditionSolutionRejectedHandler;
import de.d3web.core.io.fragments.conditions.OrConditionHandler;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.plugin.test.InitPluginManager;

/**
 * @author merz
 */
public class ConditionTest {

	private Condition ac1, ac21, ac22, ac3;

	private CondDState cDState1;
	private CondKnown cKnown1;
	private CondNum cNumE1, cNumG1, cNumIn1, cNumL1;
	private CondTextContains cTextContains1;
	private CondTextEqual cTextEqual1;
	private CondUnknown cUnknown1;

	private Solution d1;

	private XMLTag isTag;
	private XMLTag shouldTag;

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = new KnowledgeBase();
		d1 = new Solution(kb, "d1-id");

		Question qoc1 = new QuestionOC(kb, "qoc1-id");
		QuestionNum qnum1 = new QuestionNum(kb, "qnum1-id");
		QuestionNum qnum2 = new QuestionNum(kb, "qnum2-id");
		QuestionText qt1 = new QuestionText(kb, "qt1-id");

		Vector<Choice> val1 = new Vector<Choice>();
		Vector<Choice> val2 = new Vector<Choice>();

		Choice ach1 = new Choice("ach1-id");
		val1.add(ach1);

		Choice ach2 = new Choice("ach2-id");
		val1.add(ach2);
		val2.add(ach2);

		cDState1 = new CondDState(d1, new Rating(Rating.State.SUGGESTED));
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

		LinkedList<Condition> l1 = new LinkedList<Condition>();
		l1.add(cNumL1);
		l1.add(cNumIn1);
		l1.add(cNumG1);
		l1.add(cNumE1);
		ac1 = new CondAnd(l1);

		shouldTag = new XMLTag("Condition");
		shouldTag.addAttribute("type", "and");

		XMLTag condNumE = new XMLTag("Condition");
		condNumE.addAttribute("type", "numEqual");
		condNumE.addAttribute("name", "qnum1-id");
		condNumE.addAttribute("value", "4.5");

		XMLTag condNumIn = new XMLTag("Condition");
		condNumIn.addAttribute("type", "numIn");
		condNumIn.addAttribute("name", "qnum2-id");
		condNumIn.addAttribute("minValue", "4.0");
		condNumIn.addAttribute("maxValue", "12.0");

		XMLTag condNumG = new XMLTag("Condition");
		condNumG.addAttribute("type", "numGreater");
		condNumG.addAttribute("name", "qnum1-id");
		condNumG.addAttribute("value", "10.0");

		XMLTag condNumL = new XMLTag("Condition");
		condNumL.addAttribute("type", "numLess");
		condNumL.addAttribute("name", "qnum2-id");
		condNumL.addAttribute("value", "3.0");

		shouldTag.addChild(condNumE);
		shouldTag.addChild(condNumIn);
		shouldTag.addChild(condNumG);
		shouldTag.addChild(condNumL);

		isTag = new XMLTag(new AndConditionHandler().write(ac1, Util
				.createEmptyDocument()));

		assertEquals("(0)", shouldTag, isTag);
	}

	@Test
	public void testAllNonTerminalConditions() throws Exception {

		ac3 = new CondNot(cDState1);

		LinkedList<Condition> l21 = new LinkedList<Condition>();
		// l21.add(cEqual1);
		l21.add(cNumG1);
		l21.add(ac3);
		ac21 = new CondAnd(l21);

		LinkedList<Condition> l22 = new LinkedList<Condition>();
		l22.add(cTextContains1);
		ac22 = new CondMofN(l22, 1, 1);

		LinkedList<Condition> l1 = new LinkedList<Condition>();
		l1.add(ac21);
		l1.add(ac22);
		ac1 = new CondOr(l1);

		shouldTag = new XMLTag("Condition");
		shouldTag.addAttribute("type", "or");

		XMLTag andTag1 = new XMLTag("Condition");
		andTag1.addAttribute("type", "and");

		// XMLTag equalTag1 = new XMLTag("Condition");
		// equalTag1.addAttribute("type", "equal");
		// equalTag1.addAttribute("ID", "qnum1-id");
		// equalTag1.addAttribute("value", "ach1-id,ach2-id");
		// andTag1.addChild(equalTag1);

		XMLTag numGreaterTag1 = new XMLTag("Condition");
		numGreaterTag1.addAttribute("type", "numGreater");
		numGreaterTag1.addAttribute("name", "qnum1-id");
		numGreaterTag1.addAttribute("value", "10.0");
		andTag1.addChild(numGreaterTag1);

		XMLTag notTag1 = new XMLTag("Condition");
		notTag1.addAttribute("type", "not");

		XMLTag dStateTag1 = new XMLTag("Condition");
		dStateTag1.addAttribute("type", "DState");
		dStateTag1.addAttribute("name", "d1-id");
		dStateTag1.addAttribute("value", "SUGGESTED");
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
		tContainsTag1.addAttribute("name", "qt1-id");
		// tContainsTag1.addAttribute("value", "qt1-text");
		XMLTag var1 = new XMLTag("Value");
		var1.setContent("text");
		tContainsTag1.addChild(var1);
		mofnTag1.addChild(tContainsTag1);

		shouldTag.addChild(mofnTag1);

		isTag = new XMLTag(new OrConditionHandler().write(ac1, Util.createEmptyDocument()));

		assertEquals("(1)", shouldTag, isTag);
	}

	@Test
	public void testAllCondText() throws Exception {

		LinkedList<Condition> l1 = new LinkedList<Condition>();
		l1.add(cTextContains1);
		l1.add(cTextEqual1);
		ac1 = new CondOr(l1);

		shouldTag = new XMLTag("Condition");
		shouldTag.addAttribute("type", "or");

		XMLTag tContainsTag1 = new XMLTag("Condition");
		tContainsTag1.addAttribute("type", "textContains");
		tContainsTag1.addAttribute("name", "qt1-id");

		// tContainsTag1.addAttribute("value", "text");
		XMLTag var1 = new XMLTag("Value");
		var1.setContent("text");
		tContainsTag1.addChild(var1);

		shouldTag.addChild(tContainsTag1);

		XMLTag tEqualTag1 = new XMLTag("Condition");
		tEqualTag1.addAttribute("type", "textEqual");
		tEqualTag1.addAttribute("name", "qt1-id");

		// tEqualTag1.addAttribute("value", "");
		XMLTag var2 = new XMLTag("Value");
		var2.setContent("qt1-text");
		tEqualTag1.addChild(var2);

		shouldTag.addChild(tEqualTag1);

		isTag = new XMLTag(new OrConditionHandler().write(ac1, Util.createEmptyDocument()));

		assertEquals("(2)", shouldTag, isTag);
	}

	@Test
	public void testCondKnownAndUnknown() throws Exception {

		LinkedList<Condition> l1 = new LinkedList<Condition>();
		l1.add(cKnown1);
		l1.add(cUnknown1);
		ac1 = new CondOr(l1);

		shouldTag = new XMLTag("Condition");
		shouldTag.addAttribute("type", "or");

		XMLTag cKnownTag1 = new XMLTag("Condition");
		cKnownTag1.addAttribute("type", "known");
		cKnownTag1.addAttribute("name", "qnum1-id");
		shouldTag.addChild(cKnownTag1);

		XMLTag cUnKnownTag1 = new XMLTag("Condition");
		cUnKnownTag1.addAttribute("type", "unknown");
		cUnKnownTag1.addAttribute("name", "qoc1-id");
		shouldTag.addChild(cUnKnownTag1);

		isTag = new XMLTag(new OrConditionHandler().write(ac1, Util.createEmptyDocument()));

		assertEquals("(3)", shouldTag, isTag);
	}

	/**
	 * 
	 * @throws Exception
	 * @created 23.11.2010
	 */
	@Test
	public void testCondSolutionConfirmed() throws Exception {

		CondSolutionConfirmed cond = new CondSolutionConfirmed(d1);

		shouldTag = new XMLTag("Condition");
		shouldTag.addAttribute("type", ConditionSolutionConfirmedHandler.TYPE);
		shouldTag.addAttribute("name", "d1-id");

		isTag = new XMLTag(new ConditionSolutionConfirmedHandler().write(cond,
				Util.createEmptyDocument()));

		Assert.assertEquals(isTag, shouldTag);

	}

	/**
	 * 
	 * @throws Exception
	 * @created 23.11.2010
	 */
	@Test
	public void testCondSolutionRejected() throws Exception {

		CondSolutionRejected cond = new CondSolutionRejected(d1);

		shouldTag = new XMLTag("Condition");
		shouldTag.addAttribute("type", ConditionSolutionRejectedHandler.TYPE);
		shouldTag.addAttribute("name", "d1-id");

		isTag = new XMLTag(new ConditionSolutionRejectedHandler().write(cond,
				Util.createEmptyDocument()));

		Assert.assertEquals(isTag, shouldTag);

	}

}
