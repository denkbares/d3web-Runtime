/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.diaFlux.io;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.inference.FlowchartProcessedCondition;

/**
 * 
 * @author Reinhard Hatko
 * @created 11.11.2010
 */
public class FlowchartProcessedConditionHandler implements FragmentHandler {

	private static final String FLOWCHART_PROCESSED = "FlowchartProcessed";

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		String flowName = element.getElementsByTagName(CallFlowActionFragmentHandler.FLOW_NAME).item(
				0).getTextContent();

		return new FlowchartProcessedCondition(flowName);
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		FlowchartProcessedCondition condition = (FlowchartProcessedCondition) object;

		Element actionElem = doc.createElement("Condition");
		actionElem.setAttribute("type", FLOWCHART_PROCESSED);

		Element flowElem = doc.createElement(CallFlowActionFragmentHandler.FLOW_NAME);
		actionElem.appendChild(flowElem);

		Text flowNameNode = doc.createTextNode(condition.getFlowName());
		flowElem.appendChild(flowNameNode);

		return actionElem;
	}

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkCondition(element, FLOWCHART_PROCESSED);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof FlowchartProcessedCondition;
	}

}
