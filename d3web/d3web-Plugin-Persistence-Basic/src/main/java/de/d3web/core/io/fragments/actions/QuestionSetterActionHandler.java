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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.abstraction.ActionSetQuestion;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;

/**
 * Handles the two default successors ActionAddValue and ActionSetValue of
 * ActionQuestionSetter. Other successors must have their own FragementHandler
 * with a higher priority
 * 
 * @author Norman Brümmer, Markus Friedrich (denkbares GmbH)
 */
public class QuestionSetterActionHandler implements FragmentHandler<KnowledgeBase> {

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
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		Question question = null;
		// value will be later determined, now set to undefined for safety
		Object value = UndefinedValue.getInstance();
		NodeList nl = element.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node child = nl.item(i);
			if (child.getNodeName().equalsIgnoreCase("question")) {
				String id = child.getAttributes().getNamedItem("name").getNodeValue();
				question = persistence.getArtifact().getManager().searchQuestion(id);
			}
			else if (child.getNodeName().equalsIgnoreCase("values")) {
				NodeList values = child.getChildNodes();

				List<Object> parsedValues = new LinkedList<Object>();
				for (int k = 0; k < values.getLength(); ++k) {
					Node valNode = values.item(k);
					if (valNode.getNodeName().equalsIgnoreCase("value")) {
						String type = valNode.getAttributes().getNamedItem("type").getNodeValue();
						if (type.equalsIgnoreCase("evaluatable")) {
							List<Element> childNodes = XMLUtil.getElementList(valNode.getChildNodes());
							for (Element e : childNodes) {
								parsedValues.add(persistence.readFragment(e));
							}
						}
						else {
							String name = valNode.getAttributes().getNamedItem("name").getNodeValue();
							if (type.equalsIgnoreCase("answer")
									|| type.equalsIgnoreCase("answerChoice")) {
								if (name.equals(Unknown.getInstance().getValue().toString())) {
									parsedValues.add(Unknown.getInstance());
								}
								else {
									parsedValues.add(new ChoiceValue(name));
								}
							}
							else if (type.equalsIgnoreCase("num")) {
								parsedValues.add(new NumValue(Double.parseDouble(name)));
							}
							else if (type.equalsIgnoreCase("text")) {
								parsedValues.add(new TextValue(name));
							}
							else if (type.equalsIgnoreCase("date")) {
								parsedValues.add(new DateValue(new Date(Long.parseLong(name))));
							}
							else if (type.equalsIgnoreCase("mcanswer")) {
								List<ChoiceID> ids = new LinkedList<ChoiceID>();
								for (Element grandchild : XMLUtil.getElementList(valNode.getChildNodes())) {
									if (grandchild.getNodeName().equals("choice")) {
										ids.add(new ChoiceID(grandchild.getAttribute("name")));
									}
								}
								parsedValues.add(new MultipleChoiceValue(
										ids.toArray(new ChoiceID[ids.size()])));
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
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		ActionSetQuestion action = (ActionSetQuestion) object;
		Question question = action.getQuestion();
		Element element = persistence.getDocument().createElement("Action");
		if (action instanceof ActionSetQuestion) {
			element.setAttribute("type", "ActionSetValue");
		}
		Element questionNode = persistence.getDocument().createElement("Question");
		String questionId = "";
		if (question != null) {
			questionId = question.getName();
		}
		questionNode.setAttribute("name", questionId);
		element.appendChild(questionNode);
		Element valuesNode = persistence.getDocument().createElement("Values");
		if (action != null && action.getValue() instanceof Value) {
			String name = "";
			Element valueNode = persistence.getDocument().createElement("Value");
			if (action.getValue() instanceof ChoiceValue) {
				ChoiceValue cv = (ChoiceValue) (action.getValue());
				Choice choice = cv.getChoice((QuestionChoice) question);
				valueNode.setAttribute("type", "answer");
				name = choice.getName();
			}
			else if (action.getValue() instanceof NumValue) {
				NumValue numValue = (NumValue) action.getValue();
				name = Double.toString(numValue.getDouble());
				valueNode.setAttribute("type", "num");
			}
			else if (action.getValue() instanceof DateValue) {
				DateValue dateValue = (DateValue) action.getValue();
				name = Long.toString(dateValue.getDate().getTime());
				valueNode.setAttribute("type", "date");
			}
			else if (action.getValue() instanceof TextValue) {
				TextValue textValue = (TextValue) action.getValue();
				name = textValue.getText();
				valueNode.setAttribute("type", "text");
			}
			else if (action.getValue() instanceof MultipleChoiceValue) {
				MultipleChoiceValue mcv = (MultipleChoiceValue) (action.getValue());
				for (ChoiceID cid : mcv.getChoiceIDs()) {
					Element choiceElement = persistence.getDocument().createElement("choice");
					choiceElement.setAttribute("name", cid.getText());
					valueNode.appendChild(choiceElement);
				}
				valueNode.setAttribute("type", "mcanswer");
			}
			else {
				name = ((Value) (action.getValue())).getValue().toString();
				valueNode.setAttribute("type", "answer");// backward
															// compatibility ->
															// unknown
			}
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
						Element valueNode = persistence.getDocument().createElement("Value");
						valueNode.setAttribute("type", "answer");
						valueNode.setAttribute("name", a.getName());
						valuesNode.appendChild(valueNode);
					}
					else if (o != null) {
						Element valueNode = persistence.getDocument().createElement("Value");
						valueNode.appendChild(persistence.writeFragment(o));
						valueNode.setAttribute("type", "evaluatable");
						valuesNode.appendChild(valueNode);
					}
				}
			}
			else {
				Element valueNode = persistence.getDocument().createElement("Value");
				valueNode.appendChild(persistence.writeFragment(action.getValue()));
				valueNode.setAttribute("type", "evaluatable");
				valuesNode.appendChild(valueNode);
			}
		}

		element.appendChild(valuesNode);
		return element;
	}
}
