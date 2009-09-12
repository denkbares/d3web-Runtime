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

/*
 * Created on 16.09.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package de.d3web.caserepository.dom;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.caserepository.AbstractCaseRepositoryHandler;
import de.d3web.caserepository.utilities.JarIndexData;
import de.d3web.caserepository.utilities.Utilities;
import de.d3web.kernel.domainModel.KnowledgeBase;

/**
 * @author Atzmueller
 */
public class CaseRepositoryHandler extends AbstractCaseRepositoryHandler {

	private CaseObjectListCreator colc = null;

	private CaseObjectListCreator getCaseObjectListCreator() {
		if (colc == null)
			colc = new CaseObjectListCreator();
		return colc;
	}

	/* (non-Javadoc)
	 * @see de.d3web.persistence.xml.CaseRepositoryHandler#load(de.d3web.kernel.domainModel.KnowledgeBase, java.net.URL)
	 */
	public List load(KnowledgeBase kb, URL url) {
		Collection ret = null;

		getCaseObjectListCreator().setKnowledgeBase(kb);

		try {
			if (Utilities.isCRXMLFile(url)) {
				ret = getCaseObjectListCreator().createCaseObjectCollection(url);
				// [FIXME]:aha:quick and dirty
				String loc = url.toExternalForm();
				if (url.getProtocol().equals("jar"))
					setStorageLocation(loc.substring(loc.lastIndexOf("!/") + 2));
				else
					Logger.getLogger(this.getClass().getName()).warning("can't find out where to set location based on '" + loc + "'");
			} else if (Utilities.hasCasesInf(url)) {
				URL jarInternalURL = new JarIndexData(url).getCaseRepositoryURL(getId());
				ret = getCaseObjectListCreator().createCaseObjectCollection(jarInternalURL);
				setStorageLocation(jarInternalURL.toExternalForm());
			} else {
				InputStream zipInput = Utilities.getInputStreamFromZipJarURL(url);
				ret = getCaseObjectListCreator().createCaseObjectCollection(zipInput);
				setStorageLocation(url.toExternalForm());
			}

		} catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).throwing(
				this.getClass().getName(), "load", e);
		}

		return (List) ret;
	}

	/**
	 * 
	 * @param itemName
	 * @param creator
	 */
	public void addAdditionalCreator(String itemName, AdditionalCaseObjectCreator creator) {
		getCaseObjectListCreator().addAdditionalCreator(itemName, creator);
	}
	
}
