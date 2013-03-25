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

import junit.framework.Assert;

import org.junit.Test;

import de.d3web.core.inference.PSConfig;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.abnormality.Abnormality;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.costbenefit.inference.AbortException;
import de.d3web.costbenefit.inference.ConditionalValueSetter;
import de.d3web.costbenefit.inference.ExpertMode;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.inference.ValueTransition;
import de.d3web.costbenefit.inference.astar.AStarAlgorithm;
import de.d3web.costbenefit.inference.astar.AStarPath;
import de.d3web.costbenefit.inference.astar.State;
import de.d3web.costbenefit.inference.astar.TPHeuristic;
import de.d3web.costbenefit.model.SearchModel;
import de.d3web.costbenefit.model.Target;
import de.d3web.interview.inference.PSMethodInterview;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Tests the usage of the Property targetOnly
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 27.06.2012
 */
public class TargetOnlyTest {

	@Test
	public void test() throws IOException, AbortException {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QuestionOC state = new QuestionOC(kb, "State");
		Choice stateA = new Choice("A");
		Choice stateB = new Choice("B");
		state.addAlternative(stateA);
		state.addAlternative(stateB);
		ChoiceValue valueStateA = new ChoiceValue(stateA);
		ChoiceValue valueStateB = new ChoiceValue(stateB);

		// the first QContainer is applicable on start and enables the usage of
		// the second
		QContainer first = new QContainer(kb, "First");
		QuestionOC q1 = new QuestionOC(first, "Q1");
		Choice answer1 = new Choice("answer1");
		q1.addAlternative(answer1);
		ChoiceValue valueAnswer1 = new ChoiceValue(answer1);
		DefaultAbnormality.setAbnormality(q1, valueAnswer1, Abnormality.A0);
		ValueTransition vt1 = new ValueTransition(state, Arrays.asList(new ConditionalValueSetter(
				valueStateA, new CondEqual(q1, valueAnswer1))));
		new StateTransition(new CondAnd(Collections.<Condition> emptyList()), Arrays.asList(vt1),
				first);
		first.getInfoStore().addValue(BasicProperties.COST, 1.0);

		// the second QContainer enables the usage of the target
		QContainer second = new QContainer(kb, "Second");
		QuestionOC q2 = new QuestionOC(second, "Q2");
		Choice answer2 = new Choice("answer2");
		q2.addAlternative(answer2);
		ChoiceValue valueAnswer2 = new ChoiceValue(answer2);
		DefaultAbnormality.setAbnormality(q2, valueAnswer2, Abnormality.A0);
		ValueTransition vt2 = new ValueTransition(state, Arrays.asList(new ConditionalValueSetter(
				valueStateB, new CondEqual(q2, valueAnswer2))));
		new StateTransition(new CondEqual(state, valueStateA), Arrays.asList(vt2), second);
		second.getInfoStore().addValue(BasicProperties.COST, 1.0);

		QContainer target = new QContainer(kb, "Target");
		new QuestionNum(target, "Finish");
		new StateTransition(new CondEqual(state, valueStateB),
				Collections.<ValueTransition> emptyList(), target);

		// this QContainer can also be used to prepare StateB to enable the
		// usage of the target, but it can only be used as a target itself
		QContainer targetOnly = new QContainer(kb, "TargetOnly");
		QuestionOC q3 = new QuestionOC(targetOnly, "Q3");
		Choice answer3 = new Choice("answer3");
		q3.addAlternative(answer3);
		ChoiceValue valueAnswer3 = new ChoiceValue(answer3);
		DefaultAbnormality.setAbnormality(q3, valueAnswer3, Abnormality.A0);
		ValueTransition vt3 = new ValueTransition(state, Arrays.asList(new ConditionalValueSetter(
				valueStateB, new CondEqual(q3, valueAnswer3))));
		new StateTransition(new CondAnd(Collections.<Condition> emptyList()), Arrays.asList(vt3),
				targetOnly);
		targetOnly.getInfoStore().addValue(BasicProperties.COST, 1.0);
		targetOnly.getInfoStore().addValue(PSMethodCostBenefit.TARGET_ONLY, true);

		TPHeuristic tpHeuristic = new TPHeuristic();
		AStarAlgorithm aStarAlgorithm = new AStarAlgorithm();
		aStarAlgorithm.setHeuristic(tpHeuristic);
		PSMethodCostBenefit cb = new PSMethodCostBenefit();
		cb.setSearchAlgorithm(aStarAlgorithm);
		kb.addPSConfig(new PSConfig(PSConfig.PSState.active, cb, "PSMethodCostBenefit",
				"d3web-CostBenefit", 6));
		Session session = SessionFactory.createSession(kb);
		SearchModel model = new SearchModel(session);
		model.addTarget(new Target(targetOnly));
		model.addTarget(new Target(target));
		tpHeuristic.init(model);

		AStarPath emptyPath = new AStarPath(null, null, 0);
		State startState = new State(session, Collections.<Question, Value> emptyMap());
		// heuristic ignores targetOnly as intermediate step
		Assert.assertEquals(2.0, tpHeuristic.getDistance(model, emptyPath, startState, target));
		Assert.assertEquals(0.0, tpHeuristic.getDistance(model, emptyPath, startState, targetOnly));

		ExpertMode em = ExpertMode.getExpertMode(session);
		// calculate path to target
		em.selectTarget(target);
		QContainer[] sequence = session.getSessionObject(cb).getCurrentSequence();
		Assert.assertEquals(3, sequence.length);
		Assert.assertEquals(first, sequence[0]);
		Assert.assertEquals(second, sequence[1]);
		Assert.assertEquals(target, sequence[2]);

		em.selectTarget(targetOnly);
		sequence = session.getSessionObject(cb).getCurrentSequence();
		Assert.assertEquals(1, sequence.length);
		Assert.assertEquals(targetOnly, sequence[0]);
		Assert.assertEquals(
				q3,
				session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class)).nextForm().getInterviewObject());
		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(q3, valueAnswer3));
		// after answering q3, target should be applicable
		em.selectTarget(target);
		sequence = session.getSessionObject(cb).getCurrentSequence();
		Assert.assertEquals(1, sequence.length);
		Assert.assertEquals(target, sequence[0]);

	}

}
