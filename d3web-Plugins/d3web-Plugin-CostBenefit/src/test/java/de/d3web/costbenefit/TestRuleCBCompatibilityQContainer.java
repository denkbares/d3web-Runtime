/*
 * Copyright (C) 2012 denkbares GmbH
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import org.junit.Assert;

import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.abnormality.Abnormality;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.costbenefit.blackboard.CostBenefitCaseObject;
import de.d3web.costbenefit.inference.AbortException;
import de.d3web.costbenefit.inference.ConditionalValueSetter;
import de.d3web.costbenefit.inference.ExpertMode;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.inference.ValueTransition;
import de.d3web.interview.Form;
import de.d3web.interview.Interview;
import de.d3web.interview.inference.PSMethodInterview;
import com.denkbares.plugin.test.InitPluginManager;

/**
 * Tests the combination of Rules and the CostBenefit
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 28.06.2012
 */
public class TestRuleCBCompatibilityQContainer {

	@Test
	public void testWithRules() throws IOException, AbortException {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QuestionOC state = new QuestionOC(kb, "State");
		Choice stateA = new Choice("A");
		Choice stateB = new Choice("B");
		Choice stateUnknown = new Choice("unknownState");
		state.addAlternative(stateA);
		state.addAlternative(stateB);
		state.addAlternative(stateUnknown);
		ChoiceValue valueStateA = new ChoiceValue(stateA);
		ChoiceValue valueStateB = new ChoiceValue(stateB);
		state.getInfoStore().addValue(BasicProperties.INIT, stateUnknown.getName());

		// the first QContainer is applicable on start and enables the usage of
		// the first
		QContainer first = new QContainer(kb, "First");
		QuestionOC q1 = new QuestionOC(first, "Q1");
		Choice normal = new Choice("normal");
		Choice abnormal = new Choice("abnormal");
		q1.addAlternative(normal);
		q1.addAlternative(abnormal);
		ChoiceValue valueNormal = new ChoiceValue(normal);
		ChoiceValue valueAbnormal = new ChoiceValue(abnormal);
		DefaultAbnormality.setAbnormality(q1, valueNormal, Abnormality.A0);
		DefaultAbnormality.setAbnormality(q1, valueAbnormal, Abnormality.A5);
		// create a subquestion of q1
		QuestionOC q1b = new QuestionOC(q1, "Q1b");
		Choice answer1b = new Choice("answer1b");
		ChoiceValue valueAnswer1b = new ChoiceValue(answer1b);
		DefaultAbnormality.setAbnormality(q1b, valueAnswer1b, Abnormality.A0);
		q1b.addAlternative(answer1b);
		LinkedList<QASet> followUpQuestions = new LinkedList<>();
		followUpQuestions.add(q1b);
		RuleFactory.createIndicationRule(followUpQuestions, new CondEqual(q1, valueAbnormal));
		ValueTransition vt1 = new ValueTransition(state, Arrays.asList(new ConditionalValueSetter(
				valueStateA, new CondEqual(q1, valueNormal)), new ConditionalValueSetter(
				valueStateA, new CondEqual(q1b, valueAnswer1b))));
		RuleFactory.createIndicationRule(followUpQuestions, new CondEqual(q1, valueAbnormal));
		RuleFactory.createSetValueRule(q1b, Unknown.getInstance(), new CondEqual(q1,
				valueNormal));
		new StateTransition(new CondNot(new CondEqual(state, valueStateA)), Collections.singletonList(vt1),
				first);
		first.getInfoStore().addValue(BasicProperties.COST, 1.0);

		// the second QContainer enables the usage of the target
		QContainer second = new QContainer(kb, "Second");
		QuestionOC q2 = new QuestionOC(second, "Q2");
		Choice answer2 = new Choice("answer2");
		q2.addAlternative(answer2);
		ChoiceValue valueAnswer2 = new ChoiceValue(answer2);
		DefaultAbnormality.setAbnormality(q2, valueAnswer2, Abnormality.A0);
		ValueTransition vt2 = new ValueTransition(state, Collections.singletonList(new ConditionalValueSetter(
				valueStateB, new CondEqual(q2, valueAnswer2))));
		new StateTransition(new CondEqual(state, valueStateA), Collections.singletonList(vt2), second);
		second.getInfoStore().addValue(BasicProperties.COST, 1.0);

		QContainer target = new QContainer(kb, "Target");
		QuestionOC finish = new QuestionOC(target, "Finish");
		Choice ok = new Choice("Ok");
		finish.addAlternative(ok);
		DefaultAbnormality.setAbnormality(finish, new ChoiceValue(ok), Abnormality.A0);
		new StateTransition(new CondEqual(state, valueStateB),
				Collections.emptyList(), target);
		runInterview(kb, target, false, q1, q2, finish);
		runInterview(kb, target, true, q1, q1b, q2, finish);
	}

	private void runInterview(KnowledgeBase kb, QContainer target, boolean abnorm, Question... questions) throws AbortException {
		Session session = SessionFactory.createSession(kb);
		ExpertMode em = ExpertMode.getExpertMode(session);
		em.selectTarget(target);
		Interview interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
		Form nextForm = interview.nextForm();
		CostBenefitCaseObject cbObject = session.getSessionObject(session.getPSMethodInstance(PSMethodCostBenefit.class));
		Assert.assertEquals(0, cbObject.getCurrentPathIndex());
		int i = 0;
		while (nextForm.isNotEmpty()) {
			InterviewObject next = nextForm.getActiveQuestions().get(0);
			Assert.assertEquals(questions[i++], next);
			answer(session, next, abnorm);
			nextForm = interview.nextForm();
		}
		// Check that the path has been done completely
		Assert.assertEquals(-1, cbObject.getCurrentPathIndex());
	}

	public void answer(Session session, InterviewObject interviewObject, boolean abnorm) {
		Assert.assertTrue(interviewObject instanceof QuestionOC);
		QuestionOC qoc = (QuestionOC) interviewObject;
		if (qoc.getAllAlternatives().size() == 1) {
			Choice choice = qoc.getAllAlternatives().get(0);
			session.getBlackboard().addValueFact(
					FactFactory.createUserEnteredFact(qoc, new ChoiceValue(choice)));
		}
		else {
			double abnormality;
			if (abnorm) {
				abnormality = Abnormality.A5;
			}
			else {
				abnormality = Abnormality.A0;
			}
			for (Choice c : qoc.getAllAlternatives()) {
				ChoiceValue value = new ChoiceValue(c);
				if (interviewObject.getInfoStore().getValue(BasicProperties.DEFAULT_ABNORMALITIY).getValue(
						value) == abnormality) {
					session.getBlackboard().addValueFact(
							FactFactory.createUserEnteredFact(qoc, value));
				}
			}
		}
	}

}
