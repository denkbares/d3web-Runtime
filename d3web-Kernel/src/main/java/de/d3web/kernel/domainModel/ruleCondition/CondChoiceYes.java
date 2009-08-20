package de.d3web.kernel.domainModel.ruleCondition;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.qasets.QuestionYN;
/**
 * This condition checks, if a YES/NO question has the YES value.
 * The composite pattern is used for this. This class is a "leaf".
 * 
 * @author joba
 */
@Deprecated
public class CondChoiceYes extends CondEqual {


	/**
	 * Creates a new equal-condition. 
	 * @param quest the question to check
	 */
	public CondChoiceYes(QuestionYN question) {
		super(question, question.yes);
	}

	/**
	 * Checks if the question has the value(s) specified in the constructor.
	 */
	public boolean eval(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException {
		checkAnswer(theCase);
		return ((AnswerChoice) question.getValue(theCase).get(0)).isAnswerYes();
	}

	/**
	 * Verbalizes the condition.
	 */
	public String toString() {
		return "<Condition type='choiceYes' ID='" + question.getId() + "'/>\n";
	}
	
	public AbstractCondition copy() {
		return new CondChoiceYes((QuestionYN)getQuestion());
	}
}