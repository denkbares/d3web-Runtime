/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.fragments.DCMarkupHandler;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.IDObjectComparator;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.QContainer;
import de.d3web.core.terminology.Question;
import de.d3web.core.terminology.QuestionChoice;
import de.d3web.core.terminology.info.DCElement;
import de.d3web.core.terminology.info.DCMarkup;
import de.d3web.core.terminology.info.MMInfoObject;
import de.d3web.core.terminology.info.MMInfoStorage;
import de.d3web.core.terminology.info.PropertiesContainer;
import de.d3web.core.terminology.info.Property;
/**
 * PersistanceHandler for MMInfos
 * Creation date: (25.01.2002 14:18:47)
 * @author: Christian Betz, Michael Scharvogel, Norman Brümmer
 */
public class MMInfoPersistenceHandler implements KnowledgeReader, KnowledgeWriter {

	public final static String MMINFO_PERSISTENCE_HANDLER = "mminfo";
	private static final DCMarkupHandler DC_MARKUP_HANDLER = new DCMarkupHandler();

	@Override
	public void read(KnowledgeBase kb, InputStream stream, ProgressListener listener) throws IOException {
		// [TODO]:aha:check for "does this file actually match the  knowledgebase"!
		// Anzahl Slices ermitteln
		int slicecount = 0;
		int aktslicecount = 0;

		listener.updateProgress(0, "Starting to load multimedia");

		Hashtable<String, PropertiesContainer> ansIdAnswerHash = buildAnswerIdAnswerHash(kb
				.getQuestions());

		Document doc = Util.streamToDocument(stream);
		NodeList mminfos = doc.getElementsByTagName("MMInfo");
		slicecount = mminfos.getLength();

		for (int i = 0; i < mminfos.getLength(); ++i) {

			Node mminfo = mminfos.item(i);

			DCMarkup dcmarkup = null;
			List<String> content = new LinkedList<String>();

			NodeList nl = mminfo.getChildNodes();
			for (int j = 0; j < nl.getLength(); j++) {
				Node node = nl.item(j);
				if (node instanceof Element && DC_MARKUP_HANDLER.canRead((Element)node))
					dcmarkup = (DCMarkup) PersistenceManager.getInstance()
							.readFragment((Element) node, null);
				else if (node.getNodeName().equals("Content"))
					content.add(0, XMLUtil.getText(node));
				else if (node.getNodeName().equalsIgnoreCase("DCElement")
						||node.getNodeName().equalsIgnoreCase("Descriptor")) {
					throw new IOException("Not supported format. DCElements must be contained in an DCMarkup node.");
				}
			}

			// these line can be commented in to parse an d3 persistence file
			// in this case, the exception above must be commented out
//			if (dcmarkup == null) dcmarkup = (DCMarkup) new DCMarkupHandler().read(null, (Element) mminfo);

			String objId = dcmarkup.getContent(DCElement.SOURCE);

			if (objId != null) {

				PropertiesContainer source = kb.searchQASet(objId);
				if (source == null)
					source = kb.searchDiagnosis(objId);
				if (source == null)
					source = ansIdAnswerHash.get(objId);

				// and add the MMInfo
				if (source != null) {
					MMInfoStorage mminfoStorage = (MMInfoStorage) source
							.getProperties().getProperty(Property.MMINFO);
					if (mminfoStorage == null) {
						mminfoStorage = new MMInfoStorage();
						source.getProperties().setProperty(Property.MMINFO,
								mminfoStorage);
					}
					Iterator<String> iter = content.iterator();
					/*
					 * while (iter.hasNext()) mminfoStorage.addMMInfo(new
					 * MMInfoObject(dcmarkup, (String) iter.next()));
					 */

					while (iter.hasNext()) {
						MMInfoObject mmio = new MMInfoObject(dcmarkup, (String) iter.next());
						if (mmio.getDCMarkup() == null)
							throw new IOException("content will be forgotten: "
									+ mmio.getContent());
						else
							mminfoStorage.addMMInfo(mmio);
					}

				}
			}
			listener.updateProgress(((float) aktslicecount++) / slicecount,
					"Loading multimedia: object " + aktslicecount + " of "
							+ slicecount);
		}
		listener.updateProgress(1, "Loading multimedia finished");
	}

