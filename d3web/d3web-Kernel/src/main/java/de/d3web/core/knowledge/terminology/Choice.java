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

import java.util.List;

import de.d3web.core.knowledge.DefaultInfoStore;
import de.d3web.core.knowledge.InfoStore;

/**
 * Answer (alternative) class for choice questions Creation date: (13.09.2000
 * 14:32:50)
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

	/**
	 * Firstly compares for equal reference, then for equal class instance, then
	 * for equal getText() values. <BR>
	 * 2002-05-29 joba: added for better comparisons
	 * */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		else if (other instanceof Choice) {
			return ((Choice) other).getSignatureString().equals(
					this.getSignatureString());
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		String signatureString = getSignatureString();
		result = prime * result + ((signatureString == null) ? 0 : signatureString.hashCode());
		return result;
	}

	private String getSignatureString() {
		if (question == null) {
			return getName();
		}
		else {
			return getName() + question.getName();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.domainModel.Answer#hashCode()
	 */
	// @Override
	// public int hashCode() {
	// return getId().hashCode() + getName().hashCode();
	// }

	@Override
	public int compareTo(Choice other) {
		List<Choice> range = ((QuestionChoice) this.getQuestion()).getAllAlternatives();
		int i1 = range.indexOf(this);
		int i2 = range.indexOf(other);
		int result = i1 - i2;
		if (result == 0) {
			return getSignatureString().compareTo(other.getSignatureString());
		}
		else {
			return result;
		}

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
