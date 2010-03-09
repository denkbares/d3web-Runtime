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

/*
 * Created on 04.07.2003
 */
package de.d3web.core.session.blackboard;

import de.d3web.core.knowledge.terminology.QuestionChoice;

/**
 * Stores the dynamic, user specific values for an QuestionChoice
 * object. It corresponds to the static QuestionChoice object.<br>
 * Values to be stored:<br>
 * <li> Current value corresponding to a given user case.
 * @author Tobias Vogele
 * @see QuestionChoice
 */
public class CaseQuestionChoice extends CaseQuestion {

	private Double numSchemaValue = null;

	public CaseQuestionChoice(QuestionChoice question) {
		super(question);
	}

	/**
	 * @return the current numerical value of the question 
	 * according to a give XPSCase. This value is used to
	 * be processed by a Num2ChoiceSchema.
	 */
	public Double getNumericalSchemaValue() {
		if (numSchemaValue == null) {
			numSchemaValue = new Double(0.0);
		}
		return numSchemaValue;
	}


	public void setNumericalSchemaValue(Double newValue) {
		numSchemaValue = newValue;
	}
}
