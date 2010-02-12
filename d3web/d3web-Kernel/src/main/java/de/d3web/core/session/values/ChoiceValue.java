package de.d3web.core.session.values;

import de.d3web.core.session.Value;

/**
 * This class represents a choice entered by a user 
 * during a dialog session.
 * @author joba
 *
 */
public class ChoiceValue implements Value {

	AnswerChoice value;
	
	public ChoiceValue(AnswerChoice value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
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
