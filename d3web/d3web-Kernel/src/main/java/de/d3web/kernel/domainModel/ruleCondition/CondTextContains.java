package de.d3web.kernel.domainModel.ruleCondition;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.answers.AnswerText;
import de.d3web.kernel.domainModel.qasets.QuestionText;
/**
 * Condition for text questions, where a specified value
 * has to be contained in the answer of QuestionText.
 * The composite pattern is used for this. This class is a "leaf".
 * 
 * @author joba
 */
public class CondTextContains extends CondQuestion {
	private String value;

	/**
	 * Creates a new condtion, where a specified value needs to
	 * be contained in text question.
	 * @param question the specified text question
	 * @param value the specified value (String)
	 */
	public CondTextContains(QuestionText question, String value) {
		super(question);
		this.value = value;
	}

	/**
	  * @return true, iff the question contains a specified value.
	  */
	public boolean eval(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException {

		checkAnswer(theCase);

		AnswerText answer = (AnswerText) question.getValue(theCase).get(0);
		String value = (String) answer.getValue(theCase);
		if (value != null) {
			return (value.indexOf(this.value) > -1);
		} else {
			return false;
		}
	}

	public java.lang.String getValue() {
		return value;
	}

	public void setValue(String newValue) {
		value = newValue;
	}

	

	/**
	 * Verbalizes the condition.
	 */
	public String toString() {

		return "<Condition type='textContains' ID='"
			+ question.getId()
			+ "' value='"
			+ value
			+ "'>"
			+ "</Condition>\n";
	}

	public boolean equals(Object other) {
		if (!super.equals(other)) return false;
		
		if (this.getValue() != null && ((CondTextContains)other).getValue() != null)
					return this.getValue().equals(((CondTextContains)other).getValue());
				else return this.getValue() == ((CondTextContains)other).getValue();	
	}
	
	public AbstractCondition copy() {
		return new CondTextContains((QuestionText)getQuestion(), getValue());
	}
	

}
