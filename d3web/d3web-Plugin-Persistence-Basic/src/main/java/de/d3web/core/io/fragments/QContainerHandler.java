/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.core.io.fragments;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.InfoStoreUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.info.Property.Autosave;

/**
 * Handler for QContainers Children are ignored, hierarchies are read/written by
 * the knowledge readers/writers.
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
		String id = element.getAttribute("name");
		List<Element> childNodes = XMLUtil.getElementList(element.getChildNodes());
		QContainer qcon = new QContainer(kb, id);
		PropertiesHandler ph = new PropertiesHandler();
		for (Element child : childNodes) {
			if (child.getNodeName().equals(XMLUtil.INFO_STORE)) {
				XMLUtil.fillInfoStore(qcon.getInfoStore(), child, kb);
			}
			// Read old Properties Format
			else if (ph.canRead(child)) {
				InfoStoreUtil.copyEntries(ph.read(kb, child), qcon.getInfoStore());
			}
		}
		return qcon;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Element element = doc.createElement("QContainer");
		QContainer qContainer = (QContainer) object;
		element.setAttribute("name", qContainer.getName());
		XMLUtil.appendInfoStore(element, qContainer, Autosave.basic);
		return element;
	}
}