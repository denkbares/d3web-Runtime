/*
 * Copyright (C) 2016 denkbares GmbH. All rights reserved.
 */

package de.d3web.interview.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;

import com.denkbares.strings.Strings;
import com.denkbares.utils.XMLUtils;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.interview.inference.condition.CondActive;
import de.d3web.interview.measure.Measurement;

/**
 * Handler for reading and writing {@link CondActive}s.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 09.06.16
 */
public class MeasurementHandler implements FragmentHandler<KnowledgeBase> {
	protected static final String ELEMENT = "measurement";
	private static final String ATTR_IDENTIFIER = "identifier";

	private static final String ELEMENT_START = "start";
	private static final String ELEMENT_STOP = "stop";

	private static final String ELEMENT_MAPPING = "mapping";
	private static final String ATTR_MEASURAND = "measurand";
	private static final String ATTR_QUESTION = "question";

	/**
	 * Classes inheriting this class should set a type attribute that uniquely identifies them.
	 * In addition, a lower priority should ensure that canWrite is called on the subclass.
	 */
	protected static final String ATTR_TYPE = "type";

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals(ELEMENT) && Strings.isBlank(element.getAttribute(ATTR_TYPE));
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof Measurement);
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		String identifier = element.getAttribute(ATTR_IDENTIFIER);
		Condition start = null;
		Condition stop = null;
		Map<String, String> mapping = new HashMap<>();

		for (Element child : XMLUtil.getElementList(element.getChildNodes())) {
			switch (child.getNodeName()) {
				case ELEMENT_START:
					start = (Condition) persistence.readFragment(XMLUtils.getFirstChild(child));
					break;
				case ELEMENT_STOP:
					stop = (Condition) persistence.readFragment(XMLUtils.getFirstChild(child));
					break;
				case ELEMENT_MAPPING:
					mapping.put(child.getAttribute(ATTR_MEASURAND), child.getAttribute(ATTR_QUESTION));
					break;
			}
		}

		return new Measurement(identifier, mapping, start, stop);
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		Measurement measurement = (Measurement) object;
		Element node = persistence.getDocument().createElement(ELEMENT);
		node.setAttribute(ATTR_IDENTIFIER, measurement.getIdentifier());

		// creating mapping nodes
		for (Entry<String, String> entry : measurement.getMapping().entrySet()) {
			Element mapping = persistence.getDocument().createElement(ELEMENT_MAPPING);
			mapping.setAttribute(ATTR_MEASURAND, entry.getKey());
			mapping.setAttribute(ATTR_QUESTION, entry.getValue());
			node.appendChild(mapping);
		}

		Condition start = measurement.getStartCondition();
		if (start != null) {
			Element element = persistence.getDocument().createElement(ELEMENT_START);
			element.appendChild(persistence.writeFragment(start));
			node.appendChild(element);
		}
		Condition stop = measurement.getStopCondition();
		if (stop != null) {
			Element element = persistence.getDocument().createElement(ELEMENT_STOP);
			element.appendChild(persistence.writeFragment(stop));
			node.appendChild(element);
		}
		return node;
	}
}
