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

import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.session.protocol.Protocol;

/**
 * Via this interface, basic methods of Sessions can be accessed
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 27.09.2010
 */
public interface SessionHeader extends NamedObject {

	/**
	 * Returns the {@link Protocol} of this session.
	 *
	 * @return {@link Protocol}
	 */
	Protocol getProtocol();

	/**
	 * This method should be called, when the session is edited manually.
	 *
	 * @created 24.09.2010
	 */
	void touch();

	/**
	 * This method should be called, when the session was edited at the given date.
	 *
	 * @param date the Date when the Session was edited
	 * @created 24.09.2010
	 */
	void touch(Date date);

	/**
	 * Returns the Date of the last change on this Session.
	 *
	 * @return the date of the last edit
	 * @created 24.09.2010
	 */
	Date getLastChangeDate();

	/**
	 * Returns the creation date of the Session.
	 *
	 * @return the creation date
	 * @created 24.09.2010
	 */
	Date getCreationDate();

	/**
	 * Returns the id of the session
	 *
	 * @return the id of the session
	 */
	@Override
	String getId();

	/**
	 * Sets the name of the SessionHeader
	 *
	 * @param name the new name
	 */
	void setName(String name);
}
