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

package de.d3web.dialog2.basics.usermanaging;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author bates
 * 
 *         To change this generated comment edit the template variable
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class UserDefinedProperties {

	private Hashtable<String, String> properties = null;

	public UserDefinedProperties() {
		properties = new Hashtable<String, String>();
	}

	public void addProperty(String name, String value) {
		properties.put(name, value);
	}

	public boolean getBooleanPropertyValue(String name) {
		if (name != null) {
			String val = properties.get(name);
			if ((val != null) && val.equalsIgnoreCase("TRUE")) {
				return true;
			}
			return false;
		}
		return false;
	}

	public String getPropertyValue(String name) {
		if (name != null) {
			return properties.get(name);
		}
		return null;
	}

	public String getXMLString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<UserDefinedProperties>");
		Enumeration<String> enu = properties.keys();
		while (enu.hasMoreElements()) {
			String name = enu.nextElement();
			String value = properties.get(name);
			sb.append("<Property name='" + name + "' value='" + value + "' />");
		}

		sb.append("</UserDefinedProperties>");
		return sb.toString();
	}

	public void removeProperty(String name) {
		if (name != null) {
			properties.remove(name);
		}
	}

	@Override
	public String toString() {
		return properties.toString();
	}

}
