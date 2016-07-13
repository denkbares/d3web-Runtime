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
package de.d3web.interview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.interviewmanager.InterviewAgenda.InterviewState;
import de.d3web.core.session.values.UndefinedValue;
import com.denkbares.utils.Log;

/**
 * The default implementation of {@link Interview}: This class stores an
 * {@link InterviewAgenda} managing the activation/deactivation of
 * {@link Question}/{@link QContainer} instances, that are indicated due to
 * values entered in the specified {@link Session}.
 * <p>
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
	 */
	public DefaultInterview(Session session) {
		this.session = session;
		this.agenda = new InterviewAgenda();
		this.formStrategy = new NextUnansweredQuestionFormStrategy();
	}

	@Override
	public Form nextForm() {
		return formStrategy.nextForm(this.agenda.getCurrentlyActiveObjects(),
				session);
	}

	@Override
	public void notifyFactChange(PropagationEntry changedFact) {
		Value oldValue = changedFact.getOldValue();
		Value newValue = changedFact.getNewValue();
		if (newValue instanceof Indication) {
			notifyIndicationChange(changedFact, oldValue, newValue);
		}
		else if (newValue instanceof QuestionValue) {
			notifyQuestionValueChange(changedFact, oldValue, newValue);
		}
	}

	private void notifyQuestionValueChange(PropagationEntry changedFact, Value oldValue, Value newValue) {
		// need to check, whether the agenda needs an update due to an
		// answered question
		InterviewObject indicatedObject = (InterviewObject) changedFact
				.getObject();
		if (this.agenda.onAgenda(indicatedObject)) {
			// Check: the VALUE has changed from DEFINED to UNDEFINED =>
			// activate
			if (UndefinedValue.isUndefinedValue(newValue)
					&& UndefinedValue.isNotUndefinedValue(oldValue)) {
				this.agenda.activate(indicatedObject);
			}
			// Check: the VALUE has changed from UNDEFINED to DEFINED =>
			// de-activate
			else if (UndefinedValue.isNotUndefinedValue(newValue)
					&& UndefinedValue.isUndefinedValue(oldValue)) {
				this.agenda.deactivateFirst(indicatedObject);
			}
			// Check: VALUE changed from DEFINED to DEFINED =>
			// de-activate
			else if (UndefinedValue.isNotUndefinedValue(newValue)
					&& UndefinedValue.isNotUndefinedValue(oldValue)) {
				this.agenda.deactivateFirst(indicatedObject);
			}
			else {
				Log.warning("UNKNOWN VALUE CHANGE: old: " + oldValue + ", new: " + newValue);
			}
		}
		// Need to update indicated QContainers:
		// 1) When all contained questions have been answered
		// (and no follow-up questions are active), then deactivate
		// 2) When all contained qcontainers are deactivated, then also
		// deactivate
		checkParentalQContainer(indicatedObject);
	}

	private void notifyIndicationChange(PropagationEntry changedFact, Value oldValue, Value newValue) {
		InterviewObject indicatedObject = (InterviewObject) changedFact
				.getObject();
		Indication oldIndication = (Indication) oldValue;
		Indication newIndication = (Indication) newValue;

		// #### MULTIPLE_INDICATED ####
		if (newIndication.hasState(State.MULTIPLE_INDICATED)) {
			agenda.delete(indicatedObject, oldIndication);
			Collection<Fact> interviewFacts = session.getBlackboard().getInterviewFacts(indicatedObject);
			for (Fact fact : interviewFacts) {
				Indication factIndication = (Indication) fact.getValue();
				if (factIndication.hasState(State.MULTIPLE_INDICATED)) {
					agenda.append(indicatedObject, factIndication);
				}
			}
		}
		else if (oldIndication.hasState(State.MULTIPLE_INDICATED)) {
			agenda.delete(indicatedObject, oldIndication);
			// after removing act like a new indication
			if (!newIndication.hasState(State.NEUTRAL)) {
				notifyIndicationChange(changedFact, new Indication(State.NEUTRAL, 0), newValue);
			}
		}

		// #### INDICATED ####
		else if (newIndication.hasState(State.INDICATED)) {
			if (!oldIndication.equals(newIndication)) {
				this.agenda.append(indicatedObject, newIndication);
				checkParentalQContainer(indicatedObject);
			}
		}

		// #### INSTANT_INDICATED ####
		else if (newIndication.hasState(State.INSTANT_INDICATED)) {
			if (!oldIndication.equals(newIndication)) {
				this.agenda.append(indicatedObject, newIndication);
				checkParentalQContainer(indicatedObject);
			}
		}

		// #### REPEATED_INDICATED ####
		else if (newIndication.hasState(State.REPEATED_INDICATED)) {
			this.agenda.deactivate(indicatedObject);
			this.agenda.activate(indicatedObject, newIndication);
			checkParentalQContainer(indicatedObject);
		}

		// #### NEUTRAL / RELEVANT ####
		else if (newIndication.hasState(State.NEUTRAL)
				|| newIndication.hasState(State.RELEVANT)) {

			if (oldIndication.hasState(State.REPEATED_INDICATED)
					|| oldIndication.hasState(State.INDICATED)
					|| oldIndication.hasState(State.INSTANT_INDICATED)) {
				this.agenda.deactivate(indicatedObject);
				checkParentalQContainer(indicatedObject);
			}
			else if (oldIndication.hasState(State.CONTRA_INDICATED)) {
				checkParentalQContainer(indicatedObject);
			}
		}

		// #### CONTRA_INDICATED ####
		else if (newIndication.hasState(State.CONTRA_INDICATED)) {
			if (oldIndication.hasState(State.INDICATED)
					|| oldIndication.hasState(State.REPEATED_INDICATED)
					|| oldIndication.hasState(State.INSTANT_INDICATED)) {
				this.agenda.deactivate(indicatedObject);
				checkParentalQContainer(indicatedObject);
			}
			else if (oldIndication.hasState(State.NEUTRAL)
					|| oldIndication.hasState(State.RELEVANT)) {
				checkParentalQContainer(indicatedObject);
			}
		}

		else if (oldIndication.getState() != newIndication.getState()) {
			Log.warning("Unknown indication state: old: " + oldIndication + ", new: "
					+ newIndication + ", ignoring it...");
		}
	}

	/**
	 * Usually, the specified interviewObject has changed. Therefore, we need to
	 * (recursively) check whether the parental {@link QContainer} instances
	 * need to be activated/deactivated due to the value change. For instance,
	 * if every {@link Question} of a {@link QContainer} is inactive, then the
	 * parental {@link QContainer} should be deactivated, too.
	 *
	 * @param interviewObject object for which to check parent
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
					getInterviewAgenda().deactivateFirst(qContainer);
				default:
					break;
			}
		}
	}

	private InterviewState checkChildrenState(TerminologyObject[] children) {
		for (TerminologyObject child : children) {
			if (child instanceof InterviewObject) {
				// ignore contraindicated children
				if (session.getBlackboard().getIndication((InterviewObject) child).isContraIndicated()) {
					continue;
				}
				// If at least on question is not answered, then return
				// State=ACTIVE
				if (child instanceof Question) {
					Value value = session.getBlackboard()
							.getValue((Question) child);

					if (value instanceof UndefinedValue) {
						return InterviewState.ACTIVE;
					}
					// ACTIVE, when at least one follow-up question is ACTIVE
					for (TerminologyObject followUpQuestion : getAllFollowUpChildrenOf(new TerminologyObject[] { child })) {
						if (isActive((InterviewObject) followUpQuestion)) {
							return InterviewState.ACTIVE;
						}
						else if (session.getBlackboard().getIndication(
								(InterviewObject) followUpQuestion).isRelevant()
								&& UndefinedValue.isUndefinedValue(session.getBlackboard().getValue(
								(ValueObject) followUpQuestion))) {
							return InterviewState.ACTIVE;
						}
					}
				}
				// If at least on child qcontainer is active, then return
				// State=ACTIVE
				else if (child instanceof QContainer) {
					InterviewState childState = checkChildrenState((QContainer) child);
					if (childState == InterviewState.ACTIVE) {
						return InterviewState.ACTIVE;
					}
				}
			}
		}
		return InterviewState.INACTIVE;
	}

	private static List<TerminologyObject> getAllFollowUpChildrenOf(
			TerminologyObject[] objects) {
		Collection<TerminologyObject> followers = new HashSet<>();
		return getAllFollowUpChildrenOf(objects, followers);
	}

	private static List<TerminologyObject> getAllFollowUpChildrenOf(
			TerminologyObject[] objects, Collection<TerminologyObject> followers) {
		List<TerminologyObject> children = new ArrayList<>();
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
	 * For a specified {@link InterviewObject} instance all parental QContainers
	 * are computed, that are included in the current {@link InterviewAgenda}.
	 *
	 * @param interviewObject the specified {@link InterviewObject} instance
	 * @return all (recursively) parental {@link QContainer} instances that are
	 * on the agenda
	 */
	private List<QContainer> computeParentalContainersOnAgenda(
			InterviewObject interviewObject) {
		List<QContainer> parentsOnAgenda = new ArrayList<>();
		List<InterviewObject> visitedContainers = new ArrayList<>();
		computeParentalContainersOnAgenda(interviewObject, parentsOnAgenda, visitedContainers);
		return parentsOnAgenda;
	}

	private void computeParentalContainersOnAgenda(
			InterviewObject interviewObject, List<QContainer> parentsOnAgenda, List<InterviewObject> visitedContainers) {
		for (TerminologyObject parent : interviewObject.getParents()) {
			//noinspection SuspiciousMethodCalls
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
	public void setFormStrategy(@SuppressWarnings("deprecation") de.d3web.core.session.interviewmanager.FormStrategy strategy) {
		this.formStrategy = (FormStrategy) strategy;
	}

	@Override
	public boolean isActive(InterviewObject interviewObject) {
		return getInterviewAgenda().hasState(interviewObject, InterviewState.ACTIVE);
	}

	@Override
	public void setFormStrategy(FormStrategy strategy) {
		this.formStrategy = strategy;
	}

	@Override
	public FormStrategy getFormStrategy() {
		return this.formStrategy;
	}
}
