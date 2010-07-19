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

package de.d3web.abstraction;

import de.d3web.abstraction.formula.FormulaDateExpression;
import de.d3web.abstraction.formula.FormulaExpression;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.Rule;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.session.CaseObjectSource;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.EvaluatableAnswerDateValue;
import de.d3web.core.session.values.EvaluatableAnswerNumValue;
import de.d3web.core.session.values.NumValue;

/**
 * Sets a specified value for a specified question.
 * 
 * Creation date: (20.06.2001 18:19:13)
 * 
 * @author Joachim Baumeister
 */
public class ActionSetValue extends ActionQuestionSetter implements
		CaseObjectSource {

	// private Map schemaValueHash = null;
	@Override
	public String toString() {
		return "<RuleAction type=\"SetValue\">\n" + "  ["
				+ getQuestion().getId() + ": " + getValue() + "]"
				+ "\n</RuleAction>";
	}

	/**
	 * creates a new ActionSetValue for the given corresponding rule
	 */
	public ActionSetValue() {
		super();
	}

	/**
	 * Sets the specified value for the specified question.
	 */
	@Override
	public void doIt(Session session, Object source, PSMethod psmethod) {
		if (getValue() != null) {
			storeActionValues(session, getValue());
			Value tempVal;

			// for (int i = 0; i < getValues().length; i++) {
			if (getValue() instanceof FormulaExpression) {
				tempVal = ((FormulaExpression) getValue()).eval(session);
				setLastSetValue(session, (Double) ((NumValue) tempVal)
						.getValue());
			} else if (getValue() instanceof EvaluatableAnswerNumValue) {
				EvaluatableAnswerNumValue evaluatableValue = (EvaluatableAnswerNumValue) getValue();

				// put the evaluated value to hashtable for undo
				Double newValue = evaluatableValue.eval(session);
				setLastSetValue(session, newValue);
				tempVal = new NumValue(newValue);
			} else if (getValue() instanceof FormulaDateExpression) {
				tempVal = ((FormulaDateExpression) getValue()).eval(session);
			} else if (getValue() instanceof EvaluatableAnswerDateValue) {
				// AnswerDate ans = new AnswerDate();
				// ans.setQuestion(getQuestion());
				EvaluatableAnswerDateValue evaluatableValue = (EvaluatableAnswerDateValue) getValue();
				// we don't need to store the value in the hashtable,
				// because this is only used for QuestionOC, which
				// can not take a AnswerDate
				//
				// ans.setValue(evaluatableValue);
				tempVal = new DateValue(evaluatableValue.eval(session)); // ans;
			} else if (getValue() instanceof Choice) {
				tempVal = new ChoiceValue((Choice) getValue());
			} else {
				tempVal = (Value) getValue();
			}

			// }
			session.getBlackboard().addValueFact(
					new DefaultFact(getQuestion(), tempVal, source, psmethod));

		}

	}

	/**
	 * Tries to undo the included action.
	 */
	@Override
	public void undo(Session session, Object source, PSMethod psmethod) {
		session.getBlackboard().removeValueFact(getQuestion(), source);
	}

	@Override
	public PSAction copy() {
		ActionSetValue a = new ActionSetValue();
		a.setQuestion(getQuestion());
		a.setValue(getValue());
		return a;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o instanceof ActionSetValue) {
			ActionSetValue a = (ActionSetValue) o;
			return (isSame(a.getQuestion(), getQuestion()) && a.getValue()
					.equals(getValue()));
		} else
			return false;
	}

	private boolean isSame(Object obj1, Object obj2) {
		if (obj1 == null && obj2 == null)
			return true;
		if (obj1 != null && obj2 != null)
			return obj1.equals(obj2);
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		if (getQuestion() != null)
			hash += getQuestion().hashCode();
		if (getValue() != null)
			hash += getValue().hashCode();
		return hash;
	}

}