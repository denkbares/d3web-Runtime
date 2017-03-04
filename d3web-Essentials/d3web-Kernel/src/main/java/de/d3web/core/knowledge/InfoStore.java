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

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.denkbares.strings.Locales;
import com.denkbares.utils.Triple;
import de.d3web.core.knowledge.terminology.info.Property;

public interface InfoStore {

	Locale NO_LANGUAGE = Locale.ROOT;

	/**
	 * Returns the value stored for the specified key and language.
	 * <p>
	 * If no language is given, we try to return the default value for the given property (same as language {@link
	 * #NO_LANGUAGE}). If this is not available, we check for any value with any language for the given property,
	 * before we continue to the default value of the property itself (independent of the InfoStore). If this is not
	 * defined, null is returned.
	 * <p>
	 * If one or more languages are given, we try to find the value with a language best matching the given and
	 * available languages, as described in {@link Locales#findBestLocale}. If there aren't any language specific
	 * values available in this store, again the default value for the given property is returned (same as language
	 * {@link #NO_LANGUAGE}). If there is also no default value available in this store, again the default value of the
	 * property itself is returned. If this is not defined, null is returned.
	 *
	 * @param key the property to be accessed
	 * @return the value for that key
	 * @see Property#getDefaultValue()
	 */
	<StoredType> StoredType getValue(Property<StoredType> key, Locale... language);

	/**
	 * Removes the stored item for the specified key and the default language {@link #NO_LANGUAGE}.
	 *
	 * @param key the key to be removed
	 * @return if there was such a property
	 */
	boolean remove(Property<?> key);

	/**
	 * Removes the stored item for the specified key and the specified language.
	 *
	 * @param key      the key to be removed
	 * @param language the language to be removed
	 * @return if there was such a property
	 */
	boolean remove(Property<?> key, Locale language);

	/**
	 * Check if there is a stored item for the specified key and the default language {@link
	 * #NO_LANGUAGE}.
	 *
	 * @param key the key to be removed
	 * @return if there is such a property
	 */
	boolean contains(Property<?> key);

	/**
	 * Check if there is a stored item for the specified key and the specified language.
	 *
	 * @param key      the key to be removed
	 * @param language the language to be removed
	 * @return if there is such a property
	 */
	boolean contains(Property<?> key, Locale language);

	/**
	 * Adds a value to this InfoStore for the specified property. If the value is not compatible to
	 * the properties storage class, an ClassCastException is thrown. If there is already a value
	 * for that property (and no language) is defined, it will be overwritten.
	 *
	 * @param key   the property to store the value for
	 * @param value the value to store
	 * @throws ClassCastException   if the value is not compatible with the property
	 * @throws NullPointerException if the key or value is null
	 * @created 28.10.2010
	 */
	<T> void addValue(Property<? super T> key, T value) throws ClassCastException;

	/**
	 * Adds a value to this InfoStore for the specified property. If the value is not compatible to
	 * the properties storage class, an ClassCastException is thrown. If there is already a value
	 * for that property and the specified language is defined, it will be overwritten.
	 *
	 * @param key      the property to store the value for
	 * @param language the language to store the property for
	 * @param value    the value to store
	 * @throws IllegalArgumentException if a language other than NO_LANGUAGE is specified for a non
	 *                                  multilingual property
	 * @throws ClassCastException       if the value is not compatible with the property
	 * @throws NullPointerException     if the key or value is null
	 * @created 28.10.2010
	 */
	void addValue(Property<?> key, Locale language, Object value) throws ClassCastException;

	/**
	 * Returns all entries of this store
	 *
	 * @return All entries
	 * @created 11.10.2010
	 */
	@NotNull Collection<Triple<Property<?>, Locale, Object>> entries();

	/**
	 * Returns all the entries that are available for the specified property, this info store has
	 * values for. The set may return the null locale as a key {@link #NO_LANGUAGE} if there is an
	 * entry without a language.
	 *
	 * @param key the property to get the entries for
	 * @return the entries of the specified property
	 */
	@NotNull <StoredType> Map<Locale, StoredType> entries(Property<StoredType> key);

	/**
	 * Returns true, if the collection is empty, false otherwise
	 *
	 * @return boolean
	 * @created 11.10.2010
	 */
	boolean isEmpty();
}
