/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.costbenefit.inference.ConditionalValueSetter;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.inference.ValueTransition;
import de.d3web.costbenefit.inference.astar.TPHeuristic;
import de.d3web.plugin.test.InitPluginManager;

/**
 * 
 * @author Katja Scheuermann & Markus Friedrich
 * @created 08.05.2013
 */
public class TestExtendedConflictsTPHeuristic {

	private KnowledgeBase kb;
	private Condition condLightSwitchOff;
	private Condition condLightSwitchDay;
	private Condition condLightSwitchDriving;
	private Condition condLightSwitchFront;
	private Condition condLightSwitchRear;
	private Condition condAdapterOn;
	private Condition condAdapterOff;
	private QContainer qCMeasure;
	private Choice cFrontFogLight;
	private QuestionOC stateLightSwitch;
	private QContainer stateContainer;
	private ValueTransition vRearFog;
	private QContainer qCSwitchRearFog;
	private QContainer qCSwitchDriving;
	private ValueTransition vDriving;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();

		kb = KnowledgeBaseUtils.createKnowledgeBase();
		stateContainer = new QContainer(kb, "states");
		stateLightSwitch = new QuestionOC(stateContainer, "light switch");
		Choice cOff = new Choice("Off");
		Choice cDaytimeRunningLight = new Choice("daytime running light");
		Choice cDrivingLight = new Choice("driving light");
		cFrontFogLight = new Choice("front fog light");
		Choice cRearFogLight = new Choice("rear fog light");

		stateLightSwitch.addAlternative(cOff);
		stateLightSwitch.addAlternative(cDaytimeRunningLight);
		stateLightSwitch.addAlternative(cDrivingLight);
		stateLightSwitch.addAlternative(cFrontFogLight);
		stateLightSwitch.addAlternative(cRearFogLight);

		stateLightSwitch.getInfoStore().addValue(BasicProperties.INIT, cRearFogLight.getName());

		QuestionOC stateAdapter = new QuestionOC(stateContainer, "Adapter");
		Choice cAdapterRemoved = new Choice("removed");
		Choice cAdapterInstalled = new Choice("installed");
		stateAdapter.addAlternative(cAdapterRemoved);
		stateAdapter.addAlternative(cAdapterInstalled);
		stateAdapter.getInfoStore().addValue(BasicProperties.INIT, cAdapterRemoved.getName());

		condLightSwitchOff = new CondEqual(stateLightSwitch, new ChoiceValue(cOff));
		condLightSwitchDay = new CondEqual(stateLightSwitch, new ChoiceValue(
				cDaytimeRunningLight));
		condLightSwitchDriving = new CondEqual(stateLightSwitch, new ChoiceValue(
				cDrivingLight));
		condLightSwitchFront = new CondEqual(stateLightSwitch, new ChoiceValue(
				cFrontFogLight));
		condLightSwitchRear = new CondEqual(stateLightSwitch, new ChoiceValue(
				cRearFogLight));

		condAdapterOn = new CondEqual(stateAdapter, new ChoiceValue(cAdapterInstalled));
		condAdapterOff = new CondEqual(stateAdapter, new ChoiceValue(cAdapterRemoved));

		QContainer qCSwitchDaytime = new QContainer(kb, "Switch on daytime running light");
		QuestionOC qDaytimeOn = new QuestionOC(qCSwitchDaytime,
				"Please switch the light switch to the daytime running light!");
		qDaytimeOn.addAlternative(new Choice("OK"));

		ConditionalValueSetter cvsDaytimeRunningLights = new ConditionalValueSetter(
				new ChoiceValue(
						cDaytimeRunningLight), null);
		ValueTransition vDaytime = new ValueTransition(stateLightSwitch,
				Arrays.asList(cvsDaytimeRunningLights));
		new StateTransition(new CondAnd(Arrays.asList(condLightSwitchOff)),
				Arrays.asList(vDaytime), qCSwitchDaytime);

		qCSwitchDriving = new QContainer(kb, "Switch on driving light");
		QuestionOC qDrivingOn = new QuestionOC(qCSwitchDriving,
				"Please switch the light switch to the driving light!");
		qDrivingOn.addAlternative(new Choice("OK"));

		ConditionalValueSetter cvsDrivingLights = new ConditionalValueSetter(new ChoiceValue(
				cDrivingLight), null);
		vDriving = new ValueTransition(stateLightSwitch,
				Arrays.asList(cvsDrivingLights));
		new StateTransition(new CondAnd(Arrays.asList(condLightSwitchDay)),
				Arrays.asList(vDriving), qCSwitchDriving);

		QContainer qCSwitchFrontFog = new QContainer(kb, "Switch on front fog lights");
		QuestionOC qFrontFogOn = new QuestionOC(qCSwitchDriving,
				"Please switch the light switch to the front fog lights!");
		qFrontFogOn.addAlternative(new Choice("OK"));

