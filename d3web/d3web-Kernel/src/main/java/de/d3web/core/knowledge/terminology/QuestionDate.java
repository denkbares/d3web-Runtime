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

/*
 * Created on 09.10.2003
 */
package de.d3web.core.knowledge.terminology;

import java.util.Date;

import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.CaseQuestion;
import de.d3web.core.session.blackboard.CaseQuestionDate;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.values.AnswerDate;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;

/**
 * A Question which asks for a date.
 * 
 * @author Tobias Vogele
 */
public class QuestionDate extends Question {

	/**
	 * These constants are used for Property.QUESTION_DATE_TYPE to specify the
	 * type of input.
	 */
	public final static String TYPE_DATE = "date";
	public final static String TYPE_TIME = "time";
	public final static String TYPE_DATE_TIME = "date_time";

	public QuestionDate(String id) {
		super(id);
	}

	// @Override
	// @Deprecated
	// public void setValue(Session theCase, Object[] values) {
	// if (values.length != 1) {
	// Logger.getLogger(this.getClass().getName()).warning("wrong number of answeralternatives");
	// } else {
	// ((CaseQuestionDate)
	// theCase.getCaseObject(this)).setValue((Answer)values[0]);
	// }
	// }

	@Override
	public void setValue(Session theCase, Value value) throws IllegalArgumentException {
		if (value instanceof DateValue) {
			((CaseQuestionDate) theCase.getCaseObject(this)).setValue(value);
		}
		else if (value instanceof Unknown || value instanceof UndefinedValue) {
			((CaseQuestion) (theCase.getCaseObject(this))).setValue(value);
		}
		else throw new IllegalArgumentException(value
				+ " is not an instance of DateValue.");
	}

	public SessionObject createCaseObject(Session session) {
		return new CaseQuestionDate(this);
	}

	/**
	 * @return AnswerNum (with value = value)
	 */
	public AnswerDate getAnswer(Session theCase, String value) {
		if (value == null) {
			return null;
		}
		else {
			AnswerDate result = new AnswerDate();
			result.setValue(value);
			result.setQuestion(this);
			return result;
		}
	}

	/**
	 * @return AnswerNum (with value = value)
	 */
	public AnswerDate getAnswer(Session theCase, Date value) {
		if (value == null) {
			return null;
		}
		else {
			AnswerDate result = new AnswerDate();
			result.setValue(value);
			result.setQuestion(this);
			return result;
		}
	}
}
