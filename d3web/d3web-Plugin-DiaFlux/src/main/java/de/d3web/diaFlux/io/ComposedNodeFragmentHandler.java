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

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.FlowFactory;

/**
 * 
 * 
 * @author Reinhard Hatko
 * @created 28.11.2010
 */
public class ComposedNodeFragmentHandler extends
		AbstractNodeFragmentHandler {

	private static final String COMPOSED = "Composed";

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		String id = element.getAttribute(DiaFluxPersistenceHandler.ID);
		String flowName = element.getElementsByTagName(CallFlowActionFragmentHandler.FLOW_NAME).item(
				0).getTextContent();
		String startNodeName = element.getElementsByTagName(
				CallFlowActionFragmentHandler.START_NODE_NAME).item(0).getTextContent();

		return FlowFactory.getInstance().createComposedNode(id, flowName, startNodeName);
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		ComposedNode node = (ComposedNode) object;
		Element nodeElement = createNodeElement(node, doc);

		Element composedElem = doc.createElement(COMPOSED);
		nodeElement.appendChild(composedElem);

		Element flowElem = doc.createElement(CallFlowActionFragmentHandler.FLOW_NAME);
		composedElem.appendChild(flowElem);

		Text flowNameNode = doc.createTextNode(node.getCalledFlowName());
		flowElem.appendChild(flowNameNode);

		Element startNodeElem = doc.createElement(CallFlowActionFragmentHandler.START_NODE_NAME);
		composedElem.appendChild(startNodeElem);

		Text startNameNode = doc.createTextNode(node.getCalledStartNodeName());
		startNodeElem.appendChild(startNameNode);

		return nodeElement;
	}

	@Override
	public boolean canRead(Element element) {
		return element.getElementsByTagName(COMPOSED).getLength() == 1;
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof ComposedNode;
	}

}
