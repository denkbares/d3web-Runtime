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
package de.d3web.core.records.io;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.records.SessionConversionFactory;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.records.io.fragments.ActualQContainerEntryHandler;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.protocol.ActualQContainerEntry;
import de.d3web.core.session.protocol.ProtocolEntry;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Tests {@link ActualQContainerEntryHandler}
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 20.12.2012
 */
public class ActualQContainerEntryHandlerTest {

	@Test
	public void test() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QContainer qContainer = new QContainer(kb, "Testcontainer");
		Session session = SessionFactory.createSession(kb);
		Date date = new Date();
		session.getProtocol().addEntries(new ActualQContainerEntry(date, qContainer.getName()));
		SessionRecord record = SessionConversionFactory.copyToSessionRecord(session);
		File file = new File("target/temp/ActualQContainerEntryTest.xml");
		file.getParentFile().mkdirs();
		SessionPersistenceManager.getInstance().saveSessions(file, Collections.singletonList(record));
		Collection<SessionRecord> reloadedRecords = SessionPersistenceManager.getInstance().loadSessions(
				file);
		Assert.assertEquals(1, reloadedRecords.size());
		Session reloadedSession = SessionConversionFactory.copyToSession(kb,
				reloadedRecords.iterator().next());
		List<ProtocolEntry> protocolHistory = reloadedSession.getProtocol().getProtocolHistory();
		Assert.assertEquals(1, protocolHistory.size());
		Assert.assertTrue(protocolHistory.get(0) instanceof ActualQContainerEntry);
		ActualQContainerEntry reloadedEntry = (ActualQContainerEntry) protocolHistory.get(0);
		Assert.assertEquals(date, reloadedEntry.getDate());
		Assert.assertEquals(qContainer.getName(), reloadedEntry.getQContainerName());
	}
}
