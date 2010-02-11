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

package de.d3web.indication;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleAction;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.blackboard.CaseQuestion;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.terminology.QuestionChoice;
import de.d3web.core.utilities.Utils;
import de.d3web.indication.inference.PSMethodSuppressAnswer;
/**
 * RuleAction to suppress alternatives of a specified Question
 * Creation date: (19.06.2001 18:36:54)
 * @author Joachim Baumeister
 */
public class ActionSuppressAnswer extends RuleAction {
	private QuestionChoice question = null;

	/* alternatives that should be suppressed, if rule fires */
	private List<AnswerChoice> suppress = null;

	/**
	 * Creates a new ActionSuppressAnswer for the given corresponding rule
	 */
	public ActionSuppressAnswer() {
		super();
	}

	/**
	 * Method called, if rule fires
	 * Creation date: (21.08.2000 07:24:48)
	 * @param theCase current case
	 */
	public void doIt(XPSCase theCase) {
		((CaseQuestion) theCase.getCaseObject(getQuestion())).addRuleSuppress(
			getCorrespondingRule());
	}

	/**
	 * @return PSMethodSuppressAnswer.class
	 */
	public Class getProblemsolverContext() {
		return PSMethodSuppressAnswer.class;
	}

	/**
	 * Creation date: (26.10.00 14:40:27)
	 * @return the QuestionChoice whose alternatives shall be suppressed if the corresponding rule fires
	 */
	public QuestionChoice getQuestion() {
		return question;
	}

	/**
	 * @return  all objects participating on the action.
	 */
	public List getTerminalObjects() {
		List terminals = new ArrayList(1);
		if (getQuestion() != null) {
			terminals.add(getQuestion());
		}
		return terminals;
	}

	/**
	 * @return a list of alternatives to be suppressed.
	 * Creation date: (26.10.00 14:57:55)
	 */
	public List<AnswerChoice> getSuppress() {
		return suppress;
	}

	/**
	 * inserts the corresponding rule as knowledge to the specified Question
	 */
	private void insertRuleIntoQuestion(QuestionChoice theQuestion) {
		if (theQuestion != null)
			theQuestion.addKnowledge(
				getProblemsolverContext(),
				getCorrespondingRule(),
				MethodKind.BACKWARD);
	}

	/**
	 * removes the corresponding rule from the specified Question
	 */
	private void removeRuleFromOldQuestion(QuestionChoice theQuestion) {
		if (theQuestion != null)
			theQuestion.removeKnowledge(
				getProblemsolverContext(),
				getCorrespondingRule(),
				MethodKind.BACKWARD);
	}

	/**
	 * sets the Question whose alternatives shall be suppressed in case of firing.
	 */
	public void setQuestion(QuestionChoice theQuestion) {
		removeRuleFromOldQuestion(this.question);
		this.question = theQuestion;
		insertRuleIntoQuestion(this.question);
	}

	/**
	 * Sets the answers to be suppressed.
	 * Creation date: (26.10.00 14:57:55)
	 */
	public void setSuppress(AnswerChoice[] theSuppressArray) {
		setSuppress(Utils.createList(theSuppressArray));
	}

	/**
	 * Sets the answers to be suppressed.
	 * Creation date: (26.10.00 14:57:55)
	 */
	public void setSuppress(List<AnswerChoice> theSuppress) {
		this.suppress = theSuppress;
	}

	/**
	 * 
	 * Creation date: (31.08.00 15:21:31)
	 * @return String-representation of this suppress-rule
	 */
	public String toString() {
		return "<RuleAction type=\"SupressAnswer\">\n"
			+ "  ["
			+ getQuestion().getId()
			+ ": "
			+ getSuppress()
			+ "]"
			+ "\n</RuleAction>";
	}

	/**
	 * method invoked in case of undoing (the action) of this rule
	 * Creation date: (21.08.2000 07:25:41)
	 * @param theCase current case
	 */
	public void undo(XPSCase theCase) {
		(
			(CaseQuestion) theCase.getCaseObject(
				getQuestion())).removeRuleSuppress(
			getCorrespondingRule());
	}

	public RuleAction copy() {
		ActionSuppressAnswer a = new ActionSuppressAnswer();
		a.setRule(getCorrespondingRule());
		a.setQuestion(getQuestion());
		a.setSuppress(new LinkedList<AnswerChoice>(getSuppress()));
		return a;
	}
	
	public int hashCode() {
		int hash = 0;
		if(getQuestion() != null) {
			hash += getQuestion().hashCode();
		}
		if(getSuppress() != null) {
			hash += getSuppress().hashCode();
		}
		return hash;
	}
	
	public boolean equals(Object o) {
		if (o==this) 
			return true;
		if (o instanceof ActionSuppressAnswer) {
			ActionSuppressAnswer a = (ActionSuppressAnswer)o;
			return (isSame(a.getQuestion(), getQuestion()) &&
					isSame(a.getSuppress(), getSuppress()));
		}
		else
			return false;
	}
	private boolean isSame(Object obj1, Object obj2) {
		if(obj1 == null && obj2 == null) return true;
		if(obj1 != null && obj2 != null) return obj1.equals(obj2);
		return false;
	}
	
}