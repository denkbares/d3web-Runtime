package de.d3web.kernel.dynamicObjects;

import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.qasets.QuestionText;

/**
 * Stores the dynamic, user specific values for an QuestionText
 * object. It corresponds to the static QuestionText object.<br>
 * Values to be stored:<br>
 * <li> Current string value corresponding to a given user case.
 * @author joba
 * @see QuestionText
 */
public class CaseQuestionText extends CaseQuestion {
	private Answer value = null;

	public CaseQuestionText(QuestionText question) {
		super(question);
	}

	public Answer getValue() {
		return value;
	}

	public void setValue(Answer value) {
		this.value = value;
	}
}