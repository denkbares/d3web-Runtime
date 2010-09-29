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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

	protected Map<String, SessionRecord> sessionRecords = new HashMap<String, SessionRecord>();

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
			if (oldRecord == sessionRecord) {
				Logger.getLogger(this.getClass().getSimpleName()).warning(
						"SessionRecord " + sessionRecord.getId()
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
	public boolean remove(SessionRecord sessionRecord) {
		if (sessionRecord == null) throw new NullPointerException(
				"null can't be removed from the SessionRepository.");
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
	public KnowledgeBase getKnowledgeBase() {
		if (sessionRecords.values().size() > 0) {
			return iterator().next().getKnowledgeBase();
		}
		return null;
	}

}
