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

package de.d3web.utils.test;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import de.d3web.strings.Locales;

import static org.junit.Assert.assertEquals;

/**
 * @author Volker Belli (denkbares GmbH)
 * @created 08.01.2015
 */
public class LocalesTest {

	@Test
	public void bestMatch() {
		Assert.assertEquals(Locale.GERMAN, Locales.findBestLocale(Locale.GERMANY,
				Locale.GERMAN, Locale.ENGLISH, Locale.ROOT));
		Assert.assertEquals(Locale.GERMANY, Locales.findBestLocale(Locale.GERMAN,
				Locale.GERMANY, Locale.ENGLISH, Locale.ROOT));
		Assert.assertEquals(Locale.ROOT, Locales.findBestLocale(Locale.CHINESE,
				Locale.GERMANY, Locale.ENGLISH, Locale.ROOT));
		Assert.assertEquals(Locale.GERMANY, Locales.findBestLocale(Locale.CHINESE,
				Locale.GERMANY, Locale.ENGLISH));
	}

	@Test
	public void parseLocale() {
		Locale[] locales = Locale.getAvailableLocales();
		for (Locale locale : locales) {
			// TODO: java 6 does not support scripts, so Strings also does not. Remove this skip operation when migrate to java 7
			if (locale.toString().contains("#")) continue;
			assertEquals(locale, Locales.parseLocale(locale.toString()));
		}
	}
}
