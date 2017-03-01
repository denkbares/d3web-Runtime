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

	private Map<Property<?>, Map<Locale, Object>> multiLangEntries;
	private Map<Property<?>, Object> singleLangEntries;

	@Override
	@NotNull
	public Collection<Triple<Property<?>, Locale, Object>> entries() {
		Collection<Triple<Property<?>, Locale, Object>> result = new ArrayList<>();
		if (multiLangEntries != null) {
			for (Entry<Property<?>, Map<Locale, Object>> entry : this.multiLangEntries.entrySet()) {
				for (Entry<Locale, Object> localeEntry : entry.getValue().entrySet()) {
					result.add(new Triple<>(entry.getKey(), localeEntry.getKey(), localeEntry.getValue()));
				}
			}
		}
		if (singleLangEntries != null) {
			for (Entry<Property<?>, Object> entry : this.singleLangEntries.entrySet()) {
				result.add(new Triple<>(entry.getKey(), null, entry.getValue()));
			}
		}
		return Collections.unmodifiableCollection(result);
	}

	@Override
	@NotNull
	public <StoredType> Map<Locale, StoredType> entries(Property<StoredType> key) {
		if (key == null) {
			throw new NullPointerException(KEY_MUST_NOT_BE_NULL);
		}
		if (key.isMultilingual() && multiLangEntries != null) {
			//noinspection unchecked
			return Collections.unmodifiableMap((Map<Locale, StoredType>) multiLangEntries.getOrDefault(key, Collections.emptyMap()));
		}
		else if (!key.isMultilingual() && singleLangEntries != null) {
			HashMap<Locale, StoredType> temp = new HashMap<>(4);
			//noinspection unchecked
			StoredType value = (StoredType) singleLangEntries.get(key);
			if (value != null) {
				temp.put(null, value);
			}
			return Collections.unmodifiableMap(temp);
		}
		return Collections.emptyMap();
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
		return multiLangEntries == null ?
				Collections.emptyList() : multiLangEntries.getOrDefault(key, Collections.emptyMap()).keySet();
	}

	private <StoredType> StoredType getEntry(Property<StoredType> key, Locale language) {
		if (key.isMultilingual() && multiLangEntries != null) {
			Map<Locale, Object> localeObjectMap = this.multiLangEntries.get(key);
			if (localeObjectMap == null) return null;
			return key.getStoredClass().cast(localeObjectMap.get(language));
		}
		else if (!key.isMultilingual() && singleLangEntries != null) {
			return key.getStoredClass().cast(singleLangEntries.get(key));
		}
		return null;
	}

	@Override
	public boolean remove(Property<?> key) {
		if (key == null) throw new NullPointerException(KEY_MUST_NOT_BE_NULL);
		return remove(key, NO_LANGUAGE);
	}

	@Override
	public boolean remove(Property<?> key, Locale language) {
		if (key == null) throw new NullPointerException(KEY_MUST_NOT_BE_NULL);
		if (key.isMultilingual() && multiLangEntries != null) {
			Map<Locale, Object> localeObjectMap = multiLangEntries.get(key);
			if (localeObjectMap == null) return false;
			boolean removed = localeObjectMap.remove(language) != null;
			if (removed && localeObjectMap.isEmpty()) multiLangEntries.remove(key);
			return removed;
		}
		else if (!key.isMultilingual() && singleLangEntries != null) {
			return singleLangEntries.remove(key) != null;
		}
		return false;
	}

	@Override
	public boolean contains(Property<?> key) {
		return contains(key, NO_LANGUAGE);
	}

	@Override
	public boolean contains(Property<?> key, Locale language) {
		if (key == null) throw new NullPointerException(KEY_MUST_NOT_BE_NULL);
		if (key.isMultilingual() && multiLangEntries != null) {
			Map<Locale, Object> localeObjectMap = this.multiLangEntries.get(key);
			return localeObjectMap != null && localeObjectMap.containsKey(language);
		}
		else if (!key.isMultilingual() && singleLangEntries != null) {
			return singleLangEntries.containsKey(key);
		}
		return false;
	}

	@Override
	public <T> void addValue(Property<? super T> key, T value) {
		addValue(key, NO_LANGUAGE, value);
	}

	@Override
	public void addValue(Property<?> key, Locale language, Object value) {
		if (value == null) throw new NullPointerException("The value must not be null.");
		if (key == null) throw new NullPointerException(KEY_MUST_NOT_BE_NULL);
		if (!key.getStoredClass().isInstance(value)) {
			throw new ClassCastException("value '" + value + "' is not compatible with defined storage class "
					+ key.getStoredClass());
		}
		if (key.isMultilingual()) {
			if (multiLangEntries == null) {
				multiLangEntries = new HashMap<>(4);
			}
			Map<Locale, Object> localeObjectMap = multiLangEntries.computeIfAbsent(key, k -> new HashMap<>(4));
			localeObjectMap.put(language, value);
		}
		else if (language == NO_LANGUAGE) {
			if (singleLangEntries == null) {
				singleLangEntries = new HashMap<>();
			}
			singleLangEntries.put(key, value);
		}
		else {
			throw new IllegalArgumentException("The property " + key + " does not support languages");
		}
	}

	@Override
	public boolean isEmpty() {
		return (singleLangEntries == null || singleLangEntries.isEmpty())
				&& (multiLangEntries == null || multiLangEntries.isEmpty());
	}
}
