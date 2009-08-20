package de.d3web.kernel.domainModel.ruleCondition;

import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.qasets.Question;
/**
 * Superclass for all conditions depending on Questions
 * Creation date: (23.11.2000 15:59:09)
 * @author Norman Brümmer
 */
public abstract class CondQuestion extends TerminalCondition {
	protected Question question = null;

	/**
	 * Creates a new CondQuestion object
	 */
	protected CondQuestion(de.d3web.kernel.domainModel.IDObject idobject) {
		super(idobject);
		question = (Question) idobject;
	}

	/**
	 * Checks the answer of the stored Question
	 * @throws NoAnswerException, if Question has no answer
	 * @throws UnknownAnswerException if Question is unknown
	 */
	protected void checkAnswer(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException {
		List values = question.getValue(theCase);
		if ((values == null)
			|| (values.size() == 0)
			|| (values.get(0) == null)) {
			throw NoAnswerException.getInstance();
		} else {
			if (values.get(0).equals(question.getUnknownAlternative())) {
				throw UnknownAnswerException.getInstance();
			}
		}
	}

	public de.d3web.kernel.domainModel.qasets.Question getQuestion() {
		return question;
	}

	protected void setQuestion(Question question) {
		this.question = question;
	}
	
	
	public boolean equals(Object other) {
		if (!super.equals(other))
			return false;
			
		if (this.getQuestion() != null)
			return this.getQuestion().equals(((CondQuestion)other).getQuestion());
		else return this.getQuestion() == ((CondQuestion)other).getQuestion();
	}

	public int hashCode() {
		
		//alle IDs zusammens
		if (getQuestion() != null && getQuestion().getId()!= null)
			return (getQuestion().getId()).hashCode();
		else return toString().hashCode();
		
	}

	

}
