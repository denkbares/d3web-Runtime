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

import java.util.LinkedList;
import java.util.List;
import org.xml.sax.Attributes;
import de.d3web.caserepository.MetaDataImpl;
import de.d3web.core.terminology.info.Property;

/**
 * @author bates
 */
public class MetaDataTagReader extends AbstractTagReader {

	private static MetaDataTagReader instance = null;
	public static AbstractTagReader getInstance() {
		if (instance == null) {
			instance = new MetaDataTagReader("MetaData");
		}
		return instance;
	}
	protected MetaDataTagReader(String id) { super(id); }

	private MetaDataImpl currentMetaData = null;

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#getTagNames()
	 */
	public List getTagNames() {
		List ret = new LinkedList();
		ret.add("Metadata");
		ret.add("Account");
		ret.add("ProcessingTime");
		return ret;
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	protected void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equals("Metadata")) {
			startMetadata(attributes);
		} else if (qName.equals("ProcessingTime")) {
			startProcessingTime(attributes);
		} 
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	protected void endElement(String uri, String localName, String qName) {
		if (qName.equals("Metadata")) {
			endMetadata();
		} else if (qName.equals("Account")) {
			endAccount();
		}
	}

	private void startMetadata(Attributes attributes) {
		currentMetaData = new MetaDataImpl();
	}

	private void endMetadata() {
		getCaseObject().getProperties().setProperty(Property.CASE_METADATA, currentMetaData);
		currentMetaData = null;
	}

	private void startProcessingTime(Attributes attributes) {
		if (currentMetaData != null)
			currentMetaData.setProcessingTime(Long.parseLong(attributes.getValue("value")));
	}

	private void endAccount() {
		if (currentMetaData != null) {
			currentMetaData.setAccount(getTextBetweenCurrentTag());
		}
	}

}
