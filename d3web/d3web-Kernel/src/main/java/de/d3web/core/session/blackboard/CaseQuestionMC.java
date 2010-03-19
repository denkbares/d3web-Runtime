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

package de.d3web.core.session.blackboard;

import java.util.List;

import de.d3web.core.knowledge.terminology.Answer;
import de.d3web.core.knowledge.terminology.AnswerMultipleChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
/**
 * Stores the dynamic, user specific values for an QuestionMC
 * object. It corresponds to the static QuestionMC object.<br>
 * Values to be stored:<br>
 * <li> Current values corresponding to a given user case.
 * @author Christian Betz, joba
 * @see QuestionMC
 */
public class CaseQuestionMC extends CaseQuestionChoice {
	Answer theAnswer;
	
	public CaseQuestionMC(QuestionMC question) {
		super(question);
	}

	/**
	 * @return the user-specific value of the depending questionMC
	 */
	public Answer getValue() {
		return theAnswer;
	}

	/**
	 * Sets the user-specific value of the depending questionMC
	 */
	public void setValue(List value) {
		theAnswer = new AnswerMultipleChoice(value);
	}
	

	/**
	 * Sets the user-specific value of the depending questionMC
	 */
	public void setValue(Answer answer) {
		theAnswer = answer;
	}

}