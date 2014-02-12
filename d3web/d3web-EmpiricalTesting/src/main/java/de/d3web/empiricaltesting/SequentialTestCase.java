/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.empiricaltesting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class SequentialTestCase {

	private String name = "";
	private final LinkedHashMap<Date, RatedTestCase> ratedTestCases = new LinkedHashMap<Date, RatedTestCase>();

	private Date startDate = null;
	private Date lastAddedDate = null;

	/**
	 * Default Constructor
	 */
	public SequentialTestCase() {
		this(Collections.<RatedTestCase> emptyList());
	}

	public SequentialTestCase(Collection<RatedTestCase> rtcs) {
		for (RatedTestCase ratedTestCase : rtcs) {
			add(ratedTestCase);
		}
	}

	public void setStartDate(Date startDate) {
		if (this.startDate != null) throw new UnsupportedOperationException(
				"Start date can only be set once");
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Adds RatedTestCase to this SequentialTestCase.
	 * 
	 * @param ratedTestCase The RatedTestCase which will be added
	 * @return true if the RatedTestCase was added to this SequntialTestCase
	 */
	public boolean add(RatedTestCase ratedTestCase) {
		Date timeStamp = ratedTestCase.getTimeStamp();
		if (startDate == null) {
			startDate = timeStamp == null ? new Date(0) : timeStamp;
		}
		if (timeStamp == null) {
			timeStamp = lastAddedDate == null ? startDate : new Date(lastAddedDate.getTime() + 1);
		}
		if (lastAddedDate == null || lastAddedDate.before(timeStamp)) {
			lastAddedDate = timeStamp;
		}
		else {
			String msg = "RatedTestCases have to be added in a sequential order.\nLast added: "
					+ lastAddedDate + " (" + lastAddedDate.getTime() + "), current: " + timeStamp
					+ " (" + timeStamp.getTime() + ")";
			throw new IllegalArgumentException(msg);
		}
		ratedTestCases.put(timeStamp, ratedTestCase);
		return true; // we just return true to accommodate the history interface
	}

	public Collection<Date> chronology() {
		return ratedTestCases.keySet();
	}

	/**
	 * Inverses the rating comparator of all RatedSolutions in all
	 * RatedTestCases of this SequentialTestCase.
	 */
	public void inverseSortSolutions() {
		for (RatedTestCase ratedTestCase : ratedTestCases.values()) {
			ratedTestCase.inverseSortSolutions();
		}
	}

	/**
	 * Here, the name is copied and new instances of the contained test cases
	 * are created. The objects within the test cases are not created again but
	 * taken from the original one.
	 * 
	 * @return a flat copy of the instance
	 */
	public SequentialTestCase flatClone() {
		SequentialTestCase newSTC = new SequentialTestCase();
		newSTC.setName(this.getName());
		for (RatedTestCase rtc : ratedTestCases.values()) {
			newSTC.add(rtc.flatClone());
		}
		return newSTC;
	}

	/**
	 * Shows String Representation of this SequentialTestCase
	 * 
	 * name: ratedTestCase, RatedTestCase, ...
	 */
	@Override
	public String toString() {
		StringBuffer buffy = new StringBuffer(getName() + ": ");
		for (RatedTestCase rtc : ratedTestCases.values()) {
			buffy.append(rtc.toString() + ", ");
		}
		buffy.replace(buffy.length() - 2, buffy.length(), ""); // remove last
		// ", "
		return buffy.toString();
	}

	/**
	 * Returns the name of this SequentialTestCase.
	 * 
	 * @return name of this SequentialTestCase
	 */
	public synchronized String getName() {
		return name;
	}

	/**
	 * Sets the name of this SequentialTestCase.
	 * 
	 * @param name desired name of this SequentialTestCase
	 */
	public synchronized void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((ratedTestCases == null) ? 0 : ratedTestCases.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof SequentialTestCase)) return false;
		SequentialTestCase other = (SequentialTestCase) obj;
		if (name == null) {
			if (other.name != null) return false;
		}
		else if (!name.equals(other.name)) return false;
		if (ratedTestCases == null) {
			if (other.ratedTestCases != null) return false;
		}
		else if (!ratedTestCases.equals(other.ratedTestCases)) return false;
		return true;
	}

	/**
	 * Returns the SequentialTestCase's RatedTestCases
	 * 
	 * @return List of RatedTestCases
	 */
	public List<RatedTestCase> getCases() {
		return new ArrayList<RatedTestCase>(ratedTestCases.values());
	}

	/**
	 * Returns the {@link RatedTestCase} for a given timeStamp. If there is no
	 * case, null is returned.
	 * 
	 * @created 31.10.2013
	 * @param timeStamp the Date of the case to return
	 * @return the case of the given timeStamp
	 */
	public RatedTestCase getCase(Date timeStamp) {
		return ratedTestCases.get(timeStamp);
	}

	/**
	 * Tests if this SequentialTestCase contains the same RatedTestCase as
	 * another SequentialTestCase
	 * 
	 * @param obj Other SequentialTestCase
	 * @return true, if RatedTestCases are equal false, if RatedTestCases aren't
	 *         equal
	 */
	public boolean testTo(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof SequentialTestCase)) return false;
		SequentialTestCase other = (SequentialTestCase) obj;
		if (ratedTestCases == null) {
			if (other.ratedTestCases != null) return false;
		}
		else if (!ratedTestCases.values().containsAll(other.ratedTestCases.values())) return false;
		return true;
	}

}
