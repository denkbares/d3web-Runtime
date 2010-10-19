/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

import java.util.Date;

/**
 * Base interface for any protocol entry. A protocol entry is a kind of typed
 * log message for a specified session. The protocol entries are persistent to
 * the session and its session record when the session is stored onto a disc or
 * database.
 * <p>
 * <b>Protocol entries have to be knowledge base independent:</b><br>
 * Protocol entries are stored in both session and session records with no
 * conversion. But session records are missing a defined knowledge base instance
 * they are belonging to. For this reason, protocol entries must not rely on any
 * instances of knowledge base contained objects. They have to be able to be
 * used even if no knowledge base is available, as well as for multiple
 * knowledge base instances.
 * <p>
 * <b>Protocol entries have to be immutable:</b><br>
 * Protocol entries may be (re-)used in several instances of session
 * repositories and/or sessions. For this reason, they are not allowed to be
 * mutable. This required that the originally contained information must not
 * change at any time during the protocol entry instance lifetime (This does not
 * necessarily means that the are not allowed to keep any dynamic caches.)
 * <p>
 * The d3web kernel implementation provides two basic protocol entry types.
 * These are {@link FactProtocolEntry} and {@link TextProtocolEntry}. You may
 * define and use any additional protocol entry class, but keep in mind to also
 * provide session persistence fragment handlers for these classes to enable to
 * store session repositories onto disc.
 * 
 * @author volker_belli
 * @created 19.10.2010
 */
public interface ProtocolEntry {

	/**
	 * Returns the date of the protocol entry. This is usually the date when the
	 * entry has been logged.
	 * 
	 * @created 19.10.2010
	 * @return the log date
	 */
	public Date getDate();
}
