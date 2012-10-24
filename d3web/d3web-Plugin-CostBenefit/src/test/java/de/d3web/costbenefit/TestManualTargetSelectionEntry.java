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
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import de.d3web.core.inference.PSConfig;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.records.SessionConversionFactory;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.records.io.SessionPersistenceManager;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.protocol.ProtocolEntry;
import de.d3web.costbenefit.ids.IterativeDeepeningSearchAlgorithm;
import de.d3web.costbenefit.inference.AbortException;
import de.d3web.costbenefit.inference.ExpertMode;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;
import de.d3web.costbenefit.model.Target;
import de.d3web.costbenefit.session.protocol.ManualTargetSelectionEntry;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Tests ManualTargetSelectionEntry (including its persistence)
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 28.06.2012
 */
public class TestManualTargetSelectionEntry {

	private File file = new File("target/session/ManualTargetSelectionEntry.xml");

	@Test
	public void test() throws AbortException, IOException {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		PSMethodCostBenefit psMethod = new PSMethodCostBenefit();
		// use IDS because we also test multitarget, which are not fully
		// supported by AStar
		psMethod.setSearchAlgorithm(new IterativeDeepeningSearchAlgorithm());
		kb.addPSConfig(new PSConfig(PSConfig.PSState.active, psMethod,
				"PSMethodCostBenefit",
				"d3web-CostBenefit", 6));
		QContainer target1 = new QContainer(kb, "Target1");
		QContainer target2 = new QContainer(kb, "Target2");
		Target multiTarget = new Target(Arrays.asList(target1, target2));
		Session session = SessionFactory.createSession(kb);
		ExpertMode em = ExpertMode.getExpertMode(session);
		em.selectTarget(target1);
		em.selectTarget(target2);
		em.selectTarget(multiTarget);
		checkEntries(target1, target2, session, true);
		// test persistence
		file.getParentFile().mkdirs();
		SessionRecord sessionRecord = SessionConversionFactory.copyToSessionRecord(session);
		SessionPersistenceManager.getInstance().saveSessions(file, Arrays.asList(sessionRecord));
		Collection<SessionRecord> loadedRecords = SessionPersistenceManager.getInstance().loadSessions(
				file);
		Assert.assertEquals(1, loadedRecords.size());
		Session reloadedSession = SessionConversionFactory.copyToSession(kb,
				loadedRecords.iterator().next());
		checkEntries(target1, target2, reloadedSession, true);
		Assert.assertTrue(removeLastEntry(reloadedSession));
		checkEntries(target1, target2, reloadedSession, false);
	}

	private void checkEntries(QContainer target1, QContainer target2, Session session, boolean containsMultitarget) {
		List<ManualTargetSelectionEntry> manualTargetSelectionEntries = getEntries(session);
		Assert.assertEquals(target1.getName(),
				manualTargetSelectionEntries.get(0).getTargetNames()[0]);
		Assert.assertEquals(target2.getName(),
				manualTargetSelectionEntries.get(1).getTargetNames()[0]);
		if (containsMultitarget) {
			Assert.assertEquals(target1.getName(),
					manualTargetSelectionEntries.get(2).getTargetNames()[0]);
			Assert.assertEquals(target2.getName(),
					manualTargetSelectionEntries.get(2).getTargetNames()[1]);
		}
	}

	public List<ManualTargetSelectionEntry> getEntries(Session session) {
		List<ManualTargetSelectionEntry> manualTargetSelectionEntries = new LinkedList<ManualTargetSelectionEntry>();
		for (ProtocolEntry entry : session.getProtocol().getProtocolHistory()) {
			if (entry instanceof ManualTargetSelectionEntry) {
				manualTargetSelectionEntries.add((ManualTargetSelectionEntry) entry);
			}
		}
		return manualTargetSelectionEntries;
	}

	private boolean removeLastEntry(Session session) {
		ManualTargetSelectionEntry lastTargetEntry = null;
		for (ProtocolEntry entry : session.getProtocol().getProtocolHistory()) {
			if (entry instanceof ManualTargetSelectionEntry) {
				lastTargetEntry = (ManualTargetSelectionEntry) entry;
			}
		}
		if (lastTargetEntry != null) {
			return session.getProtocol().removeEntry(lastTargetEntry);
		}
		return false;
	}

}
