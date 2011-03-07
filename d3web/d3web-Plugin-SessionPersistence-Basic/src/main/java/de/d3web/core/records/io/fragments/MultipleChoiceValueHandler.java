/*
 * Copyright (C) 2011 denkbares GmbH
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
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.MultipleChoiceValue;

/**
 * Handels MultipleChoiceValues
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.09.2010
 */
public class MultipleChoiceValueHandler implements FragmentHandler {

	private static final String elementName = "multipleChoiceValue";
	private static final String choiceElementName = "choice";

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		List<ChoiceID> choiceIDs = new LinkedList<ChoiceID>();
		for (Element e : XMLUtil.getElementList(element.getChildNodes())) {
			ChoiceID answer = new ChoiceID(e.getTextContent());
			choiceIDs.add(answer);
		}
		return new MultipleChoiceValue(choiceIDs);
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		MultipleChoiceValue mcv = (MultipleChoiceValue) object;
		Element element = doc.createElement(elementName);
		for (ChoiceID choice : mcv.getChoiceIDs()) {
			Element choiceElement = doc.createElement(choiceElementName);
			choiceElement.setTextContent(choice.getText());
			element.appendChild(choiceElement);
		}
		return element;
	}

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals(elementName);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof MultipleChoiceValue;
	}

}