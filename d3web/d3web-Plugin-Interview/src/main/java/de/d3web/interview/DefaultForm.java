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
package de.d3web.interview;

import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;

public class DefaultForm implements Form {

	private final InterviewObject interviewObject;
	private String title = "noname";
	private final Session session;

	public DefaultForm(String title, InterviewObject interviewObject, Session session) {
		this.title = title;
		this.interviewObject = interviewObject;
		this.session = session;
	}

	@Override
	public InterviewObject getInterviewObject() {
		return this.interviewObject;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public String toString() {
		return this.title + ": " + this.interviewObject;
	}

	@Override
	public boolean isNotEmpty() {
		return true;
	}

	@Override
	public List<Question> getActiveQuestions() {
		List<Question> result = new LinkedList<Question>();
		collectActiveQuestions(interviewObject, result);
		return result;
	}

	private void collectActiveQuestions(InterviewObject interviewObject, List<Question> activeQuestions) {
		if (session.getBlackboard().getIndication(interviewObject).isContraIndicated()) {
			return;
		}
		if (interviewObject instanceof Question) {
			activeQuestions.add((Question) interviewObject);
			for (TerminologyObject to : interviewObject.getChildren()) {
				if (to instanceof InterviewObject
						|| session.getBlackboard().getIndication((InterviewObject) to).hasState(
								State.RELEVANT)) {
					collectActiveQuestions((InterviewObject) to, activeQuestions);
				}
			}
		}
		else if (interviewObject instanceof QContainer) {
			QContainer qcon = (QContainer) interviewObject;
			for (TerminologyObject to : qcon.getChildren()) {
				if (to instanceof InterviewObject) {
					collectActiveQuestions((InterviewObject) to, activeQuestions);
				}
			}
		}
	}

	@Override
	public QContainer getRoot() {
		if (interviewObject instanceof QContainer) {
			return (QContainer) interviewObject;
		}
		return null;
	}
}
