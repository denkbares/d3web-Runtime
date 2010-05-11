package de.d3web.core.session.interviewmanager;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;

public class DefaultInterview implements Interview {

	private final InterviewAgenda agenda;
	private KnowledgeBase knowledgeBase;

	// Strategy: how to generate the forms for the dialog? 
	// E.g.: One question vs. multiple questions presented by the dialog
	private FormStrategy formStrategy; 

	
	
	public DefaultInterview(KnowledgeBase knowledgeBase) {
		this.knowledgeBase = knowledgeBase;
		this.agenda = new InterviewAgenda(this.knowledgeBase);
		this.formStrategy = new OneQuestionFormStrategy();
	}

	@Override
	public Form nextForm() {
		return formStrategy.nextForm(this.agenda.getCurrentlyActiveObjects());
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
				// Check: the value has changed from a defined to an undefined state => activate again
				if (newValue instanceof UndefinedValue && !(oldValue instanceof UndefinedValue)) {
					this.agenda.activate(indicatedObject);
				}
				// Check: the value has changed from undefined to defined => de-activate
				else if (!(newValue instanceof UndefinedValue) && oldValue instanceof UndefinedValue) {
					this.agenda.deactivate(indicatedObject);
				}
				else {
					System.out.println("UNKNOWN VALUE CHANGE: old=(" + oldValue+ ") new=("+newValue+")");
				}
				// TODO: Need to update indicated QContainers: 
				// 1)x When all contained questions have been answered, then deactivate
				// 2) When all contained qcontainers are deactivated, then also deactivate
			}
		}

	}

	@Override
	public InterviewAgenda getInterviewAgenda() {
		return this.agenda;
	}
}
