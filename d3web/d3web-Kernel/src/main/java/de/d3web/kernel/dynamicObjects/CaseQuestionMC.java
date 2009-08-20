package de.d3web.kernel.dynamicObjects;

import java.util.LinkedList;
import java.util.List;

import de.d3web.kernel.domainModel.qasets.QuestionMC;
/**
 * Stores the dynamic, user specific values for an QuestionMC
 * object. It corresponds to the static QuestionMC object.<br>
 * Values to be stored:<br>
 * <li> Current values corresponding to a given user case.
 * @author Christian Betz, joba
 * @see QuestionMC
 */
public class CaseQuestionMC extends CaseQuestionChoice {
	private List value = new LinkedList();

	public CaseQuestionMC(QuestionMC question) {
		super(question);
	}

	/**
	 * @return the user-specific value of the depending questionMC
	 */ 
	public List getValue() {
		return value;
	}

	/**
	 * Sets the user-specific value of the depending questionMC
	 */ 
	public void setValue(List value) {
		this.value = value;
	}

}