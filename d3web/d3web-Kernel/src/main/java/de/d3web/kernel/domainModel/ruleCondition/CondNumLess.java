package de.d3web.kernel.domainModel.ruleCondition;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
/**
 * Condition for numerical questions, where the value
 * has to be less than a given value (Double value).
 * The composite pattern is used for this. This class is a "leaf".
 * 
 * @author Michael Wolber, joba
 */
public class CondNumLess extends CondNum {

	/**
	 * Creates a new condtion, where a the specified numerical question needs to
	 * be less than the specified value.
	 * @param quest the specified numerical question
	 * @param val the specified value (Double)
	 */
	public CondNumLess(QuestionNum question, Double value) {
		super(question, value);

	}

	/**
	  * @return true, iff the value of the question is less 
	  * than the given numerical value.
	  */
	public boolean eval(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException {
		checkAnswer(theCase);

		AnswerNum answer = (AnswerNum) getQuestion().getValue(theCase).get(0);

		Double value = (Double) answer.getValue(theCase);

		if (value != null) {
			return (value.doubleValue() < getAnswerValue().doubleValue());
		} else {
			return false;
		}
	}

	/**
	 * Verbalizes the condition.
	 */
	public String toString() {
		return "<Condition type='numLess' ID='"
			+ getQuestion().getId()
			+ "' value='"
			+ getAnswerValue()
			+ "'>"
			+ "</Condition>\n";
	}
	
	public AbstractCondition copy() {
		return new CondNumLess((QuestionNum)getQuestion(),  getAnswerValue());
	}
	
}
