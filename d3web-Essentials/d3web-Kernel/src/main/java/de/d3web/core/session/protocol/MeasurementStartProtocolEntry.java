/*
 * Copyright (C) 2018 denkbares GmbH, Germany
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

import de.d3web.core.session.protocol.ProtocolEntry;

/**
 * @author Jonas Müller
 * @created 03.09.18
 */
public class MeasurementStartProtocolEntry implements ProtocolEntry {

	private final Date date;
	private final String questionName;

	public MeasurementStartProtocolEntry(String questionName, Date date) {
		this.questionName = questionName;
		this.date = date;
	}

	public String getQuestionName() {
		return questionName;
	}

	@Override
	public Date getDate() {
		return date;
	}
}
