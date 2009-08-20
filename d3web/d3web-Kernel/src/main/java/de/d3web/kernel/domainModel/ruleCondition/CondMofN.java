package de.d3web.kernel.domainModel.ruleCondition;
import java.util.Iterator;
import java.util.List;

import de.d3web.kernel.XPSCase;
/**
 * Implements an "m out of n"-condition, where m sub-conditions of all
 * n conditions have to be true.
 * The composite pattern is used for this. This class is a "composite".
 * 
 * @author joba
 */
public class CondMofN extends NonTerminalCondition {

	private int min;
	private int max;

	/**
	 * Creates a new M-out-of-N-condition with a list of sub-conditions.
	 * set min and max borders via setMin() and setMax().
	 * evaluates with min <= numberOfTrueConditions <= max
	 */
	public CondMofN(List terms, int min, int max) {
		super(terms);
		setMin(min);
		setMax(max);
	}

	/**
	  * checks this M-out-of-N condition.
	  * @return true, iff m sub-conditions of all n conditions are true.
	  */
	public boolean eval(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException {
		int trueTillNow = 0;
		Iterator iter = terms.iterator();

		boolean wasNoAnswer = false;

		while (iter.hasNext()) {

			try {
				if (((AbstractCondition) iter.next()).eval(theCase)) {
					trueTillNow++;
					if (trueTillNow > getMax())
						return false;
				}

			} catch (NoAnswerException nax) {
				wasNoAnswer = true;
			} catch (UnknownAnswerException uax) {

			}

		}

		if (trueTillNow >= getMin())
			return true;

		else if (wasNoAnswer) {
			throw NoAnswerException.getInstance();
		} else
			return false;

	}

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public void setMin(int min) {
		this.min = min;
	}

	/**
	 * Verbalizes the condition.
	 */
	public String toString() {

		Iterator iter = terms.iterator();
		String ret =
			"<Condition type='MofN' min='"
				+ getMin()
				+ "' max='"
				+ getMax()
				+ "' size='"
				+ terms.size()
				+ "'>\n";

		while (iter.hasNext()) {
			ret += ((AbstractCondition) iter.next()).toString();
		}

		return ret + "</Condition>\n";
	}
	
	public boolean equals(Object other){
		if (!super.equals(other))
			return false;
		
		//noch min und max vergleichen
		return (this.getMin() == ((CondMofN)other).getMin()) && (this.getMax() == ((CondMofN)other).getMax());
	}

	protected AbstractCondition createInstance(List theTerms, AbstractCondition o) {
		int min = ((CondMofN)o).getMin();
		int max = ((CondMofN)o).getMax();
		return new CondMofN(theTerms, min, max);
	}
}