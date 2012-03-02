/*
 * Copyright (C) 2011 denkbares GmbH
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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import de.d3web.core.io.progress.DummyProgressListener;
import de.d3web.core.io.progress.ProgressListener;
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

	public void load(File file) throws IOException {
		this.load(file, new DummyProgressListener());
	}

	public void load(File file, ProgressListener listener) throws IOException {
		if (file == null) {
			throw new NullPointerException("File is null. Unable to load SessionRepository.");
		}
		if (!file.exists() || file.isDirectory()) {
			throw new IllegalArgumentException(
					"File doesn't exist or is a directory. Unable to load SessionRepository.");
		}
		SessionPersistenceManager spm = SessionPersistenceManager.getInstance();
		Collection<SessionRecord> records = spm.loadSessions(file, listener);
		for (SessionRecord sr : records) {
			add(sr);
		}
	}

	public void save(File file) throws IOException {
		this.save(file, new DummyProgressListener());
	}

	public void save(File file, ProgressListener listener) throws IOException {
		if (file == null) throw new NullPointerException(
				"File is null. Unable to save SessionRepository.");
		if (file.isDirectory()) {
			throw new IllegalArgumentException(
					"File is a directory. Unable to save SessionRepository.");
		}
		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		SessionPersistenceManager spm = SessionPersistenceManager.getInstance();
		spm.saveSessions(file, sessionRecords.values(), listener);
	}

	public void load(InputStream file) throws IOException {
		this.load(file, new DummyProgressListener());
	}

	public void load(InputStream stream, ProgressListener listener) throws IOException {
		if (stream == null) {
			throw new NullPointerException("InputStream is null. Unable to load SessionRepository.");
		}
		SessionPersistenceManager spm = SessionPersistenceManager.getInstance();
		Collection<SessionRecord> records = spm.loadSessions(stream, listener);
		for (SessionRecord sr : records) {
			add(sr);
		}
	}

	public void save(OutputStream stream) throws IOException {
		this.save(stream, new DummyProgressListener());
	}

	public void save(OutputStream stream, ProgressListener listener) throws IOException {
		if (stream == null) throw new NullPointerException(
				"OutputStream is null. Unable to save SessionRepository.");
		SessionPersistenceManager spm = SessionPersistenceManager.getInstance();
		spm.saveSessions(stream, sessionRecords.values(), listener);
	}

}
