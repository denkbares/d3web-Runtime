/*
 * Created on 09.10.2003
 */
package de.d3web.kernel.dynamicObjects;

import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionDate;

/**
 * Stores the dynamic, user specific values for an QuestionDate
 * object. It corresponds to the static QuestionDate object.<br>
 * Values to be stored:<br>
 * <li> Current date value corresponding to a given user case.
 * @author Tobias vogele
 * @see QuestionDate 
 */
public class CaseQuestionDate extends CaseQuestion {

	private Answer value = null;

	public CaseQuestionDate(Question question) {
		super(question);
	}

	public Answer getValue() {
		return value;
	}

	public void setValue(Answer value) {
		this.value = value;
	}
}
