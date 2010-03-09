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

package de.d3web.caserepository.sax;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.CaseObjectImpl;
import de.d3web.core.knowledge.KnowledgeBase;
/**
 * @author bates
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CaseRepositoryDefaultHandler extends DefaultHandler {

	public static boolean TRACE = false;

	private Map caseObjectsIDMap = new HashMap();
	private Hashtable tagsToRegisteredReadersHash = new Hashtable();
	private Set registeredReaders = new HashSet();

	private CaseObjectImpl currentCaseObject = null;

	private KnowledgeBase knowledgeBase = null;

	private List caseObjects = new LinkedList();

	public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
		this.knowledgeBase = knowledgeBase;
	}

	public List getCaseObjectList() {
		return caseObjects;
	}
		
	public void registerReader(AbstractTagReader reader) {
	    ID2CaseMapper me = new ID2CaseMapper() {
            public CaseObject getCaseObject(String id) {
                return (CaseObject) caseObjectsIDMap.get(id);
            }
	    };
		registeredReaders.add(reader);
		reader.setID2CaseMapper(me);
		
		Iterator iter = reader.getTagNames().iterator();
		while (iter.hasNext()) {
			String tag = iter.next().toString();
			Object o = tagsToRegisteredReadersHash.get(tag);
			if (o == null)
				o = new HashSet();
			((Set) o).add(reader);
			tagsToRegisteredReadersHash.put(tag, o);
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		try {
			Set readers = (Set) tagsToRegisteredReadersHash.get(qName);
			if (readers == null || readers.isEmpty()) {
				if (qName.equals("Problem") || qName.equals("Case")) {
					// [MISC]:marty:old case base format: "Problem", new "Case"
					startCaseObject(attributes);
				}
			} else {
				Iterator iter = readers.iterator();
				while (iter.hasNext()) {
					AbstractTagReader reader = (AbstractTagReader) iter.next();
					reader.activateAndStartElement(uri, localName, qName, attributes);
				}
			}
		} catch (Exception x) {
			Logger.getLogger(this.getClass().getName()).throwing(
				this.getClass().getName(), "startElement", x);
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName) {
		try {
			Set readers = (Set) tagsToRegisteredReadersHash.get(qName);
			if (readers == null || readers.isEmpty()) {
				if (qName.equals("Problem") || qName.equals("Case")) {
					// [MISC]:marty:old case base format: "Problem", new "Case"
					endCaseObject();
				}
			} else {
				Iterator iter = readers.iterator();
				while (iter.hasNext()) {
					AbstractTagReader reader = (AbstractTagReader) iter.next();
					reader.deactivateAndEndElement(uri, localName, qName);
				}
			}
		} catch (Exception x) {
			Logger.getLogger(this.getClass().getName()).throwing(
				this.getClass().getName(), "endElement", x);
		}

	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] chars, int start, int length) {
		Iterator iter = registeredReaders.iterator();
		while (iter.hasNext())
			((AbstractTagReader) iter.next()).characters(chars, start, length);
	}

	private void startCaseObject(Attributes attributes) {
		String id = attributes.getValue("id");
		if (id != null) {
			CaseObjectImpl existingCase = (CaseObjectImpl) caseObjectsIDMap.get(id);
			if (existingCase != null)
				currentCaseObject = existingCase;
			else
				currentCaseObject = new CaseObjectImpl(knowledgeBase);
		} else {
			currentCaseObject = new CaseObjectImpl(knowledgeBase);
		}

		Enumeration enumeration = tagsToRegisteredReadersHash.elements();
		while (enumeration.hasMoreElements()) {
			Iterator iter = ((Set) enumeration.nextElement()).iterator();
			while (iter.hasNext())
				((AbstractTagReader) iter.next()).initialize(knowledgeBase, currentCaseObject);
		}
	}

	private void endCaseObject() {
		
		String id = currentCaseObject.getId();
		
		if (caseObjectsIDMap.containsKey(id)) {
			// do nothing, because we don't want double cases
		} else {
			caseObjects.add(currentCaseObject);
			caseObjectsIDMap.put(id, currentCaseObject);
		}
		
		currentCaseObject = null;
	}

}
