package de.d3web.kernel.dynamicObjects;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.qasets.Question;
/**
 * Stores the dynamic, user specific values for an QuestionNum
 * object. It corresponds to the static QuestionNum object.<br>
 * Values to be stored:<br>
 * <li> Current value corresponding to a given user case.
 * @author Christian Betz, joba
 * @see de.d3web.kernel.domainModel.QuestionNum
 */
public class CaseQuestionNum extends CaseQuestion {
	Answer value = null;

	/**
	 * Creates a new dynamic store for the given QuestionNum
	 */
	public CaseQuestionNum(Question question) {
		super(question);
	}

	public Answer getValue() {
		return value;
	}

	public void setValue(Answer value) {
		this.value = value;
	}

	public String toString() {
		return getValue().toString();
	}
}