/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *                    denkbares GmbH
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
package de.d3web.core.io.fragments.actions;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.terminology.Question;
import de.d3web.core.terminology.QuestionChoice;
import de.d3web.indication.ActionSuppressAnswer;
/**
 * Handels actions supressing answers
 *
 * @author Norman Brümmer, Markus Friedrich (denkbares GmbH)
 */
public class SuppressAnswerActionHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, "Action", "ActionSuppressAnswer");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof ActionSuppressAnswer);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		Question q = null;
		List<AnswerChoice> suppress = new LinkedList<AnswerChoice>();
		NodeList nl = element.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node n = nl.item(i);
			if (n.getNodeName().equalsIgnoreCase("Question")) {
				String id = n.getAttributes().getNamedItem("ID").getNodeValue();
				q = kb.searchQuestion(id);
			} else if (n.getNodeName().equalsIgnoreCase("Suppress")) {
				NodeList sanslist = n.getChildNodes();
				for (int k = 0; k < sanslist.getLength(); ++k) {
					Node answer = sanslist.item(k);
					if (answer.getNodeName().equalsIgnoreCase("Answer")) {
						String id = answer.getAttributes().getNamedItem("ID").getNodeValue();
						AnswerChoice ans = kb.searchAnswerChoice(id);
						suppress.add(ans);
					}
				}
			}
		}
		ActionSuppressAnswer action = new ActionSuppressAnswer();
		action.setQuestion((QuestionChoice) q);
		action.setSuppress(suppress);
		return action;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		ActionSuppressAnswer action = (ActionSuppressAnswer) object;
		Element element = doc.createElement("Action");
		element.setAttribute("type", "ActionSuppressAnswer");
		XMLUtil.appendQuestionLinkElement(element, action.getQuestion());
		Element suppressNode = doc.createElement("Suppress");
		for (AnswerChoice a: action.getSuppress()) {
			Element answerNode = doc.createElement("Answer");
			answerNode.setAttribute("ID", a.getId());
			suppressNode.appendChild(answerNode);
		}
		element.appendChild(suppressNode);
		return element;
	}

}
