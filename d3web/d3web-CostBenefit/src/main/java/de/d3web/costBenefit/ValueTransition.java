package de.d3web.costBenefit;
import java.util.List;

import de.d3web.core.terminology.Question;

/**
 * A ValueTransition contains a question and a List of ConditionalValueSetters,
 * which are sorted by priority.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 *
 */
public class ValueTransition {
	
	private Question question;
	private List<ConditionalValueSetter> setters;
	
	public ValueTransition(Question question,
			List<ConditionalValueSetter> setters) {
		super();
		this.question = question;
		this.setters = setters;
	}


	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}
	
	public List<ConditionalValueSetter> getSetters() {
		return setters;
	}

	public void setSetters(List<ConditionalValueSetter> setters) {
		this.setters = setters;
	}
	
	

}
