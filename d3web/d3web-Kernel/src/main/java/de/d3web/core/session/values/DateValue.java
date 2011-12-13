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
package de.d3web.core.session.values;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.Value;

/**
 * This class stores a date assigned as value to a {@link QuestionDate}.
 * 
 * @author joba (denkbares GmbH), Sebastian Furth
 * @created 07.04.2010
 */
public class DateValue implements QuestionValue {

	/**
	 * The Format, in which the dates are saved and loaded. The Format is for
	 * example 2003-10-20-13-51-23
	 */
	public static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	private final Date value;

	/**
	 * Constructs a new DateValue
	 * 
	 * @param value the Date for which a new DateValue should be instantiated
	 * @throws NullPointerException if a null object was passed in
	 */
	public DateValue(Date value) {
		if (value == null) {
			throw new NullPointerException();
		}
		this.value = value;
	}

	/**
	 * @return the {@link Date} of this date value
	 */
	@Override
	public Object getValue() {
		return value;
	}

	public Date getDate() {
		return value;
	}

	/**
	 * Returns the date as String in the format in which it can be parsed at
	 * setDate(String)
	 */
	public String getDateString() {
		return format.format(value);
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DateValue other = (DateValue) obj;
		return value.getTime() == other.value.getTime();
	}

	@Override
	public int compareTo(Value o) {
		if (!(o instanceof DateValue)) {
			throw new IllegalArgumentException();
		}
		return value.compareTo(((DateValue) o).value);
	}

}
