/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.core.session.values;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.terminology.AnswerMultipleChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.session.Value;

/**
 * This class represents the container for multiple values that can be given to
 * a {@link QuestionMC}.
 * 
 * @author joba (denkbares GmbH)
 * @created 07.04.2010
 */
public class MultipleChoiceValue implements Value {
	private List<ChoiceValue> values = new LinkedList<ChoiceValue>();
	private String id = "";
	public static String ID_SEPARATOR = "#####";
	
	public MultipleChoiceValue(List<ChoiceValue> values) {
		this.values = values;
		if (this.values != null) {
			for (ChoiceValue choiceValue : this.values) {
				id += choiceValue.getAnswerChoiceID() + ID_SEPARATOR;
			}
			id = id.substring(0, id.length() - ID_SEPARATOR.length());
		}
	}

	public String getAnswerChoicesID() {
		return id;
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

	/**
	 * Checks, if all choices are included in the other
	 * {@link MultipleChoiceValue}.
	 * 
	 * @param other
	 *            another {@link MultipleChoiceValue}
	 * @return true, if all choice values are contained in the other value
	 * @author joba
	 * @date 08.04.2010
	 */
	public boolean containsAll(MultipleChoiceValue other) {
		return this.values.containsAll(other.values);
	}

	/**
	 * Checks, if the specified value is contained as choice in this
	 * {@link MultipleChoiceValue}.
	 * 
	 * @param value
	 * @return true, if the specified choice value is contained as choice in
	 *         this instance
	 * @author joba
	 * @date 08.04.2010
	 */
	public boolean contains(ChoiceValue value) {
		return this.values.contains(value);
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

	public String getName() {
		StringBuffer b = new StringBuffer();
		for (Iterator<ChoiceValue> iterator = values.iterator(); iterator.hasNext();) {
			ChoiceValue answer = (ChoiceValue) iterator.next();
			b.append(((AnswerChoice) answer.getValue()).getName());
			if (iterator.hasNext())
				b.append(", ");
		}
		return b.toString();
	}

	@Override
	public String toString() {
		return values.toString();
	}

	public List<AnswerChoice> asChoiceList() {
		List<AnswerChoice> choices = new ArrayList<AnswerChoice>(values.size());
		for (ChoiceValue value : values) {
			choices.add((AnswerChoice) value.getValue());
		}
		return choices;
	}

}
