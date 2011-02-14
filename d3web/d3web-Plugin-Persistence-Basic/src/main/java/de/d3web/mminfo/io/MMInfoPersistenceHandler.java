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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.IDObjectComparator;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.info.Property.Autosave;

/**
 * PersistanceHandler for MMInfos Creation date: (25.01.2002 14:18:47)
 * 
 * @author: Christian Betz, Michael Scharvogel, Norman Brümmer
 */
public class MMInfoPersistenceHandler implements KnowledgeReader, KnowledgeWriter {

	public final static String MMINFO_PERSISTENCE_HANDLER = "mminfo";

	@Override
	public void read(KnowledgeBase kb, InputStream stream, ProgressListener listener) throws IOException {
		listener.updateProgress(0, "Starting to load multimedia");
		Document doc = Util.streamToDocument(stream);
		List<Element> rootElements = XMLUtil.getElementList(doc.getChildNodes());
		if (rootElements.size() != 1) {
			throw new IOException("There is more than one root element!");
		}
		Element rootElement = rootElements.get(0);
		String rootNodeName = rootElement.getNodeName();
		if (rootNodeName.equalsIgnoreCase("KnowledgeBase")) {
			throw new IOException("Old MMInfo File not supported any longer.");
		}
		else if (rootNodeName.equals("MMInfos")) {

			List<Element> children = XMLUtil.getElementList(rootElement.getChildNodes());
			int slicecount = children.size();
			int aktslicecount = 0;
			for (Element child : children) {
				if (child.getNodeName().equals("KnowledgeBase")) {
					XMLUtil.fillInfoStore(kb.getInfoStore(), child, kb);
				}
				else if (child.getNodeName().equals("idObject")) {
					String name = child.getAttribute("name");
					String choice = child.getAttribute("choice");
					NamedObject namedObject = kb.getManager().search(name);
					if (namedObject == null) {
						throw new IOException("NamedObject " + name
								+ " cannot be found in KnowledgeBase.");
					}
					if (!choice.isEmpty()) {
						if (namedObject instanceof QuestionChoice) {
							for (Choice c : ((QuestionChoice) namedObject).getAllAlternatives()) {
								if (c.getName().equals(choice)) {
									namedObject = c;
									break;
								}
							}
							if (!(namedObject instanceof Choice)) {
								throw new IOException("Choice " + choice + " not found in " + name);
							}
						}
						else {
							throw new IOException(
									"The choice attribute is only allowed for QuestionChoices.");
						}
					}
					XMLUtil.fillInfoStore(namedObject.getInfoStore(), child, kb);
				}
				listener.updateProgress(((float) aktslicecount++) / slicecount,
						"Loading multimedia: object " + aktslicecount + " of "
								+ slicecount);
			}
		}
		else {
			throw new IOException(
					"The name of the root node must be \"MMInfos\" or \"KnowledgeBase\"");
		}
		listener.updateProgress(1, "Loading multimedia finished");
	}

	@Override
	public void write(KnowledgeBase kb, OutputStream stream, ProgressListener listener) throws IOException {
		listener.updateProgress(0, "Starting to save multimedia");
		int maxvalue = getEstimatedSize(kb);
		float aktvalue = 0;
		List<TerminologyObject> objects = new ArrayList<TerminologyObject>(
				kb.getManager().getAllTerminologyObjects());
		Collections.sort(objects, new IDObjectComparator());

		Document doc = Util.createEmptyDocument();
		Element mminfosElement = doc.createElement("MMInfos");
		doc.appendChild(mminfosElement);
		Element kbElement = doc.createElement("KnowledgeBase");
		mminfosElement.appendChild(kbElement);
		XMLUtil.appendInfoStoreEntries(kbElement, kb.getInfoStore(), Autosave.mminfo);
		listener.updateProgress(aktvalue++ / maxvalue, "Saving multimedia "
				+ Math.round(aktvalue) + " of " + maxvalue);
		for (TerminologyObject object : objects) {
			appendIDObject(doc, mminfosElement, object, null);
			// also append choices
			if (object instanceof QuestionChoice) {
				for (Choice c : ((QuestionChoice) object).getAllAlternatives()) {
					appendIDObject(doc, mminfosElement, object, c);
				}
			}
			listener.updateProgress(aktvalue++ / maxvalue, "Saving multimedia "
					+ Math.round(aktvalue) + " of " + maxvalue);
		}
		listener.updateProgress(1, "Multimedia saved");
		Util.writeDocumentToOutputStream(doc, stream);
	}

	private void appendIDObject(Document doc, Element mminfosElement, TerminologyObject object, Choice choice) throws IOException {
		Element idObjectElement = doc.createElement("idObject");
		idObjectElement.setAttribute("name", object.getName());
		if (choice != null) {
			idObjectElement.setAttribute("choice", choice.getName());
			XMLUtil.appendInfoStoreEntries(idObjectElement, choice.getInfoStore(), Autosave.mminfo);
		}
		else {
			XMLUtil.appendInfoStoreEntries(idObjectElement, object.getInfoStore(), Autosave.mminfo);
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