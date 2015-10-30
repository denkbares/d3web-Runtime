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
package de.d3web.core.records.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.FragmentManager;
import de.d3web.core.io.progress.DummyProgressListener;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.records.DefaultSessionRecord;
import de.d3web.core.records.SessionRecord;
import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;

/**
 * PersistenceManager to write/read SessionRecords to/from an xml file
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.09.2010
 */
public final class SessionPersistenceManager {

	private static final String EXTENDED_POINT_FRAGMENTHANDLER = "FragmentHandler";
	private static final String EXTENDED_POINT_PERSISTENCEHANDLER = "SessionPersistenceHandler";
	private static final String EXTENDED_PLUGIN_ID = "d3web-SessionPersistence-ExtensionPoints";

	private static SessionPersistenceManager manager;
	private Extension[] handler;
	private final FragmentManager<SessionRecord> fragmentManager = new FragmentManager<SessionRecord>();

	private static final String REPOSITORY_TAG = "repository";
	private static final String SESSION_TAG = "session";

	private SessionPersistenceManager() {
	}

	public static SessionPersistenceManager getInstance() {
		if (manager == null) {
			manager = new SessionPersistenceManager();
		}
		return manager;
	}

	FragmentManager<SessionRecord> getFragmentManager() {
		return fragmentManager;
	}

	private void updateHandler() {
		PluginManager theManager = PluginManager.getInstance();
		handler = theManager.getExtensions(EXTENDED_PLUGIN_ID, EXTENDED_POINT_PERSISTENCEHANDLER);
		getFragmentManager().init(EXTENDED_PLUGIN_ID, EXTENDED_POINT_FRAGMENTHANDLER);
	}

	/**
	 * Writes a set of session records as xml into the specified file.
	 *
	 * @param file           the target file to be written
	 * @param sessionRecords the session records to be stored
	 * @throws IOException if the xml content cannot be written into the file
	 * @created 31.08.2011
	 */
	public void saveSessions(File file, Collection<SessionRecord> sessionRecords) throws IOException {
		saveSessions(file, sessionRecords, new DummyProgressListener());
	}

	/**
	 * Writes a set of session records as xml into the specified file.
	 *
	 * @param file           the target file to be written
	 * @param sessionRecords the session records to be stored
	 * @param listener       the progress listener to indicate the progress
	 * @throws IOException if the xml content cannot be written into the file
	 * @created 19.05.2011
	 */
	public void saveSessions(File file, Collection<SessionRecord> sessionRecords, ProgressListener listener) throws IOException {
		try (OutputStream stream = new FileOutputStream(file)) {
			saveSessions(stream, sessionRecords, listener);
		}
		// find the latest change date to adapt the file to
		Date latestChange = new Date(0);
		for (SessionRecord co : sessionRecords) {
			if (co.getLastChangeDate().after(latestChange)) {
				latestChange = co.getLastChangeDate();
			}
		}
		file.setLastModified(latestChange.getTime());
	}

	/**
	 * Writes a set of session records as xml into the specified stream.
	 *
	 * @param stream         the target stream to be written
	 * @param sessionRecords the session records to be stored
	 * @throws IOException if the xml content cannot be written into the file
	 * @created 31.08.2011
	 */
	public void saveSessions(OutputStream stream, Collection<SessionRecord> sessionRecords) throws IOException {
		saveSessions(stream, sessionRecords, new DummyProgressListener());
	}

	/**
	 * Writes a set of session records as xml into the specified stream.
	 *
	 * @param stream         the target stream to be written
	 * @param sessionRecords the session records to be stored
	 * @param listener       the progress listener to indicate the progress
	 * @throws IOException if the xml content cannot be written into the file
	 * @created 19.05.2011
	 */
	public void saveSessions(OutputStream stream, Collection<SessionRecord> sessionRecords, ProgressListener listener) throws IOException {
		updateHandler();
		Document doc = XMLUtil.createEmptyDocument();
		Element repElement = doc.createElement(REPOSITORY_TAG);
		doc.appendChild(repElement);
		int counter = 0;
		for (SessionRecord co : sessionRecords) {
			// update progress
			float percent = sessionRecords.size() / (float) counter++;
			listener.updateProgress(
					percent * 0.7f,
					"writing record " + counter + " of " + sessionRecords.size());

			Element sessionElement = doc.createElement(SESSION_TAG);
			sessionElement.setAttribute("id", co.getId());
			sessionElement.setAttribute("created", XMLUtil.writeDate(co.getCreationDate()));
			sessionElement.setAttribute("changed", XMLUtil.writeDate(co.getLastChangeDate()));
			for (Extension extension : handler) {
				SessionPersistenceHandler theHandler = (SessionPersistenceHandler) extension.getSingleton();
				SessionPersistence persistence = new SessionPersistence(this, co, sessionElement);
				theHandler.write(persistence, sessionElement, listener);
			}
			repElement.appendChild(sessionElement);
		}

		listener.updateProgress(0.7f, "writing records to disc");
		XMLUtil.writeDocumentToOutputStream(doc, stream);
		listener.updateProgress(1f, "writing records done");
	}

