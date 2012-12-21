/*
 * Copyright (C) 2012 denkbares GmbH
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
 * This entry is added by interviews to the Protocol to indicate that the user
 * is presented a new QContainer
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 18.12.2012
 */
public class ActualQContainerEntry implements ProtocolEntry {

	private final Date date;
	private final String qContainer;

	public ActualQContainerEntry(String qContainer) {
		this(new Date(), qContainer);
	}

	public ActualQContainerEntry(Date date, String qContainer) {
		this.date = date;
		this.qContainer = qContainer;
	}

	@Override
	public Date getDate() {
		return date;
	}

	public String getQContainerName() {
		return qContainer;
	}

}
