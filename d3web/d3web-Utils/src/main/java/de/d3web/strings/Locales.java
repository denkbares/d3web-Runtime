/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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

package de.d3web.strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Utility methods to deal with locales.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 08.01.2015
 */
public class Locales {

	public static final Comparator<Locale> ASCENDING = new Comparator<Locale>() {
		@Override
		public int compare(Locale o1, Locale o2) {
			if (o1 == o2) return 0;
			if (o1 == null) return -1;
			if (o2 == null) return 1;
			return String.valueOf(o1).compareTo(String.valueOf(o2));
		}
	};

	/**
	 * Parses a locale from a locale string representation. This is the inverse method to {@link
	 * java.util.Locale#toString()}. If the specified text is null or cannot be parsed, null is
	 * returned. If the specified text is empty, the root locale is returned. Leading or trailing
	 * whitespaces will be ignored by this method.
	 *
	 * @param text the locale's text representation to be parsed
	 * @return the parsed locale
	 */
	public static Locale parseLocale(String text) {
		return Strings.parseLocale(text);
	}

	/**
	 * Returns the best matching locale out of a collection of available locales. It returns the
	 * ROOT locale if no locales matches the available locales, but the root locales is included. It
	 * returns the first locale of the specified available locales if neither any locale matches the
	 * preferred locale, not the ROOT locale is included.
	 * <p/>
	 * If the available locales are null or empty, null is returned.
	 *
	 * @param preferred the preferred locale to be used
	 * @param available the available locales
	 * @return the best matching locale
	 */
	public static Locale findBestLocale(Locale preferred, Locale... available) {
		return findBestLocale(preferred, Arrays.asList(available));
	}

	/**
	 * Returns the best matching locale out of a collection of available locales. It returns the
	 * ROOT locale if no locales matches the available locales, but the root locales is included. It
	 * returns the first locale of the specified available locales if neither any locale matches the
	 * preferred locale, nor the ROOT locale is included.
	 * <p/>
	 * If the available locales are null or empty, null is returned. Otherwise the method is
	 * guaranteed to return a locale instance out of the available ones.
	 *
	 * @param preferred the preferred locale to be used
	 * @param available the available locales
	 * @return the best matching locale
	 */
	public static Locale findBestLocale(Locale preferred, Collection<Locale> available) {
		// if no locales contained, return null (we cannot select one)
		if (available == null || available.isEmpty()) return null;

		Locale defaultLocale = available.iterator().next();
		return (preferred != null)
				? findBestLocale(preferred, available, 1, defaultLocale)
				: defaultLocale;
	}

	private static Locale findBestLocale(Locale preferred, Collection<Locale> available, int minScore, Locale defaultLocale) {
		// get locale if available
		if (available.contains(preferred)) return preferred;

		// otherwise try to find best locale
		Locale bestLocale = null;
		int bestScore = minScore - 1;
		for (Locale locale : available) {
			int score = rateMatch(preferred, locale);
			if (score > bestScore) {
				bestScore = score;
				bestLocale = locale;
			}
		}
		if (bestLocale != null) return bestLocale;

		// otherwise use default value
		return defaultLocale;
	}

	/**
	 * Returns the best matching locale out of a collection of available locales. The best matching
	 * locale is the first locale of the preferenceList that has at least a language match in the
	 * available locales; for that locale, the best matching one is selected out of the availables.
	 * The Method returns the ROOT locale if no locales matches the available locales, but the root
	 * locales is included. It returns the first locale of the specified available locales if
	 * neither any locale matches the preferred locale, nor the ROOT locale is included.
	 * <p/>
	 * If the available locales are null or empty, null is returned. Otherwise the method is
	 * guaranteed to return a locale instance out of the available ones. If the preferenceList is
	 * empty the ROOT locale is matched against the available locales.
	 *
	 * @param preferenceList the preferred locales to be used
	 * @param available the available locales
	 * @return the best matching locale
	 */
	public static Locale findBestLocale(List<Locale> preferenceList, Collection<Locale> available) {
		// if no locales contained, return null (we cannot select one)
		if (available == null || available.isEmpty()) return null;

		// search for first locale that has a language match
		// and use the best match for that locale
		for (Locale preferred : preferenceList) {
			Locale match = findBestLocale(preferred, available, 100, null);
			if (match != null) return match;
		}

		// otherwise try normal selection of the first preferred locale
		Locale first = preferenceList.isEmpty() ? Locale.ROOT : preferenceList.get(0);
		return findBestLocale(first, available);
	}

