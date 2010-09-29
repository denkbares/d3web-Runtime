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

package de.d3web.core.knowledge.terminology.info;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties is for adding additional information to an object
 * 
 * @see de.d3web.core.knowledge.terminology.info.PropertiesContainer
 * @author hoernlein
 */
public class Properties {

	@Override
	public String toString() {
		return properties.toString();
	}

	private final Map<Property, Object> properties = new HashMap<Property, Object>();

	public Object getProperty(Property pd) {
		return properties.get(pd);
	}

	public void setProperty(Property pd, Object o) {
		properties.put(pd, o);
	}

	public Set<Property> getKeys() {
		return properties.keySet();
	}

	public boolean isEmpty() {
		return properties.isEmpty();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Properties other = (Properties) obj;
		if (properties == null) {
			if (other.properties != null) {
				return false;
			}
		}
		else if (!properties.equals(other.properties)) {
			return false;
		}
		return true;
	}

}
