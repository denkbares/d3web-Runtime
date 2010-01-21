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

package de.d3web.kernel.psMethods.questionSetter;

import java.util.Arrays;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.CaseObjectSource;
import de.d3web.kernel.domainModel.RuleAction;
import de.d3web.kernel.domainModel.answers.AnswerDate;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.answers.EvaluatableAnswerDateValue;
import de.d3web.kernel.domainModel.answers.EvaluatableAnswerNumValue;
import de.d3web.kernel.domainModel.formula.FormulaDateExpression;
import de.d3web.kernel.domainModel.formula.FormulaExpression;
import de.d3web.kernel.domainModel.qasets.QuestionOC;
import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.supportknowledge.Property;

/**
 * Sets a specified value for a specified question.
 * 
 * Creation date: (20.06.2001 18:19:13)
 * 
 * @author Joachim Baumeister
 */
public class ActionSetValue extends ActionQuestionSetter implements CaseObjectSource {

	private static final long serialVersionUID = -1213290904090399929L;

	// private Map schemaValueHash = null;
	public String toString() {
		return "<RuleAction type=\"SetValue\">\n" + "  [" + getQuestion().getId() + ": "
				+ getValues() + "]" + "\n</RuleAction>";
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
	public void doIt(XPSCase theCase) {

		if ((getValues() != null) && (getValues().length > 0)) {
			if (!lastFiredRuleEqualsCurrentRuleAndNotFired(theCase)) {
				Object[] tempVal = new Object[getValues().length];
				for (int i = 0; i < getValues().length; i++) {
					if (getValues()[i] instanceof FormulaExpression) {
						tempVal[i] = ((FormulaExpression) getValues()[i]).eval(theCase);
						setLastSetValue(theCase, (Double) ((AnswerNum) tempVal[i])
								.getValue(theCase));
					} else if (getValues()[i] instanceof EvaluatableAnswerNumValue) {
						AnswerNum ans = new AnswerNum();
						ans.setQuestion(getQuestion());
						EvaluatableAnswerNumValue evaluatableValue = (EvaluatableAnswerNumValue) getValues()[i];

						// put the evaluated value to hashtable for undo
						Double newValue = evaluatableValue.eval(theCase);

						setLastSetValue(theCase, newValue);

						ans.setValue(evaluatableValue);
						tempVal[i] = ans;
					} else if (getValues()[i] instanceof FormulaDateExpression) {
						tempVal[i] = ((FormulaDateExpression) getValues()[i]).eval(theCase);
					} else if (getValues()[i] instanceof EvaluatableAnswerDateValue) {
						AnswerDate ans = new AnswerDate();
						ans.setQuestion(getQuestion());
						EvaluatableAnswerDateValue evaluatableValue = (EvaluatableAnswerDateValue) getValues()[i];
						// we don't need to store the value in the hashtable,
						// because this is only used for QuestionOC, which
						// can not take a AnswerDate

						ans.setValue(evaluatableValue);
						tempVal[i] = ans;
					} else {
						tempVal[i] = getValues()[i];
					}

				}
				storeActionValues(theCase, getValues());
				theCase.setValue(getQuestion(), tempVal, getCorrespondingRule());

				// if the question is an oc-si-question without schema, the
				// severest answer has to be set
				// (of all answers, which shall be set)
				if ((getQuestion() instanceof QuestionOC)
						&& (((QuestionOC) getQuestion()).getSchemaForQuestion() == null)
						&& (getQuestion().getKnowledge(PSMethodQuestionSetter.class,
								MethodKind.BACKWARD) != null)) {
					Answer ans = getSeverestAnswer((QuestionOC) getQuestion(), theCase);
					if ((ans != null) && (!ans.equals(tempVal[0]))) {
						Object[] newValues = new Object[1];
						newValues[0] = ans;
						theCase.setValue(getQuestion(), getValues());
					}
				}
			}
		}

	}

	/**
	 * Tries to undo the included action.
	 */
	public void undo(XPSCase theCase) {

		if (!Boolean.TRUE.equals(getQuestion().getProperties().getProperty(Property.TIME_VALUED))
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
			AnswerNum substractAnswer = new AnswerNum();
			substractAnswer.setQuestion(getQuestion());
			substractAnswer.setValue(valueToSubstract);
			theCase.setValue(getQuestion(), new Object[]{substractAnswer});
			
			// if the question is an oc-si-question without schema, the severest
			// answer has to be set
			// (of all answers, which shall be set)
			if ((getQuestion() instanceof QuestionOC)
					&& (((QuestionOC) getQuestion()).getSchemaForQuestion() == null)
					&& (getQuestion().getKnowledge(PSMethodQuestionSetter.class, MethodKind.BACKWARD) != null)) {
				Answer ans = getSeverestAnswer((QuestionOC) getQuestion(), theCase);
				if (ans != null) {
					Object[] values = new Object[1];
					values[0] = ans;
					theCase.setValue(getQuestion(), values);
				}
			}
		} else {
			getQuestion().undoSymptomValue(theCase, getCorrespondingRule());
		}


	}
	public RuleAction copy() {
		ActionSetValue a = new ActionSetValue();
		a.setRule(getCorrespondingRule());
		a.setQuestion(getQuestion());
		a.setValues(getValues());
		return a;
	}

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o instanceof ActionSetValue) {
			ActionSetValue a = (ActionSetValue) o;
			return (isSame(a.getQuestion(), getQuestion()) && Arrays.equals(a.getValues(),
					getValues()));
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

	public int hashCode() {
		int hash = 0;
		if (getQuestion() != null)
			hash += getQuestion().hashCode();
		if (getValues() != null)
			hash += getValues().hashCode();
		return hash;
	}

}