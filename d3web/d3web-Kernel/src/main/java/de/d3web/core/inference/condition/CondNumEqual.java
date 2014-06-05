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
 * Condition for numerical questions, where the value has to be equal to a given
 * value (Double value). The comparison is performed with a predefined
 * impression. The composite pattern is used for this. This class is a "leaf".
 * 
 * @author Michael Wolber, joba
 */
public class CondNumEqual extends CondNum {

	/**
	 * Creates a new condition, where a the specified numerical question needs
	 * to be equal to the specified value.
	 * 
	 * @param question the specified numerical question
	 * @param value the specified value (Double)
	 */
	public CondNumEqual(QuestionNum question, Double value) {
		super(question, value);
	}

	@Override
	protected boolean compare(Double caseValue, Double conditionedValue) {
		return (Math.abs(caseValue.doubleValue() - conditionedValue.doubleValue()) <= CondNum.EPSILON);
	}

	@Override
	public String toString() {
		return getQuestion().getName()
				+ " = "
				+ getConditionValue();
	}

}