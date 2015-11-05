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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.scoring.HeuristicRating;
import de.d3web.scoring.Score;
import de.d3web.strings.Strings;

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
	 * Date format to validate a time zone parsable by the other formats used in this ValueUtils.
	 */
	private static final SimpleDateFormat TIME_ZONE_DATE_FORMAT = new SimpleDateFormat("z", Locale.ENGLISH);

	private static final SimpleDateFormat DATE_FORMAT_WITHOUT_TIME_ZONE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static final SimpleDateFormat DATE_FORMAT_WITH_TIME_ZONE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z", Locale.ENGLISH);

	private static final Map<TimeZone, SimpleDateFormat> DATE_FORMAT_WITHOUT_TIME_ZONE_MAP = new HashMap<>();

	private static final Map<TimeZone, List<SimpleDateFormat>> DATE_FORMATS_WITHOUT_TIME_ZONE_MAP = new HashMap<>();
	private static final List<SimpleDateFormat> DATE_FORMATS_WITH_TIME_ZONE = new ArrayList<>();

	private static final Pattern DATE_STRING_TIME_ZONE_PATTERN
			= Pattern.compile("\\s((?:[a-zA-Z]\\s*)+|GMT[+-]\\d?\\d:\\d\\d)\\s*$", Pattern.CASE_INSENSITIVE);

	static {
		List<SimpleDateFormat> dateFormatsWithoutTimeZone = new ArrayList<>();

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

		DATE_FORMATS_WITH_TIME_ZONE.add(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS z", Locale.ENGLISH));
		DATE_FORMATS_WITH_TIME_ZONE.add(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss z", Locale.ENGLISH));
		DATE_FORMATS_WITH_TIME_ZONE.add(new SimpleDateFormat("yyyy-MM-dd-HH-mm z", Locale.ENGLISH));
		DATE_FORMATS_WITH_TIME_ZONE.add(new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS z", Locale.ENGLISH));
		DATE_FORMATS_WITH_TIME_ZONE.add(new SimpleDateFormat("yyyy-MM-dd HH-mm-ss z", Locale.ENGLISH));
		DATE_FORMATS_WITH_TIME_ZONE.add(new SimpleDateFormat("yyyy-MM-dd HH-mm z", Locale.ENGLISH));
		DATE_FORMATS_WITH_TIME_ZONE.add(DATE_FORMAT_WITH_TIME_ZONE);
		DATE_FORMATS_WITH_TIME_ZONE.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.ENGLISH));
		DATE_FORMATS_WITH_TIME_ZONE.add(new SimpleDateFormat("yyyy-MM-dd HH:mm z", Locale.ENGLISH));
		DATE_FORMATS_WITH_TIME_ZONE.add(new SimpleDateFormat("yyyy-MM-dd z", Locale.ENGLISH));
		DATE_FORMATS_WITH_TIME_ZONE.add(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS z", Locale.ENGLISH));
		DATE_FORMATS_WITH_TIME_ZONE.add(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z", Locale.ENGLISH));
		DATE_FORMATS_WITH_TIME_ZONE.add(new SimpleDateFormat("dd.MM.yyyy HH:mm z", Locale.ENGLISH));
		DATE_FORMATS_WITH_TIME_ZONE.add(new SimpleDateFormat("dd.MM.yyyy z", Locale.ENGLISH));
		DATE_FORMATS_WITH_TIME_ZONE.add(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS z", Locale.ENGLISH));
		DATE_FORMATS_WITH_TIME_ZONE.add(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z", Locale.ENGLISH));
		DATE_FORMATS_WITH_TIME_ZONE.add(new SimpleDateFormat("dd/MM/yyyy HH:mm z", Locale.ENGLISH));
		DATE_FORMATS_WITH_TIME_ZONE.add(new SimpleDateFormat("dd/MM/yyyy z", Locale.ENGLISH));
		// can parse Date.toString()
		DATE_FORMATS_WITH_TIME_ZONE.add(new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy", Locale.ROOT));

		DATE_FORMAT_WITHOUT_TIME_ZONE_MAP.put(null, DATE_FORMAT_WITHOUT_TIME_ZONE);
		DATE_FORMATS_WITHOUT_TIME_ZONE_MAP.put(null, dateFormatsWithoutTimeZone);

		for (SimpleDateFormat simpleDateFormat : dateFormatsWithoutTimeZone) {
			simpleDateFormat.setLenient(false);
		}
		for (SimpleDateFormat simpleDateFormat : DATE_FORMATS_WITH_TIME_ZONE) {
			simpleDateFormat.setLenient(false);
		}
	}

	private static List<SimpleDateFormat> getDateFormatsWithTimeZone() {
		return DATE_FORMATS_WITH_TIME_ZONE;
	}

	private static SimpleDateFormat getDefaultDateFormatWithTimeZone() {
		return DATE_FORMAT_WITH_TIME_ZONE;
	}

	private static synchronized List<SimpleDateFormat> getDateFormatsWithoutTimeZone(TimeZone zone) {
		List<SimpleDateFormat> simpleDateFormats = DATE_FORMATS_WITHOUT_TIME_ZONE_MAP.get(zone);
		if (simpleDateFormats == null) {
			List<SimpleDateFormat> simpleDateFormatsDef = DATE_FORMATS_WITHOUT_TIME_ZONE_MAP.get(null);
			simpleDateFormats = new ArrayList<>();
			for (SimpleDateFormat simpleDateFormat : simpleDateFormatsDef) {
				SimpleDateFormat clone = (SimpleDateFormat) simpleDateFormat.clone();
				clone.setTimeZone(zone);
				simpleDateFormats.add(clone);
			}
			DATE_FORMATS_WITHOUT_TIME_ZONE_MAP.put(zone, simpleDateFormats);
		}
		return simpleDateFormats;
	}

	private static synchronized SimpleDateFormat getDefaultDateFormatWithoutTimeZone(TimeZone zone) {
		SimpleDateFormat simpleDateFormat = DATE_FORMAT_WITHOUT_TIME_ZONE_MAP.get(zone);
		if (simpleDateFormat == null) {
			simpleDateFormat = DATE_FORMAT_WITHOUT_TIME_ZONE_MAP.get(null);
			SimpleDateFormat clone = (SimpleDateFormat) simpleDateFormat.clone();
			clone.setTimeZone(zone);
			DATE_FORMAT_WITHOUT_TIME_ZONE_MAP.put(zone, clone);
		}
		return simpleDateFormat;
	}

	/**
	 * Checks whether the given time zone id is valid for the other DateFormats used to parse dates in this util class.
	 *
	 * @param timeZoneId the time zone id to check
	 * @return true if the time zone id is valid, false if not
	 */
	public static synchronized boolean isValidTimeZoneId(String timeZoneId) {
		try {
			synchronized (TIME_ZONE_DATE_FORMAT) {
				TIME_ZONE_DATE_FORMAT.parse(timeZoneId);
			}
			return true;
		}
		catch (ParseException e) {
			return false;
		}
	}

	public static String[] getAllowedDateFormatPatterns() {
		List<SimpleDateFormat> dateFormats = new ArrayList<>();
		List<SimpleDateFormat> dateFormatsWithTimeZone = getDateFormatsWithTimeZone();
		List<SimpleDateFormat> dateFormatsWithoutTimeZone = getDateFormatsWithoutTimeZone(null);
		for (int i = 0; i < dateFormatsWithTimeZone.size() || i < dateFormatsWithoutTimeZone.size(); i++) {
			if (i < dateFormatsWithTimeZone.size()) dateFormats.add(dateFormatsWithTimeZone.get(i));
			if (i < dateFormatsWithoutTimeZone.size()) dateFormats.add(dateFormatsWithoutTimeZone.get(i));
		}
		String[] result = new String[dateFormats.size()];
		int index = 0;
		for (SimpleDateFormat format : dateFormats) {
			result[index++] = format.toPattern();
		}
		return result;
	}

	/**
	 * The format returned here should be used when saving DateValues to be able to parse the date with the static
	 * method {@link ValueUtils#createDateValue(QuestionDate, String)}. Be aware that {@link SimpleDateFormat}s are not
	 * thread safe, although you will get a new instance of the format every time you call this method.
	 */
	public static SimpleDateFormat getDefaultDateFormat() {
		return (SimpleDateFormat) DATE_FORMAT_WITH_TIME_ZONE.clone();
	}

	/**
	 * Creates a {@link Value} for a {@link Question}. If the given String
	 * is no valid representation for a Value for the given Question, an IllegalArgumentException is thrown.
	 *
	 * @param terminologyObject the terminologyObject for which the {@link Value} is created
	 * @param valueString       a String representation of the {@link Value} to be
	 *                          created
	 * @return a {@link Value} representing the given valueString, cannot be <tt>null</tt>!
	 * @throws IllegalArgumentException if the given valueString cannot be transformed into a Value
	 * @created 11.08.2012
	 */
	public static Value createValue(TerminologyObject terminologyObject, String valueString) {
		if (terminologyObject instanceof Question) {
			return createQuestionValue((Question) terminologyObject, valueString);
		}
		else if (terminologyObject instanceof Solution) {
			return createSolutionValue(valueString);
		}
		else {
			throw new IllegalArgumentException("Creating values for " + terminologyObject.getClass() + " currently not supported");
		}
	}

	/**
	 * Creates an appropriate Solution value for the given String.<br/>
	 * If the String is parsable as a double, a {@link HeuristicRating} with that score is returned.<br/>
	 * If the String matches one of the {@link Score} symbols (e.g. P7, N7...) a {@link HeuristicRating} with that
	 * score is returned.<br>
	 * If the String matches any of the solutions states (like established, suggested...), a Rating for the given
	 * {@link Rating.State} is returned.
	 *
	 * @param valueString the String representation of the solution value
	 * @return a {@link Rating} representing the given valueString, cannot be <tt>null</tt>!
	 * @throws IllegalArgumentException if the given valueString cannot be transformed into a Value
	 */
	public static Rating createSolutionValue(String valueString) {
		double doubleValue = parseDouble(valueString);
		if (!Double.isNaN(doubleValue)) {
			return new HeuristicRating(doubleValue);
		}
		for (Score score : Score.getAllScores()) {
			if (score.getSymbol().equalsIgnoreCase(valueString)) {
				return new HeuristicRating(score);
			}
		}
		return new Rating(valueString);
	}

	/**
	 * Creates a {@link Value} for a {@link Question}. If the given String
	 * is no valid representation for a Value for the given Question, an IllegalArgumentException is thrown.
	 *
	 * @param question    the question for which the {@link Value} is created
	 * @param valueString a String representation of the {@link Value} to be
	 *                    created
	 * @return a {@link Value} representing the given valueString, cannot be <tt>null</tt>!
	 * @throws IllegalArgumentException if the given valueString cannot be transformed into a Value
	 * @created 11.08.2012
	 */
	public static Value createQuestionValue(Question question, String valueString) {

		Value value = null;

		if (valueString.equalsIgnoreCase(Unknown.getInstance().getValue().toString())) {
			value = Unknown.getInstance();
		}

		else if (valueString.equalsIgnoreCase(UndefinedValue.getInstance().toString())) {
			value = UndefinedValue.getInstance();
		}

		else if (question instanceof QuestionChoice) {
			value = createQuestionChoiceValue((QuestionChoice) question, valueString);
		}

		else if (question instanceof QuestionNum) {
			double doubleValue = parseDouble(valueString);
			if (!Double.isNaN(doubleValue)) {
				value = new NumValue(doubleValue);
			}
		}

		else if (question instanceof QuestionText) {
			value = new TextValue(valueString);
		}

		else if (question instanceof QuestionDate) {
			try {
				value = createDateValue((QuestionDate) question, valueString);
			}
			catch (IllegalArgumentException ignore1) {
				try {
					value = new DateValue(new Date(Long.parseLong(valueString)));
				}
				catch (NumberFormatException ignore2) {
					// will be handled below when value == null.
				}
			}
		}

		if (value == null) {
			throw new IllegalArgumentException("Unable to create value from String '"
					+ valueString + "' for question '" + question.getName() + "'");
		}

		return value;
	}

	private static double parseDouble(String valueString) {
		double doubleValue;
		try {
			doubleValue = Double.parseDouble(valueString.replace(',', '.'));
		}
		catch (IllegalArgumentException e) {
			doubleValue = Double.NaN;
		}
		return doubleValue;
	}

	/**
	 * Creates a {@link ChoiceValue} for a {@link QuestionChoice}. If the given String
	 * is no valid representation for a Value for the given Question, an IllegalArgumentException is thrown.
	 * This method also creates {@link ChoiceValue}s for {@link QuestionMC}s. To get a {@link MultipleChoiceValue}, use
	 * the method {@link #handleExistingValue(TerminologyObject, Value, Value)}, which will merge a {@link ChoiceValue}
	 * with an existing {@link ChoiceValue} or {@link MultipleChoiceValue} to a new {@link MultipleChoiceValue}.
	 *
	 * @param question    the question for which the {@link Value} is created
	 * @param valueString a String representation of the {@link Value} to be
	 *                    created
	 * @return a {@link Value} or <tt>null</tt> if the given String is no valid
	 * representation for a Value for the given Question
	 * @created 11.08.2012
	 */
	public static Value createQuestionChoiceValue(QuestionChoice question, String valueString) {
		Choice choice = KnowledgeBaseUtils.findChoice(question, valueString);
		if (choice == null) {
			throw new IllegalArgumentException("'" + valueString + "' is not a valid choice for question '" + question.getName() + "'");
		}
		if (question instanceof QuestionMC) {
			return new MultipleChoiceValue(new ChoiceID(choice));
		}
		else {
			return new ChoiceValue(choice);
		}
	}

	/**
	 * Handles the value for normal interview behavior depending on the existing value. If <tt>value</tt> and
	 * <tt>existingValue</tt> are equal, Unknown is returned. In case of {@link QuestionMC}, <tt>value</tt> and
	 * <tt>existingValue</tt> are merged into a {@link MultipleChoiceValue}. If the existing {@link Value} already
	 * contains the new <tt>value</tt>, the new value will be removed from the {@link MultipleChoiceValue}. <p/>
	 * In all the other cases, the value is just returned without changing it.
	 *
	 * @param object        the {@link TerminologyObject} belonging to <tt>value</tt> and
	 *                      <tt>existingValue</tt>
	 * @param value         the value to handel in context to the existingValue
	 * @param existingValue the existingValue to handle in context to the value
	 * @return a value taking into account the given <tt>value</tt> and <tt>existingValue</tt>
	 */
	public static Value handleExistingValue(TerminologyObject object, Value value, Value existingValue) {
		if (value.equals(existingValue)) return Unknown.getInstance();
		if (object instanceof QuestionMC
				&& (value instanceof ChoiceValue || value instanceof MultipleChoiceValue)
				&& (existingValue instanceof ChoiceValue || existingValue instanceof MultipleChoiceValue)) {
			MultipleChoiceValue multipleChoiceValue = mergeChoiceValuesXOR((QuestionMC) object, value, existingValue);
			if (multipleChoiceValue.getChoiceIDs().isEmpty()) {
				value = Unknown.getInstance();
			}
			else {
				value = multipleChoiceValue;
			}
		}
		return value;
	}

	/**
	 * Merges two values into one {@link MultipleChoiceValue}. The merging is done using XOR (exclusive disjunction),
	 * meaning that return value will contain all choices from the given values, except those that were in both given
	 * values. If you also want those choices present in both given values, use method {@link
	 * #mergeChoiceValuesOR(QuestionMC, Value, Value)}.
	 * <p>
	 * The argument values can either be {@link ChoiceValue} or {@link MultipleChoiceValue}. If the are of another
	 * type, an IllegalArgumentException is thrown.
	 *
	 * @param questionMC the question to which the values belong
	 * @param value1     one of the values to be merged
	 * @param value2     one of the values to be merged
	 * @return a {@link MultipleChoiceValue} with the xor merged choices from value1 and value2
	 * @throws IllegalArgumentException if the argument Values are not of type {@link ChoiceValue} or
	 *                                  {@link MultipleChoiceValue}
	 */
	public static MultipleChoiceValue mergeChoiceValuesXOR(QuestionMC questionMC, Value value1, Value value2) {
		return mergeChoiceValues(questionMC, value1, value2, ValueUtils::xorMerge);
	}

	/**
	 * Merges two values into one {@link MultipleChoiceValue}. The merging is done using OR (disjunction),
	 * meaning that return value will contain all choices from the given values. If you don't want those choices
	 * present in both given values, use method {@link #mergeChoiceValuesXOR(QuestionMC, Value, Value)}.
	 * <p>
	 * The argument values can either be {@link ChoiceValue} or {@link MultipleChoiceValue}. If the are of another
	 * type, an IllegalArgumentException is thrown.
	 *
	 * @param questionMC the question to which the values belong
	 * @param value1     one of the values to be merged
	 * @param value2     one of the values to be merged
	 * @return a {@link MultipleChoiceValue} with the merged choices from value1 and value2
	 * @throws IllegalArgumentException if the argument Values are not of type {@link ChoiceValue} or
	 *                                  {@link MultipleChoiceValue}
	 */
	public static MultipleChoiceValue mergeChoiceValuesOR(QuestionMC questionMC, Value value1, Value value2) {
		return mergeChoiceValues(questionMC, value1, value2, ValueUtils::orMerge);
	}

	private static List<Choice> xorMerge(List<Choice> choices1, List<Choice> choices2) {
		List<Choice> xorMerged = new ArrayList<>();
		for (Choice choice1 : choices1) {
			if (!choices2.remove(choice1)) {
				xorMerged.add(choice1);
			}
		}
		xorMerged.addAll(choices2);
		return xorMerged;
	}

	private static MultipleChoiceValue mergeChoiceValues(QuestionMC questionMC, Value value1, Value value2, BiFunction<List<Choice>, List<Choice>, List<Choice>> mergeFunction) {
		List<Choice> choices1 = getChoices(questionMC, value1);
		List<Choice> choices2 = getChoices(questionMC, value2);
		List<Choice> orMerged = mergeFunction.apply(choices1, choices2);
		return MultipleChoiceValue.fromChoices(orMerged);
	}

	private static List<Choice> orMerge(List<Choice> choices1, List<Choice> choices2) {
		LinkedHashSet<Choice> orMergedSet = new LinkedHashSet<>();
		orMergedSet.addAll(choices1);
		orMergedSet.addAll(choices2);
		return new ArrayList<>(orMergedSet);
	}

	private static List<Choice> getChoices(QuestionMC questionMC, Value value) {
		List<Choice> choices = new ArrayList<>();
		if (value instanceof ChoiceValue) {
			choices.add(((ChoiceValue) value).getChoice(questionMC));
		}
		else if (value instanceof MultipleChoiceValue) {
			choices.addAll(((MultipleChoiceValue) value)
					.asChoiceList(questionMC));
		}
		else {
			throw new IllegalArgumentException("Invalid value type of merging. Expected "
					+ ChoiceValue.class.getSimpleName() + " or " + MultipleChoiceValue.class
					.getSimpleName() + ", but was " + value.getClass().getSimpleName());
		}
		return choices;
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
	 * also properly readable for humans. The String will contain the time zone ID of the JVM.
	 *
	 * @param value the value for which we want the date string
	 */
	public static String getDateVerbalization(DateValue value) {
		return getDateVerbalization(value, TimeZoneDisplayMode.ALWAYS);
	}

	/**
	 * Returns the date as String in a format which can be parsed with {@link DateValue#createDateValue(String)} and is
	 * also properly readable for humans. The String will contain the time zone ID of the JVM.
	 *
	 * @param date the date for which we want the string
	 */
	public static String getDateVerbalization(Date date) {
		return getDateVerbalization(date, TimeZoneDisplayMode.ALWAYS);
	}

	/**
	 * Returns the date as String in a format which can be parsed with {@link DateValue#createDateValue(String)} and is
	 * also properly readable for humans. The String will contain the time zone ID of the JVM.
	 *
	 * @param date         the date for which we want the string
	 * @param timeZoneMode decides whether the used TimeZone should be appended to the string
	 */
	public static String getDateVerbalization(Date date, TimeZoneDisplayMode timeZoneMode) {
		return getDateVerbalization((TimeZone) null, date, timeZoneMode);
	}

	/**
	 * Returns the date as String in a format which can be parsed with {@link DateValue#createDateValue(String)} and is
	 * also properly readable for humans. The String will contain the time zone ID of the JVM.
	 *
	 * @param value        the value for which we want the date string
	 * @param timeZoneMode decides whether the used TimeZone should be appended to the string
	 */
	public static String getDateVerbalization(DateValue value, TimeZoneDisplayMode timeZoneMode) {
		return getDateVerbalization((TimeZone) null, value.getDate(), timeZoneMode);
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
		return getDateVerbalization(getTimeZone(question), value.getDate(), timeZoneMode);
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
			dateFormat = getDefaultDateFormatWithTimeZone();
		}
		else if (timeZoneMode == TimeZoneDisplayMode.NEVER) {
			dateFormat = getDefaultDateFormatWithoutTimeZone(timeZone);
		}
		else {
			dateFormat = TimeZone.getDefault().equals(timeZone) || timeZone == null ?
					getDefaultDateFormatWithoutTimeZone(timeZone) : getDefaultDateFormatWithTimeZone();
		}
		if (timeZone != null) {
			dateFormat = (SimpleDateFormat) dateFormat.clone();
			dateFormat.setTimeZone(timeZone);
			dateString = dateFormat.format(date);
		}
		else {
			//noinspection SynchronizationOnLocalVariableOrMethodParameter
			synchronized (dateFormat) {
				dateString = dateFormat.format(date);
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
	 * @return the parsed DateValue, cannot be <tt>null</tt>!
	 * @throws IllegalArgumentException if the given valueString cannot be transformed into a Value
	 * @created 13.04.2015
	 */
	public static DateValue createDateValue(QuestionDate question, String dateString) throws IllegalArgumentException {
		return createDateValue(getTimeZone(question), dateString);
	}

	/**
	 * Creates a {@link DateValue} from a given String. If no time zone is given in the String, the time zone of the
	 * local JVM will be used.
	 * To be parseable, the String has to come in one of the available {@link DateFormat} from
	 * {@link DateValue#getAllowedFormatStrings()}.
	 * <p>
	 * <b>Attention:</b> If the corresponding question is available while calling the method, you should instead use
	 * {@link ValueUtils#createDateValue(QuestionDate, String)}, especially, if your String does not contain a TimeZone
	 * identifier. Having the Question available will use a specified time zone of the question's UNIT property if
	 * given
	 * and if the String does not provide one. If the dateString always has an appended time zone, there will be no
	 * difference.
	 *
	 * @param dateString the value to parse
	 * @return the parsed DateValue, cannot be <tt>null</tt>!
	 * @throws IllegalArgumentException if the given valueString cannot be transformed into a Value
	 * @created 23.10.2013
	 */
	public static DateValue createDateValue(String dateString) {
		return createDateValue((TimeZone) null, dateString);
	}

	/**
	 * Creates a {@link DateValue} from a given String. If the String comes with a time zone, that time zone will be
	 * used to parse the date. If no time zone is given in the String, the timeZone attribute will be used. If that
	 * isn't given either, the default time zone of the JVM is used.
	 * To be parseable, the String has to come in one of the available {@link DateFormat} from
	 * {@link DateValue#getAllowedFormatStrings()}.
	 *
	 * @param timeZone   the timeZone to be used if the the dateString does not contain one
	 * @param dateString the value to parse
	 * @return the parsed DateValue
	 * @created 13.04.2015
	 */
	private static DateValue createDateValue(TimeZone timeZone, String dateString) {
		// We parse the time zone separately. There are two reasons.
		// 1. SimpleDateFormat does not parse strict at the end of the string. If it can match a format to the
		//    start of the string, it will do it, disregarding any appended invalid time zones or other characters.
		//    If we would just check with the formats with the time zone pattern z and than with the formats without,
		//    the dates would be parsed by the formats without, disregarding any invalid time zones.
		// 2. There is a difference between parsing certain date String with appended time zone id and parsing the
		//    date String without the append time zone id and instead setting the time zone of the SDF to the one
		//    represented by the appended String. The difference appears when parsing dates with time zones that have
		//    summer/daylight-saving-time like CET and CEST.
		//    To stay consistent also with verbalizing the dates again, we choose to parse the time zones separate.
		Matcher matcher = DATE_STRING_TIME_ZONE_PATTERN.matcher(dateString);
		if (matcher.find()) {
			dateString = dateString.substring(0, matcher.start());
			timeZone = getTimeZone(matcher.group(1));
		}
		for (SimpleDateFormat dateFormat : getDateFormatsWithoutTimeZone(timeZone)) {
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

		throw new IllegalArgumentException("'" + dateString + "' can not be recognized as a date");
	}

	/**
	 * Returns the TimeZone object based on the time zone id specified for the given question using the MMInfo.UNIT
	 * property.
	 */
	public static TimeZone getTimeZone(QuestionDate question) {
		TimeZone timeZone = null;
		if (question != null) {
			String timeZoneId = question.getInfoStore().getValue(MMInfo.UNIT);
			if (timeZoneId != null) {
				timeZone = getTimeZone(timeZoneId);
			}
		}
		return timeZone;
	}

	/**
	 * Returns a time zone for a timeZoneId, which is compatible with the z pattern of the SimpleDateFormat. Every
	 * timeZoneId that can be understood by the z pattern can also be understood by this method.
	 *
	 * @param timeZoneId the id of the wanted time zone
	 * @return the wanted time zone object or null, if the id is invalid
	 */
	public static TimeZone getTimeZone(String timeZoneId) {
		if (timeZoneId == null) return null;
		// not sure why, but the SDT returns false time zones for UTC and GMT...
		if (timeZoneId.equalsIgnoreCase("UTC") || timeZoneId.equalsIgnoreCase("GMT")
				|| timeZoneId.matches("GMT[+-]\\d?\\d:\\d\\d$")) {
			return TimeZone.getTimeZone(timeZoneId.toUpperCase());
		}
		try {
			synchronized (TIME_ZONE_DATE_FORMAT) {
				TIME_ZONE_DATE_FORMAT.parse(timeZoneId);
				return TIME_ZONE_DATE_FORMAT.getTimeZone();
			}
		}
		catch (ParseException ignore) {
			throw new IllegalArgumentException("'" + timeZoneId + "' is not a valid time zone id");
		}
	}

}
