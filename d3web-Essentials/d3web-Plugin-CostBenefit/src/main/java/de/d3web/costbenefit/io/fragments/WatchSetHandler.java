/*
 * Copyright (C) 2014 denkbares GmbH
 */
package de.d3web.costbenefit.io.fragments;

import java.io.IOException;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.costbenefit.inference.WatchSet;

/**
 * Handels {@link WatchSet}
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 18.09.2014
 */
public class WatchSetHandler implements FragmentHandler<KnowledgeBase> {

	private static final String WATCHSET = "watchset";

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		WatchSet watchSet = new WatchSet();
		for (Element subElement : XMLUtil.getElementList(element.getChildNodes())) {
			watchSet.addQContainer(persistence.getArtifact().getManager().searchQContainer(
					subElement.getTextContent()));
		}
		return watchSet;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		WatchSet watchSet = (WatchSet) object;
		Element element = persistence.getDocument().createElement(WATCHSET);
		for (QContainer qContainer : watchSet.getqContainers()) {
			Element qContainerElement = persistence.getDocument().createElement("qcontainer");
			qContainerElement.setTextContent(qContainer.getName());
			element.appendChild(qContainerElement);
		}
		return element;
	}

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals(WATCHSET);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof WatchSet;
	}

}
