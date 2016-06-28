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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.UndefinedValue;

/**
 * This class always creates a new {@link Form} that contains the one
 * {@link Question}, that should be presented/asked next in the dialog system.
 * Here, the next question is the question that 1) is the next active question
 * on the {@link InterviewAgenda} OR 2) for an indicated {@link QContainer} the
 * next unanswered question in this container
 * 
 * @author joba
 * 
 */
public class NextUnansweredQuestionFormStrategy extends AbstractFormStrategy {

	@Override
	public Form nextForm(List<InterviewObject> agendaEntries, Session session) {
		if (agendaEntries.isEmpty()) {
			return EmptyForm.getInstance();
		}
		else {
			for (InterviewObject object : agendaEntries) {
				if (object instanceof Question) {
					if (UndefinedValue.isUndefinedValue(session.getBlackboard().getValue(
							(ValueObject) object))
							|| session.getBlackboard().getIndication(object).hasState(
									State.REPEATED_INDICATED)) {
						return new DefaultForm(object.getName(), object, session);
					}
					else {
						for (TerminologyObject child : object.getChildren()) {
							if (child instanceof Question
									&& session.getBlackboard().getIndication(
											(InterviewObject) child).isRelevant()
									&& UndefinedValue.isUndefinedValue(session.getBlackboard().getValue(
											(ValueObject) child))) {
								return new DefaultForm(child.getName(),
										(InterviewObject) child,
										session);
							}
						}
					}
				}
				else if (object instanceof QASet) {
					Collection<TerminologyObject> traversedQuestions = new HashSet<>();
					Question nextQuestion = retrieveNextQuestionToBeAnswered((QASet) object,
							session,
							traversedQuestions);
					if (nextQuestion == null) {
						return EmptyForm.getInstance();
					}
					return new DefaultForm(nextQuestion.getName(), nextQuestion, session);
				}
			}
			return null;
		}
	}

	/**
	 * Traverses in a depth-first-search all children of the specified
	 * {@link QASet} and returns the first question, that has no value assigned
	 * in the specified session. If no unanswered {@link Question} was be found,
	 * then null is returned.
	 * 
	 * @param qaset the specified {@link QASet}
	 * @param session the specified session
	 * @param traversedObjects objects traversed already to avoid loops
	 * @return the first {@link Question} instance, that is a child of the
	 *         specified {@link QASet} and is not answered; null otherwise
	 */
	private Question retrieveNextQuestionToBeAnswered(QASet qaset,
			Session session, Collection<TerminologyObject> traversedObjects) { // NOSONAR
		// Termination of recursive traversal: Required for possibly cyclic
		// question hierarchies
		if (traversedObjects.contains(qaset)) {
			return null;
		}
		else {
			// add the current qaset to the already traversed object
			traversedObjects.add(qaset);
		}

		if (qaset instanceof Question) {
			Question question = (Question) qaset;
			// Return question, when it is directly located in a questionnaire
			// and has not been answered.
			if (isDirectQContainerQuestion(question) &&
					hasValueUndefined(question, session)) {
				return question;
			}
			// Return question, when it is not directly located in a
			// questionnaire but is
			// active on agenda (follow-up question).
			else if (isNotDirectQContainerQuestion(question) &&
					hasValueUndefined(question, session)
					&& session.getBlackboard().getIndication(question).isRelevant()) {
				return question;
			}
			// Recursively traverse for finding follow-up questions and check
			// these, whether they are active on agenda
			else {
				for (TerminologyObject child : question.getChildren()) {
					Question nextqaset = retrieveNextQuestionToBeAnswered((QASet) child, session,
							traversedObjects);
					if (nextqaset != null) {
						return nextqaset;
					}
				}
			}
		}
		// For a QContainer we try to find the first question that is included
		// in the
		// container and has not been answered
		else if (qaset instanceof QContainer) {
			TerminologyObject[] children = qaset.getChildren();
			for (TerminologyObject terminologyObject : children) {
				Question nextqaset = retrieveNextQuestionToBeAnswered((QASet) terminologyObject,
						session, traversedObjects);
				if (nextqaset != null) {
					return nextqaset;
				}
			}
		}
		return null;
	}

	/**
	 * A direct-qcontainer question is a question, that contains at least one
	 * parent, that is a qcontainer. Please note, that it is - in principle - to
	 * define knowledge bases, where a question has 1) a question and 2) a
	 * qcontainer as a parent. In this case, we also vote for a direct
	 * qcontainer question, BUT this circumstance may indicate bad knowledge
	 * base design. (joba)
	 * 
	 * @param question the specified question
	 * @return true, when the parents are only instances of {@link QContainer}.
	 */
	private boolean isDirectQContainerQuestion(Question question) {
		for (TerminologyObject parent : question.getParents()) {
			if ((parent instanceof QContainer)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see NextUnansweredQuestionFormStrategy#isDirectQContainerQuestion(Question)
	 */
	private boolean isNotDirectQContainerQuestion(Question question) {
		return !isDirectQContainerQuestion(question);
	}
}
