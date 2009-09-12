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

package de.d3web.persistence.xml.mminfo;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.kernel.supportknowledge.DCMarkup;
import de.d3web.kernel.supportknowledge.MMInfoObject;
import de.d3web.kernel.supportknowledge.MMInfoStorage;
import de.d3web.kernel.supportknowledge.Property;
import de.d3web.persistence.progress.ProgressEvent;
import de.d3web.persistence.progress.ProgressListener;
import de.d3web.persistence.xml.PersistenceManager;
import de.d3web.persistence.xml.writers.DCMarkupWriter;
import de.d3web.xml.utilities.XMLTools;
/**
 * Generates the XML representation of a MMInfoStorage Object
 * @author: Michael Scharvogel, Norman Brümmer
 */
public class MMInfoStorageWriter {

	private static Vector progressListeners = new Vector();

	private static final String encoding = "UTF-8";
	
	/**
	 * Insert the method's description here.
	 * Creation date: (19.10.2001 14:08:18)
	 * @return java.util.List
	 * @param questions java.util.List
	 */
	private static List catchAnswersFromQuestions(List questions) {
		List ret = new LinkedList();

		Iterator iter = questions.iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
			if (o instanceof QuestionChoice) {
				QuestionChoice qc = (QuestionChoice) o;
				ret.addAll(qc.getAllAlternatives());
			}
		}

