package de.d3web.kernel.supportknowledge.propertyCloner;

/**
 * A PropertyCloner can clone (make a deep copy of) objects that belong to a
 * specific class.
 * @author gbuscher
 */
public abstract class PropertyCloner {
	
	/**
	 * @param o object to clone
	 * @return the cloned object
	 */
	public abstract Object cloneProperty(Object o);

}
