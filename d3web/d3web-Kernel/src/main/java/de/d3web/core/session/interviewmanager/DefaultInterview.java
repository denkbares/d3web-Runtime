/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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
package de.d3web.core.session.interviewmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.interviewmanager.InterviewAgenda.InterviewState;
import de.d3web.core.session.values.UndefinedValue;

/**
 * The default implementation of {@link Interview}: This class stores an
 * {@link InterviewAgenda} managing the activation/deactivation of
 * {@link Question}/{@link QContainer} instances, that are indicated due to
 * values entered in the specified {@link Session}.
 * 
 * By the default--the {@link QASet} to be answered next by a dialog-- is
 * wrapped in a {@link Form}, that can be retrieved by nextForm(). A
 * {@link FormStrategy} decides about the nature of the next {@link QASet} to be
 * presented in the dialog.
 * 
 * @author joba
 */
public class DefaultInterview implements Interview {

	private final InterviewAgenda agenda;
	private final Session session;

	// Strategy: how to generate the forms for the dialog?
	// E.g.: One question vs. multiple questions presented by the dialog
	private FormStrategy formStrategy;

	/**
	 * Initializes an interview for a specified session based on a specified
	 * knowledge base.
	 * 
	 * @param session the specified session
	 * @param knowledgeBase the specified knowledge base
	 */
	public DefaultInterview(Session session) {
		this.session = session;
		this.agenda = new InterviewAgenda(this.session);
		this.formStrategy = new NextUnansweredQuestionFormStrategy();
	}

	@Override
	public Form nextForm() {
		return formStrategy.nextForm(this.agenda.getCurrentlyActiveObjects(),
				session);
	}

	// TODO: This method has a cyclomatic complexity of 25 (whereas 10 is
	// allowed). Please refactor this!
	@Override
	public void notifyFactChange(PropagationEntry changedFact) {
		Value oldValue = changedFact.getOldValue();
		Value newValue = changedFact.getNewValue();
		if (newValue instanceof Indication) {
			InterviewObject indicatedObject = (InterviewObject) changedFact
					.getObject();
			Indication oldIndication = (Indication) oldValue;
			Indication newIndication = (Indication) newValue;

			// NEUTRAL => INDICATED : 1) append to agenda 2) activate
			if (oldIndication.hasState(State.NEUTRAL)
					&& newIndication.hasState(State.INDICATED)) {
				this.agenda.append(indicatedObject);
				checkParentalQContainer(indicatedObject);
			}
			// ANY => INSTANT_INDICATED : 1) append to agenda 2) activate
			// TODO: finish the work on instance indication, currently
			// INSTANCE_INDICATION is handled like
			// standard indication
			else if (newIndication.hasState(State.INSTANT_INDICATED)) {
				this.agenda.append(indicatedObject);
				checkParentalQContainer(indicatedObject);
			}
			// INDICATED => NEUTRAL : deactivate
			else if (oldIndication.hasState(State.INDICATED)
					&& newIndication.hasState(State.NEUTRAL)) {
				this.agenda.deactivate(indicatedObject);
				checkParentalQContainer(indicatedObject);
			}
			// INDICATED => CONTRA_INDICATED : deactivate
			else if (oldIndication.hasState(State.INDICATED)
					&& newIndication.hasState(State.CONTRA_INDICATED)) {
				this.agenda.deactivate(indicatedObject);
				checkParentalQContainer(indicatedObject);
			}
			// CONTRA_INDICATED => INDICATED : 1) append to agenda if not
			// included 2) activate
			else if (oldIndication.hasState(State.CONTRA_INDICATED)
					&& newIndication.hasState(State.INDICATED)) {
				this.agenda.activate(indicatedObject);
				checkParentalQContainer(indicatedObject);
			}
			else if (oldIndication.hasState(State.INDICATED)
					&& newIndication.hasState(State.INDICATED)) { // NOSONAR
				// INDICATED => INDICATED : noop
			}
			else if (oldIndication.hasState(State.CONTRA_INDICATED)
					&& newIndication.hasState(State.NEUTRAL)) { // NOSONAR
				// CONTRA_INDICATED => NEUTRAL : noop
				checkParentalQContainer(indicatedObject);
			}
			else if (oldIndication.hasState(State.NEUTRAL)
					&& newIndication.hasState(State.CONTRA_INDICATED)) { // NOSONAR
				// NEUTRAL => CONTRA_INDICATED : noop
				checkParentalQContainer(indicatedObject);
			}
			else {
				Logger.getLogger(this.getClass().getName()).warning(
						"UNKNOWN INDICATION STATE: old=(" + oldIndication + ") new=("
								+ newIndication + ")");
			}
		}
		else if (newValue instanceof QuestionValue) {
			// need to check, whether the agenda needs an update due to an
			// answered question
			InterviewObject indicatedObject = (InterviewObject) changedFact
					.getObject();
			if (this.agenda.onAgenda(indicatedObject)) {
				// Check: the VALUE has changed from DEFINED to UNDEFINED =>
				// activate
				if (newValue instanceof UndefinedValue
						&& !(oldValue instanceof UndefinedValue)) {
					this.agenda.activate(indicatedObject);
					checkParentalQContainer(indicatedObject);
				}
				// Check: the VALUE has changed from UNDEFINED to DEFINED =>
				// de-activate
				else if (!(newValue instanceof UndefinedValue)
						&& oldValue instanceof UndefinedValue) {
					this.agenda.deactivate(indicatedObject);
					checkParentalQContainer(indicatedObject);
				}
				// Check: VALUE changed from DEFINED to DEFINED =>
				// de-activate
				else if (!(newValue instanceof UndefinedValue)
						&& !(oldValue instanceof UndefinedValue)) {
					this.agenda.deactivate(indicatedObject);
					checkParentalQContainer(indicatedObject);
				}
				else {
					Logger.getLogger(this.getClass().getName()).warning(
							"UNKNOWN VALUE CHANGE: old=(" + oldValue + ") new=(" + newValue + ")");
				}
			}
			// Need to update indicated QContainers:
			// 1)x When all contained questions have been answered
			// (and no follow-up questions are active), then deactivate
			// 2) When all contained qcontainers are deactivated, then also
			// deactivate
			checkParentalQContainer(indicatedObject);
		}

	}

