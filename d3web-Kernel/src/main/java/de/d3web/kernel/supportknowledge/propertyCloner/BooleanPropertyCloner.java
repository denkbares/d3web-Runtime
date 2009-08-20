package de.d3web.kernel.supportknowledge.propertyCloner;

/**
 * PropertyCloner for java.lang.Boolean.
 * @see PropertyCloner
 * @author gbuscher
 */
public class BooleanPropertyCloner extends PropertyCloner {

	public Object cloneProperty(Object o) {
		if (o instanceof Boolean) {
			return new Boolean(((Boolean) o).booleanValue());
		}
		return null;
	}

}
