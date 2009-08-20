package de.d3web.kernel.domainModel.ruleCondition;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.kernel.XPSCase;

/**
 * This condtion creates a negation of an other condition.
 * The composite pattern is used for this. This class is a "leaf".
 * 
 * @author Michael Wolber, joba
 */
public class CondNot extends NonTerminalCondition {
	private AbstractCondition condition;

	/**
	 * Creates a new condition where the specified condition
	 * must not be true to fulfill this one.
	 * @param condition the specified condition, which should not be true
	 */
	public CondNot(AbstractCondition condition) {
		super(
			de.d3web.kernel.utilities.Utils.createVector(
				new Object[] { condition }));
		this.condition = condition;
	}

	/**
	 * @return true, iff the condition specified in the constructor is not true.
	 */
	public boolean eval(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException {
		return (!condition.eval(theCase));
	}

	/**
	 * Verbalizes the condition.
	 */
	public String toString() {
		String ret =
			"<condition operator=\"not\">\n  "
				+ condition.toString()
				+ "</condition>\n";
		return ret;
	}

	protected AbstractCondition createInstance(List theTerms, AbstractCondition o) {
		if (theTerms.size() == 1) 
			return new CondNot((AbstractCondition)(theTerms.get(0)));
		else {
			Logger.getLogger(CondNot.class.getName()).severe("Tried to" +
					"create a CondNont instance with more/less than 1 argument.");			
			return null;
		}
	}
}