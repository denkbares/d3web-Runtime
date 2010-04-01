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

package de.d3web.kernel.psMethods.shared;

import de.d3web.core.knowledge.terminology.Question;

/**
 * Creation date: (18.10.2001 18:43:28)
 * @author: Norman Brümmer
 */
public class QuestionWeightValue {
	
	private de.d3web.core.knowledge.terminology.Question question = null;

	private int value = 0;

	/**
	 * QuestionWeight constructor comment.
	 */
	public QuestionWeightValue() {
		super();
	}

	/**
	 * @return a new instance with copied values of the object.
	 */
	public QuestionWeightValue copy() {
		QuestionWeightValue qwv = new QuestionWeightValue();
		qwv.setQuestion(getQuestion());
		qwv.setValue(getValue());
		return qwv;
	}
	
	public String toString() {
		return getQuestion() + ":" + getValue();
	}
	
	/**
	 * @return de.d3web.kernel.domainModel.Question
	 */
	public Question getQuestion() {
		return question;
	}

	/**
	 * @return int
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param newQuestion de.d3web.kernel.domainModel.Question
	 */
	public void setQuestion(Question newQuestion) {
		question = newQuestion;
	}

	/**
	 * Creation date: (18.10.2001 18:44:28)
	 * 
	 * @param newValue int
	 */
	public void setValue(int newValue) {
		value = newValue;
	}
}