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

package de.d3web.core.session;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.strings.Strings;
import de.d3web.utils.Log;

public final class ValueUtils {

	private ValueUtils() { // enforce noninstantiability
	}

	public static final long YEAR = TimeUnit.DAYS.toMillis(365);

	/**
	 * Used to determine whether the time zone should be appended to a generated date string.
	 */
	public enum TimeZoneDisplayMode {
		/**
		 * The time zone will not be appended. This can be used, if the time zone is handled or rendered independently
		 * from the String.
		 */
		NEVER,
		/**
		 * The time zone will always be appended. This should be used for example for persistence purposes, where the
		 * date is written and ready by the system.
		 */
		ALWAYS,
		/**
		 * The time zone will only be appended, if it is not the same as the default time zone of the current JVM. This
		 * is can be used, if the string is displayed to the user without much other processing.
		 */
		IF_NOT_DEFAULT
	}

	/**
	 * This format should be used when saving DateValues to be able to parse the
	 * date with the static method {@link DateValue#createDateValue(String)}
	 */
	private static final SimpleDateFormat DATE_FORMAT_WITHOUT_TIME_ZONE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private static final SimpleDateFormat DATE_FORMAT_WITH_TIME_ZONE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z", Locale.ENGLISH);

	private static final List<SimpleDateFormat> dateFormats = new ArrayList<SimpleDateFormat>();

	private static final List<SimpleDateFormat> dateFormatsWithoutTimeZone = new ArrayList<SimpleDateFormat>();

	private static final List<SimpleDateFormat> dateFormatsWithTimeZone = new ArrayList<SimpleDateFormat>();

