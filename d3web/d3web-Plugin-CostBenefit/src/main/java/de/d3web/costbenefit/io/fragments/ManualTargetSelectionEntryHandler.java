/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.costbenefit.io.fragments;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.records.io.SessionPersistenceManager;
import de.d3web.costbenefit.session.protocol.ManualTargetSelectionEntry;

/**
 * Saves and loads ManualTargetSelectionEntrys
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 28.06.2012
 */
public class ManualTargetSelectionEntryHandler implements FragmentHandler<KnowledgeBase> {

	private static final String ELEMENT_NAME = "entry";
	private static final String ELEMENT_TYPE = "manuallySelectedTarget";
	private static final String ATTR_DATE = "date";
	private static final String TARGETS = "targets";
	private static final String TARGET = "target";

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		try {
			String dateString = element.getAttribute(ATTR_DATE);
			Date date = SessionPersistenceManager.parseDate(dateString);
			List<String> targets = new LinkedList<String>();
			List<Element> elementList = XMLUtil.getElementList(element.getChildNodes());
			if (elementList.size() != 1 || !elementList.get(0).getNodeName().equals(TARGETS)) {
				throw new IOException("Element must have exactly one child named " + TARGETS);
			}
			for (Element grandChild : XMLUtil.getElementList(elementList.get(0).getChildNodes())) {
				targets.add(grandChild.getTextContent());
			}
			return new ManualTargetSelectionEntry(date, targets.toArray(new String[targets.size()]));
		}
		catch (ParseException e) {
			throw new IOException(e);
		}
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		ManualTargetSelectionEntry entry = (ManualTargetSelectionEntry) object;
		String dateString = SessionPersistenceManager.formatDate(entry.getDate());
		Element e = persistence.getDocument().createElement(ELEMENT_NAME);
		e.setAttribute("type", ELEMENT_TYPE);
		e.setAttribute(ATTR_DATE, dateString);
		Element targets = persistence.getDocument().createElement(TARGETS);
		e.appendChild(targets);
		for (String s : entry.getTargetNames()) {
			Element target = persistence.getDocument().createElement(TARGET);
			target.setTextContent(s);
			targets.appendChild(target);
		}
		return e;
	}

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, ELEMENT_NAME, ELEMENT_TYPE);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof ManualTargetSelectionEntry;
	}

}
