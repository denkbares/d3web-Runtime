/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.interview.inference.condition;

import de.d3web.core.inference.condition.CondQuestion;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.interview.Interview;
import de.d3web.interview.inference.PSMethodInterview;

/**
 * This condition checks, if an NamedObject (e.g. Question) has a value or was
 * answered with {@link AnswerUnknown} AFTER it was indicated, ie a value has
 * been set after the latest indication of the question.
 * 
 * @author Reinhard Hatko
 * @created 02.03.2011
 */
public class CondRepeatedAnswered extends CondQuestion {

	/**
	 * Creates a new CondRepeatedAnswered object for the given {@link Question}.
	 * 
	 * @param the given question
	 */
	public CondRepeatedAnswered(Question question) {
		super(question);
	}

	@Override
	public boolean eval(Session session) throws NoAnswerException {
		Interview interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
		return session.getBlackboard().getIndication(getQuestion()).hasState(
				Indication.State.REPEATED_INDICATED)
				&& !interview.getInterviewAgenda().hasState(
						getQuestion(),
						de.d3web.core.session.interviewmanager.InterviewAgenda.InterviewState.ACTIVE);
	}

	@Override
	public String toString() {
		return getQuestion().getName()
				+ " = repeatedly answered";
	}
}
