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

package de.d3web.core.knowledge.terminology;

import de.d3web.core.session.EventSource;


/**
 * Abstract class for knowledge base objects, which provide
 * an ID for retrieval. Nearly all knowledge base objects
 * should extend this class.
 * 
 * @author joba, Christian Betz
 * @see NamedObject
 */
abstract public class IDObject
	extends EventSource
	implements java.io.Serializable, IDReference {
	
	private static final long serialVersionUID = -5476364297254490944L;
	private final String id;

	public IDObject(String id) {
	    this.id = id;
	}
	
	/**
	 * Checks, if other object is an IDObject and if
	 * it contains the same ID.
	 * @return true, if equal
	 * @param other Object to compare for equality
	 */
	public boolean equals(Object other) {
		if (this == other)
			return true;
		else if ((other == null) || (getClass() != other.getClass())) {
			return false;
		} else {
			IDObject otherIDO = (IDObject) other;
			if ((getId() != null) && (otherIDO.getId() != null)) {
				return getId().equals(otherIDO.getId());
			} else {
				return super.equals(other);
			}
		}
	}

	/**
	 * @return the unique identifier of this object.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the hash code of the ID (String).
	 */
	public int hashCode() {
		if (getId() != null)
			return getId().hashCode();
		else
			return super.hashCode();
	}

	/**
	 * @return the ID of the object.
	 */
	public String toString() {
		return this.getClass().getSimpleName() + " " + getId();
	}
}
