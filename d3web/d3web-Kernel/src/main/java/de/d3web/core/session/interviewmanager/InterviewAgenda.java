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
import java.util.Collections;
import java.util.List;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.session.Session;

/**
 * The InterviewAgenda represents the Interview Objects, which should appear
 * next in the Interview
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 */
public class InterviewAgenda {

	public enum InterviewState {
		ACTIVE, INACTIVE;
	}

	private List<AgendaEntry> agenda;
	private final Session session;
	// Strategy: how to sort the entries on the agenda?
	private AgendaSortingStrategy agendaSortingStrategy;

	public class AgendaEntry implements Comparable<AgendaEntry> {

		InterviewObject interviewObject;
		InterviewState state;

		private AgendaEntry(InterviewObject interviewObject, InterviewState state) {
			this.interviewObject = interviewObject;
			this.state = state;
		}

		private boolean equalsInterviewObject(InterviewObject interviewObject) {
			return this.interviewObject.equals(interviewObject);
		}

		public boolean hasState(InterviewState state) {
			return this.state.equals(state);
		}

		@Override
		public String toString() {
			return this.interviewObject + " [" + this.state + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime
					* result
					+ ((interviewObject == null) ? 0 : interviewObject
							.hashCode());
			result = prime * result + ((state == null) ? 0 : state.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			AgendaEntry other = (AgendaEntry) obj;
			if (!getOuterType().equals(other.getOuterType())) return false;
			if (interviewObject == null) {
				if (other.interviewObject != null) return false;
			}
			else if (!interviewObject.equals(other.interviewObject)) return false;
			if (state == null) {
				if (other.state != null) return false;
			}
			else if (!state.equals(other.state)) return false;
			return true;
		}

		private InterviewAgenda getOuterType() {
			return InterviewAgenda.this;
		}

		@Override
		public int compareTo(AgendaEntry o) {
			// TODO use hierarchy index to sort two AgendaEntry
			return 0;
		}
	}

	/**
	 * A new {@link InterviewAgenda} instance is created to be used with the
	 * specified {@link KnowledgeBase} and the
	 * {@link DFSTreeAgendaSortingStrategy} is used for ordering the
	 * {@link AgendaEntry} instances on the agenda. The initial questions are
	 * put on the agenda as the first action.
	 * 
	 * @param knowledgeBase the specified {@link KnowledgeBase}.
	 */
	public InterviewAgenda(Session session) {
		agenda = new ArrayList<AgendaEntry>();
		this.session = session;
		this.agendaSortingStrategy = new DFSTreeAgendaSortingStrategy(this.session);

		// Put the init questions to the agenda first:
		// TODO: move to PSMethodInit.init() by adding indication to blackboard
		List<? extends QASet> initQuestions = this.session.getKnowledgeBase().getInitQuestions();
		for (QASet initQuestion : initQuestions) {
			append(initQuestion);
		}
	}

	/**
	 * Appends an {@link InterviewObject} to the agenda
	 * 
	 * @param interviewObject {@link InterviewObject}
	 */
	public void append(InterviewObject interviewObject) {
		trace("Append: " + interviewObject);
		if (onAgenda(interviewObject)) {
			activate(interviewObject);
		}
		else {
			agenda.add(new AgendaEntry(interviewObject, InterviewState.ACTIVE));
			organizeAgenda();
		}
	}

	/**
	 * Deactivates an {@link InterviewObject} on the agenda
	 * 
	 * @param interviewObject {@link InterviewObject}
	 */
	public void deactivate(InterviewObject interviewObject) {
		trace("De-activate: " + interviewObject);
		AgendaEntry entry = findAgendaEntry(interviewObject);
		if (entry != null) {
			entry.state = InterviewState.INACTIVE;
		}
		organizeAgenda();
	}

	/**
	 * Activates an {@link InterviewObject} on the agenda
	 * 
	 * @param interviewObject {@link InterviewObject}
	 */
	public void activate(InterviewObject interviewObject) {
		trace("Activate: " + interviewObject);
		AgendaEntry entry = findAgendaEntry(interviewObject);
		if (entry == null) {
			append(interviewObject);
		}
		else {
			entry.state = InterviewState.ACTIVE;
		}
		organizeAgenda();
	}

	/**
	 * Sorts the agenda with respect to the newly added items.
	 */
	private void organizeAgenda() {
		this.agenda = agendaSortingStrategy.sort(this.agenda);
	}

	private void trace(String string) {
		// System.out.println(string);
	}

	private AgendaEntry findAgendaEntry(InterviewObject interviewObject) {
		for (AgendaEntry entry : this.agenda) {
			if (entry.equalsInterviewObject(interviewObject)) {
				return entry;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return this.agenda.toString();
	}

	/**
	 * Checks, whether an entry -- that is also indicated -- is available on the
	 * agenda.
	 * 
	 * @return false, when the agenda contains at least one entry that is ACTIVE
	 */
	public boolean isEmpty() {
		for (AgendaEntry entry : this.agenda) {
			if (entry.hasState(InterviewState.ACTIVE)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks, if the specified {@link InterviewObject} instance is placed on
	 * the agenda (independent from its state).
	 * 
	 * @param interviewObject the specified {@link InterviewObject} instance
	 * @return true, if the specified object is on the agenda
	 */
	public boolean onAgenda(InterviewObject interviewObject) {
		return (findAgendaEntry(interviewObject) != null);
	}

	public boolean hasState(InterviewObject interviewObject, InterviewState state) {
		if (onAgenda(interviewObject)) {
			AgendaEntry entry = findAgendaEntry(interviewObject);
			return entry.hasState(state);
		}
		else {
			return false;
		}
	}

	/**
	 * Gives the (unmodifiable) list of currently active objects on the agenda.
	 * 
	 * @return an immutable list of the objects that are currently active on the
	 *         agenda
	 */
	public List<InterviewObject> getCurrentlyActiveObjects() {
		List<InterviewObject> objects = new ArrayList<InterviewObject>();
		for (AgendaEntry entry : this.agenda) {
			if (entry.hasState(InterviewState.ACTIVE)) {
				objects.add(entry.interviewObject);
			}
		}
		return Collections.unmodifiableList(objects);
	}

	/**
	 * Returns the currently used {@link AgendaSortingStrategy} of this
	 * {@link InterviewAgenda}.
	 * 
	 * @return the currently used sorting strategy of this agenda
	 */
	public AgendaSortingStrategy getAgendaSortingStrategy() {
		return agendaSortingStrategy;
	}

	/**
	 * Sets the {@link AgendaSortingStrategy} that should be used for ordering
	 * the {@link AgendaEntry} instances on this agenda.
	 * 
	 * @param agendaSortingStrategy
	 */
	public void setAgendaSortingStrategy(AgendaSortingStrategy agendaSortingStrategy) {
		this.agendaSortingStrategy = agendaSortingStrategy;
	}

}
