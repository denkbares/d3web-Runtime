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
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.DerivationType;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.Rule;
import de.d3web.kernel.domainModel.SymptomValue;
import de.d3web.kernel.domainModel.ValuedObject;
import de.d3web.kernel.domainModel.answers.AnswerUnknown;
import de.d3web.kernel.dynamicObjects.CaseQuestion;
import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.psMethods.nextQASet.PSMethodNextQASet;
import de.d3web.kernel.psMethods.questionSetter.PSMethodQuestionSetter;
import de.d3web.kernel.supportknowledge.Property;

/**
 * Abstract Class to store static parts of a question (symptom) independent from
 * the dynamic case-sensitive values. Part of the Composite design pattern (see
 * QASet for further description) DerivationType ({BASIC, DERIVED, MIXED})
 * means:
 * <LI> BASIC: question should be asked in a basic dialogue
 * <LI> DERIVED: question should not be asked, but is set by a RuleSymptom
 * <LI> MIXED: both, should be asked but can also be derived
 * 
 * @author joba, Christian Betz, norman
 * @see QASet
 * @see DerivationType
 */
public abstract class Question extends QASet implements ValuedObject {
    private AnswerUnknown unknown;

    /**
     * 
     */
    public Question() {
	super();
	// create "unknown"-alternative
	unknown = new AnswerUnknown();
	unknown.setQuestion(this);
    }

    public Question(String id) {
	super(id);
	// create "unknown"-alternative
	unknown = new AnswerUnknown();
	unknown.setQuestion(this);
    }

    public void addContraReason(Reason source, XPSCase theCase) {
	((CaseQuestion) theCase.getCaseObject(this)).addContraReason(source);
    }

    public void addProReason(Reason source, XPSCase theCase) {
	theCase.trace(this.getId() + " + proReason: " + source.toString());
	((CaseQuestion) theCase.getCaseObject(this)).addProReason(source);
    }

    public boolean expand(List onList, XPSCase theCase) {
	// Rekursionsabbruch, siehe QContainer
	return false;
    }

    public List getContraReasons(XPSCase theCase) {
	return ((CaseQuestion) theCase.getCaseObject(this)).getContraReasons();
    }

    /**
     * DerivationType ({BASIC, DERIVED, MIXED}) means:
     * <LI> BASIC: question should be asked in a basic dialogue
     * <LI> DERIVED: question should not be asked, but is set by a RuleSymptom
     * <LI> MIXED: both, should be asked but can also be derived
     * 
     * @return de.d3web.kernel.domainModel.DerivationType a static instance of
     *         DerivationType
     */
    public DerivationType getDerivationType() {
	final Class QUESTION_SETTER = PSMethodQuestionSetter.class;
	final Class FOLLOW_QUESTION = PSMethodNextQASet.class;
	final MethodKind KIND = MethodKind.BACKWARD;
	if (hasElements(getKnowledge(QUESTION_SETTER, KIND))
		&& hasElements(getKnowledge(FOLLOW_QUESTION, KIND)))
	    return DerivationType.MIXED;
	else if (hasElements(getKnowledge(QUESTION_SETTER, KIND)))
	    return DerivationType.DERIVED;
	else
	    return DerivationType.BASIC;
    }

    private boolean hasElements(Object list) {
	if (list == null)
	    return false;
	else if ((list instanceof Collection) && ((Collection) list).isEmpty())
	    return false;
	else
	    return true;
    }

    public List getProReasons(XPSCase theCase) {
	return ((CaseQuestion) theCase.getCaseObject(this)).getProReasons();
    }

    /**
     * @return the 'unkown'-alternative object.
     */
    public AnswerUnknown getUnknownAlternative() {
	return unknown;
    }

    public abstract List getValue(XPSCase theCase);

    public boolean hasValue(XPSCase theCase) {
	return (getValue(theCase) != null) && (getValue(theCase).size() > 0);
    }

    public boolean isDone(XPSCase theCase) {
	if (!getContraReasons(theCase).isEmpty()) {
	    // Question has ContraIndication (probably)
	    return true;
	} else {
	    return (getValue(theCase) != null && !getValue(theCase).isEmpty());
	}
    }

    public boolean isDone(XPSCase theCase, boolean respectValidFollowQuestions) {
	if (respectValidFollowQuestions) {
	    if (!isDone(theCase)) {
		return false;
	    }

	    // Falls auch nur ein einziges (valides) Children nicht abgearbeitet
	    // ist, ist auch die ganze FK nicht abgearbeitet.
	    Iterator iter = getChildren().iterator();
	    while (iter.hasNext()) {
		QASet child = (QASet) iter.next();
		if (child.isValid(theCase)
			&& !child.isDone(theCase, respectValidFollowQuestions)) {
		    return false;
		}
	    }
	    return true;

	} else {
	    return isDone(theCase);
	}
    }

