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
import java.util.LinkedList;
import java.util.List;

import de.d3web.abstraction.formula.FormulaElement;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.utilities.EqualsUtils;

/**
 * Sets a specified value for a specified question. The value can be a
 * {@link FormulaExpression} or a specified {@link Choice} of a question.
 * 
 * Creation date: (20.06.2001 18:19:13)
 * 
 * @author Joachim Baumeister
 */
public class ActionSetValue extends PSAction {

	private Question question;
	private Object value;

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

	@Override
	public boolean hasChangedValue(Session session) {
		// always return true, the change management will be handled by doIt()
		return true;

	}

	/**
	 * creates a new ActionSetValue for the given corresponding rule
	 */
	public ActionSetValue() {
		super();
	}

	@Override
	public List<? extends TerminologyObject> getForwardObjects() {
		List<TerminologyObject> list = new LinkedList<TerminologyObject>();
		if (getValue() instanceof FormulaElement) {
			FormulaElement fe = (FormulaElement) getValue();
			list.addAll(fe.getTerminalObjects());
		}
		return list;
	}

	/**
	 * Sets the specified value for the specified question.
	 */
	@Override
	public void doIt(Session session, Object source, PSMethod psmethod) {
		Value oldValue = session.getBlackboard().getValue(getQuestion(), psmethod, source);
		if (getValue() != null) {
			Value tempVal;
			if (getValue() instanceof FormulaElement) {
				FormulaElement evaluatableValue = (FormulaElement) getValue();
				tempVal = evaluatableValue.eval(session);
			}
			else if (getValue() instanceof Choice) {
				tempVal = new ChoiceValue((Choice) getValue());
			}
			else {
				tempVal = (Value) getValue();
			}
			// Only set the computed value, if it differs from the old value in
			// the Blackboard
			if (!oldValue.equals(tempVal)) {

				// Fact fact = new DefaultFact(getQuestion(), tempVal, source,
				// psmethod);

				Fact fact = FactFactory.createFact(session, getQuestion(), tempVal, source,
						psmethod);

				session.getBlackboard().addValueFact(fact);
			}
		}
	}

	@Override
	public void undo(Session session, Object source, PSMethod psmethod) {
		session.getBlackboard().removeValueFact(getQuestion(), source);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof ActionSetValue) {
			ActionSetValue a = (ActionSetValue) o;
			return (EqualsUtils.isSame(a.getQuestion(), getQuestion()) && a.getValue()
					.equals(getValue()));
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 0;
		if (getQuestion() != null) {
			hash += getQuestion().hashCode();
		}
		if (getValue() != null) {
			hash += getValue().hashCode();
		}
		return hash;
	}

	@Override
	public String toString() {
		return "set: " + getQuestion().getName() + " = " + getValue();
	}

}