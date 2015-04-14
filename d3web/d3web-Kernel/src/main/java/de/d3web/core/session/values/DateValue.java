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
import java.util.TimeZone;

import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.Value;
import de.d3web.core.session.ValueUtils;

/**
 * This class stores a date assigned as value to a {@link QuestionDate}.
 *
 * @author Joachim Baumeister (denkbares GmbH), Sebastian Furth
 * @created 07.04.2010
 */
public class DateValue implements QuestionValue {

	private final Date value;

	private static final SimpleDateFormat DEFAULT_FORMAT = ValueUtils.getDefaultDateFormat();

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
	 * The format returned here should be used when saving DateValues to be able to parse the date with the static
	 * method {@link DateValue#createDateValue(String)}. Be aware that {@link SimpleDateFormat}s are not thread safe,
	 * although you will get a new instance of the format every time you call this method.
	 */
	public static SimpleDateFormat getDefaultDateFormat() {
		return ValueUtils.getDefaultDateFormat();
	}

	/**
	 * Returns an array with all the date format patterns, that will be used to try to parse a given date string.
	 */
	public static String[] getAllowedFormatStrings() {
		return ValueUtils.getAllowedDateFormatPatterns();
	}


	/**
	 * Creates a {@link DateValue} from a given String. If no time zone is given in the String, the time zone of the
	 * local JVM will be used.
	 * To be parseable, the String has to come in one of the available {@link DateFormat} from
	 * {@link DateValue#getAllowedFormatStrings()}.
	 * <p/>
	 * <b>Attention:</b> If the corresponding question is available while calling the method, you should instead use
	 * {@link ValueUtils#createDateValue(QuestionDate, String)}, especially, if your String does not contain a TimeZone
	 * identifier. Having the Question available will use a specified time zone of the questions UNIT property if given
	 * and if the String does not provide one.
	 *
	 * @param dateString the value to parse
	 * @return the parsed DateValue
	 * @created 23.10.2013
	 *
	 * @deprecated use the methods in {@link ValueUtils} instead, e.g. {@link ValueUtils#createDateValue(QuestionDate, String)}
	 */
	@Deprecated
	public static DateValue createDateValue(String dateString) throws IllegalArgumentException {
		return ValueUtils.createDateValue((TimeZone) null, dateString);
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

	@Override
	public String toString() {
		return ValueUtils.getDateVerbalization(TimeZone.getDefault(), getDate(), ValueUtils.TimeZoneDisplayMode.ALWAYS, false);
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

	/**
	 * Returns the date as String in a format which can be parsed with {@link DateValue#createDateValue(String)} and is
	 * also properly readable for humans. The String will contain the time zone ID of the JVM.
	 *
	 * @deprecated use the methods in {@link ValueUtils} instead, e.g. {@link ValueUtils#getDateOrDurationVerbalization(QuestionDate, Date)}
	 */
	@Deprecated
	public String getDateString() {
		return ValueUtils.getDateVerbalization((TimeZone) null, getDate(), ValueUtils.TimeZoneDisplayMode.ALWAYS);
	}



}
