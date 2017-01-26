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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.denkbares.utils.Log;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.interview.inference.PSMethodInterview;

/**
 * This class always creates a new {@link Form} that contains the
 * {@link QContainer}, that should be presented/asked next in the dialog system.
 * This {@link QContainer} is not a non-terminal QContainer (i.e., it does not
 * contain other {@link QContainer} instances) and it contains at least one
 * unanswered but active question.
 * <p>
 * For Dialog implementations: Please use helper methods in {@link Interview} to
 * find out, whether a {@link Question} or follow-up {@link Question} is active
 * or not.
 *
 * @author joba
 */
public class CurrentQContainerFormStrategy implements FormStrategy {

	@Override
	public Form nextForm(List<InterviewObject> agendaEntries, Session session) {
		if (agendaEntries.isEmpty()) {
			return EmptyForm.getInstance();
		}
		else {
			for (InterviewObject object : agendaEntries) {
				if (object instanceof Question) {
					return new DefaultForm(object.getName(), object, session);
				}
				else if (object instanceof QContainer) {
					QContainer nextQContainer = retrieveNextUnfinishedQContainer(
							(QContainer) object, session);
					if (nextQContainer == null) {
						return EmptyForm.getInstance();
					}
					return new DefaultForm(nextQContainer.getName(), nextQContainer, session);
				}
			}
			return null;
		}
	}

	@Override
	public boolean isActive(Question question, Session session) {
		return new FormStrategyUtils(session).isActive(question);
	}

	@Override
	public boolean isForcedActive(Question question, Session session) {
		return new FormStrategyUtils(session).isForcedActive(question);
	}

	/**
	 * Staring with the specified {@link QContainer}, recursively find the next
	 * {@link QContainer} instance, that only contains {@link Question}
	 * instances (i.e., a terminal {@link QContainer}) and that contains active
	 * questions with respect to the specified {@link Session} instance.
	 *
	 * @param container the specified {@link QContainer}
	 * @param session the specified session
	 * @return an active {@link QContainer} instance
	 */
	private QContainer retrieveNextUnfinishedQContainer(QContainer container,
														Session session) {
		if (isTerminalQContainer(container)
				&& hasActiveQuestions(container, session)) {
			return container;
		}
		// container is not a terminal qcontainer, i.e., contains further
		// qcontainers
		// => traverse to the next active child in a DFS style
		else {
			TerminologyObject[] children = container.getChildren();
			for (TerminologyObject child : children) {
				if (child instanceof QContainer) {
					QContainer candidate = retrieveNextUnfinishedQContainer(
							(QContainer) child, session);
					if (candidate != null) {
						return candidate;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Checks, whether the specified container contains active (follow-up)
	 * questions or direct child questions, that have no value with respect to
	 * the specified {@link Session} instance.
	 *
	 * @param container the specified {@link QContainer} instance
	 * @param session the specified {@link Session} instance
	 * @return true, when it contains an unanswered direct question or an active (possible
	 * follow-up) question
	 */
	private boolean hasActiveQuestions(QContainer container, Session session) {
		FormStrategyUtils utils = new FormStrategyUtils(session);
		Interview interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
		for (TerminologyObject child : container.getChildren()) {
			if (child instanceof Question) {
				Question question = (Question) child;
				if (utils.hasValueUndefined(question)) {
					return true;
				}
				else {
					List<Question> followUpQuestions = collectFollowUpQuestions(question);
					for (Question followUp : followUpQuestions) {
						if (interview.isActive(followUp)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private List<Question> collectFollowUpQuestions(Question question) {
		List<Question> children = new ArrayList<>();
		for (TerminologyObject object : question.getChildren()) {
			if (object instanceof Question) {
				Question child = (Question) object;
				children.add(child);
				children.addAll(collectFollowUpQuestions(child));
			}
			else {
				Log.warning("UNHANDLED QASET TYPE");
			}
		}
		return children;
	}

	private boolean isTerminalQContainer(QContainer container) {
		for (TerminologyObject terminologyObject : container.getChildren()) {
			if (!(terminologyObject instanceof Question)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public List<Form> getForms(InterviewObject object, Session session) {
		return Collections.singletonList(new DefaultForm(object.getName(), object, session));
	}
}
