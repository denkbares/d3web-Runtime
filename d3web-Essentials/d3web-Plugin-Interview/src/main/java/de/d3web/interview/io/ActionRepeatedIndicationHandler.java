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
package de.d3web.interview.io;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.interview.indication.ActionRepeatedIndication;

/**
 * Handles {@link ActionRepeatedIndication}
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 30.04.2013
 */
public class ActionRepeatedIndicationHandler implements FragmentHandler<KnowledgeBase> {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, "Action", "RepeatedIndication");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof ActionRepeatedIndication);
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		List<Element> childNodes = XMLUtil.getElementList(element.getChildNodes());
		List<QASet> qaSets = null;
		for (Element child : childNodes) {
			if (child.getNodeName().equalsIgnoreCase("targetQASets")) {
				qaSets = XMLUtil.getTargetQASets(child, persistence.getArtifact());
			}
		}
		ActionRepeatedIndication action = new ActionRepeatedIndication();
		action.setQASets(qaSets);
		return action;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		ActionRepeatedIndication action = (ActionRepeatedIndication) object;
		Element element = persistence.getDocument().createElement("Action");
		String type = "RepeatedIndication";
		element.setAttribute("type", type);
		List<QASet> qaSets = action.getQASets();
		if (qaSets != null) {
			XMLUtil.appendTargetQASets(element, qaSets);
		}
		return element;
	}

}
