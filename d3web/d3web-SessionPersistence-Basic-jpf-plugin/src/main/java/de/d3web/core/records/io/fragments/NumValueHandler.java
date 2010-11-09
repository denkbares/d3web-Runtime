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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.values.NumValue;

/**
 * Handels NumValues
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.09.2010
 */
public class NumValueHandler implements FragmentHandler {

	private static final String elementName = "numValue";

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		return new NumValue(Double.parseDouble(element.getTextContent()));
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		NumValue numValue = (NumValue) object;
		Element element = doc.createElement(elementName);
		element.setTextContent(numValue.getValue().toString());
		return element;
	}

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals(elementName);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof NumValue;
	}

}
