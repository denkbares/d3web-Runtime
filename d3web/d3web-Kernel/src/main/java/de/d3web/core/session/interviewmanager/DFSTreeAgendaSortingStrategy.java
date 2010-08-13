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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.session.Session;
import de.d3web.core.session.interviewmanager.InterviewAgenda.AgendaEntry;

/**
 * The {@link DFSTreeAgendaSortingStrategy} sorts the entries on the
 * {@link InterviewAgenda} according the the ordering of the objects in the
 * terminology. Question/questionnaires are organized in a hierarchy and this
 * sorting strategy organizes the entries according to their depth-first search
 * ordering in the tree.
 * 
 * @author joba
 * @created 13.08.2010
 */
public class DFSTreeAgendaSortingStrategy implements AgendaSortingStrategy {
	
	private final Map<TerminologyObject, Integer> qasetIndex;
	private int maxOrderingNumber;
	private final Session session;
	
	private class DFSTreeSortingComparator implements Comparator<AgendaEntry> {
		private final Map<TerminologyObject, Integer> index;
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
	
	
	public DFSTreeAgendaSortingStrategy(Session session) {
		this.session = session;
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
		reindex(session.getKnowledgeBase().getRootQASet());
	}
	
	private void reindex(TerminologyObject qaset) {
		qasetIndex.put(qaset, maxOrderingNumber);
		maxOrderingNumber++;
		for (TerminologyObject child : qaset.getChildren()) {
			if (!qasetIndex.containsKey(child)) {
				reindex(child);
			} else { 
				continue;// terminate recursion in case of cyclic hierarchies
			}
		}
	}
	
	@Override
	public List<AgendaEntry> sort(List<AgendaEntry> entries) {
		// 1) Split entries into a) instant indicated & b) standard indicated
		// 2) Sort both lists separately
		// 3) Join the sorted sets, so that the instant indications come first

		List<AgendaEntry> instantIndicatedEntries = getInstantIndicatedEntries(entries);
		List<AgendaEntry> remainingEntries = new ArrayList<AgendaEntry>(entries);
		remainingEntries.removeAll(instantIndicatedEntries);

		Collections.sort(instantIndicatedEntries, new DFSTreeSortingComparator(this.qasetIndex));
		Collections.sort(remainingEntries, new DFSTreeSortingComparator(this.qasetIndex));

		entries = instantIndicatedEntries;
		entries.addAll(remainingEntries);
		return entries;
	}

	/**
	 * Returns all entries from the given {@link AgendaEntry} list, that contain
	 * instant indicated objects.
	 * 
	 * @created 13.08.2010
	 * @param entries
	 * @return all entries that contain instant indicated interview objects
	 */
	private List<AgendaEntry> getInstantIndicatedEntries(List<AgendaEntry> entries) {
		List<AgendaEntry> instantEntries = new ArrayList<InterviewAgenda.AgendaEntry>();
		for (AgendaEntry agendaEntry : entries) {
			Indication indication = this.session.getBlackboard().getIndication(
					agendaEntry.interviewObject);
			if (indication.hasState(State.INSTANT_INDICATED)) {
				instantEntries.add(agendaEntry);
			}
		}
		return instantEntries;
	}
}
