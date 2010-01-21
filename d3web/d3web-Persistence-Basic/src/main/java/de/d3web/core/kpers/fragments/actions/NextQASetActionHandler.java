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
package de.d3web.core.kpers.fragments.actions;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.kpers.fragments.FragmentHandler;
import de.d3web.core.kpers.utilities.XMLUtil;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.RuleAction;
import de.d3web.kernel.psMethods.nextQASet.ActionClarify;
import de.d3web.kernel.psMethods.nextQASet.ActionIndication;
import de.d3web.kernel.psMethods.nextQASet.ActionInstantIndication;
import de.d3web.kernel.psMethods.nextQASet.ActionNextQASet;
import de.d3web.kernel.psMethods.nextQASet.ActionRefine;
/**
 * Handles ActionNextQASet and its default successors
 * @author Norman Brümmer, Markus Friedrich (denkbares GmbH)
 */
public class NextQASetActionHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, "Action", "ActionNextQASet")
		||XMLUtil.checkNameAndType(element, "Action", "ActionClarify")
		||XMLUtil.checkNameAndType(element, "Action", "ActionIndication")
		||XMLUtil.checkNameAndType(element, "Action", "ActionInstantIndication")
		||XMLUtil.checkNameAndType(element, "Action", "ActionRefine");
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
		Diagnosis diag = null;
		for (Element child: childNodes) {
			if (child.getNodeName().equalsIgnoreCase("targetQASets")) {
				qaSets = XMLUtil.getTargetQASets((Element) child, kb);
			} else if (child.getNodeName().equalsIgnoreCase("targetDiagnosis")) {
				String id = child.getAttributes().getNamedItem("ID").getNodeValue();
				diag = (Diagnosis) kb.search(id);
			}
		}
		RuleAction action = null;
		if (type.equals("ActionNextQASet")) {
			throw new IOException("Can not instantiate abstract class ActionNextQASet");
		} else if (type.equals("ActionClarify")) {
			ActionClarify actionClarify = new ActionClarify();
			actionClarify.setTarget(diag);
			actionClarify.setQASets(qaSets);
			action = actionClarify;
		} else  if (type.equals("ActionIndication")) {
			ActionIndication actionIndication = new ActionIndication();
			actionIndication.setQASets(qaSets);
			action = actionIndication;
		} else  if (type.equals("ActionInstantIndication")) {
			ActionInstantIndication actionInstantIndication = new ActionInstantIndication();
			actionInstantIndication.setQASets(qaSets);
			action = actionInstantIndication;
			
		} else  if (type.equals("ActionRefine")) {
			ActionRefine actionRefine = new ActionRefine();
			actionRefine.setQASets(qaSets);
			actionRefine.setTarget(diag);
			action = actionRefine;
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
		} else if (object instanceof ActionIndication) {
			type = "ActionIndication";
		} else if (object instanceof ActionClarify) {
			type = "ActionClarify";
			ActionClarify actionClarify = (ActionClarify) object;
			appendDiag(doc, element, actionClarify.getTarget());
		} else if (object instanceof ActionRefine) {
			type = "ActionRefine";
			ActionRefine actionClarify = (ActionRefine) object;
			appendDiag(doc, element, actionClarify.getTarget());
		}
		element.setAttribute("type", type);
		List<QASet> qaSets = action.getQASets();
		if (qaSets!=null) {
			XMLUtil.appendTargetQASets(element, qaSets);
		}
		return element;
	}
	
	private void appendDiag(Document doc, Element element, Diagnosis diag) {
		Element diagElement = doc.createElement("targetDiagnosis");
		if (diag != null) {
			diagElement.setAttribute("ID", diag.getId());
		} else {
			diagElement.setAttribute("ID", "");
		}
		element.appendChild(diagElement);
	}

}
