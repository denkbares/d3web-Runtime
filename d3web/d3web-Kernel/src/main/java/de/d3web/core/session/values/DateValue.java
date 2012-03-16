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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

	// this format should be used when saving DateValues to be able to parse the
	// date with the static method createDateValue(String)
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SS");

	/**
	 * The accepted formats for dates. The first format is the one used for
	 * saving DateValues.
	 */
	private static final List<SimpleDateFormat> dateFormats = new ArrayList<SimpleDateFormat>();
	static {
		dateFormats.add(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss"));
		dateFormats.add(new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SS"));
		dateFormats.add(new SimpleDateFormat("yyyy-MM-dd HH-mm-ss"));
		dateFormats.add(DATE_FORMAT);
		dateFormats.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		dateFormats.add(new SimpleDateFormat("yyyy-MM-dd"));
		dateFormats.add(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SS"));
		dateFormats.add(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"));
		dateFormats.add(new SimpleDateFormat("dd.MM.yyyy"));
		// can parse Date.toString()
		dateFormats.add(new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy", Locale.ROOT));
	}
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

	public static DateValue createDateValue(String valueString) throws IllegalArgumentException {
		for (SimpleDateFormat dateFormat : dateFormats) {
			try {
				Date date = dateFormat.parse(valueString);
				return new DateValue(date);
			}
			catch (ParseException e) {
			}
		}
		throw new IllegalArgumentException("'" + valueString + "' can not be recognized as a date");
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
		return dateFormats.get(0).format(value);
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
