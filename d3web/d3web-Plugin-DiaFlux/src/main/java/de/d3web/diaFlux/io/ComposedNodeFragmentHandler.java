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

import org.w3c.dom.Element;
import org.w3c.dom.Text;

import de.d3web.core.io.Persistence;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ComposedNode;

/**
 * 
 * 
 * @author Reinhard Hatko
 * @created 28.11.2010
 */
public class ComposedNodeFragmentHandler extends AbstractNodeFragmentHandler {

	private static final String COMPOSED = "Composed";
	public static final String START_NODE_NAME = "StartNodeName";
	public static final String FLOW_NAME = "FlowName";
	public static final String CALL_FLOW = "CallFlow";

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		String id = element.getAttribute(DiaFluxPersistenceHandler.ID);
		String flowName = element.getElementsByTagName(ComposedNodeFragmentHandler.FLOW_NAME).item(
				0).getTextContent();
		String startNodeName = element.getElementsByTagName(
				ComposedNodeFragmentHandler.START_NODE_NAME).item(0).getTextContent();

		return new ComposedNode(id, flowName, startNodeName);
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		ComposedNode node = (ComposedNode) object;
		Element nodeElement = createNodeElement(node, persistence.getDocument());

		Element composedElem = persistence.getDocument().createElement(COMPOSED);
		nodeElement.appendChild(composedElem);

		Element flowElem = persistence.getDocument().createElement(ComposedNodeFragmentHandler.FLOW_NAME);
		composedElem.appendChild(flowElem);

		Text flowNameNode = persistence.getDocument().createTextNode(node.getCalledFlowName());
		flowElem.appendChild(flowNameNode);

		Element startNodeElem = persistence.getDocument().createElement(ComposedNodeFragmentHandler.START_NODE_NAME);
		composedElem.appendChild(startNodeElem);

		Text startNameNode = persistence.getDocument().createTextNode(node.getCalledStartNodeName());
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
