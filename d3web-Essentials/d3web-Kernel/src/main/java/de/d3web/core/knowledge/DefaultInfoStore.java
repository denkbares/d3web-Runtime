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
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

import com.denkbares.strings.Locales;
import com.denkbares.utils.Triple;
import de.d3web.core.knowledge.terminology.info.Property;

import static java.util.Locale.ROOT;

public class DefaultInfoStore implements InfoStore {

	/**
	 * Comparator providing a stable, deterministic order for locales, independent of hash map iteration order and
	 * therefore independent of the JDK in use: German first, then English, then all other locales sorted by their
	 * language tag, and the ROOT locale always last.
	 */
	public static final Comparator<Locale> STABLE_LOCALE_ORDER =
			Comparator.comparingInt(DefaultInfoStore::localeRank).thenComparing(Locale::toLanguageTag);

	private static int localeRank(Locale locale) {
		if (ROOT.equals(locale)) return 3;
		String language = locale.getLanguage();
		if ("de".equals(language)) return 0;
		if ("en".equals(language)) return 1;
		return 2;
	}

	private static final Comparator<Entry<Property<?>, Object>> PROPERTY_NAME_ORDER =
			Comparator.comparing(entry -> entry.getKey().getName());

	private volatile Map<Property<?>, Object> entries = null;

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation returns the entries in a stable, deterministic order: sorted by the property name, and for
	 * multilingual properties additionally by {@link #STABLE_LOCALE_ORDER}.
	 */
	@Override
	@NotNull
	public Collection<Triple<Property<?>, Locale, Object>> entries() {
		if (entries == null) return Collections.emptyList();
		Collection<Triple<Property<?>, Locale, Object>> result = new ArrayList<>();
		List<Entry<Property<?>, Object>> sortedEntries = new ArrayList<>(this.entries.entrySet());
		sortedEntries.sort(PROPERTY_NAME_ORDER);
		for (Entry<Property<?>, Object> entry : sortedEntries) {
			if (entry.getKey().isMultilingual()) {
				for (Entry<Locale, Object> localeEntry : sortedEntriesByLocale(asMap(entry.getValue()))) {
					result.add(new Triple<>(entry.getKey(), localeEntry.getKey(), localeEntry.getValue()));
				}
			}
			else {
				result.add(new Triple<>(entry.getKey(), ROOT, entry.getValue()));
			}
		}
		return Collections.unmodifiableCollection(result);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation returns the map with a stable, deterministic iteration order as defined by
	 * {@link #STABLE_LOCALE_ORDER}.
	 */
	@Override
	@NotNull
	public <StoredType> Map<Locale, StoredType> entries(Property<StoredType> key) {
		keyMustNotBeNull(key);
		if (entries == null) return Collections.emptyMap();
		if (key.isMultilingual()) {
			//noinspection unchecked
			return (Map<Locale, StoredType>) getAsMultiLingualMap(key);
		}
		else {
			StoredType value = key.castToStoredValue(entries.get(key));
			if (value != null) {
				return Collections.singletonMap(ROOT, value);
			}
		}
		return Collections.emptyMap();
	}

	@Override
	public <StoredType> StoredType getValue(Property<StoredType> key, Locale... language) {
		keyMustNotBeNull(key);
		// fast check for no language at all
		if (language.length == 0) {
			StoredType value = getEntry(key, ROOT);
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
			// if not, for backward compatibility, handle if the language is null
			if (language[0] == null) language = new Locale[] { ROOT };
		}

		// ok, lets see what we have and return best match
		List<Locale> allAvailableLocales = getAvailableLocales(key);
		Locale bestLocale = Locales.findBestLocale(Arrays.asList(language), allAvailableLocales);
		StoredType value = getEntry(key, bestLocale);
		if (value != null) {
			return value;
		}

		// if nothing else available use default value or null
		return key.getDefaultValue();
	}

	/**
	 * Returns the locales available for the specified property, sorted by {@link #STABLE_LOCALE_ORDER} to be
	 * deterministic and independent of hash map iteration order.
	 */
	@NotNull
	private List<Locale> getAvailableLocales(Property<?> key) {
		if (entries == null || !key.isMultilingual()) return Collections.emptyList();
		Map<Locale, Object> raw = getRawMultiLingualMap(key);
		if (raw.isEmpty()) return Collections.emptyList();
		List<Locale> locales = new ArrayList<>(raw.keySet());
		if (locales.size() > 1) locales.sort(STABLE_LOCALE_ORDER);
		return locales;
	}

	/**
	 * Returns an unmodifiable copy of the language-to-value map of the specified property, with a stable iteration
	 * order as defined by {@link #STABLE_LOCALE_ORDER}. Empty and singleton maps are returned as shared immutable
	 * instances, avoiding any sorting and copying overhead for these common cases.
	 */
	@NotNull
	private Map<Locale, Object> getAsMultiLingualMap(Property<?> key) {
		Map<Locale, Object> raw = getRawMultiLingualMap(key);
		int size = raw.size();
		if (size == 0) return Collections.emptyMap();
		if (size == 1) {
			Iterator<Entry<Locale, Object>> iterator = raw.entrySet().iterator();
			// hasNext check in case of concurrent removal of the single entry
			if (!iterator.hasNext()) return Collections.emptyMap();
			Entry<Locale, Object> entry = iterator.next();
			return Collections.singletonMap(entry.getKey(), entry.getValue());
		}
		Map<Locale, Object> sorted = new LinkedHashMap<>((int) Math.ceil(size / 0.75));
		for (Entry<Locale, Object> entry : sortedEntriesByLocale(raw)) {
			sorted.put(entry.getKey(), entry.getValue());
		}
		return Collections.unmodifiableMap(sorted);
	}

	@NotNull
	private Map<Locale, Object> getRawMultiLingualMap(Property<?> key) {
		return asMap(entries.getOrDefault(key, Collections.emptyMap()));
	}

	/**
	 * Returns the entries of the given map, sorted by {@link #STABLE_LOCALE_ORDER}. Maps with less than two entries
	 * are returned as their plain entry set, without sorting or copying.
	 */
	@NotNull
	private static Collection<Entry<Locale, Object>> sortedEntriesByLocale(Map<Locale, Object> raw) {
		if (raw.size() <= 1) return raw.entrySet();
		List<Entry<Locale, Object>> sortedEntries = new ArrayList<>(raw.entrySet());
		sortedEntries.sort(Entry.comparingByKey(STABLE_LOCALE_ORDER));
		return sortedEntries;
	}

	@SuppressWarnings("unchecked")
	private Map<Locale, Object> asMap(Object object) {
		return (Map<Locale, Object>) object;
	}

	private <StoredType> StoredType getEntry(Property<StoredType> key, Locale language) {
		if (entries == null) return null;
		if (key.isMultilingual()) {
			if (language == null) language = ROOT;
			return key.castToStoredValue(getRawMultiLingualMap(key).get(language));
		}
		else {
			return key.castToStoredValue(entries.get(key));
		}
	}

	@Override
	public boolean remove(Property<?> key) {
		keyMustNotBeNull(key);
		if (entries == null) return false;
		return entries.remove(key) != null;
	}

	private void keyMustNotBeNull(Property<?> key) {
		Objects.requireNonNull(key, "The property must not be null.");
	}

	@Override
	public boolean remove(Property<?> key, Locale language) {
		keyMustNotBeNull(key);
		if (entries == null) return false;
		if (key.isMultilingual()) {
			if (language == null) language = ROOT;
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
		keyMustNotBeNull(key);
		if (entries == null) return false;
		return entries.containsKey(key);
	}

	@Override
	public boolean contains(Property<?> key, Locale language) {
		keyMustNotBeNull(key);
		if (entries == null) return false;
		if (key.isMultilingual()) {
			if (language == null) language = ROOT;
			return getRawMultiLingualMap(key).containsKey(language);
		}
		else {
			return entries.containsKey(key);
		}
	}

	@Override
	public <T> void addValue(Property<? super T> key, T value) {
		addValue(key, ROOT, value);
	}

	@Override
	public void addValue(Property<?> key, Locale language, Object value) {
		keyMustNotBeNull(key);
		Objects.requireNonNull(value, "The value must not be null.");
		if (!key.getStoredClass().isInstance(value)) {
			throw new ClassCastException("value '" + value + "' is not compatible with defined storage class "
					+ key.getStoredClass());
		}
		if (entries == null) {
			synchronized (this) {
				if (entries == null) {
					entries = new ConcurrentHashMap<>();
				}
			}
		}
		if (key.isMultilingual()) {
			if (language == null) language = ROOT;
			asMap(entries.computeIfAbsent(key, k -> new ConcurrentHashMap<>(4))).put(language, value);
		}
		else if (Locales.isEmpty(language)) {
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

	@Override
	public String toString() {
		return "DefaultInfoStore" + entries;
	}
}
