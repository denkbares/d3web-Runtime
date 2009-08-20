package de.d3web.kernel.supportknowledge.propertyCloner;

/**
 * PropertyCloner for java.lang.Integer.
 * @see PropertyCloner
 * @author gbuscher
 */
public class IntegerPropertyCloner extends PropertyCloner {

	public Object cloneProperty(Object o) {
		if (o instanceof Integer) {
			return new Integer(((Integer) o).intValue());
		}
		return null;
	}

}
