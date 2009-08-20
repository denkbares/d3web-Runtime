/*
 * Created on 04.07.2003
 */
package de.d3web.kernel.dynamicObjects;

import de.d3web.kernel.domainModel.qasets.QuestionChoice;

/**
 * Stores the dynamic, user specific values for an QuestionChoice
 * object. It corresponds to the static QuestionChoice object.<br>
 * Values to be stored:<br>
 * <li> Current value corresponding to a given user case.
 * @author Tobias Vogele
 * @see QuestionChoice
 */
public class CaseQuestionChoice extends CaseQuestion {

	private Double numSchemaValue = null;

	public CaseQuestionChoice(QuestionChoice question) {
		super(question);
	}

	/**
	 * @return the current numerical value of the question 
	 * according to a give XPSCase. This value is used to
	 * be processed by a Num2ChoiceSchema.
	 */
	public Double getNumericalSchemaValue() {
		if (numSchemaValue == null) {
			numSchemaValue = new Double(0.0);
		}
		return numSchemaValue;
	}


	public void setNumericalSchemaValue(Double newValue) {
		numSchemaValue = newValue;
	}
}
