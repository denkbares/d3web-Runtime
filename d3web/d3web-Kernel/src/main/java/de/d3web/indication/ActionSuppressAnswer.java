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

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.PSAction;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.CaseQuestion;
import de.d3web.core.session.values.Choice;
import de.d3web.core.utilities.Utils;
import de.d3web.indication.inference.PSMethodSuppressAnswer;
/**
 * RuleAction to suppress alternatives of a specified Question
 * Creation date: (19.06.2001 18:36:54)
 * @author Joachim Baumeister
 */
public class ActionSuppressAnswer extends PSAction {
	
	private QuestionChoice question = null;

	/* alternatives that should be suppressed, if rule fires */
	private List<Choice> suppress = null;

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
	@Override
	public void doIt(Session theCase, Rule rule) {
		((CaseQuestion) theCase.getCaseObject(getQuestion())).addRuleSuppress(
			rule);
	}

	/**
	 * @return PSMethodSuppressAnswer.class
	 */
	public Class<? extends PSMethod> getProblemsolverContext() {
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
	public List<? extends NamedObject> getTerminalObjects() {
		List<NamedObject> terminals = new ArrayList<NamedObject>(1);
		if (getQuestion() != null) {
			terminals.add(getQuestion());
		}
		return terminals;
	}

	/**
	 * @return a list of alternatives to be suppressed.
	 * Creation date: (26.10.00 14:57:55)
	 */
	public List<Choice> getSuppress() {
		return suppress;
	}

	/**
	 * sets the Question whose alternatives shall be suppressed in case of firing.
	 */
	public void setQuestion(QuestionChoice theQuestion) {
		this.question = theQuestion;
	}

	/**
	 * Sets the answers to be suppressed.
	 * Creation date: (26.10.00 14:57:55)
	 */
	public void setSuppress(Choice[] theSuppressArray) {
		setSuppress(Utils.createList(theSuppressArray));
	}

	/**
	 * Sets the answers to be suppressed.
	 * Creation date: (26.10.00 14:57:55)
	 */
	public void setSuppress(List<Choice> theSuppress) {
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
	@Override
	public void undo(Session theCase, Rule rule) {
		(
			(CaseQuestion) theCase.getCaseObject(
				getQuestion())).removeRuleSuppress(rule);
	}

	public PSAction copy() {
		ActionSuppressAnswer a = new ActionSuppressAnswer();
		a.setQuestion(getQuestion());
		a.setSuppress(new LinkedList<Choice>(getSuppress()));
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