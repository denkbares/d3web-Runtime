package de.d3web.core.session.interviewmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QASet;

public class InterviewAgenda {
	public enum InterviewState {
		ACTIVE, INACTIVE;
	}
	private List<AgendaEntry> agenda;
	private KnowledgeBase knowledgeBase;
	// Strategy: how to sort the entries on the agenda?
	private DFSTreeAgendaSortingStrategy agendaSortingStrategy;

	private class AgendaEntry implements Comparable<AgendaEntry> {
		InterviewObject   interviewObject;
		InterviewState             state;
		private AgendaEntry(InterviewObject interviewObject, InterviewState state) {
			this.interviewObject = interviewObject;
			this.state             = state;
		}
		private boolean equalsInterviewObject(InterviewObject interviewObject) {
			return this.interviewObject.equals(interviewObject);
		}
		public boolean hasState(InterviewState state) {
			return this.state.equals(state);
		}
		public String toString() {
			return this.interviewObject + " ["+this.state+"]";
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
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AgendaEntry other = (AgendaEntry) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (interviewObject == null) {
				if (other.interviewObject != null)
					return false;
			} else if (!interviewObject.equals(other.interviewObject))
				return false;
			if (state == null) {
				if (other.state != null)
					return false;
			} else if (!state.equals(other.state))
				return false;
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
	
	private class DFSTreeSortingComparator implements Comparator<AgendaEntry> {
		private Map<TerminologyObject, Integer> index;
		public DFSTreeSortingComparator(
				Map<TerminologyObject, Integer> qasetIndex) {
			this.index = qasetIndex;
		}
		@Override
		public int compare(AgendaEntry entry1, AgendaEntry entry2) {
			int order1 = this.index.get(entry1.interviewObject);
			int order2 = this.index.get(entry2.interviewObject);
			return order1 - order2;
		}
	}
	
	private class DFSTreeAgendaSortingStrategy {
		private KnowledgeBase knowledgeBase;
		private Map<TerminologyObject, Integer> qasetIndex;
		private int maxOrderingNumber;

		public DFSTreeAgendaSortingStrategy(KnowledgeBase knowledgeBase) {
			this.knowledgeBase = knowledgeBase;
			this.qasetIndex = new HashMap<TerminologyObject, Integer>();
			reindex();
		}
		
		/**
		 * Traverses the QASet hierarchy using a depth-first search and
		 * attaches an ordering number to each visited {@link QASet}.
		 * This ordering number is used for the sorting of the 
		 * agenda.
		 */
		private void reindex() {
			this.maxOrderingNumber = 0;
			reindex(knowledgeBase.getRootQASet());
		}
		private void reindex(TerminologyObject qaset) {
			qasetIndex.put(qaset, maxOrderingNumber);
			maxOrderingNumber++;
			for (TerminologyObject child : qaset.getChildren()) {
				reindex(child);
			}
		}
		public void sort(List<AgendaEntry> entries) {
			Collections.sort(entries, new DFSTreeSortingComparator(this.qasetIndex));
		}
	}
	
	public InterviewAgenda(KnowledgeBase knowledgeBase) {
		agenda = new ArrayList<AgendaEntry>();
		this.knowledgeBase = knowledgeBase;
		this.agendaSortingStrategy = new DFSTreeAgendaSortingStrategy(this.knowledgeBase);
		
		// Put the init questions to the agenda first:
		List<? extends QASet> initQuestions = this.knowledgeBase.getInitQuestions();
		for (QASet initQuestion : initQuestions) {
			append(initQuestion);
		}
	}
	
	public void append(InterviewObject interviewObject) {
		trace("Append: " + interviewObject);
		if (onAgenda(interviewObject)) {
			activate(interviewObject);
		}
		else { 
			agenda.add(new AgendaEntry(interviewObject, InterviewState.ACTIVE));
		}
		organizeAgenda();
	}

	public void deactivate(InterviewObject interviewObject) {
		trace("De-activate: " + interviewObject);
		AgendaEntry entry = findAgendaEntry(interviewObject);
		if (entry != null) {
			entry.state = InterviewState.INACTIVE;
		}
	}

	public void activate(InterviewObject interviewObject) {
		trace("Activate: " + interviewObject);
		AgendaEntry entry = findAgendaEntry(interviewObject);
		if (entry == null) {
			append(interviewObject);
		}
		else {
			entry.state = InterviewState.ACTIVE;
		}
	}
	
	/**
	 * Sorts the agenda with respect to the newly
	 * added items.
	 */
	private void organizeAgenda() {
		agendaSortingStrategy.sort(agenda);
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

	public String toString() {
		return this.agenda.toString();
	}

	/**
	 * Checks, whether an entry -- that is also indicated -- is available on the agenda.
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
	 * Checks, if the specified {@link InterviewObject} instance is placed 
	 * on the agenda (independent from its state).
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
	 * 
	 * @return an immutable list of the objects that are currently active on the agenda 
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
	
}
