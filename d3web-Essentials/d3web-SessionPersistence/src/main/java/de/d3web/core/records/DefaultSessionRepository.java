/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.d3web.core.records;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.records.filter.AndFilter;
import de.d3web.core.records.filter.CreationDateFilter;
import de.d3web.core.records.filter.Filter;
import de.d3web.core.records.filter.OrFilter;
import com.denkbares.utils.Log;

/**
 * Default Implementation of the SessionRepository Interface (@link
 * SessionRepository). This implementation cannot persist the SessionRecords
 * 
 * @author Sebastian Furth (denkbares GmbH)‚
 * 
 */
public class DefaultSessionRepository implements SessionRepository {

	protected final Map<String, SessionRecord> sessionRecords = new HashMap<>();

	@Override
	public boolean add(SessionRecord sessionRecord) {
		if (sessionRecord == null) throw new NullPointerException(
				"null can't be added to the SessionRepository.");
		SessionRecord oldRecord = sessionRecords.get(sessionRecord.getId());
		if (oldRecord == null) {
			sessionRecords.put(sessionRecord.getId(), sessionRecord);
			return true;
		}
		else {
			// joba: I would recommend to keep the "==" comparison here
			if (oldRecord == sessionRecord) {
				Log.warning("SessionRecord " + sessionRecord.getId()
						+ " is already in the SessionRepository.");
				return false;

			}
			else {
				// replace record with new one
				sessionRecords.put(sessionRecord.getId(), sessionRecord);
				return true;
			}
		}
	}

	@Override
	public Iterator<SessionRecord> iterator() {
		return sessionRecords.values().iterator();
	}

	@Override
	public int size() {
		return sessionRecords.size();
	}

	@Override
	public boolean remove(SessionRecord sessionRecord) {
		if (sessionRecord == null) throw new NullPointerException(
				"null can't be removed from the SessionRepository.");
		SessionRecord storedRecord = sessionRecords.get(sessionRecord.getId());
		if (storedRecord == null || !storedRecord.equals(sessionRecord)) {
			return false;
		}
		return (sessionRecords.remove(sessionRecord.getId()) != null);
	}

	@Override
	public SessionRecord getSessionRecordById(String id) {
		if (id == null) {
			throw new NullPointerException("id is null.");
		}
		if (id.matches("\\s+")) throw new IllegalArgumentException(id
				+ " is not a valid ID.");
		return sessionRecords.get(id);
	}

	@Override
	public Collection<SessionRecord> getSessionRecords(Filter filter) {
		if (filter == null) return sessionRecords.values();
		// to enable recursive calls with a subset of the sessionRecords, the
		// method is extracted
		return getSessionRecords(sessionRecords.values(), filter);
	}

	private Collection<SessionRecord> getSessionRecords(Collection<SessionRecord> sessionRecords, Filter filter) {
		List<Filter> simpleFilters = new LinkedList<>();
		List<OrFilter> orFilters = new LinkedList<>();
		List<Filter> complexFilters = new LinkedList<>();
		sortFilters(filter, simpleFilters, orFilters, complexFilters);
		// shrinks the possible matches by applying simply filters first
		Collection<SessionRecord> matchingRecords = filterRecords(sessionRecords,
				simpleFilters.toArray(new Filter[simpleFilters.size()]));
		// handles or, this may also shrink the matching records by using
		// primarily the cheap filters:
		// for example: (A and B) or (C and D)
		for (OrFilter or : orFilters) {
			Collection<SessionRecord> temp = new HashSet<>();
			temp.addAll(getSessionRecords(matchingRecords, or.getF1()));
			temp.addAll(getSessionRecords(matchingRecords, or.getF2()));
			matchingRecords = new LinkedList<>(temp);
		}
		// at last all non-optimizable Filters are used
		matchingRecords = filterRecords(matchingRecords,
				complexFilters.toArray(new Filter[complexFilters.size()]));
		return matchingRecords;
	}

	/**
	 * @created 08.03.2011
	 * @return a Collection of all SessionRecords, being matched by all filters
	 */
	private static Collection<SessionRecord> filterRecords(Collection<SessionRecord> sessionRecords, Filter... filters) {
		Collection<SessionRecord> matchingRecords = new LinkedList<>();
		next: for (SessionRecord sr : sessionRecords) {
			for (Filter filter : filters) {
				if (!filter.accept(sr)) {
					continue next;
				}
			}
			// each filter matched
			matchingRecords.add(sr);
		}
		return matchingRecords;
	}

	private void sortFilters(Filter filter, List<Filter> simpleFilters, List<OrFilter> orFilters, List<Filter> complexFilters) {
		if (filter instanceof AndFilter) {
			sortFilters(((AndFilter) filter).getF1(), simpleFilters, orFilters, complexFilters);
			sortFilters(((AndFilter) filter).getF2(), simpleFilters, orFilters, complexFilters);
		}
		else if (isSimpleFilter(filter)) {
			simpleFilters.add(filter);
		}
		else if (filter instanceof OrFilter) {
			orFilters.add((OrFilter) filter);
		}
		else {
			complexFilters.add(filter);
		}
	}

	/**
	 * Returns true, if evaluating the filter is cheaper than evaluating common
	 * filters. Usually this method can be overwritten to mark which filters can
	 * be executed without loading the whole record
	 * 
	 * @created 08.03.2011
	 * @param f {@link Filter}
	 * @return true if the {@link Filter} is simple
	 */
	protected boolean isSimpleFilter(Filter f) {
		// an or filter is simple, when both subfilters are simple
		if (f instanceof OrFilter) {
			OrFilter or = (OrFilter) f;
			return isSimpleFilter(or.getF1()) && isSimpleFilter(or.getF2());
		}
		return (f instanceof CreationDateFilter);
	}

	/**
	 * Clears all of the specified entries form the session repository.
	 * 
	 * @created 24.05.2012
	 */
	public void clear() {
		sessionRecords.clear();
	}
}