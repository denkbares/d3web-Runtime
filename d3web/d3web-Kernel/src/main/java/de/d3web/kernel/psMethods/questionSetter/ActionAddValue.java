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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.RuleAction;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.SymptomValue;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerDate;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.answers.EvaluatableAnswerDateValue;
import de.d3web.kernel.domainModel.answers.EvaluatableAnswerNumValue;
import de.d3web.kernel.domainModel.formula.FormulaDateExpression;
import de.d3web.kernel.domainModel.formula.FormulaExpression;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.kernel.domainModel.qasets.QuestionOC;
import de.d3web.kernel.dynamicObjects.CaseQuestion;
import de.d3web.kernel.psMethods.MethodKind;

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
	public ActionAddValue(RuleComplex theCorrespondingRule) {
		super(theCorrespondingRule);
	}

	private List addValue(List currentValues, AnswerChoice val) {
		List newValues = currentValues;
		if (currentValues == null) {
			newValues = new LinkedList();
			newValues.add(val);
		} else if (currentValues.isEmpty()) {
			newValues.add(val);
		} else if (getQuestion() instanceof QuestionOC) {
			newValues = new LinkedList();
			newValues.add(val);
		} else if (!newValues.contains(val)) {
			newValues.add(val);

		}
		return newValues;
	}

	private List addValue(List currentValues, AnswerDate val) {
		// does the same like an ActionSetValue-Object
		if (currentValues == null || !currentValues.isEmpty()) {
			List newValues = new LinkedList();
			newValues.add(val);
			return newValues;
		} else {
			currentValues.add(val);
			return currentValues;
		}
	}

	private List addValue(List currentVals, AnswerNum val, XPSCase theCase) {
		if (currentVals != null) {
			if (currentVals.size() == 1) {
				Answer ans = (Answer) currentVals.get(0);
				if (ans instanceof AnswerNum) {
					Double ansval = (Double) ((AnswerNum) ans).getValue(theCase);
					Double newValue = new Double(ansval.doubleValue()
							+ ((Double) val.getValue(theCase)).doubleValue());
					AnswerNum ansnum = new AnswerNum();
					ansnum.setQuestion(getQuestion());
					ansnum.setValue(newValue);
					currentVals.remove(0); // there's only one
					currentVals.add(ansnum);
				}
			} else if (currentVals.isEmpty()) {
				currentVals.add(val);
			}
		} else {
			currentVals = new LinkedList();
			currentVals.add(val);
		}

		if ((getQuestion() instanceof QuestionOC)
				&& (((QuestionOC) getQuestion()).getSchemaForQuestion() != null)) {
			setLastSetValue(theCase, (Double) val.getValue(theCase));
		}

		return currentVals;
	}

	private List addValue(List currentVals, EvaluatableAnswerNumValue val, XPSCase theCase) {

		AnswerNum ans = new AnswerNum();
		ans.setQuestion(getQuestion());
		ans.setValue(val);

		return addValue(currentVals, ans, theCase);
	}

	private List addValue(List currentVals, EvaluatableAnswerDateValue val) {

		AnswerDate ans = new AnswerDate();
		ans.setQuestion(getQuestion());
		ans.setValue(val);

		return addValue(currentVals, ans);
	}

	private List addValue(List currentVals, FormulaExpression val, XPSCase theCase) {
		Answer evalAns = val.eval(theCase);

		if (evalAns instanceof AnswerChoice) {
			return addValue(currentVals, (AnswerChoice) evalAns);
		} else if (evalAns instanceof AnswerNum) {
			return addValue(currentVals, (AnswerNum) evalAns, theCase);
		} else
			return null;
	}

	private List addValue(List currentVals, FormulaDateExpression val, XPSCase theCase) {
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
	private List addValues(List currentValues, XPSCase theCase) {
		Object[] val = getValues();
		if (val != null) {
			for (int i = 0; i < val.length; ++i) {
				if (val[i] instanceof AnswerNum) {
					currentValues = addValue(currentValues, (AnswerNum) val[i], theCase);
				} else if (val[i] instanceof AnswerChoice) {
					currentValues = addValue(currentValues, (AnswerChoice) val[i]);
				} else if (val[i] instanceof FormulaExpression) {
					currentValues = addValue(currentValues, (FormulaExpression) val[i], theCase);
				} else if (val[i] instanceof EvaluatableAnswerNumValue) {
					currentValues = addValue(currentValues, (EvaluatableAnswerNumValue) val[i],
							theCase);
				} else if (val[i] instanceof AnswerDate) {
					currentValues = addValue(currentValues, (AnswerDate) val[i]);
				} else if (val[i] instanceof FormulaDateExpression) {
					currentValues = addValue(currentValues, (FormulaDateExpression) val[i], theCase);
				} else if (val[i] instanceof EvaluatableAnswerDateValue) {
					currentValues = addValue(currentValues, (EvaluatableAnswerDateValue) val[i]);
				}
			}
		}
		return currentValues;
	}

	/**
	 * Returns a list which contains an AnswerNum with the value to add.
	 */
	private List addSchemaValue(QuestionChoice q, XPSCase theCase) {
		Object[] val = getValues();
		List resultList = new LinkedList();
		if (val != null) {
			if (val[0] instanceof FormulaExpression) {
				AnswerNum ans = (AnswerNum) ((FormulaExpression) val[0]).eval(theCase);
				setLastSetValue(theCase, (Double) ans.getValue(theCase));
				ans.setValue((Double) ans.getValue(theCase));
				resultList.add(ans);
			} else if (val[0] instanceof EvaluatableAnswerNumValue) {
				AnswerNum ans = new AnswerNum();
				ans.setQuestion(getQuestion());
				EvaluatableAnswerNumValue evaluatableValue = (EvaluatableAnswerNumValue) val[0];

				// put the evaluated value to hashtable for undo
				Double newValue = evaluatableValue.eval(theCase);

				setLastSetValue(theCase, newValue);

				ans.setValue(evaluatableValue);
				resultList.add(ans);
			} else if (val[0] instanceof AnswerNum) {
				Double ansValue = (Double) ((AnswerNum) val[0]).getValue(theCase);
				setLastSetValue(theCase, ansValue);
				AnswerNum newAnswer = new AnswerNum();
				newAnswer.setQuestion(getQuestion());
				newAnswer.setValue(ansValue);
				resultList.add(newAnswer);
			}
		}
		return resultList;
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

			List resultList;

			if ((getQuestion() instanceof QuestionOC)
					&& (((QuestionOC) getQuestion()).getSchemaForQuestion() != null)
					&& (getValues().length > 0)
					&& (!(getValues()[0] instanceof AnswerChoice))) {
				resultList = addSchemaValue((QuestionChoice) getQuestion(), theCase);
			} else {
				resultList = addValues(getQuestion().getValue(theCase), theCase);
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
								.getValue(theCase).get(0))))) {
					Object[] newValues = new Object[1];
					newValues[0] = ans;
					theCase.setValue(getQuestion(), newValues);
				}
			} else {
				// else, set the resultList-values
				theCase.setValue(getQuestion(), resultList.toArray(), getCorrespondingRule());
			}
		}
	}

	/**
	 * @return false.
	 */
	public boolean singleFire() {
		return false;
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
						| (!ans.equals(getQuestion().getValue(theCase).get(0)))) {
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
			List newVals = new LinkedList();
			Iterator iter = pros.iterator();
			while (iter.hasNext()) {
				QASet.Reason o = (QASet.Reason) iter.next();
				if (o.getRule() != null && (o.getRule().getAction() instanceof ActionAddValue)) {
					ActionAddValue r = (ActionAddValue) o.getRule().getAction();
					r.addValues(newVals, theCase);
				}
			}
			theCase.setValue(getQuestion(), newVals.toArray(), getCorrespondingRule());
		}
	}

	private void removeRuleFromSymptomValueHistory(XPSCase theCase, RuleComplex rule) {
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
		ActionAddValue a = new ActionAddValue(getCorrespondingRule());
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