/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.d3web.core.records.io;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.records.FactRecord;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.session.Value;

/**
 * Handels the facts element in an xml case repository
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.09.2010
 */
public class FactHandler implements SessionPersistenceHandler {

	@Override
	public void read(Persistence<SessionRecord> persistence, Element sessionElement, ProgressListener listener) throws IOException {
		List<Element> elementList = XMLUtil.getElementList(sessionElement.getChildNodes());
		for (Element e : elementList) {
			if (e.getNodeName().equals("facts")) {
				List<Element> factCategorieList = XMLUtil.getElementList(e.getChildNodes());
				List<FactRecord> valueFacts = new LinkedList<>();
				List<FactRecord> interviewFacts = new LinkedList<>();
				for (Element factCategorieElement : factCategorieList) {
					if (factCategorieElement.getNodeName().equals("valueFacts")) {
						getFacts(persistence, factCategorieElement, valueFacts);
					}
					else if (factCategorieElement.getNodeName().equals("interviewFacts")) {
						getFacts(persistence, factCategorieElement, interviewFacts);
					}
				}
				for (FactRecord fact : valueFacts) {
					persistence.getArtifact().addValueFact(fact);
				}
				for (FactRecord fact : interviewFacts) {
					persistence.getArtifact().addInterviewFact(fact);
				}
			}
		}
	}

	private void getFacts(Persistence<SessionRecord> persistence, Element factCategorieElement, List<FactRecord> facts) throws IOException {
		for (Element factElement : XMLUtil.getElementList(factCategorieElement.getChildNodes())) {
			String idObjectName = factElement.getAttribute("objectName");
			String psmName = factElement.getAttribute("psm");
			if (psmName.isEmpty()) psmName = null;
			// preserve backward compatibility
			// (used class.toString instead of class.getName)
			if (psmName != null && psmName.startsWith("class ")) psmName = psmName.substring(6);
			List<Element> valueNodes = XMLUtil.getElementList(factElement.getChildNodes());
			Object readFragment = persistence.readFragment(valueNodes.get(0));
			FactRecord fact = new FactRecord(
					idObjectName, psmName, (Value) readFragment);
			facts.add(fact);
		}
	}

	@Override
	public void write(Persistence<SessionRecord> persistence, Element sessionElement, ProgressListener listener) throws IOException {
		Document doc = sessionElement.getOwnerDocument();
		SessionRecord record = persistence.getArtifact();

		Element factsElement = doc.createElement("facts");
		sessionElement.appendChild(factsElement);
		Element valueFactsElement = doc.createElement("valueFacts");
		factsElement.appendChild(valueFactsElement);
		Element interviewFactsElement = doc.createElement("interviewFacts");
		factsElement.appendChild(interviewFactsElement);

		addFacts(persistence, record.getValueFacts(), valueFactsElement);
		addFacts(persistence, record.getInterviewFacts(), interviewFactsElement);
	}

	private void addFacts(Persistence<SessionRecord> persistence, List<FactRecord> facts, Element factsElement) throws IOException {
		for (FactRecord fact : facts) {
			Element factElement = persistence.getDocument().createElement("fact");
			factsElement.appendChild(factElement);
			factElement.setAttribute("objectName", fact.getObjectName());
			String psm = fact.getPSM();
			if (psm != null) factElement.setAttribute("psm", psm);
			factElement.appendChild(persistence.writeFragment(fact.getValue()));
		}
	}
}
