/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.d3web.costbenefit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.costbenefit.inference.ConditionalValueSetter;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.inference.ValueTransition;
import de.d3web.costbenefit.inference.astar.AStarAlgorithm;
import de.d3web.costbenefit.inference.astar.AStarPath;
import de.d3web.costbenefit.inference.astar.Heuristic;
import de.d3web.costbenefit.inference.astar.State;
import de.d3web.costbenefit.inference.astar.TPHeuristic;
import de.d3web.costbenefit.model.SearchModel;
import de.d3web.costbenefit.model.Target;
import de.d3web.plugin.test.InitPluginManager;


/**
 * 
 * @author Katja Scheuermann
 * @created 14.03.2013
 */
public class TestConflictsTPHeuristic {

	/**
	 * 
	 * @created 14.03.2013
	 * @throws java.lang.Exception 
	 */
	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
	}

	/**
	 * Test method for {@link de.d3web.costbenefit.inference.astar.TPHeuristic#calculateTransitiveCondition(de.d3web.core.session.Session, de.d3web.core.knowledge.terminology.QContainer)}.
	 */
	@Test
	public void testCalculateTransitiveCondition() {
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QContainer stateContainer = new QContainer(kb, "stateContainer");

		QuestionOC stateStatus = new QuestionOC(stateContainer, "Status");
		Choice cIgnitionOn = new Choice("Igition On");
		Choice cIgnitionOff = new Choice("Igition Off");
		Choice cEngineOn = new Choice("Engine On");
		stateStatus.addAlternative(cIgnitionOn);
		stateStatus.addAlternative(cIgnitionOff);
		stateStatus.addAlternative(cEngineOn);
		stateStatus.getInfoStore().addValue(BasicProperties.INIT, cIgnitionOn.getName());

		QuestionOC stateBattery = new QuestionOC(stateContainer, "Battery");
		Choice cBatteryOn = new Choice("Battery connected");
		Choice cBatteryOff = new Choice("Battery disconnected");
		stateBattery.addAlternative(cBatteryOn);
		stateBattery.addAlternative(cBatteryOff);
		stateBattery.getInfoStore().addValue(BasicProperties.INIT, cBatteryOn.getName());

		QuestionOC stateMD = new QuestionOC(stateContainer, "Measurement Device");
		Choice cDeviceInstalled = new Choice("Device installed");
		Choice cDeviceRemoved = new Choice("Device removed");
		stateMD.addAlternative(cDeviceInstalled);
		stateMD.addAlternative(cDeviceRemoved);
		stateMD.getInfoStore().addValue(BasicProperties.INIT, cDeviceRemoved.getName());

		Condition condIgnitionOn = new CondEqual(stateStatus, new ChoiceValue(cIgnitionOn));
		Condition condIgnitionOff = new CondEqual(stateStatus, new ChoiceValue(cIgnitionOff));
		Condition condEngineOn = new CondEqual(stateStatus, new ChoiceValue(cEngineOn));

		Condition condBatteryOn = new CondEqual(stateBattery, new ChoiceValue(cBatteryOn));
		Condition condBatteryOff = new CondEqual(stateBattery, new ChoiceValue(cBatteryOff));

		Condition condMDOn = new CondEqual(stateMD, new ChoiceValue(cDeviceInstalled));
		// Condition condMDOff = new CondEqual(stateMD, new
		// ChoiceValue(cDeviceRemoved));

		Condition condIgnitionOrEngine = new CondOr(Arrays.asList(condIgnitionOn, condEngineOn));
		Condition condNotIgnitionOff = new CondNot(condIgnitionOff);

		QContainer qCIgnitionOn = new QContainer(kb, "Switch On Ignition");
		QuestionOC qIgnitionOn = new QuestionOC(qCIgnitionOn, "Please switch on the ignition!");
		qIgnitionOn.addAlternative(new Choice("OK"));

		ConditionalValueSetter cvsIgnitionOn = new ConditionalValueSetter(new ChoiceValue(
				cIgnitionOn), null);
		ValueTransition vIgnitionOn = new ValueTransition(stateStatus, Arrays.asList(cvsIgnitionOn));
		new StateTransition(new CondAnd(Arrays.asList(condBatteryOn, condIgnitionOff)),
				Arrays.asList(vIgnitionOn), qCIgnitionOn);

		QContainer qCIgnitionOff = new QContainer(kb, "Switch Off Ignition");
		QuestionOC qIgnitionOff = new QuestionOC(qCIgnitionOff, "Please switch off the ignition!");
		qIgnitionOff.addAlternative(new Choice("OK"));

		ConditionalValueSetter cvsIgnitionOff = new ConditionalValueSetter(new ChoiceValue(
				cIgnitionOff), null);
		ValueTransition vIgnitionOff = new ValueTransition(stateStatus,
				Arrays.asList(cvsIgnitionOff));
		new StateTransition(condIgnitionOn, Arrays.asList(vIgnitionOff), qCIgnitionOff);

		QContainer qCBatteryOn = new QContainer(kb, "Connect Battery");
		QuestionOC qBatteryOn = new QuestionOC(qCBatteryOn, "Please connect the battery!");
		qBatteryOn.addAlternative(new Choice("OK"));

		ConditionalValueSetter cvsBatteryOn = new ConditionalValueSetter(
				new ChoiceValue(cBatteryOn), null);
		ValueTransition vBatteryOn = new ValueTransition(stateBattery, Arrays.asList(cvsBatteryOn));
		new StateTransition(condBatteryOff, Arrays.asList(vBatteryOn), qCBatteryOn);

		QContainer qCBatteryOff = new QContainer(kb, "Disconnect Battery");
		QuestionOC qBatteryOff = new QuestionOC(qCBatteryOff, "Please disconnect the battery!");
		qBatteryOff.addAlternative(new Choice("OK"));

		ConditionalValueSetter cvsBatteryOff = new ConditionalValueSetter(new ChoiceValue(
				cBatteryOff), null);
		ValueTransition vBatteryOff = new ValueTransition(stateBattery,
				Arrays.asList(cvsBatteryOff));
		new StateTransition(new CondAnd(Arrays.asList(condIgnitionOff, condBatteryOn)),
				Arrays.asList(vBatteryOff), qCBatteryOff);

		QContainer qCMDOn = new QContainer(kb, "Install Measurement Device");
		QuestionOC qMDOn = new QuestionOC(qCMDOn, "Please install the measurement device!");
		qMDOn.addAlternative(new Choice("OK"));

		ConditionalValueSetter cvsMDOn = new ConditionalValueSetter(new ChoiceValue(
				cDeviceInstalled), null);
		ValueTransition vMDOn = new ValueTransition(stateMD, Arrays.asList(cvsMDOn));
		new StateTransition(condBatteryOff, Arrays.asList(vMDOn), qCMDOn);

		QContainer qCMDOff = new QContainer(kb, "Remove Measurement Device");
		QuestionOC qMDOff = new QuestionOC(qCMDOff, "Please remove the measurement device!");
		qMDOff.addAlternative(new Choice("OK"));

		ConditionalValueSetter cvsMDOff = new ConditionalValueSetter(
				new ChoiceValue(cDeviceRemoved), null);
		ValueTransition vMDOff = new ValueTransition(stateMD, Arrays.asList(cvsMDOff));
		new StateTransition(condBatteryOff, Arrays.asList(vMDOff), qCMDOff);

		QContainer qCMeasure = new QContainer(kb, "Measure");
		QuestionOC qMeasure = new QuestionOC(qCMeasure,
				"Please measure values with the measurement device!");
		qMeasure.addAlternative(new Choice("OK"));

		new StateTransition(new CondAnd(
				Arrays.asList(condIgnitionOrEngine, condMDOn, condBatteryOn)),
				Collections.<ValueTransition> emptyList(), qCMeasure);
		
		
		// Start a session
		Session session = SessionFactory.createSession(kb);
		Condition condition = TPHeuristic.calculateTransitiveCondition(session, qCMeasure);
		Assert.assertTrue(condition instanceof CondAnd);
		List<Condition> terms = ((CondAnd) condition).getTerms();
		Assert.assertTrue(terms.contains(condIgnitionOff));

		// calculate the heuristic with CondOr Condition and question
		// conflicting
		double hMeasurement = calculateHeuristic(kb, qCMeasure);
		Assert.assertEquals(5.0, hMeasurement);

		// min(1,1) + 1 + 1 + 1 + 1 = 5
		// min cost(conflicting state=ignition on; state=engine running) +
		// conflicting Battery=connected +
		// device=installed +
		// state=ignition off +
		// Battery=disconnected)

		new StateTransition(
				new CondAnd(Arrays.asList(condNotIgnitionOff, condMDOn, condBatteryOn)),
				Collections.<ValueTransition> emptyList(), qCMeasure);

		session = SessionFactory.createSession(kb);
		condition = TPHeuristic.calculateTransitiveCondition(session, qCMeasure);
		Assert.assertTrue(condition instanceof CondAnd);
		terms = ((CondAnd) condition).getTerms();
		Assert.assertTrue(terms.contains(condIgnitionOff));

		Assert.assertTrue(terms.contains(condBatteryOff));

		// calculate Heuristic with CondNot Condition and question conflicting
		hMeasurement = calculateHeuristic(kb, qCMeasure);

		Assert.assertEquals(5.0, hMeasurement);

		// TODO test improve distance with conflicting questions by the divided
		// transition heuristic
		// TODO
	}

	private double calculateHeuristic(KnowledgeBase kb, QContainer qCMeasure) {
		Session session = SessionFactory.createSession(kb);
		PSMethodCostBenefit costBenefit = session.getPSMethodInstance(PSMethodCostBenefit.class);
		AStarAlgorithm astar = (AStarAlgorithm) costBenefit.getSearchAlgorithm();
		Heuristic heuristic = astar.getHeuristic();
		SearchModel model = new SearchModel(session);
		Target target = new Target(qCMeasure);
		model.addTarget(target);
		heuristic.init(model);
		State state = new State(session, Collections.<Question, Value> emptyMap());
		double hMeasurement = heuristic.getDistance(model, new AStarPath(null, null, 0), state,
				qCMeasure);
		return hMeasurement;
	}

}
