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
import java.util.Locale;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;

public class DefaultForm implements Form {

	private final InterviewObject interviewObject;
	private String title = "noname";
	private final Session session;

	public DefaultForm(String title, InterviewObject interviewObject, Session session) {
		this.title = title;
		this.interviewObject = interviewObject;
		this.session = session;
	}

	@NotNull
	@Override
	public String getName() {
		return "Form: " + interviewObject.getName();
	}

	@NotNull
	@Override
	public String getPrompt(Locale lang) {
		// for questionnaires, use the prompt of them
		if (interviewObject instanceof QContainer) {
			return MMInfo.getPrompt(interviewObject, lang);
		}

		// for single questions, use no prompt
		// (makes no sense to use question's prompt, because the question will display its prompt themselves
		// --> avoid display the same prompt twice)
		return "";
	}

	@Override
	public String toString() {
		return this.title + ": " + this.interviewObject;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DefaultForm)) return false;
		DefaultForm that = (DefaultForm) o;
		return Objects.equals(interviewObject, that.interviewObject) &&
				Objects.equals(title, that.title) &&
				Objects.equals(session, that.session);
	}

	@Override
	public int hashCode() {
		return Objects.hash(interviewObject, title, session);
	}

	@Override
	public List<Question> getActiveQuestions() {
		List<Question> result = new LinkedList<>();
		collectQuestions(interviewObject, false, result);
		return result;
	}

	@Override
	public List<Question> getPotentialQuestions() {
		List<Question> result = new LinkedList<>();
		collectQuestions(interviewObject, true, result);
		return result;
	}

	private void collectQuestions(InterviewObject interviewObject, boolean all, List<Question> activeQuestions) {
		Blackboard blackboard = session.getBlackboard();
		if (blackboard.getIndication(interviewObject).isContraIndicated()) {
			return;
		}
		if (interviewObject instanceof Question) {
			// prevent cycles
			if (activeQuestions.contains(interviewObject)) {
				return;
			}
			// add the question
			activeQuestions.add((Question) interviewObject);
			// and add all follow-up questions if they are explicitly indicated
			for (TerminologyObject to : interviewObject.getChildren()) {
				if (to instanceof InterviewObject
						&& (all || blackboard.getIndication((InterviewObject) to).isRelevant())) {
					collectQuestions((InterviewObject) to, all, activeQuestions);
				}
			}
		}
		else if (interviewObject instanceof QContainer) {
			// add all children of a qcontainer, regardless is indicated or not
			for (TerminologyObject to : interviewObject.getChildren()) {
				if (to instanceof InterviewObject) {
					collectQuestions((InterviewObject) to, all, activeQuestions);
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

	@SuppressWarnings("deprecation")
	@Override
	public InterviewObject getInterviewObject() {
		return this.interviewObject;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getTitle() {
		return getName();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isNotEmpty() {
		return true;
	}
}
