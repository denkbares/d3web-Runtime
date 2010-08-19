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
import java.util.List;

import de.d3web.core.inference.PSAction;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;

/**
 * An abstract class for representing an {@link PSAction} that is
 * able to set value for a specified question.
 * 
 * @author baumeister, bates
 */
public abstract class ActionQuestionSetter extends PSAction {

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

	@Override
	public boolean hasChangedValue(Session session) {
		// always return true, the change management will be handled by doIt()
		return true;

	}
}