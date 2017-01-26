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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;

import static de.d3web.core.knowledge.Indication.State.REPEATED_INDICATED;
import static de.d3web.core.session.values.UndefinedValue.isUndefinedValue;

/**
 * This class always creates a new {@link Form} that contains the one
 * {@link Question}, that should be presented/asked next in the dialog system.
 * Here, the next question is the question that 1) is the next active question
 * on the {@link InterviewAgenda} OR 2) for an indicated {@link QContainer} the
 * next unanswered question in this container
 *
 * @author joba
 */
public class NextUnansweredQuestionFormStrategy implements FormStrategy {

	@Override
	public Form nextForm(List<InterviewObject> agendaEntries, Session session) {
		if (agendaEntries.isEmpty()) {
			return EmptyForm.getInstance();
		}
		else {
			FormStrategyUtils utils = new FormStrategyUtils(session);
			Blackboard blackboard = session.getBlackboard();
			for (InterviewObject object : agendaEntries) {
				if (object instanceof Question) {
					if (isUndefinedValue(blackboard.getValue((ValueObject) object))
							|| blackboard.getIndication(object).hasState(REPEATED_INDICATED)) {
						return new DefaultForm(object.getName(), object, session);
					}
					else {
						for (TerminologyObject child : object.getChildren()) {
							if (child instanceof Question
									&& blackboard.getIndication((Question) child).isRelevant()
									&& isUndefinedValue(blackboard.getValue((Question) child))) {
								return new DefaultForm(child.getName(), (Question) child, session);
							}
						}
					}
				}
				else if (object instanceof QASet) {
					Collection<TerminologyObject> traversedQuestions = new HashSet<>();
					Question nextQuestion = retrieveNextQuestionToBeAnswered((QASet) object, traversedQuestions, utils);
					if (nextQuestion == null) {
						return EmptyForm.getInstance();
					}
					return new DefaultForm(nextQuestion.getName(), nextQuestion, session);
				}
			}
			return EmptyForm.getInstance();
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
	 * Traverses in a depth-first-search all children of the specified
	 * {@link QASet} and returns the first question, that has no value assigned
	 * in the specified session. If no unanswered {@link Question} was be found,
	 * then null is returned.
	 *
	 * @param qaset the specified {@link QASet}
	 * @param traversedObjects objects traversed already to avoid loops
	 * @param utils a utils instance, containing the session
	 * @return the first {@link Question} instance, that is a child of the specified {@link QASet}
	 * and is not answered; null otherwise
	 */
	private Question retrieveNextQuestionToBeAnswered(QASet qaset, Collection<TerminologyObject> traversedObjects, FormStrategyUtils utils) { // NOSONAR
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
					utils.hasValueUndefined(question)) {
				return question;
			}
			// Return question, when it is not directly located in a
			// questionnaire but is
			// active on agenda (follow-up question).
			else if (isNotDirectQContainerQuestion(question) &&
					utils.hasValueUndefined(question)
					&& utils.isIndicated(question)) {
				return question;
			}
			// Recursively traverse for finding follow-up questions and check
			// these, whether they are active on agenda
			else {
				for (TerminologyObject child : question.getChildren()) {
					Question nextqaset = retrieveNextQuestionToBeAnswered(
							(QASet) child, traversedObjects, utils);
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
				Question nextqaset = retrieveNextQuestionToBeAnswered(
						(QASet) terminologyObject, traversedObjects, utils);
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

	@Override
	public List<Form> getForms(InterviewObject object, Session session) {
		return Collections.singletonList(new DefaultForm(object.getName(), object, session));
	}
}
