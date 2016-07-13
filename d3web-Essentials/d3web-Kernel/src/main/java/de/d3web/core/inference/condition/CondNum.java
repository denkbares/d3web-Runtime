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

import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.NumValue;

/**
 * Abstract class for conditions with numerical questions. This class handles
 * administrative stuff such as setting the question object and a number to compare with.
 * The children classes just insert the eval method for the real comparison.
 * <p>
 * Creation date: (03.11.00 17:21:47)
 *
 * @author Joachim Baumeister
 */
public abstract class CondNum extends CondQuestion {

	/**
	 * Level of impression for numerical comparison.
	 */
	public final static double EPSILON = 0.000001;

	private final Double conditionValue;

	/**
	 * Creates a new numerical condition having the specified value constraining
	 * to the specified question.
	 *
	 * @param question the specified question
	 * @param value    the specified value
	 */
	protected CondNum(QuestionNum question, Double value) {
		super(question);
		setQuestion(question);
		this.conditionValue = value;
	}

	/**
	 * Returns the numerical value constraining this condition.
	 *
	 * @return the numerical value of this condition
	 */
	public Double getConditionValue() {
		return conditionValue;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		else if ((other == null) || (getClass() != other.getClass())) {
			return false;
		}
		else {
			CondNum otherCN = (CondNum) other;

			boolean test;

			if (this.getQuestion() != null) {
				test = this.getQuestion().equals(
						otherCN.getQuestion());
			}
			else {
				test = otherCN.getQuestion() == null;
			}

			if (this.getConditionValue() != null) {
				test = this.getConditionValue().equals(
						otherCN.getConditionValue())
						&& test;
			}
			else {
				test = (otherCN.getConditionValue() == null) && test;
			}

			if (this.getTerminalObjects() != null) {
				test = this.getTerminalObjects().containsAll(
						otherCN.getTerminalObjects())
						&& otherCN.getTerminalObjects().containsAll(this.getTerminalObjects())
						&& test;
			}
			else {
				test = test && (this.getTerminalObjects() == null)
						&& (otherCN.getTerminalObjects() == null);
			}

			return test;
		}
	}

	@Override
	public boolean eval(Session session)
			throws NoAnswerException, UnknownAnswerException {
		Value caseValue = checkAnswer(session);
		if (caseValue instanceof NumValue) {
			Double caseValueDouble = (Double) (caseValue.getValue());
			return compare(caseValueDouble, getConditionValue());
		}
		else {
			return false;
		}
	}

	protected abstract boolean compare(Double caseValue, Double conditionedValue);

	@Override
	public int hashCode() {
		if (getQuestion() != null && getQuestion().getName() != null) {
			return (getQuestion().getName()).hashCode();
		}
		else {
			return (getClass() + toString()).hashCode();
		}
	}

	static double doubleValue(NumValue value) {
		return (Double) (value.getValue());
	}

}