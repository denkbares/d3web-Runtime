/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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

package de.d3web.xml.domtools;
import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is a utility-class for an easier access to DOM-Nodes (their attributes, children, etc)
 * Creation date: (09.11.2001 14:35:46)
 * @author Christian Betz
 */
public class DOMAccess {

	/**
	 * Tries to find a child-node of the given DOM-Node with the given name 
	 * @param node the Node whose children will be inspected
	 * @param nodeName the name of the child-node to find
	 * @return the childnode found by the specified parameters. <tt>null</tt>, if not found
	 */
	public static Node getChildNode(Node node, String nodeName) {
		NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeName().equals(nodeName)) {
				return nl.item(i);
			}
		}
		return null;
	}

	/**
	 * Returns the content of the text-section of the given DOM-Node 
	 * @param node Node to grab the text-section from
	 * @return the content of the text-section of the given DOM-Node
	 */
	public static String getText(Node node) {

		Iterator iter = new ChildrenIterator(node);
		while (iter.hasNext()) {
			Node child = (Node) iter.next();
			if (child.getNodeType() == Node.CDATA_SECTION_NODE)
				return child.getNodeValue();
		}

		StringBuffer sb = new StringBuffer();
		iter = new ChildrenIterator(node);
		while (iter.hasNext()) {
			Node child = (Node) iter.next();
			if (child.getNodeType() == Node.TEXT_NODE)
				sb.append(child.getNodeValue());
		}
		return sb.toString().trim();
	}
}