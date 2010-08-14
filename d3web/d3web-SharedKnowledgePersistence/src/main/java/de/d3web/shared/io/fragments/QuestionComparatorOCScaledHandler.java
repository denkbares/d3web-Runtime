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
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.shared.comparators.QuestionComparator;
import de.d3web.shared.comparators.oc.QuestionComparatorOCScaled;

/**
 * Handles QuestionComparatorOCScaled
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class QuestionComparatorOCScaledHandler extends QuestionComparatorHandler {

	@Override
	protected QuestionComparator getQuestionComparator() {
		return new QuestionComparatorOCScaled();
	}

	@Override
	protected String getType() {
		return "QuestionComparatorOCScaled";
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof QuestionComparatorOCScaled;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Element element = super.write(object, doc);
		QuestionComparatorOCScaled qcocs = (QuestionComparatorOCScaled) object;
		Element scalaElement = doc.createElement("scala");
		for (Double d : qcocs.getValues()) {
			Element scalavalueElement = doc.createElement("scalavalue");
			scalavalueElement.setAttribute("value", "" + d);
			scalaElement.appendChild(scalavalueElement);
		}
		element.appendChild(scalaElement);
		Element constantElement = doc.createElement("constant");
		constantElement.setAttribute("value", "" + qcocs.getConstant());
		element.appendChild(constantElement);
		return element;
	}

	@Override
	protected void addAdditionalInformation(QuestionComparator qcin, Element qnode, KnowledgeBase kb) {
		QuestionComparatorOCScaled qc = (QuestionComparatorOCScaled) qcin;
		if (qnode.getNodeName().equalsIgnoreCase("scala")) {
			NodeList scalavalues = qnode.getChildNodes();
			List<Double> values = new LinkedList<Double>();
			for (int k = 0; k < scalavalues.getLength(); ++k) {
				Node scalaVal = scalavalues.item(k);
				if (scalaVal.getNodeName().equalsIgnoreCase("scalavalue")) {
					Double val = new Double(scalaVal.getAttributes()
							.getNamedItem("value").getNodeValue());
					values.add(val);
				}
			}
			qc.setValues(values);
		}
		else if (qnode.getNodeName().equalsIgnoreCase("constant")) {
			Double val = new Double(qnode.getAttributes()
					.getNamedItem("value").getNodeValue());
			qc.setConstant(val);
		}
	}

}
