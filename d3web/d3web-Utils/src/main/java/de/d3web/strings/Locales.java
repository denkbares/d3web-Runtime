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

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

/**
 * Utility methods to deal with locales.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 08.01.2015
 */
public class Locales {

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
	 * preferred locale, not the ROOT locale is included.
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

		if (preferred != null) {
			// get locale if available
			if (available.contains(preferred)) return preferred;

			// otherwise try to find best locale
			Locale bestLocale = null;
			int bestScore = 0;
			for (Locale locale : available) {
				int score = rateMatch(preferred, locale);
				if (score > bestScore) {
					bestScore = score;
					bestLocale = locale;
				}
			}
			if (bestLocale != null) return bestLocale;
		}

		// otherwise use first one
		return available.iterator().next();
	}

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

}
