package de.d3web.kernel.domainModel.ruleCondition;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.qasets.QuestionNum;

/**
 * Condition for numerical questions, where the value
 * has to be equal to a given value (Double value).
 * The composite pattern is used for this. This class is a "leaf".
 * 
 * @author Michael Wolber, joba
 */
public class CondNumEqual extends CondNum {

	/**
	 * Creates a new condtion, where a the specified numerical question needs to
	 * be equal to the specified value.
	 * @param quest the specified numerical question
	 * @param val the specified value (Double)
	 */
	public CondNumEqual(QuestionNum question, Double value) {
		super(question, value);
	}

	/**
	  * @return true, if the question has the given numerical value.
	  * We do only compare the first n decimals after the comma.
	  * n is given by EPSILON (EPSILON is a public constant)
	  */
	public boolean eval(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException {

		checkAnswer(theCase);

		AnswerNum answer = (AnswerNum) getQuestion().getValue(theCase).get(0);
		Double value = (Double) answer.getValue(theCase);
		if (value != null) {
			return (
				Math.abs(value.doubleValue() - getAnswerValue().doubleValue())
					<= CondNum.EPSILON);
		} else {
			return false;
		}
	}

	/**
	 * Verbalizes the condition.
	 */
	public String toString() {

		return "<Condition type='numEqual' ID='"
			+ getQuestion().getId()
			+ "' value='"
			+ getAnswerValue()
			+ "'>"
			+ "</Condition>\n";
	}

	public AbstractCondition copy() {
		return new CondNumEqual((QuestionNum)getQuestion(),  getAnswerValue());
	}

}