		ConditionalValueSetter cvsFrontFogLights = new ConditionalValueSetter(new ChoiceValue(
				cFrontFogLight), null);
		ValueTransition vFrontFog = new ValueTransition(stateLightSwitch,
				Arrays.asList(cvsFrontFogLights));
		new StateTransition(new CondAnd(Arrays.asList(condLightSwitchDriving)),
				Arrays.asList(vFrontFog), qCSwitchFrontFog);

		qCSwitchRearFog = new QContainer(kb, "Switch on rear fog lights");
		QuestionOC qRearFogOn = new QuestionOC(qCSwitchDriving,
				"Please switch the light switch to the rear fog lights!");
		qRearFogOn.addAlternative(new Choice("OK"));

		ConditionalValueSetter cvsRearFogLights = new ConditionalValueSetter(new ChoiceValue(
				cRearFogLight), null);
		vRearFog = new ValueTransition(stateLightSwitch,
				Arrays.asList(cvsRearFogLights));
		new StateTransition(new CondAnd(Arrays.asList(condLightSwitchFront)),
				Arrays.asList(vRearFog), qCSwitchRearFog);

		QContainer qCInsertAdapter = new QContainer(kb, "Please Insert the Adapter");
		QuestionOC qInsertAdapter = new QuestionOC(qCInsertAdapter,
				"Please insert the measurement adapter!");
		qInsertAdapter.addAlternative(new Choice("OK"));

		ConditionalValueSetter cvsAdapterOn = new ConditionalValueSetter(new ChoiceValue(
				cAdapterInstalled), null);
		ValueTransition vAdapter = new ValueTransition(stateAdapter, Arrays.asList(cvsAdapterOn));
		new StateTransition(new CondAnd(Arrays.asList(condLightSwitchOff, condAdapterOff)),
				Arrays.asList(vAdapter), qCInsertAdapter);

		qCMeasure = new QContainer(kb, "Please start the measurement");
		QuestionOC qMeasure = new QuestionOC(qCInsertAdapter,
				"Please start the measurement!");
		qMeasure.addAlternative(new Choice("OK"));

