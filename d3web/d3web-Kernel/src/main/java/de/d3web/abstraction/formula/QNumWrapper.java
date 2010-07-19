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

package de.d3web.abstraction.formula;

import java.util.Collection;
import java.util.LinkedList;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;

/**
 * Delegate-Pattern: Wraps a QuestionNum to use it as FormulaElement. Creation
 * date: (25.07.2001 15:51:18)
 * 
 * @author Christian Betz
 */
public class QNumWrapper implements FormulaNumberElement {

	private QuestionNum value;

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
	 * @param session current case
	 * @return the Double value of the wrapped QuestionNum
	 */
	public Double eval(Session session) {
		Value val = session.getBlackboard().getValue(getQuestion());
		if (val == null
				|| val instanceof UndefinedValue
				|| val instanceof Unknown) {
			return null;
		}
		else {
			return (Double) val.getValue();
		}
	}

	/**
	 * Creation date: (25.07.2001 15:52:27)
	 * 
	 * @return the wrapped QuestionNum
	 */
	public de.d3web.core.knowledge.terminology.QuestionNum getQuestion() {
		return value;
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
	public Collection<? extends TerminologyObject> getTerminalObjects() {
		Collection<QuestionNum> ret = new LinkedList<QuestionNum>();
		ret.add(value);

		return ret;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}