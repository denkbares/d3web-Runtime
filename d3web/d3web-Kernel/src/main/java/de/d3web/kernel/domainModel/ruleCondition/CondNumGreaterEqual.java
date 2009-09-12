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

package de.d3web.kernel.domainModel.ruleCondition;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.qasets.QuestionNum;

/**
 * The specified numerical question needs to
 * be greater or equal than the specified value.
 * @author joba
 */
public class CondNumGreaterEqual extends CondNum {

	/**
	 * Creates a new condition, where a specified numerical question 
	 * needs to be greater or equal than the specified value.
	 * @param question the specified question
	 * @param value the specified value
	 */
	public CondNumGreaterEqual(QuestionNum question, Double value) {
		super(question, value);
	}

	@Override
	public boolean eval(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException {
		checkAnswer(theCase);

		AnswerNum answer = (AnswerNum) getQuestion().getValue(theCase).get(0);
		Double value = (Double) answer.getValue(theCase);
		if (value != null) {
			return (value.doubleValue() >= getAnswerValue().doubleValue());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "<Condition type='numGreaterEqual' ID='"
			+ getQuestion().getId()
			+ "' value='"
			+ getAnswerValue()
			+ "'>"
			+ "</Condition>\n";
	}

	@Override
	public AbstractCondition copy() {
		return new CondNumGreaterEqual((QuestionNum)getQuestion(),  getAnswerValue());
	}
	
}
