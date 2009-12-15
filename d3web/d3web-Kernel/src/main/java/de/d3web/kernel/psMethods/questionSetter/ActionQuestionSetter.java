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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.CaseObjectSource;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.RuleAction;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.SymptomValue;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerDate;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.answers.AnswerUnknown;
import de.d3web.kernel.domainModel.answers.EvaluatableAnswerNumValue;
import de.d3web.kernel.domainModel.formula.FormulaDateElement;
import de.d3web.kernel.domainModel.formula.FormulaDateExpression;
import de.d3web.kernel.domainModel.formula.FormulaExpression;
import de.d3web.kernel.domainModel.formula.FormulaNumberElement;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionDate;
import de.d3web.kernel.domainModel.qasets.QuestionMC;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.domainModel.qasets.QuestionOC;
import de.d3web.kernel.dynamicObjects.CaseActionQuestionSetter;
import de.d3web.kernel.dynamicObjects.CaseQuestion;
import de.d3web.kernel.dynamicObjects.XPSCaseObject;
import de.d3web.kernel.psMethods.MethodKind;

/**
 * @author baumeister, bates
 */
public abstract class ActionQuestionSetter extends RuleAction implements CaseObjectSource {
	private Question question;
	private Object[] values;

	public ActionQuestionSetter(RuleComplex theCorrespondingRule) {
		super(theCorrespondingRule);
	}

	/**
	 * @return all objects participating on the action.
	 */
	public List getTerminalObjects() {
		List terminals = new ArrayList(1);
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
		removeRuleFromOldQuestion(this.question);
		this.question = question;
		insertRuleIntoQuestion(this.question);
	}

	private void insertRuleIntoQuestion(Question questionArg) {
		if (questionArg != null)
			questionArg.addKnowledge(getProblemsolverContext(), getCorrespondingRule(),
					MethodKind.BACKWARD);
	}