	static {
		dateFormatsWithoutTimeZone.add(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS"));
		dateFormatsWithoutTimeZone.add(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss"));
		dateFormatsWithoutTimeZone.add(new SimpleDateFormat("yyyy-MM-dd-HH-mm"));
		dateFormatsWithoutTimeZone.add(new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS"));
		dateFormatsWithoutTimeZone.add(new SimpleDateFormat("yyyy-MM-dd HH-mm-ss"));
		dateFormatsWithoutTimeZone.add(new SimpleDateFormat("yyyy-MM-dd HH-mm"));
		dateFormatsWithoutTimeZone.add(DATE_FORMAT_WITHOUT_TIME_ZONE);
		dateFormatsWithoutTimeZone.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		dateFormatsWithoutTimeZone.add(new SimpleDateFormat("yyyy-MM-dd HH:mm"));
		dateFormatsWithoutTimeZone.add(new SimpleDateFormat("yyyy-MM-dd"));
		dateFormatsWithoutTimeZone.add(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS"));
		dateFormatsWithoutTimeZone.add(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"));
		dateFormatsWithoutTimeZone.add(new SimpleDateFormat("dd.MM.yyyy HH:mm"));
		dateFormatsWithoutTimeZone.add(new SimpleDateFormat("dd.MM.yyyy"));
		dateFormatsWithoutTimeZone.add(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS"));
		dateFormatsWithoutTimeZone.add(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"));
		dateFormatsWithoutTimeZone.add(new SimpleDateFormat("dd/MM/yyyy HH:mm"));
		dateFormatsWithoutTimeZone.add(new SimpleDateFormat("dd/MM/yyyy"));
	}

	static {
		dateFormatsWithTimeZone.add(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS z"));
		dateFormatsWithTimeZone.add(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss z"));
		dateFormatsWithTimeZone.add(new SimpleDateFormat("yyyy-MM-dd-HH-mm z"));
		dateFormatsWithTimeZone.add(new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS z"));
		dateFormatsWithTimeZone.add(new SimpleDateFormat("yyyy-MM-dd HH-mm-ss z"));
		dateFormatsWithTimeZone.add(new SimpleDateFormat("yyyy-MM-dd HH-mm z"));
		dateFormatsWithTimeZone.add(new SimpleDateFormat(DATE_FORMAT_WITHOUT_TIME_ZONE.toPattern() + " z"));
		dateFormatsWithTimeZone.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"));
		dateFormatsWithTimeZone.add(new SimpleDateFormat("yyyy-MM-dd HH:mm z"));
		dateFormatsWithTimeZone.add(new SimpleDateFormat("yyyy-MM-dd z"));
		dateFormatsWithTimeZone.add(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS z"));
		dateFormatsWithTimeZone.add(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z"));
		dateFormatsWithTimeZone.add(new SimpleDateFormat("dd.MM.yyyy HH:mm z"));
		dateFormatsWithTimeZone.add(new SimpleDateFormat("dd.MM.yyyy z"));
		dateFormatsWithTimeZone.add(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS z"));
		dateFormatsWithTimeZone.add(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z"));
		dateFormatsWithTimeZone.add(new SimpleDateFormat("dd/MM/yyyy HH:mm z"));
		dateFormatsWithTimeZone.add(new SimpleDateFormat("dd/MM/yyyy z"));

		// can parse Date.toString()
		dateFormatsWithTimeZone.add(new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy", Locale.ROOT));

		for (int i = 0; i < dateFormatsWithTimeZone.size() || i < dateFormatsWithoutTimeZone.size(); i++) {
			if (i < dateFormatsWithTimeZone.size()) dateFormats.add(dateFormatsWithTimeZone.get(i));
			if (i < dateFormatsWithoutTimeZone.size()) dateFormats.add(dateFormatsWithoutTimeZone.get(i));
		}
	}

	public static String[] getAllowedDateFormatPatterns() {
		String[] result = new String[dateFormats.size()];
		int index = 0;
		for (SimpleDateFormat format : dateFormats) {
			result[index++] = format.toPattern();
		}
		return result;
	}

	/**
	 * The format returned here should be used when saving DateValues to be able to parse the date with the static
	 * method {@link DateValue#createDateValue(String)}. Be aware that {@link SimpleDateFormat}s are not thread safe,
	 * although you will get a new instance of the format every time you call this method.
	 */
	public static SimpleDateFormat getDefaultDateFormat() {
		return new SimpleDateFormat(DATE_FORMAT_WITH_TIME_ZONE.toPattern());
	}

	/**
	 * Creates a {@link Value} for a {@link Question}. If the given String is no
	 * valid representation for a Value for the given Question, <tt>null</tt>
	 * will be returned.
	 *
	 * @param question    the question for which the {@link Value} is created
	 * @param valueString a String representation of the {@link Value} to be
	 *                    created
	 * @return a {@link Value} or <tt>null</tt> if the given String is no valid
	 * representation for a Value for the given Question
	 * @created 11.08.2012
	 */
	public static Value createValue(Question question, String valueString) {
		return createValue(question, valueString, null);
	}

	/**
	 * Creates a {@link Value} for a {@link Question}. If the given String is no
	 * valid representation for a Value for the given Question, <tt>null</tt>
	 * will be returned.<br/>
	 * In case of a {@link QuestionMC}, the new Value is merged with the
	 * existing Value (if possible). The existing value is allowed to be
	 * <tt>null</tt>!<br/>
	 * In case the new value equals the existing value, Unkowwn is returned.
	 *
	 * @param question      the question for which the {@link Value} is created
	 * @param valueString   a String representation of the {@link Value} to be
	 *                      created
	 * @param existingValue the existing value for the question to be merged in
	 *                      case of a QuestionMC
	 * @return a {@link Value} or <tt>null</tt> if the given String is no valid
	 * representation for a Value for the given Question
	 * @created 11.08.2012
	 */
	public static Value createValue(Question question, String valueString, Value existingValue) {

		Value value = Unknown.getInstance();

		if (valueString.equals(Unknown.getInstance().getValue())) {
			value = Unknown.getInstance();
		}

		else if (question instanceof QuestionChoice) {
			value = createQuestionChoiceValue((QuestionChoice) question, valueString,
					existingValue);
		}

		else if (question instanceof QuestionNum) {
			try {
				value = new NumValue(Double.parseDouble(valueString.replace(',', '.')));
			}
			catch (IllegalArgumentException e) {
				// null will be returned
			}
		}

		else if (question instanceof QuestionText) {
			value = new TextValue(valueString);
		}

		else if (question instanceof QuestionDate) {
			try {
				value = createDateValue((QuestionDate) question, valueString);
			}
			catch (IllegalArgumentException ignore) {
				try {
					value = new DateValue(new Date(Long.parseLong(valueString)));
				}
				catch (NumberFormatException e) {
					// null will be returned
				}
			}
		}

		if (value.equals(existingValue)) value = Unknown.getInstance();

		return value;
	}

	/**
	 * Creates a {@link Value} for a {@link QuestionChoice}. If the given String
	 * is no valid representation for a Value for the given Question,
	 * <tt>null</tt> will be returned.<br/>
	 * In case of a {@link QuestionMC}, the new Value is merged with the
	 * existing Value (if possible). The existing value is allowed to be
	 * <tt>null</tt>!
	 *
	 * @param question      the question for which the {@link Value} is created
	 * @param valueString   a String representation of the {@link Value} to be
	 *                      created
	 * @param existingValue the existing value for the question to be merged in
	 *                      case of a QuestionMC
	 * @return a {@link Value} or <tt>null</tt> if the given String is no valid
	 * representation for a Value for the given Question
	 * @created 11.08.2012
	 */
	public static Value createQuestionChoiceValue(QuestionChoice question, String valueString, Value existingValue) {
		Choice choice = KnowledgeBaseUtils.findChoice(question, valueString);
		Value value = null;
		if (question instanceof QuestionMC) {
			value = createQuestionMCValue((QuestionMC) question, choice, existingValue);
		}
		else if (choice != null) {
			value = new ChoiceValue(choice);
		}
		return value;
	}

	/**
	 * Creates a {@link Value} for a {@link QuestionChoice}. If the given String
	 * is no valid representation for a Value for the given Question,
	 * <tt>null</tt> will be returned.<br/>
	 *
	 * @param question    the question for which the {@link Value} is created
	 * @param valueString a String representation of the {@link Value} to be
	 *                    created
	 * @return a {@link Value} or <tt>null</tt> if the given String is no valid
	 * representation for a Value for the given Question
	 * @created 11.08.2012
	 */
	public static Value createQuestionChoiceValue(QuestionChoice question, String valueString) {
		Value value = null;
		Choice choice = KnowledgeBaseUtils.findChoice(question, valueString);
		if (question instanceof QuestionMC) {
			value = createQuestionMCValue((QuestionMC) question, choice, null);
		}
		else if (choice != null) {
			value = new ChoiceValue(choice);
		}
		return value;
	}

	private static Value createQuestionMCValue(QuestionMC question, Choice choice, Value existingValue) {
		Value value;
		List<Choice> choices = new ArrayList<Choice>();
		if (existingValue instanceof ChoiceValue) {
			Choice existingChoice = ((ChoiceValue) existingValue)
					.getChoice(question);
			choices.add(existingChoice);
		}
		else if (existingValue instanceof MultipleChoiceValue) {
			try {
				List<Choice> temp = ((MultipleChoiceValue) existingValue)
						.asChoiceList(question);
				choices.addAll(temp);
			}
			catch (IllegalArgumentException e) {
				Log.warning(e.getMessage());
			}
		}
		if (choice != null && !choices.remove(choice)) {
			choices.add(choice);
		}
		if (choices.isEmpty()) {
			value = Unknown.getInstance();
		}
		else {
			value = MultipleChoiceValue.fromChoices(choices);
		}
		return value;
	}

	public static String getID_or_Value(Value value) { // NOSONAR this method
		// name is ok
		if (value instanceof ChoiceValue) {
			return ((ChoiceValue) value).getChoiceID().getText();
		}
		else if (value instanceof Unknown) {
			return Unknown.UNKNOWN_ID;
		}
		else if (value instanceof UndefinedValue) {
			return UndefinedValue.UNDEFINED_ID;
		}
		else {
			return value.getValue().toString();
		}
	}

	/**
	 * If the date is within plus/minus one year of unix time 0 (1970-01-01), this method will return the duration
	 * since unix time 0, e.g. 1d 2h 40min. Else, it will return the date string (@see
	 * DateValue#getDateVerbalization()),
	 * which can be parsed with {@link DateValue#createDateValue(String)} and is also properly readable for humans.
	 */
	public static String getDateOrDurationVerbalization(Date date) {
		return getDateOrDurationVerbalization(null, date, false);
	}

	/**
	 * If the date is within plus/minus one year of unix time 0 (1970-01-01), this method will return the duration
	 * since unix time 0, e.g. 1d 2h 40min. Else, it will return the date string (@see
	 * DateValue#getDateVerbalization()),
	 * which can be parsed with {@link DateValue#createDateValue(String)} and is also properly readable for humans.
	 */
	public static String getDateOrDurationVerbalization(QuestionDate question, Date date) {
		return getDateOrDurationVerbalization(question, date, false);
	}

	/**
	 * If the date is within plus/minus one year of unix time 0 (1970-01-01), this method will return the duration
	 * since unix time 0, e.g. 1d 2h 40min. Else, it will return the date string (@see
	 * DateValue#getDateVerbalization()),
	 * which can be parsed with {@link DateValue#createDateValue(String)} and is also properly readable for humans.
	 *
	 * @param appendDateString if set to true, the actual date string will be appended in parenthesis
	 */
	public static String getDateOrDurationVerbalization(QuestionDate question, Date date, boolean appendDateString) {
		long time = date.getTime();
		if (time < YEAR && time > -YEAR) {
			String string = Strings.getDurationVerbalization(time);
			if (appendDateString) {
				string += " (" + getDateVerbalization(question, date, TimeZoneDisplayMode.IF_NOT_DEFAULT) + ")";
			}
			return string;
		}
		else {
			return getDateVerbalization(question, date, TimeZoneDisplayMode.IF_NOT_DEFAULT);
		}
	}

	/**
	 * Returns the date as String in a format which can be parsed with {@link DateValue#createDateValue(String)} and is
	 * also properly readable for humans. If the given question has no defined TimeZone (via Property UNIT), the date
	 * will use the local TimeZone of the JVM.
	 *
	 * @param question     the corresponding question for this value (will be used to retrieve TimeZone)
	 * @param value        the value for which we want the date string
	 * @param timeZoneMode decides whether the used TimeZone should be appended to the string
	 */
	public static String getDateVerbalization(QuestionDate question, DateValue value, TimeZoneDisplayMode timeZoneMode) {
		return getDateVerbalization(getTimeZone(question), value.getDate(), timeZoneMode, true);
	}

	/**
	 * Returns the date as String in a format which can be parsed with {@link DateValue#createDateValue(String)} and is
	 * also properly readable for humans. If the given question has no defined TimeZone (via Property UNIT), the date
	 * will use the local TimeZone of the JVM.
	 *
	 * @param question     the corresponding question for this value (will be used to retrieve TimeZone)
	 * @param value        the value for which we want the date string
	 * @param timeZoneMode decides whether the used TimeZone should be appended to the string
	 * @param trim         decides whether trailing zeros should be trimmed using ({@link #trimTime(String)})
	 */
	public static String getDateVerbalization(QuestionDate question, DateValue value, TimeZoneDisplayMode timeZoneMode, boolean trim) {
		return getDateVerbalization(getTimeZone(question), value.getDate(), timeZoneMode, trim);
	}

	/**
	 * Returns the date as String in a format which can be parsed with {@link DateValue#createDateValue(String)} and is
	 * also properly readable for humans. If the given question has no defined TimeZone (via Property UNIT), the date
	 * will use the local TimeZone of the JVM.
	 *
	 * @param question     the corresponding question for this value (will be used to retrieve TimeZone)
	 * @param date         the date for which we want the string
	 * @param timeZoneMode decides whether the used TimeZone should be appended to the string
	 */
	public static String getDateVerbalization(QuestionDate question, Date date, TimeZoneDisplayMode timeZoneMode) {
		return getDateVerbalization(getTimeZone(question), date, timeZoneMode, true);
	}

	/**
	 * Returns the date as String in a format which can be parsed with {@link DateValue#createDateValue(String)} and is
	 * also properly readable for humans. If the given question has no defined TimeZone (via Property UNIT), the date
	 * will use the local TimeZone of the JVM.
	 *
	 * @param timeZone     the TimeZone in which the date should be verbalized
	 * @param date         the date which should be verbalized
	 * @param timeZoneMode decides whether the used TimeZone ID should be appended to the string
	 */
	public static String getDateVerbalization(TimeZone timeZone, Date date, TimeZoneDisplayMode timeZoneMode) {
		return getDateVerbalization(timeZone, date, timeZoneMode, true);
	}

	/**
	 * Returns the date as String in a format which can be parsed with {@link DateValue#createDateValue(String)} and is
	 * also properly readable for humans. If the given question has no defined TimeZone (via Property UNIT), the date
	 * will use the local TimeZone of the JVM.
	 *
	 * @param timeZone     the TimeZone in which the date should be verbalized
	 * @param date         the date which should be verbalized
	 * @param timeZoneMode decides whether the used TimeZone ID should be appended to the string
	 * @param trim         decides whether trailing zeros should be trimmed using ({@link #trimTime(String)})
	 */
	public static String getDateVerbalization(TimeZone timeZone, Date date, TimeZoneDisplayMode timeZoneMode, boolean trim) {
		String dateString;
		SimpleDateFormat dateFormat;
		if (timeZoneMode == TimeZoneDisplayMode.ALWAYS) {
			dateFormat = DATE_FORMAT_WITH_TIME_ZONE;
		}
		else if (timeZoneMode == TimeZoneDisplayMode.NEVER) {
			dateFormat = DATE_FORMAT_WITHOUT_TIME_ZONE;
		}
		else {
			dateFormat = !TimeZone.getDefault().equals(timeZone) ?
					DATE_FORMAT_WITH_TIME_ZONE : DATE_FORMAT_WITHOUT_TIME_ZONE;
		}
		//noinspection SynchronizationOnLocalVariableOrMethodParameter
		synchronized (dateFormat) {
			TimeZone defaultTimeZone = null;
			if (timeZone != null) {
				defaultTimeZone = dateFormat.getTimeZone();
				dateFormat.setTimeZone(timeZone);
			}
			dateString = dateFormat.format(date);
			if (timeZone != null) {
				dateFormat.setTimeZone(defaultTimeZone);
			}
		}
		if (trim) dateString = trimTime(dateString);
		return dateString;
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
		dateString = dateString.replaceAll("[.:]000(?:$|(?=\\D))", "")
				.replaceAll("(?<=\\d\\d:\\d\\d):00(?:$|(?=\\D))", "").replaceAll(" 00:00(?:$|(?=\\D))", "");
		return dateString;
	}

	/**
	 * Creates a {@link DateValue} from a given String. If the String comes with a time zone, that time zone will be
	 * used to parse the date. If no time zone is given, we check the question, if a time zone is given there using
	 * the property UNIT. If yes, that time zone will be used to parse the date. If no, the time zone of the JVM will
	 * be used.
	 * To be parseable, the String has to come in one of the available {@link DateFormat} from
	 * {@link DateValue#getAllowedFormatStrings()}.
	 *
	 * @param question   the question for which the DateValue should be used
	 * @param dateString the date string to parse
	 * @return the parsed DateValue
	 * @created 13.04.2015
	 */
	public static DateValue createDateValue(QuestionDate question, String dateString) throws IllegalArgumentException {
		return createDateValue(getTimeZone(question), dateString);

	}

	/**
	 * Creates a {@link DateValue} from a given String. If the String comes with a time zone, that time zone will be
	 * used to parse the date. If no time zone is given in the String, we use the attribute <tt>timeZone</tt>. If that
	 * isn't given either, the default time zone of the JVM is used.
	 * To be parseable, the String has to come in one of the available {@link DateFormat} from
	 * {@link DateValue#getAllowedFormatStrings()}.
	 *
	 * @param timeZone   the time zone to be used if the string does not contain a time zone
	 * @param dateString the value to parse
	 * @return the parsed DateValue
	 * @created 13.04.2015
	 */
	public static DateValue createDateValue(TimeZone timeZone, String dateString) {
		// First, try the formats with TimeZone. If the String provides a TimeZone, we want to use it of course.
		for (SimpleDateFormat dateFormat : dateFormatsWithTimeZone) {
			try {
				//noinspection SynchronizationOnLocalVariableOrMethodParameter
				synchronized (dateFormat) {
					Date date = dateFormat.parse(dateString);
					return new DateValue(date);
				}
			}
			catch (ParseException ignore) {
			}
		}
		// The String does not seem to contain a TimeZone. If we have a time zone we use it. If not, we will use the
		// time zone of JVM.
		for (SimpleDateFormat dateFormat : dateFormatsWithoutTimeZone) {
			try {
				//noinspection SynchronizationOnLocalVariableOrMethodParameter
				synchronized (dateFormat) {
					TimeZone currentTimeZone = null;
					if (timeZone != null) {
						currentTimeZone = dateFormat.getTimeZone();
						dateFormat.setTimeZone(timeZone);
					}
					Date date = dateFormat.parse(dateString);
					if (timeZone != null) {
						dateFormat.setTimeZone(currentTimeZone);
					}
					return new DateValue(date);
				}
			}
			catch (ParseException ignore) {
			}
		}
		throw new IllegalArgumentException("'" + dateString + "' can not be recognized as a date");
	}

	public static TimeZone getTimeZone(QuestionDate question) {
		TimeZone timeZone = null;
		if (question != null) {
			String timeZoneId = question.getInfoStore().getValue(MMInfo.UNIT);
			if (timeZoneId != null) timeZone = TimeZone.getTimeZone(timeZoneId);
		}
		return timeZone;
	}

}
