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
 * Created on 09.10.2003
 */
package de.d3web.kernel.domainModel.qasets;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.answers.AnswerDate;
import de.d3web.kernel.dynamicObjects.CaseQuestionDate;
import de.d3web.kernel.dynamicObjects.XPSCaseObject;

/**
 * A Question which asks for a date.
 * @author Tobias Vogele
 */
public class QuestionDate extends Question {
	
	/**
	 * These constants are used for Property.QUESTION_DATE_TYPE to specify the type
	 * of input.
	 */
	public final static String TYPE_DATE = "date";
	public final static String TYPE_TIME = "time";
	public final static String TYPE_DATE_TIME = "date_time";

	public QuestionDate() {
	    super();
	}
	
	public QuestionDate(String id) {
	    super(id);
	}

	/* (non-Javadoc)
	 * @see de.d3web.kernel.domainModel.qasets.Question#getValue(de.d3web.kernel.XPSCase)
	 */
	public List getValue(XPSCase theCase) {
		Answer value = ((CaseQuestionDate) theCase.getCaseObject(this)).getValue();
		if (value != null) {
			LinkedList list = new LinkedList();
			list.add(value);
			return list;
		} else {
			return new LinkedList();
		}
	}

	/* (non-Javadoc)
	 * @see de.d3web.kernel.domainModel.NamedObject#setValue(de.d3web.kernel.XPSCase, java.lang.Object[])
	 */
	public void setValue(XPSCase theCase, Object[] values) {
		if (values.length > 1) {
			Logger.getLogger(this.getClass().getName()).warning("wrong number of answeralternatives");
		} else {
			Answer newValue = null;
			if (values.length == 1) {
				newValue = (Answer) values[0];
			}
			((CaseQuestionDate) theCase.getCaseObject(this)).setValue(newValue);
		}
		notifyListeners(theCase, this);
	}

	/* (non-Javadoc)
	 * @see de.d3web.kernel.domainModel.CaseObjectSource#createCaseObject()
	 */
	public XPSCaseObject createCaseObject() {
		return new CaseQuestionDate(this);
	}

	/**
	 * @return AnswerNum (with value = value)
	 */
	public AnswerDate getAnswer(XPSCase theCase, String value) {
		if (value == null) {
			return null;
		}else {
			AnswerDate result = new AnswerDate();
			result.setValue(value);
			result.setQuestion(this);
			return result;
		}
	}
	
	/**
	 * @return AnswerNum (with value = value)
	 */
	public AnswerDate getAnswer(XPSCase theCase, Date value) {
		if (value == null) {
			return null;
		}else {
			AnswerDate result = new AnswerDate();
			result.setValue(value);
			result.setQuestion(this);
			return result;
		}
	}	
}
