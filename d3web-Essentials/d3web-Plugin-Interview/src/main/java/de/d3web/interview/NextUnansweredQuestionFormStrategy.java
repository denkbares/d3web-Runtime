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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;

import static de.d3web.core.knowledge.Indication.State.REPEATED_INDICATED;
import static de.d3web.core.session.values.UndefinedValue.isUndefinedValue;

/**
 * This class always creates a new {@link Form} that contains the one {@link Question}, that should be presented/asked
 * next in the dialog system. Here, the next question is the question that 1) is the next active question on the {@link
 * InterviewAgenda} OR 2) for an indicated {@link QContainer} the next unanswered question in this container
 *
 * @author joba
 */
public class NextUnansweredQuestionFormStrategy implements FormStrategy {

	private boolean groupTopLevelQuestions = false;

	/**
	 * Returns true if the top-level questions of each questionnaire should be grouped in a single form. By default each
	 * question has its own form. If this flag is true, each follow-up question still gets its own form, but the
	 * top-level questions of each questionnaire will be collected into a single form.
	 */
	public boolean isGroupTopLevelQuestions() {
		return groupTopLevelQuestions;
	}

	/**
	 * Sets if the top-level questions of each questionnaire should be grouped in a single form. By default (if false)
	 * each question has its own form. If this flag is set to true, each follow-up question still gets its own form, but
	 * the top-level questions of each questionnaire will be collected into a single form.
	 */
	public void setGroupTopLevelQuestions(boolean groupTopLevelQuestions) {
		this.groupTopLevelQuestions = groupTopLevelQuestions;
	}

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
					return retrieveNextForm((QASet) object, utils);
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

	protected Form retrieveNextForm(QASet container, FormStrategyUtils utils) {
		QASet nextItem = retrieveNextItemToBeAnswered(container, new HashSet<>(), utils);
		if (nextItem == null) {
			return EmptyForm.getInstance();
		}
		return (nextItem instanceof QContainer)
				? new TopLevelForm((QContainer) nextItem)
				: new DefaultForm(nextItem.getName(), nextItem, utils.getSession());
	}

	/**
	 * Traverses in a depth-first-search all children of the specified {@link QASet} and returns the first question,
	 * that has no value assigned in the specified session. If no unanswered {@link Question} was be found, then null is
	 * returned.
	 * <p>
	 * if the flag {@link #isGroupTopLevelQuestions()} is set to true, the method may return a qContainer instead of a
	 * question.
	 *
	 * @param qaset            the specified {@link QASet}
	 * @param traversedObjects objects traversed already to avoid loops
	 * @param utils            a utils instance, containing the session
	 * @return the first {@link Question} instance, that is a child of the specified {@link QASet} and is not answered;
	 * null otherwise
	 */
	private QASet retrieveNextItemToBeAnswered(QASet qaset, Set<TerminologyObject> traversedObjects, FormStrategyUtils utils) { // NOSONAR
		// Termination of recursive traversal: Required for possibly cyclic
		// question hierarchies
		if (!traversedObjects.add(qaset)) {
			return null;
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
					QASet nextqaset = retrieveNextItemToBeAnswered(
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
				QASet nextItem = retrieveNextItemToBeAnswered(
						(QASet) terminologyObject, traversedObjects, utils);
				if (nextItem != null) {
					// if we want to group root questions,
					// and the fond item is a root question of this examined qContainer,
					// use the qcontainer instead
					if (isGroupTopLevelQuestions() && (nextItem instanceof Question)
							&& isDirectQContainerQuestion((QContainer) qaset, (Question) nextItem)) {
						return qaset;
					}
					// else use the fount item itself
					return nextItem;
				}
			}
		}
		return null;
	}

	/**
	 * A direct-qcontainer question is a question, that contains at least one parent, that is a qcontainer. Please note,
	 * that it is - in principle - to define knowledge bases, where a question has 1) a question and 2) a qcontainer as
	 * a parent. In this case, we also vote for a direct qcontainer question, BUT this circumstance may indicate bad
	 * knowledge base design. (joba)
	 *
	 * @param question the specified question
	 * @return true, when the parents are only instances of {@link QContainer}.
	 */
	private static boolean isDirectQContainerQuestion(Question question) {
		for (TerminologyObject parent : question.getParents()) {
			if ((parent instanceof QContainer)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * A direct-qcontainer question is a question, that contains at least one parent, that is a qcontainer. Please note,
	 * that it is - in principle - to define knowledge bases, where a question has 1) a question and 2) a qcontainer as
	 * a parent. In this case, we also vote for a direct qcontainer question, BUT this circumstance may indicate bad
	 * knowledge base design. (joba)
	 *
	 * @param question the specified question
	 * @return true, when the parents are only instances of {@link QContainer}.
	 */
	private static boolean isDirectQContainerQuestion(QContainer parent, Question question) {
		for (TerminologyObject p : question.getParents()) {
			if (Objects.equals(parent, p)) {
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

	/**
	 * Form that shows all root questions of the qContainer as a single form
	 */
	private static class TopLevelForm implements Form {

		private final QContainer parent;
		private final List<Question> topLevelQuestions;

		public TopLevelForm(QContainer parent) {
			this.parent = parent;
			this.topLevelQuestions = Stream.of(parent.getChildren())
					.filter(Question.class::isInstance).map(Question.class::cast)
					.filter(q -> isDirectQContainerQuestion(parent, q)).collect(Collectors.toList());
		}

		@NotNull
		@Override
		public String getName() {
			return parent.getName();
		}

		@NotNull
		@Override
		public String getPrompt(Locale lang) {
			return MMInfo.getPrompt(parent, lang);
		}

		@Override
		public boolean isEmpty() {
			return getActiveQuestions().isEmpty();
		}

		@Override
		public List<Question> getActiveQuestions() {
			return topLevelQuestions;
		}

		@Override
		public List<Question> getPotentialQuestions() {
			return topLevelQuestions;
		}

		@Override
		public QContainer getRoot() {
			return parent;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof TopLevelForm)) return false;
			TopLevelForm that = (TopLevelForm) o;
			return Objects.equals(parent, that.parent);
		}

		@Override
		public int hashCode() {
			return Objects.hash(parent);
		}
	}
}
