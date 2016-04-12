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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.PSConfig;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.abnormality.Abnormality;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.records.SessionConversionFactory;
import de.d3web.core.records.SessionRecord;
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
import de.d3web.xcl.XCLModel;
import de.d3web.xcl.inference.PSMethodXCL;

/**
 * Tests the usage of different properties
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 27.06.2012
 */
public class PropertiesTest {

	private static final String TARGET_ONLY_NAME = "TargetOnly";
	private static final String FIRST_NAME = "First";
	private static final String SECOND_NAME = "Second";
	private static final String PERMANENTLY_NAME = "Permanently Relevant";
	private KnowledgeBase kb;
	private QuestionOC state;
	private QuestionOC indicator;
	private QContainer targetOnly;
	private QContainer target;
	private QContainer first;
	private QContainer second;
	private QContainer permanentlyRelevant;
	private ChoiceValue valueAnswer1;
	private ChoiceValue valueAnswer2;
	private ChoiceValue valueAnswer3;
	private ChoiceValue valueAnswer51;
	private ChoiceValue valueAnswer52;
	private QuestionOC q1;
	private QuestionOC q2;
	private QuestionOC q3;
	private QuestionOC q4;
	private QuestionOC q5;
	private ChoiceValue choiceValue4;
	private ChoiceValue choiceValue5;
	private ChoiceValue choiceValue6;
	private ChoiceValue valueStateA;
	private ChoiceValue valueStateWorks;
	private ChoiceValue valueStateNotWorks;
	private ChoiceValue valueStateB;
	private Solution s1;
	private Solution s2;
	private Solution s3;
	private PSMethodCostBenefit cb;
	private TPHeuristic tpHeuristic;

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		tpHeuristic = new TPHeuristic();
		AStarAlgorithm aStarAlgorithm = new AStarAlgorithm();
		aStarAlgorithm.setHeuristic(tpHeuristic);
		cb = new PSMethodCostBenefit();
		cb.setSearchAlgorithm(aStarAlgorithm);

		kb.addPSConfig(new PSConfig(PSConfig.PSState.active, cb, "PSMethodCostBenefit",
				"d3web-CostBenefit", 6));
		state = new QuestionOC(kb, "State");
		Choice stateA = new Choice("A");
		Choice stateB = new Choice("B");
		state.addAlternative(stateA);
		state.addAlternative(stateB);
		valueStateA = new ChoiceValue(stateA);
		valueStateB = new ChoiceValue(stateB);

		indicator = new QuestionOC(kb, "Indicator");
		Choice works = new Choice("works");
		Choice notWorks = new Choice("not work");
		indicator.addAlternative(works);
		indicator.addAlternative(notWorks);
		valueStateWorks = new ChoiceValue(works);
		valueStateNotWorks = new ChoiceValue(notWorks);
		indicator.getInfoStore().addValue(PSMethodCostBenefit.FINAL_QUESTION, true);

		first = new QContainer(kb, FIRST_NAME);
		q1 = new QuestionOC(first, "Q1");
		Choice answer1 = new Choice("answer1");
		q1.addAlternative(answer1);
		valueAnswer1 = new ChoiceValue(answer1);
		DefaultAbnormality.setAbnormality(q1, valueAnswer1, Abnormality.A0);
		ValueTransition vt1 = new ValueTransition(state, Arrays.asList(new ConditionalValueSetter(
				valueStateA, new CondEqual(q1, valueAnswer1))));
		new StateTransition(new CondAnd(Collections.<Condition> emptyList()), Arrays.asList(vt1),
				first);
		first.getInfoStore().addValue(BasicProperties.COST, 1.0);

		second = new QContainer(kb, SECOND_NAME);
		q2 = new QuestionOC(second, "Q2");
		Choice answer2 = new Choice("answer2");
		q2.addAlternative(answer2);
		valueAnswer2 = new ChoiceValue(answer2);
		DefaultAbnormality.setAbnormality(q2, valueAnswer2, Abnormality.A0);
		ValueTransition vt2 = new ValueTransition(state, Arrays.asList(new ConditionalValueSetter(
				valueStateB, new CondEqual(q2, valueAnswer2))));
		ValueTransition vtIndicator = new ValueTransition(indicator,
				Arrays.asList(new ConditionalValueSetter(valueStateWorks, new CondEqual(q2,
						valueAnswer2))));
		new StateTransition(new CondEqual(state, valueStateA), Arrays.asList(vt2, vtIndicator),
				second);
		second.getInfoStore().addValue(BasicProperties.COST, 1.0);

