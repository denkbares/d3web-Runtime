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
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.kernel.supportknowledge.DCElement;
import de.d3web.kernel.supportknowledge.DCMarkup;
import de.d3web.kernel.supportknowledge.MMInfoObject;
import de.d3web.kernel.supportknowledge.MMInfoStorage;
import de.d3web.kernel.supportknowledge.PropertiesContainer;
import de.d3web.kernel.supportknowledge.Property;
import de.d3web.persistence.progress.ProgressEvent;
import de.d3web.persistence.progress.ProgressListener;
import de.d3web.persistence.utilities.URLUtils;
import de.d3web.persistence.xml.PersistenceManager;
import de.d3web.persistence.xml.loader.DCMarkupUtilities;
import de.d3web.xml.domtools.DOMAccess;
import de.d3web.xml.utilities.XMLTools;

/**
 * Creation date: (19.10.2001 13:51:24)
 * @author: Norman Brümmer
 */
public class MMInfoLoader {

	private static Vector progressListeners = new Vector();
	private static Hashtable ansIdAnswerHash = null;
	
	private static ProgressEvent everLastingProgressEvent = new ProgressEvent(null, 0,0,null,0,0);

	/**
	 * Creation date: (19.10.2001 15:22:09)
	 * @param questions java.util.List
	 */
	private static void buildAnswerIdAnswerHash(List questions) {

		ansIdAnswerHash = new Hashtable();

		Iterator iter = questions.iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
			if (o instanceof QuestionChoice) {
				QuestionChoice q = (QuestionChoice) o;
				Iterator ansiter = q.getAllAlternatives().iterator();
				while (ansiter.hasNext()) {
					AnswerChoice ans = (AnswerChoice) ansiter.next();
					ansIdAnswerHash.put(ans.getId(), ans);
				}
			}
		}
	}

	/**
	 * Creation date: (19.10.2001 15:20:40)
	 * @return de.d3web.kernel.domainModel.KnowledgeBase
	 * @param kb de.d3web.kernel.domainModel.KnowledgeBase
	 * @param xmlSource java.net.URL
	 */
	public static KnowledgeBase loadMMinfoStorages(KnowledgeBase kb, URL xmlSource) {

//		Anzahl Slices ermitteln
		int slicecount=0;
		int aktslicecount=0;
		
		everLastingProgressEvent.type = ProgressEvent.START;
		everLastingProgressEvent.operationType = ProgressEvent.OPERATIONTYPE_LOAD;
		everLastingProgressEvent.taskDescription =PersistenceManager.resourceBundle.getString("d3web.Persistence.MMInfoLoader.loadMM");
		everLastingProgressEvent.currentValue = 0;
		everLastingProgressEvent.finishedValue = 1;
		fireProgressEvent(everLastingProgressEvent);
			  
			
		buildAnswerIdAnswerHash(kb.getQuestions());
		
		try {
			DocumentBuilder dBuilder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc =
				dBuilder.parse(URLUtils.openStream(xmlSource));
								// do not use the following line instead,
								// because this filters tabs and breaks:
								//InputFilter.getFilteredInputSource(xmlSource)

			NodeList mminfos = doc.getElementsByTagName("MMInfo");
			slicecount = mminfos.getLength();
			
			for (int i = 0; i < mminfos.getLength(); ++i) {

				Node mminfo = mminfos.item(i);
				
				DCMarkup dcmarkup = null;
				List content = new LinkedList();

				NodeList nl = mminfo.getChildNodes();
				for (int j = 0; j < nl.getLength(); j++) {
					Node node = nl.item(j);
					if (node.getNodeName().equals("DCMarkup"))
						dcmarkup = DCMarkupUtilities.getDCMarkup(node);
					else if (node.getNodeName().equals("Content"))
						content.add(0, DOMAccess.getText(node));
					
				}
				
				// [MISC]:aha:obsolete after supportknowledge refactoring is propagated
				if (dcmarkup == null)
					dcmarkup = DCMarkupUtilities.getDCMarkup(mminfo);
				
				String objId = dcmarkup.getContent(DCElement.SOURCE);

				if (objId != null) {

					PropertiesContainer source = kb.searchQASet(objId);
					if (source == null)
						source = kb.searchDiagnosis(objId);
					if (source == null)
						source = (PropertiesContainer) ansIdAnswerHash.get(objId);

					// and add the MMInfo
					if (source != null) {
						MMInfoStorage mminfoStorage = (MMInfoStorage) source.getProperties().getProperty(Property.MMINFO);
						if (mminfoStorage == null) {
							mminfoStorage = new MMInfoStorage();
							source.getProperties().setProperty(Property.MMINFO, mminfoStorage);
						}
						Iterator iter = content.iterator();
						/*
						while (iter.hasNext())
							mminfoStorage.addMMInfo(new MMInfoObject(dcmarkup, (String) iter.next()));
						*/

						while (iter.hasNext()) {
							MMInfoObject mmio = new MMInfoObject(dcmarkup, XMLTools.prepareFromCDATA((String) iter.next()));
							if (mmio.getDCMarkup() == null)
								Logger.getLogger(MMInfoLoader.class.getName()).warning("content will be forgotten: " + mmio.getContent());
							else
								mminfoStorage.addMMInfo(mmio);
						}
						
					}
				}
				
				everLastingProgressEvent.type = ProgressEvent.UPDATE;
				everLastingProgressEvent.operationType = ProgressEvent.OPERATIONTYPE_LOAD;
				everLastingProgressEvent.taskDescription =PersistenceManager.resourceBundle.getString("d3web.Persistence.MMInfoLoader.loadMMObject")
				+aktslicecount+ PersistenceManager.resourceBundle.getString("d3web.Persistence.MMInfoLoader.loadMMObjectOF")+slicecount;
				everLastingProgressEvent.currentValue = aktslicecount++;
				everLastingProgressEvent.finishedValue = slicecount;
				fireProgressEvent(everLastingProgressEvent);
		
			}

		} catch (Exception x) {
			Logger.getLogger(MMInfoLoader.class.getName()).throwing(MMInfoLoader.class.getName(), "loadMMInfoStorage", x);
		}
		
		everLastingProgressEvent.type = ProgressEvent.DONE;
		everLastingProgressEvent.operationType = ProgressEvent.OPERATIONTYPE_LOAD;
		everLastingProgressEvent.taskDescription =PersistenceManager.resourceBundle.getString("d3web.Persistence.MMInfoLoader.loadMM");
		everLastingProgressEvent.currentValue = 1;
		everLastingProgressEvent.finishedValue = 1;
		fireProgressEvent(everLastingProgressEvent);
		


		return kb;
	}
	
	public static void addProgressListener(ProgressListener listener) {
		progressListeners.add(listener);
	}

	public static void fireProgressEvent(ProgressEvent evt) {
		for (int i=0;i< progressListeners.size(); i++)
			((ProgressListener)progressListeners.elementAt(i)).updateProgress(evt);
	}

	public static void removeProgressListener(ProgressListener listener) {
		progressListeners.remove(listener);
	}
}