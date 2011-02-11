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

package de.d3web.abstraction.formula;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;

/**
 * FormulaElement term that can count the answers of a QuestionMC Creation date:
 * (14.08.2000 16:33:00)
 * 
 * @author Christian
 */
public class Count implements FormulaNumberElement {

	private QuestionMC questionMC = null;

	/**
	 * Creates a new Count with null-question.
	 */
	public Count() {
		this(null);
	}

	/**
	 * Creates a new Count object that counts the answers of questionMC
	 **/
	public Count(QuestionMC questionMC) {
		this.questionMC = questionMC;
	}

	@Override
	public Collection<? extends TerminologyObject> getTerminalObjects() {
		Collection<QuestionMC> ret = new LinkedList<QuestionMC>();
		ret.add(questionMC);

		return ret;
	}

	/**
	 * @return the number of active alternatives for a multiple-choice answer,
	 *         0, if the active answer is "No" or "unknown".
	 */
	@Override
	public Value eval(Session session) {
		MultipleChoiceValue value = (MultipleChoiceValue) session.getBlackboard().getValue(
				getQuestionMC());
		List<Choice> choices = value.asChoiceList(getQuestionMC());

		// check, if AnswerNo oder AnswerUnknown is included
		for (Choice answerChoice : choices) {
			if (answerChoice.isAnswerNo()) {
				return new NumValue(new Double(0));
			}
		}
		return new NumValue(choices.size());
	}

	public QuestionMC getQuestionMC() {
		return questionMC;
	}

	@Override
	public String toString() {
		return "#" + (getQuestionMC() != null ? " " + getQuestionMC().toString() : ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}