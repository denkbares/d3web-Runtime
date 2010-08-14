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
import de.d3web.abstraction.inference.PSMethodAbstraction;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.EvaluatableAnswerDateValue;
import de.d3web.core.session.values.EvaluatableAnswerNumValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.UndefinedValue;

/**
 * Adds a specified value to the value of a specified question.
 * 
 * Creation date: (20.06.2001 18:19:13)
 * 
 * @author Joachim Baumeister
 */
public class ActionAddValue extends ActionQuestionSetter {

	/**
	 * creates a new ActionAddValue for the given corresponding rule
	 */
	public ActionAddValue() {
		super();
	}

	private Value addValue(Value currentValue, Value val) {
		Value newValue = currentValue;
		if (currentValue == null) {
			newValue = val;
		}
		else if (getQuestion() instanceof QuestionOC) {
			newValue = val;
		}
		else if (!newValue.equals(val)) {
			newValue = val;

		}
		return newValue;
	}

	private Value addValue(Value currentValue, DateValue val) {
		// does the same like an ActionSetValue-Object
		if (currentValue == null) {
			return val;
		}
		else {
			return currentValue;
		}
	}

	private Value addValue(Value currentVal, NumValue val, Session session) {
		if (currentVal != null) {
			Value ans = currentVal;
			if (ans instanceof NumValue) {
				Double ansval = (Double) ((NumValue) ans).getValue();
				Double newValue = new Double(ansval.doubleValue()
						+ ((Double) val.getValue()).doubleValue());
				currentVal = new NumValue(newValue);
			}
			else {
				currentVal = val;
			}

			if ((getQuestion() instanceof QuestionOC)
					&& (((QuestionOC) getQuestion()).getSchemaForQuestion() != null)) {
				setLastSetValue(session, (Double) val.getValue());
			}
		}
		return currentVal;
	}

	private Value addValue(Value currentVal, EvaluatableAnswerNumValue val, Session session) {
		NumValue ans = new NumValue(val.eval(session));
		return addValue(currentVal, ans, session);
	}

	private Value addValue(Value currentVals, EvaluatableAnswerDateValue val, Session session) {
		return addValue(currentVals, new DateValue(val.eval(session)));
	}

	private Value addValue(Value currentVals, FormulaExpression val, Session session) {
		Value evalAns = val.eval(session);
		if (evalAns instanceof ChoiceValue) {
			return addValue(currentVals, evalAns);
		}
		else if (evalAns instanceof NumValue) {
			return addValue(currentVals, (NumValue) evalAns, session);
		}
		else return null;
	}

	private Value addValue(Value currentVals, FormulaDateExpression val, Session session) {
		Value evalAns = val.eval(session);
		if (evalAns instanceof DateValue) {
			return addValue(currentVals, (DateValue) evalAns);
		}
		else {
			return UndefinedValue.getInstance();
		}
	}

	/**
	 * Alters the list to add the values of this rule action. Don't forget to
	 * call session.SetValue to propagate the changes. Creation date:
	 * (30.01.2002 16:10:28)
	 */
	private Value addValues(Value currentValue, Session session) {
		Object val = getValue();
		if (val != null) {
			if (val instanceof NumValue) {
				currentValue = addValue(currentValue, (NumValue) val, session);
			}
			else if (val instanceof ChoiceValue) {
				currentValue = addValue(currentValue, (ChoiceValue) val);
			}
			else if (val instanceof FormulaExpression) {
				currentValue = addValue(currentValue, (FormulaExpression) val, session);
			}
			else if (val instanceof EvaluatableAnswerNumValue) {
				currentValue = addValue(currentValue, (EvaluatableAnswerNumValue) val,
						session);
			}
			else if (val instanceof DateValue) {
				currentValue = addValue(currentValue, (DateValue) val);
			}
			else if (val instanceof FormulaDateExpression) {
				currentValue = addValue(currentValue, (FormulaDateExpression) val,
						session);
			}
			else if (val instanceof EvaluatableAnswerDateValue) {
				currentValue = addValue(currentValue, (EvaluatableAnswerDateValue) val,
						session);
			}
		}
		return currentValue;
	}

