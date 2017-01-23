/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.interview;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;

/**
 * This abstract class is basically introduced to define some helper methods,
 * that are commonly used by implementations of {@link FormStrategy}.
 *
 * @author joba
 */
public abstract class AbstractFormStrategy implements FormStrategy {

	/**
	 * Helper method to check, if a {@link Value} is assigned to the specified
	 * {@link Question} instance in the specified {@link Session} other than
	 * {@link UndefinedValue}.
	 *
	 * @param question the specified {@link Question} instance
	 * @param session the specified {@link Session} instance
	 * @return true, when the specified question has a value other than {@link UndefinedValue}
	 */
	protected boolean hasValueUndefined(Question question, Session session) {
		Value value = session.getBlackboard().getValue(question);
		return (value instanceof UndefinedValue);
	}

	/**
	 * Returns true if any of the questions remains unanswered (by any problem solver!) in the
	 * specified session. In this case the questions are not totally completed. Returns false if the
	 * questions are completely answered and therefore.
	 *
	 * @param questions the questions to be checked
	 * @param session the session to check the answers
	 * @return if any question is unanswered and the list is completed
	 */
	protected boolean hasAnyValueUndefined(Collection<Question> questions, Session session) {
		return questions.stream().anyMatch(q -> hasValueUndefined(q, session));
	}

	@Override
	public List<Form> getForms(InterviewObject object, Session session) {
		return Collections.singletonList(new DefaultForm(object.getName(), object, session));
	}
}
