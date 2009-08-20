package de.d3web.kernel.domainModel.ruleCondition;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.qasets.Question;

/**
 * Condition that checks if a Queation has the "unknown" value
 * Creation date: (23.11.2000 13:18:44)
 * @author Norman Brümmer
 */
public class CondUnknown extends CondQuestion {

	/**
	 * Creates a new CondUnknown object
	 */
	public CondUnknown(Question question) {
		super(question);
	}

	/**
	 * @return true iff question has "unknown" value
	 */
	public boolean eval(XPSCase theCase) throws NoAnswerException {
		try {
			checkAnswer(theCase);
			return false;
		} catch (UnknownAnswerException ex) {
			return ((Answer) question.getValue(theCase).get(0)).isUnknown();
		}
	}

	public String toString() {
		String questionID = "";
		if(question != null) {
			questionID = question.getId();
		}
		return "<Condition type='unknown' ID='"
			+ questionID
			+ "' value='unknown'>"
			+ "</Condition>\n";
	}

	public AbstractCondition copy() {
		return new CondUnknown(getQuestion());
	}	
	

}
