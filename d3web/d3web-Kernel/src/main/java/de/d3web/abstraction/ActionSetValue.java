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

package de.d3web.abstraction;

import de.d3web.abstraction.formula.FormulaDateExpression;
import de.d3web.abstraction.formula.FormulaExpression;
import de.d3web.abstraction.inference.PSMethodQuestionSetter;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.Rule;
import de.d3web.core.knowledge.terminology.Answer;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.CaseObjectSource;
import de.d3web.core.session.Value;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.values.AnswerChoice;
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
public class ActionSetValue extends ActionQuestionSetter implements CaseObjectSource {

	// private Map schemaValueHash = null;
	@Override
	public String toString() {
		return "<RuleAction type=\"SetValue\">\n" + "  [" + getQuestion().getId() + ": "
				+ getValue() + "]" + "\n</RuleAction>";
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
	public void doIt(XPSCase theCase, Rule rule) {

		if (getValue() != null) {
			if (!lastFiredRuleEqualsCurrentRuleAndNotFired(theCase)) {
				Value tempVal;

				// for (int i = 0; i < getValues().length; i++) {
				if (getValue() instanceof FormulaExpression) {
					tempVal = ((FormulaExpression) getValue()).eval(theCase);
					setLastSetValue(theCase, (Double) ((NumValue) tempVal)
							.getValue());
				}
				else if (getValue() instanceof EvaluatableAnswerNumValue) {
					EvaluatableAnswerNumValue evaluatableValue = (EvaluatableAnswerNumValue) getValue();

					// put the evaluated value to hashtable for undo
					Double newValue = evaluatableValue.eval(theCase);
					setLastSetValue(theCase, newValue);
					tempVal = new NumValue(newValue);
				}
				else if (getValue() instanceof FormulaDateExpression) {
					tempVal = ((FormulaDateExpression) getValue()).eval(theCase);
				}
				else if (getValue() instanceof EvaluatableAnswerDateValue) {
					// AnswerDate ans = new AnswerDate();
					// ans.setQuestion(getQuestion());
					EvaluatableAnswerDateValue evaluatableValue = (EvaluatableAnswerDateValue) getValue();
					// we don't need to store the value in the hashtable,
					// because this is only used for QuestionOC, which
					// can not take a AnswerDate
					//
					// ans.setValue(evaluatableValue);
					tempVal = new DateValue(evaluatableValue.eval(theCase)); // ans;
				}
				else {
					tempVal = (Value) getValue();
				}

				// }

				// TODO: joba 4.2010 : replace this with the Blackboard
				// storeActionValues(theCase, getValue());

				theCase.setValue(getQuestion(), tempVal, rule);

				// if the question is an oc-si-question without schema, the
				// severest answer has to be set
				// (of all answers, which shall be set)
				if ((getQuestion() instanceof QuestionOC)
						&& (((QuestionOC) getQuestion()).getSchemaForQuestion() == null)
						&& (getQuestion().getKnowledge(PSMethodQuestionSetter.class,
						MethodKind.BACKWARD) != null)) {
					Answer ans = getSeverestAnswer((QuestionOC) getQuestion(), theCase);
					if (ans != null) {
						AnswerChoice tempValChoice = (AnswerChoice) tempVal.getValue();
						if (!ans.equals(tempValChoice))
							theCase.setValue(getQuestion(), (Value) getValue());
					}
				}
			}
		}

	}

	/**
	 * Tries to undo the included action.
	 */
	@Override
	public void undo(XPSCase theCase, Rule rule) {

		if (!Boolean.TRUE.equals(getQuestion().getProperties().getProperty(
				Property.TIME_VALUED))
				&& (getQuestion() instanceof QuestionOC)
				&& (((QuestionOC) getQuestion()).getSchemaForQuestion() != null)) {
			// must take the value from hashtable, because it may be the
			// result
			// of a formula
			// which terminal objects have changed!
			Double lastSetValue = getLastSetValue(theCase);
			if (lastSetValue == null) {
				lastSetValue = new Double(0);
			}
			Double valueToSubstract = new Double(lastSetValue.doubleValue() * -1.0);
			theCase.setValue(getQuestion(), new NumValue(valueToSubstract));

			// if the question is an oc-si-question without schema, the severest
			// answer has to be set
			// (of all answers, which shall be set)
			if ((getQuestion() instanceof QuestionOC)
					&& (((QuestionOC) getQuestion()).getSchemaForQuestion() == null)
					&& (getQuestion().getKnowledge(PSMethodQuestionSetter.class,
					MethodKind.BACKWARD) != null)) {
				Answer ans = getSeverestAnswer((QuestionOC) getQuestion(), theCase);
				if (ans != null && ans instanceof AnswerChoice) {
					theCase.setValue(getQuestion(), new ChoiceValue((AnswerChoice) ans));
				}
			}
		}
		else {
			getQuestion().undoSymptomValue(theCase, rule);
		}

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
			return (isSame(a.getQuestion(), getQuestion()) && a.getValue().equals(
					getValue()));
		}
		else return false;
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