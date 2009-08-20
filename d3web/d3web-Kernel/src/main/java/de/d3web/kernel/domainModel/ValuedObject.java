package de.d3web.kernel.domainModel;
import de.d3web.kernel.XPSCase;

/**
 * A ValuedObject is a knowledge base object that can have value
 * @author Joachim Baumeister
 */
public interface ValuedObject extends IDReference {

	/**
	 * sets the value for this valued object for the current case
	 */
	void setValue(XPSCase theCase, Object[] values);
}