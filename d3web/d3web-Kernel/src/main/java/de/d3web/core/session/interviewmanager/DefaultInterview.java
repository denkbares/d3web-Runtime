package de.d3web.core.session.interviewmanager;

import java.util.ArrayList;
import java.util.List;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.interviewmanager.InterviewAgenda.InterviewState;
import de.d3web.core.session.values.UndefinedValue;

public class DefaultInterview implements Interview {

	private final InterviewAgenda agenda;
	private KnowledgeBase knowledgeBase;
	private Session session;

	// Strategy: how to generate the forms for the dialog? 
	// E.g.: One question vs. multiple questions presented by the dialog
	private FormStrategy formStrategy; 

	
	
	public DefaultInterview(Session session, KnowledgeBase knowledgeBase) {
		this.session = session;
		this.knowledgeBase = knowledgeBase;
		this.agenda = new InterviewAgenda(this.knowledgeBase);
		this.formStrategy = new NextUnansweredQuestionFormStrategy();
	}

	@Override
	public Form nextForm() {
		return formStrategy.nextForm(this.agenda.getCurrentlyActiveObjects(), session);
	}

	@Override
	public void notifyFactChange(PropagationEntry changedFact) {
		Value oldValue = (Value) changedFact.getOldValue();
		Value newValue = (Value) changedFact.getNewValue();
		if (newValue instanceof Indication) {
			InterviewObject indicatedObject = (InterviewObject)changedFact.getObject();
			Indication      oldIndication   = (Indication)oldValue;
			Indication      newIndication   = (Indication)newValue;
			
			// NEUTRAL => INDICATED : 1) append to agenda 2) activate
			if (oldIndication.hasState(State.NEUTRAL) && newIndication.hasState(State.INDICATED)) {
				this.agenda.append(indicatedObject);
			}
			// INDICATED => NEUTRAL : deactivate
			else if  (oldIndication.hasState(State.INDICATED) && newIndication.hasState(State.NEUTRAL)) {
				this.agenda.deactivate(indicatedObject);
			}
			// INDICATED => CONTRA_INDICATED : deactivate
			else if  (oldIndication.hasState(State.INDICATED) && newIndication.hasState(State.CONTRA_INDICATED)) {
				this.agenda.deactivate(indicatedObject);
			}
			// CONTRA_INDICATED => INDICATED : 1) append to agenda if not included 2) activate
			else if  (oldIndication.hasState(State.CONTRA_INDICATED) && newIndication.hasState(State.INDICATED)) {
				this.agenda.activate(indicatedObject);
			}
			else if  (oldIndication.hasState(State.INDICATED) && newIndication.hasState(State.INDICATED)) {
				// INDICATED => INDICATED      : noop
			}
			else if  (oldIndication.hasState(State.CONTRA_INDICATED) && newIndication.hasState(State.NEUTRAL)) {
				// CONTRA_INDICATED => NEUTRAL : noop
			}
			else if  (oldIndication.hasState(State.NEUTRAL) && newIndication.hasState(State.CONTRA_INDICATED)) {
				// NEUTRAL => CONTRA_INDICATED : noop
			}
			// TODO: INSTANT_INDICATION
			else {
				System.out.println("UNKNOWN INDICATION STATE: old=(" + oldIndication+ ") new=("+newIndication+")");
			}
		}
		else if (newValue instanceof QuestionValue) {
			// need to check, whether the aganda needs an update due to an answered question
			InterviewObject indicatedObject = (InterviewObject)changedFact.getObject();
			if (this.agenda.onAgenda(indicatedObject)) {
				// Check: the VALUE has changed from DEFINED to UNDEFINED => activate 
				if (newValue instanceof UndefinedValue && !(oldValue instanceof UndefinedValue)) {
					this.agenda.activate(indicatedObject);
					checkParentalQContainer(indicatedObject);
				}
				// Check: the VALUE has changed from UNDEFINED to DEFINED => de-activate
				else if (!(newValue instanceof UndefinedValue) && oldValue instanceof UndefinedValue) {
					this.agenda.deactivate(indicatedObject);
					checkParentalQContainer(indicatedObject);
				}
				else {
					System.out.println("UNKNOWN VALUE CHANGE: old=(" + oldValue+ ") new=("+newValue+")");
				}
			}
			// Need to update indicated QContainers: 
			// 1)x When all contained questions have been answered, then deactivate
			// 2) When all contained qcontainers are deactivated, then also deactivate
			checkParentalQContainer(indicatedObject);
		}

	}

	/**
	 * Usually, the specified interviewObject has changed. Therefore, we need to 
	 * (recursively) check whether the parental {@link QContainer} instances need to be
	 * activated/deactivated due to the value change. 
	 * For instance, if every {@link Question} of a {@link QContainer} is inactive, then 
	 * the parental {@link QContainer} should be deactivated, too. 
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
		//   If all questions are answered, then return State=INACTIVE
		//   If at least on question is not answered, then return State=ACTIVE
		if (allQuestions(children)) {
			for (TerminologyObject child : children) {
				Value value = session.getBlackboard().getValue((Question)child);
				if (value instanceof UndefinedValue) {
					return InterviewState.ACTIVE;
				}
			}
			return InterviewState.INACTIVE;
		}
		// If all children are QContainer instances:
		//   Compute the state of all qcontainers
		//   If at least one is ACTIVE, then return State=ACTIVE
		//   If all children are INACTIVE, then return State=INACTIVE
		else if (allQContainers(children)) {
			for (TerminologyObject child : children) {
				InterviewState childState = checkChildrenState((QContainer)child);
				if (childState.equals(InterviewState.ACTIVE)) {
					return InterviewState.ACTIVE;
				}
			}
			return InterviewState.INACTIVE;
		}
		// TODO: logger message: Not able to handle the given collection of TerminologyObject instances
		return InterviewState.INACTIVE;
	}
	
	private InterviewState checkChildrenState(QContainer container) {
		return checkChildrenState(container.getChildren());
	}

	private boolean allQContainers(TerminologyObject[] objects) {
		for (TerminologyObject object : objects) {
			if ((object instanceof QContainer) == false) {
				return false;
			}
		}
		return true;
	}

	private boolean allQuestions(TerminologyObject[] objects) {
		for (TerminologyObject object : objects) {
			if ((object instanceof Question) == false) {
				return false;
			}
		}
		return true;
	}

	/**
	 * For a specified {@link InterviewObject} instance all parental QContainers are 
	 * computed, that are included in the current {@link InterviewAgenda}.
	 * @param interviewObject the specified {@link InterviewObject} instance
	 * @return all (recursively) parental {@link QContainer} instances that are on the agenda
	 */
	private List<QContainer> computeParentalContainersOnAgenda(InterviewObject interviewObject) {
		List<QContainer> containers = new ArrayList<QContainer>();
		for (TerminologyObject parent : interviewObject.getParents()) {
			if (parent instanceof QContainer &&
				getInterviewAgenda().onAgenda((InterviewObject)parent)) {
				containers.add((QContainer)parent);
			}
			if (parent.getParents().length > 0) {
				for (TerminologyObject parentOfParent : parent.getParents()) {
					containers.addAll(computeParentalContainersOnAgenda((InterviewObject)parentOfParent));
				}
			}
		}
		return containers;
	}


	@Override
	public InterviewAgenda getInterviewAgenda() {
		return this.agenda;
	}
}
