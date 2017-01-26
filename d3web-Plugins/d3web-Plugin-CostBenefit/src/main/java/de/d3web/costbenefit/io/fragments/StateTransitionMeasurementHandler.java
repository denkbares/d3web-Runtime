/*
 * Copyright (C) 2017 denkbares GmbH. All rights reserved.
 */

package de.d3web.costbenefit.io.fragments;

import java.io.IOException;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.costbenefit.inference.StateTransitionMeasurement;
import de.d3web.interview.io.MeasurementHandler;
import de.d3web.interview.measure.Measurement;

/**
 * @author Volker Belli (denkbares GmbH)
 * @created 26.01.2017
 */
public class StateTransitionMeasurementHandler extends MeasurementHandler {

	private static final String TYPE = "StateTransition";

	@Override
	public boolean canRead(Element element) {
		return super.canRead(element) && TYPE.equals(element.getAttribute(XMLUtil.TYPE));
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		Measurement measurement = (Measurement) super.read(element, persistence);
		return new StateTransitionMeasurement(measurement);
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof StateTransitionMeasurement);
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		Element element = super.write(object, persistence);
		element.setAttribute(XMLUtil.TYPE, TYPE);
		return element;
	}
}
