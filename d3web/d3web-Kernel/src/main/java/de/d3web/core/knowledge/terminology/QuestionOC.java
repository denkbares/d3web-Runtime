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

package de.d3web.core.knowledge.terminology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.core.session.XPSCase;
import de.d3web.core.session.blackboard.CaseQuestionOC;
import de.d3web.core.session.blackboard.XPSCaseObject;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.session.values.AnswerNum;

/**
 * Storage for Questions with predefined single answers (alternatives).
 * <BR>
 * Part of the Composite design pattern (see QASet for further description)
 * 
 * @author joba, Christian Betz
 * @see QASet
 */
public class QuestionOC extends QuestionChoice {
	private static final long serialVersionUID = 1L;

	public QuestionOC(String id) {
		super(id);
	}

	public XPSCaseObject createCaseObject(XPSCase session) {
		return new CaseQuestionOC(this);
	}

	public Answer getAlternative(int key) {
		return (Answer) getAlternatives().get(key);
	}

	public List<AnswerChoice> getAlternatives() {
		if (alternatives == null) {
			return Collections.emptyList();
		} else
			return alternatives;
	}

	/**
	 * @return a List of Answers which are currently the value of the question.
	 */
	public Answer getValue(XPSCase theCase) {
		Answer value =
			((CaseQuestionOC) theCase.getCaseObject(this)).getValue();
		return value;
		//		if (value != null) {
//			ArrayList<Answer> v = new ArrayList<Answer>(1);
//			v.add(value);
//			return (v);
//		} else {
//			return new ArrayList<Answer>(0);
//		}
	}

	/**
	 * Sets the current value of this OC-question belonging to the
	 * specified XPSCase.<BR>
	 * <B>Caution:</B> It is possible to set numerical values to a one-choice
	 * question. In this case, a Num2ChoiceSchema must be defined a KnowledgeSlice.
	 * @param theCase the belonging XPSCase
	 * @param antwort an array of Answer instances
	 */
	public void setValue(XPSCase theCase, Object[] values) {
		List<Answer> newValues = new ArrayList<Answer>();

		if (values.length == 0) {
			((CaseQuestionOC) theCase.getCaseObject(this)).setValue(null);
		} else if (values.length == 1) {

			// check, if numerical value was set to oc-question
			// if so, then convert numerical value to choice and
			// set converted choice to values[0]
			if (values[0] instanceof AnswerNum) {
				values[0] = convertNumericalValue(theCase, (AnswerNum)values[0]);
			}

			// Bei OC-Fragen kann nur höchstens eine AntwortAlternative angegeben sein.
			Answer newValue;
			if (values.length == 1) {
				newValue = (Answer) values[0];
				newValues.add(newValue);
			} else {
				newValue = null;
			}
			((CaseQuestionOC) theCase.getCaseObject(this)).setValue(newValue);
		} else {
			Logger.getLogger(this.getClass().getName()).throwing(
				this.getClass().getName(),
				"setValue",
				new Exception("too many answers given to question \"" + getId() + "\" (> 1)"));
		}
	}

	public void setValue(XPSCase theCase, Answer value) {
		if (value == null) {
			((CaseQuestionOC) theCase.getCaseObject(this)).setValue(null);
		} else {
			// if num value was passed, then convert numerical value to choice and
			// check, if numerical value was set to oc-question
			if (value instanceof AnswerNum) {
				value = convertNumericalValue(theCase, (AnswerNum)value);
			}
			((CaseQuestionOC) theCase.getCaseObject(this)).setValue(value);
		} 
	}

	public String toString() {
		return super.toString();
	}

}
