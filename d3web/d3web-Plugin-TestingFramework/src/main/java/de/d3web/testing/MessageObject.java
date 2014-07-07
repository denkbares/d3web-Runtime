/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.testing;

/**
 * Objects (e.g. of a knowledge base) can be associated with an error message. In that way a system
 * displaying the message can render the object in a special way (if a corresponding renderer is at
 * hand).
 *
 * @author Albrecht Striffler
 */
public class MessageObject {

	String objectName;
	Class<?> clazz;

	public MessageObject(String objectName, Class<?> clazz) {
		this.objectName = objectName;
		this.clazz = clazz;
		if (objectName == null) throw new NullPointerException();
		if (clazz == null) throw new NullPointerException();
	}

	public Class<?> geObjectClass() {
		return clazz;
	}

	public String getObjectName() {
		return objectName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MessageObject that = (MessageObject) o;

		if (!clazz.equals(that.clazz)) return false;
		if (!objectName.equals(that.objectName)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = objectName.hashCode();
		result = 31 * result + clazz.hashCode();
		return result;
	}
}
