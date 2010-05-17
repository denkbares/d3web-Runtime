package de.d3web.core.session;

import de.d3web.core.knowledge.terminology.Question;

public class DefaultProtocolEntry implements ProtocolEntry {

	private Question question;
	private Value    value;
	
	public DefaultProtocolEntry(Question question, Value value) {
		this.question = question;
		this.value    = value;
	}
	
	@Override
	public Question getQuestion() {
		return this.question;
	}

	@Override
	public Value getValue() {
		return this.value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((question == null) ? 0 : question.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultProtocolEntry other = (DefaultProtocolEntry) obj;
		if (question == null) {
			if (other.question != null)
				return false;
		} else if (!question.equals(other.question))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
