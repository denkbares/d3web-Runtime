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

import de.d3web.abstraction.ActionAddValue;
import de.d3web.abstraction.ActionQuestionSetter;
import de.d3web.abstraction.ActionSetValue;
import de.d3web.core.KnowledgeBase;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.terminology.Answer;
import de.d3web.core.terminology.Question;
/**
 * Handels the two default successors ActionAddValue and ActionSetValue of ActionQuestionSetter.
 * Other successors must have their own FragementHandler with a higher priority
 * @author Norman Brümmer, Markus Friedrich (denkbares GmbH)
 */
public class QuestionSetterActionHandler implements FragmentHandler{

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, "Action", "ActionAddValue")
			||XMLUtil.checkNameAndType(element, "Action", "ActionSetValue");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof ActionQuestionSetter);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		String roottype = element.getAttribute("type");
		Question question = null;
		List<Object> value = new LinkedList<Object>();
		NodeList nl = element.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node child = nl.item(i);
			if (child.getNodeName().equalsIgnoreCase("question")) {
				String id = child.getAttributes().getNamedItem("ID").getNodeValue();
				question = kb.searchQuestion(id);
			} else if (child.getNodeName().equalsIgnoreCase("values")) {
				NodeList values = child.getChildNodes();
				for (int k = 0; k < values.getLength(); ++k) {
					Node valNode = values.item(k);
					if (valNode.getNodeName().equalsIgnoreCase("value")) {
						String type = valNode.getAttributes().getNamedItem("type").getNodeValue();
						if (type.equalsIgnoreCase("answer") || type.equalsIgnoreCase("answerChoice")) {
							String id = valNode.getAttributes().getNamedItem("ID").getNodeValue();
							value.add(kb.searchAnswerChoice(id));
						} else if (type.equalsIgnoreCase("evaluatable")) {
							List<Element> childNodes = XMLUtil.getElementList(valNode.getChildNodes());
							for (Element e: childNodes) {
								value.add(PersistenceManager.getInstance().readFragment(e, kb));
							}
						}
					}
				}
			}
		}
		ActionQuestionSetter action = null;
		if (roottype.equals("ActionAddValue")) {
			action = new ActionAddValue();
		} else if (roottype.equals("ActionSetValue")) {
			action = new ActionSetValue();
		}
		action.setQuestion(question);
		action.setValues(value.toArray());
		return action;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		ActionQuestionSetter action = (ActionQuestionSetter) object;
		Question question = action.getQuestion();
		Element element = doc.createElement("Action");
		if (action instanceof ActionAddValue) {
			element.setAttribute("type", "ActionAddValue");
		} else if (action instanceof ActionSetValue) {
			element.setAttribute("type", "ActionSetValue");
		}
		Element questionNode = doc.createElement("Question");
		String questionId = "";
		if(question != null) {
			questionId = question.getId();
		}
		questionNode.setAttribute("ID", questionId);
		element.appendChild(questionNode);
		Element valuesNode = doc.createElement("Values");
		PersistenceManager pm = PersistenceManager.getInstance();
		for (Object o: action.getValues()) {
			if (o != null && o instanceof Answer) {
				Answer a = (Answer) o;
				if (a.getId()==null) {
					throw new IOException("Answer "+a.getText()+" has no ID");
				}
				Element valueNode = doc.createElement("Value");
				valueNode.setAttribute("type", "answer");
				valueNode.setAttribute("ID", a.getId());
				valuesNode.appendChild(valueNode);
			} else if (o != null) {
				Element valueNode = doc.createElement("Value");
				valueNode.appendChild(pm.writeFragment(o, doc));
				valueNode.setAttribute("type", "evaluatable");
				valuesNode.appendChild(valueNode);
			}
		}
		element.appendChild(valuesNode);
		return element;
	}
}
