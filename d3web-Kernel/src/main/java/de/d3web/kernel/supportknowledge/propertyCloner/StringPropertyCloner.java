package de.d3web.kernel.supportknowledge.propertyCloner;

/**
 * PropertyCloner for java.lang.String.
 * @see PropertyCloner
 * @author gbuscher
 */
public class StringPropertyCloner extends PropertyCloner {

	public Object cloneProperty(Object o) {
		if (o instanceof String) {
			return new String(o.toString());
		}
		return null;
	}

}
