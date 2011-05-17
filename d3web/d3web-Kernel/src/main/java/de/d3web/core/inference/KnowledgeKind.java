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
 * Helper class to provide explicit information about the knowledge to be stored
 * in the knowledge maps. Creation date: (07.09.00 13:40:08)
 * 
 * @author Joachim Baumeister
 */
public class KnowledgeKind<T extends KnowledgeSlice> {

	private final String kind;
	private final Class<T> clazz;

	/**
	 * Insert the method's description here. Creation date: (07.09.00 13:40:46)
	 * 
	 * @param theKind java.lang.String
	 */
	public KnowledgeKind(String kind, Class<T> clazz) {
		this.kind = kind;
		this.clazz = clazz;
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
		KnowledgeKind<?> other = (KnowledgeKind<?>) obj;
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

	public Class<T> getClazz() {
		return clazz;
	}
}
