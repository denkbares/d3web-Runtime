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
import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.sax.AbstractTagReader;
import de.d3web.caserepository.sax.CaseObjectListCreator;
import de.d3web.kernel.supportknowledge.DCElement;
/**
 * Default implementation of an XML-loader (uses sax.CaseObjectListCreator)
 * it does NO pre-filtering
 * @see DomXMLCaseLoader
 * @author: Norman Brümmer, georg
 */
public class DefaultXMLCaseLoader implements XMLCaseLoader {
	private String xmlFile = null;
	private URL xmlFileUrl = null;
	
	private CaseObjectListCreator creator = null;

	private de.d3web.kernel.domainModel.KnowledgeBase knowledgeBase = null;

	/**
	 * DefaultXMLCaseLoader constructor comment.
	 */
	public DefaultXMLCaseLoader() {
		super();
		creator = new CaseObjectListCreator();
	}

	public Set loadAppend(String kbid) {
		Set ret = new HashSet();	
		List cases = creator.createCaseObjectList(new File(xmlFile), knowledgeBase);
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

	public void setKnowledgeBase(de.d3web.kernel.domainModel.KnowledgeBase newKnowledgeBase) {
		knowledgeBase = newKnowledgeBase;
	}

	public void setXMLFile(java.lang.String newXmlFile) {
		xmlFile = newXmlFile;
	}

	public void setXMLFileURL(URL url) {
		xmlFileUrl = url;
		xmlFile = url.getFile();
	}
	
	public void addTagReader(AbstractTagReader tagReader) {
		creator.addTagReader(tagReader);
	}
}