    public void removeContraReason(Reason source, XPSCase theCase) {
	((CaseQuestion) theCase.getCaseObject(this)).removeContraReason(source);
    }

    public void removeProReason(Reason source, XPSCase theCase) {
	((CaseQuestion) theCase.getCaseObject(this)).removeProReason(source);
    }

    /**
     * @return a list containing all items in list1 which are not in list2
     */

    public static final List setDifference(List list1, List list2) {
	Vector res = new Vector();
	Iterator iter = list1.iterator();
	while (iter.hasNext()) {
	    Object elem = iter.next();
	    if (!list2.contains(elem)) {
		res.add(elem);
	    }
	}
	return res;
    }

    /**
     * Sets the knowledgebase, to which this objects belongs to and adds this
     * object to the knowledge base (reverse link).
     * 
     * @param newKnowledgeBase
     *                de.d3web.kernel.domainModel.KnowledgeBase
     */
    public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
	    super.setKnowledgeBase(knowledgeBase);
	    // maybe somebody should remove this object from the old
	    // knowledge base if available
	    getKnowledgeBase().add(this);
	}

    public abstract void setValue(XPSCase theCase, Object[] values);

    /**
     * Sets a new value to the case object of this question and saves the
     * overwritten value-array in its historyStack.
     * 
     * @param theCase
     *                current case
     * @param newRule
     *                rule that has modified the question´s value-array It will
     *                be stored together with the overwritten value-array
     * @param values
     *                new value-array which will be set to the question.
     */
    public void setValue(XPSCase theCase, Rule ruleSymptom,
	    Object[] values) {
	CaseQuestion caseQuestion = ((CaseQuestion) theCase.getCaseObject(this));
	if (ruleSymptom != null) {
	    // get old value and push it (with new Rule) on historyStack.
	    SymptomValue symptomValue = new SymptomValue(getValue(theCase)
		    .toArray(), ruleSymptom);
	    caseQuestion.getValueHistory().add(0, symptomValue);
	}
	setValue(theCase, values);

    }

    public String toString() {
	return super.toString();
    }

    /**
     * This method is invoked if a rule is going to be undone. That means that
     * either if this rule is on top of the history-stack the value that has
     * been overwritten by it will be set to this question or (else) the
     * history-stack-entry will be removed
     * 
     * @param theCase
     *                current case
     * @param rule
     *                rule going to be undone
     */
    public void undoSymptomValue(XPSCase theCase, Rule ruleSymptom) {
	CaseQuestion caseQuestion = ((CaseQuestion) theCase.getCaseObject(this));
	if (caseQuestion.getValueHistory().size() == 0) {
	    setValue(theCase, new Object[] {});
	} else {
	    ListIterator valueIter = caseQuestion.getValueHistory()
		    .listIterator();
	    SymptomValue symptomValue = null;
	    int index = 0;
	    while (valueIter.hasNext()) {
		symptomValue = (SymptomValue) valueIter.next();
		if (ruleSymptom.equals(symptomValue.getRule())) {
		    theCase.trace("loesche: " + ruleSymptom.getId()
			    + " bei index " + index);
		    theCase.trace("his vor remove: "
			    + caseQuestion.getValueHistory());
		    if (index == 0) {
			if (Boolean.TRUE.equals(getProperties().getProperty(
				Property.TIME_VALUED))) {
			    if (getKnowledge(PSMethodQuestionSetter.class,
				    PSMethodQuestionSetter.NUM2CHOICE_SCHEMA) == null) {
				theCase
					.setValue(this, symptomValue
						.getValues());
			    }
			} else { // standard (non-temporal) case
			    theCase.setValue(this, symptomValue.getValues());
			}
		    } else {
			SymptomValue nextValue = (SymptomValue) caseQuestion
				.getValueHistory().get(index - 1);
			nextValue.setValues(symptomValue.getValues());
		    }

		    caseQuestion.getValueHistory().remove(index);
		    theCase.trace("his nach remove: "
			    + caseQuestion.getValueHistory());
		    break;
		}
		index++;
	    }
	}
    }

    public String verbalizeWithoutValue() {
	String res = "\n " + super.toString();
	return res;
    }

    public String verbalizeWithValue(XPSCase theCase) {
	return verbalizeWithoutValue() + "\n Wert -> " + getValue(theCase);
    }
}