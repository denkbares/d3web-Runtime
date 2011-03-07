/*
 * Copyright (C) 2010 denkbares GmbH
 * 
 * This is free software for non commercial use
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 */
package de.d3web.core.records.io;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Element;

import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.records.SessionRecord;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 04.10.2010
 */
public class NameHandler implements SessionPersistenceHandler {

	@Override
	public void read(Element sessionElement, SessionRecord sessionRecord, ProgressListener listener) throws IOException {
		List<Element> elementList = XMLUtil.getElementList(sessionElement.getChildNodes());
		String name = null;
		for (Element e : elementList) {
			if (e.getNodeName().equals("name")) {
				name = e.getTextContent();
			}
		}
		if (name != null) {
			sessionRecord.setName(name);
		}
	}

	@Override
	public void write(Element sessionElement, SessionRecord sessionRecord, ProgressListener listener) throws IOException {
		Element element = sessionElement.getOwnerDocument().createElement("name");
		String name = sessionRecord.getName();
		if (name != null) element.setTextContent(name);
		sessionElement.appendChild(element);
	}

}
