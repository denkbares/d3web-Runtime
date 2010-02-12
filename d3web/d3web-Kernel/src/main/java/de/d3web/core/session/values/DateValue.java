package de.d3web.core.session.values;

import java.util.Date;

import de.d3web.core.session.Value;

public class DateValue implements Value {

	private Date value;

	public DateValue(Date value) {
		this.value = value;
	}
	
	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public int compareTo(Value o) {
		if (o instanceof DateValue) {
			return value.compareTo(((DateValue)o).value);
		}
		else {
			return 0;
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
		DateValue other = (DateValue) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
