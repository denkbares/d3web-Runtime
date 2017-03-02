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
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import com.denkbares.strings.Locales;
import com.denkbares.utils.Triple;
import de.d3web.core.knowledge.terminology.info.Property;

public class DefaultInfoStore implements InfoStore {

	private Map<Property<?>, Object> entries = null;

	@Override
	@NotNull
	public Collection<Triple<Property<?>, Locale, Object>> entries() {
		if (entries == null) return Collections.emptyList();
		Collection<Triple<Property<?>, Locale, Object>> result = new ArrayList<>();
		for (Entry<Property<?>, Object> entry : this.entries.entrySet()) {
			if (entry.getKey().isMultilingual()) {
				for (Entry<Locale, Object> localeEntry : asMap(entry.getValue()).entrySet()) {
					result.add(new Triple<>(entry.getKey(), localeEntry.getKey(), localeEntry.getValue()));
				}
			}
			else {
				result.add(new Triple<>(entry.getKey(), NO_LANGUAGE, entry.getValue()));
			}
		}
		return Collections.unmodifiableCollection(result);
	}

	@Override
	@NotNull
	public <StoredType> Map<Locale, StoredType> entries(Property<StoredType> key) {
		keyMustNotBeNull(key);
		if (entries == null) return Collections.emptyMap();
		if (key.isMultilingual()) {
			//noinspection unchecked
			return Collections.unmodifiableMap((Map<Locale, StoredType>)
					entries.getOrDefault(key, Collections.emptyMap()));
		}
		else {
			StoredType value = key.castToStoredValue(entries.get(key));
			if (value != null) {
				return Collections.singletonMap(NO_LANGUAGE, value);
			}
		}
		return Collections.emptyMap();
	}

	@Override
	public <StoredType> StoredType getValue(Property<StoredType> key, Locale... language) {
		keyMustNotBeNull(key);
		if (language == null) language = new Locale[0];

		// fast check for no language at all
		if (language.length == 0) {
			StoredType value = getEntry(key, NO_LANGUAGE);
			if (value != null) {
				return value;
			}
			// if this is not a multilingual property, don't bother with locale matching
			if (!key.isMultilingual()) {
				return key.getDefaultValue();
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
		if (entries == null || !key.isMultilingual()) return Collections.emptyList();
		return getAsMultiLingualMap(key).keySet();
	}

	@NotNull
	private <StoredType> Map<Locale, Object> getAsMultiLingualMap(Property<StoredType> key) {
		return asMap(entries.getOrDefault(key, Collections.emptyMap()));

	}

	@SuppressWarnings("unchecked")
	private Map<Locale, Object> asMap(Object object) {
		return (Map<Locale, Object>) object;
	}

	private <StoredType> StoredType getEntry(Property<StoredType> key, Locale language) {
		if (entries == null) return null;
		if (key.isMultilingual()) {
			return key.castToStoredValue(getAsMultiLingualMap(key).get(language));
		}
		else {
			return key.castToStoredValue(entries.get(key));
		}
	}

	@Override
	public boolean remove(Property<?> key) {
		keyMustNotBeNull(key);
		return remove(key, NO_LANGUAGE);
	}

	private void keyMustNotBeNull(Property<?> key) {
		Objects.requireNonNull(key, "The property must not be null.");
	}

	@Override
	public boolean remove(Property<?> key, Locale language) {
		keyMustNotBeNull(key);
		if (entries == null) return false;
		if (key.isMultilingual()) {
			Map<Locale, Object> localeObjectMap = asMap(entries.get(key));
			if (localeObjectMap == null) return false;
			boolean removed = localeObjectMap.remove(language) != null;
			if (removed && localeObjectMap.isEmpty()) entries.remove(key);
			return removed;
		}
		else {
			return entries.remove(key) != null;
		}
	}

	@Override
	public boolean contains(Property<?> key) {
		return contains(key, NO_LANGUAGE);
	}

	@Override
	public boolean contains(Property<?> key, Locale language) {
		keyMustNotBeNull(key);
		if (entries == null) return false;
		if (key.isMultilingual()) {
			return getAsMultiLingualMap(key).containsKey(language);
		}
		else {
			return entries.containsKey(key);
		}
	}

	@Override
	public <T> void addValue(Property<? super T> key, T value) {
		addValue(key, NO_LANGUAGE, value);
	}

	@Override
	public void addValue(Property<?> key, Locale language, Object value) {
		Objects.requireNonNull(value, "The value must not be null.");
		keyMustNotBeNull(key);
		if (!key.getStoredClass().isInstance(value)) {
			throw new ClassCastException("value '" + value + "' is not compatible with defined storage class "
					+ key.getStoredClass());
		}
		if (entries == null) {
			entries = new HashMap<>();
		}
		if (key.isMultilingual()) {
			asMap(entries.computeIfAbsent(key, k -> new HashMap<>(4))).put(language, value);
		}
		else if (language == NO_LANGUAGE) {
			entries.put(key, value);
		}
		else {
			throw new IllegalArgumentException("The property " + key + " does not support languages");
		}
	}

	@Override
	public boolean isEmpty() {
		return entries == null || entries.isEmpty();
	}
}
