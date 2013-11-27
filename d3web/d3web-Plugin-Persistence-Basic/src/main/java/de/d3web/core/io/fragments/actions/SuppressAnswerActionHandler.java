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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.indication.ActionSuppressAnswer;

/**
 * Handels actions supressing answers
 * 
 * @author Norman Brümmer, Markus Friedrich (denkbares GmbH)
 */
public class SuppressAnswerActionHandler implements FragmentHandler<KnowledgeBase> {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, "Action", "ActionSuppressAnswer");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof ActionSuppressAnswer);
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		ActionSuppressAnswer action = new ActionSuppressAnswer();
		NodeList nl = element.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node n = nl.item(i);
			if (n.getNodeName().equalsIgnoreCase("Question")) {
				String id = n.getAttributes().getNamedItem("name").getNodeValue();
				action.setQuestion((QuestionChoice)
						persistence.getArtifact().getManager().searchQuestion(id));
			}
			else if (n.getNodeName().equalsIgnoreCase("Suppress")) {
				NodeList sanslist = n.getChildNodes();
				for (int k = 0; k < sanslist.getLength(); ++k) {
					Node answer = sanslist.item(k);
					if (answer.getNodeName().equalsIgnoreCase("Answer")) {
						String id = answer.getAttributes().getNamedItem("name").getNodeValue();
						action.addSuppress(new ChoiceID(id));
					}
				}
			}
		}
		return action;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		ActionSuppressAnswer action = (ActionSuppressAnswer) object;
		Element element = persistence.getDocument().createElement("Action");
		element.setAttribute("type", "ActionSuppressAnswer");
		XMLUtil.appendQuestionLinkElement(element, action.getQuestion());
		Element suppressNode = persistence.getDocument().createElement("Suppress");
		for (ChoiceID choice : action.getSuppress()) {
			Element answerNode = persistence.getDocument().createElement("Answer");
			answerNode.setAttribute("name", choice.getText());
			suppressNode.appendChild(answerNode);
		}
		element.appendChild(suppressNode);
		return element;
	}

}
