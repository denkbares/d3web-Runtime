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
package de.d3web.core.session.values;

import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.Value;

/**
 * This class stores a (string) value assigned to a {@link QuestionText}.
 * 
 * @author joba (denkbares GmbH)
 * @created 07.04.2010
 */
public class TextValue implements QuestionValue {

	private final String value;

	/**
	 * Constructs a new TextValue
	 * 
	 * @param value the String for which a new TextValue should be instantiated
	 * @throws NullPointerException if a null object was passed in
	 */
	public TextValue(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		this.value = value;
	}

	/**
	 * @return the {@link String} of this text value
	 */
	@Override
	public Object getValue() {
		return value;
	}

	public String getText() {
		return value;
	}

	@Override
	public int compareTo(Value o) {
		if (o instanceof TextValue) {
			return value.compareTo(((TextValue) o).value);
		}
		else {
			return -1;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		TextValue other = (TextValue) obj;
		if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getValue().toString();
	}

}
