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

import de.d3web.core.knowledge.terminology.QuestionNum;

/**
 * The specified numerical question needs to be greater or equal than the
 * specified value.
 * 
 * @author joba
 */
public class CondNumGreaterEqual extends CondNum {

	/**
	 * Creates a new condition, where a specified numerical question needs to be
	 * greater or equal than the specified value.
	 * 
	 * @param question the specified question
	 * @param value the specified value
	 */
	public CondNumGreaterEqual(QuestionNum question, Double value) {
		super(question, value);
	}

	@Override
	protected boolean compare(Double caseValue, Double conditionedValue) {
		return caseValue >= conditionedValue;
	}

	@Override
	public String toString() {
		return getQuestion().getName()
				+ " >= "
				+ getConditionValue();
	}

}
