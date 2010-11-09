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
package de.d3web.core.session;

import java.util.Date;

import de.d3web.core.knowledge.terminology.IDObject;
import de.d3web.core.session.protocol.Protocol;

/**
 * Via this interface, basic methods of Sessions can be accessed
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 27.09.2010
 */
public interface SessionHeader extends IDObject {

	// --- full implementation is reserved for later implementation ---
	// (inkrement 2)
	public Protocol getProtocol();

	/**
	 * This method should be called, when the session is edited manually
	 * 
	 * @created 24.09.2010
	 */
	public void touch();

	/**
	 * This method should be called, when the session was edited at the given
	 * date
	 * 
	 * @created 24.09.2010
	 * @param date the Date when the Session was edited
	 */
	public void touch(Date date);

	/**
	 * Returns the Date of the last change on this Session
	 * 
	 * @created 24.09.2010
	 * @return the date of the last edit
	 */
	public Date getLastChangeDate();

	/**
	 * Returns the creation date of the Session
	 * 
	 * @created 24.09.2010
	 * @return the creation date
	 */
	public Date getCreationDate();

}