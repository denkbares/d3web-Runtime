/*
 * Copyright (C) 2010 denkbares GmbH
 * 
 * This is free software for non commercial use
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 */
package de.d3web.core.records.io.fragments;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.scoring.HeuristicRating;

/**
 * Handler for HeuristicRating. Needs a higher priority than RatingHandler
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.09.2010
 */
public class HeuristicRatingHandler implements FragmentHandler {

	private static final String elementName = "rating";
	private static final String elementType = "heuristic";
	private static final String attributeType = "type";
	private static final String attributeScore = "score";

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		try {
			double score = Double.parseDouble(element.getAttribute(attributeScore));
			return new HeuristicRating(score);
		}
		catch (NumberFormatException e) {
			throw new IOException("heuristic score has invalid format", e);
		}
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		HeuristicRating hr = (HeuristicRating) object;
		Element element = doc.createElement(elementName);
		element.setAttribute(attributeType, elementType);
		element.setAttribute(attributeScore, String.valueOf(hr.getScore()));
		element.setTextContent(hr.getName());
		return element;
	}

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, elementName, elementType);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof HeuristicRating;
	}

}