	/**
	 * Usually, the specified interviewObject has changed. Therefore, we need to
	 * (recursively) check whether the parental {@link QContainer} instances
	 * need to be activated/deactivated due to the value change. For instance,
	 * if every {@link Question} of a {@link QContainer} is inactive, then the
	 * parental {@link QContainer} should be deactivated, too.
	 * 
	 * @param interviewObject
	 */
	private void checkParentalQContainer(InterviewObject interviewObject) {
		List<QContainer> containersOnAgenda = computeParentalContainersOnAgenda(interviewObject);
		for (QContainer qContainer : containersOnAgenda) {
			InterviewState state = checkChildrenState(qContainer);
			switch (state) {
			case ACTIVE:
				getInterviewAgenda().activate(qContainer);
				break;
			case INACTIVE:
				getInterviewAgenda().deactivate(qContainer);
			default:
				break;
			}
		}
	}

	private InterviewState checkChildrenState(TerminologyObject[] children) {
		// If all children are Question instances:
		// Consider not only the direct children, but also their follow-up
		// questions
		// If all questions are answered, then return State=INACTIVE
		// If at least on question is not answered, then return State=ACTIVE
		if (allQuestions(children)) {
			// ACTIVE, when at least one direct children is not answered
			for (TerminologyObject child : children) {
				Value value = session.getBlackboard()
						.getValue((Question) child);
				if (value instanceof UndefinedValue) {
					return InterviewState.ACTIVE;
				}
			}
			// ACTIVE, when at least one follow-up question is ACTIVE
			for (TerminologyObject followUpQuestion : getAllFollowUpChildrenOf(children)) {
				if (isActive((InterviewObject) followUpQuestion)) {
					return InterviewState.ACTIVE;
				}
			}
			return InterviewState.INACTIVE;
		}
		// If all children are QContainer instances:
		// Compute the state of all qcontainers
		// If at least one is ACTIVE, then return State=ACTIVE
		// If all children are INACTIVE, then return State=INACTIVE
		else if (allQContainers(children)) {
			for (TerminologyObject child : children) {
				InterviewState childState = checkChildrenState((QContainer) child);
				if (childState.equals(InterviewState.ACTIVE)) {
					return InterviewState.ACTIVE;
				}
			}
			return InterviewState.INACTIVE;
		}
		// TODO: logger message: Not able to handle the given collection of
		// TerminologyObject instances
		return InterviewState.INACTIVE;
	}

