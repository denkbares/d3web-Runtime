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
package de.d3web.shared.io.fragments;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.shared.comparators.QuestionComparator;
/**
 * Provides basic functions for QuestionComparatorHandlers
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public abstract class QuestionComparatorHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, "KnowledgeSlice", getType());
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Element element = doc.createElement("KnowledgeSlice");
		QuestionComparator qc = (QuestionComparator) object;
		element.setAttribute("ID", qc.getId());
		element.setAttribute("type", getType());
		XMLUtil.appendQuestionLinkElement(element, qc.getQuestion());
		Element unknownSimilarityElement = doc.createElement("unknownSimilarity");
		unknownSimilarityElement.setAttribute("value", ""+qc.getUnknownSimilarity());
		element.appendChild(unknownSimilarityElement);
		return element;
	}
	
	protected abstract String getType();

	@Override
	public Object read(KnowledgeBase kb, Element n) throws IOException {
		QuestionComparator qc = getQuestionComparator();
		NodeList nl = n.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node qnode = nl.item(i);
			if (qnode.getNodeName().equalsIgnoreCase("unknownSimilarity")) {
				Double unknownSim = new Double(qnode.getAttributes()
						.getNamedItem("value").getNodeValue());
				qc.setUnknownSimilarity(unknownSim.doubleValue());
			} else if (qnode.getNodeName().equalsIgnoreCase("question")) {
				String qid = qnode.getAttributes().getNamedItem("ID")
						.getNodeValue();
				Question q = (Question) kb.search(qid);
				qc.setQuestion(q);
			} else if (qnode instanceof Element) {
				addAdditionalInformation(qc, (Element) qnode, kb);
			}
		}
		return qc;
	}
	
	/**
	 * Provides a new instance of the QuestionComparator of this Handler
	 * @return new instance of the represented QuestionComparator
	 */
	protected abstract QuestionComparator getQuestionComparator();
	
	/**
	 * This methods adds additional information from the xml representation to
	 * the QuestionComparator.
	 * The default implementation adds no information, only a few QuestionComparatorHandler
	 * need this functionality
	 * @param qc QuestionComparator, who has to be filled with additional information
	 * @param element Element representing the additional information
	 * @param kb KnowledgeBase
	 * @throws IOException if an error occurs
	 */
	protected void addAdditionalInformation(QuestionComparator qc, Element element, KnowledgeBase kb) throws IOException {
		//default is no additional information
	}
}
