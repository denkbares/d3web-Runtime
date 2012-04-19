/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.core.inference.condition;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.UndefinedValue;

/**
 * This condition checks, whether a specified value is assigned to a question
 * The composite pattern is used for this. This class is a "leaf".
 * 
 * @author joba
 */
public class CondEqual extends CondQuestion {

	private final Value conditionValue;

	/**
	 * Creates a new equal-condition constraining a specified question to a
	 * specified value.
	 * 
	 * @param question the question to check
	 * @param value the value the question needs to be assigned to
	 * @throws IllegalArgumentException if the Value is instance of
	 *         {@link UndefinedValue}
	 */
	public CondEqual(Question question, Value value) throws IllegalArgumentException {
		super(question);
		if (value instanceof UndefinedValue) {
			throw new IllegalArgumentException("Value must not be UndefinedValue.");
		}
		this.conditionValue = value;
	}

	@Override
	public boolean eval(Session session)
			throws NoAnswerException, UnknownAnswerException {
		Value value = checkAnswer(session);
		if (getQuestion() instanceof QuestionMC) {
			if (value instanceof MultipleChoiceValue) {
				MultipleChoiceValue currentValue = (MultipleChoiceValue) value;
				return currentValue.contains(this.conditionValue);
			}
		}
		return this.conditionValue.equals(value);
	}

	/**
	 * Returns the values that have to be assigned to the question to fulfill
	 * the condition.
	 * 
	 * @return the constrained values of this condition
	 */
	public Value getValue() {
		return conditionValue;
	}

	@Override
	public String toString() {
		String ret = getQuestion().getName() +
				" == " + this.conditionValue;
		return ret;
	}

	@Override
	public boolean equals(Object other) {
		if (!super.equals(other)) return false;

		if (this.getValue() != null && ((CondEqual) other).getValue() != null) {
			return this.getValue().equals(((CondEqual) other).getValue())
					&& ((CondEqual) other).getValue().equals((this).getValue());
		}
		else {
			return this.getValue() == ((CondEqual) other).getValue();
		}
	}

	@Override
	public int hashCode() {
		return super.hashCode() * 31 + getValue().hashCode();
	}
}