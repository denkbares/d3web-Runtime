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
package de.d3web.core.records.filter;

import java.util.Date;

import de.d3web.core.records.SessionRecord;

/**
 * Enables filtering of CaseRecords by creationdate
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 07.03.2011
 */
public class CreationDateFilter implements Filter {

	private final Date startDate;
	private final Date endDate;
	private final boolean includingStartDate;
	private final boolean includingEndDate;

	/**
	 * Creates a CreationDateFilter
	 * 
	 * @param startDate Filters Sessionrecords with a creationdate after
	 *        startDate, ignored if null
	 * @param endDate Filters Sessionrecords with a creationdate before endDate,
	 *        ignored if null
	 * @param includingStartDate when set to true, the filter also fits when the
	 *        startDate equals the startDate
	 * @param includingEndDate when set to true, the filter also fits when the
	 *        endDate equals the endDate
	 * @throws IllegalArgumentException when both dates are null or startDate is
	 *         after endDate
	 */
	public CreationDateFilter(Date startDate, Date endDate, boolean includingStartDate, boolean includingEndDate) {
		if (startDate == null && endDate == null) {
			throw new IllegalArgumentException("One date argument must not be null");
		}
		else if (startDate != null && endDate != null && startDate.after(endDate)) {
			throw new IllegalArgumentException("startDate must be before endDate.");
		}
		this.startDate = startDate;
		this.endDate = endDate;
		this.includingStartDate = includingStartDate;
		this.includingEndDate = includingEndDate;
	}

	@Override
	public boolean match(SessionRecord record) {
		Date creationDate = record.getCreationDate();
		if (creationDate.equals(startDate) && includingStartDate) {
			return true;
		}
		else if (creationDate.equals(endDate) && includingEndDate) {
			return true;
		}
		else if (startDate != null && endDate == null) {
			return creationDate.after(startDate);
		}
		else if (startDate == null && endDate != null) {
			return creationDate.before(endDate);
		}
		else {
			return creationDate.after(startDate) && creationDate.before(endDate);
		}
	}
}
