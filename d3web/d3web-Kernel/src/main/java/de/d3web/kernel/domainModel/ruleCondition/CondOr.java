package de.d3web.kernel.domainModel.ruleCondition;

import java.util.Iterator;
import java.util.List;

import de.d3web.kernel.XPSCase;
/**
 * Implements an "OR"-condition, where at least 
 * one sub-condition has to be true.
 * The composite pattern is used for this. This class is a "composite".
 * 
 * @author Michael Wolber, joba
 */
public class CondOr extends NonTerminalCondition {

	/**
	 * Creates a new OR-condition with a list of sub-conditions.
	 */
	public CondOr(List terms) {
		super(terms);
	}

	/**
	 * @return true, if at least one sub-condition is true.
	 */
	public boolean eval(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException {

		boolean wasNoAnswer = false;
		boolean wasUnknownAnswer = false;

		Iterator iter = terms.iterator();
		while (iter.hasNext()) {
			try {
				if (((AbstractCondition) iter.next()).eval(theCase))
					return true;
			} catch (NoAnswerException nae) {
				wasNoAnswer = true;
			} catch (UnknownAnswerException uae) {
				wasUnknownAnswer = true;
			}
		}

		if (wasNoAnswer) {
			throw NoAnswerException.getInstance();
		}

		if (wasUnknownAnswer) {
			throw UnknownAnswerException.getInstance();
		}

		return false;
	}

	/**
	 * Verbalizes the condition.
	 */
	public String toString() {

		String ret = "<Condition type='or'>\n";
		Iterator iter = terms.iterator();
		while (iter.hasNext()) {
			ret += ((AbstractCondition) iter.next()).toString();
		}

		return ret + "</Condition>\n";

	}

	protected AbstractCondition createInstance(List theTerms, AbstractCondition o) {
		return new CondOr(theTerms);
	}
}