	private void removeRuleFromOldQuestion(Question questionArg) {
		if (questionArg != null)
			questionArg.removeKnowledge(getProblemsolverContext(), getCorrespondingRule(),
					MethodKind.BACKWARD);
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

	/**
	 * checks if any action value (e.g. terminal objects of a formula) have
	 * changed this Method is public, because it is used in
	 * RuleComplex.check(..)
	 * 
	 * @see RuleComplex
	 */
	public boolean actionValuesChanged(XPSCase theCase) {

		Hashtable questionToValuesHash = getActionValues(theCase);
		if ((questionToValuesHash != null) && (!questionToValuesHash.isEmpty())) {
			Enumeration keys = questionToValuesHash.keys();
			while (keys.hasMoreElements()) {
				Question q = (Question) keys.nextElement();
				//theCase.trace("key: " + q.getId());
				Object oldValue = questionToValuesHash.get(q); // can be Double
															   // or Date
				assert (oldValue instanceof Double || oldValue instanceof Date) : "Unknown oldValue-Type: "
						+ oldValue;

				List newValues = null;
				if (q instanceof QuestionNum || q instanceof QuestionDate) {
					newValues = q.getValue(theCase);
				} else if (q instanceof QuestionOC) {
					newValues = new LinkedList();
					if ((this.values != null) && (this.values.length > 0)) {
						EvaluatableAnswerNumValue evalAnsnumVal = (EvaluatableAnswerNumValue) this.values[0];
						Double value = evalAnsnumVal.eval(theCase);
						AnswerNum ansNum = new AnswerNum();
						ansNum.setQuestion(getQuestion());
						ansNum.setValue(value);
						newValues.add(ansNum);
					}

				}

				if ((values != null) && !newValues.isEmpty()) {
					if (oldValue == null)
						return true;
					Answer ans = (Answer) newValues.get(0); // can be AnswerDate
															// or AnswerDouble
					assert (ans instanceof AnswerDate || ans instanceof AnswerNum) : "Unknown newValue-Answer-Type: "
							+ ans;
					Object newValue = ans.getValue(theCase); // can be Double or
															 // Date
					assert (newValue instanceof Double || newValue instanceof Date) : "Unknown newValue-Type: "
							+ newValue;
					//theCase.trace("old:" + oldValue + ", new:" + newValue + ": equals? "
					//		+ oldValue.equals(newValue));
					if (!oldValue.equals(newValue)) {
						return true;
					}
				} else {
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
		Hashtable questionToValuesHash = new Hashtable();
		//theCase.trace("attempting to store action values (elementary formulaExpression values)");
		if (valuesArg.length == 0) {
			return; // should only be one value!
		}

		Object obj = valuesArg[0];

		if ((obj instanceof FormulaExpression) || (obj instanceof FormulaNumberElement)
				|| (obj instanceof FormulaDateElement) || (obj instanceof FormulaDateExpression)) {
			Collection terminalObjects;
			if (obj instanceof FormulaExpression) {
				terminalObjects = ((FormulaExpression) obj).getFormulaElement()
						.getTerminalObjects();
			} else if (obj instanceof FormulaNumberElement) {
				terminalObjects = ((FormulaNumberElement) obj).getTerminalObjects();
			} else if (obj instanceof FormulaDateExpression) {
				terminalObjects = ((FormulaDateExpression) obj).getFormulaDateElement()
						.getTerminalObjects();
			} else if (obj instanceof FormulaDateElement) {
				terminalObjects = ((FormulaDateElement) obj).getTerminalObjects();
			} else {
				throw new Error("Programmerror. Bad Type: " + obj);
			}

			Iterator iter = terminalObjects.iterator();
			while (iter.hasNext()) {
				Question q = (Question) iter.next();
				if (q instanceof QuestionNum) {
					QuestionNum qNum = (QuestionNum) q;
					List value = qNum.getValue(theCase);
					if ((value != null) && !value.isEmpty()) {
						Answer ans = (Answer) value.get(0);
						Object val = ans.getValue(theCase);
						questionToValuesHash.put(q, val);
						//theCase.trace("put to hash: " + q.getId() + "; " + val);
					}
				} else if (q instanceof QuestionMC) {
					QuestionMC qMC = (QuestionMC) q;
					List value = qMC.getValue(theCase);
					Double val = new Double(value.size());
					questionToValuesHash.put(q, val);
					//theCase.trace("put to hash: " + q.getId() + "; " + val);
				} else if (q instanceof QuestionDate) {
					QuestionDate qDate = (QuestionDate) q;
					List value = qDate.getValue(theCase);
					if ((value != null) && (!value.isEmpty())) {
						Answer ans = (Answer) value.get(0);
						Object val = ans.getValue(theCase);
						questionToValuesHash.put(q, val);
						//theCase.trace("put to hash: " + q.getId() + "; " + val);
					}
				}
			}
		} else if (obj instanceof AnswerNum || obj instanceof AnswerDate) {
			Answer ans = (Answer) obj;
			Object val = ans.getValue(theCase);
			Question q = ans.getQuestion();
			if (q != null) {
				questionToValuesHash.put(q, val);
				//theCase.trace("put to hash: " + q.getId() + "; " + val);
			} else {
				theCase.trace("Question was null! Answer: " + ans);
				theCase.trace("taking Question from Action: " + question.getId());
				questionToValuesHash.put(question, val);
				//theCase.trace("put to hash: " + question.getId() + "; " + val);
			}
		}

		setActionValues(theCase, questionToValuesHash);

	}

	/**
	 * this method is needed for protection from cycles in rule firing
	 */
	protected boolean lastFiredRuleEqualsCurrentRuleAndNotFired(XPSCase theCase) {
		if (getLastFiredRule(theCase) != null) {
			return !getLastFiredRule(theCase).hasFired(theCase)
					&& getCorrespondingRule().equals(getLastFiredRule(theCase));
		} else
			return false;
	}
	/**
	 * this method is needed for protection from cycles in rule firing
	 */
	protected RuleComplex getLastFiredRule(XPSCase theCase) {
		CaseQuestion q = (CaseQuestion) theCase.getCaseObject(getQuestion());
		Object o = q.getValueHistory();
		if ((o != null) && (o instanceof List)) {
			if (!((List) o).isEmpty()) {
				SymptomValue v = (SymptomValue) ((List) o).get(0);
				return v.getRule();
			} else
				return null;
		} else
			return null;
	}

	/**
	 * @return PSMethodQuestionSetter.class
	 */
	public Class getProblemsolverContext() {
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
		Iterator proIter = getQuestion().getProReasons(theCase).iterator();
		while (proIter.hasNext()) {
			Object reason = proIter.next();
			if (reason instanceof QASet.Reason) {
				RuleComplex rule = ((QASet.Reason) reason).getRule();
				RuleAction action = rule.getAction();
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
									+ ((AnswerChoice) severestAnswer).getText() + "\" and \""
									+ ((AnswerChoice) newAnswer).getText() + "\"");
							int i = 0;
							boolean found = false;
							while ((i < allAnswers.length) && (!found)) {
								if (severestAnswer.equals(allAnswers[i])) {
									found = true;
								} else if (newAnswer.equals(allAnswers[i])) {
									found = true;
									severestAnswer = newAnswer;
								}
								i++;
							}
							theCase.trace(" take \"" + ((AnswerChoice) severestAnswer).getText()
									+ "\"");
						} else {
							severestAnswer = (Answer) actionValues[0];
						}
					}
				}
			}
		}
		return severestAnswer;
	}

	/**
	 * @see de.d3web.kernel.domainModel.CaseObjectSource#createCaseObject(XPSCase)
	 */
	public XPSCaseObject createCaseObject(XPSCase session) {
		return new CaseActionQuestionSetter(this);
	}

	/*
	 * @see de.d3web.kernel.domainModel.RuleAction#doIt(de.d3web.kernel.XPSCase)
	 */
	public void doIt(XPSCase theCase) {
	}

	/**
	 * @return Hashtable
	 */
	public Hashtable getActionValues(XPSCase theCase) {
		return ((CaseActionQuestionSetter) theCase.getCaseObject(this)).getActionValues();
	}

	/**
	 * @param hashtable
	 */
	public void setActionValues(XPSCase theCase, Hashtable hashtable) {
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