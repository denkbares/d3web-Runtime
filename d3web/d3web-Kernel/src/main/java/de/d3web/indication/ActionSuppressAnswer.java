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

package de.d3web.indication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.utils.EqualsUtils;

/**
 * RuleAction to suppress alternatives of a specified Question
 * 
 * Creation date: (19.06.2001 18:36:54)
 * 
 * @author Joachim Baumeister
 */
public class ActionSuppressAnswer extends PSAction {

	private QuestionChoice question = null;

	/* alternatives that should be suppressed, if rule fires */
	private final List<ChoiceID> suppress = new LinkedList<ChoiceID>();

	/**
	 * Creates a new ActionSuppressAnswer for the given corresponding rule
	 */
	public ActionSuppressAnswer() {
		super();
	}

	/**
	 * Method called, if rule fires Creation date: (21.08.2000 07:24:48)
	 * 
	 * @param session current case
	 */
	@Override
	public void doIt(Session session, Object source, PSMethod psmethod) {
		// TODO: suppress actions are not working currently
	}

	/**
	 * Creation date: (26.10.00 14:40:27)
	 * 
	 * @return the QuestionChoice whose alternatives should be suppressed if the
	 *         corresponding rule fires
	 */
	public QuestionChoice getQuestion() {
		return question;
	}

	/**
	 * @return all objects participating on the action.
	 */
	@Override
	public List<? extends TerminologyObject> getBackwardObjects() {
		List<TerminologyObject> terminals = new ArrayList<TerminologyObject>(1);
		if (getQuestion() != null) {
			terminals.add(getQuestion());
		}
		return terminals;
	}

	/**
	 * @return a list of alternatives to be suppressed. Creation date: (26.10.00
	 *         14:57:55)
	 */
	public List<ChoiceID> getSuppress() {
		return Collections.unmodifiableList(suppress);
	}

	/**
	 * Sets the Question whose alternatives should be suppressed in case of
	 * firing.
	 */
	public void setQuestion(QuestionChoice theQuestion) {
		this.question = theQuestion;
	}

	public void addSuppress(ChoiceID choiceToSuppress) {
		this.suppress.add(choiceToSuppress);
	}

	public void addSuppress(Choice choiceToSuppress) {
		this.suppress.add(new ChoiceID(choiceToSuppress));
	}

	/**
	 * 
	 * Creation date: (31.08.00 15:21:31)
	 * 
	 * @return String-representation of this suppress-rule
	 */
	@Override
	public String toString() {
		return "<RuleAction type=\"SupressAnswer\">\n"
				+ "  ["
				+ getQuestion().getName()
				+ ": "
				+ getSuppress()
				+ "]"
				+ "\n</RuleAction>";
	}

	/**
	 * Method invoked in case of undoing (the action) of this rule
	 * 
	 * Creation date: (21.08.2000 07:25:41)
	 * 
	 * @param session current case
	 */
	@Override
	public void undo(Session session, Object source, PSMethod psmethod) {
		// TODO: integration of suppress action!
	}

	@Override
	public int hashCode() {
		int hash = 0;
		if (getQuestion() != null) {
			hash += getQuestion().hashCode();
		}
		if (getSuppress() != null) {
			hash += getSuppress().hashCode();
		}
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof ActionSuppressAnswer) {
			ActionSuppressAnswer a = (ActionSuppressAnswer) o;
			return (EqualsUtils.isSame(a.getQuestion(), getQuestion()) && EqualsUtils.isSame(
					a.getSuppress(), getSuppress()));
		}
		else {
			return false;
		}
	}
}