	/**
	 * Returns a list which contains an AnswerNum with the value to add.
	 */
	private Value addSchemaValue(QuestionChoice q, Session session) {
		Object val = getValue();
		Value resultAnswer = null;
		if (val != null) {
			if (val instanceof FormulaExpression) {
				NumValue ans = (NumValue) ((FormulaExpression) val).eval(session);
				setLastSetValue(session, (Double) ans.getValue());
				resultAnswer = ans;
			}
			else if (val instanceof EvaluatableAnswerNumValue) {
				EvaluatableAnswerNumValue evaluatableValue = (EvaluatableAnswerNumValue) val;
				// put the evaluated value to hashtable for undo
				Double newValue = evaluatableValue.eval(session);
				setLastSetValue(session, newValue);
				resultAnswer = new NumValue(newValue);
			}
			else if (val instanceof NumValue) {
				NumValue nv = (NumValue) val;
				setLastSetValue(session, (Double) (nv.getValue()));
				resultAnswer = nv;
			}
		}
		return resultAnswer;
	}

	/**
	 * This method will be called, when the corresponding rule fires. It adds
	 * the specified values to those of the specified Question
	 * 
	 * Creation date: (15.08.2000 11:21:21)
	 * 
	 * @param session current case
	 */
	@Override
	public void doIt(Session session, Object source, PSMethod psmethod) {
		// getQuestion().addProReason(new QASet.Reason(rule), session);
		Value resultValue;
		if ((getQuestion() instanceof QuestionOC)
					&& (((QuestionOC) getQuestion()).getSchemaForQuestion() != null)
					&& (!(getValue() instanceof ChoiceValue))) {
			resultValue = addSchemaValue((QuestionChoice) getQuestion(), session);
		}
		else {
			resultValue = addValues(session.getBlackboard().getValue(getQuestion()), session);
		}
		storeActionValues(session, getValue());

		// if the question is an oc-si-question without schema, the severest
		// answer has to be set (of all answers, which shall be set)
		if ((getQuestion() instanceof QuestionOC)
					&& (((QuestionOC) getQuestion()).getSchemaForQuestion() == null)
					&& (getQuestion().getKnowledge(PSMethodAbstraction.class,
							MethodKind.BACKWARD) != null)) {
			Value severestAnswer = (Value) getValue();

			if ((severestAnswer != null)
						&& (UndefinedValue.isUndefinedValue(session.getBlackboard().getValue(
								getQuestion())))) {
				Fact fact = FactFactory.createFact(getQuestion(),
							severestAnswer,
							source, psmethod);
				session.getBlackboard().addValueFact(fact);
			}
		}
		else {
			// else, set the resultList-values
			session.getBlackboard().addValueFact(
						FactFactory.createFact(getQuestion(), resultValue, source,
								psmethod));
		}

	}

	@Override
	public String toString() {
		return "<RuleAction type=\"AddValue\">\n" + "  [" + getQuestion().getId() + ": "
				+ getValue() + "]" + "\n</RuleAction>";
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
		ActionAddValue a = new ActionAddValue();
		a.setQuestion(getQuestion());
		a.setValue(getValue());
		return a;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o instanceof ActionAddValue) {
			ActionAddValue a = (ActionAddValue) o;
			return isSame(a.getQuestion(), getQuestion()) &&
					getValue().equals(a.getValue());
		}
		else return false;
	}

	private boolean isSame(Object obj1, Object obj2) {
		if (obj1 == null && obj2 == null) return true;
		if (obj1 != null && obj2 != null) return obj1.equals(obj2);
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		if (getQuestion() != null) hash += getQuestion().hashCode();
		if (getValue() != null) hash += getValue().hashCode();
		return hash;
	}
}