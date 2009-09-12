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

package de.d3web.utilities.caseLoaders;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.dom.CaseObjectListCreator;
import de.d3web.kernel.supportknowledge.DCElement;
/**
 * Default implementation of an XML-loader.
 * it does NO pre-filtering
 * Creation date: (16.08.2001 20:11:02)
 * @author: Norman Brümmer
 */
public class DomXMLCaseLoader implements XMLCaseLoader {
	private String xmlFile = null;
	private URL xmlFileUrl = null;

	private de.d3web.kernel.domainModel.KnowledgeBase knowledgeBase = null;

	/**
	 * DefaultXMLCaseLoader constructor comment.
	 */
	public DomXMLCaseLoader() {
		super();
	}

	/**
	 * @param xmlFile java.lang.String
	 */
	public Set loadAppend(String kbid) {
		Set ret = new HashSet();
		CaseObjectListCreator creator = new CaseObjectListCreator();
		creator.setKnowledgeBase(knowledgeBase);
		Vector cases = (Vector) creator.createCaseObjectCollection(xmlFile);
		Iterator iter = cases.iterator();
		while (iter.hasNext()) {
			CaseObject co = (CaseObject) iter.next();
			if (co.getId() == null) {
				co.getDCMarkup().setContent(DCElement.IDENTIFIER, Integer.toString(co.hashCode()));
			}
			CaseRepository.getInstance().addCase(kbid, co);
			ret.add(co.getId());
		}
		return ret;
	}

	public Set load(String kbid) {
		CaseRepository.getInstance().purgeAllCases(kbid);
		return loadAppend(kbid);
	}

	/**
	 * Creation date: (22.08.01 00:45:06)
	 * @param newKnowledgeBase de.d3web.kernel.domainModel.KnowledgeBase
	 */
	public void setKnowledgeBase(de.d3web.kernel.domainModel.KnowledgeBase newKnowledgeBase) {
		knowledgeBase = newKnowledgeBase;
	}

	/**
	 * Creation date: (22.08.01 00:45:47)
	 * @param newXmlFile java.lang.String
	 */
	public void setXMLFile(java.lang.String newXmlFile) {
		xmlFile = newXmlFile;
	}

	/**
	 * Creation date: (03.11.01 13:20:11)
	 * @param url java.net.URL
	 */
	public void setXMLFileURL(URL url) {
		xmlFileUrl = url;
		xmlFile = url.getFile();
	}
}