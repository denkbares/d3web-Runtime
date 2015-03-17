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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.Value;
import de.d3web.strings.Strings;

/**
 * This class stores a date assigned as value to a {@link QuestionDate}.
 *
 * @author joba (denkbares GmbH), Sebastian Furth
 * @created 07.04.2010
 */
public class DateValue implements QuestionValue {

	/**
	 * This format should be used when saving DateValues to be able to parse the
	 * date with the static method {@link DateValue#createDateValue(String)}
	 */
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");

	/**
	 * The accepted formats for dates. The first format is the one used for
	 * saving DateValues.
	 */
	private static final List<SimpleDateFormat> dateFormats = new ArrayList<SimpleDateFormat>();

	static {
		dateFormats.add(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS"));
		dateFormats.add(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss"));
		dateFormats.add(new SimpleDateFormat("yyyy-MM-dd-HH-mm"));
		dateFormats.add(new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS"));
		dateFormats.add(new SimpleDateFormat("yyyy-MM-dd HH-mm-ss"));
		dateFormats.add(new SimpleDateFormat("yyyy-MM-dd HH-mm"));
		dateFormats.add(DATE_FORMAT);
		dateFormats.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		dateFormats.add(new SimpleDateFormat("yyyy-MM-dd HH:mm"));
		dateFormats.add(new SimpleDateFormat("yyyy-MM-dd"));
		dateFormats.add(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS"));
		dateFormats.add(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"));
		dateFormats.add(new SimpleDateFormat("dd.MM.yyyy HH:mm"));
		dateFormats.add(new SimpleDateFormat("dd.MM.yyyy"));
		dateFormats.add(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS"));
		dateFormats.add(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"));
		dateFormats.add(new SimpleDateFormat("dd/MM/yyyy HH:mm"));
		dateFormats.add(new SimpleDateFormat("dd/MM/yyyy"));
		// can parse Date.toString()
		dateFormats.add(new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy", Locale.ROOT));
	}

	public static final long YEAR = TimeUnit.DAYS.toMillis(365);

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
	 * The format returned here should be used when saving DateValues to be able
	 * to parse the date with the static method
	 * {@link DateValue#createDateValue(String)}. Be aware that {
	 *
	 * @link SimpleDateFormat}s are not thread safe.
	 */
	public static SimpleDateFormat getDefaultDateFormat() {
		return new SimpleDateFormat(DATE_FORMAT.toPattern());
	}

	/**
	 * Creates a {@link DateValue} from a given String. To be parseable, the
	 * String has to come in one of the available {@link DateFormat} from
	 * {@link DateValue#getAllowedFormatStrings()}
	 *
	 * @param valueString the value to parse
	 * @return the parsed DateValue
	 * @throws IllegalArgumentException if the given string cannot be parsed
	 * @created 23.10.2013
	 */
	public static DateValue createDateValue(String valueString) throws IllegalArgumentException {
		for (SimpleDateFormat dateFormat : dateFormats) {
			try {
				synchronized (dateFormat) {
					Date date = dateFormat.parse(valueString);
					return new DateValue(date);
				}
			}
			catch (ParseException ignore) {
			}
		}
		throw new IllegalArgumentException("'" + valueString + "' can not be recognized as a date");
	}

	public static String[] getAllowedFormatStrings() {
		String[] result = new String[dateFormats.size()];
		int index = 0;
		for (SimpleDateFormat format : dateFormats) {
			synchronized (format) {
				result[index++] = format.toPattern();
			}
		}
		return result;
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
	 * Returns the date as String in a format which can be parsed with
	 * {@link DateValue#createDateValue(String)} and is also properly readable
	 * for humans.
	 */
	public String getDateString() {
		return getDateString(value);
	}

	/**
	 * Returns the date as String in a format which can be parsed with
	 * {@link DateValue#createDateValue(String)} and is also properly readable
	 * for humans.
	 */
	public static String getDateString(Date date) {
		String dateString;
		synchronized (DATE_FORMAT) {
			dateString = DATE_FORMAT.format(date);
		}
		dateString = trimTime(dateString);

		return dateString;
	}

	/**
	 * If the date is within plus/minus one year of unix time 0 (1970-01-01), this method will return the duration
	 * since unix time 0, e.g. 1d 2h 40min. Else, it will return the date string (@see DateValue#getDateString()),
	 * which can be parsed with {@link DateValue#createDateValue(String)} and is also properly readable for humans.
	 */
	public String getDateOrDurationString() {
		return getDateOrDurationString(value);
	}

	/**
	 * If the date is within plus/minus one year of unix time 0 (1970-01-01), this method will return the duration
	 * since unix time 0, e.g. 1d 2h 40min. Else, it will return the date string (@see DateValue#getDateString()),
	 * which can be parsed with {@link DateValue#createDateValue(String)} and is also properly readable for humans.
	 */
	public static String getDateOrDurationString(Date date) {
		long time = date.getTime();
		if (time < YEAR && time > -YEAR) {
			return Strings.getDurationVerbalization(time);
		}
		else {
			return getDateString(date);
		}
	}

	/**
	 * If the given date string ends with trailing zeros from the time, those zeros will
	 * be trimmed correctly. Trims milli seconds, seconds, and hours with minutes.<p/>
	 * Example:
	 * <ul>
	 * <li>2000-01-01 15:44:32.000 -> 2000-01-01 15:44:32</li>
	 * <li>2000-01-01 15:44:00.000 -> 2000-01-01 15:44</li>
	 * <li>2000-01-01 00:00:00.000 -> 2000-01-01</li>
	 * </ul>
	 *
	 * @param dateString the date verbalization to trim
	 * @return a trimmed version of the date string
	 */
	public static String trimTime(String dateString) {
		// we remove trailing zero milliseconds, seconds, minutes and hours,
		// because it does not add any information to the date string and can
		// still be parsed by the available formats
		dateString = dateString.replaceAll("[.:]000$", "")
				.replaceAll("(?<=\\d\\d:\\d\\d):00$", "").replaceAll(" 00:00$", "");
		return dateString;
	}

	@Override
	public String toString() {
		synchronized (DATE_FORMAT) {
			return DATE_FORMAT.format(value);
		}
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