	private List<TerminologyObject> getAllFollowUpChildrenOf(
			TerminologyObject[] objects) {
		Collection<TerminologyObject> followers = new HashSet<TerminologyObject>();
		return getAllFollowUpChildrenOf(objects, followers);
	}

	private List<TerminologyObject> getAllFollowUpChildrenOf(
			TerminologyObject[] objects, Collection<TerminologyObject> followers) {
		List<TerminologyObject> children = new ArrayList<TerminologyObject>();
		for (TerminologyObject object : objects) {
			for (TerminologyObject child : object.getChildren()) {
				if (!followers.contains(child)) {
					followers.add(child);
					children.add(child);
					children.addAll(getAllFollowUpChildrenOf(new TerminologyObject[] { child },
							followers));
				}
			}
		}
		return children;
	}

	private InterviewState checkChildrenState(QContainer container) {
		return checkChildrenState(container.getChildren());
	}

	/**
	 * Checks, whether the specified objects are all instances of
	 * {@link QContainer}.
	 * 
	 * @param objects the specified objects
	 * @return true, when the specified objects are all instances of
	 *         {@link QContainer}.
	 */
	private boolean allQContainers(TerminologyObject[] objects) {
		for (TerminologyObject object : objects) {
			if (!(object instanceof QContainer)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks, whether the specified objects are all instances of
	 * {@link Question}.
	 * 
	 * @param objects the specified objects
	 * @return true, when the specified objects are all instances of
	 *         {@link Question}.
	 */
	private boolean allQuestions(TerminologyObject[] objects) {
		for (TerminologyObject object : objects) {
			if (!(object instanceof Question)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * For a specified {@link InterviewObject} instance all parental QContainers
	 * are computed, that are included in the current {@link InterviewAgenda}.
	 * 
	 * @param interviewObject the specified {@link InterviewObject} instance
	 * @return all (recursively) parental {@link QContainer} instances that are
	 *         on the agenda
	 */
	private List<QContainer> computeParentalContainersOnAgenda(
			InterviewObject interviewObject) {
		List<QContainer> parentsOnAgenda = new ArrayList<QContainer>();
		List<InterviewObject> visitedContainers = new ArrayList<InterviewObject>();
		computeParentalContainersOnAgenda(interviewObject, parentsOnAgenda, visitedContainers);
		return parentsOnAgenda;
	}

	private void computeParentalContainersOnAgenda(
			InterviewObject interviewObject, List<QContainer> parentsOnAgenda, List<InterviewObject> visitedContainers) {
		for (TerminologyObject parent : interviewObject.getParents()) {
			if (!visitedContainers.contains(parent)) {
				visitedContainers.add((InterviewObject) parent);
				if (parent instanceof QContainer
						&& getInterviewAgenda().onAgenda(
								(InterviewObject) parent)) {
					parentsOnAgenda.add((QContainer) parent);
				}
				if (parent.getParents().length > 0) {
					computeParentalContainersOnAgenda((InterviewObject) parent, parentsOnAgenda,
							visitedContainers);
				}
			}
		}
	}

	@Override
	public InterviewAgenda getInterviewAgenda() {
		return this.agenda;
	}

	@Override
	public void setFormStrategy(FormStrategy strategy) {
		this.formStrategy = strategy;
	}

	@Override
	public boolean isActive(InterviewObject interviewObject) {
		return getInterviewAgenda().hasState(interviewObject,
				InterviewState.ACTIVE);
	}
}
