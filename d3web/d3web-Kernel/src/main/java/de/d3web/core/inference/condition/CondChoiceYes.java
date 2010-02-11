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
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.terminology.QuestionYN;
/**
 * This condition checks, if a YES/NO question has the YES value.
 * The composite pattern is used for this. This class is a "leaf".
 * 
 * @author joba
 */
@Deprecated
public class CondChoiceYes extends CondEqual {


	/**
	 * Creates a new equal-condition. 
	 * @param quest the question to check
	 */
	public CondChoiceYes(QuestionYN question) {
		super(question, question.yes);
	}

	/**
	 * Checks if the question has the value(s) specified in the constructor.
	 */
	public boolean eval(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException {
		checkAnswer(theCase);
		return ((AnswerChoice) question.getValue(theCase).get(0)).isAnswerYes();
	}

	/**
	 * Verbalizes the condition.
	 */
	public String toString() {
		return "\u2190 CondChoiceYes question: " + question.getId();
	}
	
	public AbstractCondition copy() {
		return new CondChoiceYes((QuestionYN)getQuestion());
	}
}