package de.d3web.kernel.domainModel;

/**
 * This is thrown, when an unaccepable "Attribute"-value is tried to be set.
 * e.g.: Set a String value for a NumberAttribute Creation date: (24.03.00
 * 15:31:26)
 * 
 * @author joba
 */
public class ValueNotAcceptedException extends Exception {

	public ValueNotAcceptedException() {
		super("");
	}

	/**
	 * creates a new ValueNotAcceptedException.
	 * 
	 * @param bad
	 *            the type that has wrongly been set
	 * @param good
	 *            the type that should have been set.
	 */
	public ValueNotAcceptedException(Class bad, Class good) {
		super(bad + " is not " + good);
	}

	public ValueNotAcceptedException(String message) {
		super(message);
	}

	/**
	 * Gives a debugging message. Creation date: (24.03.00 15:46:29)
	 */
	public String toString() {
		return this.getClass().toString() + ": " + getMessage();
	}
}
