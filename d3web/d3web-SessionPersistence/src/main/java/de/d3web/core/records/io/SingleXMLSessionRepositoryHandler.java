/*
 * Copyright (C) 2010 denkbares GmbH
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.io.progress.DummyProgressListener;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.records.SessionRepository;
import de.d3web.core.records.SessionRepositoryImpl;

/**
 * This implementation of the SessionRepositoryPersistenceHandler interface can
 * handle exactly one XML file. This XML file has to contain the whole
 * SessionRepository.
 * 
 * @author Sebastian Furth (denkbares GmbH)
 * 
 */
public class SingleXMLSessionRepositoryHandler implements SessionRepositoryPersistenceHandler {

	/*
	 * Singleton instance
	 */
	private static SingleXMLSessionRepositoryHandler instance = new SingleXMLSessionRepositoryHandler();

	private SingleXMLSessionRepositoryHandler() {
	}

	/**
	 * Returns an instance of SingleXMLSessionRepositoryHandler.
	 * 
	 * @return instance of SingleXMLSessionRepositoryHandler
	 */
	public static SingleXMLSessionRepositoryHandler getInstance() {
		return instance;
	}

	@Override
	public SessionRepository load(KnowledgeBase kb, File file) throws IOException {
		if (kb == null) throw new IllegalArgumentException(
				"KnowledgeBase is null. Unable to load SessionRepository.");
		if (file == null) throw new IllegalArgumentException(
				"File is null. Unable to load SessionRepository.");
		SessionPersistenceManager spm = SessionPersistenceManager.getInstance();
		Collection<SessionRecord> sessionRecords = spm.loadSessions(file,
				new DummyProgressListener(), kb);
		SessionRepository sessionRepository = new SessionRepositoryImpl();
		for (SessionRecord sr : sessionRecords) {
			sessionRepository.add(sr);
		}
		return sessionRepository;
	}

	@Override
	public void save(SessionRepository sessionRepository, File file) throws IOException {
		if (sessionRepository == null) throw new IllegalArgumentException(
				"SessionRepository is null. Unable to save SessionRepository.");
		if (file == null) throw new IllegalArgumentException(
				"File is null. Unable to save SessionRepository.");
		List<SessionRecord> records = new LinkedList<SessionRecord>();
		Iterator<SessionRecord> iterator = sessionRepository.iterator();
		while (iterator.hasNext()) {
			records.add(iterator.next());
		}
		SessionPersistenceManager spm = SessionPersistenceManager.getInstance();
		spm.saveSessions(file, records, new DummyProgressListener(),
				sessionRepository.getKnowledgeBase());
	}

}
