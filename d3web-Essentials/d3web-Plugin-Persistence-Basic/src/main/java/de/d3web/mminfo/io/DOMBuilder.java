/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the "License"). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * glassfish/bootstrap/legal/CDDLv1.0.txt or
 * https://glassfish.dev.java.net/public/CDDLv1.0.html. See the License for the
 * specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and
 * include the License file at glassfish/bootstrap/legal/CDDLv1.0.txt. If
 * applicable, add the following below this CDDL HEADER, with the fields
 * enclosed by brackets "[]" replaced with your own identifying information:
 * Portions Copyright [yyyy] [name of copyright owner]
 */
// Copyright (c) 1998, 2005, Oracle. All rights reserved.
package de.d3web.mminfo.io;

import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.d3web.strings.Strings;

public class DOMBuilder {

	private final Document document;
	private final Stack<Node> nodes = new Stack<>();
	private final Element element;

	public DOMBuilder(Document document) throws SAXException {
		this.document = document;
		this.element = document.createElementNS(null, "localRoot");
		this.nodes.push(element);
	}

	public Element getElement() {
		return element;
	}

	public boolean isDone() {
		// we are finished if only the document is left on the stack
		return nodes.size() == 1;
	}

	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
		if (Strings.isBlank(namespaceURI)) namespaceURI = null;
		Element element = document.createElementNS(namespaceURI, qName);
		Node parentNode = nodes.peek();
		parentNode.appendChild(element);
		nodes.push(element);

		int count = atts.getLength();
		for (int i = 0; i < count; i++) {
			element.setAttributeNS(atts.getURI(i), atts.getQName(i), atts.getValue(i));
		}
	}

	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (isDone()) throw new SAXException("parsing exeeds xml tree structure");
		nodes.pop();
	}

	public void characters(char[] ch, int start, int length) {
		String content = new String(ch, start, length);
		if (Strings.isBlank(content)) return;

		Text text = document.createTextNode(content);
		Node parentNode = nodes.peek();
		parentNode.appendChild(text);
	}
}