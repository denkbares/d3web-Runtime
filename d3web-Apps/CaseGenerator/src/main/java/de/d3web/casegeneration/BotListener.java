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

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.empiricaltesting.SequentialTestCase;

/**
 * 
 * @author volker_belli
 * @created 30.08.2011
 */
public interface BotListener {

	/**
	 * This method is called before the knowledge base will be used the first
	 * time.
	 * 
	 * @created 30.08.2011
	 * @param interviewBot the {@link InterviewBot} created that event
	 * @param knowledgeBase the prepared knowledge base
	 */
	void knowledgePrepared(InterviewBot interviewBot, KnowledgeBase knowledgeBase);

	/**
	 * This method is called before the created session and sequential test case
	 * will be used / expanded the first time.
	 * 
	 * @created 30.08.2011
	 * @param interviewBot the {@link InterviewBot} created that event
	 * @param session the root session of the tree
	 * @param stc the root sequential test case of the tree
	 */
	void sessionPrepared(InterviewBot interviewBot, Session session, SequentialTestCase stc);

	/**
	 * This method is called when a path is detected to be aborted due to any
	 * kind of error or unexpected situation. Possible reasons may be (but not
	 * limited to):
	 * <ul>
	 * <li>not possible answers sets could be detected
	 * <li>the number of possible solutions diverges
	 * <li>the user has aborted the ddtree creation
	 * </ul>
	 * 
	 * @created 30.08.2011
	 * @param interviewBot the {@link InterviewBot} created that event
	 * @param session the session where the error is detected
	 * @param stc the erroneous path, including a final node containing the
	 *        error message
	 */
	void pathErroneous(InterviewBot interviewBot, Session session, SequentialTestCase stc);

	/**
	 * This method is called when a path is completed and all possible reasoning
	 * has been done.
	 * 
	 * @created 30.08.2011
	 * @param interviewBot the {@link InterviewBot} created that event
	 * @param session the session of the completed path
	 * @param stc the completed path
	 */
	void pathCompleted(InterviewBot interviewBot, Session session, SequentialTestCase stc);

}
