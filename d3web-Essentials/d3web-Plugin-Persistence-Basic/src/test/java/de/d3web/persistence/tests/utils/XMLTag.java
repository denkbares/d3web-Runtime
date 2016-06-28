/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.persistence.tests.utils;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author bates This Class represents an XMLTag generated from a DOM-Node. You
 *         can access its name, content, attributes and children.
 */
public class XMLTag {

	private String name = null;
	private Hashtable<String, String> attributes = null;
	private List<XMLTag> children = null;
	private String content = null;

	/**
	 * Creates a new XMLTag-representation with the given name
	 * 
	 * @param name the Name of the new Tag
	 */
	public XMLTag(String name) {
		this.name = name;
		attributes = new Hashtable<>();
		children = new LinkedList<>();
		content = "\n";
	}

	/**
	 * Creates a new XMLTag-representation from the given DOM-Node
	 * 
	 * @param node DOM-Node to create the XMLTag-Object from
	 */
	public XMLTag(Node node) {
		// will create an XMLTag from a Node !

		attributes = new Hashtable<>();
		children = new LinkedList<>();
		content = "\n";

		// name
		name = node.getNodeName();

		// attributes
		NamedNodeMap attr = node.getAttributes();

		if (attr != null) {
			for (int i = 0; i < attr.getLength(); ++i) {
				Node attribute = attr.item(i);
				String aName = attribute.getNodeName();
				String aValue = attribute.getNodeValue();
				addAttribute(aName, aValue);
			}
		}

		// children and content
		NodeList cList = node.getChildNodes();
		for (int i = 0; i < cList.getLength(); ++i) {
			Node child = cList.item(i);
			// String cName = child.getNodeName();

			if (child.getNodeType() == Node.ELEMENT_NODE) {
				addChild(new XMLTag(child));
			}
			else if ((child.getNodeType() == Node.TEXT_NODE) && (child.getNodeValue() != null)) {
				content = child.getNodeValue();
			}
			else if ((child.getNodeType() == Node.CDATA_SECTION_NODE)
					&& (child.getNodeValue() != null)) {
				content = child.getNodeValue();
			}
		}

	}

	/**
	 * Adds a new attribute to this XMLTag
	 * 
	 * @param name the name of the new attribute
	 * @param value the attribute´s value
	 */
	public void addAttribute(String name, String value) {
		attributes.put(name, value);
	}

	/**
	 * Returns the children of this Tag
	 * 
	 * @return List of all children of this XMLTag
	 */
	public List<XMLTag> getChildren() {
		return children;
	}

	/**
	 * Adds a new child to this Tag
	 * 
	 * @param child to add
	 */
	public void addChild(XMLTag child) {
		children.add(child);
	}

	/**
	 * Returns the tag-content.
	 * 
	 * @return the tag´s content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the tag´s name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the content of this tag.
	 * 
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Returns a Hashtable containing attribute-names as key and their value as
	 * value.
	 * 
	 * @return the attributes of this tag in a Hashtable
	 */
	public Hashtable<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * @return a String-representation of this XMLTag
	 */
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();

		ret.append("<");
		ret.append(name);

		Enumeration<String> enumeration = attributes.keys();
		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();
			String value = attributes.get(key);

			ret.append(" ").append(key).append("='").append(value).append("'");
		}
		ret.append(">\n");

		for (XMLTag aChildren : children) {
			ret.append(aChildren);
		}

		ret.append(content).append("\n");

		ret.append("</").append(name).append(">\n");

		return ret.toString();
	}

	/**
	 * Checks for equality by comparing name, attributes, content and children
	 * 
	 * @return true, if equal as described above
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		XMLTag xmlTag = (XMLTag) o;

		if (name != null ? !name.equals(xmlTag.name) : xmlTag.name != null) return false;
		if (attributes != null ? !attributes.equals(xmlTag.attributes) : xmlTag.attributes != null) return false;
		if (children != null ? !children.equals(xmlTag.children) : xmlTag.children != null) return false;
		return content != null ? content.equals(xmlTag.content) : xmlTag.content == null;

	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
		result = 31 * result + (children != null ? children.hashCode() : 0);
		result = 31 * result + (content != null ? content.hashCode() : 0);
		return result;
	}
}
