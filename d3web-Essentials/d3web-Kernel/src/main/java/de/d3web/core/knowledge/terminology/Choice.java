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

package de.d3web.core.knowledge.terminology;

import java.util.Objects;

import de.d3web.core.knowledge.DefaultInfoStore;
import de.d3web.core.knowledge.InfoStore;

/**
 * Answer (alternative) class for choice questions Creation date: (13.09.2000 14:32:50)
 *
 * @author norman
 */
public class Choice implements NamedObject, Comparable<Choice> {

	/**
	 * The {@link Question} instance this {@link Choice} belongs to
	 */
	private Question question;

	private final String text;
	private final InfoStore infoStore = new DefaultInfoStore();

	/**
	 * Creates a new choice with the given name
	 *
	 * @param name the name of the choice
	 * @throws NullPointerException when the name is null
	 */
	public Choice(String name) {
		if (name == null) {
			throw new NullPointerException("Name must not be null.");
		}
		this.text = name;
	}

	@Override
	public String getName() {
		return text;
	}

	/**
	 * Creation date: (28.09.00 17:50:31)
	 *
	 * @return true, if this is an as AnswerNo configured answer (false here)
	 */
	public boolean isAnswerNo() {
		return false;
	}

	/**
	 * Creation date: (28.09.00 17:50:14)
	 *
	 * @return true, if this is an as AnswerYes configured answer (false here)
	 */
	public boolean isAnswerYes() {
		return false;
	}

	/**
	 * Creation date: (15.09.2000 12:07:31)
	 *
	 * @return String representation of the answer
	 */
	@Override
	public String toString() {
		return text;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Choice)) return false;
		Choice choice = (Choice) o;
		return Objects.equals(question, choice.question) &&
				Objects.equals(text, choice.text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(question, text);
	}

	@Override
	public int compareTo(Choice other) {
		int result = Integer.compare(getIndex(), other.getIndex());
		if (result != 0) return result;

		result = text.compareTo(other.text);
		if (result != 0) return result;

		if (question == other.question) return 0;
		if (question == null) return 1;
		if (other.question == null) return -1;
		return question.getName().compareTo(other.question.getName());
	}

	/**
	 * Retuns the index of the choice in the choice's question, or -1 if the choice is not part of a choice question.
	 */
	public int getIndex() {
		return (question instanceof QuestionChoice)
				? ((QuestionChoice) question).getAllAlternatives().indexOf(this) : -1;
	}

	@Override
	@Deprecated
	public String getId() {
		return getName();
	}

	/**
	 * Returns the {@link Question} instance corresponding to this Choice.
	 *
	 * @return the question related with this answer
	 */
	public Question getQuestion() {
		return question;
	}

	/**
	 * Sets the {@link Question} object, that is corresponding to this Choice.
	 *
	 * @param question the corresponding {@link Question}
	 */
	public void setQuestion(Question question) {
		this.question = question;
	}

	@Override
	public InfoStore getInfoStore() {
		return infoStore;
	}
}
