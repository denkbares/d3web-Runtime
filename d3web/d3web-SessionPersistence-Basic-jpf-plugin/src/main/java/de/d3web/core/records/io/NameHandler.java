/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.core.records.io;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Element;

import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.records.SessionRecord;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 04.10.2010
 */
public class NameHandler implements SessionPersistenceHandler {

	@Override
	public void read(Element sessionElement, SessionRecord sessionRecord, ProgressListener listener) throws IOException {
		List<Element> elementList = XMLUtil.getElementList(sessionElement.getChildNodes());
		String name = null;
		for (Element e : elementList) {
			if (e.getNodeName().equals("name")) {
				name = e.getTextContent();
			}
		}
		if (name != null) {
			sessionRecord.setName(name);
		}
	}

	@Override
	public void write(Element sessionElement, SessionRecord sessionRecord, ProgressListener listener) throws IOException {
		Element element = sessionElement.getOwnerDocument().createElement("name");
		String name = sessionRecord.getName();
		if (name != null) element.setTextContent(name);
		sessionElement.appendChild(element);
	}

}
