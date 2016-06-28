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
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;

/**
 * Handler for LinkedLists Must have a lower priority, so that other handlers
 * with special lists are tried first
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class LinkedListHandler implements FragmentHandler<KnowledgeBase> {

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals("List");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof LinkedList<?>);
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		List<Object> list = new LinkedList<>();
		List<Element> children = XMLUtil.getElementList(element.getChildNodes());
		for (Element entry : children) {
			if ((entry != null) && (entry.getNodeName().equals("Entry"))) {
				String textContent = entry.getTextContent();
				if (textContent != null && !textContent.equals("")) {
					Object o = XMLUtil.getPrimitiveValue(textContent, entry.getAttribute("class"));
					list.add(o);
				}
				else {
					List<Element> childNodes = XMLUtil.getElementList(entry.getChildNodes());
					if (childNodes.size() == 1) {
						list.add(persistence.readFragment(childNodes.get(0)));
					}
					else {
						throw new IOException("Entry must have exactly one child.");
					}
				}
			}
		}
		return list;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		Element element = persistence.getDocument().createElement("List");
		LinkedList<?> list = (LinkedList<?>) object;
		for (Object o : list) {
			Element entry = persistence.getDocument().createElement("Entry");
			entry.setAttribute("class", o.getClass().getName());
			entry.appendChild(persistence.writeFragment(o));
			element.appendChild(entry);
		}
		return element;
	}

}
