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
 * Created on 13.10.2003
 * 
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
package de.d3web.abstraction.formula;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.values.DateValue;

/**
 * Delegate-Pattern: Wraps a QuestionNum to use it as FormulaElement. Creation
 * date: (25.07.2001 15:51:18)
 * 
 * @author Christian Betz
 */
public class QDateWrapper extends FormulaDatePrimitive {

	/**
	 * Creates a new FormulaTerm with null-arguments.
	 */
	public QDateWrapper() {
		this(null);
	}
	
	
	/**
	 * QNumWrapper constructor comment.
	 */
	public QDateWrapper(QuestionDate q) {
		super();
		setQuestion(q);
	}

	/**
	 * @param theCase
	 *            current case
	 * @return evaluated AnswerNumValue (Double) of the wrapped QuestionNum
	 */
	public Date eval(XPSCase theCase) {
		if (getQuestion().getValue(theCase) == null) {
			return null;
		}
		DateValue value = (DateValue) getQuestion().getValue(theCase);
		// EvaluatableAnswerDateValue ret
		// =(EvaluatableAnswerDateValue)ans.getValue(theCase);
		return (Date) value.getValue();
			
	}

	/**
	 * Creation date: (25.07.2001 15:52:27)
	 * 
	 * @return the wrapped QuestionNum
	 */
	public QuestionDate getQuestion() {
		return (QuestionDate)value;
	}

	/**
	 * Sets the QuestionNum that will be wrapped
	 */
	private void setQuestion(QuestionDate newQuestion) {
		value = newQuestion;
	}

	/**
	 * @see FormulaElement
	 */
	public Collection<Object> getTerminalObjects() {
		return Collections.singletonList(value);
	}

	@Override
	public String toString() {
		return value == null ? "question:null" : value.toString();
	}
	
	@Override
	public void setValue(Object o) {
		setQuestion((QuestionDate)o);
	}
}
