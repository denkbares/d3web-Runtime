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

package de.d3web.core.inference;

/**
 * Helper class to provide explict information about the knowledge to be stored
 * in the ps-method knowledge maps. Creation date: (07.09.00 13:40:08)
 * 
 * @author Joachim Baumeister
 */
public class MethodKind {

	private final String kind;

	public final static MethodKind FORWARD = new MethodKind("FORWARD");
	public final static MethodKind BACKWARD = new MethodKind("BACKWARD");

	/**
	 * Insert the method's description here. Creation date: (07.09.00 13:40:46)
	 * 
	 * @param theKind java.lang.String
	 */
	public MethodKind(String kind) {
		this.kind = kind;
	}

	/**
	 * @return a string representation of this Object
	 */
	@Override
	public String toString() {
		return kind;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
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
		MethodKind other = (MethodKind) obj;
		if (kind == null) {
			if (other.kind != null) {
				return false;
			}
		}
		else if (!kind.equals(other.kind)) {
			return false;
		}
		return true;
	}
}
