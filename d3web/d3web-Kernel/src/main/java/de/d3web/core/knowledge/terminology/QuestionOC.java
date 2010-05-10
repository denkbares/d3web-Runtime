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

import java.util.Collections;
import java.util.List;

import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.CaseQuestionOC;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.values.Choice;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;

/**
 * Storage for Questions with predefined single answers (alternatives). <BR>
 * Part of the Composite design pattern (see QASet for further description)
 * 
 * @author joba, Christian Betz
 * @see QASet
 */
public class QuestionOC extends QuestionChoice {

	public QuestionOC(String id) {
		super(id);
	}

	public SessionObject createCaseObject(Session session) {
		return new CaseQuestionOC(this);
	}

	public Answer getAlternative(int key) {
		return getAlternatives().get(key);
	}

	public List<Choice> getAlternatives() {
		if (alternatives == null) {
			return Collections.emptyList();
		}
		else return alternatives;
	}

	@Override
	public void setValue(Session theCase, Value value) throws IllegalArgumentException {
		if (value == null) {
			((CaseQuestionOC) theCase.getCaseObject(this)).setValue(UndefinedValue.getInstance());
		}
		else if (value instanceof ChoiceValue) {
			((CaseQuestionOC) theCase.getCaseObject(this)).setValue(value);
		}
		// if num value was passed, then convert numerical value to choice and
		// check, if numerical value was set to oc-question
		else if (value instanceof NumValue) {
			Double doubleValue = (Double) ((NumValue) value).getValue();
			value = convertNumericalValue(theCase, doubleValue.doubleValue());
			((CaseQuestionOC) theCase.getCaseObject(this)).setValue(value);
		}
		else if (value instanceof Unknown || value instanceof UndefinedValue) {
			((CaseQuestionOC) (theCase.getCaseObject(this))).setValue(value);
		}
		else {
			throw new IllegalArgumentException(value
					+ " is not an accepted Value implementation for " + getClass() + ".");
		}
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
