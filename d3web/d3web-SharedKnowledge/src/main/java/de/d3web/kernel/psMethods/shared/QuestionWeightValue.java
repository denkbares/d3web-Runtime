package de.d3web.kernel.psMethods.shared;

import de.d3web.kernel.domainModel.qasets.Question;

/**
 * Creation date: (18.10.2001 18:43:28)
 * @author: Norman Brümmer
 */
public class QuestionWeightValue implements java.io.Serializable {
	private de.d3web.kernel.domainModel.qasets.Question question = null;

	private int value = 0;

	/**
	 * QuestionWeight constructor comment.
	 */
	public QuestionWeightValue() {
		super();
	}

	/**
	 * @return a new instance with copied values of the object.
	 */
	public QuestionWeightValue copy() {
		QuestionWeightValue qwv = new QuestionWeightValue();
		qwv.setQuestion(getQuestion());
		qwv.setValue(getValue());
		return qwv;
	}
	
	public String toString() {
		return getQuestion() + ":" + getValue();
	}
	
	/**
	 * @return de.d3web.kernel.domainModel.Question
	 */
	public Question getQuestion() {
		return question;
	}

	/**
	 * @return int
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param newQuestion de.d3web.kernel.domainModel.Question
	 */
	public void setQuestion(Question newQuestion) {
		question = newQuestion;
	}

	/**
	 * Creation date: (18.10.2001 18:44:28)
	 * 
	 * @param newValue int
	 */
	public void setValue(int newValue) {
		value = newValue;
	}
}