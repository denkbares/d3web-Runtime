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

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.InfoStoreUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.AnswerNo;
import de.d3web.core.knowledge.terminology.AnswerYes;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.info.Property.Autosave;

/**
 * Handels Choice Answers
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class ChoiceHandler implements FragmentHandler<KnowledgeBase> {

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals("Answer");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof Choice);
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		String type = element.getAttribute("type");
		String id = element.getAttribute("name");
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
			PropertiesHandler ph = new PropertiesHandler();
			for (Element node : childNodes) {
				if (node.getNodeName().equals(XMLUtil.INFO_STORE)) {
					XMLUtil.fillInfoStore(persistence, ac.getInfoStore(), node);
				}
				else if (ph.canRead(node)) {
					InfoStoreUtil.copyEntries(ph.read(persistence, node), ac.getInfoStore());
				}
			}
		}
		return ac;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		Element element = persistence.getDocument().createElement("Answer");
		Choice a = (Choice) object;
		element.setAttribute("name", a.getName());
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
		XMLUtil.appendInfoStore(persistence, element, a, Autosave.basic);
		return element;
	}

}
