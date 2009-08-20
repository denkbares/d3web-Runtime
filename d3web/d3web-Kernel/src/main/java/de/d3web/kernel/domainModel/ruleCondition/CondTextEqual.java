package de.d3web.kernel.domainModel.ruleCondition;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.answers.AnswerText;
import de.d3web.kernel.domainModel.qasets.QuestionText;
/**
 * Condition for text questions, where the value
 * has to be equal to a given value (String value).
 * The composite pattern is used for this. This class is a "leaf".
 * 
 * @author joba
 */
public class CondTextEqual extends CondQuestion {
	private String value;

	/**
	 * Creates a new condtion, where the specified text question needs to
	 * be equal to the specified value.
	 * @param question the specified text question
	 * @param value the specified value (String)
	 */
	public CondTextEqual(QuestionText question, String value) {
		super(question);
		this.value = value;
	}

	/**
	  * @return true, iff the question is equal to a specified value.
	  */
	public boolean eval(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException {
		checkAnswer(theCase);
		AnswerText answer = (AnswerText) question.getValue(theCase).get(0);
		String value = (String) answer.getValue(theCase);
		if (value != null) {
			return (value.equals(this.value));
		} else {
			return false;
		}
	}

	public java.lang.String getValue() {
		return value;
	}

	public void setValue(java.lang.String newValue) {
		value = newValue;
	}

	/**
	 * Verbalizes the condition.
	 */
	public String toString() {

		return "<Condition type='textEqual' ID='"
			+ question.getId()
			+ "' value='"
			+ value
			+ "'"
			+ "</Condition>\n";
	}
	
	public boolean equals(Object other) {
		if (!super.equals(other)) return false;
	
		if (this.getValue() != null && ((CondTextEqual)other).getValue() != null)
			return this.getValue().equals(((CondTextEqual)other).getValue());
		else return this.getValue() == ((CondTextEqual)other).getValue();	
	}
	
	public AbstractCondition copy() {
		return new CondTextEqual((QuestionText)getQuestion(), getValue());
	}
	

}
