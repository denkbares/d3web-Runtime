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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.abstraction.formula.FormulaDateExpression;
import de.d3web.abstraction.formula.FormulaExpression;
import de.d3web.abstraction.inference.PSMethodQuestionSetter;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleAction;
import de.d3web.core.knowledge.terminology.Answer;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.session.SymptomValue;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.blackboard.CaseQuestion;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.session.values.AnswerDate;
import de.d3web.core.session.values.AnswerNum;
import de.d3web.core.session.values.EvaluatableAnswerDateValue;
import de.d3web.core.session.values.EvaluatableAnswerNumValue;

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

	private Answer addValue(Answer currentValue, Answer val) {
		Answer newValue = currentValue;
		if (currentValue == null) {
			newValue = val;
		} else if (getQuestion() instanceof QuestionOC) {
			newValue = val;
		} else if (!newValue.equals(val)) {
			newValue = val;

		}
		return newValue;
	}

	private Answer addValue(Answer currentValue, AnswerDate val) {
		// does the same like an ActionSetValue-Object
		if (currentValue == null) {
			return val;
		} else {
			return currentValue;
		}
	}

	private Answer addValue(Answer currentVal, AnswerNum val, XPSCase theCase) {
		if (currentVal != null) {
			Answer ans = currentVal;
			if (ans instanceof AnswerNum) {
				Double ansval = (Double) ((AnswerNum) ans).getValue(theCase);
				Double newValue = new Double(ansval.doubleValue()
						+ ((Double) val.getValue(theCase)).doubleValue());
				AnswerNum ansnum = new AnswerNum();
				ansnum.setQuestion(getQuestion());
				ansnum.setValue(newValue);
				currentVal = ansnum;
		} else {
			currentVal = val;
		}
			
		if ((getQuestion() instanceof QuestionOC)
				&& (((QuestionOC) getQuestion()).getSchemaForQuestion() != null)) {
			setLastSetValue(theCase, (Double) val.getValue(theCase));
		}
		}
		return currentVal;
	}

	private Answer addValue(Answer currentVal, EvaluatableAnswerNumValue val, XPSCase theCase) {

		AnswerNum ans = new AnswerNum();
		ans.setQuestion(getQuestion());
		ans.setValue(val);

		return addValue(currentVal, ans, theCase);
	}

	private Answer addValue(Answer currentVals, EvaluatableAnswerDateValue val) {

		AnswerDate ans = new AnswerDate();
		ans.setQuestion(getQuestion());
		ans.setValue(val);

		return addValue(currentVals, ans);
	}

	private Answer addValue(Answer currentVals, FormulaExpression val, XPSCase theCase) {
		Answer evalAns = val.eval(theCase);

		if (evalAns instanceof AnswerChoice) {
			return addValue(currentVals, (AnswerChoice) evalAns);
		} else if (evalAns instanceof AnswerNum) {
			return addValue(currentVals, (AnswerNum) evalAns, theCase);
		} else
			return null;
	}

	private Answer addValue(Answer currentVals, FormulaDateExpression val, XPSCase theCase) {
		Answer evalAns = val.eval(theCase);

		if (evalAns instanceof AnswerDate) {
			return addValue(currentVals, (AnswerDate) evalAns);
		} else
			return null;
	}

	/**
	 * Alters the list to add the values of this rule action. Don't forget to
	 * call theCase.SetValue to propagate the changes. Creation date:
	 * (30.01.2002 16:10:28)
	 */
	private Answer addValues(Answer currentValue, XPSCase theCase) {
		Object[] val = getValues();
		if (val != null) {
			for (int i = 0; i < val.length; ++i) {
				if (val[i] instanceof AnswerNum) {
					currentValue = addValue(currentValue, (AnswerNum) val[i], theCase);
				} else if (val[i] instanceof AnswerChoice) {
					currentValue = addValue(currentValue, (AnswerChoice) val[i]);
				} else if (val[i] instanceof FormulaExpression) {
					currentValue = addValue(currentValue, (FormulaExpression) val[i], theCase);
				} else if (val[i] instanceof EvaluatableAnswerNumValue) {
					currentValue = addValue(currentValue, (EvaluatableAnswerNumValue) val[i],
							theCase);
				} else if (val[i] instanceof AnswerDate) {
					currentValue = addValue(currentValue, (AnswerDate) val[i]);
				} else if (val[i] instanceof FormulaDateExpression) {
					currentValue = addValue(currentValue, (FormulaDateExpression) val[i], theCase);
				} else if (val[i] instanceof EvaluatableAnswerDateValue) {
					currentValue = addValue(currentValue, (EvaluatableAnswerDateValue) val[i]);
				}
			}
		}
		return currentValue;
	}

	/**
	 * Returns a list which contains an AnswerNum with the value to add.
	 */
	private Answer addSchemaValue(QuestionChoice q, XPSCase theCase) {
		Object[] val = getValues();
		Answer resultAnswer = null;
		if (val != null) {
			if (val[0] instanceof FormulaExpression) {
				AnswerNum ans = (AnswerNum) ((FormulaExpression) val[0]).eval(theCase);
				setLastSetValue(theCase, (Double) ans.getValue(theCase));
				ans.setValue((Double) ans.getValue(theCase));
				resultAnswer = ans;
			} else if (val[0] instanceof EvaluatableAnswerNumValue) {
				AnswerNum ans = new AnswerNum();
				ans.setQuestion(getQuestion());
				EvaluatableAnswerNumValue evaluatableValue = (EvaluatableAnswerNumValue) val[0];

				// put the evaluated value to hashtable for undo
				Double newValue = evaluatableValue.eval(theCase);

				setLastSetValue(theCase, newValue);

				ans.setValue(evaluatableValue);
				resultAnswer = ans;
			} else if (val[0] instanceof AnswerNum) {
				Double ansValue = (Double) ((AnswerNum) val[0]).getValue(theCase);
				setLastSetValue(theCase, ansValue);
				AnswerNum newAnswer = new AnswerNum();
				newAnswer.setQuestion(getQuestion());
				newAnswer.setValue(ansValue);
				resultAnswer = newAnswer;
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
	 * @param theCase
	 *            current case
	 */
	public void doIt(XPSCase theCase) {
		if (!lastFiredRuleEqualsCurrentRuleAndNotFired(theCase)) {
			getQuestion().addProReason(new QASet.Reason(getCorrespondingRule()), theCase);

			Answer resultAnswer;

			if ((getQuestion() instanceof QuestionOC)
					&& (((QuestionOC) getQuestion()).getSchemaForQuestion() != null)
					&& (getValues().length > 0)
					&& (!(getValues()[0] instanceof AnswerChoice))) {
				resultAnswer = addSchemaValue((QuestionChoice) getQuestion(), theCase);
			} else {
				resultAnswer = addValues(getQuestion().getValue(theCase), theCase);
			}
			storeActionValues(theCase, getValues());

			// if the question is an oc-si-question without schema, the severest
			// answer
			// has to be set (of all answers, which shall be set)
			if ((getQuestion() instanceof QuestionOC)
					&& (((QuestionOC) getQuestion()).getSchemaForQuestion() == null)
					&& (getQuestion().getKnowledge(PSMethodQuestionSetter.class,
							MethodKind.BACKWARD) != null)) {
				Answer ans = getSeverestAnswer((QuestionOC) getQuestion(), theCase);
				if ((ans != null)
						&& ((!getQuestion().hasValue(theCase)) || (!ans.equals(getQuestion()
								.getValue(theCase))))) {
					Object[] newValues = new Object[1];
					newValues[0] = ans;
					theCase.setValue(getQuestion(), newValues);
				}
			} else {
				// else, set the resultList-values
				theCase.setValue(getQuestion(), new Answer[] {resultAnswer}, getCorrespondingRule());
			}
		}
	}

	public String toString() {
		return "<RuleAction type=\"AddValue\">\n" + "  [" + getQuestion().getId() + ": "
				+ getValues() + "]" + "\n</RuleAction>";
	}

	/**
	 * Tries to undo the included action.
	 */
	public void undo(XPSCase theCase) {
		getQuestion().removeProReason(new QASet.Reason(getCorrespondingRule()), theCase);

		removeRuleFromSymptomValueHistory(theCase, getCorrespondingRule());

		// if the question is an oc-si-question without schema, the severest
		// answer has to be set
		// (of all answers, which shall be set)
		if ((getQuestion() instanceof QuestionOC)
				&& (((QuestionOC) getQuestion()).getSchemaForQuestion() == null)
				&& (getQuestion().getKnowledge(PSMethodQuestionSetter.class, MethodKind.BACKWARD) != null)) {
			Answer ans = getSeverestAnswer((QuestionOC) getQuestion(), theCase);
			if (ans != null) {
				if ((!getQuestion().hasValue(theCase))
						| (!ans.equals(getQuestion().getValue(theCase)))) {
					Object[] values = new Object[1];
					values[0] = ans;
					theCase.setValue(getQuestion(), values);
				}
			} else {
				theCase.setValue(getQuestion(), new Object[0]);
			}

		} else if ((getQuestion() instanceof QuestionOC)
				&& (((QuestionOC) getQuestion()).getSchemaForQuestion() != null)) {
			// must take the value from hashtable, because it may be the result
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

		} else {
			List pros = getQuestion().getProReasons(theCase);
			Iterator iter = pros.iterator();
			while (iter.hasNext()) {
				QASet.Reason o = (QASet.Reason) iter.next();
				if (o.getRule() != null && (o.getRule().getAction() instanceof ActionAddValue)) {
					ActionAddValue r = (ActionAddValue) o.getRule().getAction();
					// TODO: do nothing here - remove the whole s**t when integrating the blackboard (joba)
					// r.addValue((Answer)null, theCase);
				}
			}
			theCase.setValue(getQuestion(), Collections.EMPTY_LIST.toArray(), getCorrespondingRule());
		}
	}

	private void removeRuleFromSymptomValueHistory(XPSCase theCase, Rule rule) {
		CaseQuestion q = (CaseQuestion) theCase.getCaseObject(getQuestion());
		Object o = q.getValueHistory();
		if ((o != null) && (o instanceof List)) {

			List l = ((List) o);

			if (l.size() >= 1) {
				if (rule.equals(((SymptomValue) (l.get(0))).getRule())) {
					l.remove(0);
				}
			}

		}
	}

	public RuleAction copy() {
		ActionAddValue a = new ActionAddValue();
		a.setRule(getCorrespondingRule());
		a.setQuestion(getQuestion());
		a.setValues(getValues());
		return a;
	}

    public boolean equals(Object o) {
        if (o==this) 
            return true;
        if (o instanceof ActionAddValue) {
            ActionAddValue a = (ActionAddValue)o;
            return (isSame(a.getQuestion(), getQuestion()) &&
                   Arrays.equals(a.getValues(), getValues()));
        }
        else
            return false;
    }

	private boolean isSame(Object obj1, Object obj2) {
		if(obj1 == null && obj2 == null) return true;
		if(obj1 != null && obj2 != null) return obj1.equals(obj2);
		return false;
	}
	
    public int hashCode() {
		int hash = 0;
		if(getQuestion() != null) hash += getQuestion().hashCode();
		if(getValues() != null) hash += getValues().hashCode();
        return hash; 
    }
}