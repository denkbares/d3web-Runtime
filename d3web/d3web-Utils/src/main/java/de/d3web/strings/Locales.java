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
	public static Locale findBestLocale(Locale preferred, Collection<Locale> available) {
		if (available == null || available.isEmpty()) return null;

		if (preferred != null) {
			// get locale if available
			if (available.contains(preferred)) return preferred;

			// otherwise try to find best locale
			Locale best = Locale.lookup(
					Arrays.asList(new Locale.LanguageRange(preferred.toLanguageTag())),
					available);
			if (best != null) return best;
		}

		// otherwise use root locale or any if not available
		return available.contains(Locale.ROOT) ? Locale.ROOT : available.iterator().next();
	}

}
