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

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.interviewmanager.Form;
import de.d3web.core.session.interviewmanager.FormStrategy;
import de.d3web.core.session.interviewmanager.InterviewAgenda;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 25.03.2013
 */
@SuppressWarnings("deprecation")
public interface Interview extends de.d3web.core.session.interviewmanager.Interview, SessionObject {

	/**
	 * Returns the next form of the Interview
	 * 
	 * @return next Form
	 */
	Form nextForm();

	/**
	 * Optional configuration: Explicitly set a strategy that defines how the
	 * nextForm method computes the next form. On example is the
	 * {@link NextUnansweredQuestionFormStrategy}.
	 * 
	 * @param strategy the specified FormStrategy
	 */
	void setFormStrategy(FormStrategy strategy);

	/**
	 * Interface to notify the Interview, that the value of a fact has changed.
	 * 
	 * @param changedFact the changed fact with the new and the old value
	 */
	void notifyFactChange(PropagationEntry changedFact);

	/**
	 * Returns the agenda of the currently running {@link Interview} instance.
	 * 
	 * @return the {@link InterviewAgenda} instance of the currently running
	 *         {@link Interview}.
	 */
	InterviewAgenda getInterviewAgenda();

	/**
	 * Test, if the specified {@link InterviewObject} instance is ACTIVE with
	 * respect to the given state on the {@link InterviewAgenda}.
	 * 
	 * @param interviewObject the specified {@link InterviewObject} instance
	 * @return true, if the specified {@link InterviewObject} instance has an
	 *         active state on the {@link InterviewAgenda}
	 */
	boolean isActive(InterviewObject interviewObject);

}
