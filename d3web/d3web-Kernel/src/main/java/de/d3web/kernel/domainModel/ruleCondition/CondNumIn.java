package de.d3web.kernel.domainModel.ruleCondition;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.qasets.QuestionNum;

/**
 * Condition for numerical questions, where the question
 * needs to have a value in the specified range. (Double value).
 * The composite pattern is used for this. This class is a "leaf".
 * 
 * @author joba
 */
public class CondNumIn extends CondNum {

	private NumericalInterval _interval;

	/**
	 * Creates a new condtion, where a the specified numerical question needs to
	 * be equal to the specified value.
	 * @param quest the specified numerical question
	 * @param val the specified value (Double)
	 */
	public CondNumIn(QuestionNum question, Double minValue, Double maxValue) {
		this(question, new NumericalInterval(minValue.doubleValue(), maxValue.doubleValue(), false, false));
	}

	public CondNumIn(QuestionNum question, NumericalInterval theInterval) {
		super(question, null);
		_interval = theInterval;
	}

	/**
	  * We do only compare the first n decimals after the comma.
	  * n is given by EPSILON (EPSILON is a public constant)
	  * @return true, if the question has the given numerical value.
	  */
	public boolean eval(XPSCase theCase) throws NoAnswerException, UnknownAnswerException {

		checkAnswer(theCase);

		AnswerNum answer = (AnswerNum) getQuestion().getValue(theCase).get(0);
		Double value = (Double) answer.getValue(theCase);
		if (value != null) {
			if (_interval != null) {
				return _interval.contains(value.doubleValue());
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public java.lang.Double getMaxValue() {
		return new Double(_interval.getRight());
	}

	public java.lang.Double getMinValue() {
		return new Double(_interval.getLeft());
	}

	/**
	 * Verbalizes the condition.
	 */
	public String toString() {

		return "<Condition type='numIn' ID='"
			+ getQuestion().getId()
			+ "' minValue='"
			+ getMinValue()
			+ "' maxValue='"
			+ getMaxValue()
			+ "'>"
			+ "</Condition>\n";
	}

	public boolean equals(Object other) {
		if (!super.equals(other))
			return false;

		if (this.getInterval() != null)
			return this.getInterval().equals(((CondNumIn)other).getInterval());
		else
			return (((CondNumIn)other).getInterval() == null);
	}

	/**
	 * @return NumericalInterval
	 */
	public NumericalInterval getInterval() {
		return _interval;
	}
	
	public void setInterval(NumericalInterval inter) {
		_interval = inter;
	}
	
	public String getValue(){
		StringBuffer out = new StringBuffer();
		out.append(getInterval().isLeftOpen() ? "(" : "[");
		out.append(getInterval().getLeft()+", "+getInterval().getRight());
		out.append(getInterval().isRightOpen() ? ")" : "]");
		return out.toString();	
	}

	public AbstractCondition copy() {
		return new CondNumIn((QuestionNum)getQuestion(),  getInterval());
	}
	
}