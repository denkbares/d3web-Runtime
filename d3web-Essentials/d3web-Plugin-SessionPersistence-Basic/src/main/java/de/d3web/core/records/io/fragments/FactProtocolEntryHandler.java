/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.d3web.core.records.io.fragments;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.session.Value;
import de.d3web.core.session.protocol.FactProtocolEntry;
import de.d3web.strings.Strings;

/**
 * Handles writing of a {@link FactProtocolEntry}. It delegated the writing of
 * the specific value to other fragment handlers.
 * 
 * @author volker_belli
 * @created 20.10.2010
 */
public class FactProtocolEntryHandler implements FragmentHandler<SessionRecord> {

	private static final String ELEMENT_NAME = "entry";
	private static final String ELEMENT_TYPE = "fact";
	private static final String ATTR_DATE = "date";
	private static final String ATTR_OBJECT_NAME = "objectName";
	private static final String ATTR_SOLVER = "psm";

	@Override
	public Object read(Element element, Persistence<SessionRecord> persistence) throws IOException {
		try {
			// prepare fact entry header information
			String dateString = element.getAttribute(ATTR_DATE);
			Date date = Strings.readDate(dateString, Strings.DATE_FORMAT_COMPATIBILITY);
			String name = element.getAttribute(ATTR_OBJECT_NAME);
			String solver = element.getAttribute(ATTR_SOLVER);

			// load fact value by delegate it to the fragments
			List<Element> children = XMLUtil.getElementList(element.getChildNodes());
			if (children.size() != 1) {
				throw new IOException("multiple values are not allowed for a fact entry");
			}
			Value value = (Value) persistence.readFragment(children.get(0));

			// and return the fact
			return new FactProtocolEntry(date, name, solver, value);
		}
		catch (ParseException e) {
			throw new IOException(e);
		}
	}

	@Override
	public Element write(Object object, Persistence<SessionRecord> persistence) throws IOException {
		// prepare information
		FactProtocolEntry entry = (FactProtocolEntry) object;
		String dateString = Strings.writeDate(entry.getDate());

		// create element
		Element element = persistence.getDocument().createElement(ELEMENT_NAME);
		element.setAttribute("type", getElementType());
		element.setAttribute(ATTR_DATE, dateString);
		element.setAttribute(ATTR_OBJECT_NAME, entry.getTerminologyObjectName());
		element.setAttribute(ATTR_SOLVER, entry.getSolvingMethodClassName());

		// append value child/children
		element.appendChild(persistence.writeFragment(entry.getValue()));
		return element;
	}

	protected String getElementType() {
		return ELEMENT_TYPE;
	}

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, ELEMENT_NAME, getElementType());
	}

	@Override
	public boolean canWrite(Object object) {
		return object.getClass().equals(FactProtocolEntry.class);
	}

}
