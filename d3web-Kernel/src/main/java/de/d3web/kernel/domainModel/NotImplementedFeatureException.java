package de.d3web.kernel.domainModel;

/**
 * Exception thrown if not implemented methods are called
 * @author Michael Scharvogel
 */
public class NotImplementedFeatureException extends Exception {

	public NotImplementedFeatureException() {
		super();
	}

	public NotImplementedFeatureException(String s) {
		super(s);
	}

	/**
	 * @return String that represents the value of this object.
	 */
	public String toString() {
		return "Exeption is thrown, as a method was called that was not yet Implemented";
	}
}