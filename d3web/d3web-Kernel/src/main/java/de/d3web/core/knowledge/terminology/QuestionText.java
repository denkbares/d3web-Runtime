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

import java.util.logging.Logger;

import de.d3web.core.session.XPSCase;
import de.d3web.core.session.blackboard.CaseQuestionText;
import de.d3web.core.session.blackboard.XPSCaseObject;
import de.d3web.core.session.values.AnswerText;
/**
 * A question which asks for a string text.
 * @author Joachim Baumeister
 */
public class QuestionText extends Question {
	
	private int height;
	private int width;

	public QuestionText(String id) {
		super(id);
	}

	/**
	 * @return AnswerText (with value = value)
	 */
	public AnswerText getAnswer(XPSCase theCase, String value) {
		AnswerText result = new AnswerText();
		result.setText(value);
		result.setQuestion(this);
		return result;
	}

	/**
	 * @return a newly created user-case dependent CaseQuestionText object.
	 */
	public XPSCaseObject createCaseObject(XPSCase session) {
		return new CaseQuestionText(this);
	}

	/**
	 * @return the height of the displayed text field asking the text question.
	 */
	public int getHeight() {
		return height;
	}

	public Answer getValue(XPSCase theCase) {
		Answer value =
			((CaseQuestionText) theCase.getCaseObject(this)).getValue();
		return value;		
//		if (value != null) {
//			ArrayList<Answer> v = new ArrayList<Answer>(1);
//			v.add(value);
//			return (v);
//		} else {
//			return Collections.EMPTY_LIST;
//		}
	}

	/**
	 * @return the width of the displayed text field asking the text question.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the height of the displayed text field
	 * asking the text question. The specified
	 * value must be greater 0.
	 * @param newHeight int the specified height
	 */
	public void setHeight(int height) {
		if (height > 0)
			this.height = height;
	}

	public void setValue(XPSCase theCase, Answer value) {
		Answer answeredValue = (Answer)value;
		((CaseQuestionText) theCase.getCaseObject(this)).setValue(answeredValue);
	}
	
	public void setValue(XPSCase theCase, Object[] values) {
		if (values.length <= 1) {
			setValue(theCase, (Answer)values[0]);
		} else {
			Logger.getLogger(this.getClass().getName()).warning("wrong number of answers");
		}
	}

	/**
	 * Sets the width of the displayed text field
	 * asking the text question. The specified
	 * value must be greater 0.
	 * @param newHeight int the specified width
	 */
	public void setWidth(int newWidth) {
		if (newWidth > 0)
			width = newWidth;
	}
}
