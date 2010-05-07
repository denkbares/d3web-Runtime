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

package de.d3web.core.knowledge.terminology;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import de.d3web.abstraction.inference.PSMethodQuestionSetter;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.Rule;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.Session;
import de.d3web.core.session.SymptomValue;
import de.d3web.core.session.Value;
import de.d3web.core.session.ValuedObject;
import de.d3web.core.session.blackboard.CaseQuestion;
import de.d3web.core.session.values.AnswerUnknown;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.indication.inference.PSMethodNextQASet;

/**
 * This is an abstract class, that stores the static parts of a question
 * (symptom, input) independent from the dynamic session-specific values. This
 * class is part of the applied design pattern <i>Composite</i> (including
 * {@link QASet} and {@link QContainer}).
 * 
 * @author joba, norman
 * @see QASet
 * @see DerivationType
 */
public abstract class Question extends QASet {
	
	private final AnswerUnknown unknown;

	/**
	 * Creates a new {@link Question} instance with the specified unique
	 * identifier.
	 * 
	 * @param id
	 *            the specified unique identifier
	 */
	public Question(String id) {
		super(id);
		// create "unknown"-alternative
		unknown = new AnswerUnknown();
		unknown.setQuestion(this);
	}

	@Override
	public void addContraReason(Reason source, Session theCase) {
		((CaseQuestion) theCase.getCaseObject(this)).addContraReason(source);
	}

	@Override
	public void addProReason(Reason source, Session theCase) {
		((CaseQuestion) theCase.getCaseObject(this)).addProReason(source);
	}

	@Override
	public List<Reason> getContraReasons(Session theCase) {
		return ((CaseQuestion) theCase.getCaseObject(this)).getContraReasons();
	}

	/**
	 * DerivationType ({BASIC, DERIVED, MIXED}) means: <LI>BASIC: question
	 * should be asked in a basic dialogue <LI>DERIVED: question should not be
	 * asked, but is set by a RuleSymptom <LI>MIXED: both, should be asked but
	 * can also be derived
	 * 
	 * @return de.d3web.kernel.domainModel.DerivationType a static instance of
	 *         DerivationType
	 */
	public DerivationType getDerivationType() {
		final Class<? extends PSMethod> QUESTION_SETTER = PSMethodQuestionSetter.class;
		final Class<? extends PSMethod> FOLLOW_QUESTION = PSMethodNextQASet.class;
		final MethodKind KIND = MethodKind.BACKWARD;
		if (hasElements(getKnowledge(QUESTION_SETTER, KIND))
				&& hasElements(getKnowledge(FOLLOW_QUESTION, KIND))) return DerivationType.MIXED;
		else if (hasElements(getKnowledge(QUESTION_SETTER, KIND))) return DerivationType.DERIVED;
		else return DerivationType.BASIC;
	}

	private boolean hasElements(Object list) {
		if (list == null) return false;
		else if ((list instanceof Collection<?>) && ((Collection<?>) list).isEmpty()) return false;
		else return true;
	}

	@Override
	public List<Reason> getProReasons(Session theCase) {
		return ((CaseQuestion) theCase.getCaseObject(this)).getProReasons();
	}

	/**
	 * We do not use AnswerUnknown anymore, but we can set {@link Unknown} in a
	 * given session.
	 */
	@Deprecated
	public AnswerUnknown getUnknownAlternative() {
		return unknown;
	}

	/**
	 * Returns the value of the question given in the specified {@link Session};
	 * returns {@link UndefinedValue} if no value is assigned.
	 * 
	 * @param theCase
	 *            the given {@link Session}
	 * @return {@link UndefinedValue} if no value is assigned, the actual value
	 *         otherwise.
	 * @author joba
	 * @date 07.04.2010
	 */
	@Deprecated
	public abstract Value getValue(Session theCase);

	@Override
	public boolean hasValue(Session session) {
		return !UndefinedValue.isUndefinedValue(session.getValue(this));
	}

