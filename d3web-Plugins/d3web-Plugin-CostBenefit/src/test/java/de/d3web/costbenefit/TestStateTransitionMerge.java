/*
 * Copyright (C) 2026 denkbares GmbH, Germany
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.denkbares.plugin.test.InitPluginManager;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.costbenefit.inference.ConditionalValueSetter;
import de.d3web.costbenefit.inference.CostBenefitProperties;
import de.d3web.costbenefit.inference.PSMethodStateTransition;
import de.d3web.costbenefit.inference.PSMethodStateTransition.StateTransitionFact;

public class TestStateTransitionMerge {

	private KnowledgeBase knowledgeBase;
	private QuestionOC state;
	private Session session;
	private PSMethodStateTransition solver;

	@BeforeClass
	public static void init() throws IOException {
		InitPluginManager.init();
	}

	@Before
	public void setUp() {
		knowledgeBase = KnowledgeBaseUtils.createKnowledgeBase();
		QContainer states = new QContainer(knowledgeBase, "States");
		state = new QuestionOC(states, "State", "old", "middle", "new");
		session = SessionFactory.createSession(knowledgeBase);
		solver = new PSMethodStateTransition();
	}

	@Test
	public void testNewestFactWinsRegardlessOfOrder() {
		Fact oldest = new StateTransitionFact(session, state, new ChoiceValue("old"));
		Fact middle = new StateTransitionFact(session, state, new ChoiceValue("middle"));
		Fact newest = new StateTransitionFact(session, state, new ChoiceValue("new"));

		assertMergeResultForAllOrders(newest, oldest, middle, newest);
	}

	@Test
	public void testReloadedFinalFactKeepsPriority() {
		state.getInfoStore().addValue(CostBenefitProperties.UUT_STATE, CostBenefitProperties.UUTState.checkOnce);
		Fact reloaded = new DefaultFact(state, new ChoiceValue("old"), new Object(), solver);
		Fact middle = new StateTransitionFact(session, state, new ChoiceValue("middle"));
		Fact newest = new StateTransitionFact(session, state, new ChoiceValue("new"));

		assertMergeResultForAllOrders(reloaded, reloaded, middle, newest);
	}

	@Test
	public void testPermanentlyRelevantFactOverridesReloadedFinalFact() {
		state.getInfoStore().addValue(CostBenefitProperties.UUT_STATE, CostBenefitProperties.UUTState.checkOnce);
		QContainer permanentlyRelevant = new QContainer(knowledgeBase, "PermanentlyRelevant");
		permanentlyRelevant.getInfoStore().addValue(CostBenefitProperties.PERMANENTLY_RELEVANT, true);
		QuestionOC observation = new QuestionOC(permanentlyRelevant, "Observation", "confirmed");
		ConditionalValueSetter setter = new ConditionalValueSetter(new ChoiceValue("old"),
				new CondEqual(observation, new ChoiceValue("confirmed")));
		Fact preferred = new StateTransitionFact(setter, session, state, setter.getAnswer());
		Fact newer = new StateTransitionFact(session, state, new ChoiceValue("middle"));
		Fact reloaded = new DefaultFact(state, new ChoiceValue("new"), new Object(), solver);

		assertMergeResultForAllOrders(preferred, preferred, newer, reloaded);
	}

	private void assertMergeResultForAllOrders(Fact expected, Fact first, Fact second, Fact third) {
		Assert.assertSame(expected, solver.mergeFacts(new Fact[] { first, second, third }));
		Assert.assertSame(expected, solver.mergeFacts(new Fact[] { first, third, second }));
		Assert.assertSame(expected, solver.mergeFacts(new Fact[] { second, first, third }));
		Assert.assertSame(expected, solver.mergeFacts(new Fact[] { second, third, first }));
		Assert.assertSame(expected, solver.mergeFacts(new Fact[] { third, first, second }));
		Assert.assertSame(expected, solver.mergeFacts(new Fact[] { third, second, first }));
	}
}
