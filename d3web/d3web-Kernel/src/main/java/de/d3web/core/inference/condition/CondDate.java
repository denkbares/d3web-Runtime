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

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.DateValue;

/**
 * Abstract class for conditions with date questions. This class handles
 * administrative stuff such as setting the question object and a date (
 * {@link DateValue}) to compare with. The children classes just insert the compare
 * method for the real comparison.
 * 
 * @author Sebastian Furth
 *
 */
public abstract class CondDate extends CondQuestion {

	protected DateValue value;
	
	protected CondDate(Question idobject) {
		super(idobject);
	}

	@Override
	public boolean eval(Session session) throws NoAnswerException, UnknownAnswerException {
		checkAnswer(session);
		DateValue value = (DateValue) session.getBlackboard().getValue(getQuestion());
		if (value != null) {
			return compare(value);
		}
		else {
			return false;
		}
	}

	protected abstract boolean compare(DateValue caseValue);

	/**
	 * Returns the {@link DateValue} value, that has to be the answer
	 * of the contained {@link QuestionDate}.
	 * 
	 * @return the conditioned String value
	 */
	public DateValue getValue() {
		return this.value;
	}

	/**
	 * Sets the {@link DateValue} value, that has to be the answer of
	 * the contained {@link QuestionDate}.
	 * 
	 * @param value the conditioned String value
	 */
	public void setValue(DateValue value) {
		this.value = value;
	}

}
