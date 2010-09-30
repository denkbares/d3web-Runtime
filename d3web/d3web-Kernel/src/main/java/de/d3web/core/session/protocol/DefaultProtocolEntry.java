/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.core.session.protocol;

import de.d3web.core.session.blackboard.Fact;

public class DefaultProtocolEntry implements ProtocolEntry {

	private final Fact fact;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fact == null) ? 0 : fact.hashCode());
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
		DefaultProtocolEntry other = (DefaultProtocolEntry) obj;
		if (fact == null) {
			if (other.fact != null) {
				return false;
			}
		}
		else if (!fact.equals(other.fact)) {
			return false;
		}
		return true;
	}

	public DefaultProtocolEntry(Fact fact) {
		this.fact = fact;
	}

	@Override
	public Fact getFact() {
		return this.fact;
	}

	@Override
	public String toString() {
		return this.fact.toString();
	}

}
