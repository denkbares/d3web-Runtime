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
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.NoSuchFragmentHandlerException;
import de.d3web.core.io.fragments.DCMarkupHandler;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.IDObjectComparator;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.knowledge.terminology.info.Property.Autosave;
import de.d3web.core.utilities.Triple;

/**
 * PersistanceHandler for MMInfos Creation date: (25.01.2002 14:18:47)
 * 
 * @author: Christian Betz, Michael Scharvogel, Norman Brümmer
 */
public class MMInfoPersistenceHandler implements KnowledgeReader, KnowledgeWriter {

	public final static String MMINFO_PERSISTENCE_HANDLER = "mminfo";
	private static final DCMarkupHandler DC_MARKUP_HANDLER = new DCMarkupHandler();

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
			parseOldMMInfoFile(kb, listener, doc);
		}
		else if (rootNodeName.equals("MMInfos")) {
			Map<String, NamedObject> ansIdAnswerHash = buildAnswerIdAnswerHash(kb.getManager()
					.getQuestions());
			List<Element> children = XMLUtil.getElementList(rootElement.getChildNodes());
			int slicecount = children.size();
			int aktslicecount = 0;
			for (Element child : children) {
				if (child.getNodeName().equals("KnowledgeBase")) {
					XMLUtil.fillInfoStore(kb.getInfoStore(), child, kb);
				}
				else if (child.getNodeName().equals("idObject")) {
					String id = child.getAttribute("name");
					NamedObject idObject = ansIdAnswerHash.get(id);
					if (idObject == null) {
						idObject = kb.getManager().search(id);
					}
					XMLUtil.fillInfoStore(idObject.getInfoStore(), child, kb);
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
		List<NamedObject> objects = kb.getManager().getAllIDObjects();
		Collections.sort(objects, new IDObjectComparator());

		Document doc = Util.createEmptyDocument();
		Element mminfosElement = doc.createElement("MMInfos");
		doc.appendChild(mminfosElement);
		Element kbElement = doc.createElement("KnowledgeBase");
		mminfosElement.appendChild(kbElement);
		XMLUtil.appendInfoStoreEntries(kbElement, kb.getInfoStore(), Autosave.mminfo);
		listener.updateProgress(aktvalue++ / maxvalue, "Saving multimedia "
				+ Math.round(aktvalue) + " of " + maxvalue);
		for (NamedObject object : objects) {
			Element idObjectElement = doc.createElement("idObject");
			idObjectElement.setAttribute("name", object.getName());
			XMLUtil.appendInfoStoreEntries(idObjectElement, object.getInfoStore(), Autosave.mminfo);
			if (XMLUtil.getElementList(idObjectElement.getChildNodes()).size() > 0) {
				mminfosElement.appendChild(idObjectElement);
			}
			listener.updateProgress(aktvalue++ / maxvalue, "Saving multimedia "
					+ Math.round(aktvalue) + " of " + maxvalue);
		}
		listener.updateProgress(1, "Multimedia saved");
		Util.writeDocumentToOutputStream(doc, stream);
	}

	@Override
	public int getEstimatedSize(KnowledgeBase kb) {
		return kb.getManager().getAllIDObjects().size() + 1;
	}

	private static Map<String, NamedObject> buildAnswerIdAnswerHash(
			List<Question> questions) {

		Map<String, NamedObject> ansIdAnswerHash = new Hashtable<String, NamedObject>();

		Iterator<Question> iter = questions.iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
			if (o instanceof QuestionChoice) {
				QuestionChoice q = (QuestionChoice) o;
				Iterator<Choice> ansiter = q.getAllAlternatives()
						.iterator();
				while (ansiter.hasNext()) {
					Choice ans = ansiter.next();
					ansIdAnswerHash.put(ans.getName(), ans);
				}
			}
		}
		return ansIdAnswerHash;
	}

	private void parseOldMMInfoFile(KnowledgeBase kb, ProgressListener listener, Document doc) throws NoSuchFragmentHandlerException, IOException {
		Map<String, NamedObject> ansIdAnswerHash = buildAnswerIdAnswerHash(kb.getManager()
				.getQuestions());
		NodeList mminfos = doc.getElementsByTagName("MMInfo");
		int slicecount = mminfos.getLength();
		int aktslicecount = 0;
		for (int i = 0; i < mminfos.getLength(); ++i) {

			Node mminfo = mminfos.item(i);
			// the former dcmarkup is repersented as a triple containing the id
			// of the NamedObject, the String of the Property and the language
			Triple<String, Property<?>, Locale> dcmarkup = null;
			List<String> content = new LinkedList<String>();

			NodeList nl = mminfo.getChildNodes();
			for (int j = 0; j < nl.getLength(); j++) {
				Node node = nl.item(j);
				if (node instanceof Element && DC_MARKUP_HANDLER.canRead((Element) node)) dcmarkup = DC_MARKUP_HANDLER.read(
						kb, (Element) node);
				else if (node.getNodeName().equals("Content")) content.add(0, XMLUtil.getText(node));
				else if (node.getNodeName().equalsIgnoreCase("DCElement")
						|| node.getNodeName().equalsIgnoreCase("Descriptor")) {
					throw new IOException(
							"Not supported format. DCElements must be contained in an DCMarkup node.");
				}
			}
			if (dcmarkup == null) continue;
			// these line can be commented in to parse an d3 persistence file
			// in this case, the exception above must be commented out
			// if (dcmarkup == null) dcmarkup = (DCMarkup) new
			// DCMarkupHandler().read(null, (Element) mminfo);

			String objId = dcmarkup.getA();

			if (objId != null) {

				NamedObject source = kb.getManager().searchQASet(objId);
				if (source == null) source = kb.getManager().searchSolution(objId);
				if (source == null) source = ansIdAnswerHash.get(objId);

				// and add the MMInfo
				if (source != null) {
					Property<?> property = dcmarkup.getB();
					Locale locale = dcmarkup.getC();
					Iterator<String> iter = content.iterator();
					while (iter.hasNext()) {
						String actualContent = iter.next();
						if (property == null) {
							Logger.getLogger("Persistence").info("content will be forgotten: "
									+ actualContent);
						}
						else if (locale != null) {
							source.getInfoStore().addValue(property, locale, actualContent);
						}
						else {
							source.getInfoStore().addValue(property, actualContent);
						}
					}
				}
			}
			listener.updateProgress(((float) aktslicecount++) / slicecount,
					"Loading multimedia: object " + aktslicecount + " of "
							+ slicecount);
		}
	}
}