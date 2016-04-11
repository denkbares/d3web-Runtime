/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg denkbares GmbH
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

package de.d3web.mminfo.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import de.d3web.core.io.KnowledgeBasePersistence;
import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.ProgressInputStream;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.info.Property.Autosave;
import de.d3web.core.utilities.NamedObjectComparator;

/**
 * PersistanceHandler for MMInfos
 * 
 * @author: Markus Friedrich, Volker Belli
 */
public class MMInfoPersistenceHandler implements KnowledgeReader, KnowledgeWriter {

	public final static String MMINFO_PERSISTENCE_HANDLER = "mminfo";

	@Override
	public void read(PersistenceManager manager, KnowledgeBase kb, InputStream stream, ProgressListener listener) throws IOException {
		listener.updateProgress(0, "Starting to load multimedia");

		// we create a persistence with an empty xml document
		// because we do not read from it, but require it for
		// creating elements our of sax parser if a fragment handler
		// shall be used for a specific mminfo property
		Persistence<KnowledgeBase> dummyPersistence = new KnowledgeBasePersistence(manager, kb);
		MMInfoContentHandler handler = new MMInfoContentHandler(dummyPersistence);
		try {
			InputStream in = new ProgressInputStream(stream, listener, "Loading multimedia");
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(in, handler);
		}
		catch (SAXException e) {
			if (e.getException() instanceof IOException) {
				throw (IOException) e.getException();
			}
			throw new IOException("error parsing mminfo file", e);
		}
		catch (ParserConfigurationException e) {
			throw new IOException("unexpected internal error parsing mminfo file", e);
		}
		finally {
			listener.updateProgress(1, "Loading multimedia finished");
		}
	}

	@Override
	public void write(PersistenceManager manager, KnowledgeBase kb, OutputStream stream, ProgressListener listener) throws IOException {
		listener.updateProgress(0, "Starting to save multimedia");
		int maxvalue = getEstimatedSize(kb);
		float aktvalue = 0;
		List<TerminologyObject> objects = new ArrayList<TerminologyObject>(
				kb.getManager().getAllTerminologyObjects());
		Collections.sort(objects, new NamedObjectComparator());

		Persistence<KnowledgeBase> persistence = new KnowledgeBasePersistence(manager, kb);
		Document doc = persistence.getDocument();

		Element mminfosElement = doc.createElement("MMInfos");
		doc.appendChild(mminfosElement);
		Element kbElement = doc.createElement("KnowledgeBase");
		mminfosElement.appendChild(kbElement);
		XMLUtil.appendInfoStoreEntries(persistence, kbElement, kb.getInfoStore(), Autosave.mminfo);
		listener.updateProgress(aktvalue++ / maxvalue, "Saving multimedia "
				+ Math.round(aktvalue) + " of " + maxvalue);
		for (TerminologyObject object : objects) {
			appendIDObject(persistence, mminfosElement, object, null);
			// also append choices
			if (object instanceof QuestionChoice) {
				for (Choice c : ((QuestionChoice) object).getAllAlternatives()) {
					appendIDObject(persistence, mminfosElement, object, c);
				}
			}
			listener.updateProgress(aktvalue++ / maxvalue, "Saving multimedia "
					+ Math.round(aktvalue) + " of " + maxvalue);
		}
		listener.updateProgress(1, "Multimedia saved");
		XMLUtil.writeDocumentToOutputStream(doc, stream);
	}

	private void appendIDObject(Persistence<KnowledgeBase> persistence, Element mminfosElement, TerminologyObject object, Choice choice) throws IOException {
		Element idObjectElement = persistence.getDocument().createElement("idObject");
		idObjectElement.setAttribute("name", object.getName());
		if (choice != null) {
			idObjectElement.setAttribute("choice", choice.getName());
			XMLUtil.appendInfoStoreEntries(
					persistence, idObjectElement, choice.getInfoStore(), Autosave.mminfo);
		}
		else {
			XMLUtil.appendInfoStoreEntries(
					persistence, idObjectElement, object.getInfoStore(), Autosave.mminfo);
		}
		if (XMLUtil.getElementList(idObjectElement.getChildNodes()).size() > 0) {
			mminfosElement.appendChild(idObjectElement);
		}
	}

	@Override
	public int getEstimatedSize(KnowledgeBase kb) {
		return kb.getManager().getAllTerminologyObjects().size() + 1;
	}
}