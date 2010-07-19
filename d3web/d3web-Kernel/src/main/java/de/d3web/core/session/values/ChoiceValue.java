/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.core.session.values;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.Value;

/**
 * This class represents a choice entered by a user
 * during a dialog session.
 * @author joba
 *
 */
public class ChoiceValue implements QuestionValue {

	Choice value;
	
	public ChoiceValue(Choice value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}
	
	public String getAnswerChoiceID() {
		if (value != null) {
			return value.getId();
		}
		else {
			return "";
		}
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChoiceValue other = (ChoiceValue) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public int compareTo(Value o) {
		// there is no possibility to compare ChoiceValue since
		// we do not know the other ChoiceValue instances
		return 0;
	}

}
