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

package de.d3web.core.knowledge.terminology;

import java.util.List;
import java.util.Vector;

import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.CaseQuestion;
import de.d3web.core.session.blackboard.CaseQuestionMC;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.values.Choice;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;

/**
 * Storage for Questions with predefined multiple answers (alternatives). <BR>
 * Part of the Composite design pattern (see QASet for further description)
 * 
 * @author joba, Christian Betz
 * @see QASet
 */
public class QuestionMC extends QuestionChoice {

	public QuestionMC(String id) {
		super(id);
	}

	public SessionObject createCaseObject(Session session) {
		return new CaseQuestionMC(this);
	}

	public List<Choice> getAlternatives() {
		if (alternatives == null) {
			return new Vector<Choice>();
		}
		else return alternatives;
	}

	/**
	 * Sets the current values of this MC-question belonging to the specified
	 * Session.<BR>
	 * <B>Caution:</B> It is possible to set numerical values to a MC- question.
	 * In this case, a Num2ChoiceSchema must be defined a KnowledgeSlice.
	 * 
	 * @param theCase the belonging Session
	 * @param antwort an array of Answer instances
	 */
	@Override
	public void setValue(Session theCase, Value value) throws IllegalArgumentException {
		if (value instanceof MultipleChoiceValue) {
			((CaseQuestionMC) theCase.getCaseObject(this)).setValue(value);
		}
		else if (value instanceof Unknown || value instanceof UndefinedValue) {
			((CaseQuestion) (theCase.getCaseObject(this))).setValue(value);
		}
		else {
			throw new IllegalArgumentException(value
					+ " is not an instance of MultipleChoiceValue.");
		}
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
