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
package de.d3web.core.kpers.fragments;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.kpers.PersistenceManager;
import de.d3web.core.kpers.fragments.FragmentHandler;
import de.d3web.core.kpers.utilities.XMLUtil;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.session.values.AnswerNo;
import de.d3web.core.session.values.AnswerYes;
import de.d3web.core.terminology.info.Properties;
/**
 * Handels Choice Answers
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class AnswerChoiceHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals("Answer");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof AnswerChoice);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		String type = element.getAttribute("type");
		String id = element.getAttribute("ID");
		AnswerChoice ac = null;
		if (type.equals("AnswerNo")) {
			ac = new AnswerNo(id);
		} else if (type.equals("AnswerYes")) {
			ac = new AnswerYes(id);
		} else if (type.equals("AnswerChoice")) {
			ac = new AnswerChoice(id);
		}
		if (ac !=  null) {
			List<Element> childNodes = XMLUtil.getElementList(element.getChildNodes());
			for (Element node: childNodes) {
				if (node.getNodeName().equals("Text")) {
					ac.setText(node.getTextContent());
				} else {
					Object properties = PersistenceManager.getInstance().readFragment(node, kb);
					if (properties instanceof Properties) {
						ac.setProperties((Properties) properties);
					}
				}
			}
		}
		return ac;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Element element = doc.createElement("Answer");
		AnswerChoice a = (AnswerChoice) object;
		element.setAttribute("ID", a.getId());
		element.setAttribute("type", "answer");
		if (a instanceof AnswerNo) {
			element.setAttribute("type", "AnswerNo");
		} else if (a instanceof AnswerYes) {
			element.setAttribute("type", "AnswerYes");
		} else {
			element.setAttribute("type", "AnswerChoice");
		}
		XMLUtil.appendTextNode(a.getText(), element);
		Properties properties = a.getProperties();
		if (properties!=null && !properties.isEmpty()) {
			element.appendChild(PersistenceManager.getInstance().writeFragment(properties, doc));
		}
		return element;
	}

}
