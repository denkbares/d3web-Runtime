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
package de.d3web.shared.io.fragments;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.session.Value;
import de.d3web.core.session.ValueFactory;
import de.d3web.shared.Abnormality;
import de.d3web.shared.AbnormalityInterval;
import de.d3web.shared.AbnormalityNum;
import de.d3web.shared.AbstractAbnormality;

/**
 * Handles the default implementations of AbstractAbnormality. Other Handlers
 * for descendants of AbstractAbnormality must have a higher priority.
 * 
 * @author Norman Brümmer, Markus Friedrich (denkbares GmbH)
 */
public class AbstractAbnormalityHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, "KnowledgeSlice", "abnormality");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof AbstractAbnormality);
	}

	@Override
	public Object read(KnowledgeBase kb, Element n) throws IOException {
		Question question = null;
		NodeList abChildren = n.getChildNodes();
		for (int k = 0; k < abChildren.getLength(); ++k) {
			Node abChild = abChildren.item(k);
			if (abChild.getNodeName().equalsIgnoreCase("question")) {
				question = (Question) kb.search(abChild.getAttributes()
						.getNamedItem("ID").getNodeValue());
				break;
			}
		}
		AbstractAbnormality ret = null;
		if (question instanceof QuestionChoice) {
			Abnormality abnorm = new Abnormality();
			ret = abnorm;
			abnorm.setQuestion(question);
			abChildren = n.getChildNodes();
			for (int k = 0; k < abChildren.getLength(); ++k) {
				Node abChild = abChildren.item(k);
				if (abChild.getNodeName().equalsIgnoreCase("values")) {
					NodeList vals = abChild.getChildNodes();
					for (int l = 0; l < vals.getLength(); ++l) {
						Node valChild = vals.item(l);
						if (valChild.getNodeName().equalsIgnoreCase(
								"abnormality")) {
							String ansID = valChild.getAttributes()
									.getNamedItem("ID").getNodeValue();
							Value ans = XMLUtil.getAnswer(question, ansID);
							String value = valChild.getAttributes()
									.getNamedItem("value").getNodeValue();
							abnorm.addValue(ans, AbstractAbnormality
									.convertConstantStringToValue(value));
						}
					}
				}
			}

		}
		else if (question instanceof QuestionNum) {
			AbnormalityNum abnorm = new AbnormalityNum();
			ret = abnorm;
			abnorm.setQuestion(question);
			abChildren = n.getChildNodes();
			for (Element child : XMLUtil.getElementList(abChildren)) {
				Object readFragment = PersistenceManager.getInstance().readFragment(child, kb);
				if (readFragment instanceof AbnormalityInterval) {
					abnorm.addValue((AbnormalityInterval) readFragment);
				}
				else if (readFragment instanceof List<?>) {
					for (Object o : (List<?>) readFragment) {
						if (o instanceof AbnormalityInterval) {
							abnorm.addValue((AbnormalityInterval) o);
						}
						else {
							throw new IOException("Object " + o + " is no AbnormalityInterval");
						}
					}
				}
				else {
					throw new IOException(
							"Object "
									+ readFragment
									+ " is neighter an AbnormalityInterval nor a list of AbnormalityIntervals");
				}
			}
		}
		else {
			throw new IOException("no abnormality handling for questions of type "
					+ question.getClass());
		}
		return ret;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		AbstractAbnormality abstractAbnormality = (AbstractAbnormality) object;
		Element element = doc.createElement("KnowledgeSlice");
		element.setAttribute("type", "abnormality");
		XMLUtil.appendQuestionLinkElement(element, abstractAbnormality.getQuestion());
		if (abstractAbnormality instanceof Abnormality) {
			Abnormality abnormality = (Abnormality) abstractAbnormality;
			Element valuesNode = doc.createElement("values");
			Enumeration<Value> answers = abnormality.getAnswerEnumeration();
			while (answers.hasMoreElements()) {
				Value answer = answers.nextElement();
				Element abnormalityElement = doc.createElement("abnormality");
				abnormalityElement.setAttribute("ID", ValueFactory.getID_or_Value(answer));
				abnormalityElement.setAttribute(
						"value",
						Abnormality.convertValueToConstantString(abstractAbnormality.getValue(answer)));
				valuesNode.appendChild(abnormalityElement);
			}
			element.appendChild(valuesNode);
		}
		else if (abstractAbnormality instanceof AbnormalityNum) {
			AbnormalityNum abnormalityNum = (AbnormalityNum) abstractAbnormality;
			element.appendChild(PersistenceManager.getInstance().writeFragment(
					abnormalityNum.getIntervals(), doc));
		}
		else {
			throw new IOException("AbstractAbnormalityHandler cannot handle " + object);
		}
		return element;
	}

}
