/*
 * Copyright (C) 2010 denkbares GmbH
 * 
 * This is free software for non commercial use
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 */
package de.d3web.core.records.io.fragments;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.records.io.SessionPersistenceManager;
import de.d3web.core.session.protocol.TextProtocolEntry;

/**
 * Handles writing of a {@link TextFactProtocolEntry}.
 * 
 * @author volker_belli
 * @created 26.10.2010
 */
public class TextProtocolEntryHandler implements FragmentHandler {

	private static final String ELEMENT_NAME = "entry";
	private static final String ELEMENT_TYPE = "text";
	private static final String ATTR_DATE = "date";

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		try {
			String dateString = element.getAttribute(ATTR_DATE);
			Date date = SessionPersistenceManager.DATE_FORMAT.parse(dateString);
			String text = element.getTextContent();

			// and return the fact
			return new TextProtocolEntry(date, text);
		}
		catch (ParseException e) {
			throw new IOException(e);
		}
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		// prepare information
		TextProtocolEntry entry = (TextProtocolEntry) object;
		String dateString = SessionPersistenceManager.DATE_FORMAT.format(entry.getDate());

		// create element
		Element element = doc.createElement(ELEMENT_NAME);
		element.setAttribute("type", ELEMENT_TYPE);
		element.setAttribute(ATTR_DATE, dateString);
		element.setTextContent(entry.getMessage());
		return element;
	}

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, ELEMENT_NAME, ELEMENT_TYPE);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof TextProtocolEntry;
	}

}
