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

/*
 * Created on 09.10.2003
 */
package de.d3web.core.session.blackboard;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;

/**
 * Stores the dynamic, user specific values for an QuestionDate
 * object. It corresponds to the static QuestionDate object.<br>
 * Values to be stored:<br>
 * <li> Current date value corresponding to a given user case.
 * @author Tobias vogele
 * @see QuestionDate
 */
public class CaseQuestionDate extends CaseQuestion {
	private Value value = UndefinedValue.getInstance();

	public CaseQuestionDate(Question question) {
		super(question);
	}

	public Value getValue() {
		return value;
	}

	@Override
	public void setValue(Value value) {
		this.value = value;
	}
}