	/**
	 * Rates the similarity between the two specified locales with a number between 0 and 170.
	 */
	private static int rateMatch(Locale preferred, Locale available) {
		int score = 0;

		// score if same language or both empty language or available is more common (empty)
		String p1 = preferred.getLanguage();
		String a1 = available.getLanguage();
		if (p1.equals(a1)) score += 100;
		else if (a1.isEmpty()) score += 10;
		else return score;

		// score if same country or available is more common (empty country)
		String p2 = preferred.getCountry();
		String a2 = available.getCountry();
		if (!p2.isEmpty() && p2.equals(a2)) score += 50;
		else if (a2.isEmpty()) score += 5;
		else return score;

		// score if same variant or available is more common (empty variant)
		String p3 = preferred.getVariant();
		String a3 = available.getVariant();
		if (p3.equals(a3)) score += 20;
		else if (a3.isEmpty()) score += 2;

		return score;
	}

	/**
	 * Creates a String representation for a specified locale that can later on be parsed by the
	 * #parseLocale method.
	 * <p/>
	 * It is very similar to the {@link Locale#toString()} method, but does not produce an empty
	 * string for the default locale, instead "ROOT" is returned. Additionally, null is returned as
	 * "null".
	 *
	 * @param locale if the specified languages shall be sorted
	 * @return the parsable string
	 */
	public static String toParsableLocale(Locale locale) {
		String name = (locale == null) ? "null" : String.valueOf(locale);
		return Strings.isBlank(name) ? "ROOT" : name;
	}

	/**
	 * Creates a list of the languages that can later on be parsed by the #parseList method.
	 * <p/>
	 * It is very similar to the {@link Locale#toString()} method, but does not produce an empty
	 * string for the default locale, instead "ROOT" is returned. Additionally, null locales in the
	 * collection are returned as "null".
	 *
	 * @param languages the languages to represent as a parsable string
	 * @return the parsable string
	 */
	public static String toParsableList(Locale... languages) {
		return toParsableList(Arrays.asList(languages));
	}

	/**
	 * Creates a list of the languages that can later on be parsed by the #parseList method.
	 * <p/>
	 * It is very similar to the {@link Locale#toString()} method, but does not produce an empty
	 * string for the default locale, instead "ROOT" is returned. Additionally, null locales in the
	 * collection are returned as "null".
	 *
	 * @param languages the languages to represent as a parsable string
	 * @return the parsable string
	 */
	public static String toParsableList(Collection<Locale> languages) {
		if (languages == null) return "";
		StringBuilder result = new StringBuilder();
		for (Locale language : languages) {
			if (result.length() > 0) result.append(";");
			result.append(toParsableLocale(language));
		}
		return result.toString();
	}

	/**
	 * Creates a list of the languages that can later on be parsed by the #parseList method.
	 * <p/>
	 * It is very similar to the {@link Locale#toString()} method, but does not produce an empty
	 * string for the default locale, instead "ROOT" is returned. Additionally, null locales in the
	 * collection are returned as "null".
	 *
	 * @param sorted if the specified languages shall be sorted
	 * @param languages the languages to represent as a parsable string
	 * @return the parsable string
	 */
	public static String toParsableList(boolean sorted, Collection<Locale> languages) {
		if (languages == null) return "";
		if (sorted) {
			languages = new ArrayList<Locale>(languages);
			Collections.sort((List) languages, ASCENDING);
		}
		return toParsableList(languages);
	}

	/**
	 * Creates a list of the languages that can later on be parsed by the #parseList method.
	 * <p/>
	 * It is very similar to the {@link Locale#toString()} method, but does not produce an empty
	 * string for the default locale, instead "ROOT" is returned. Additionally, null locales in the
	 * collection are returned as "null".
	 *
	 * @param sorted if the specified languages shall be sorted
	 * @param languages the languages to represent as a parsable string
	 * @return the parsable string
	 */
	public static String toParsableList(boolean sorted, Locale... languages) {
		return toParsableList(sorted, Arrays.asList(languages));
	}

	/**
	 * Reads a representation of a set/list of languages. Each language is separated by ',' or ';'.
	 * The root language shall be represented by "ROOT". The returned set preserves the order of the
	 * entries read from the string.
	 *
	 * @param languages the string representation ot be read
	 * @return the languages read from the string representation
	 */
	public static Set<Locale> parseList(String languages) {
		if (Strings.isBlank(languages)) return Collections.emptySet();
		LinkedHashSet<Locale> result = new LinkedHashSet<Locale>();
		for (String lang : languages.split("[;,]")) {
			result.add(parseLocale(lang));
		}
		return result;
	}
}
