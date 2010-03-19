package de.d3web.core.session.values;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.terminology.AnswerMultipleChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.session.Value;
import de.d3web.core.session.ValueFactory;

/**
 * This class represents the container for multiple values
 * that can be given to a {@link QuestionMC}.
 * 
 * @author joba
 *
 */
public class MultipleChoiceValue implements Value {
	private List<ChoiceValue> values = new LinkedList<ChoiceValue>();
	
	public MultipleChoiceValue(List<ChoiceValue> values) {
		this.values = values;
	}

	public MultipleChoiceValue(AnswerMultipleChoice values) {
		this.values = new ArrayList<ChoiceValue>(values.getChoices().size());
		for (AnswerChoice choices : values.getChoices()) {
			this.values.add(new ChoiceValue(choices));
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MultipleChoiceValue other = (MultipleChoiceValue) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

	@Override
	public Object getValue() {
		return values;
	}

	@Override
	public int compareTo(Value o) {
		if (o == null) throw new NullPointerException();
		if (o instanceof MultipleChoiceValue) {
			return values.size() - ((MultipleChoiceValue)o).values.size();
		} else {
			return -1;
		}
	}

}
