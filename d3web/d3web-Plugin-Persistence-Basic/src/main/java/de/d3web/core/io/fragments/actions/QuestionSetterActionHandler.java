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
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.abstraction.ActionSetQuestion;
import de.d3web.core.io.FragmentManager;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;

/**
 * Handles the two default successors ActionAddValue and ActionSetValue of
 * ActionQuestionSetter. Other successors must have their own FragementHandler
 * with a higher priority
 * 
 * @author Norman Brümmer, Markus Friedrich (denkbares GmbH)
 */
public class QuestionSetterActionHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, "Action", "ActionAddValue")
				|| XMLUtil.checkNameAndType(element, "Action", "ActionSetValue");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof ActionSetQuestion);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		Question question = null;
		// value will be later determined, now set to undefined for safety
		Object value = UndefinedValue.getInstance();
		NodeList nl = element.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node child = nl.item(i);
			if (child.getNodeName().equalsIgnoreCase("question")) {
				String id = child.getAttributes().getNamedItem("name").getNodeValue();
				question = kb.getManager().searchQuestion(id);
			}
			else if (child.getNodeName().equalsIgnoreCase("values")) {
				NodeList values = child.getChildNodes();

				List<Object> parsedValues = new LinkedList<Object>();
				for (int k = 0; k < values.getLength(); ++k) {
					Node valNode = values.item(k);
					if (valNode.getNodeName().equalsIgnoreCase("value")) {
						String type = valNode.getAttributes().getNamedItem("type").getNodeValue();
						if (type.equalsIgnoreCase("answer")
								|| type.equalsIgnoreCase("answerChoice")) {
							String name = valNode.getAttributes().getNamedItem("name").getNodeValue();
							if (name.equals(Unknown.getInstance().getValue().toString())) {
								parsedValues.add(Unknown.getInstance());
							}
							else {
								parsedValues.add(new ChoiceValue(name));
							}
						}
						else if (type.equalsIgnoreCase("evaluatable")) {
							List<Element> childNodes = XMLUtil.getElementList(valNode.getChildNodes());
							for (Element e : childNodes) {
								parsedValues.add(PersistenceManager.getInstance().readFragment(
										e, kb));
							}
						}
					}
				}
				if (parsedValues.size() == 1) {
					value = parsedValues.get(0);
				}
				else {
					value = parsedValues;
				}
			}
		}

		ActionSetQuestion action = new ActionSetQuestion();
		action.setQuestion(question);
		action.setValue(value);
		return action;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		ActionSetQuestion action = (ActionSetQuestion) object;
		Question question = action.getQuestion();
		Element element = doc.createElement("Action");
		if (action instanceof ActionSetQuestion) {
			element.setAttribute("type", "ActionSetValue");
		}
		Element questionNode = doc.createElement("Question");
		String questionId = "";
		if (question != null) {
			questionId = question.getName();
		}
		questionNode.setAttribute("name", questionId);
		element.appendChild(questionNode);
		Element valuesNode = doc.createElement("Values");
		FragmentManager pm = PersistenceManager.getInstance();
		if (action != null && action.getValue() instanceof Value) {
			String name = "";
			if (action.getValue() instanceof ChoiceValue) {
				ChoiceValue cv = (ChoiceValue) (action.getValue());
				Choice choice = cv.getChoice((QuestionChoice) question);
				name = choice.getName();
			}
			else {
				name = ((Value) (action.getValue())).getValue().toString();
			}
			Element valueNode = doc.createElement("Value");
			valueNode.setAttribute("type", "answer");
			valueNode.setAttribute("name", name);
			valuesNode.appendChild(valueNode);
		}
		else {
			if (action.getValue() instanceof Object[]) {
				Object[] list = (Object[]) action.getValue();
				for (Object o : list) {
					if (o instanceof Choice) {
						Choice a = (Choice) o;
						if (a.getName() == null) {
							throw new IOException("Answer " + a.getName() + " has no ID");
						}
						Element valueNode = doc.createElement("Value");
						valueNode.setAttribute("type", "answer");
						valueNode.setAttribute("name", a.getName());
						valuesNode.appendChild(valueNode);
					}
					else if (o != null) {
						Element valueNode = doc.createElement("Value");
						valueNode.appendChild(pm.writeFragment(o, doc));
						valueNode.setAttribute("type", "evaluatable");
						valuesNode.appendChild(valueNode);
					}
				}
			}
			else {
				Element valueNode = doc.createElement("Value");
				valueNode.appendChild(pm.writeFragment(action.getValue(), doc));
				valueNode.setAttribute("type", "evaluatable");
				valuesNode.appendChild(valueNode);
			}
		}

		element.appendChild(valuesNode);
		return element;
	}
}
