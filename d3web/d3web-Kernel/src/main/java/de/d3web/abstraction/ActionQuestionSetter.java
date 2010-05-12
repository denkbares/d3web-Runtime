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
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.Rule;
import de.d3web.core.knowledge.terminology.Answer;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QASet.Reason;
import de.d3web.core.session.CaseObjectSource;
import de.d3web.core.session.Session;
import de.d3web.core.session.SymptomValue;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.CaseActionQuestionSetter;
import de.d3web.core.session.blackboard.CaseQuestion;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.values.Choice;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.EvaluatableAnswerNumValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;

/**
 * @author baumeister, bates
 */
public abstract class ActionQuestionSetter extends PSAction implements CaseObjectSource {

	private Question question;
	private Object value;

	public ActionQuestionSetter() {
		super();
	}

	/**
	 * @return all objects participating on the action.
	 */
	@Override
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
	public Object getValue() {
		return this.value;
	}

	/**
	 * sets the values to set to the defined Question
	 */
	public void setValue(Object theValue) {
		this.value = theValue;
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
	public void undo(Session theCase) {
	}

	@Override
	public boolean hasChangedValue(Session session) {

		Hashtable<Question, Object> questionToValuesHash = getActionValues(session);
		if ((questionToValuesHash != null) && (!questionToValuesHash.isEmpty())) {
			Enumeration<Question> keys = questionToValuesHash.keys();
			while (keys.hasMoreElements()) {
				Question q = keys.nextElement();
				// theCase.trace("key: " + q.getId());
				Object oldValue = questionToValuesHash.get(q); // can be Double
				// or Date
				assert (oldValue instanceof Double || oldValue instanceof Date) : "Unknown oldValue-Type: "
						+ oldValue;

				Value newValue = null;
				if (q instanceof QuestionNum || q instanceof QuestionDate) {
					newValue = session.getBlackboard().getValue(q);
				}
				else if (q instanceof QuestionOC) {
					if (this.value != null) {
						EvaluatableAnswerNumValue evalAnsnumVal = (EvaluatableAnswerNumValue) this.value;
						Double value = evalAnsnumVal.eval(session);
						newValue = new NumValue(value);
					}
				}

				if (this.value != null) {
					if (oldValue == null) return true;
					Value val = newValue; // can be AnswerDate
					// or AnswerDouble
					assert (val instanceof DateValue || val instanceof NumValue) : "Unknown newValue-Answer-Type: "
							+ val;
					Object updatedVal = val.getValue(); // can be Double or
					// Date
					assert (updatedVal instanceof Double || updatedVal instanceof Date) : "Unknown newValue-Type: "
							+ updatedVal;
					// theCase.trace("old:" + oldValue + ", new:" + newValue +
					// ": equals? "
					// + oldValue.equals(newValue));
					if (!oldValue.equals(updatedVal)) {
						return true;
					}
				}
				else {
					if (oldValue != null) return true;
				}
			}
		}
		return false;
	}

	/**
	 * stores the current state of terminal objects of the given action values
	 */
	protected void storeActionValues(Session theCase, Object valuesArg) {
		Hashtable<Question, Object> questionToValuesHash = new Hashtable<Question, Object>();
		// theCase.trace("attempting to store action values (elementary formulaExpression values)");
		if (valuesArg == null) {
			return; // should only be one value!
		}

		Object obj = valuesArg;

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
					Value value = theCase.getBlackboard().getValue(qNum);
					if (value != null) {
						Object val = value.getValue();
						questionToValuesHash.put(q, val);
					}
				}
				else if (q instanceof QuestionMC) {
					QuestionMC qMC = (QuestionMC) q;
					Double val = null;
					Value value = theCase.getBlackboard().getValue(qMC);
					if (value instanceof MultipleChoiceValue) {
						List<ChoiceValue> l = (List<ChoiceValue>) (value.getValue());
						val = new Double(l.size());
					}
					else {
						val = new Double(0);
					}
					questionToValuesHash.put(q, val);
				}
				else if (q instanceof QuestionDate) {
					QuestionDate qDate = (QuestionDate) q;
					Value value = theCase.getBlackboard().getValue(qDate);
					if (value != null && value instanceof DateValue) {
						Object val = value.getValue();
						questionToValuesHash.put(q, val);
					}
				}
			}
		}
		else if (obj instanceof NumValue || obj instanceof DateValue) {
			Answer ans = (Answer) obj;
			Object val = ans.getValue(theCase);
			Question q = ans.getQuestion();
			if (q != null) {
				questionToValuesHash.put(q, val);
				// theCase.trace("put to hash: " + q.getId() + "; " + val);
			}
			else {
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
	protected boolean lastFiredRuleEqualsCurrentRuleAndNotFired(Session theCase) {
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
	protected Rule getLastFiredRule(Session theCase) {
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
	@Override
	public Class<? extends PSMethod> getProblemsolverContext() {
		return PSMethodQuestionSetter.class;
	}

	/**
	 * @return the AnswerOC, that shall be set by an active Rule and that is
	 *         more severe than all other answers, that shall be set. (The
	 *         severeness is defined by the order of the alternatives; the first
	 *         answer is the severest.)
	 */
	protected Choice getSeverestAnswer(QuestionOC siQuestionOC, Session theCase) {
		Choice severestAnswer = null;
		// use an array to accelerate
		Object[] allAnswers = siQuestionOC.getAllAlternatives().toArray();

		// go through all proreasons (only QASet.Reasons)
		Iterator<Reason> proIter = getQuestion().getProReasons(theCase).iterator();
		while (proIter.hasNext()) {
			Reason reason = proIter.next();
			Rule rule = reason.getRule();
			PSAction action = rule.getAction();
			if (action instanceof ActionQuestionSetter) {
				Object actionValue = ((ActionQuestionSetter) action).getValue();
				if (actionValue instanceof ChoiceValue) {
					// determine the more severe answer between the
					// newAnswer and the
					// up-to-now severest answer
					Choice choice = (Choice) ((ChoiceValue) actionValue).getValue();

					if ((severestAnswer != null) && (!severestAnswer.equals(choice))) {
						int i = 0;
						boolean found = false;
						while ((i < allAnswers.length) && (!found)) {
							if (severestAnswer.equals(allAnswers[i])) {
								found = true;
							}
							else if (choice.equals(allAnswers[i])) {
								found = true;
								severestAnswer = choice;
							}
							i++;
						}
					}
					else {
						severestAnswer = choice;
						// severestAnswer = (Answer) actionValues[0];
					}
				}
			}
		}
		return severestAnswer;
	}

	/**
	 * @see de.d3web.core.session.CaseObjectSource#createCaseObject(Session)
	 */
	public SessionObject createCaseObject(Session session) {
		return new CaseActionQuestionSetter(this);
	}

	/**
	 * @return Hashtable
	 */
	public Hashtable<Question, Object> getActionValues(Session theCase) {
		return ((CaseActionQuestionSetter) theCase.getCaseObject(this)).getActionValues();
	}

	/**
	 * @param hashtable
	 */
	public void setActionValues(Session theCase, Hashtable<Question, Object> hashtable) {
		((CaseActionQuestionSetter) theCase.getCaseObject(this)).setActionValues(hashtable);
	}

	/**
	 * @return Double
	 */
	public Double getLastSetValue(Session theCase) {
		return ((CaseActionQuestionSetter) theCase.getCaseObject(this)).getLastSetValue();
	}

	/**
	 * @param theCase
	 * @param newValue
	 */
	public void setLastSetValue(Session theCase, Double newValue) {
		((CaseActionQuestionSetter) theCase.getCaseObject(this)).setLastSetValue(newValue);
	}
}