		return ret;
	}

	/**
	 * building the XML representation of an MMInfoStorage Object
	 * @return java.lang.String XML representation of the MMInfoStorage Object
	 * @param o java.lang.Object teh MMInfoStorage Object
	 */
	private static String getXMLString(Object o, String objId) {

		StringBuffer sb = new StringBuffer();

		if (o == null) {
			// D3WebCase.trace("null is no MMInfoStorage !!!");
		} else if (!(o instanceof MMInfoStorage)) {
			// D3WebCase.trace(o.toString() + " is no MMInfoStorage Object !!!");
		} else {
			// Picking the stored infoMap
			// Getting the Key Set of the infoMap
			MMInfoStorage mmi = (MMInfoStorage) o;
			for (DCMarkup markup : mmi.getAllDCMarkups()) {
				StringBuffer sb1 = new StringBuffer();
				sb1.append("<MMInfo>\n");
				sb1.append(DCMarkupWriter.getInstance().getXMLString(markup));
				boolean hasContent = false;
				for (MMInfoObject info : mmi.getMMInfo(markup)) {
					String content = info.getContent().trim();
					if (!content.isEmpty()) {
						hasContent = true;
						sb1.append("<Content><![CDATA[" + XMLTools.prepareForCDATA(content) + "]]></Content>\n");
					}
				}
				sb1.append("</MMInfo>\n");
				if (hasContent) sb.append(sb1);
			}
		}

		return sb.toString();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (19.10.2001 14:17:43)
	 * @param args java.lang.String[]
	 */
	public static void main(String[] args) throws Exception {
		/*	D3WebCase.TRACE = true;
		
			KnowledgeBase kb = new de.d3web.kernel.knowledgebases.KfzWb();
		
			QuestionChoice q = (QuestionChoice) kb.searchQuestions("Mf57");
			AnswerChoice ans = (AnswerChoice) q.getAnswer("Mf57a1");
		
			System.out.println(q.getId() + ", " + ans.getId());
		
			MMInfoStorage mminfo1 = new MMInfoStorage();
			DescriptorObject dobj1 = new DescriptorObject();
			dobj1.addDCElement(DC.TITLE, "title");
			mminfo1.addMMInfo(new MMInfoObject(dobj1, "value1"));
			q.setMMInfoStorage(mminfo1);
		
			MMInfoStorage mminfo2 = new MMInfoStorage();
			DescriptorObject dobj2 = new DescriptorObject();
			dobj2.addDCElement(DC.TITLE, "title-ans");
			mminfo2.addMMInfo(new MMInfoObject(dobj1, "value2"));
			ans.setMMInfoStorage(mminfo2);
		
			writeMMInfoStorages(kb, new URL("file:///c:/kfzmminfo.xml"));
			System.out.println("done.");*/

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (19.10.2001 13:57:13)
	 * @param kb de.d3web.kernel.domainModel.KnowledgeBase
	 * @param xmlTarget java.net.URL
	 */
	public static Document writeMMInfoStorages(KnowledgeBase kb) {

		fireProgressEvent(new ProgressEvent(kb,ProgressEvent.START, ProgressEvent.OPERATIONTYPE_SAVE,PersistenceManager.resourceBundle.getString("d3web.Persistence.MMInfoStorageWriter.saveMM"),0,1));
		long maxvalue =getProgressTime(kb);
		long aktvalue=0;
		
		List qContainers = kb.getQContainers();
		List diagnoses = kb.getDiagnoses();
		List questions = kb.getQuestions();
		List answers = catchAnswersFromQuestions(questions);

		try {
			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version='1.0' encoding='"+encoding+"' ?>");
			sb.append("<KnowledgeBase" + " type='" + MMInfoPersistenceHandler.MMINFO_PERSISTENCE_HANDLER + "'" + " system='d3web'" + ">");

			sb.append(DCMarkupWriter.getInstance().getXMLString(kb.getDCMarkup()));

			sb.append("<MMInfos>");

			// diagnoses

			Iterator iter = diagnoses.iterator();
			while (iter.hasNext()) {
				Diagnosis d = (Diagnosis) iter.next();
				MMInfoStorage mms = (MMInfoStorage) d.getProperties().getProperty(Property.MMINFO);
				if (mms != null) {
					fireProgressEvent(new ProgressEvent(kb,
						ProgressEvent.UPDATE,
						ProgressEvent.OPERATIONTYPE_SAVE,
						PersistenceManager.resourceBundle.getString("d3web.Persistence.MMInfoStorageWriter.saveMM")
							+ maxvalue,
						aktvalue, maxvalue));
					sb.append(getXMLString(mms, d.getId()));
				}
			}

			// qContainers

			iter = qContainers.iterator();
			while (iter.hasNext()) {
				QContainer q = (QContainer) iter.next();
				MMInfoStorage mms = (MMInfoStorage) q.getProperties().getProperty(Property.MMINFO);
				if (mms != null) {
					fireProgressEvent(new ProgressEvent(kb,
						ProgressEvent.UPDATE,
						ProgressEvent.OPERATIONTYPE_SAVE,
						PersistenceManager.resourceBundle.getString("d3web.Persistence.MMInfoStorageWriter.saveMM")
							+ maxvalue,
						aktvalue, maxvalue));
					sb.append(getXMLString(mms, q.getId()));
				}
			}


			// questions

			iter = questions.iterator();
			while (iter.hasNext()) {
				Question q = (Question) iter.next();
				MMInfoStorage mms = (MMInfoStorage) q.getProperties().getProperty(Property.MMINFO);
				if (mms != null) {
					fireProgressEvent(new ProgressEvent(kb,
						ProgressEvent.UPDATE,
						ProgressEvent.OPERATIONTYPE_SAVE,
						PersistenceManager.resourceBundle.getString("d3web.Persistence.MMInfoStorageWriter.saveMM"),
						aktvalue++,
						maxvalue));
					sb.append(getXMLString(mms, q.getId()));
				}
			}

			// answers

			iter = answers.iterator();
			while (iter.hasNext()) {
				AnswerChoice a = (AnswerChoice) iter.next();
				MMInfoStorage mms = (MMInfoStorage) a.getProperties().getProperty(Property.MMINFO);
				if (mms != null) {
					fireProgressEvent(new ProgressEvent(kb,
						ProgressEvent.UPDATE,
						ProgressEvent.OPERATIONTYPE_SAVE,
						PersistenceManager.resourceBundle.getString("d3web.Persistence.MMInfoStorageWriter.saveMM"),
						aktvalue++,
						maxvalue));
					sb.append(getXMLString(mms, a.getId()));
				}
			}

			sb.append("</MMInfos>");
			sb.append("</KnowledgeBase>");

			// Jetzt noch rausschreiben...

			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			//use same approach as BasicPersistenceHandler
			byte[] content = sb.toString().getBytes(encoding);
			 java.io.ByteArrayInputStream stream = new java.io.ByteArrayInputStream(content);
			
			//InputStream stream = new StringBufferInputStream(new StringBufferStream(sb));

			Document dom = builder.parse(stream);

			fireProgressEvent(new ProgressEvent(kb,
				ProgressEvent.DONE,
				ProgressEvent.OPERATIONTYPE_SAVE,
				PersistenceManager.resourceBundle.getString("d3web.Persistence.MMInfoStorageWriter.saveMM"),
				1,1));
	
			return dom;


		} catch (Exception x) {
			Logger.getLogger(MMInfoStorageWriter.class.getName()).throwing(MMInfoStorageWriter.class.getName(), "writeMMInfoStorage", x);
			return null;
		}

	}

	/**
	 * @param kb
	 * @return
	 */
	public static long getProgressTime(KnowledgeBase kb) {
		long count = 0;

		List diagnoses = kb.getDiagnoses();
		List qcontainers = kb.getQContainers();
		List questions = kb.getQuestions();
		List answers = catchAnswersFromQuestions(questions);

		// diagnoses

		Iterator iter = diagnoses.iterator();
		while (iter.hasNext()) {
			Diagnosis d = (Diagnosis) iter.next();
			MMInfoStorage mms = (MMInfoStorage) d.getProperties().getProperty(Property.MMINFO);
			if (mms != null) {
				count++;
			}
		}
		
		// qcontainers

		iter = qcontainers.iterator();
		while (iter.hasNext()) {
			QContainer q = (QContainer) iter.next();
			MMInfoStorage mms = (MMInfoStorage) q.getProperties().getProperty(Property.MMINFO);
			if (mms != null) {
				count++;
			}
		}

		// questions

		iter = questions.iterator();
		while (iter.hasNext()) {
			Question q = (Question) iter.next();
			MMInfoStorage mms = (MMInfoStorage) q.getProperties().getProperty(Property.MMINFO);
			if (mms != null) {
				count++;
			}
		}

		// answers

		iter = answers.iterator();
		while (iter.hasNext()) {
			AnswerChoice a = (AnswerChoice) iter.next();
			MMInfoStorage mms = (MMInfoStorage) a.getProperties().getProperty(Property.MMINFO);
			if (mms != null) {
				count++;
			}
		}

		return count;
	}

	public static void addProgressListener(ProgressListener listener) {
		progressListeners.add(listener);
	}

	public static void fireProgressEvent(ProgressEvent evt) {
		for (int i = 0; i < progressListeners.size(); i++)
			 ((ProgressListener) progressListeners.elementAt(i)).updateProgress(evt);
	}

	public static void removeProgressListener(ProgressListener listener) {
		progressListeners.remove(listener);
	}
}