		permanentlyRelevant = new QContainer(kb, PERMANENTLY_NAME);
		q4 = new QuestionOC(permanentlyRelevant, "Q4");
		Choice answer4 = new Choice("fourth");
		Choice answer5 = new Choice("fifth");
		Choice answer6 = new Choice("sixth");
		q4.addAlternative(answer4);
		q4.addAlternative(answer5);
		q4.addAlternative(answer6);
		choiceValue4 = new ChoiceValue(answer4);
		choiceValue5 = new ChoiceValue(answer5);
		choiceValue6 = new ChoiceValue(answer6);

		DefaultAbnormality.setAbnormality(q4, choiceValue4, Abnormality.A0);
		ValueTransition vt4 = new ValueTransition(state, Arrays.asList(new ConditionalValueSetter(
				valueStateB, new CondEqual(q4, choiceValue4)), new ConditionalValueSetter(
				valueStateA, new CondEqual(q4, choiceValue5))));
		ValueTransition vtWork4 = new ValueTransition(indicator, Arrays.asList(
				new ConditionalValueSetter(
						valueStateWorks, new CondEqual(q4, choiceValue4)),
				new ConditionalValueSetter(
						valueStateNotWorks, new CondEqual(q4, choiceValue5))));
		new StateTransition(new CondOr(Arrays.<Condition> asList(new CondEqual(state, valueStateA),
				new CondEqual(state, valueStateB))), Arrays.asList(vt4, vtWork4),
				permanentlyRelevant);
		permanentlyRelevant.getInfoStore().addValue(PSMethodCostBenefit.PERMANENTLY_RELEVANT, true);
		permanentlyRelevant.getInfoStore().addValue(BasicProperties.COST, 0.1);

		target = new QContainer(kb, "Target");
		q5 = new QuestionOC(target, "Q5");
		Choice answer51 = new Choice("answer5.1");
		Choice answer52 = new Choice("answer5.2");
		q5.addAlternative(answer51);
		q5.addAlternative(answer52);
		valueAnswer51 = new ChoiceValue(answer51);
		valueAnswer52 = new ChoiceValue(answer52);
		new QuestionNum(target, "Finish");
		new StateTransition(new CondAnd(Arrays.<Condition> asList(
				new CondEqual(state, valueStateB),
				new CondEqual(indicator, valueStateWorks))),
				Collections.<ValueTransition> emptyList(), target);

		targetOnly = new QContainer(kb, TARGET_ONLY_NAME);
		q3 = new QuestionOC(targetOnly, "Q3");
		Choice answer3 = new Choice("answer3");
		q3.addAlternative(answer3);
		valueAnswer3 = new ChoiceValue(answer3);
		DefaultAbnormality.setAbnormality(q3, valueAnswer3, Abnormality.A0);
		ValueTransition vt3 = new ValueTransition(state, Arrays.asList(new ConditionalValueSetter(
				valueStateB, new CondEqual(q3, valueAnswer3))));
		ValueTransition vt3works = new ValueTransition(indicator,
				Arrays.asList(new ConditionalValueSetter(
						valueStateWorks, new CondEqual(q3, valueAnswer3))));
		new StateTransition(new CondAnd(Collections.<Condition> emptyList()), Arrays.asList(vt3,
				vt3works),
				targetOnly);
		targetOnly.getInfoStore().addValue(BasicProperties.COST, 1.0);
		targetOnly.getInfoStore().addValue(PSMethodCostBenefit.TARGET_ONLY, true);

		s1 = new Solution(kb, "S1");
		s2 = new Solution(kb, "S2");
		s3 = new Solution(kb, "S3");

