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
 * Created on 24.09.2003
 */
package de.d3web.caserepository.addons.shared;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.xml.sax.Attributes;

import de.d3web.caserepository.addons.IAppliedQSets;
import de.d3web.caserepository.sax.AbstractTagReader;
import de.d3web.kernel.domainModel.qasets.QContainer;

/**
 * 24.09.2003 12:31:02
 * @author hoernlein
 */
public class AppliedQSetsReader extends AbstractTagReader {
	
	private IAppliedQSets aq = null;

	protected AppliedQSetsReader(String id) { super(id); }
	private static AppliedQSetsReader instance;
	private AppliedQSetsReader() { this("AppliedQSetsReader"); }
	public static AbstractTagReader getInstance() {
		if (instance == null)
			instance = new AppliedQSetsReader();
		return instance;
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.readers.AbstractTagReader#getTagNames()
	 */
	public List getTagNames() {
		return Arrays.asList(new String[] {
			"QContainers",
			"QContainer"
		});
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.readers.AbstractTagReader#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	protected void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equals("QContainers"))
			startQContainers();
		else if (qName.equals("QContainer"))
			startQContainer(attributes);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.readers.AbstractTagReader#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	protected void endElement(String uri, String localName, String qName) {
		if (qName.equals("QContainers"))
			endQContainers();
		else if (qName.equals("QContainer"))
			; // do nothing
	}

	private void startQContainers() {
		aq = new AppliedQSets();
	}

	private void endQContainers() {
		getCaseObject().setAppliedQSets(aq);
		aq = null;
	}

	private void startQContainer(Attributes attributes) {
		
		String id = attributes.getValue("id");
		QContainer q = getKnowledgeBase().searchQContainers(id);
		if (q == null) {
			Logger.getLogger(this.getClass().getName()).warning("no qcontainer found for " + id + " - omitting");
			return;
		}
		
		aq.setApplied(q);
		
		boolean e = "yes".equals(attributes.getValue("essential"));
		if (e)
			aq.setEssential(q);

		boolean s = "yes".equals(attributes.getValue("start"));
		if (s)
			aq.setStart(q);
			
	}

}
