package de.d3web.kernel.domainModel.ruleCondition;
import de.d3web.kernel.domainModel.qasets.QuestionNum;

/**
 * Abtract class for numerical conditions. It handles
 * administrative stuff, like setting question object
 * and number (AnswerNum) to compare with.
 * The children classes just insert the eval method
 * for the real comparison.
 * 
 * Creation date: (03.11.00 17:21:47)
 * @author Joachim Baumeister
 */
public abstract class CondNum extends CondQuestion {
	public final static double EPSILON = 0.000001;

	private Double answerValue;

	protected CondNum(QuestionNum question, Double value) {
		super(question);
		setQuestion(question);
		setAnswerValue(value);
	}

	public Double getAnswerValue() {
		return answerValue;
	}

	public void setAnswerValue(Double answerValue) {
		this.answerValue = answerValue;
	}

	private void setQuestion(QuestionNum question) {
		this.question = question;
	}
	
	public boolean equals(Object other) {
		if (this == other)
			return true;
		else if ((other == null) || (getClass() != other.getClass())) {
			return false;
		} else {
			CondNum otherCN = (CondNum) other;

			boolean test = true;
			
			if (this.getQuestion() != null)
				test = this.getQuestion().equals(otherCN.getQuestion()) && test;
			else
				test = (otherCN.getQuestion() == null) && test;
				
			if (this.getAnswerValue() != null)
				test = this.getAnswerValue().equals(otherCN.getAnswerValue()) && test;
			else
				test = (otherCN.getAnswerValue() == null) && test;
				
			if (this.getTerminalObjects() != null && otherCN != null)
				test = this.getTerminalObjects().containsAll(otherCN.getTerminalObjects())
				&& otherCN.getTerminalObjects().containsAll(this.getTerminalObjects())
				&& test;
			else test = test && (this.getTerminalObjects() == null) && (otherCN.getTerminalObjects() == null);
				
			return test;
		}
	}
	
	

	public int hashCode() {
		if (getQuestion() != null && getQuestion().getId()!= null)
			return (getQuestion().getId()).hashCode();
			
		else
			return (getClass().toString()+toString()).hashCode();
	}

}