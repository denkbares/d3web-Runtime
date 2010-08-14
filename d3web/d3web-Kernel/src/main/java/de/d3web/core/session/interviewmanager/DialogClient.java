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

package de.d3web.core.session.interviewmanager;

import de.d3web.core.session.Value;
import de.d3web.core.session.Session;

/**
 * A DialogClient is an Object that can be handled by the DialogProxy. If the
 * Proxy is asked for Answers of a Question, it will first ask all registered
 * clients if they have such an answer. Every Client gets a priority by which it
 * can be compared by DialogClientComparator A DialogClient may be a
 * ShadowMemory (in RAM) or e.g. a Database-Client...
 * 
 * @see ShadowMemory
 * @author Norman Brümmer
 */
public abstract class DialogClient {

	private int priority = 0;

	public DialogClient() {
		super();
	}

	/**
	 * @return a List of Answers for the Question with the given ID, if such
	 *         answers exist, otherwise null
	 */
	public abstract Value getAnswers(String QuestionID);

	/**
	 * @return the Priority of this client. 1 is the highest.
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * fills this Client with Question-IDs and answers from the given case
	 */
	public abstract void putCase(Session session);

	/**
	 * @param newPriority Priority of this Client. Neccessary for Proxy. 1 is
	 *        the highest...
	 */
	public void setPriority(int newPriority) {
		priority = newPriority;
	}
}