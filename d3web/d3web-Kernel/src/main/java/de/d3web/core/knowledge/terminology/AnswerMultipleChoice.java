package de.d3web.core.knowledge.terminology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.d3web.core.session.XPSCase;
import de.d3web.core.session.values.AnswerChoice;

public class AnswerMultipleChoice extends Answer {

	private List<AnswerChoice> choices = new ArrayList<AnswerChoice>();
	

	public AnswerMultipleChoice(List<AnswerChoice> answers) {
		super(null);
		setChoices(answers);
	}

	public AnswerMultipleChoice(AnswerChoice[] answers) {
		super(null);
		for (AnswerChoice answerChoice : answers) {
			choices.add(answerChoice);
		}
	}
	

	// @Override
	// public String getId() {
	// return "";
	// }

	@Override
	public String getName() {
		StringBuffer b = new StringBuffer();
		for (Iterator<AnswerChoice> iterator = choices.iterator(); iterator.hasNext();) {
			AnswerChoice answer = (AnswerChoice) iterator.next();
			b.append(answer.getName());
			if (iterator.hasNext())
				b.append(", ");
		}
		return b.toString();
	}

	public List<AnswerChoice> getChoices() {
		return Collections.unmodifiableList(choices);
	}
	
	public void setChoices(List<AnswerChoice> answers) {
		choices = answers;
	}
	
	@Override
	public String getId() {
		Collections.sort(choices);
		String theId = "";

		for (int i = 0; i < choices.size(); i++) {
			if (i > 0) {
				theId += "_" + choices.get(i).getId();
			}
			else {
				theId += choices.get(i).getId();
			}
		}
		return theId;
	}
	
	@Override
	public Object getValue(XPSCase theCase) {
		return choices.toString();
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 17;
		result = prime * result + ((choices == null) ? 0 : choices.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		AnswerMultipleChoice other = (AnswerMultipleChoice) obj;
		if (choices == null) {
			if (other.choices != null)
				return false;
		} else if (!choices.equals(other.choices))
			return false;
		return true;
	}

	@Override
	public int compareTo(Answer o) {
		if (o == this) {
			return 0;
		}
		if (o instanceof AnswerMultipleChoice) {
			if (choices.equals(((AnswerMultipleChoice) o).choices))
				return 0;
			else
				return choices.size() - ((AnswerMultipleChoice)o).choices.size();
		}
		return -1;
	}
	
	public int numberOfChoices() {
		if (choices == null)
			return 0;
		else
			return choices.size();
	}
	
	@Override
	public String toString() {
		return choices.toString();
	}

	/**
	 * Checks whether the choices of the specified value are
	 * contained in the choices of this value.
	 * @param value the specified value
	 * @return true, when the specified values are included in this values
	 */
	public boolean contains(Answer value) {
		if (value instanceof AnswerMultipleChoice) {
			return choices.containsAll(((AnswerMultipleChoice)value).choices);
		} else if (value instanceof AnswerChoice) {
			return choices.contains((AnswerChoice)value);
		}
		return false;
	}

}
