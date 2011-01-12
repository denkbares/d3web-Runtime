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
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.shared.QuestionWeightValue;
import de.d3web.shared.SolutionWeightValue;
import de.d3web.shared.Weight;

/**
 * Handels Weights
 * 
 * @author Norman Brümmer, Markus Friedrich (denkbares GmbH)
 */
public class WeightHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, "KnowledgeSlice", "weight");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof Weight);
	}

	@Override
	public Object read(KnowledgeBase kb, Element n) throws IOException {
		String questionID = null;
		String valueQ = null;

		Question q = null;

		questionID = n.getAttributes().getNamedItem("questionID")
				.getNodeValue();
		valueQ = n.getAttributes().getNamedItem("value").getNodeValue();
		q = kb.getManager().searchQuestion(questionID);
		Weight weight = new Weight();

		QuestionWeightValue questionWV = new QuestionWeightValue();
		questionWV.setQuestion(q);
		questionWV.setValue(Weight.convertConstantStringToValue(valueQ));
		weight.setQuestionWeightValue(questionWV);
		NodeList nl = n.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node diagNode = nl.item(i);
			if (diagNode.getNodeName().equalsIgnoreCase("diagnosis")) {
				String diagID = diagNode.getAttributes().getNamedItem("ID")
						.getNodeValue();
				String valueD = diagNode.getAttributes().getNamedItem(
						"value").getNodeValue();
				SolutionWeightValue diagnosisWV = new SolutionWeightValue();
				diagnosisWV.setSolution(kb.getManager().searchSolution(diagID));
				diagnosisWV.setValue(Weight
						.convertConstantStringToValue(valueD));
				weight.addDiagnosisWeightValue(diagnosisWV);
			}
		}
		return weight;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Weight weight = (Weight) object;
		Element element = doc.createElement("KnowledgeSlice");
		element.setAttribute("ID", "W" + weight.getQuestionWeightValue().getQuestion().getId());
		element.setAttribute("type", "weight");
		element.setAttribute("questionID", weight.getQuestionWeightValue().getQuestion().getId());
		element.setAttribute("value",
				Weight.convertValueToConstantString(weight.getQuestionWeightValue().getValue()));
		List<SolutionWeightValue> diagnosisWeightValues = weight.getSolutionWeightValues();
		for (SolutionWeightValue dwv : diagnosisWeightValues) {
			Element dwvNode = doc.createElement("diagnosis");
			dwvNode.setAttribute("ID", dwv.getSolution().getId());
			dwvNode.setAttribute("value", Weight.convertValueToConstantString(dwv.getValue()));
			element.appendChild(dwvNode);
		}
		return element;
	}

}
