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
package de.d3web.core.session.interviewmanager;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.InterviewObject;

/**
 * The Interview manages the interview state
 * 
 * @author Volker Belli & Joachim Baumeister (denkbares GmbH)
 */
public interface Interview {

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
