package de.d3web.core.session;

/**
 * This instance represents the value that is assigned
 * to a question, when a respective answer is given by
 * a user.
 * 
 * @author joba
 */
public interface Value extends Comparable<Value> {
	Object getValue();
}
