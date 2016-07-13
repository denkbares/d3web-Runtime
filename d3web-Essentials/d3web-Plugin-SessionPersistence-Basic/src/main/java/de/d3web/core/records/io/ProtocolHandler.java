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
package de.d3web.core.records.io;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import com.denkbares.progress.ProgressListener;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.session.protocol.Protocol;
import de.d3web.core.session.protocol.ProtocolEntry;

/**
 * Handels the facts element in an xml case repository
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.09.2010
 */
public class ProtocolHandler implements SessionPersistenceHandler {

	private static final String HANDLER_ELEMENT_NAME = "protocol";

	@Override
	public void read(Persistence<SessionRecord> persistence, Element sessionElement, ProgressListener listener) throws IOException {
		List<Element> handlerNodes = XMLUtil.getElementList(sessionElement.getChildNodes());
		Protocol protocol = persistence.getArtifact().getProtocol();
		for (Element handlerNode : handlerNodes) {
			if (handlerNode.getNodeName().equalsIgnoreCase(HANDLER_ELEMENT_NAME)) {
				List<Element> entryNodes = XMLUtil.getElementList(handlerNode.getChildNodes());
				for (Element entryNode : entryNodes) {
					ProtocolEntry entry = (ProtocolEntry) persistence.readFragment(entryNode);
					protocol.addEntry(entry);
				}
			}
		}
	}

	@Override
	public void write(Persistence<SessionRecord> persistence, Element sessionElement, ProgressListener listener) throws IOException {
		Document doc = sessionElement.getOwnerDocument();
		Element handlerNode = doc.createElement(HANDLER_ELEMENT_NAME);
		sessionElement.appendChild(handlerNode);

		Protocol protocol = persistence.getArtifact().getProtocol();
		for (ProtocolEntry entry : protocol.getProtocolHistory()) {
			Element entryNode = persistence.writeFragment(entry);
			handlerNode.appendChild(entryNode);
		}
	}

}
