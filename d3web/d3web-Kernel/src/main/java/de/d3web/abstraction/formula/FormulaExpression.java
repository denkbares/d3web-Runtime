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

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Value;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.NumValue;

/**
 * Encapsulates a FormulaElement and ensures the return of an answer num
 * <p>
 * Looks like a delegate-pattern to me
 * </p>
 * Creation date: (15.11.2000 16:24:01)
 * 
 * @author Christian Betz, joba
 *         <P>
 *         [joba] changed QuestionNum to Question, since QuestionOC can also
 *         belong to a FormulaExpression (-> Num2ChoiceSchema)
 */
public class FormulaExpression {

	/** the Question this expression belongs to */
	private final Question question;

	/** The encapsulated formula element */
	private final FormulaNumberElement fElement;

	@Override
	public String toString() {
		return "[FormulaExpression, " + question.getId() + "] " + fElement.toString();
	}

	/**
	 * Creates a new FormulaExpression with null-arguments.
	 */
	public FormulaExpression() {
		this(null, null);
	}

	/**
	 * creates a new FormulaExpression by the given Question and FormulaElement
	 */
	public FormulaExpression(Question question, FormulaNumberElement fElement) {
		super();
		this.question = question;
		this.fElement = fElement;
	}

	/**
	 * Evaluates the formulaElement and creates the returned value into an
	 * AnswerNum
	 * 
	 * @return an AnswerNum containing the evaluated value
	 */
	public Value eval(Session session) {
		Double answer = fElement.eval(session);
		if (answer != null) {
			return new NumValue(answer);
		}
		else return null;
	}

	public FormulaNumberElement getFormulaElement() {
		return fElement;
	}

	public Question getQuestionNum() {
		return question;
	}
}