	@Override
	public void write(KnowledgeBase kb, OutputStream stream, ProgressListener listener) throws IOException {
		listener.updateProgress(0, "Starting to save multimedia");
		int maxvalue =getEstimatedSize(kb);
		float aktvalue=0;
		
		List<QContainer> qContainers = kb.getQContainers();
		List<Diagnosis> diagnoses = kb.getDiagnoses();
		List<Question> questions = kb.getQuestions();
		Collections.sort(questions, new IDObjectComparator());
		Collections.sort(qContainers, new IDObjectComparator());
		List<AnswerChoice> answers = catchAnswersFromQuestions(questions);

		
		
		Document doc = Util.createEmptyDocument();
		Element root = doc.createElement("KnowledgeBase");
		doc.appendChild(root);
		root.setAttribute("type", MMInfoPersistenceHandler.MMINFO_PERSISTENCE_HANDLER);
		root.setAttribute("system", "d3web");
		DCMarkup dcMarkup = kb.getDCMarkup();
		if (dcMarkup!=null && !dcMarkup.isEmpty()) {
			root.appendChild(PersistenceManager.getInstance().writeFragment(dcMarkup, doc));
		}
		Element mmiElement = doc.createElement("MMInfos");
		root.appendChild(mmiElement);
		
		// diagnoses
		Iterator<Diagnosis> diter = diagnoses.iterator();
		while (diter.hasNext()) {
			Diagnosis d = diter.next();
			MMInfoStorage mms = (MMInfoStorage) d.getProperties().getProperty(Property.MMINFO);
			if (mms != null) {
				listener.updateProgress(aktvalue++/maxvalue, "Saving multimedia "+Math.round(aktvalue)+" of "+ maxvalue);
				appendMMInfos(mms, d.getId(), mmiElement);
			}
		}

		// qContainers
		Iterator<QContainer> qconiter = qContainers.iterator();
		while (qconiter.hasNext()) {
			QContainer q = qconiter.next();
			MMInfoStorage mms = (MMInfoStorage) q.getProperties().getProperty(Property.MMINFO);
			if (mms != null) {
				listener.updateProgress(aktvalue++/maxvalue, "Saving multimedia "+Math.round(aktvalue)+" of "+ maxvalue);
				appendMMInfos(mms, q.getId(), mmiElement);
			}
		}

		// questions
		Iterator<Question> qiter = questions.iterator();
		while (qiter.hasNext()) {
			Question q = qiter.next();
			MMInfoStorage mms = (MMInfoStorage) q.getProperties().getProperty(Property.MMINFO);
			if (mms != null) {
				listener.updateProgress(aktvalue++/maxvalue, "Saving multimedia "+Math.round(aktvalue)+" of "+ maxvalue);
				appendMMInfos(mms, q.getId(), mmiElement);
			}
		}

		// answers
		Iterator<AnswerChoice> aiter = answers.iterator();
		while (aiter.hasNext()) {
			AnswerChoice a = aiter.next();
			MMInfoStorage mms = (MMInfoStorage) a.getProperties().getProperty(Property.MMINFO);
			if (mms != null) {
				listener.updateProgress(aktvalue++/maxvalue, "Saving multimedia "+Math.round(aktvalue)+" of "+ maxvalue);
				appendMMInfos(mms, a.getId(), mmiElement);
			}
		}
		listener.updateProgress(1, "Multimedia saved");
		Util.writeDocumentToOutputStream(doc, stream);
	}

	@Override
	public int getEstimatedSize(KnowledgeBase kb) {
		int count = 0;

		List<Diagnosis> diagnoses = kb.getDiagnoses();
		List<QContainer> qcontainers = kb.getQContainers();
		List<Question> questions = kb.getQuestions();
		List<AnswerChoice> answers = catchAnswersFromQuestions(questions);

		// diagnoses

		Iterator<Diagnosis> diter = diagnoses.iterator();
		while (diter.hasNext()) {
			Diagnosis d = diter.next();
			MMInfoStorage mms = (MMInfoStorage) d.getProperties().getProperty(Property.MMINFO);
			if (mms != null) {
				count++;
			}
		}
		
		// qcontainers
		Iterator<QContainer> qconiter = qcontainers.iterator();
		while (qconiter.hasNext()) {
			QContainer q = qconiter.next();
			MMInfoStorage mms = (MMInfoStorage) q.getProperties().getProperty(Property.MMINFO);
			if (mms != null) {
				count++;
			}
		}

		// questions
		Iterator<Question> qiter = questions.iterator();
		while (qiter.hasNext()) {
			Question q = qiter.next();
			MMInfoStorage mms = (MMInfoStorage) q.getProperties().getProperty(Property.MMINFO);
			if (mms != null) {
				count++;
			}
		}

		// answers

		Iterator<AnswerChoice> aiter = answers.iterator();
		while (aiter.hasNext()) {
			AnswerChoice a = aiter.next();
			MMInfoStorage mms = (MMInfoStorage) a.getProperties().getProperty(Property.MMINFO);
			if (mms != null) {
				count++;
			}
		}

		return count;
	}
	
	private static Hashtable<String, PropertiesContainer> buildAnswerIdAnswerHash(
			List<Question> questions) {

		Hashtable<String, PropertiesContainer> ansIdAnswerHash = new Hashtable<String, PropertiesContainer>();

		Iterator<Question> iter = questions.iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
			if (o instanceof QuestionChoice) {
				QuestionChoice q = (QuestionChoice) o;
				Iterator<AnswerChoice> ansiter = q.getAllAlternatives()
						.iterator();
				while (ansiter.hasNext()) {
					AnswerChoice ans = ansiter.next();
					ansIdAnswerHash.put(ans.getId(), ans);
				}
			}
		}
		return ansIdAnswerHash;
	}
	
	private static List<AnswerChoice> catchAnswersFromQuestions(List<Question> questions) {
		List<AnswerChoice>  ret = new LinkedList<AnswerChoice> ();

		Iterator<Question> iter = questions.iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
			if (o instanceof QuestionChoice) {
				QuestionChoice qc = (QuestionChoice) o;
				ret.addAll(qc.getAllAlternatives());
			}
		}
		return ret;
	}
	
	private static void appendMMInfos(MMInfoStorage mmi, String objId, Element mminfos) throws IOException {
		// Picking the stored infoMap
		// Getting the Key Set of the infoMap
		Document doc = mminfos.getOwnerDocument();
		for (DCMarkup markup : mmi.getAllDCMarkups()) {
			Element element = null;
			for (MMInfoObject info : mmi.getMMInfo(markup)) {
				String content = info.getContent().trim();
				if (!content.isEmpty()) {
					if (element==null) {
						element = doc.createElement("MMInfo");
						mminfos.appendChild(element);
						if (!markup.isEmpty())
							element.appendChild(PersistenceManager.getInstance().writeFragment(markup, doc));
					}
					Element contentElement = doc.createElement("Content");
					contentElement.setTextContent(content);
					element.appendChild(contentElement);
				}
			}
		}
	}
}