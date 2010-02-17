package de.d3web.costBenefit;
import de.d3web.core.inference.condition.AbstractCondition;
import de.d3web.core.terminology.Answer;

/**
 * This class contains a condition and an answer. If the condition is true and no condition
 * of a previous ConditionalValueSetter if the same ValueTransition was true, this answer
 * is set.
 * @author Markus Friedrich (denkbares GmbH)
 *
 */
public class ConditionalValueSetter {

	private Answer answer;
	private AbstractCondition condition;
	
	public ConditionalValueSetter(Answer answer, AbstractCondition condition) {
		super();
		this.answer = answer;
		this.condition = condition;
	}
	
	public Answer getAnswer() {
		return answer;
	}
	
	public void setAnswer(Answer answer) {
		this.answer = answer;
	}
	
	public AbstractCondition getCondition() {
		return condition;
	}
	
	public void setCondition(AbstractCondition condition) {
		this.condition = condition;
	}
	
	
	
}
