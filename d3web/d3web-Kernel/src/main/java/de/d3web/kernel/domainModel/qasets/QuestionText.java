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

package de.d3web.kernel.domainModel.qasets;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.answers.AnswerText;
import de.d3web.kernel.dynamicObjects.CaseQuestionText;
import de.d3web.kernel.dynamicObjects.XPSCaseObject;
/**
 * A question which asks for a string text.
 * @author Joachim Baumeister
 */
public class QuestionText extends Question {
	private int height;
	private int width;

	public QuestionText() {
		super();
	}
	
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
	 * @return a newly created user-case dependend CaseQuestionText object.
	 */
	public XPSCaseObject createCaseObject(XPSCase session) {
		return new CaseQuestionText(this);
	}

	/**
	 * @return the height of the displayed textfield asking the text question.
	 */
	public int getHeight() {
		return height;
	}

	public java.util.List getValue(XPSCase theCase) {
		Answer value =
			((CaseQuestionText) theCase.getCaseObject(this)).getValue();
		if (value != null) {
			ArrayList v = new ArrayList(1);
			v.add(value);
			return (v);
		} else {
			return new ArrayList(0);
		}
	}

	/**
	 * @return the width of the displayed textfield asking the text question.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the height of the displayed textfield
	 * asking the text question. The specified
	 * value must be greater 0.
	 * @param newHeight int the specified height
	 */
	public void setHeight(int height) {
		if (height > 0)
			this.height = height;
	}

	public void setValue(XPSCase theCase, Object[] values) {
		//List oldValues = getValue(theCase);
		List newValues = new ArrayList(1);
		if (values.length <= 1) {
			// Bei Text-Fragen kann nur höchstens eine AntwortAlternative angegeben sein.
			Answer newValue;
			if (values.length == 1) {
				newValue = (Answer) values[0];
				newValues.add(newValue);
			} else {
				newValue = null;
			}
			((CaseQuestionText) theCase.getCaseObject(this)).setValue(newValue);

			/* joba: Propagierung übenrimmt jetzt der Fall
			// weiterpropagieren der neuen Werte
			propagate(theCase, oldValues, newValues);
			*/
		} else {
			Logger.getLogger(this.getClass().getName()).warning("wrong number of answers");
		}
		notifyListeners(theCase,this);
	}

	/**
	 * Sets the width of the displayed textfield
	 * asking the text question. The specified
	 * value must be greater 0.
	 * @param newHeight int the specified width
	 */
	public void setWidth(int newWidth) {
		if (newWidth > 0)
			width = newWidth;
	}
}
