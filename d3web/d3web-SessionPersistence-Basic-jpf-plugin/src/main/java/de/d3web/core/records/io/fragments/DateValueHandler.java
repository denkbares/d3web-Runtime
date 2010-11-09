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
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.records.io.SessionPersistenceManager;
import de.d3web.core.session.values.DateValue;

/**
 * Handels DateValues
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.09.2010
 */
public class DateValueHandler implements FragmentHandler {

	private static final String elementName = "dateValue";

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		try {
			return new DateValue(
					SessionPersistenceManager.DATE_FORMAT.parse(element.getTextContent()));
		}
		catch (ParseException e) {
			throw new IOException(e);
		}
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		DateValue dv = (DateValue) object;
		Date date = (Date) dv.getValue();
		Element element = doc.createElement(elementName);
		element.setTextContent(SessionPersistenceManager.DATE_FORMAT.format(date));
		return element;
	}

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals(elementName);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof DateValue;
	}

}