	@Override
	public boolean isDone(Session theCase) {
		if (!getContraReasons(theCase).isEmpty()) {
			// Question has ContraIndication (probably)
			return true;
		}
		else {
			return !UndefinedValue.isUndefinedValue(theCase.getValue(this));
			// (getValue(theCase) != null && theCase.getValue(this) !=
			// UndefinedValue.getInstance());
		}
	}

	@Override
	public boolean isDone(Session theCase, boolean respectValidFollowQuestions) {
		if (respectValidFollowQuestions) {
			if (!isDone(theCase)) {
				return false;
			}

			// The question is NOT done, until every valid children is also done
			for (TerminologyObject to: getChildren()) {
				QASet child = (QASet) to;
				if (child.isValid(theCase)
						&& !child.isDone(theCase, respectValidFollowQuestions)) {
					return false;
				}
			}
			return true;

		}
		else {
			return isDone(theCase);
		}
	}

	@Override
	public void removeContraReason(Reason source, Session theCase) {
		((CaseQuestion) theCase.getCaseObject(this)).removeContraReason(source);
	}

	@Override
	public void removeProReason(Reason source, Session theCase) {
		((CaseQuestion) theCase.getCaseObject(this)).removeProReason(source);
	}

	/**
	 * Sets the knowledge base, to which this objects belongs to and adds this
	 * object to the knowledge base (reverse link).
	 * 
	 * @param newKnowledgeBase
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 */
	@Override
	public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
		super.setKnowledgeBase(knowledgeBase);
		// maybe somebody should remove this object from the old
		// knowledge base if available
		getKnowledgeBase().add(this);
	}


	@Override
	public abstract void setValue(Session theCase, Value value) throws IllegalArgumentException;

	/**
	 * Sets a new value to the case object of this question and saves the
	 * overwritten value-array in its historyStack.
	 * 
	 * @param theCase
	 *            current case
	 * @param newRule
	 *            rule that has modified the question´s value-array It will be
	 *            stored together with the overwritten value-array
	 * @param values
	 *            new value-array which will be set to the question.
	 */
	public void setValue(Session theCase, Rule ruleSymptom,
			Value value) throws IllegalArgumentException {
		setValue(theCase, value);
	}

	@Override
	public String toString() {
		return super.toString();
	}

	/**
	 * This method is invoked if a rule in the specified {@link Session} is
	 * retracted. That means that either if this rule is on top of the
	 * history-stack the value that has been overwritten by it will be set to
	 * this question or (else) the history-stack-entry will be removed.
	 * 
	 * @param session
	 *            the specified problem-solving session
	 * @param ruleSymptom
	 *            the retracted rule
	 */
	public void undoSymptomValue(Session session, Rule ruleSymptom) {
		CaseQuestion caseQuestion = ((CaseQuestion) session.getCaseObject(this));
		if (caseQuestion.getValueHistory().size() == 0) {
			setValue(session, UndefinedValue.getInstance());
		}
		else {
			ListIterator<SymptomValue> valueIter = caseQuestion.getValueHistory()
					.listIterator();
			SymptomValue symptomValue = null;
			int index = 0;
			while (valueIter.hasNext()) {
				symptomValue = valueIter.next();
				if (ruleSymptom.equals(symptomValue.getRule())) {
					if (index == 0) {
						if (Boolean.TRUE.equals(getProperties().getProperty(
								Property.TIME_VALUED))) {
							if (getKnowledge(PSMethodQuestionSetter.class,
									PSMethodQuestionSetter.NUM2CHOICE_SCHEMA) == null) {
								session.setValue(this, symptomValue.getValues());
							}
						}
						else { // standard (non-temporal) case
							session.setValue(this, symptomValue.getValues());
						}
					}
					else {
						SymptomValue nextValue = (SymptomValue) caseQuestion
								.getValueHistory().get(index - 1);
						nextValue.setValues(symptomValue.getValues());
					}

					caseQuestion.getValueHistory().remove(index);
					break;
				}
				index++;
			}
		}
	}

	// deleteme: joba: 04.2010
	// private String verbalizeWithoutValue() {
	// return "\n " + super.toString();
	// }
	//
	// public String verbalizeWithValue(Session session) {
	// return verbalizeWithoutValue() + "\n Value -> " + session.getValue(this);
	// }
}