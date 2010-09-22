/*
 * Copyright (C) 2010 denkbares GmbH
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.core.knowledge.KnowledgeBase;

/**
 * Default Implementation of the SessionRepository Interface (@link
 * SessionRepository). This implementation cannot persist the SessionRecords
 * 
 * @author Sebastian Furth (denkbares GmbH)‚
 * 
 */
public class DefaultSessionRepository implements SessionRepository {

	protected List<SessionRecord> sessionRecords = new ArrayList<SessionRecord>();

	@Override
	public boolean add(SessionRecord sessionRecord) {
		if (sessionRecord == null) throw new NullPointerException(
				"null can't be added to the SessionRepository.");
		if (sessionRecords.contains(sessionRecord)) {
			Logger.getLogger(this.getClass().getSimpleName())
					.warning(
							"SessionRecord " + sessionRecord.getId()
									+ " is already in the SessionRepository.");
			return false;
		}
		return sessionRecords.add(sessionRecord);
	}

	@Override
	public Iterator<SessionRecord> iterator() {
		return sessionRecords.iterator();
	}

	@Override
	public boolean remove(SessionRecord sessionRecord) {
		if (sessionRecord == null) throw new NullPointerException(
				"null can't be removed from the SessionRepository.");
		if (!sessionRecords.contains(sessionRecord)) throw new IllegalArgumentException(
				"SessionRecord "
						+ sessionRecord.getId() + " is not in the SessionRepository");
		return sessionRecords.remove(sessionRecord);
	}

	@Override
	public SessionRecord getSessionRecordById(String id) {
		if (id == null) {
			throw new NullPointerException("id is null.");
		}
		if (id.matches("\\s+")) throw new IllegalArgumentException(id
				+ " is not a valid ID.");
		for (SessionRecord co : sessionRecords) {
			if (co.getId().equals(id)) return co;
		}
		return null;
	}

	@Override
	public KnowledgeBase getKnowledgeBase() {
		if (sessionRecords.size() > 0) {
			return sessionRecords.get(0).getKb();
		}
		return null;
	}

}
