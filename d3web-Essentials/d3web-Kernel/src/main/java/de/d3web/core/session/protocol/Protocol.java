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

import java.util.Collection;
import java.util.List;

import de.d3web.core.session.Session;

/**
 * The {@link Protocol} stores all findings entered during a {@link Session} in
 * a sequential order.
 * 
 * Comment: Later also timestamps and further information are stored in the
 * protocol, too.
 * 
 * @author Volker Belli
 * 
 */
public interface Protocol {

	/**
	 * Return the list of all protocol entries in a chronological order.
	 * 
	 * @return all protocol entries in a chronological order
	 */
	List<ProtocolEntry> getProtocolHistory();

	/**
	 * Return the list of all protocol entries of a certain class in a
	 * chronological order. The returned list is always a subset of the total
	 * protocol history. It contains all protocol entries that are of the
	 * specified filterClass or of any of its subclasses.
	 * 
	 * @return the matching protocol entries in a chronological order
	 */
	<T extends ProtocolEntry> List<T> getProtocolHistory(Class<T> filterClass);

	/**
	 * Append a new protocol entry to the {@link Protocol}. The protocol takes
	 * care that the entries are kept in a chronological order.
	 * 
	 * @param entry the entry to be added to the protocol
	 */
	void addEntry(ProtocolEntry entry);

	/**
	 * Append a new protocol entries to the {@link Protocol}. The protocol takes
	 * care that the entries are kept in a chronological order.
	 * 
	 * @param entries the entries to be added to the protocol
	 */
	void addEntries(Collection<? extends ProtocolEntry> entries);

	/**
	 * Append a new protocol entries to the {@link Protocol}. The protocol takes
	 * care that the entries are kept in a chronological order.
	 * 
	 * @param entries the entries to be added to the protocol
	 */
	void addEntries(ProtocolEntry... entries);

	/**
	 * Removes all protocol entries from that protocol. The protocol will be
	 * empty after that call.
	 * 
	 * @created 26.10.2010
	 */
	void clear();

	/**
	 * Removes the first occurrence of the specified ProtocolEntry from this
	 * Protocol.
	 * 
	 * @created 24.10.2012
	 * @param entry
	 * @return true if entry was contained in this protocol
	 */
	boolean removeEntry(ProtocolEntry entry);
}
