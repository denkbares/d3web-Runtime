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

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.session.values.NumValue;

/**
 * Handels NumValues
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.09.2010
 */
public class NumValueHandler implements FragmentHandler<SessionRecord> {

	private static final String elementName = "numValue";

	@Override
	public Object read(Element element, Persistence<SessionRecord> persistence) throws IOException {
		return new NumValue(Double.parseDouble(element.getTextContent()));
	}

	@Override
	public Element write(Object object, Persistence<SessionRecord> persistence) throws IOException {
		NumValue numValue = (NumValue) object;
		Element element = persistence.getDocument().createElement(elementName);
		element.setTextContent(numValue.getValue().toString());
		return element;
	}

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals(elementName);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof NumValue;
	}

}
