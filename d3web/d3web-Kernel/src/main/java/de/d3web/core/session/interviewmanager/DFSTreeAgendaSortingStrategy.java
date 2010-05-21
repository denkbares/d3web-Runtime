package de.d3web.core.session.interviewmanager;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.session.interviewmanager.InterviewAgenda.AgendaEntry;

public class DFSTreeAgendaSortingStrategy implements AgendaSortingStrategy {
	
	private KnowledgeBase knowledgeBase;
	private Map<TerminologyObject, Integer> qasetIndex;
	private int maxOrderingNumber;
	
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
	
	
	public DFSTreeAgendaSortingStrategy(KnowledgeBase knowledgeBase)   {
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
	@Override
	public void sort(List<AgendaEntry> entries) {
		Collections.sort(entries, new DFSTreeSortingComparator(this.qasetIndex));
	}
}
