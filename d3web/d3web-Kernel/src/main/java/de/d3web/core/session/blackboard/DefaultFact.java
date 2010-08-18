/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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

package de.d3web.core.session.blackboard;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Value;

public class DefaultFact implements Fact {

	private final TerminologyObject terminologyObject;
	private final Value value;
	private final Object source;
	private final PSMethod psMethod;

	public DefaultFact(TerminologyObject terminologyObject,
						Value value, Object source, PSMethod psMethod) {
		super();
		this.terminologyObject = terminologyObject;
		this.value = value;
		this.source = source;
		this.psMethod = psMethod;
	}

	@Override
	public PSMethod getPSMethod() {
		return psMethod;
	}

	@Override
	public Object getSource() {
		return source;
	}

	@Override
	public TerminologyObject getTerminologyObject() {
		return terminologyObject;
	}

	@Override
	public Value getValue() {
		return value;
	}

	@Override
	public String toString() {
		return getTerminologyObject() + " = " + getValue() + " [" + getSource() + " / "
				+ getPSMethod() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((psMethod == null) ? 0 : psMethod.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime
				* result
				+ ((terminologyObject == null) ? 0 : terminologyObject
						.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		DefaultFact other = (DefaultFact) obj;
		if (psMethod == null) {
			if (other.psMethod != null) return false;
		}
		else if (!psMethod.equals(other.psMethod)) return false;
		if (source == null) {
			if (other.source != null) return false;
		}
		else if (!source.equals(other.source)) return false;
		if (terminologyObject == null) {
			if (other.terminologyObject != null) return false;
		}
		else if (!terminologyObject.equals(other.terminologyObject)) return false;
		if (value == null) {
			if (other.value != null) return false;
		}
		else if (!value.equals(other.value)) return false;
		return true;
	}
}
