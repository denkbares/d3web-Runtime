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

package de.d3web.kernel.domainModel.qasets;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.Num2ChoiceSchema;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.dynamicObjects.CaseQuestionChoice;
import de.d3web.kernel.psMethods.questionSetter.PSMethodQuestionSetter;
import de.d3web.kernel.supportknowledge.Property;
import de.d3web.kernel.utilities.Tester;
import de.d3web.kernel.utilities.Utils;

/**
 * Storage for Questions with predefined answers (alternatives).
 * Abstract because you can choose from multiple/single choices (answers).<BR>
 * Part of the Composite design pattern (see QASet for further description)
 * 
 * @author joba, Christian Betz
 * @see QASet
 */
public abstract class QuestionChoice extends Question {
	protected List<AnswerChoice> alternatives;

	public QuestionChoice() {
		super();
		this.setAlternatives(new LinkedList());
	}
	
	public QuestionChoice(String id) {
		super(id);
		this.setAlternatives(new LinkedList());
	}

	/**
	 * Gives you all the answers (alternatives) and does not
	 * care about any rules which could possibly suppress an answer.
	 * @param theCase currentCase
	 * @return a List of all alternatives that are not suppressed by any RuleSuppress
	 **/
	public List<AnswerChoice> getAllAlternatives() {
		return alternatives;
	}

	private Answer findAlternative(List alternativesArg, final String id) {
		return (Answer) Utils.findIf(alternativesArg, new Tester() {
			public boolean test(Object testObj) {
				if ((testObj instanceof AnswerChoice)
					&& (((AnswerChoice) testObj).getId().equalsIgnoreCase(id))) {
					return true;
				} else {
					return false;
				}
			}
		});
	}

	/**
	 * if theCase == null, find the alternative in all alternatives,
	 * else find the alternative in all currently (depend on the case) available alternatives
	 * @return Answer (either instanceof AnswerChoice or AnswerUnknown)
	 **/
	public Answer getAnswer(XPSCase theCase, String id) {
		if (id == null)
			return null;
		if (theCase == null)
			return findAlternative(alternatives, id);
		else
			return findAlternative(getAlternatives(theCase), id);
	}

	/**
	 * Gives you only the possible answers (alternatives) which
	 * are not suppressed by any rule.
	 *	@param theCase currentCase
	 *	@return a Vector of all alternatives that are not suppressed by any RuleSuppress
	 **/
	public List<Answer> getAlternatives(XPSCase theCase) {
		CaseQuestionChoice caseQ = (CaseQuestionChoice) theCase.getCaseObject(this);
		List<Answer> suppVec = caseQ.getMergedSuppressAlternatives();
		List<Answer> result = new LinkedList<Answer>();
        Iterator<AnswerChoice> e = alternatives.iterator();
		while (e.hasNext()) {
			Answer elem = e.next();
			if (!suppVec.contains(elem))
				result.add(elem);
		}
		return result;
	}

	public abstract List getValue(XPSCase theCase);


	/**
	 * sets the answer alternatives from which a user or rule can
	 * choose one or more to answer this question.
	 */
	public void setAlternatives(List alternatives) {
		if (alternatives != null) {
			this.alternatives = alternatives;
			Iterator iter = this.alternatives.iterator();
			while (iter.hasNext()) {
				((Answer) iter.next()).setQuestion(this);
			}
		} else
			setAlternatives(new LinkedList());

	}
    
    public void addAlternative(AnswerChoice answer) {
        if ((answer != null) && (!getAllAlternatives().contains(answer))) {
            alternatives.add(answer);
            answer.setQuestion(this);
        }
    }

	public abstract void setValue(XPSCase theCase, Object[] values);

	public String toString() {
		String res = super.toString();
		return res;
	}

	public String verbalizeWithoutValue(XPSCase theCase) {
		String res = "\n " + super.toString();
		Iterator iter = getAlternatives(theCase).iterator();
		while (iter.hasNext())
			res += "\n  " + iter.next().toString();
		return res;
	}

	public String verbalizeWithValue(XPSCase theCase) {
		return verbalizeWithoutValue(theCase)
			+ "\n Wert -> "
			+ getValue(theCase);
	}

	/**
	 * @return the current numerical value of the question 
	 * according to a give XPSCase. This value is used to
	 * be processed by a Num2ChoiceSchema.
	 */
	public Double getNumericalSchemaValue(XPSCase theCase) {
		return ((CaseQuestionChoice) theCase.getCaseObject(this)).getNumericalSchemaValue();
	}


	private void setNumericalSchemaValue(XPSCase theCase, Double newValue) {
		((CaseQuestionChoice) theCase.getCaseObject(this)).setNumericalSchemaValue(newValue);
	}
	
	/**
	 * @return the Num2ChoiceSchema that has been set to this question, null, if no such schema exists.
	 */
	public Num2ChoiceSchema getSchemaForQuestion() {
		Collection schemaCol =
			getKnowledge(PSMethodQuestionSetter.class, PSMethodQuestionSetter.NUM2CHOICE_SCHEMA);
		if ((schemaCol != null) && (!schemaCol.isEmpty())) {
			return (Num2ChoiceSchema) schemaCol.toArray()[0];
		} else {
			return null;
		}
	}
	
	protected Answer convertNumericalValue(XPSCase theCase, AnswerNum value) {
		Num2ChoiceSchema schema = getSchemaForQuestion();
		if (schema != null) {
			double newValue = ((Double) (value.getValue(theCase))).doubleValue();
			Double numValue = null;
			if (Boolean.TRUE.equals(getProperties().getProperty(Property.TIME_VALUED))) {
				numValue = new Double(newValue);
			} else {
				numValue = new Double(getNumericalSchemaValue(theCase).doubleValue() + newValue);
			}
			setNumericalSchemaValue(theCase, numValue);
			Answer answer = schema.getAnswerForNum(numValue, getAllAlternatives(), theCase);
			return answer;
		} else {
			Logger.getLogger(this.getClass().getName()).throwing(
				this.getClass().getName(),
				"convertNumericalValue",
					new RuntimeException("No Num2ChoiceSchema defined for " + getId() + ":"
							+ getText()));
			return value;
		}
	}
	
	public Answer getAnswer(XPSCase theCase, Double value) {
		if (value == null)
			return null;
		else {
			Num2ChoiceSchema schema = getSchemaForQuestion();
			if (schema != null) {
				Answer answer = schema.getAnswerForNum(value, getAllAlternatives(), theCase);
				answer.setQuestion(this);
				return answer;
			} else
				return null;
		}
	}	
}