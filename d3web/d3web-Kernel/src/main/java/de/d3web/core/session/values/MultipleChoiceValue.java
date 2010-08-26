/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.core.session.values;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.Value;

/**
 * This class represents the container for multiple values that can be given to
 * a {@link QuestionMC}.
 * 
 * @author joba (denkbares GmbH)
 * @created 07.04.2010
 */
public class MultipleChoiceValue implements QuestionValue {

	private final Collection<ChoiceValue> values;
	public static String ID_SEPARATOR = "#####";

	/**
	 * Constructs a new MultipleChoiceValue
	 * 
	 * @param values the List of Choices for which a new MultipleChoiceValue
	 *        should be instantiated
	 * @throws NullPointerException if a null object was passed in
	 */
	public MultipleChoiceValue(List<ChoiceValue> values) {
		if (values == null) throw new NullPointerException();
		this.values = new HashSet<ChoiceValue>(values);
	}

	public static MultipleChoiceValue fromChoices(List<Choice> choices) {
		ArrayList<ChoiceValue> values = new ArrayList<ChoiceValue>(choices.size());
		for (Choice choice : choices) {
			values.add(new ChoiceValue(choice));
		}
		return new MultipleChoiceValue(values);
	}

	public String getAnswerChoicesID() {
		String id = "";
		for (ChoiceValue choiceValue : this.values) {
			id += choiceValue.getAnswerChoiceID() + ID_SEPARATOR;
		}
		if (id.length() > ID_SEPARATOR.length()) {
			id = id.substring(0, id.length() - ID_SEPARATOR.length());
		}
		return id;
	}

	/**
	 * Checks, if all choices are included in the other
	 * {@link MultipleChoiceValue}.
	 * 
	 * @param other another {@link MultipleChoiceValue}
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
	public boolean contains(Value value) {
		return this.values.contains(value);
	}

	@Override
	public Object getValue() {
		return values;
	}

	@Override
	public int compareTo(Value o) {
		if (o == null) throw new NullPointerException();
		if (o instanceof MultipleChoiceValue) {
			return values.size() - ((MultipleChoiceValue) o).values.size();
		}
		else {
			return -1;
		}
	}

	public String getName() {
		StringBuffer b = new StringBuffer();
		for (Iterator<ChoiceValue> iterator = values.iterator(); iterator.hasNext();) {
			ChoiceValue answer = iterator.next();
			b.append(((Choice) answer.getValue()).getName());
			if (iterator.hasNext()) b.append(", ");
		}
		return b.toString();
	}

	@Override
	public String toString() {
		return values.toString();
	}

	public List<Choice> asChoiceList() {
		List<Choice> choices = new ArrayList<Choice>(values.size());
		for (ChoiceValue value : values) {
			choices.add((Choice) value.getValue());
		}
		return choices;
	}

	/**
	 * Standard Eclipse equals() method. Removed values == null check due to the
	 * null-check in the constructor.
	 * 
	 * @author Marc-Oliver Ochlast (denkbares GmbH)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		MultipleChoiceValue other = (MultipleChoiceValue) obj;
		if (!values.equals(other.values)) return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + values.hashCode();
		return result;
	}
}
