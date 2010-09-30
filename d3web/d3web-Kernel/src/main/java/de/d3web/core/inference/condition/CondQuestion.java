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

import de.d3web.core.knowledge.terminology.IDObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;

/**
 * Abstract class for all conditions that constrain a {@link Question} to a
 * specific value.
 * 
 * Creation date: (23.11.2000 15:59:09)
 * 
 * @author Norman Bruemmer, joba
 */
public abstract class CondQuestion extends TerminalCondition {

	protected Question question = null;

	/**
	 * Creates a new CondQuestion instance with the specified {@link IDObject}.
	 * 
	 * @param idobject the specified {@link IDObject}
	 */
	protected CondQuestion(Question idobject) {
		super(idobject);
		question = idobject;
	}

	/**
	 * Shortcut to be used for the eval methods of inheriting classes: This
	 * method checks if there exists a given value for this condition in the
	 * specified {@link Session}.
	 * 
	 * @throws NoAnswerException if the question has currently no answer
	 * @throws UnknownAnswerException if the question is answered with
	 *         {@link AnswerUnknown}
	 */
	protected void checkAnswer(Session session)
			throws NoAnswerException, UnknownAnswerException {
		Value value = session.getBlackboard().getValue(question);
		;
		if (value instanceof UndefinedValue || value == null) {
			throw NoAnswerException.getInstance();
		}
		else if (value instanceof Unknown) {
			throw UnknownAnswerException.getInstance();
		}
	}

	/**
	 * Returns the question that is constrained by this condition.
	 * 
	 * @return the constrained question of this condition
	 */
	public Question getQuestion() {
		return question;
	}

	/**
	 * Sets the question that is constrained by this condition.
	 * 
	 * @param question the constrained question of this condition
	 */
	protected void setQuestion(Question question) {
		this.question = question;
	}

	@Override
	public boolean equals(Object other) {
		if (!super.equals(other)) {
			return false;
		}

		if (this.getQuestion() != null) {
			return this.getQuestion().equals(
					((CondQuestion) other).getQuestion());
		}
		else {
			return this.getQuestion() == ((CondQuestion) other).getQuestion();
		}
	}

	@Override
	public int hashCode() {
		// use the ID for the hashCode
		if (getQuestion() != null && getQuestion().getId() != null) {
			return (getQuestion().getId()).hashCode();
		}
		else {
			return toString().hashCode();
		}
	}
}
