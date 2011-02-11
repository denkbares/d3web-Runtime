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

import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.session.values.DateValue;

/**
 * Condition for date questions, where the value has to be equal to a given
 * value (DateValue). The composite pattern is used for this. This class is a
 * "leaf".
 * 
 * @author Sebastian Furth
 */
public class CondDateEqual extends CondDate {

	/**
	 * Creates a new condition, where the value of the specified date question
	 * needs to be equal to the specified value.
	 * 
	 * @param question the specified date question
	 * @param value the specified value (DateValue)
	 */
	public CondDateEqual(QuestionDate question, DateValue value) {
		super(question);
		this.setValue(value);
	}

	@Override
	public String toString() {
		return "\u2190 CondDateEqual question: "
				+ getQuestion().getName()
				+ " value: "
				+ getValue();
	}

	@Override
	public Condition copy() {
		return new CondDateEqual((QuestionDate) getQuestion(), getValue());
	}

	@Override
	protected boolean compare(DateValue caseValue) {
		return caseValue.equals(getValue());
	}
}
