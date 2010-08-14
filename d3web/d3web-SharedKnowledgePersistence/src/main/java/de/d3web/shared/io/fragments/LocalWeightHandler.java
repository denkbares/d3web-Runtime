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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Value;
import de.d3web.core.session.ValueFactory;
import de.d3web.shared.LocalWeight;

/**
 * Handles LocalWeights
 * 
 * @author Norman Brümmer, Markus Friedrich (denkbares GmbH)
 */
public class LocalWeightHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, "KnowledgeSlice", "localweight");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof LocalWeight);
	}

	@Override
	public Object read(KnowledgeBase kb, Element n) throws IOException {
		String questionID = null;
		String diagnosisID = null;
		Question q = null;
		Solution d = null;
		questionID = n.getAttributes().getNamedItem("questionID")
				.getNodeValue();
		diagnosisID = n.getAttributes().getNamedItem("diagnosisID")
				.getNodeValue();

		q = kb.searchQuestion(questionID);
		d = kb.searchSolution(diagnosisID);

		if (q instanceof QuestionChoice) {
			LocalWeight lw = new LocalWeight();
			lw.setQuestion(q);
			lw.setSolution(d);
			NodeList abChildren = n.getChildNodes();
			for (int k = 0; k < abChildren.getLength(); ++k) {
				Node abChild = abChildren.item(k);
				if (abChild.getNodeName().equalsIgnoreCase("values")) {
					NodeList vals = abChild.getChildNodes();
					for (int l = 0; l < vals.getLength(); ++l) {
						Node valChild = vals.item(l);
						if (valChild.getNodeName().equalsIgnoreCase(
								"localweight")) {
							String ansID = valChild.getAttributes()
									.getNamedItem("ID").getNodeValue();
							Value ans = XMLUtil.getAnswer(q, ansID);
							String value = valChild.getAttributes()
									.getNamedItem("value").getNodeValue();
							lw.setValue(ans, LocalWeight
									.convertConstantStringToValue(value));
						}
					}
				}
			}
			return lw;
		}
		else {
			throw new IOException(
					"no abnormality handling for questions of type "
							+ q.getClass());
		}
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		LocalWeight localWeight = (LocalWeight) object;
		Element element = doc.createElement("KnowledgeSlice");
		element.setAttribute("ID", "W" + localWeight.getQuestion().getId());
		element.setAttribute("type", "localweight");
		element.setAttribute("questionID", localWeight.getQuestion().getId());
		element.setAttribute("diagnosisID", localWeight.getSolution().getId());
		Element valuesNode = doc.createElement("values");
		Enumeration<Value> answers = localWeight.getAnswerEnumeration();
		while (answers.hasMoreElements()) {
			Value answer = answers.nextElement();
			Element localweightNode = doc.createElement("localweight");
			localweightNode.setAttribute("ID", ValueFactory.getID_or_Value(answer));
			localweightNode.setAttribute("value",
					LocalWeight.convertValueToConstantString(localWeight.getValue(answer)));
			valuesNode.appendChild(localweightNode);
		}
		element.appendChild(valuesNode);
		return element;
	}

}
