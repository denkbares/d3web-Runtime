package de.d3web.kernel.domainModel.ruleCondition;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.NamedObject;

/**
 * Abstract superclass for all conditions which are available in rules.
 * Any condition needs to have an eval method to evaluate itself.
 * The composite pattern is used for this. This class is the "component".
 * 
 * @author Joachim Baumeister, Christian Betz
 */
public abstract class AbstractCondition implements java.io.Serializable {

	public abstract boolean eval(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException;

	public abstract List<? extends NamedObject> getTerminalObjects();

	/**
	 * Verbalizes the condition.
	 */
	public String toString() {
		return "[" + getTerminalObjects() + "]";
	}
	
	
	public abstract boolean equals(Object obj);

	public abstract int hashCode();
	
	/**
	 * Create a deep copy of the instance.
	 * (Prototype pattern)
	 */
	public abstract AbstractCondition copy();

}