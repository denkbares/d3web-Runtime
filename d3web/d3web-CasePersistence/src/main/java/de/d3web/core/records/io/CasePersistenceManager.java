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

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.CaseObjectImpl;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;

public class CasePersistenceManager {

	private CasePersistenceManager manager;
	private Extension[] handler;

	private static final String REPOSITORY_TAG = "repository";
	private static final String CASE_TAG = "case";

	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");

	private CasePersistenceManager() {
		updateHandler();
	}

	public CasePersistenceManager getInstance() {
		if (manager == null) {
			manager = new CasePersistenceManager();
		}
		return manager;
	}

	private void updateHandler() {
		PluginManager manager = PluginManager.getInstance();
		handler = manager.getExtensions("de.d3web.casepersistence", "CasePersistenceHandler");
	}

	public void saveCases(File file, Collection<CaseObject> caseObjects, ProgressListener listener, KnowledgeBase kb) throws IOException {
		Document doc = Util.createEmptyDocument();
		Element repElement = doc.createElement(REPOSITORY_TAG);
		doc.appendChild(repElement);
		repElement.setAttribute("kbID", kb.getId());
		for (CaseObject co : caseObjects) {
			Element caseElement = doc.createElement(CASE_TAG);
			caseElement.setAttribute("id", co.getId());
			// TODO read dates from co
			caseElement.setAttribute("created", format.format(new Date()));
			caseElement.setAttribute("changed", format.format(new Date()));
			for (Extension extension : handler) {
				CasePersistenceHandler handler = (CasePersistenceHandler) extension.getSingleton();
				handler.write(caseElement, co, listener);
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

	public Collection<CaseObject> loadCases(File file, ProgressListener listener, KnowledgeBase kb) throws IOException {
		FileInputStream stream = new FileInputStream(file);
		Collection<CaseObject> caseObjects = new ArrayList<CaseObject>();
		try {
			Document doc = Util.streamToDocument(stream);
			List<Element> childNodes = XMLUtil.getElementList(doc.getChildNodes());
			if (childNodes.size() == 1
					&& childNodes.get(0).getNodeName().equalsIgnoreCase(REPOSITORY_TAG)) {
				Element repositoryElement = childNodes.get(0);
				if (!repositoryElement.getAttribute("kbID").equals(kb.getId())) {
					throw new IOException(
							"KnowledgeBase ID does not aggree with the CaseRepository");
				}
				List<Element> caseElements = XMLUtil.getElementList(repositoryElement.getChildNodes());
				for (Element e : caseElements) {
					String id = e.getAttribute("id");
					String created = e.getAttribute("created");
					String changed = e.getAttribute("changed");
					try {
						// TODO save parsed values to co
						Date creationDate = format.parse(created);
						Date dateOfLastEdit = format.parse(changed);
						CaseObject co = new CaseObjectImpl(kb, id, creationDate, dateOfLastEdit);
						for (Extension extension : handler) {
							CasePersistenceHandler handler = (CasePersistenceHandler) extension.getSingleton();
							handler.read(e, co, listener);
						}
					}
					catch (ParseException e1) {
						throw new IOException(e1);
					}
				}
			}
			return caseObjects;
		}
		finally {
			stream.close();
		}
	}
}
