/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.core.session.interviewmanager;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.interviewmanager.InterviewAgenda.InterviewState;
import de.d3web.core.session.values.UndefinedValue;

/**
 * This abstract class is basically introduced to define some helper methods,
 * that are commonly used by implementations of {@link FormStrategy}. 
 * 
 * @author joba
 *
 */
public abstract class AbstractFormStrategy implements FormStrategy {

	/**
	 * Helper method to check, if a {@link Value} is assigned to the 
	 * specified {@link Question} instance in the specified {@link Session} 
	 * other than {@link UndefinedValue}.
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
	 * Helper method to check, if the specified {@link Question} instance
	 * has an ACTIVE state on the {@link InterviewAgenda} of the specified
	 * {@link Session} instance.
	 * 
	 * @param question the specified {@link Question} instance 
	 * @param session the specified {@link Session} instance
	 * @return true, when the specified question is ACTIVE on the interview agenda
	 */
	protected boolean isActiveOnAgenda(Question question, Session session) {
		return session.getInterviewManager().getInterviewAgenda().hasState(
				question, InterviewState.ACTIVE);
	}

}
