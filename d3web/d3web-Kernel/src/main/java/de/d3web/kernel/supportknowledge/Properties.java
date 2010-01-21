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

package de.d3web.kernel.supportknowledge;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties is for adding additional information to an object
 * @see de.d3web.kernel.supportknowledge.PropertiesContainer
 * @author hoernlein
 */
public class Properties implements java.io.Serializable {
	
	private static final long serialVersionUID = 3220708494712501553L;

	public String toString() {
	return properties.toString();}
	
	private Map<Property, Object> properties = new HashMap<Property, Object>();
	
	public Object getProperty(Property pd) {
		return properties.get(pd);
	}
	
	public void setProperty(Property pd, Object o) {
		properties.put(pd, o);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Properties)) return false;
		if (!properties.keySet().equals(((Properties) obj).properties.keySet())) return false;
		return properties.entrySet().equals(((Properties) obj).properties.entrySet());
	}
	
	public Set<Property> getKeys() {
		return properties.keySet();
	}
	
	public boolean isEmpty() {
		return properties.isEmpty();
	}

}
