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

import de.d3web.core.knowledge.InterviewObject;
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
	 * Returns a form of an {@link InterviewObject}, even if it is actually not
	 * contained on the {@link InterviewAgenda}
	 * 
	 * @created 15.04.2013
	 * @param object
	 * @return
	 */
	Form getForm(InterviewObject object, Session session);

}
