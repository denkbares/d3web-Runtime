package de.d3web.kernel.domainModel.ruleCondition;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerUnknown;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
/**
 * This condition checks, if a question has an specified value (or values).
 * The composite pattern is used for this. This class is a "leaf".
 * 
 * @author Christian Betz
 */
public class CondEqual extends CondQuestion {
	private List values;

	/**
	 * Creates a new equal-condition. 
	 * @param _quest the question to check
	 * @param _val the value the question needs to have to fulfill the condition
	 */
	public CondEqual(Question question, AnswerUnknown value) {
		super(question);
		values = new Vector();
		values.add(value);
	}

	/**
	 * Creates a new equal-condition. 
	 * @param question the question to check
	 * @param values the value/values the question needs to have to fulfill the condition
	 */
	public CondEqual(Question question, List values) {
		super(question);
		this.values = values;
	}

	/**
	 * Creates a new equal-condition. more generic.
	 * @param question the question to check
	 * @param value the value the question needs to have to fulfill the condition
	 */
	public CondEqual(QuestionChoice question, Answer value) {
		super(question);
		values = new Vector();
		values.add(value);
	}
	
	/**
	 * Creates a new equal-condition. 
	 * @param question the question to check
	 * @param value the value the question needs to have to fulfill the condition
	 */
	public CondEqual(Question question, Answer value) {
		super(question);
		values = new Vector();
		values.add(value);
	}
	
	/**
	 * Creates a new equal-condition.<br/>
	 * THIS METHOD ONLY EXISTS TO SOLVE THE AMBIGUITY<br/>
	 * @param _quest the question to check
	 * @param _val the value the question needs to have to fulfill the condition
	 */
	public CondEqual(QuestionChoice question, AnswerUnknown value) {
		super(question);
		values = new Vector();
		values.add(value);
	}

	/**
	 * Checks if the question has the value(s) specified in the constructor.
	 */
	public boolean eval(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException {
		checkAnswer(theCase);
		return (question.getValue(theCase).containsAll(values));
	}

	private String getId(Object answer) {
		if (answer instanceof AnswerChoice)
			return ((AnswerChoice) answer).getId();
		else if (answer instanceof AnswerUnknown)
			return ((AnswerUnknown) answer).getId();
		else {
			Logger.getLogger(this.getClass().getName()).warning(
				"Could not convert "
					+ answer
					+ ". Check "
					+ this.getClass()
					+ ".getId(Object)!");
			return answer.toString();
		}
	}

	public List getValues() {
		return values;
	}

	public void setValues(List newValues) {
		values = newValues;
	}

	/**
	 * Verbalizes the condition.
	 */
	public String toString() {
		String ret =
			"<Condition type='equal' ID='" + question.getId() + "' value='";

		Iterator iter = values.iterator();

		if (iter.hasNext()) {
			ret += getId(iter.next());
		}

		while (iter.hasNext()) {
			ret += "," + getId(iter.next());
		}

		ret += "'></Condition>\n";

		return ret;
	}
	
	
	public boolean equals(Object other) {
		if (!super.equals(other)) return false;
		
		if (this.getValues() != null && ((CondEqual)other).getValues() != null)
			return this.getValues().containsAll(((CondEqual)other).getValues()) && ((CondEqual)other).getValues().containsAll((this).getValues());
		else return this.getValues() == ((CondEqual)other).getValues();
	}

	public AbstractCondition copy() {
		return new CondEqual(getQuestion(), getValues());
	}
	
	public int hashCode() {
		int hash = super.hashCode()*31;
		for (Object o : this.getValues()) {
			// hash code ignores order of answers (according to equals)
			hash += o.hashCode();
		}
		return hash;
	}

}