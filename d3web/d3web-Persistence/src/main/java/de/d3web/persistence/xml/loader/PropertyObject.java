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

package de.d3web.persistence.xml.loader;

import de.d3web.kernel.supportknowledge.Property;

/**
 * Realizes a static representation of properties for a KnowledgeBase
 * This is needed because the properties must be set after parsing the knowledge base source.<br>
 * Creation date: (04.10.2001 18:51:53)
 * @author Norman Brümmer
 */
public class PropertyObject {

	private Property property = null;
	private Object value = null;

	/**
	 * Creates a new PropertyObject
	 */
	public PropertyObject() {
		super();
	}

	/**
	 * @return the encapsulated PropertyDescriptor
	 */
	public Property getProperty() {
		return property;
	}

	/**
	 * @return the encapsulated Property-value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * specifies the PropertyDescriptor to encapsulate
	 */
	public void setProperty(Property newProperty) {
		property = newProperty;
	}

	/**
	 * specifies the value of the encapsulated PropertyDescriptor
	 */
	public void setValue(Object newValue) {
		value = newValue;
	}
}