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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.kpers.fragments.FragmentHandler;
import de.d3web.core.kpers.utilities.Util;
import de.d3web.core.kpers.utilities.XMLUtil;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.Score;
import de.d3web.kernel.psMethods.heuristic.ActionHeuristicPS;
/**
 * Handels HeuristicPSActions
 * @author Norman Brümmer, Markus Friedrich(denkbares GmbH)
 */
public class HeuristicPSActionHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, "Action", "ActionHeuristicPS");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof ActionHeuristicPS);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		Score score = null;
		Diagnosis diag = null;
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			Node child = children.item(i);
			if (child.getNodeName().equalsIgnoreCase("Score")) {
				String value = child.getAttributes().getNamedItem("value").getNodeValue();
				score = Util.getScore(value);
			} else if (child.getNodeName().equalsIgnoreCase("Diagnosis")) {
				String id = child.getAttributes().getNamedItem("ID").getNodeValue();
				diag = kb.searchDiagnosis(id);
			}
		}
		ActionHeuristicPS actionHeuristicPS = new ActionHeuristicPS();
		actionHeuristicPS.setDiagnosis(diag);
		actionHeuristicPS.setScore(score);
		return actionHeuristicPS;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		ActionHeuristicPS action = (ActionHeuristicPS) object;
		Element element = doc.createElement("Action");
		element.setAttribute("type", "ActionHeuristicPS");
		Score theScore = action.getScore();
		Diagnosis theDiag = action.getDiagnosis();
		String scoreSymbol = "";
		String diagId = "";
		if(theScore != null) {
			scoreSymbol = theScore.getSymbol();
			if ((scoreSymbol == null) || (scoreSymbol == ""))
				scoreSymbol = theScore.getScore() + "";
		} 
		if(theDiag != null) {
			diagId = theDiag.getId();
		}
		Element scoreElement = doc.createElement("Score");
		scoreElement.setAttribute("value", scoreSymbol);
		Element diagElement = doc.createElement("Diagnosis");
		diagElement.setAttribute("ID", diagId);
		element.appendChild(diagElement);
		element.appendChild(scoreElement);
		return element;
	}

}
