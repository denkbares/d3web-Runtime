package de.d3web.core.session.values;

import de.d3web.core.session.Value;

/**
 * Represents a numerical value (internally stored as a {@link Double}).  
 * @author joba
 *
 */
public class NumValue implements Value {

	Double value;  
	
	public NumValue(double value) {
		this.value = Double.valueOf(value);
	}
	
	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public int compareTo(Value o) {
		if (o instanceof NumValue) {
			return value.compareTo(((NumValue) o).value);
		}
		return -1;
	}

}
