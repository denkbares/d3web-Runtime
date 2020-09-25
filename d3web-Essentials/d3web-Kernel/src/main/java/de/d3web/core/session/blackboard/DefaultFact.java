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

import org.jetbrains.annotations.NotNull;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Value;

/**
 * Default implementation for a fact
 */
public class DefaultFact implements Fact {

	private final TerminologyObject terminologyObject;
	private final Value value;
	private final Object source;
	private final PSMethod psMethod;

	public DefaultFact(TerminologyObject terminologyObject,
					   Value value, Object source, PSMethod psMethod) {
		if (terminologyObject == null || value == null || source == null || psMethod == null) {
			throw new NullPointerException();
		}
		this.terminologyObject = terminologyObject;
		this.value = value;
		this.source = source;
		this.psMethod = psMethod;
	}

	@Override
	@NotNull
	public PSMethod getPSMethod() {
		return psMethod;
	}

	@Override
	@NotNull
	public Object getSource() {
		return source;
	}

	@Override
	@NotNull
	public TerminologyObject getTerminologyObject() {
		return terminologyObject;
	}

	@Override
	@NotNull
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
		result = prime * result + psMethod.hashCode();
		result = prime * result + source.hashCode();
		result = prime * result + terminologyObject.hashCode();
		result = prime * result + value.hashCode();
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
		DefaultFact other = (DefaultFact) obj;
		if (!psMethod.equals(other.psMethod)) {
			return false;
		}
		if (!source.equals(other.source)) {
			return false;
		}
		if (!terminologyObject.equals(other.terminologyObject)) {
			return false;
		}
		return value.equals(other.value);
	}
}
