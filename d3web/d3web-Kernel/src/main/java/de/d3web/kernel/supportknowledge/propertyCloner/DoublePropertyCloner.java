package de.d3web.kernel.supportknowledge.propertyCloner;

/**
 * PropertyCloner for java.lang.Double.
 * @see PropertyCloner
 * @author gbuscher
 */
public class DoublePropertyCloner extends PropertyCloner {

	public Object cloneProperty(Object o) {
		if (o instanceof Double) {
			return new Double(((Double) o).doubleValue());
		}
		return null;
	}

}
