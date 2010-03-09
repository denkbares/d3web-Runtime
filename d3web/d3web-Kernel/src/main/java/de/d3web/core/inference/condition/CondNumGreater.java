/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.core.inference.condition;

import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.values.AnswerNum;

/**
 * Condition for numerical questions, where the value
 * has to be greater than a given value (Double value).
 * The composite pattern is used for this. This class is a "leaf".
 * 
 * @author Michael Wolber, joba
 */
public class CondNumGreater extends CondNum {

	private static final long serialVersionUID = 4544477310920618499L;

	/**
	 * Creates a new condition, where a the specified numerical question needs to
	 * be greater than the specified value.
	 * @param question the specified numerical question
	 * @param value the specified value (Double)
	 */
	public CondNumGreater(QuestionNum question, Double value) {
		super(question, value);
	}

	@Override
	public boolean eval(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException {
		checkAnswer(theCase);

		AnswerNum answer = (AnswerNum) getQuestion().getValue(theCase).get(0);
		Double value = (Double) answer.getValue(theCase);
		if (value != null) {
			return (value.doubleValue() > getAnswerValue().doubleValue());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "\u2190 CondNumGreater question: "
			+ question.getId()
			+ " value: "
			+ getAnswerValue();
	}
	
	@Override
	public AbstractCondition copy() {
		return new CondNumGreater((QuestionNum)getQuestion(),  getAnswerValue());
	}

}