		XCLModel.insertXCLRelation(kb, new CondEqual(q4, choiceValue4), s1);
		XCLModel.insertXCLRelation(kb, new CondEqual(q4, choiceValue5), s2);
		XCLModel.insertXCLRelation(kb, new CondEqual(q4, choiceValue6), s3);
		XCLModel.insertXCLRelation(kb, new CondEqual(q5, valueAnswer51), s1);
		XCLModel.insertXCLRelation(kb, new CondEqual(q5, valueAnswer51), s2);
		XCLModel.insertXCLRelation(kb, new CondEqual(q5, valueAnswer52), s3);
	}

	@Test
	public void testInterview() throws IOException, AbortException {
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
		// normally permanently relevant would be chosen because of the lower
		// costs but permanently
		// relevant QContainer must not be chosen as intermediate QContainer
		Assert.assertEquals(second, sequence[1]);
		Assert.assertEquals(target, sequence[2]);

		em.selectTarget(targetOnly);
		sequence = session.getSessionObject(cb).getCurrentSequence();
		Assert.assertEquals(1, sequence.length);
		Assert.assertEquals(targetOnly, sequence[0]);
		Assert.assertEquals(
				q3,
				session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class)).nextForm().getActiveQuestions().get(
						0));
		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(q3, valueAnswer3));
		// after answering q3, target should be applicable
		em.selectTarget(target);
		sequence = session.getSessionObject(cb).getCurrentSequence();
		Assert.assertEquals(1, sequence.length);
		Assert.assertEquals(target, sequence[0]);

	}

	@Test
	public void testPermanentlyRelevant() throws AbortException, IOException {
		Session session = SessionFactory.createSession(kb);
		ExpertMode expertMode = ExpertMode.getExpertMode(session);
		Assert.assertTrue(expertMode.getApplicablePermanentlyRelevantQContainers().isEmpty());
		expertMode.selectTarget(first);
		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(q1, valueAnswer1));
		Collection<QContainer> applicablePermanentlyRelevantQContainers = expertMode.getApplicablePermanentlyRelevantQContainers();
		Assert.assertTrue(applicablePermanentlyRelevantQContainers.size() == 1);
		Assert.assertEquals(permanentlyRelevant,
				applicablePermanentlyRelevantQContainers.iterator().next());
		expertMode.selectTarget(permanentlyRelevant);
		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(q4, choiceValue5));
		SessionRecord record = SessionConversionFactory.copyToSessionRecord(session);
		expertMode.selectTarget(second);
		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(q2, valueAnswer2));
		Assert.assertEquals(valueStateB, session.getBlackboard().getValueFact(state).getValue());
		Assert.assertEquals(valueStateNotWorks,
				session.getBlackboard().getValueFact(indicator).getValue());
		// check if the priority also works for reloaded sessions
		// (StateTransitionFacts are converted to DefaultFacts when reloading a
		// session)
		Session reloadedSession = SessionConversionFactory.copyToSession(kb, record);
		ExpertMode emReloaded = ExpertMode.getExpertMode(reloadedSession);
		Assert.assertEquals(valueStateA,
				reloadedSession.getBlackboard().getValueFact(state).getValue());
		emReloaded.selectTarget(second);
		reloadedSession.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(q2, valueAnswer2));
		Assert.assertEquals(valueStateB,
				reloadedSession.getBlackboard().getValueFact(state).getValue());
		Assert.assertEquals(valueStateNotWorks,
				reloadedSession.getBlackboard().getValueFact(indicator).getValue());

	}

	@Test
	public void testPermanentlyRelevantTarget() throws AbortException {
		Session session = SessionFactory.createSession(kb);
		QContainer[] sequence = session.getSessionObject(cb).getCurrentSequence();
		Assert.assertEquals(3, sequence.length);
		// the information gain of the permanent relevant QContainer must be
		// higher then the information gain of the target
		PSMethodXCL xcl = session.getPSMethodInstance(PSMethodXCL.class);
		double informationGainTarget = xcl.getInformationGain(Arrays.asList(q5),
				Arrays.asList(s1, s2, s3), session);
		double informationGainPermanentlyRelevant = xcl.getInformationGain(Arrays.asList(q4),
				Arrays.asList(s1, s2, s3), session);
		Assert.assertTrue(informationGainTarget < informationGainPermanentlyRelevant);
		// normally permanently relevant would be chosen because of the lower
		// costs and the higher benefit
		Assert.assertEquals(first, sequence[0]);
		Assert.assertEquals(second, sequence[1]);
		Assert.assertEquals(target, sequence[2]);
	}

	@Test
	public void testPersistence() throws IOException {
		File file = new File("target/kb/TestProperties");
		file.getParentFile().mkdirs();
		PersistenceManager.getInstance().save(kb, file);
		KnowledgeBase reloadedKB = PersistenceManager.getInstance().load(file);
		QContainer reloadedFirst = reloadedKB.getManager().searchQContainer(FIRST_NAME);
		Assert.assertFalse(reloadedFirst.getInfoStore().getValue(
				PSMethodCostBenefit.PERMANENTLY_RELEVANT));
		Assert.assertFalse(reloadedFirst.getInfoStore().getValue(PSMethodCostBenefit.TARGET_ONLY));
		QContainer reloadedPermanentlyRelevant = reloadedKB.getManager().searchQContainer(
				PERMANENTLY_NAME);
		Assert.assertTrue(reloadedPermanentlyRelevant.getInfoStore().getValue(
				PSMethodCostBenefit.PERMANENTLY_RELEVANT));
		QContainer reloadedTargetOnly = reloadedKB.getManager().searchQContainer(TARGET_ONLY_NAME);
		Assert.assertFalse(reloadedTargetOnly.getInfoStore().getValue(
				PSMethodCostBenefit.PERMANENTLY_RELEVANT));
		Assert.assertTrue(reloadedTargetOnly.getInfoStore().getValue(
				PSMethodCostBenefit.TARGET_ONLY));
	}

}
