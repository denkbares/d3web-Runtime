package de.d3web.kernel.dynamicObjects;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.qasets.QuestionOC;
/**
 * Stores the dynamic, user specific values for an QuestionOC
 * object. It corresponds to the static QuestionOC object.<br>
 * Values to be stored:<br>
 * <li> Current value corresponding to a given user case.
 * @author Christian Betz, joba
 * @see QuestionOC
 */
public class CaseQuestionOC extends CaseQuestionChoice {
	private Answer value = null;

	public CaseQuestionOC(QuestionOC question) {
		super(question);
	}

	public Answer getValue() {
		return value;
	}

	public void setValue(Answer value) {
		this.value = value;
	}
}