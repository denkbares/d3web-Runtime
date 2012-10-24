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

package de.d3web.core.session.protocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Default implementation of Protocol
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 */
public class DefaultProtocol implements Protocol {

	private final List<ProtocolEntry> entries = new ArrayList<ProtocolEntry>();

	public DefaultProtocol() {
	}

	@Override
	public List<ProtocolEntry> getProtocolHistory() {
		return Collections.unmodifiableList(this.entries);
	}

	@Override
	public void addEntry(ProtocolEntry entry) {
		int index = this.entries.size();
		// if the stored entry is later, we reduce the index to store at
		for (; index > 0; index--) {
			// so break if stored one is not after that the one to be insert
			if (!entries.get(index - 1).getDate().after(entry.getDate())) break;
		}
		this.entries.add(index, entry);
	}

	@Override
	public <T extends ProtocolEntry> List<T> getProtocolHistory(Class<T> filterClass) {
		List<T> result = new LinkedList<T>();
		for (ProtocolEntry entry : this.entries) {
			if (filterClass.isInstance(entry)) {
				result.add(filterClass.cast(entry));
			}
		}
		return Collections.unmodifiableList(result);
	}

	@Override
	public void addEntries(Collection<? extends ProtocolEntry> entries) {
		for (ProtocolEntry entry : entries) {
			addEntry(entry);
		}
	}

	@Override
	public void addEntries(ProtocolEntry... entries) {
		addEntries(Arrays.asList(entries));
	}

	@Override
	public void clear() {
		this.entries.clear();
	}

	@Override
	public boolean removeEntry(ProtocolEntry entry) {
		return entries.remove(entry);
	}

}
