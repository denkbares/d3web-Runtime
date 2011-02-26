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

package de.d3web.core.session.interviewmanager.tests;

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.test.InitPluginManager;

public class DialogTest {

	KnowledgeBase kb;
	QContainer pregnancyQuestions, heightWeightQuestions;
	QuestionOC sex, pregnant, ask_for_pregnancy, initQuestion, pregnancyContainerIndication;
	QuestionNum weight, height;
	ChoiceValue female, dont_ask;
	private Session session;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();

		QASet root = kb.getRootQASet();
		pregnancyQuestions = new QContainer(root, "pregnancyQuestions");
		sex = new QuestionOC(pregnancyQuestions, "sex", "male", "female");
		pregnant = new QuestionOC(sex, "pregnant", new String[] {
				"yes", "no" });
		female = new ChoiceValue(KnowledgeBaseUtils.findChoice(sex, "female"));

		ask_for_pregnancy = new QuestionOC(pregnancyQuestions, "ask for pregnancy", "yes", "no");
		dont_ask = new ChoiceValue(KnowledgeBaseUtils.findChoice(ask_for_pregnancy, "no"));

		heightWeightQuestions = new QContainer(root, "heightWeightQuestions");
		weight = new QuestionNum(heightWeightQuestions, "weight");
		height = new QuestionNum(heightWeightQuestions, "height");

		initQuestion = new QuestionOC(root, "initQuestion", "all", "pregnacyQuestions",
				"height+weight");

		pregnancyContainerIndication = new QuestionOC(root, "pregnancyContainerIndication", "yes",
				"no");

		// Rule: sex = female => INDICATE ( pregnant )
		RuleFactory.createIndicationRule(pregnant, new CondEqual(sex, female));
		// Rule: ask for pregnancy = no => CONTRA_INDICATE ( pregnant )
		RuleFactory.createContraIndicationRule(pregnant, new CondEqual(ask_for_pregnancy,
				dont_ask));

		// Rule: initQuestion = pregnacyQuestions => INDICATE CONTAINER (
		// pregnancyQuestions )
		RuleFactory.createIndicationRule(
				pregnancyQuestions,
				new CondEqual(initQuestion,
						new ChoiceValue(KnowledgeBaseUtils.findChoice(initQuestion,
								"pregnacyQuestions"))));

		// Rule: initQuestion = height+weight => INDICATE CONTAINER (
		// heightWeightQuestions )
		RuleFactory.createIndicationRule(
				heightWeightQuestions,
				new CondEqual(initQuestion,
						new ChoiceValue(KnowledgeBaseUtils.findChoice(initQuestion,
								"height+weight"))));

		// Rule: initQuestion = all => INDICATE CONTAINER ( pregnancyQuestions,
		// heightWeightQuestions )
		RuleFactory.createIndicationRule(
				Arrays.asList(new QASet[] {
						pregnancyQuestions, heightWeightQuestions }),
				new CondEqual(initQuestion, new ChoiceValue(KnowledgeBaseUtils.findChoice(
						initQuestion, "all"))));

		// Rule: pregnancyContainerIndication = yes => INDICATE CONTAINER (
		// pregnancyQuestions )
		RuleFactory.createIndicationRule(
				pregnancyQuestions,
				new CondEqual(pregnancyContainerIndication,
						new ChoiceValue(KnowledgeBaseUtils.findChoice(
								pregnancyContainerIndication, "yes"))));

		session = SessionFactory.createSession(kb);
	}

	@Test
	public void testIndication() {
		// SET: sex = female
		// EXPECT: question "pregnant" is INDICATED
		setValue(sex, female);
		assertEquals(
				new Indication(State.INDICATED),
				session.getBlackboard().getIndication(pregnant));

		// SET: sex = undefined
		// EXPCECT: question "pregnant" is NEURTRAL
		setValue(sex, UndefinedValue.getInstance());
		assertEquals(
				new Indication(State.NEUTRAL),
				session.getBlackboard().getIndication(pregnant));
	}

	@Test
	public void testContraIndication() {
		// SET: sex = female
		// EXPECT: question "pregnant" is INDICATED
		setValue(sex, female);
		assertEquals(
				new Indication(State.INDICATED),
				session.getBlackboard().getIndication(pregnant));

		// SET: ask_for_pregnancy = no
		// EXPCECT: question "pregnant" is CONTRA_INDICATED
		setValue(ask_for_pregnancy, dont_ask);
		assertEquals(
				new Indication(State.CONTRA_INDICATED),
				session.getBlackboard().getIndication(pregnant));

		// SET: ask_for_pregnancy = UNDEFINED
		// EXPCECT: question "pregnant" is INDICATED again
		setValue(ask_for_pregnancy, UndefinedValue.getInstance());
		assertEquals(
				new Indication(State.INDICATED),
				session.getBlackboard().getIndication(pregnant));
	}

	@Test
	public void testQContainerIndication() {
		// SET: initQuestion = all
		// EXPECT: INDICATE CONTAINER ( pregnancyQuestions,
		// heightWeightQuestions )
		setValue(initQuestion,
				new ChoiceValue(KnowledgeBaseUtils.findChoice(initQuestion, "all")));
		assertEquals(
				new Indication(State.INDICATED),
				session.getBlackboard().getIndication(pregnancyQuestions));
		assertEquals(
				new Indication(State.INDICATED),
				session.getBlackboard().getIndication(heightWeightQuestions));

		// SET: initQuestion = Undefined (i.e., retract the previous action)
		// EXPECT: NEUTRAL CONTAINER ( pregnancyQuestions, heightWeightQuestions
		// )
		setValue(initQuestion, UndefinedValue.getInstance());
		assertEquals(
				new Indication(State.NEUTRAL),
				session.getBlackboard().getIndication(pregnancyQuestions));
		assertEquals(
				new Indication(State.NEUTRAL),
				session.getBlackboard().getIndication(heightWeightQuestions));
	}

	@Test
	public void testQContainerIndicationByMutlipleRules() {
		// SET: initQuestion = all
		// EXPECT: INDICATE CONTAINER ( pregnancyQuestions,
		// heightWeightQuestions )
		setValue(initQuestion,
				new ChoiceValue(KnowledgeBaseUtils.findChoice(initQuestion, "all")));
		assertEquals(
				new Indication(State.INDICATED),
				session.getBlackboard().getIndication(pregnancyQuestions));
		assertEquals(
				new Indication(State.INDICATED),
				session.getBlackboard().getIndication(heightWeightQuestions));

		// SET: pregnancyContainerIndication = yes
		// EXPECT: INDICATE CONTAINER ( pregnancyQuestions ) // i.e. doubled
		// indication of pregnancyQuestions
		setValue(pregnancyContainerIndication, new ChoiceValue(KnowledgeBaseUtils.findChoice(
				pregnancyContainerIndication, "yes")));
		assertEquals(
				new Indication(State.INDICATED),
				session.getBlackboard().getIndication(pregnancyQuestions));

		// SET: initQuestion = Undefined (i.e., retract the previous action)
		// EXPECT: NEUTRAL CONTAINER ( heightWeightQuestions )
		// INDICATE CONTAINER ( pregnancyQuestions ) // due to doubled
		// indication
		setValue(initQuestion, UndefinedValue.getInstance());
		assertEquals(
				new Indication(State.INDICATED),
				session.getBlackboard().getIndication(pregnancyQuestions));
		assertEquals(
				new Indication(State.NEUTRAL),
				session.getBlackboard().getIndication(heightWeightQuestions));
	}

	private void setValue(Question question, Value value) {
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, question,
						value, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));
	}
}
