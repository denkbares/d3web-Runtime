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
 * Created on 13.10.2003
 * 
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
package de.d3web.abstraction.formula;

import java.util.Date;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Value;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.DateValue;

/**
 * Encapsulates a FormulaDateElement and ensures the return of an answer date
 * </p>
 * 
 * @author Tobias Vogele
 *         <P>
 * @see FormulaExpression
 */
public class FormulaDateExpression {

	/** the Question this expression belongs to */
	private final Question question;

	/** The encapsulated formula date element */
	private final FormulaDateElement fElement;

	@Override
	public String toString() {
		return "[FormulaDateExpression, " + question.getId() + "] " + fElement.toString();
	}

	/**
	 * creates a new FormulaDateExpression by the given Question and
	 * FormulaDateElement
	 */
	public FormulaDateExpression(Question question, FormulaDateElement fElement) {
		super();
		this.question = question;
		this.fElement = fElement;
	}

	/**
	 * Evaluates the formulaDateElement and creates the returned value into an
	 * AnswerDate
	 * 
	 * @return an AnswerDate containing the evaluated value
	 */
	public Value eval(Session session) {
		Date answer = fElement.eval(session);
		if (answer != null) {
			return new DateValue(answer);
		}
		else return null;
	}

	public FormulaDateElement getFormulaDateElement() {
		return fElement;
	}

	public Question getQuestionDate() {
		return question;
	}
}
