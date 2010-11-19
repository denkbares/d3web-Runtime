/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.diaFlux.io;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.inference.CallFlowAction;


/**
 * 
 * @author Reinhard Hatko
 * @created 11.11.2010
 */
public class CallFlowActionFragmentHandler implements FragmentHandler {

	private static final String CALL_FLOW = "CallFlow";

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		String flowName = element.getElementsByTagName("FlowName").item(0).getTextContent();
		String startNodeName = element.getElementsByTagName("StartNodeName").item(0).getTextContent();

		return new CallFlowAction(flowName, startNodeName);
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		CallFlowAction action = (CallFlowAction) object;

		Element actionElem = doc.createElement("Action");
		actionElem.setAttribute("type", CALL_FLOW);


		Element flowElem = doc.createElement("FlowName");
		actionElem.appendChild(flowElem);

		Text flowNameNode = doc.createTextNode(action.getFlowName());
		flowElem.appendChild(flowNameNode);

		Element startNodeElem = doc.createElement("StartNodeName");
		actionElem.appendChild(startNodeElem);

		Text startNameNode = doc.createTextNode(action.getStartNodeName());
		startNodeElem.appendChild(startNameNode);

		return actionElem;
	}

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, "Action", CALL_FLOW);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof CallFlowAction;
	}

}
