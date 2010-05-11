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

import java.util.Arrays;
import java.util.List;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.interviewmanager.InterviewAgenda.InterviewState;
import de.d3web.core.session.values.UndefinedValue;

/**
 * This class always creates a new {@link Form} that contains the one
 * {@link Question}, that should be presented/asked next in the dialog system. 
 * 
 * @author joba
 *
 */
public class NextUnansweredQuestionFormStrategy implements FormStrategy {

	@Override
	public Form nextForm(List<InterviewObject> agendaEnties, Session session) {
		if (agendaEnties.isEmpty()) {
			return EmptyForm.getInstance();
		}
		else {
			InterviewObject object = agendaEnties.get(0);
			if (object instanceof Question) {
				List<InterviewObject> interviewObjects = Arrays.asList(object);
				return new DefaultForm(((Question)object).getName(), interviewObjects);
			}
			else if (object instanceof QASet) {
				Question nextQuestion = retrieveNextQuestionToBeAnswered((QASet)object, session);
				if (nextQuestion == null) {
					return EmptyForm.getInstance();
				}
				return new DefaultForm(nextQuestion.getName(), nextQuestion);
			}
			return null;			
		}
	}

	/**
	 * Traverses in a depth-first-search all children of the specified {@link QASet} and 
	 * returns the first question, that has no value assigned in the specified session.
	 * If no unanswered {@link Question} was be found, then null is returned.
	 * @param qaset the specified {@link QASet}
	 * @param session the specified session
	 * @return the first {@link Question} instance, that is a child of the specified {@link QASet} 
	 *         and is not answered; null otherwise 
	 */
	private Question retrieveNextQuestionToBeAnswered(QASet qaset, Session session) {
		if (qaset instanceof Question) {
			Question question = (Question)qaset;
			// Return question, when it is directly located in a questionnaire and has not been answered.
			if (isDirectQContainerQuestion(question) &&
				hasNoValue(question,session)) {
				return question;
			}
			// Return question, when it is not directly located in a questionnaire but is 
			// active on agenda (follow-up question).
			else if (isNotDirectQContainerQuestion(question) &&
					isActiveOnAgenda(question, session)) {
				return question;
			}
			// Recursively traverse for finding follow-up questions and check these, whether they are active on agenda
			else {
				for (TerminologyObject child : question.getChildren()) {
					Question nextqaset = retrieveNextQuestionToBeAnswered((QASet)child, session);
					if (nextqaset != null) {
						return nextqaset;
					}
				}
			}
		}
		// For a QContainer we try to find the first question that is included in the 
		// container and has not been answered
		else if (qaset instanceof QContainer) {
			TerminologyObject[] children = qaset.getChildren();
			for (TerminologyObject terminologyObject : children) {
				Question nextqaset = retrieveNextQuestionToBeAnswered((QASet)terminologyObject, session);
				if (nextqaset != null) {
					return nextqaset;
				}
			}
		}
		return null;
	}



	private boolean isActiveOnAgenda(Question question, Session session) {
		return session.getInterviewManager().getInterviewAgenda().hasState(question, InterviewState.ACTIVE);
	}

	/**
	 * A direct-qcontainer question must qcontainers as its parents. 
	 * @param question the specified question
	 * @return true, when the parents are only instances of {@link QContainer}.
	 */
	private boolean isDirectQContainerQuestion(Question question) {
		for (TerminologyObject parent : question.getParents()) {
			if ((parent instanceof QContainer) == false) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @see NextUnansweredQuestionFormStrategy.isDirectQContainerQuestion(question)
	 */
	private boolean isNotDirectQContainerQuestion(Question question) {
		return !isDirectQContainerQuestion(question);
	}

	private boolean hasNoValue(Question question, Session session) {
		Value value = session.getBlackboard().getValue(question);
		return (value instanceof UndefinedValue);	
	}
}
