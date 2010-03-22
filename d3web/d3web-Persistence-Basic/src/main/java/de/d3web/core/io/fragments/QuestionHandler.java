/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.core.io.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.knowledge.terminology.QuestionZC;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.knowledge.terminology.info.Properties;
import de.d3web.core.session.values.AnswerChoice;
/**
 * FragmentHandler for Questions
 * Children are ignored, hierarchies are read/written by the knowledge readers/writers.
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class QuestionHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return (element.getNodeName().equals("Question"));
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof Question);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		String type = element.getAttribute("type");
		String id = element.getAttribute("ID");
		Question q = null;
		List<Element> childNodes = XMLUtil.getElementList(element.getChildNodes());
		Element text =  null;
		ArrayList<NumericalInterval> intervalls = null;
		Properties properties = null;
		Element answersElement = null;
		for (Element child: childNodes) {
			if (child.getNodeName().equals("Text")) {
				text = (Element) child;
			}
			else if (child.getNodeName().equals("Answers")) {
				answersElement = (Element) child;
			}
			//If the child is none of the types above and it doesn't contain the children or the costs,
			//it contains the properties or the Intervalls.
			//Costs are no longer stored in IDObjects, so they are ignored
			else if (!child.getNodeName().equals("Children")&&!child.getNodeName().equals("Costs")) {
				Object readFragment = PersistenceManager.getInstance().readFragment(child, kb);
				if (readFragment instanceof Properties) {
					properties = (Properties) readFragment;
				} else if (readFragment instanceof List<?>) {
					intervalls = new ArrayList<NumericalInterval>();
					List<?> list = (List<?>) readFragment;
					for (Object o: list) {
						if (o instanceof NumericalInterval) {
							intervalls.add((NumericalInterval) o);
						}
					}
				} else {
					throw new IOException("Object "+readFragment+" is not a valid child of a question.");
				}
			}
		}
		if (type.equals("YN")) {
			q = new QuestionYN(id);
		} else if (type.equals(QuestionZC.XML_IDENTIFIER)) {
			q = new QuestionZC(id);
		} else if (type.equals("OC")) {
			q = new QuestionOC(id);
		} else if (type.equals("MC")) {
			q = new QuestionMC(id);
		} else if (type.equals("Num")) {
			QuestionNum questionNum = new QuestionNum(id);
			q = questionNum;
			if (intervalls != null) {
				questionNum.setValuePartitions(intervalls);
			}
		} else if (type.equals("Text")) {
			q = new QuestionText(id);
		} else if (type.equals("Date")) {
			q = new QuestionDate(id);
		}
		q.setName(text.getTextContent());
		if (properties != null) {
			q.setProperties(properties);
		}
		if (answersElement!= null && q instanceof QuestionChoice) {
			QuestionChoice qc = (QuestionChoice) q;
			List<Element> answerNodes = XMLUtil.getElementList(answersElement.getChildNodes());
			List<AnswerChoice> answers = new ArrayList<AnswerChoice>();
			for (Element answerElement: answerNodes) {
				answers.add((AnswerChoice) PersistenceManager.getInstance().readFragment(answerElement, kb));
			}
			qc.setAlternatives(answers);
		}
		q.setKnowledgeBase(kb);
		return q;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Question q = (Question) object;
		Element e = doc.createElement("Question");
		XMLUtil.appendTextNode(q.getName(), e);
		e.setAttribute("ID", q.getId());
		if (q instanceof QuestionYN) {
			e.setAttribute("type", "YN");
		} else if (q instanceof QuestionZC) {
			e.setAttribute("type", QuestionZC.XML_IDENTIFIER);
		} else if (q instanceof QuestionOC) {
			e.setAttribute("type", "OC");
		} else if (q instanceof QuestionMC) {
			e.setAttribute("type", "MC");
		} else if (q instanceof QuestionNum) {
			e.setAttribute("type", "Num");
			//adding intervalls
			List<?> valuePartitions = ((QuestionNum) q).getValuePartitions();
			if (valuePartitions!=null) {
				e.appendChild(PersistenceManager.getInstance().writeFragment(valuePartitions, doc));
			}
		} else if (q instanceof QuestionText) {
			e.setAttribute("type", "Text");
		} else if (q instanceof QuestionDate) {
			e.setAttribute("type", "Date");
		}
		
		if (q instanceof QuestionChoice) {
			QuestionChoice qc = (QuestionChoice) q;
			List<AnswerChoice> children = qc.getAllAlternatives();
			if (children != null) {
				Element answerNodes = doc.createElement("Answers");
				for (AnswerChoice child: children) {
					answerNodes.appendChild(PersistenceManager.getInstance().writeFragment(child, doc));
				}
				e.appendChild(answerNodes);
			}
		}
		Properties properties = q.getProperties();
		if (properties!=null && !properties.isEmpty()) {
			e.appendChild(PersistenceManager.getInstance().writeFragment(properties, doc));
		}
		return e;
	}

}
