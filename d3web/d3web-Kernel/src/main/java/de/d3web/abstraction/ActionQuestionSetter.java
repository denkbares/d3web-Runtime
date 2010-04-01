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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import de.d3web.abstraction.formula.FormulaDateElement;
import de.d3web.abstraction.formula.FormulaDateExpression;
import de.d3web.abstraction.formula.FormulaExpression;
import de.d3web.abstraction.formula.FormulaNumberElement;
import de.d3web.abstraction.inference.PSMethodQuestionSetter;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.PSAction;
import de.d3web.core.knowledge.terminology.Answer;
import de.d3web.core.knowledge.terminology.AnswerMultipleChoice;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QASet.Reason;
import de.d3web.core.session.CaseObjectSource;
import de.d3web.core.session.SymptomValue;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.blackboard.CaseActionQuestionSetter;
import de.d3web.core.session.blackboard.CaseQuestion;
import de.d3web.core.session.blackboard.XPSCaseObject;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.session.values.AnswerDate;
import de.d3web.core.session.values.AnswerNum;
import de.d3web.core.session.values.AnswerUnknown;
import de.d3web.core.session.values.EvaluatableAnswerNumValue;

/**
 * @author baumeister, bates
 */
public abstract class ActionQuestionSetter extends PSAction implements CaseObjectSource {

	private static final long serialVersionUID = 9036655281237588136L;
	private Question question;
	private Object[] values;

	public ActionQuestionSetter() {
		super();
	}

	/**
	 * @return all objects participating on the action.
	 */
	public List<? extends NamedObject> getTerminalObjects() {
		List<NamedObject> terminals = new ArrayList<NamedObject>(1);
		if (getQuestion() != null) {
			terminals.add(getQuestion());
		}
		return terminals;
	}

	/**
	 * sets the Question this action can set values for, removes the
	 * corresponding rule from the old question and inserts is to the new (as
	 * knowledge)
	 */
	public void setQuestion(Question question) {
		this.question = question;
	}

	/**
	 * @return the values this action can set to the question that is defined
	 */
	public Object[] getValues() {
		return values;
	}

	/**
	 * sets the values to set to the defined Question
	 */
	public void setValues(Object[] theValues) {
		values = theValues;
	}

	/**
	 * @return the Question this Action can set values to
	 */
	public Question getQuestion() {
		return question;
	}

	/**
	 * does nothing here in this abstract Action
	 */
	public void undo(XPSCase theCase) {
	}

