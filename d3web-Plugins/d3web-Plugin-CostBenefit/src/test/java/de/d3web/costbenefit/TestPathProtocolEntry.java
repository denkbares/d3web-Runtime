/*
 * Copyright (C) 2014 denkbares GmbH
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
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;

import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.records.SessionConversionFactory;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.records.io.SessionPersistenceManager;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.protocol.ProtocolEntry;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.costbenefit.inference.AbortException;
import de.d3web.costbenefit.inference.ConditionalValueSetter;
import de.d3web.costbenefit.inference.ExpertMode;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.inference.ValueTransition;
import de.d3web.costbenefit.session.protocol.CalculatedPathEntry;
import com.denkbares.plugin.test.InitPluginManager;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 24.01.2014
 */
public class TestPathProtocolEntry {

	@Test
	public void test() throws IOException, AbortException {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		// State QContainer
		QContainer states = new QContainer(kb, "States");
		QuestionOC statusA = new QuestionOC(states, "StatusA");
		Choice choiceA_A = new Choice("ValueA_A");
		Choice choiceA_B = new Choice("ValueA_B");
		ChoiceValue valueA_B = new ChoiceValue(choiceA_B);
		statusA.addAlternative(choiceA_A);
		statusA.addAlternative(choiceA_B);
		// QContainer1 (always applicable, always setting statusA to valueA_B
		QContainer qContainer1 = new QContainer(kb, "QContainer1");
		new StateTransition(null, Collections.singletonList(new ValueTransition(statusA,
				Collections.singletonList(new ConditionalValueSetter(valueA_B, null)))), qContainer1);
		QuestionOC next = new QuestionOC(qContainer1, "Next");
		Choice okChoice = new Choice("ok");
		next.addAlternative(okChoice);
		// QContainer2 (precondition valueA_B, no post transition
		QContainer qContainer2 = new QContainer(kb, "QContainer2");
		new StateTransition(new CondEqual(statusA, valueA_B),
				Collections.emptyList(), qContainer2);
		QuestionOC finish = new QuestionOC(qContainer2, "Finish");
		Choice doneChoice = new Choice("ok");
		finish.addAlternative(doneChoice);
		Session session = SessionFactory.createSession(kb);
		ExpertMode expertMode = ExpertMode.getExpertMode(session);
		expertMode.selectTarget(qContainer2);
		CalculatedPathEntry originalEntry = getEntry(session);
		SessionRecord record = SessionConversionFactory.copyToSessionRecord(session);
		File file = new File("target/session/CaclulatedPathEntry.xml");
		file.getParentFile().mkdirs();
		SessionPersistenceManager.getInstance().saveSessions(file, Collections.singletonList(record));
		Collection<SessionRecord> loadSessions = SessionPersistenceManager.getInstance().loadSessions(
				file);
		Assert.assertEquals(1, loadSessions.size());
		Session reloadedSession = SessionConversionFactory.copyToSession(kb,
				loadSessions.iterator().next());
		CalculatedPathEntry reloadedEntry = getEntry(reloadedSession);
		Assert.assertEquals(originalEntry.getCalculationTime(), reloadedEntry.getCalculationTime());
		Assert.assertEquals(originalEntry.getDate(), reloadedEntry.getDate());
		Assert.assertTrue(Arrays.equals(originalEntry.getPath(), reloadedEntry.getPath()));
	}

	private static CalculatedPathEntry getEntry(Session session) {
		List<CalculatedPathEntry> entries = new LinkedList<>();
		for (ProtocolEntry entry : session.getProtocol().getProtocolHistory()) {
			if (entry instanceof CalculatedPathEntry) {
				entries.add((CalculatedPathEntry) entry);
			}
		}
		Assert.assertEquals(1, entries.size());
		return entries.get(0);
	}

}
