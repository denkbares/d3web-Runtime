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

package de.d3web.core.terminology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.core.session.XPSCase;
import de.d3web.core.session.blackboard.CaseQuestionOC;
import de.d3web.core.session.blackboard.XPSCaseObject;
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

	public QuestionOC() {
		super();
	}
	
	public QuestionOC(String id) {
		super(id);
	}

	public XPSCaseObject createCaseObject(XPSCase session) {
		return new CaseQuestionOC(this);
	}

	public Answer getAlternative(int key) {
		return (Answer) getAlternatives().get(key);
	}

	public List getAlternatives() {
		if (alternatives == null) {
			return Collections.EMPTY_LIST;
		} else
			return alternatives;
	}

	/**
	 * @return a List of Answers which are currently the value of the question.
	 */
	public List<Answer> getValue(XPSCase theCase) {
		Answer value =
			((CaseQuestionOC) theCase.getCaseObject(this)).getValue();
		if (value != null) {
			ArrayList<Answer> v = new ArrayList<Answer>(1);
			v.add(value);
			return (v);
		} else {
			return new ArrayList<Answer>(0);
		}
	}

	public void setValue(XPSCase theCase, Object value) {
		if (value == null) {
			((CaseQuestionOC) theCase.getCaseObject(this)).setValue(null);
		} else {
			// check, if numerical value was set to oc-question
			// if so, then convert numerical value to choice and
			// set converted choice to value
			if (value instanceof AnswerNum) {
				value = convertNumericalValue(theCase, (AnswerNum)value);
			}
			Answer newValue = (Answer) value;
			((CaseQuestionOC) theCase.getCaseObject(this)).setValue(newValue);
		}
		notifyListeners(theCase,this);
	}
	
	/**
	 * Sets the current value of this OC-question belonging to the
	 * specified XPSCase.<BR>
	 * <B>Caution:</B> It is possible to set numerical values to a one-choice
	 * question. In this case, a Num2ChoiceSchema must be defined a KnowledgeSlice.
	 * @param theCase the belonging XPSCase
	 * @param antwort an array of Answer instances
	 */
	@Deprecated
	public void setValue(XPSCase theCase, Object[] values) {
		if (values.length <= 1) {
			setValue(theCase, values[0]);
		} else {
			Logger.getLogger(this.getClass().getName()).throwing(
				this.getClass().getName(),
				"setValue",
				new Exception("too many answers given to question \"" + getId() + "\" (> 1)"));
		}
	}

	public String toString() {
		return super.toString();
	}

}
