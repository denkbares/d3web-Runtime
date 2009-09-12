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

package de.d3web.caserepository.dom;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import de.d3web.caserepository.CaseObject;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.persistence.progress.ProgressEvent;
import de.d3web.persistence.progress.ProgressListener;
import de.d3web.persistence.progress.ProgressNotifier;
import de.d3web.persistence.xml.PersistenceManager;

/**
 * Creates the CaseObjects defined in one XML-File using CaseObjectCreator.
 * @author: Patrick von Schoen
 */
public class CaseObjectListCreator implements ProgressNotifier {
	
	private KnowledgeBase knowledgeBase = null;
	private Vector progressListeners = new Vector();
	private CaseObjectCreator coc = new CaseObjectCreator();

	/**
	 * 
	 * @param itemName
	 * @param creator
	 */
	public void addAdditionalCreator(String itemName, AdditionalCaseObjectCreator creator) {
		coc.addAdditionalCreator(itemName, creator);
	}

	/**
	 * 
	 * @param newProgressListener
	 */
	public void addProgressListener(ProgressListener newProgressListener) {
		progressListeners.add(newProgressListener);
	}

	/**
	 * 
	 * @param xmlfile String
	 * @return List
	 */
	public List createCaseObjectCollection(String xmlfile) {

		if (knowledgeBase == null) {
			Logger.getLogger(this.getClass().getName()).warning("no knowledgebase specified");
			return null;
		}

		Logger.getLogger(this.getClass().getName()).info("loading the case base ...");
		double startMs = System.currentTimeMillis();
		
		Document doc = null;
		try {
			doc = DocumentCreator.createDocument(xmlfile);
		} catch (IOException io) {
			Logger.getLogger(this.getClass().getName()).warning("xmlfile not found: " + io);
			return null;
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).warning("parsing error: " + e.toString());
			return null;
		}

		List cb = createCaseObjectCollection(doc);
		
		double finishedMs = System.currentTimeMillis();
		Logger.getLogger(this.getClass().getName()).info(
			"... finished (took "
			+ Double.toString((finishedMs - startMs) / 1000)
			+ " s)"
		);
		
		return cb;
	}

	/**
	 * 
	 * @param repositoryURL URL
	 * @return List
	 */
	public List createCaseObjectCollection(URL repositoryURL) {
		
		if (knowledgeBase == null) {
			Logger.getLogger(this.getClass().getName()).warning("no knowledgebase specified");
			return null;
		}

		Logger.getLogger(this.getClass().getName()).info("loading the case base ...");
		double startMs = System.currentTimeMillis();

		Document doc = null;
		try {
			doc = DocumentCreator.createDocument(repositoryURL);
		} catch (IOException io) {
			Logger.getLogger(this.getClass().getName()).warning("xmlfile not found: " + io);
			return null;
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).warning("parsing error: " + e.toString());
			return null;
		}

		List cb = createCaseObjectCollection(doc);
		
		double finishedMs = System.currentTimeMillis();
		Logger.getLogger(this.getClass().getName()).info(
			"... finished (took "
			+ Double.toString((finishedMs - startMs) / 1000)
			+ " s)"
		);
		
		return cb;
	}
	
	/**
	 * 
	 * @param repositoryURL URL
	 * @return List
	 */
	public List createCaseObjectCollection(InputStream inputStream) {

		if (knowledgeBase == null) {
			Logger.getLogger(this.getClass().getName()).warning("no knowledgebase specified");
			return null;
		}

		Logger.getLogger(this.getClass().getName()).info("loading the case base ...");
		double startMs = System.currentTimeMillis();

		Document doc = null;
		try {
			doc = DocumentCreator.createDocument(inputStream);
		} catch (IOException io) {
			Logger.getLogger(this.getClass().getName()).warning("xmlfile not found: " + io);
			return null;
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).warning("parsing error: " + e.toString());
			return null;
		}

		List cb = createCaseObjectCollection(doc);
	
		double finishedMs = System.currentTimeMillis();
		Logger.getLogger(this.getClass().getName()).info(
			"... finished (took "
			+ Double.toString((finishedMs - startMs) / 1000)
			+ " s)"
		);
	
		return cb;
	}

	/**
	 * 
	 * @param doc Document
	 * @return List
	 */
	public List createCaseObjectCollection(Document doc) {
		
		int maxProg=0;
		int aktProg=0;
		
		if (knowledgeBase == null) {
			Logger.getLogger(this.getClass().getName()).warning("No knowledgebase specified!");
			return null;
		}

		Vector caseObjects = new Vector();

		coc.setKnowledgeBase(knowledgeBase);

		NodeList nl2 = doc.getElementsByTagName("Problem");

		// Set max for progress listeners
		maxProg = nl2.getLength();
		

		// notify progress listeners: 0
		fireProgressEvent(new ProgressEvent(this, ProgressEvent.START,ProgressEvent.OPERATIONTYPE_LOAD,PersistenceManager.resourceBundle.getString("d3web.Persistence.CaseObjectListCreator.loadCase"),aktProg, maxProg));
		
		
		for (int i = 0; i < nl2.getLength(); i++) {
			String countString = String.valueOf(i);
			Logger.getLogger(this.getClass().getName()).info("************* Case: " + countString + " *************");

			CaseObject caseObject = coc.createCaseObject(nl2.item(i));
			caseObjects.add(caseObject);
			// notify progress listeners: i+1
			fireProgressEvent(new ProgressEvent(this, ProgressEvent.UPDATE,ProgressEvent.OPERATIONTYPE_LOAD,
				PersistenceManager.resourceBundle.getString("d3web.Persistence.CaseObjectListCreator.loadCaseObject")
				+ (aktProg++)+ PersistenceManager.resourceBundle.getString("d3web.Persistence.CaseObjectListCreator.loadCaseObjectOf")+maxProg,aktProg, maxProg));
		

		}
		// Notify done to progress listeners
		fireProgressEvent(new ProgressEvent(this, ProgressEvent.DONE,ProgressEvent.OPERATIONTYPE_LOAD,PersistenceManager.resourceBundle.getString("d3web.Persistence.CaseObjectListCreator.loadCase"),aktProg, maxProg));
		

		return caseObjects;
	}

	/**
	 * 
	 * @return List
	 * @param xmlfile String
	 */
	public List createCaseObjectCollectionFromString(String xmlCode) {

		Document doc = null;
		try {
			doc = DocumentCreator.createDocumentFromString(xmlCode);
		} catch (IOException io) {
			Logger.getLogger(this.getClass().getName()).warning("xmlfile not found: " + io);
			return null;
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).warning("parsing error: " + e.toString());
			return null;
		}

		return createCaseObjectCollection(doc);
	}

	/**
	 * 
	 * @param knowledgeBase KnowledgeBase
	 */
	public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
		this.knowledgeBase = knowledgeBase;
	}
	
	public void fireProgressEvent(ProgressEvent evt) {
		Enumeration enumeration = progressListeners.elements();
		while (enumeration.hasMoreElements())
			 ((ProgressListener) enumeration.nextElement()).updateProgress(evt);
	

	}

	public void removeProgressListener(ProgressListener listener) {
		progressListeners.remove(listener);

	}

	/* (non-Javadoc)
	 * @see de.d3web.persistence.progress.ProgressNotifier#getProgressTime(int, java.lang.Object)
	 */
	public long getProgressTime(int operationType, Object additionalInformation) {
		return PROGRESSTIME_UNKNOWN;
	}

}