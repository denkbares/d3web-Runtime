
package de.d3web.kernel.domainModel.ruleCondition;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.qasets.QuestionNum;

/**
 * The specified numerical question needs to
 * be less or equal than the specified value.
 * @author baumeister
 */
public class CondNumLessEqual extends CondNum {
	/**
	 * The specified numerical question needs to
	 * be less or equal than the specified value.
	 */
	public CondNumLessEqual(QuestionNum question, Double value) {
		super(question, value);
	}

	public boolean eval(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException {
		checkAnswer(theCase);

		AnswerNum answer = (AnswerNum) getQuestion().getValue(theCase).get(0);
		Double value = (Double) answer.getValue(theCase);
		if (value != null) {
			return (value.doubleValue() <= getAnswerValue().doubleValue());
		} else {
			return false;
		}
	}

	public String toString() {

		return "<Condition type='numLessEqual' ID='"
			+ getQuestion().getId()
			+ "' value='"
			+ getAnswerValue()
			+ "'>"
			+ "</Condition>\n";
	}

	public AbstractCondition copy() {
		return new CondNumLessEqual((QuestionNum)getQuestion(),  getAnswerValue());
	}
	
}
