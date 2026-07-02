/*
 * Copyright (C) 2026 denkbares GmbH
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

package de.d3web.core.knowledge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.denkbares.plugin.test.InitPluginManager;
import com.denkbares.utils.Triple;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.knowledge.terminology.info.Property;

import static org.junit.Assert.assertEquals;

/**
 * Tests that {@link DefaultInfoStore} returns its entries and locales in a stable, deterministic order (German
 * first, then English, then all other locales sorted by their language tag, ROOT last), independent of hash map
 * iteration order and JDK version.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 02.07.2026
 */
public class DefaultInfoStoreTest {

	private InfoStore store;

	@BeforeClass
	public static void init() throws IOException {
		InitPluginManager.init();
	}

	@Before
	public void setUp() {
		store = new DefaultInfoStore();
	}

	@Test
	public void entriesOfPropertyHaveStableLocaleOrder() {
		// add in scrambled order...
		store.addValue(MMInfo.PROMPT, Locale.ROOT, "root");
		store.addValue(MMInfo.PROMPT, Locale.FRENCH, "fr");
		store.addValue(MMInfo.PROMPT, Locale.UK, "en-GB");
		store.addValue(MMInfo.PROMPT, Locale.ITALIAN, "it");
		store.addValue(MMInfo.PROMPT, Locale.GERMANY, "de-DE");
		store.addValue(MMInfo.PROMPT, Locale.ENGLISH, "en");
		store.addValue(MMInfo.PROMPT, Locale.GERMAN, "de");

		// ...and expect german first, then english, then others by language tag, ROOT last
		Map<Locale, String> entries = store.entries(MMInfo.PROMPT);
		assertEquals(List.of(Locale.GERMAN, Locale.GERMANY, Locale.ENGLISH, Locale.UK,
						Locale.FRENCH, Locale.ITALIAN, Locale.ROOT),
				new ArrayList<>(entries.keySet()));
	}

	@Test
	public void allEntriesHaveStableOrder() {
		store.addValue(MMInfo.PROMPT, Locale.ENGLISH, "prompt-en");
		store.addValue(MMInfo.DESCRIPTION, Locale.ROOT, "description-root");
		store.addValue(MMInfo.PROMPT, Locale.GERMAN, "prompt-de");
		store.addValue(MMInfo.DESCRIPTION, Locale.GERMAN, "description-de");

		List<String> keys = new ArrayList<>();
		for (Triple<Property<?>, Locale, Object> entry : store.entries()) {
			keys.add(entry.getA().getName() + ":" + entry.getB().toLanguageTag());
		}
		// sorted by property name first, by locale second
		assertEquals(List.of("description:de", "description:und", "prompt:de", "prompt:en"), keys);
	}

	@Test
	public void getValueFallsBackDeterministically() {
		store.addValue(MMInfo.PROMPT, Locale.FRENCH, "fr");
		store.addValue(MMInfo.PROMPT, Locale.ENGLISH, "en");
		store.addValue(MMInfo.PROMPT, Locale.GERMAN, "de");

		// no match and no ROOT entry available: the first locale of the stable order (german) wins
		assertEquals("de", store.getValue(MMInfo.PROMPT, Locale.ITALIAN));
	}
}