		new StateTransition(new CondAnd(Arrays.asList(condLightSwitchRear, condAdapterOn)),
				Collections.<ValueTransition> emptyList(), qCMeasure);
	}

	/**
	 * The test checks the case, that the original condition is conflicting and
	 * already fulfilled.
	 * 
	 * @created 08.05.2013
	 */
	@Test
	public void testfulfilledOriginalCondition() {
		Session session = SessionFactory.createSession(kb);
		Condition condition = TPHeuristic.calculateTransitiveCondition(session, qCMeasure);
		Assert.assertTrue(condition instanceof CondAnd);
		List<Condition> terms = ((CondAnd) condition).getTerms();
		Assert.assertTrue(terms.contains(condLightSwitchFront));
		Assert.assertTrue(terms.contains(condLightSwitchDriving));
		Assert.assertTrue(terms.contains(condLightSwitchDay));
	}

	/**
	 * The test checks the case, that a candidate preparing a conflicting
	 * condition is fulfilled
	 * 
	 * @created 08.05.2013
	 */
	@Test
	public void testfulfilledPreparingCondition() {
		// change start state to front fog light
		stateLightSwitch.getInfoStore().addValue(BasicProperties.INIT, cFrontFogLight.getName());
		Session session = SessionFactory.createSession(kb);
		Condition condition = TPHeuristic.calculateTransitiveCondition(session, qCMeasure);
		Assert.assertTrue(condition instanceof CondAnd);
		List<Condition> terms = ((CondAnd) condition).getTerms();
		Assert.assertTrue(terms.contains(condLightSwitchFront));
		Assert.assertTrue(terms.contains(condLightSwitchDriving));
		Assert.assertTrue(terms.contains(condLightSwitchDay));
	}

	/**
	 * This test proofs if another state question is already fulfilled, that
	 * this question must not be expanded because it does not refer to the
	 * original conflicted state question.
	 * 
	 * @created 08.05.2013
	 */
	@Test
	public void testfulfilledPreparingConditionBadCase() {
		// change start state to front fog light
		stateLightSwitch.getInfoStore().addValue(BasicProperties.INIT, cFrontFogLight.getName());
		// adding another state question
		QuestionOC stateFogLightSwitch = new QuestionOC(stateContainer, "fog light switch");
		Choice cFogOn = new Choice("On");
		Choice cFogOff = new Choice("Off");
		stateFogLightSwitch.addAlternative(cFogOn);
		stateFogLightSwitch.addAlternative(cFogOff);
		stateFogLightSwitch.getInfoStore().addValue(BasicProperties.INIT, cFogOn.getName());
		Condition condFogOn = new CondEqual(stateFogLightSwitch, new ChoiceValue(cFogOn));
		Condition condFogOff = new CondEqual(stateFogLightSwitch, new ChoiceValue(cFogOff));
		// switching to rear fog light requires the new state
		new StateTransition(new CondAnd(Arrays.asList(condLightSwitchFront, condFogOn)),
				Arrays.asList(vRearFog), qCSwitchRearFog);
		// add qcontainers switching the new state on and off
		QContainer qCSwitchFog = new QContainer(kb, "Switch on the fog light switch");
		QuestionOC qSwitchFog = new QuestionOC(qCSwitchFog,
				"Please switch on the fog light switch!");
		qSwitchFog.addAlternative(new Choice("OK"));

		ConditionalValueSetter cvsFogOn = new ConditionalValueSetter(
				new ChoiceValue(
						cFogOn), null);
		ValueTransition vFogOn = new ValueTransition(stateFogLightSwitch,
				Arrays.asList(cvsFogOn));
		new StateTransition(new CondAnd(Arrays.asList(condFogOff)),
				Arrays.asList(vFogOn), qCSwitchFog);

		QContainer qCSwitchFogOff = new QContainer(kb, "Switch off the fog light switch");
		QuestionOC qSwitchFogOff = new QuestionOC(qCSwitchFog,
				"Please switch off the fog light switch!");
		qSwitchFogOff.addAlternative(new Choice("OK"));

		ConditionalValueSetter cvsFogOff = new ConditionalValueSetter(
				new ChoiceValue(
						cFogOff), null);
		ValueTransition vFogOff = new ValueTransition(stateFogLightSwitch,
				Arrays.asList(cvsFogOff));
		new StateTransition(new CondAnd(Arrays.asList(condFogOn)),
				Arrays.asList(vFogOff), qCSwitchFogOff);

		Session session = SessionFactory.createSession(kb);
		Condition condition = TPHeuristic.calculateTransitiveCondition(session, qCMeasure);
		Assert.assertTrue(condition instanceof CondAnd);
		List<Condition> terms = ((CondAnd) condition).getTerms();
		Assert.assertTrue(terms.contains(condLightSwitchFront));
		Assert.assertTrue(terms.contains(condLightSwitchDriving));
		Assert.assertTrue(terms.contains(condLightSwitchDay));
		// cond fog on can be used, but is fullfilled anyway
		// Update 17.04.2015: it could not be used, if the heuristic would be
		// optimistic in all cases, see comment in TPHeuristic
		Assert.assertTrue(terms.contains(condFogOn));
		// condFogOff must not be used -> state question differs from original
		// fulfilled condition, recursive adding is blocked
		Assert.assertFalse(terms.contains(condFogOff));
	}

	/**
	 * The test checks the case, that a candidate preparing a conflicting
	 * condition is fulfilled. In this special scenario the recursion stops
	 * because of a value conflict in a recursively added condition.
	 * 
	 * @created 08.05.2013
	 */
	@Test
	public void testfulfilledPreparingConditionWithRecursiveValueConflict() {
		// change start state to front fog light
		stateLightSwitch.getInfoStore().addValue(BasicProperties.INIT, cFrontFogLight.getName());
		// switching to driving light is also possible with lights off
		new StateTransition(new CondOr(Arrays.asList(condLightSwitchDay, condLightSwitchOff)),
				Arrays.asList(vDriving), qCSwitchDriving);
		Session session = SessionFactory.createSession(kb);
		Condition condition = TPHeuristic.calculateTransitiveCondition(session, qCMeasure);
		Assert.assertTrue(condition instanceof CondAnd);
		List<Condition> terms = ((CondAnd) condition).getTerms();
		Assert.assertTrue(terms.contains(condLightSwitchFront));
		Assert.assertTrue(terms.contains(condLightSwitchDriving));
		Assert.assertFalse(terms.contains(condLightSwitchDay));
	}

	/**
	 * The test checks the case, that a candidate preparing a conflicting
	 * condition is fulfilled. In this special scenario the candidate cannot be
	 * added because it has a value conflict
	 * 
	 * @created 08.05.2013
	 */
	@Test
	public void testfulfilledPreparingConditionWithValueConflict() {
		// change start state to front fog light
		stateLightSwitch.getInfoStore().addValue(BasicProperties.INIT, cFrontFogLight.getName());
		// switching to rear fog light is also possible with lights off
		new StateTransition(new CondOr(Arrays.asList(condLightSwitchFront, condLightSwitchOff)),
				Arrays.asList(vRearFog), qCSwitchRearFog);
		Session session = SessionFactory.createSession(kb);
		Condition condition = TPHeuristic.calculateTransitiveCondition(session, qCMeasure);
		Assert.assertTrue(condition instanceof CondAnd);
		List<Condition> terms = ((CondAnd) condition).getTerms();
		Assert.assertFalse(terms.contains(condLightSwitchFront));
		Assert.assertFalse(terms.contains(condLightSwitchDriving));
		Assert.assertFalse(terms.contains(condLightSwitchDay));
	}

}
