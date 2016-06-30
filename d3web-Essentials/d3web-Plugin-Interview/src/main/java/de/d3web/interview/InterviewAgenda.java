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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.interview.indication.IndicationComparator;

/**
 * The InterviewAgenda represents the Interview Objects, which should appear
 * next in the interview.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 */
public final class InterviewAgenda implements de.d3web.core.session.interviewmanager.InterviewAgenda {

	private final List<AgendaEntry> agenda;

	public static final class AgendaEntry implements Comparable<AgendaEntry> {

		private static final Comparator<Indication> comparator = new IndicationComparator();
		private final InterviewObject interviewObject;
		private InterviewState interviewState;
		private final Indication indication;

		private AgendaEntry(InterviewObject interviewObject, InterviewState state, Indication indication) {
			this.interviewObject = interviewObject;
			this.interviewState = state;
			this.indication = indication;
		}

		/**
		 * Getter for InterviewObject.
		 */
		public InterviewObject getInterviewObject() {
			return interviewObject;
		}

		public void setInterviewState(InterviewState state) {
			this.interviewState = state;
		}

		public InterviewState getInterviewState() {
			return interviewState;
		}

		private boolean equalsInterviewObject(InterviewObject interviewObject) {
			return this.interviewObject.equals(interviewObject);
		}

		public boolean hasState(InterviewState state) {
			return this.interviewState == state;
		}

		@Override
		public String toString() {
			return "[" + this.interviewState + "] " + this.interviewObject;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result;
			result = prime
					* result
					+ ((interviewObject == null) ? 0 : interviewObject
							.hashCode());
			result = prime * result + ((interviewState == null) ? 0 : interviewState.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			AgendaEntry other = (AgendaEntry) obj;
			if (interviewObject == null) {
				if (other.interviewObject != null) {
					return false;
				}
			}
			else if (!interviewObject.equals(other.interviewObject)) {
				return false;
			}
			if (interviewState == null) {
				if (other.interviewState != null) {
					return false;
				}
			}
			if (!indication.equals(other.indication)) {
				return false;
			}
			else if (interviewState != other.interviewState) {
				return false;
			}
			return true;
		}

		@Override
		public int compareTo(@NotNull AgendaEntry other) {
			return comparator.compare(indication, other.indication);
		}

		public Indication getIndication() {
			return indication;
		}
	}

	/**
	 * A new {@link InterviewAgenda} instance is created.
	 */
	public InterviewAgenda() {
		agenda = new ArrayList<>();
	}

	/**
	 * Appends an {@link InterviewObject} to the agenda
	 * 
	 * @param interviewObject {@link InterviewObject}
	 */
	public final void append(InterviewObject interviewObject, Indication indication) {
		if (findAgendaEntry(interviewObject, indication) != null) {
			activate(interviewObject, indication);
		}
		else {
			// if the interviewObject is a QASet:
			if (interviewObject instanceof QASet) {
				// check, if this QASet is either a question
				// or a non-empty QContainer
				QASet interviewQuestion = (QASet) interviewObject;
				if (!interviewQuestion.isQuestionOrHasQuestions()) {
					// if the QContainer is empty: Don't add it to the agenda!
					return;
				}
			}
			agenda.add(new AgendaEntry(interviewObject, InterviewState.ACTIVE, indication));
		}
	}

	/**
	 * Deactivates all entries of the interviewObject
	 * 
	 * @created 14.05.2013
	 * @param interviewObject the object to deactivate
	 */
	public void deactivate(InterviewObject interviewObject) {
		for (AgendaEntry entry : this.agenda) {
			if (entry.equalsInterviewObject(interviewObject)) {
				entry.setInterviewState(InterviewState.INACTIVE);
			}
		}
	}

	/**
	 * Deactivates the first active entry of the {@link InterviewObject}
	 * 
	 * @created 15.05.2013
	 * @param interviewObject the object to deactivate
	 */
	public void deactivateFirst(InterviewObject interviewObject) {
		for (AgendaEntry entry : this.agenda) {
			if (entry.equalsInterviewObject(interviewObject)
					&& entry.hasState(InterviewState.ACTIVE)) {
				// multiple indicated states have to be removed, otherwise old
				// entries can "pop" in the again while answering the next
				// occurrence
				if (entry.getIndication().hasState(State.MULTIPLE_INDICATED)) {
					// modifying the agenda is ok, because we return the method
					// -> no exception
					this.agenda.remove(entry);
				}
				else {
					entry.setInterviewState(InterviewState.INACTIVE);
				}
				return;
			}
		}
	}

	public void delete(InterviewObject interviewObject, Indication indication) {
		List<AgendaEntry> toDelete = new ArrayList<>();
		for (AgendaEntry entry : this.agenda) {
			if (entry.equalsInterviewObject(interviewObject)
					&& entry.getIndication().equals(indication)) {
				toDelete.add(entry);
			}
		}
		agenda.removeAll(toDelete);
	}

	/**
	 * Activates the first occurency of an {@link InterviewObject} on the agenda
	 * 
	 * @param interviewObject {@link InterviewObject}
	 */
	public void activate(InterviewObject interviewObject) {
		activate(interviewObject, null);
	}

	/**
	 * Activates the entry with the specified object an indication
	 * 
	 * @created 14.05.2013
	 * @param interviewObject {@link InterviewObject}
	 * @param indication {@link Indication}
	 */
	public void activate(InterviewObject interviewObject, Indication indication) {
		AgendaEntry entry = findAgendaEntry(interviewObject, indication);
		if (entry == null) {
			append(interviewObject, indication);
		}
		else {
			entry.setInterviewState(InterviewState.ACTIVE);
		}
	}

	private AgendaEntry findAgendaEntry(InterviewObject interviewObject, Indication indication) {
		for (AgendaEntry entry : this.agenda) {
			if (entry.equalsInterviewObject(interviewObject)
					&& (indication == null || indication.equals(entry.getIndication()))) {
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
		for (AgendaEntry entry : this.agenda) {
			if (entry.equalsInterviewObject(interviewObject)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns if the specified interview object is on the agenda and has the
	 * specified state.
	 * <p>
	 * The method returns false (!) for any object not being on the agenda, even
	 * if checking for the state {@link InterviewState#INACTIVE}.
	 * 
	 * @created 10.03.2011
	 * @param interviewObject the object to be checked
	 * @param state the state to be expected
	 * @return if the object is on the agenda and has the expected state
	 */
	@Override
	public boolean hasState(InterviewObject interviewObject, InterviewState state) {
		for (AgendaEntry entry : this.agenda) {
			if (entry.equalsInterviewObject(interviewObject) && entry.hasState(state)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gives the (unmodifiable) list of currently active objects on the agenda.
	 * 
	 * @return an immutable list of the objects that are currently active on the
	 *         agenda
	 */
	public List<InterviewObject> getCurrentlyActiveObjects() {
		Collections.sort(this.agenda);
		// organize if required
		List<InterviewObject> objects = new ArrayList<>();
		for (AgendaEntry entry : this.agenda) {
			if (entry.hasState(InterviewState.ACTIVE)) {
				objects.add(entry.getInterviewObject());
			}
		}
		return Collections.unmodifiableList(objects);
	}

}
