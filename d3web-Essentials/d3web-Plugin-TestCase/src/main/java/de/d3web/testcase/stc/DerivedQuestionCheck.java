/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.testcase.stc;

import de.d3web.core.inference.condition.CondNum;
import de.d3web.core.inference.condition.CondNumEqual;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.testcase.model.Check;

/**
 * Checks if a {@link Question} has the specified {@link QuestionValue} in a
 * {@link Session}
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 24.01.2012
 */
public class DerivedQuestionCheck implements Check {

	private final Question question;
	private final QuestionValue value;

	public DerivedQuestionCheck(Question question, QuestionValue value) {
		this.question = question;
		this.value = value;
	}

	@Override
	public boolean check(Session session) {
		// handle QuestionNums
		if (value instanceof NumValue && question instanceof QuestionNum) {
			return checkQuestionNum(session, (QuestionNum) question, (NumValue) value);
		}
		Value blackBoardValue = session.getBlackboard().getValue(
				question);
		// handler QuestionMCs
		if (blackBoardValue instanceof MultipleChoiceValue && question instanceof QuestionMC) {
			MultipleChoiceValue currentValue = (MultipleChoiceValue) blackBoardValue;
			return currentValue.contains(value);
		}
		return blackBoardValue.equals(value);
	}

	private boolean checkQuestionNum(Session session, QuestionNum question, NumValue value) {
		// we use eval of CondNum, because it uses CondNum.EPSILON
		CondNum condNum = new CondNumEqual(question, value.getDouble());
		try {
			return condNum.eval(session);
		}
		catch (NoAnswerException e) {
			return false;
		}
		catch (UnknownAnswerException e) {
			return false;
		}
	}

	@Override
	public String getCondition() {
		return question.getName() + " = " + value.toString();
	}

	public Question getQuestion() {
		return question;
	}

	public QuestionValue getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DerivedQuestionCheck that = (DerivedQuestionCheck) o;

		if (!question.equals(that.question)) return false;
		return value.equals(that.value);

	}

	@Override
	public int hashCode() {
		int result = question.hashCode();
		result = 31 * result + value.hashCode();
		return result;
	}

}
