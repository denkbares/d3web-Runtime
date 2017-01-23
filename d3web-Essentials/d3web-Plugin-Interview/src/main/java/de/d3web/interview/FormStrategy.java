/*
 * Copyright (C) 2013 denkbares GmbH
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

import java.util.List;
import java.util.stream.Collectors;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;

/**
 * Combines Interview Objects to Forms
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 25.03.2013
 */
@SuppressWarnings("deprecation")
public interface FormStrategy extends de.d3web.core.session.interviewmanager.FormStrategy {

	/**
	 * Returns the next form that should be presented to the user according to
	 * the {@link InterviewAgenda}
	 */
	@Override
	Form nextForm(List<InterviewObject> agendaEntries, Session session);

	/**
	 * Returns one or multiple forms of an {@link InterviewObject}, even if it is actually not
	 * contained on the {@link InterviewAgenda}. Most FormStrategy implementation will try to
	 * create a single form for each interview object, but for complex qcontainer the strategy might
	 * decide to split it into multiple forms as well.
	 *
	 * @param object the interview object to create the Form(s) for
	 * @param session the session to create the form(s) for
	 * @return the forms created
	 * @created 15.04.2013
	 */
	List<Form> getForms(InterviewObject object, Session session);

	/**
	 * Returns all the questions to be asked for the specified interview objects. These are the
	 * questions that will be contained in the forms that would be created by {@link
	 * #getForms(InterviewObject, Session)}.
	 *
	 * @param object the interview objects to be answered
	 * @param session the session to answer the questions for
	 * @return the questions that are active within the session for the specified interview objects
	 * @created 15.04.2013
	 */
	default List<Question> getActiveQuestions(InterviewObject object, Session session) {
		return getForms(object, session).stream()
				.map(Form::getActiveQuestions).flatMap(List::stream)
				.distinct().collect(Collectors.toList());
	}
}
