package de.d3web.core.session;

import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.InterviewObject;

public class DefaultInterview implements Interview {

	private final Session session;
	private final List<InterviewObject> agenda = new LinkedList<InterviewObject>();

	public DefaultInterview(Session session) {
		this.session = session;
	}

	@Override
	public InterviewObject getCurrentInterviewObject() {
		if (agenda.isEmpty()) return null;
		return agenda.get(0);
	}
	
	public InterviewObject[] getAgenda() {
		return this.agenda.toArray(new InterviewObject[this.agenda.size()]);
	}

	// TODO: this notification must be called after the facts have been propagated (or at the end of the propagation)
	void notifyIndicationChange(InterviewObject object) {
		Indication indication = this.session.getBlackboard().getIndication(object);
		switch (indication.getState()) {
		case INDICATED:
			if (!agenda.contains(object)) {
				agenda.add(object);
			}
			break;
		case INSTANT_INDICATED:
			agenda.remove(object);
			agenda.add(0, object);
			break;
		case NEUTRAL:
		case CONTRA_INDICATED:
			agenda.remove(object);
			break;
		default:
			throw new IllegalStateException();
		}
	}
	
	// TODO: this notification must be called after the facts have been propagated (or at the end of the propagation)
	void notifyValueChange(InterviewObject object) {
		this.session.getBlackboard().removeInterviewFacts(object);
		this.agenda.remove(object);
	}

	@Override
	public void notifyFactChange(PropagationEntry changedFact) {
		Value oldValue = (Value)changedFact.getOldValue();
		Value newValue = (Value)changedFact.getNewValue();
		if (newValue instanceof Indication) {
			// System.out.println(oldValue + " \t => \t " + newValue + "\t for " + changedFact.getObject()) ;
		} 
		
		// NEUTRAL           => INDICATED         : 1) append to agenda 2) activate
		// NEUTRAL           => CONTRA_INDICATED  : noop
		// INDICATED         => NEUTRAL           : deactivate
		// INDICATED         => CONTRA_INDICATED  : deactivate
		// CONTRA_INDICATED  => INDICATED         : 1) append to agenda if not included 2) activate
		// CONTRA_INDICATED  => NEUTRAL           : noop
		
	}
}














