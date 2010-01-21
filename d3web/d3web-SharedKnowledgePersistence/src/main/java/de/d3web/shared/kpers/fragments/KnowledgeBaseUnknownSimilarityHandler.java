/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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
package de.d3web.shared.kpers.fragments;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.kpers.fragments.FragmentHandler;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.psMethods.shared.comparators.KnowledgeBaseUnknownSimilarity;
/**
 * Handles KnowledgeBaseUnknownSimilaritys
 * In contrary to other KnowledgeSlices, the nodename for the xml representation
 * is globalUnknownSimilarity an not KnowledgeSlice
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class KnowledgeBaseUnknownSimilarityHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals("globalUnknownSimilarity");
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof KnowledgeBaseUnknownSimilarity;
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		Double sim = new Double(element.getAttributes().getNamedItem("value").getNodeValue());
		KnowledgeBaseUnknownSimilarity kbus = new KnowledgeBaseUnknownSimilarity();
		kbus.setId("globalUnknownSim");
		kbus.setSimilarity(sim);
		kbus.setKnowledgeBase(kb);
		return kbus;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		KnowledgeBaseUnknownSimilarity knus = (KnowledgeBaseUnknownSimilarity) object;
		Element gunkSimElement = doc.createElement("globalUnknownSimilarity");
		gunkSimElement.setAttribute("value", ""+knus.getSimilarity());
		return gunkSimElement;
	}

}
