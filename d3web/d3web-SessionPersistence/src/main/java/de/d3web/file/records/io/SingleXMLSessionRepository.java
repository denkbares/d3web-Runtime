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
package de.d3web.file.records.io;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import de.d3web.core.io.progress.DummyProgressListener;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.records.DefaultSessionRepository;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.records.io.SessionPersistenceManager;

/**
 * This implementation of the SessionRepositoryPersistenceHandler interface can
 * handle exactly one XML file. This XML file has to contain the whole
 * SessionRepository.
 * 
 * @author Sebastian Furth (denkbares GmbH)
 * 
 */
public class SingleXMLSessionRepository extends DefaultSessionRepository {

	public void load(KnowledgeBase kb, File file) throws IOException {
		if (kb == null) throw new NullPointerException(
				"KnowledgeBase is null. Unable to load SessionRepository.");
		if (file == null) {
			throw new NullPointerException("File is null. Unable to load SessionRepository.");
		}
		if (!file.exists() || file.isDirectory()) {
			throw new IllegalArgumentException(
					"File doesn't exist or is a directory. Unable to load SessionRepository.");
		}
		SessionPersistenceManager spm = SessionPersistenceManager.getInstance();
		sessionRecords = new LinkedList<SessionRecord>(spm.loadSessions(file,
				new DummyProgressListener(), kb));
	}

	public void save(File file) throws IOException {
		if (file == null) throw new NullPointerException(
				"File is null. Unable to save SessionRepository.");
		if (file.isDirectory()) {
			throw new IllegalArgumentException(
					"File is a directory. Unable to save SessionRepository.");
		}
		SessionPersistenceManager spm = SessionPersistenceManager.getInstance();
		spm.saveSessions(file, sessionRecords, new DummyProgressListener(),
				this.getKnowledgeBase());
	}

}
