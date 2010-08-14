/*
 * Copyright (C) 2009 denkbares GmbH
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.shared.comparators.QuestionComparator;
import de.d3web.shared.comparators.num.QuestionComparatorNumFuzzy;

/**
 * Handels QuestionComparatorNumFuzzy
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class QuestionComparatorNumFuzzyHandler extends
		QuestionComparatorHandler {

	public final static String POSITIVE_INFINITY_STR = "+INFINITY";
	public final static String NEGATIVE_INFINITY_STR = "-INFINITY";

	@Override
	protected String getType() {
		return "QuestionComparatorNumFuzzy";
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof QuestionComparatorNumFuzzy;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Element element = super.write(object, doc);
		QuestionComparatorNumFuzzy qcnf = (QuestionComparatorNumFuzzy) object;
		Element fuzzyParametersElement = doc.createElement("fuzzyParameters");
		fuzzyParametersElement.setAttribute("increasingLeft",
				doubleToString(qcnf.getIncreasingLeft()));
		fuzzyParametersElement.setAttribute("constLeft", doubleToString(qcnf.getConstLeft()));
		fuzzyParametersElement.setAttribute("constRight", doubleToString(qcnf.getConstRight()));
		fuzzyParametersElement.setAttribute("decreasingRight",
				doubleToString(qcnf.getDecreasingRight()));
		fuzzyParametersElement.setAttribute("interpretation", qcnf.getInterpretationMethod());
		element.appendChild(fuzzyParametersElement);
		return element;
	}

	private static String doubleToString(Double d) {
		if (d.isInfinite()) {
			if (d.equals(Double.POSITIVE_INFINITY)) {
				return POSITIVE_INFINITY_STR;
			}
			else {
				return NEGATIVE_INFINITY_STR;
			}
		}
		return d + "";
	}

	@Override
	protected QuestionComparator getQuestionComparator() {
		return new QuestionComparatorNumFuzzy();
	}

	@Override
	protected void addAdditionalInformation(QuestionComparator qcin, Element qnode, KnowledgeBase kb) {
		QuestionComparatorNumFuzzy qc = (QuestionComparatorNumFuzzy) qcin;
		if (qnode.getNodeName().equalsIgnoreCase("fuzzyParameters")) {
			Double increasingLeft = QuestionComparatorNumFuzzy.stringToDouble(qnode.getAttributes()
					.getNamedItem("increasingLeft").getNodeValue());
			Double constLeft = QuestionComparatorNumFuzzy.stringToDouble(qnode.getAttributes()
					.getNamedItem("constLeft").getNodeValue());
			Double constRight = QuestionComparatorNumFuzzy.stringToDouble(qnode.getAttributes()
					.getNamedItem("constRight").getNodeValue());
			Double decreasingRight = QuestionComparatorNumFuzzy.stringToDouble(qnode.getAttributes()
					.getNamedItem("decreasingRight").getNodeValue());
			String interpretationMethod = qnode.getAttributes().getNamedItem("interpretation")
					.getNodeValue();

			qc.setIncreasingLeft(increasingLeft);
			qc.setConstLeft(constLeft);
			qc.setConstRight(constRight);
			qc.setDecreasingRight(decreasingRight);
			qc.setInterpretationMethod(interpretationMethod);
		}
	}

}
