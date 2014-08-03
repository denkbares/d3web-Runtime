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
import java.util.List;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.records.io.SessionPersistenceManager;
import de.d3web.costbenefit.session.protocol.CalculatedPathEntry;

/**
 * Saves and loads CalculatedPathEntries
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 28.06.2012
 */
public class CalculatedPathEntryHandler implements FragmentHandler<KnowledgeBase> {

	private static final String ELEMENT_NAME = "entry";
	private static final String ELEMENT_TYPE = "calculatedPath";
	private static final String ATTR_DATE = "date";
	private static final String PATH = "path";
	private static final String QCONTAINER = "qcontainer";
	private static final String CALCULATION_TIME = "calculationTime";

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		try {
			String dateString = element.getAttribute(ATTR_DATE);
			Date date = SessionPersistenceManager.parseDate(dateString);
			List<Element> elementList = XMLUtil.getElementList(element.getChildNodes());
			if (elementList.size() != 2
					|| !elementList.get(0).getNodeName().equals(CALCULATION_TIME)
					|| !elementList.get(1).getNodeName().equals(PATH)) {
				throw new IOException("Element must have exactly two children named "
						+ CALCULATION_TIME + " and " + PATH);
			}
			long time = Long.parseLong(elementList.get(0).getTextContent());
			List<Element> grandChildren = XMLUtil.getElementList(elementList.get(1).getChildNodes());
			String[] qcontainer = new String[grandChildren.size()];
			int i = 0;
			for (Element grandChild : grandChildren) {
				qcontainer[i++] = grandChild.getTextContent();
			}
			return new CalculatedPathEntry(date, qcontainer, time);
		}
		catch (ParseException e) {
			throw new IOException(e);
		}
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		CalculatedPathEntry entry = (CalculatedPathEntry) object;
		String dateString = SessionPersistenceManager.formatDate(entry.getDate());
		Element e = persistence.getDocument().createElement(ELEMENT_NAME);
		e.setAttribute("type", ELEMENT_TYPE);
		e.setAttribute(ATTR_DATE, dateString);
		Element time = persistence.getDocument().createElement(CALCULATION_TIME);
		time.setTextContent(Long.toString(entry.getCalculationTime()));
		e.appendChild(time);
		Element path = persistence.getDocument().createElement(PATH);
		e.appendChild(path);
		for (String s : entry.getPath()) {
			Element qcontainer = persistence.getDocument().createElement(QCONTAINER);
			qcontainer.setTextContent(s);
			path.appendChild(qcontainer);
		}
		return e;
	}

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, ELEMENT_NAME, ELEMENT_TYPE);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof CalculatedPathEntry;
	}

}
