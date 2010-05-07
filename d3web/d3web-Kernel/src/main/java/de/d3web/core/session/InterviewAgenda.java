package de.d3web.core.session;

import java.util.List;

import de.d3web.core.knowledge.TerminologyObject;

class InterviewAgenda {
	private Session session;
	private enum State {
		ACTIVE, INACTIVE;
	}
	private List<AgendaEntry> agenda;

	private class AgendaEntry {
		TerminologyObject terminologyObject;
		State             state;
		private AgendaEntry(TerminologyObject terminologyObject, State state) {
			this.terminologyObject = terminologyObject;
			this.state             = state;
		}
		private boolean isActive() {
			return (this.state == state.ACTIVE);
		}
	}
	
	
	InterviewAgenda(Session session) {
		this.session = session;
	}
	
	boolean append(TerminologyObject terminologyObject) {
		return agenda.add(new AgendaEntry(terminologyObject, State.ACTIVE));
	}
	
}
