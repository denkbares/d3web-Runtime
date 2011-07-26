/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.d3web.costbenefit;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.costbenefit.inference.ConditionalValueSetter;
import de.d3web.costbenefit.inference.ExpertMode;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.inference.ValueTransition;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Tests firing and proceeding of QContainers containing a ok Question and not
 * containing such a question
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 26.07.2011
 */
public class TestEmptyQContainers {

	@Test
	public void test() throws Exception {
		InitPluginManager.init();
		// prepare kb and state question
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QuestionOC state = new QuestionOC(kb, "state");
		Choice a = new Choice("a");
		state.addAlternative(a);
		Choice b = new Choice("b");
		state.addAlternative(b);
		// create TestStep (without ok Question)
		QContainer testStep = new QContainer(kb, "TestStep");
		QuestionOC testStepQuestion = new QuestionOC(testStep, "TestStep question");
		Choice lighting = new Choice("lighting");
		DefaultAbnormality.setAbnormality(testStepQuestion, new ChoiceValue(lighting),
				DefaultAbnormality.A0);
		testStepQuestion.addAlternative(lighting);
		List<ValueTransition> postTransitions = new LinkedList<ValueTransition>();
		List<ConditionalValueSetter> setters = Arrays.asList(new ConditionalValueSetter(
				new ChoiceValue(b), new CondEqual(testStepQuestion, new ChoiceValue(lighting))));
		postTransitions.add(new ValueTransition(state, setters));
		new StateTransition(new CondAnd(new LinkedList<Condition>()), postTransitions, testStep);
		// create follow up QContainer
		QContainer follower = new QContainer(kb, "follower");
		QuestionNum followerQuestion = new QuestionNum(follower, "dummy");
		new StateTransition(new CondEqual(state, new ChoiceValue(b)),
				new LinkedList<ValueTransition>(), follower);
		// create TestStep2 (with ok Question)
		QContainer testStep2 = new QContainer(kb, "TestStep2");
		QuestionOC okQuestionOC = new QuestionOC(testStep2, "okQuestion");
		Choice ok = new Choice("ok");
		okQuestionOC.addAlternative(ok);
		QuestionNum testStep2Question = new QuestionNum(testStep2, "TestStep2 question");
		postTransitions = new LinkedList<ValueTransition>();
		setters = Arrays.asList(new ConditionalValueSetter(
				new ChoiceValue(a), new CondNumGreater(testStep2Question, 5.0)));
		postTransitions.add(new ValueTransition(state, setters));
		new StateTransition(new CondAnd(new LinkedList<Condition>()), postTransitions, testStep2);
		// start session
		Session session = SessionFactory.createSession(kb);
		Blackboard blackboard = session.getBlackboard();
		// set follower as target
		ExpertMode.getExpertMode(session).selectTarget(follower);
		blackboard.addValueFact(FactFactory.createUserEnteredFact(testStepQuestion,
				new ChoiceValue(lighting)));
		Assert.assertEquals(b.toString(), blackboard.getValue(state).getValue().toString());
		// check that follower will be the next QContainer:
		Assert.assertEquals(followerQuestion,
				session.getInterview().nextForm().getInterviewObject());
		Assert.assertTrue(session.getInterview().getInterviewAgenda().getCurrentlyActiveObjects().contains(
				follower));
		// set testStep2 as target
		ExpertMode.getExpertMode(session).selectTarget(testStep2);
		// check that follower is not indicated any more:
		Assert.assertFalse(session.getInterview().getInterviewAgenda().getCurrentlyActiveObjects().contains(
				follower));
		blackboard.addValueFact(FactFactory.createUserEnteredFact(okQuestionOC, new ChoiceValue(ok)));
		Assert.assertEquals(b.toString(), blackboard.getValue(state).getValue().toString());
		blackboard.addValueFact(FactFactory.createUserEnteredFact(testStep2Question, new NumValue(
				5.1)));
		Assert.assertEquals(a.toString(), blackboard.getValue(state).getValue().toString());
		// calculate a path to TestStep
		ExpertMode.getExpertMode(session).selectTarget(follower);
		// now the teststep should be executed
		Assert.assertEquals(b.toString(), blackboard.getValue(state).getValue().toString());
		Assert.assertEquals(followerQuestion,
				session.getInterview().nextForm().getInterviewObject());
		Assert.assertTrue(session.getInterview().getInterviewAgenda().getCurrentlyActiveObjects().contains(
				follower));
		ExpertMode.getExpertMode(session).selectTarget(testStep2);
		// teststep2 should not be executed...
		Assert.assertEquals(b.toString(), blackboard.getValue(state).getValue().toString());
		blackboard.addValueFact(FactFactory.createUserEnteredFact(okQuestionOC, new ChoiceValue(ok)));
		// ...and now it should
		Assert.assertEquals(a.toString(), blackboard.getValue(state).getValue().toString());
	}

}
