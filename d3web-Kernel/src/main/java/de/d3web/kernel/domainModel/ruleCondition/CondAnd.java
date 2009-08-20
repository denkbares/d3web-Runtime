package de.d3web.kernel.domainModel.ruleCondition;
import java.util.Iterator;
import java.util.List;

import de.d3web.kernel.XPSCase;
/**
 * Implements an "and"-condition, where all sub-conditions have to be true.
 * The composite pattern is used for this. This class is a "composite".
 * 
 * @author Michael Wolber, joba
 */
public class CondAnd extends NonTerminalCondition {

	/**
	 * Creates a new AND-condition with a list of sub-conditions.
	 */
	public CondAnd(List terms) {
		super(terms);
	}

	/**
	  * AND: Returns true, iff all sub-conditions are true.
	  */
	public boolean eval(XPSCase theCase) throws NoAnswerException, UnknownAnswerException {
		boolean wasNoAnswer = false;
		boolean wasUnknownAnswer = false;

		Iterator iter = terms.iterator();
		while (iter.hasNext()) {
			try {
				if (!((AbstractCondition) iter.next()).eval(theCase)) {
					return false;
				}
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
		return true;
	}

	/**
	 * Verbalizes the condition.
	 */
	public String toString() {
		String ret = "<Condition type='and'>\n";

		Iterator iter = terms.iterator();
		while (iter.hasNext()) {
			AbstractCondition ac = (AbstractCondition) iter.next();
			if (ac != null)
				ret += ac.toString();
			else {
			}
		}

		ret += "</Condition>\n";

		return ret;
	}

	protected AbstractCondition createInstance(List theTerms, AbstractCondition o) {
		return new CondAnd(theTerms);
	}
	
	
}