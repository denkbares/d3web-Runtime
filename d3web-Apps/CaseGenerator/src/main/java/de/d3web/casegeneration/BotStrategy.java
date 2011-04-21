/*
 * Copyright (C) 2011 denkbares GmbH, Germany
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
package de.d3web.casegeneration;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.session.Session;

/**
 * This interface is to implement a "Strategy Pattern" to define how the
 * InterviewBot creates the sequential test cases out of a session. It defines
 * which questions will be answered simultaneously and what combinations of
 * answers are allowed to be given by the "virtual user".
 * 
 * @author volker_belli
 * @created 20.04.2011
 */
public interface BotStrategy {

	/**
	 * Checks if the interview is already finished.
	 * 
	 * @created 20.04.2011
	 * @param session the session to be checked
	 * @return if the interview is finished
	 */
	boolean isFinished(Session session);

	/**
	 * Returns a list of next {@link InterviewObject}s that should be answered
	 * simultaneously for one sequence step. If the interview is already
	 * finished, the method may return null.
	 * 
	 * @created 20.04.2011
	 * @param session the session to create the next steps
	 * @return the next steps or null if the interview is finished
	 */
	InterviewObject[] getNextSequenceItems(Session session);

	/**
	 * Returns a list of {@link FactSet}s. Each FactSet is a combination of
	 * answers for one follow-up sequence step. The returned list therefore
	 * describes all follow-up sequence steps that should be evaluated.
	 * <p>
	 * The specified <code>interviewItems</code> usually are those items
	 * previously returned by {@link #getNextSequenceItems(Session)}. Each
	 * returned FactSet should usually contain at least one value fact for each
	 * of these items.
	 * <p>
	 * If no further follow-up sequence steps are to be executed, null is
	 * returned.
	 * 
	 * @created 20.04.2011
	 * @param session the current session to answer in
	 * @param interviewItems the interview items to be answered
	 * @return
	 */
	FactSet[] getNextSequenceAnswers(Session session, InterviewObject[] interviewItems);
}
