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
package de.d3web.core.inference.tests;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Assert;

import org.junit.Test;

import de.d3web.abstraction.formula.FormulaNumber;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.PropagationListener;
import de.d3web.core.inference.condition.CondNumEqual;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.NumValue;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Basic test for {@link PropagationListener}
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 28.03.2012
 */
public class PropagationListenerTest {

	@Test
	public void test() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QuestionNum q1 = new QuestionNum(kb, "Q1");
		QuestionNum q2 = new QuestionNum(kb, "Q2");
		RuleFactory.createSetValueRule(q2, new FormulaNumber(10.0), new CondNumEqual(q1, 5.0));
		TestListener listener = new TestListener();
		SessionFactory.addPropagationListener(listener);
		Session session = SessionFactory.createSession(kb);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(q1, new NumValue(5.0)));
		Assert.assertEquals(2, listener.entries.size());
		Assert.assertEquals(2, listener.postpropagationEntries.size());
		for (PropagationEntry entry : listener.entries) {
			if (entry.getObject() == q1) {
				Assert.assertEquals(new NumValue(5.0), entry.getNewValue());
			}
			else if (entry.getObject() == q2) {
				Assert.assertEquals(new NumValue(10.0), entry.getNewValue());
			}
			else {
				throw new AssertionError();
			}
		}
	}

	private static class TestListener implements PropagationListener {

		private final Collection<PropagationEntry> entries = new LinkedList<>();
		private final Collection<PropagationEntry> postpropagationEntries = new LinkedList<>();

		@Override
		public void propagationStarted(Session session, Collection<PropagationEntry> entries) {
		}

		@Override
		public void propagating(Session session, PSMethod psMethod, Collection<PropagationEntry> entries) {
		}

		@Override
		public void postPropagationStarted(Session session, Collection<PropagationEntry> entries) {
			this.entries.addAll(entries);

		}

		@Override
		public void propagationFinished(Session session, Collection<PropagationEntry> entries) {
			this.postpropagationEntries.addAll(entries);
		}

	}
}
