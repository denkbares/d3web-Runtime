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

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.DateValue;

/**
 * Abstract class for conditions with date questions. This class handles
 * administrative stuff such as setting the question object and a date (
 * {@link DateValue}) to compare with. The children classes just insert the
 * compare method for the real comparison.
 * 
 * @author Sebastian Furth
 * 
 */
public abstract class CondDate extends CondQuestion {

	private DateValue value;

	protected CondDate(Question idobject) {
		super(idobject);
	}

	@Override
	public boolean eval(Session session) throws NoAnswerException, UnknownAnswerException {
		checkAnswer(session);
		DateValue dateValue = (DateValue) session.getBlackboard().getValue(getQuestion());
		if (dateValue != null) {
			return compare(dateValue);
		}
		else {
			return false;
		}
	}

	protected abstract boolean compare(DateValue caseValue);

	/**
	 * Returns the {@link DateValue} value, that has to be the answer of the
	 * contained {@link QuestionDate}.
	 * 
	 * @return the conditioned String value
	 */
	public DateValue getValue() {
		return this.value;
	}

	/**
	 * Sets the {@link DateValue} value, that has to be the answer of the
	 * contained {@link QuestionDate}.
	 * 
	 * @param value the conditioned String value
	 */
	public void setValue(DateValue value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CondDate other = (CondDate) obj;
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		}
		else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

}
