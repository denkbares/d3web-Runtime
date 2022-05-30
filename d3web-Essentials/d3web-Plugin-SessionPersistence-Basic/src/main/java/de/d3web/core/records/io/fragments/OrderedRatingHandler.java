package de.d3web.core.records.io.fragments;

import java.io.IOException;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.terminology.OrderedRating;
import de.d3web.core.records.SessionRecord;

/**
 * @author Veronika Oschmann (denkbares GmbH)
 * @created 23.05.22
 */
public class OrderedRatingHandler implements FragmentHandler<SessionRecord> {

	private static final String elementName = "rating";
	private static final String elementType = "ordered";
	private static final String attributeType = "type";
	private static final String attributeOrderKey = "orderKey";

	@Override
	public Object read(Element element, Persistence<SessionRecord> persistence) throws IOException {
		try {
			double score = Double.parseDouble(element.getAttribute(attributeOrderKey));
			return new OrderedRating(element.getTextContent(), score);
		}
		catch (NumberFormatException e) {
			throw new IOException("heuristic score has invalid format", e);
		}
	}

	@Override
	public Element write(Object object, Persistence<SessionRecord> persistence) throws IOException {
		OrderedRating orderedRating = (OrderedRating) object;
		Element element = persistence.getDocument().createElement(elementName);
		element.setAttribute(attributeType, elementType);
		element.setAttribute(attributeOrderKey, String.valueOf(orderedRating.getOrderKey()));
		element.setTextContent(orderedRating.getName());
		return element;
	}

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, elementName, elementType);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof OrderedRating;
	}
}
