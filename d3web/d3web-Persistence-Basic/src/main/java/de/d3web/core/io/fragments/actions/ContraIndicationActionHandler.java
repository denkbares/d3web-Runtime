/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg denkbares GmbH
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
package de.d3web.core.io.fragments.actions;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.indication.ActionContraIndication;

/**
 * Handels ContraIndicationActions
 * 
 * @author Norman Brümmer, Markus Friedrich (denkbares GmbH)
 */
public class ContraIndicationActionHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, "Action", "ActionContraIndication");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof ActionContraIndication);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		List<Element> childNodes = XMLUtil.getElementList(element.getChildNodes());
		List<QASet> qaSets = null;
		for (Element child : childNodes) {
			if (child.getNodeName().equalsIgnoreCase("targetQASets")) {
				qaSets = XMLUtil.getTargetQASets(child, kb);
			}
		}
		ActionContraIndication actionContraIndication = new ActionContraIndication();
		actionContraIndication.setQASets(qaSets);
		return actionContraIndication;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		ActionContraIndication action = (ActionContraIndication) object;
		Element element = doc.createElement("Action");
		element.setAttribute("type", "ActionContraIndication");
		List<QASet> qaSets = action.getQASets();
		XMLUtil.appendTargetQASets(element, qaSets);
		return element;
	}
}
