/*
 * Copyright (C) 2010 denkbares GmbH
 * 
 * This is free software for non commercial use
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 */
package de.d3web.core.records.io;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.NoSuchFragmentHandlerException;
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
	public void read(Element sessionElement, SessionRecord sessionRecord, ProgressListener listener) throws IOException {
		List<Element> elementList = XMLUtil.getElementList(sessionElement.getChildNodes());
		for (Element e : elementList) {
			if (e.getNodeName().equals("facts")) {
				List<Element> factCategorieList = XMLUtil.getElementList(e.getChildNodes());
				List<FactRecord> valueFacts = new LinkedList<FactRecord>();
				List<FactRecord> interviewFacts = new LinkedList<FactRecord>();
				for (Element factCategorieElement : factCategorieList) {
					if (factCategorieElement.getNodeName().equals("valueFacts")) {
						getFacts(factCategorieElement, valueFacts);
					}
					else if (factCategorieElement.getNodeName().equals("interviewFacts")) {
						getFacts(factCategorieElement, interviewFacts);
					}
				}
				for (FactRecord fact : valueFacts) {
					sessionRecord.addValueFact(fact);
				}
				for (FactRecord fact : interviewFacts) {
					sessionRecord.addInterviewFact(fact);
				}
			}
		}
	}

	private void getFacts(Element factCategorieElement, List<FactRecord> facts) throws NoSuchFragmentHandlerException, IOException {
		for (Element factElement : XMLUtil.getElementList(factCategorieElement.getChildNodes())) {
			String idObjectName = factElement.getAttribute("objectName");
			String psmName = factElement.getAttribute("psm");
			if (psmName.length() == 0) psmName = null;
			List<Element> valueNodes = XMLUtil.getElementList(factElement.getChildNodes());
			SessionPersistenceManager spm = SessionPersistenceManager.getInstance();
			Object readFragment = spm.readFragment(valueNodes.get(0), null);
			FactRecord fact = new FactRecord(
					idObjectName, psmName, (Value) readFragment);
			facts.add(fact);
		}
	}

	@Override
	public void write(Element sessionElement, SessionRecord sessionRecord, ProgressListener listener) throws IOException {
		Document doc = sessionElement.getOwnerDocument();
		Element factsElement = doc.createElement("facts");
		sessionElement.appendChild(factsElement);
		Element valueFactsElement = doc.createElement("valueFacts");
		factsElement.appendChild(valueFactsElement);
		Element interviewFactsElement = doc.createElement("interviewFacts");
		factsElement.appendChild(interviewFactsElement);
		addFacts(sessionRecord.getValueFacts(), doc, valueFactsElement);
		addFacts(sessionRecord.getInterviewFacts(), doc, interviewFactsElement);
	}

	private void addFacts(List<FactRecord> facts, Document doc, Element factsElement) throws NoSuchFragmentHandlerException, IOException {
		for (FactRecord fact : facts) {
			Element factElement = doc.createElement("fact");
			factsElement.appendChild(factElement);
			factElement.setAttribute("objectName", fact.getObjectName());
			String psm = fact.getPsm();
			if (psm != null) factElement.setAttribute("psm", psm);
			factElement.appendChild(SessionPersistenceManager.getInstance().writeFragment(
					fact.getValue(), doc));
		}
	}
}
