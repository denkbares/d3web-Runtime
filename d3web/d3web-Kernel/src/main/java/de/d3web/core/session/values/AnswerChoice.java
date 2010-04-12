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

package de.d3web.core.session.values;

import java.util.List;

import de.d3web.core.knowledge.terminology.Answer;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.Session;

/**
 * Answer (alternative) class for choice questions Creation date: (13.09.2000
 * 14:32:50)
 * 
 * @author norman
 */
public class AnswerChoice extends Answer {

	private String text;

	public AnswerChoice(String theId) {
		super(theId);
	}


	public String getName() {
		return text;
	}

	/**
	 * Creation date: (15.09.2000 11:03:33)
	 * 
	 * @return the value of this answer object depending on the current case
	 */
	public Object getValue(Session theCase) {
		return getName();
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

	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Creation date: (15.09.2000 12:07:31)
	 * 
	 * @return String representation of the answer
	 */
	public String toString() {
		return getName();
	}

	/**
	 * Firstly compares for equal reference, then for equal class instance, then
	 * for equal getText() values. <BR>
	 * 2002-05-29 joba: added for better comparisons
	 * */
	public boolean equals(Object other) {
		if (this == other) return true;
		else if (other instanceof AnswerChoice) return ((AnswerChoice) other).getId().equals(
				this.getId());
		else return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.domainModel.Answer#hashCode()
	 */
	public int hashCode() {
		return getId().hashCode() + getName().hashCode();
	}

	@Override
	public int compareTo(Answer other) {
		if (other instanceof AnswerChoice) {
			List<AnswerChoice> range = ((QuestionChoice) this.getQuestion()).getAllAlternatives();
			int i1 = range.indexOf(this);
			int i2 = range.indexOf(other);
			return i1 - i2;
		}
		if (other instanceof AnswerUnknown) {
			// unknown comes at the and
			return -1;
		}
		throw new IllegalArgumentException(
				"Cannot compare answers of type AnswerChoice and " + other.getClass());
	}

}
