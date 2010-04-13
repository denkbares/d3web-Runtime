/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Value;
import de.d3web.core.session.Session;
/**
 * This is a DialogClient that can store answers of Quesitons in the random access memory.
 * It is used by the DialogProxy
 * @author Norman Brümmer
 */
public class ShadowMemory extends DialogClient {
	private Hashtable questionIdAnswersHash = null;

	public ShadowMemory() {
		super();
		questionIdAnswersHash = new Hashtable();
	}

	/**
	 * adds Answers for a Question with questionID as id
	 */
	public void addAnswers(String questionID, Object answers) {
		questionIdAnswersHash.put(questionID, answers);
	}

	/**
	 * @return List of Answers stored for the Question with id quesitonID, null, if no answeres have been stored.
	 */
	@Override
	public Value getAnswers(String questionID) {
		return (Value) questionIdAnswersHash.get(questionID);
	}

	public void initialize() {
		questionIdAnswersHash = new Hashtable();
	}

	/**
	 * stores all questionID-Answers-Pairs of the given Session
	 * @param Session to put
	 */
	@Override
	public void putCase(Session theCase) {
		List questions = theCase.getAnsweredQuestions();

		Iterator iter = questions.iterator();
		while (iter.hasNext()) {
			Question q = (Question) iter.next();
			addAnswers(q.getId(), q.getValue(theCase));
		}
	}

	/**
	 * removes the stored answers of the Question with id questionID
	 * @param questionID id of the Question which answers will be removed
	 */
	public void removeAnswers(String questionID) {
		questionIdAnswersHash.remove(questionID);
	}
}