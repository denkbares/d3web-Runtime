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
import org.w3c.dom.Node;

import de.d3web.core.inference.PSAction;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ActionNode;
import de.d3web.diaFlux.flow.FlowFactory;

/**
 * 
 * @author Reinhard Hatko
 * @created 11.11.2010
 */
public class ActionNodeFragmentHandler extends
		AbstractNodeFragmentHandler {

	public static final String ACTION = "Action";

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		String id = element.getAttribute(DiaFluxPersistenceHandler.ID);

		Node actionElem = element.getElementsByTagName(ACTION).item(0);

		PSAction action = (PSAction) PersistenceManager.getInstance().readFragment(
				(Element) actionElem, kb);

		return FlowFactory.getInstance().createActionNode(id, action);

	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		ActionNode node = (ActionNode) object;
		Element nodeElement = createNodeElement(node, doc);

		Element actionElem = PersistenceManager.getInstance().writeFragment(node.getAction(), doc);

		nodeElement.appendChild(actionElem);

		return nodeElement;
	}

	@Override
	public boolean canRead(Element element) {
		return element.getElementsByTagName(ACTION).getLength() == 1;
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof ActionNode;
	}

}
