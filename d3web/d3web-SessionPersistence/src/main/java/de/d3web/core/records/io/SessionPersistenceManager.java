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

import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.records.SessionRecord;
import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;

public class SessionPersistenceManager {

	private SessionPersistenceManager manager;
	private Extension[] handler;

	private static final String REPOSITORY_TAG = "repository";
	private static final String SESSION_TAG = "session";

	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");

	private SessionPersistenceManager() {
		updateHandler();
	}

	public SessionPersistenceManager getInstance() {
		if (manager == null) {
			manager = new SessionPersistenceManager();
		}
		return manager;
	}

	private void updateHandler() {
		PluginManager manager = PluginManager.getInstance();
		handler = manager.getExtensions("de.d3web.sessionpersistence", "SessionPersistenceHandler");
	}

	public void saveSessions(File file, Collection<SessionRecord> sessionRecord, ProgressListener listener, KnowledgeBase kb) throws IOException {
		Document doc = Util.createEmptyDocument();
		Element repElement = doc.createElement(REPOSITORY_TAG);
		doc.appendChild(repElement);
		repElement.setAttribute("kbID", kb.getId());
		for (SessionRecord co : sessionRecord) {
			Element sessionElement = doc.createElement(SESSION_TAG);
			sessionElement.setAttribute("id", co.getId());
			sessionElement.setAttribute("created", format.format(co.getCreationDate()));
			sessionElement.setAttribute("changed", format.format(co.getLastEditDate()));
			for (Extension extension : handler) {
				SessionPersistenceHandler handler = (SessionPersistenceHandler) extension.getSingleton();
				handler.write(sessionElement, co, listener);
			}
		}
		OutputStream stream = new FileOutputStream(file);
		try {
			Util.writeDocumentToOutputStream(doc, stream);
		}
		finally {
			stream.close();
		}
	}

	public Collection<SessionRecord> loadSessions(File file, ProgressListener listener, KnowledgeBase kb) throws IOException {
		FileInputStream stream = new FileInputStream(file);
		Collection<SessionRecord> sessionRecords = new ArrayList<SessionRecord>();
		try {
			Document doc = Util.streamToDocument(stream);
			List<Element> childNodes = XMLUtil.getElementList(doc.getChildNodes());
			if (childNodes.size() == 1
					&& childNodes.get(0).getNodeName().equalsIgnoreCase(REPOSITORY_TAG)) {
				Element repositoryElement = childNodes.get(0);
				if (!repositoryElement.getAttribute("kbID").equals(kb.getId())) {
					throw new IOException(
							"KnowledgeBase ID does not aggree with the Repository");
				}
				List<Element> sessionElements = XMLUtil.getElementList(repositoryElement.getChildNodes());
				for (Element e : sessionElements) {
					String id = e.getAttribute("id");
					String created = e.getAttribute("created");
					String changed = e.getAttribute("changed");
					try {
						Date creationDate = format.parse(created);
						Date dateOfLastEdit = format.parse(changed);
						SessionRecord sr = new SessionRecord(id, kb, creationDate, dateOfLastEdit);
						for (Extension extension : handler) {
							SessionPersistenceHandler handler = (SessionPersistenceHandler) extension.getSingleton();
							handler.read(e, sr, listener);
						}
						sessionRecords.add(sr);
					}
					catch (ParseException e1) {
						throw new IOException(e1);
					}
				}
			}
			return sessionRecords;
		}
		finally {
			stream.close();
		}
	}
}
