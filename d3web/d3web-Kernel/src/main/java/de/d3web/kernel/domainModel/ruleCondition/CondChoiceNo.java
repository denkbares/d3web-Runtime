package de.d3web.kernel.domainModel.ruleCondition;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.qasets.QuestionYN;

/**
 * This condition checks, if a YES/NO question has the NO value.
 * The composite pattern is used for this. This class is a "leaf".
 * 
 * @author joba
 */
@Deprecated
public class CondChoiceNo extends CondEqual {

	/**
	 * Creates a new equal-condition. 
	 * @param quest the question to check
	 */
	public CondChoiceNo(QuestionYN question) {
		super(question, question.no);
	}

	/**
	 * Checks if the question has the value(s) specified in the constructor.
	 */
	public boolean eval(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException {
		checkAnswer(theCase);
		return ((AnswerChoice) question.getValue(theCase).get(0)).isAnswerNo();
	}

	/**
	 * Verbalizes the condition.
	 */
	public String toString() {
		return "<condition operator=\"choiceNo\" id=\""
			+ question.getId()
			+ "\"/>\n";
	}


	public AbstractCondition copy() {
		return new CondChoiceNo((QuestionYN)getQuestion());
	}
	
	
}
