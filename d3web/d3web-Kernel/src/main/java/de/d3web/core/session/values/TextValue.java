package de.d3web.core.session.values;

import de.d3web.core.session.Value;

public class TextValue implements Value {

	private String value;

	public TextValue(String value) {
		this.value = value;
	}
	
	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public int compareTo(Value o) {
		if (o instanceof TextValue) {
			return value.compareTo(((TextValue)o).value);
		} else
			return -1;
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
		TextValue other = (TextValue) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public String toString() {
		return getValue().toString();
	}

}
