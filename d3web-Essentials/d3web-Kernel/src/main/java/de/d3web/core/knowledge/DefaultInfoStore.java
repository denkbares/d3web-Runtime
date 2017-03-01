/*
 * Copyright (C) 2011 denkbares GmbH
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.jetbrains.annotations.NotNull;

import com.denkbares.strings.Locales;
import com.denkbares.utils.Triple;
import de.d3web.core.knowledge.terminology.info.Property;

public class DefaultInfoStore implements InfoStore {

	private static final String KEY_MUST_NOT_BE_NULL = "The key must not be null.";

	private final Map<Property<?>, Map<Locale, Object>> entries = new HashMap<>();

	@Override
	@NotNull
	public Collection<Triple<Property<?>, Locale, Object>> entries() {
		Collection<Triple<Property<?>, Locale, Object>> result = new ArrayList<>();
		for (Entry<Property<?>, Map<Locale, Object>> entry : this.entries.entrySet()) {
			for (Entry<Locale, Object> localeEntry : entry.getValue().entrySet()) {
				result.add(new Triple<>(
						entry.getKey(),
						localeEntry.getKey(),
						localeEntry.getValue()));
			}
		}
		return Collections.unmodifiableCollection(result);
	}

	@Override
	@NotNull
	public <StoredType> Map<Locale, StoredType> entries(Property<StoredType> key) {
		//noinspection unchecked
		return Collections.unmodifiableMap((Map<Locale, StoredType>) entries.getOrDefault(key, Collections.emptyMap()));
	}

	@Override
	public <StoredType> StoredType getValue(Property<StoredType> key, Locale... language) {

		if (key == null) {
			throw new NullPointerException(KEY_MUST_NOT_BE_NULL);
		}

		if (language == null) language = new Locale[0];

		// fast check for no language at all
		if (language.length == 0) {
			StoredType value = getEntry(key, NO_LANGUAGE);
			if (value != null) {
				return value;
			}
		}
		// fast check for exactly one language
		else if (language.length == 1) {
			StoredType value = getEntry(key, language[0]);
			if (value != null) {
				return value;
			}
		}

		// ok, lets see what we have and return best match
		Collection<Locale> allAvailableLocales = getAvailableLocales(key);
		Locale bestLocale = Locales.findBestLocale(Arrays.asList(language), allAvailableLocales);
		StoredType value = getEntry(key, bestLocale);
		if (value != null) {
			return value;
		}

		// if nothing else available use default value or null
		return key.getDefaultValue();
	}

	@NotNull
	private <StoredType> Collection<Locale> getAvailableLocales(Property<StoredType> key) {
		return entries.getOrDefault(key, Collections.emptyMap()).keySet();
	}

	private <StoredType> StoredType getEntry(Property<StoredType> key, Locale language) {
		Map<Locale, Object> localeObjectMap = this.entries.get(key);
		if (localeObjectMap == null) return null;
		return key.getStoredClass().cast(localeObjectMap.get(language));
	}

	@Override
	public boolean remove(Property<?> key) {
		if (key == null) throw new NullPointerException(KEY_MUST_NOT_BE_NULL);
		return remove(key, NO_LANGUAGE);
	}

	@Override
	public boolean remove(Property<?> key, Locale language) {
		if (key == null) throw new NullPointerException(KEY_MUST_NOT_BE_NULL);
		Map<Locale, Object> localeObjectMap = entries.get(key);
		if (localeObjectMap == null) return false;
		boolean removed = localeObjectMap.remove(language) != null;
		if (removed && localeObjectMap.isEmpty()) entries.remove(key);
		return removed;
	}

	@Override
	public boolean contains(Property<?> key) {
		return contains(key, NO_LANGUAGE);
	}

	@Override
	public boolean contains(Property<?> key, Locale language) {
		Map<Locale, Object> localeObjectMap = this.entries.get(key);
		return localeObjectMap != null && localeObjectMap.containsKey(language);
	}

	@Override
	public <T> void addValue(Property<? super T> key, T value) {
		addValue(key, NO_LANGUAGE, value);
	}

	@Override
	public void addValue(Property<?> key, Locale language, Object value) {
		if (value == null) throw new NullPointerException("The value must not be null.");
		if (key == null) throw new NullPointerException(KEY_MUST_NOT_BE_NULL);
		if (language != NO_LANGUAGE && !key.isMultilingual()) {
			throw new IllegalArgumentException("The property " + key
					+ " does not support a language");
		}
		if (!key.getStoredClass().isInstance(value)) {
			throw new ClassCastException("value '" + value +
					"' is not compatible with defined storage class "
					+ key.getStoredClass());
		}
		Map<Locale, Object> localeObjectMap = entries.computeIfAbsent(key, k -> new HashMap<>(4));
		localeObjectMap.put(language, value);
	}

	@Override
	public boolean isEmpty() {
		return entries.isEmpty();
	}
}
