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

package de.d3web.kernel.domainModel.formula;
import java.util.Collection;
import java.util.LinkedList;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.qasets.QuestionNum;

/**
 * Delegate-Pattern: Wraps a QuestionNum to use it as FormulaElement. Creation date: (25.07.2001 15:51:18)
 * 
 * @author Christian Betz
 */
public class QNumWrapper extends FormulaNumberPrimitive {

	/**
	 * Creates a new FormulaTerm with null-arguments.
	 */
	public QNumWrapper() {
		this(null);
	}

	/**
	 * QNumWrapper constructor comment.
	 */
	public QNumWrapper(QuestionNum q) {
		super();
		setQuestion(q);
	}

	/**
	 * @param theCase
	 *            current case
	 * @return evaluated AnswerNumValue (Double) of the wrapped QuestionNum
	 */
	public Double eval(XPSCase theCase) {
		if (getQuestion().getValue(theCase) == null
			|| getQuestion().getValue(theCase).isEmpty()
			|| getQuestion().getValue(theCase).get(0).equals(getQuestion().getUnknownAlternative())) {
			return null;
		}
		return (Double) ((AnswerNum) (getQuestion().getValue(theCase).get(0))).getValue(theCase);
	}

	/**
	 * Creation date: (25.07.2001 15:52:27)
	 * 
	 * @return the wrapped QuestionNum
	 */
	public de.d3web.kernel.domainModel.qasets.QuestionNum getQuestion() {
		return (QuestionNum) value;
	}

	public void setValue(Object o) {
		setQuestion((QuestionNum) o);
	}

	/**
	 * @see FormulaElement
	 */
	public java.lang.String getXMLString() {

		StringBuffer sb = new StringBuffer();
		sb.append("<FormulaPrimitive type='QNumWrapper'>\n");
		sb.append("<Value>" + getQuestion().getId() + "</Value>");
		sb.append("</FormulaPrimitive>\n");
		return sb.toString();
	}

	/**
	 * Sets the QuestionNum that will be wrapped
	 */
	private void setQuestion(QuestionNum newQuestion) {
		value = newQuestion;
	}

	/**
	 * @see FormulaElement
	 */
	public Collection getTerminalObjects() {
		Collection ret = new LinkedList();
		ret.add(value);

		return ret;
	}

	public String toString() {
		return value.toString();
	}
}