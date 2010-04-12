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

import de.d3web.core.session.Value;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.CaseQuestion;
import de.d3web.core.session.blackboard.CaseQuestionText;
import de.d3web.core.session.blackboard.XPSCaseObject;
import de.d3web.core.session.values.AnswerText;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
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
	public AnswerText getAnswer(Session theCase, String value) {
		AnswerText result = new AnswerText();
		result.setText(value);
		result.setQuestion(this);
		return result;
	}

	/**
	 * @return a newly created user-case dependent CaseQuestionText object.
	 */
	public XPSCaseObject createCaseObject(Session session) {
		return new CaseQuestionText(this);
	}

	/**
	 * @return the height of the displayed text field asking the text question.
	 */
	public int getHeight() {
		return height;
	}

	@Override
	public Value getValue(Session theCase) {
		return ((CaseQuestionText) theCase.getCaseObject(this)).getValue();
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

	@Override
	public void setValue(Session theCase, Value value) throws IllegalArgumentException {
		if (value instanceof TextValue ||
				value instanceof Unknown ||
				value instanceof UndefinedValue) {
			((CaseQuestionText) theCase.getCaseObject(this)).setValue(value);
		}
		else if (value instanceof Unknown || value instanceof UndefinedValue) {
			((CaseQuestion) (theCase.getCaseObject(this))).setValue(value);
		}
		else {
			throw new IllegalArgumentException(value
					+ " is not an accpepted Value instance.");
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
