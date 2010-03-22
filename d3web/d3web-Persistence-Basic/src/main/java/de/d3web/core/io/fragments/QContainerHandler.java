/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.core.io.fragments;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.info.Properties;
/**
 * Handler for QContainers
 * Children are ignored, hierarchies are read/written by the knowledge readers/writers.
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class QContainerHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals("QContainer");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof QContainer);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		String id = element.getAttribute("ID");
		String priority = element.getAttribute("priority");
		List<Element> childNodes = XMLUtil.getElementList(element.getChildNodes());
		String text = "";
		Properties properties = null;
		for (Element child: childNodes) {
			if (child.getNodeName().equals("Text")) {
				text = child.getTextContent();
			}
			//if the child is none of the types above and it doesn't contain the children or the costs,
			//it contains the properties.
			//Costs are no longer stored in IDObjects, so they are ignored
			else if (!child.getNodeName().equals("Children")&&!child.getNodeName().equals("Costs")) {
				properties=(Properties) PersistenceManager.getInstance().readFragment(child, kb);
			}
		}
		QContainer qcon = new QContainer(id);
		qcon.setPriority(Integer.getInteger(priority));
		qcon.setName(text);
		if (properties != null) {
			qcon.setProperties(properties);
		}
		qcon.setKnowledgeBase(kb);
		return qcon;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Element element = doc.createElement("QContainer");
		QContainer qContainer = (QContainer) object;
		element.setAttribute("ID", qContainer.getId());
		Integer priority = qContainer.getPriority();
		if (priority!=null) {
			element.setAttribute("priority", priority.toString());
		}
		XMLUtil.appendTextNode(qContainer.getName(), element);
		Properties properties = qContainer.getProperties();
		if (properties!=null && !properties.isEmpty()) {
			element.appendChild(PersistenceManager.getInstance().writeFragment(properties, doc));
		}
		return element;
	}
}