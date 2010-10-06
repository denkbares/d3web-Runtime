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

package de.d3web.core.session;

import java.util.Date;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.interviewmanager.FormStrategy;
import de.d3web.core.session.interviewmanager.NextUnansweredQuestionFormStrategy;

/**
 * Factory for Session objects.
 * 
 * @author joba, Norman Brümmer, Georg
 */
public final class SessionFactory {

	private SessionFactory() { // enforce noninstantiability
	}

	/**
	 * Factory-method that creates instances of Session.
	 * 
	 * @param knowledgeBase the knowledge base used in the case.
	 * @return new Session instance based on the specified knowledge base
	 */
	public static synchronized Session createSession(KnowledgeBase knowledgeBase) {
		return createSession(null, knowledgeBase, new NextUnansweredQuestionFormStrategy(),
				new Date());
	}

	/**
	 * Factory-method that creates instances of Session
	 * 
	 * @created 27.09.2010
	 * @param id the ID
	 * @param kb {@link KnowledgeBase}
	 * @param creationDate Date of creation
	 * @return {@link Session}
	 */
	public static synchronized DefaultSession createSession(String id, KnowledgeBase kb, Date creationDate) {
		return createSession(id, kb, new NextUnansweredQuestionFormStrategy(), creationDate);
	}

	/**
	 * Factory-method that creates instances of Session.
	 * 
	 * @param id the ID
	 * @param knowledgeBase the knowledge base used in the case.
	 * @param formStrategy the specified {@link FormStrategy}
	 * @return new Session instance based on the specified id, knowledge base
	 *         and form strategy
	 */
	public static synchronized DefaultSession createSession(String id,
			KnowledgeBase knowledgeBase,
			FormStrategy formStrategy, Date creationDate) {
		return new DefaultSession(id, knowledgeBase, formStrategy, creationDate);
	}
}