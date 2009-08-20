package de.d3web.kernel.domainModel.ruleCondition;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.qasets.Question;

/**
 * This condition checks, if an IDObject (e.g. Question) has a value and is not
 * unknown The composite pattern is used for this. This class is a "leaf".
 * Creation date: (13.09.2000 14:07:14)
 * 
 * @author Norman Brümmer
 */
public class CondKnown extends CondQuestion {

	/**
	 * Creates a new CondKnown object for the given Question
	 */
	public CondKnown(Question question) {
		super(question);
	}

	/**
	 * checks the condition.
	 * 
	 * @return true, iff the question has an answer that is different from
	 *         "unknown"
	 */
	public boolean eval(XPSCase theCase) throws NoAnswerException {
		try {
			checkAnswer(theCase);
			return true;
		} catch (UnknownAnswerException ex) {
			// commented out 20041110 marty
			// this is not really necessary:
			//			Logger.getLogger(this.getClass().getName()).throwing(
			//				this.getClass().getName(), "eval", ex);
			return false;
		}
	}

	/**
	 * @return java.lang.String
	 */
	public String toString() {
		return "<Condition type='known' ID='" + question.getId()
				+ "' value='any'>" + "</Condition>\n";
	}

	public AbstractCondition copy() {
		return new CondKnown(getQuestion());
	}
}