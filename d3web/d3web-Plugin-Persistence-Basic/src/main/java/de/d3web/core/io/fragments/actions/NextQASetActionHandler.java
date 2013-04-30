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

import de.d3web.core.inference.PSAction;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.indication.ActionIndication;
import de.d3web.indication.ActionInstantIndication;
import de.d3web.indication.ActionNextQASet;
import de.d3web.indication.ActionRelevantIndication;

/**
 * Handles ActionNextQASet and its default successors
 * 
 * @author Norman Brümmer, Markus Friedrich (denkbares GmbH)
 */
public class NextQASetActionHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, "Action", "ActionNextQASet")
				|| XMLUtil.checkNameAndType(element, "Action", "ActionClarify")
				|| XMLUtil.checkNameAndType(element, "Action", "ActionIndication")
				|| XMLUtil.checkNameAndType(element, "Action", "ActionInstantIndication")
				|| XMLUtil.checkNameAndType(element, "Action", "ActionRefine")
				|| XMLUtil.checkNameAndType(element, "Action", "ActionRelevantIndication")
				|| XMLUtil.checkNameAndType(element, "Action", "RepeatedIndication");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof ActionNextQASet);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		String type = element.getAttribute("type");
		List<Element> childNodes = XMLUtil.getElementList(element.getChildNodes());
		List<QASet> qaSets = null;
		for (Element child : childNodes) {
			if (child.getNodeName().equalsIgnoreCase("targetQASets")) {
				qaSets = XMLUtil.getTargetQASets(child, kb);
			}
		}
		PSAction action = null;
		if (type.equals("ActionNextQASet")) {
			throw new IOException("Can not instantiate abstract class ActionNextQASet");
		}
		else if (type.equals("ActionIndication") || type.equals("ActionClarify")
				|| type.equals("ActionRefine")) {
			ActionIndication actionIndication = new ActionIndication();
			actionIndication.setQASets(qaSets);
			action = actionIndication;
		}
		else if (type.equals("ActionInstantIndication")) {
			ActionInstantIndication actionInstantIndication = new ActionInstantIndication();
			actionInstantIndication.setQASets(qaSets);
			action = actionInstantIndication;

		}
		else if (type.equals("ActionRelevantIndication")) {
			ActionRelevantIndication ari = new ActionRelevantIndication();
			ari.setQASets(qaSets);
			action = ari;
		}
		return action;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		ActionNextQASet action = (ActionNextQASet) object;
		Element element = doc.createElement("Action");
		String type = "ActionNextQASet";
		if (object instanceof ActionInstantIndication) {
			type = "ActionInstantIndication";
		}
		else if (object instanceof ActionIndication) {
			type = "ActionIndication";
		}
		else if (object instanceof ActionRelevantIndication) {
			type = "ActionRelevantIndication";
		}
		element.setAttribute("type", type);
		List<QASet> qaSets = action.getQASets();
		if (qaSets != null) {
			XMLUtil.appendTargetQASets(element, qaSets);
		}
		return element;
	}

}
