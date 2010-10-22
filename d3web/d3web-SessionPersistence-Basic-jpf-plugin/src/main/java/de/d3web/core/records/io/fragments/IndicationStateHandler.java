/*
 * Copyright (C) 2010 denkbares GmbH
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.core.records.io.fragments;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.KnowledgeBase;

/**
 * Handels NumValues
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.09.2010
 */
public class IndicationStateHandler implements FragmentHandler {

	private static final String elementName = "indicationState";

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		String text = element.getTextContent();
		try {
			return Indication.State.valueOf(text);
		}
		catch (IllegalArgumentException e) {
			throw new IOException("invalid text constant " + text + " in indicationState", e);
		}
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Indication.State state = (Indication.State) object;
		Element element = doc.createElement(elementName);
		element.setTextContent(state.name());
		return element;
	}

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals(elementName);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof Indication.State;
	}

}
