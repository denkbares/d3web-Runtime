/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.core.io.fragments;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.InfoStoreUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.AnswerNo;
import de.d3web.core.knowledge.terminology.AnswerYes;
import de.d3web.core.knowledge.terminology.Choice;

/**
 * Handels Choice Answers
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class ChoiceHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals("Answer");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof Choice);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		String type = element.getAttribute("type");
		String id = element.getAttribute("ID");
		Choice ac = null;
		if (type.equals("AnswerNo")) {
			ac = new AnswerNo(id);
		}
		else if (type.equals("AnswerYes")) {
			ac = new AnswerYes(id);
		}
		else if (type.equals("AnswerChoice")) {
			ac = new Choice(id);
		}
		if (ac != null) {
			List<Element> childNodes = XMLUtil.getElementList(element.getChildNodes());
			for (Element node : childNodes) {
				if (node.getNodeName().equals("Text")) {
					ac.setText(node.getTextContent());
				}
				else {
					InfoStore source = (InfoStore) PersistenceManager.getInstance().readFragment(
							node, kb);
					InfoStoreUtil.copyEntries(source, ac.getInfoStore());
				}
			}
		}
		return ac;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Element element = doc.createElement("Answer");
		Choice a = (Choice) object;
		element.setAttribute("ID", a.getId());
		element.setAttribute("type", "answer");
		if (a instanceof AnswerNo) {
			element.setAttribute("type", "AnswerNo");
		}
		else if (a instanceof AnswerYes) {
			element.setAttribute("type", "AnswerYes");
		}
		else {
			element.setAttribute("type", "AnswerChoice");
		}
		XMLUtil.appendTextNode(a.getName(), element);
		InfoStore infoStore = a.getInfoStore();
		if (infoStore != null && !infoStore.isEmpty()) {
			element.appendChild(PersistenceManager.getInstance().writeFragment(infoStore, doc));
		}
		return element;
	}

}
