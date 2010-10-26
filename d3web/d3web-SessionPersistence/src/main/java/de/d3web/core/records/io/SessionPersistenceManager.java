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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.FragmentManager;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.Util;
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
public final class SessionPersistenceManager extends FragmentManager {

	private static final String EXTENDED_POINT_FRAGMENTHANDLER = "FragmentHandler";
	private static final String EXTENDED_POINT_PERSISTENCEHANDLER = "SessionPersistenceHandler";
	private static final String EXTENDED_PLUGIN_ID = "d3web-SessionPersistence-ExtensionPoints";

	private static SessionPersistenceManager manager;
	private Extension[] handler;

	private static final String REPOSITORY_TAG = "repository";
	private static final String SESSION_TAG = "session";

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SS");

	private SessionPersistenceManager() {

	}

	public static SessionPersistenceManager getInstance() {
		if (manager == null) {
			manager = new SessionPersistenceManager();
		}
		return manager;
	}

	private void updateHandler() {
		PluginManager theManager = PluginManager.getInstance();
		handler = theManager.getExtensions(EXTENDED_PLUGIN_ID, EXTENDED_POINT_PERSISTENCEHANDLER);
		fragmentPlugins = theManager.getExtensions(EXTENDED_PLUGIN_ID,
				EXTENDED_POINT_FRAGMENTHANDLER);
	}

	public void saveSessions(File file, Collection<SessionRecord> sessionRecord, ProgressListener listener) throws IOException {
		updateHandler();
		Document doc = Util.createEmptyDocument();
		Element repElement = doc.createElement(REPOSITORY_TAG);
		doc.appendChild(repElement);
		Date latestChange = new Date(0);
		int counter = 0;
		for (SessionRecord co : sessionRecord) {
			// update progress
			float percent = sessionRecord.size() / (float) counter++;
			listener.updateProgress(
					percent * 0.7f,
					"writing record " + counter + " of " + sessionRecord.size());

			if (co.getLastChangeDate().after(latestChange)) {
				latestChange = co.getLastChangeDate();
			}
			Element sessionElement = doc.createElement(SESSION_TAG);
			sessionElement.setAttribute("id", co.getId());
			sessionElement.setAttribute("created", DATE_FORMAT.format(co.getCreationDate()));
			sessionElement.setAttribute("changed", DATE_FORMAT.format(co.getLastChangeDate()));
			for (Extension extension : handler) {
				SessionPersistenceHandler theHandler = (SessionPersistenceHandler) extension.getSingleton();
				theHandler.write(sessionElement, co, listener);
			}
			repElement.appendChild(sessionElement);
		}
		file.setLastModified(latestChange.getTime());

		listener.updateProgress(0.7f, "writing records to disc");
		OutputStream stream = new FileOutputStream(file);
		try {
			Util.writeDocumentToOutputStream(doc, stream);
			listener.updateProgress(1f, "writing records done");
		}
		finally {
			stream.close();
		}
	}

	public Collection<SessionRecord> loadSessions(File file, ProgressListener listener) throws IOException {
		updateHandler();
		FileInputStream stream = new FileInputStream(file);
		Collection<SessionRecord> sessionRecords = new ArrayList<SessionRecord>();
		try {
			listener.updateProgress(0.0f, "reading file from disc");
			Document doc = Util.streamToDocument(stream);
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
						Date creationDate = DATE_FORMAT.parse(created);
						Date dateOfLastEdit = DATE_FORMAT.parse(changed);
						DefaultSessionRecord sr =
								new DefaultSessionRecord(id, creationDate, dateOfLastEdit);
						for (Extension extension : handler) {
							SessionPersistenceHandler theHandler = (SessionPersistenceHandler) extension.getSingleton();
							theHandler.read(e, sr, listener);
						}
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
		finally {
			stream.close();
		}
	}
}
