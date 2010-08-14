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
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.shared.comparators.QuestionComparator;
import de.d3web.shared.comparators.num.QuestionComparatorNumSection;

/**
 * Handles QuestionComparatorNumSection
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class QuestionComparatorNumSectionHandler extends QuestionComparatorHandler {

	@Override
	protected String getType() {
		return "QuestionComparatorNumSection";
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof QuestionComparatorNumSectionHandler;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Element element = super.write(object, doc);
		QuestionComparatorNumSection qcns = (QuestionComparatorNumSection) object;
		Element sectionsElement = doc.createElement("sections");
		Iterator<Double> xIter = qcns.getxValues().iterator();
		Iterator<Double> yIter = qcns.getyValues().iterator();
		while (xIter.hasNext()) {
			Element sectionElement = doc.createElement("section");
			sectionElement.setAttribute("xvalue", "" + xIter.next());
			sectionElement.setAttribute("yvalue", "" + yIter.next());
			sectionsElement.appendChild(sectionElement);
		}
		element.appendChild(sectionsElement);
		return element;
	}

	@Override
	protected QuestionComparator getQuestionComparator() {
		return new QuestionComparatorNumSection();
	}

	@Override
	protected void addAdditionalInformation(QuestionComparator qc, Element child, KnowledgeBase kb) {
		if (child.getNodeName().equalsIgnoreCase("sections")) {
			NodeList secs = child.getChildNodes();
			for (int k = 0; k < secs.getLength(); ++k) {
				Node section = secs.item(k);
				if (section.getNodeName().equalsIgnoreCase("section")) {
					Double x = new Double(section.getAttributes()
							.getNamedItem("xvalue").getNodeValue());
					Double y = new Double(section.getAttributes()
							.getNamedItem("yvalue").getNodeValue());

					((QuestionComparatorNumSection) qc).addValuePair(x, y);
				}
			}
		}
	}

}