	/**
	 * Reads the {@link SessionRecord}s from a xml file and returns them as a
	 * collection.
	 *
	 * @param file the file to read the records from
	 * @return the records read
	 * @throws IOException if the file cannot be accessed or its contents are
	 *                     not stored session records
	 * @created 31.08.2011
	 */
	public Collection<SessionRecord> loadSessions(File file) throws IOException {
		return loadSessions(file, new DummyProgressListener());
	}

	/**
	 * Reads the {@link SessionRecord}s from a xml file and returns them as a
	 * collection.
	 *
	 * @param file     the file to read the records from
	 * @param listener the progress listener to indicate the progress
	 * @return the records read
	 * @throws IOException if the file cannot be accessed or its contents are
	 *                     not stored session records
	 * @created 31.08.2011
	 */
	public Collection<SessionRecord> loadSessions(File file, ProgressListener listener) throws IOException {
		FileInputStream stream = new FileInputStream(file);
		try {
			return loadSessions(stream, listener);
		}
		finally {
			stream.close();
		}
	}

	/**
	 * Reads the {@link SessionRecord}s from a xml input stream and returns them
	 * as a collection.
	 *
	 * @param inputStream the stream to read the records from
	 * @return the records read
	 * @throws IOException if the stream's content is not a xml of stored
	 *                     session records
	 * @created 31.08.2011
	 */
	public Collection<SessionRecord> loadSessions(InputStream inputStream) throws IOException {
		return loadSessions(inputStream, new DummyProgressListener());
	}

	/**
	 * Reads the {@link SessionRecord}s from a xml input stream and returns them
	 * as a collection.
	 *
	 * @param inputStream the stream to read the records from
	 * @param listener    the progress listener to indicate the progress
	 * @return the records read
	 * @throws IOException if the stream's content is not a xml of stored
	 *                     session records
	 * @created 31.08.2011
	 */
	public Collection<SessionRecord> loadSessions(InputStream inputStream, ProgressListener listener) throws IOException {
		updateHandler();
		Collection<SessionRecord> sessionRecords = new ArrayList<SessionRecord>();
		listener.updateProgress(0.0f, "reading file from disc");
		Document doc = XMLUtil.streamToDocument(inputStream);
		List<Element> childNodes = XMLUtil.getElementList(doc.getChildNodes());
		if (childNodes.size() == 1
				&& childNodes.get(0).getNodeName().equalsIgnoreCase(REPOSITORY_TAG)) {
			Element repositoryElement = childNodes.get(0);
			List<Element> sessionElements = XMLUtil.getElementList(repositoryElement.getChildNodes());
			int max = sessionElements.size();
			int counter = 0;
			for (Element e : sessionElements) {
				// update progress
				listener.updateProgress(
						0.3f + max / (float) counter,
						"parsing session " + counter + " of " + max);

				String id = e.getAttribute("id");
				String created = e.getAttribute("created");
				String changed = e.getAttribute("changed");
				try {
					Date creationDate = XMLUtil.readDate(created);
					Date dateOfLastEdit = XMLUtil.readDate(changed);
					DefaultSessionRecord sr =
							new DefaultSessionRecord(id, creationDate, dateOfLastEdit);
					for (Extension extension : handler) {
						SessionPersistenceHandler theHandler = (SessionPersistenceHandler) extension.getSingleton();
						SessionPersistence persistence = new SessionPersistence(this, sr, e);
						theHandler.read(persistence, e, listener);
					}
					sr.touch(dateOfLastEdit);
					sessionRecords.add(sr);
				}
				catch (ParseException e1) {
					throw new IOException(e1);
				}
			}
		}
		listener.updateProgress(1f, "parsing done");
		return sessionRecords;
	}

}