	@Override
	public boolean hasChangedValue(XPSCase theCase) {

		Hashtable<Question, Object> questionToValuesHash = getActionValues(theCase);
		if ((questionToValuesHash != null) && (!questionToValuesHash.isEmpty())) {
			Enumeration<Question> keys = questionToValuesHash.keys();
			while (keys.hasMoreElements()) {
				Question q = (Question) keys.nextElement();
				// theCase.trace("key: " + q.getId());
				Object oldValue = questionToValuesHash.get(q); // can be Double
				// or Date
				assert (oldValue instanceof Double || oldValue instanceof Date) : "Unknown oldValue-Type: "
						+ oldValue;

				Answer newValues = null;
				if (q instanceof QuestionNum || q instanceof QuestionDate) {
					newValues = q.getValue(theCase);
				}
				else if (q instanceof QuestionOC) {
					newValues = new AnswerNum();
					if ((this.values != null) && (this.values.length > 0)) {
						EvaluatableAnswerNumValue evalAnsnumVal = (EvaluatableAnswerNumValue) this.values[0];
						Double value = evalAnsnumVal.eval(theCase);
						AnswerNum ansNum = new AnswerNum();
						ansNum.setQuestion(getQuestion());
						ansNum.setValue(value);
						newValues = ansNum;
					}

				}

				if (values != null) {
					if (oldValue == null)
						return true;
					Answer ans = newValues; // can be AnswerDate
					// or AnswerDouble
					assert (ans instanceof AnswerDate || ans instanceof AnswerNum) : "Unknown newValue-Answer-Type: "
							+ ans;
					Object newValue = ans.getValue(theCase); // can be Double or
					// Date
					assert (newValue instanceof Double || newValue instanceof Date) : "Unknown newValue-Type: "
							+ newValue;
					// theCase.trace("old:" + oldValue + ", new:" + newValue +
					// ": equals? "
					// + oldValue.equals(newValue));
					if (!oldValue.equals(newValue)) {
						return true;
					}
				}
				else {
					if (oldValue != null)
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * stores the current state of terminal objects of the given action values
	 */
	protected void storeActionValues(XPSCase theCase, Object[] valuesArg) {
		Hashtable<Question, Object> questionToValuesHash = new Hashtable<Question, Object>();
		// theCase.trace("attempting to store action values (elementary formulaExpression values)");
		if (valuesArg.length == 0) {
			return; // should only be one value!
		}

		Object obj = valuesArg[0];

		if ((obj instanceof FormulaExpression) || (obj instanceof FormulaNumberElement)
				|| (obj instanceof FormulaDateElement)
				|| (obj instanceof FormulaDateExpression)) {
			Collection<Object> terminalObjects;
			if (obj instanceof FormulaExpression) {
				terminalObjects = ((FormulaExpression) obj).getFormulaElement()
						.getTerminalObjects();
			}
			else if (obj instanceof FormulaNumberElement) {
				terminalObjects = ((FormulaNumberElement) obj).getTerminalObjects();
			}
			else if (obj instanceof FormulaDateExpression) {
				terminalObjects = ((FormulaDateExpression) obj).getFormulaDateElement()
						.getTerminalObjects();
			}
			else if (obj instanceof FormulaDateElement) {
				terminalObjects = ((FormulaDateElement) obj).getTerminalObjects();
			}
			else {
				throw new Error("Programmerror. Bad Type: " + obj);
			}

			Iterator<Object> iter = terminalObjects.iterator();
			while (iter.hasNext()) {
				Question q = (Question) iter.next();
				if (q instanceof QuestionNum) {
					QuestionNum qNum = (QuestionNum) q;
					Answer value = qNum.getValue(theCase);
					if (value != null) {
						Object val = value.getValue(theCase);
						questionToValuesHash.put(q, val);
					}
				}
				else if (q instanceof QuestionMC) {
					QuestionMC qMC = (QuestionMC) q;
					AnswerMultipleChoice value = (AnswerMultipleChoice) qMC.getValue(theCase);
					Double val = new Double(value.numberOfChoices());
					questionToValuesHash.put(q, val);
				}
				else if (q instanceof QuestionDate) {
					QuestionDate qDate = (QuestionDate) q;
					Answer value = qDate.getValue(theCase);
					if (value != null) {
						Object val = value.getValue(theCase);
						questionToValuesHash.put(q, val);
					}
				}
			}
		}
		else if (obj instanceof AnswerNum || obj instanceof AnswerDate) {
			Answer ans = (Answer) obj;
			Object val = ans.getValue(theCase);
			Question q = ans.getQuestion();
			if (q != null) {
				questionToValuesHash.put(q, val);
				// theCase.trace("put to hash: " + q.getId() + "; " + val);
			}
			else {
				theCase.trace("Question was null! Answer: " + ans);
				theCase.trace("taking Question from Action: " + question.getId());
				questionToValuesHash.put(question, val);
				// theCase.trace("put to hash: " + question.getId() + "; " +
				// val);
			}
		}

		setActionValues(theCase, questionToValuesHash);

	}

	/**
	 * this method is needed for protection from cycles in rule firing
	 */
	protected boolean lastFiredRuleEqualsCurrentRuleAndNotFired(XPSCase theCase) {
		Rule lastFiredRule = getLastFiredRule(theCase);
		if (lastFiredRule != null) {
			return !lastFiredRule.hasFired(theCase)
					&& lastFiredRule.getAction().equals(this);
		}
		else return false;
	}

	/**
	 * this method is needed for protection from cycles in rule firing
	 */
	protected Rule getLastFiredRule(XPSCase theCase) {
		CaseQuestion q = (CaseQuestion) theCase.getCaseObject(getQuestion());
		Object o = q.getValueHistory();
		if ((o != null) && (o instanceof List<?>)) {
			if (!((List<?>) o).isEmpty()) {
				SymptomValue v = (SymptomValue) ((List<?>) o).get(0);
				return v.getRule();
			}
			else return null;
		}
		else return null;
	}

	/**
	 * @return PSMethodQuestionSetter.class
	 */
	public Class<? extends PSMethod> getProblemsolverContext() {
		return PSMethodQuestionSetter.class;
	}

	/**
	 * @return the AnswerOC, that shall be set by an active Rule and that is
	 *         more severe than all other answers, that shall be set. (The
	 *         severeness is defined by the order of the alternatives; the first
	 *         answer is the severest.)
	 */
	protected Answer getSeverestAnswer(QuestionOC siQuestionOC, XPSCase theCase) {
		Answer severestAnswer = null;
		// use an array to accelerate
		Object[] allAnswers = siQuestionOC.getAllAlternatives().toArray();

		// go through all proreasons (only QASet.Reasons)
		Iterator<Reason> proIter = getQuestion().getProReasons(theCase).iterator();
		while (proIter.hasNext()) {
			Reason reason = proIter.next();
			Rule rule = reason.getRule();
			PSAction action = rule.getAction();
			if (action instanceof ActionQuestionSetter) {
				Object[] actionValues = ((ActionQuestionSetter) action).getValues();
				if ((actionValues[0] instanceof AnswerChoice)
						|| (actionValues[0] instanceof AnswerUnknown)) {
					// determine the more severe answer between the
					// newAnswer and the
					// up-to-now severest answer
					Answer newAnswer = (Answer) actionValues[0];
					if ((severestAnswer != null) && (!severestAnswer.equals(newAnswer))) {
						theCase.trace("(" + siQuestionOC.getId() + "): of \""
								+ ((AnswerChoice) severestAnswer).getName() + "\" and \""
								+ ((AnswerChoice) newAnswer).getName() + "\"");
						int i = 0;
						boolean found = false;
						while ((i < allAnswers.length) && (!found)) {
							if (severestAnswer.equals(allAnswers[i])) {
								found = true;
							}
							else if (newAnswer.equals(allAnswers[i])) {
								found = true;
								severestAnswer = newAnswer;
							}
							i++;
						}
						theCase.trace(" take \""
								+ ((AnswerChoice) severestAnswer).getName()
								+ "\"");
					}
					else {
						severestAnswer = (Answer) actionValues[0];
					}
				}
			}
		}
		return severestAnswer;
	}

	/**
	 * @see de.d3web.core.session.CaseObjectSource#createCaseObject(XPSCase)
	 */
	public XPSCaseObject createCaseObject(XPSCase session) {
		return new CaseActionQuestionSetter(this);
	}

	/**
	 * @return Hashtable
	 */
	public Hashtable<Question, Object> getActionValues(XPSCase theCase) {
		return ((CaseActionQuestionSetter) theCase.getCaseObject(this)).getActionValues();
	}

	/**
	 * @param hashtable
	 */
	public void setActionValues(XPSCase theCase, Hashtable<Question, Object> hashtable) {
		((CaseActionQuestionSetter) theCase.getCaseObject(this)).setActionValues(hashtable);
	}

	/**
	 * @return Double
	 */
	public Double getLastSetValue(XPSCase theCase) {
		return ((CaseActionQuestionSetter) theCase.getCaseObject(this)).getLastSetValue();
	}

	/**
	 * @param theCase
	 * @param newValue
	 */
	public void setLastSetValue(XPSCase theCase, Double newValue) {
		((CaseActionQuestionSetter) theCase.getCaseObject(this)).setLastSetValue(newValue);
	}
}