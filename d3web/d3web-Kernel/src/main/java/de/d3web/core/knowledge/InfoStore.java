/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

import java.net.URL;
import java.util.Collection;
import java.util.Locale;

import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.utilities.Triple;

public interface InfoStore {

	/**
	 * Default key to store the title text of a terminology object. The type of
	 * the info item stored should be {@link String}.
	 */
	public static final String TITLE = "title";

	/**
	 * Default key to store the descriptive text of a terminology object. The
	 * type of the info item stored should be {@link String}.
	 */
	public static final String DESCRIPTION = "description";

	/**
	 * Default key to store a link of a terminology object. The type of the info
	 * item stored should be {@link URL}.
	 */
	public static final String LINK = "link";

	/**
	 * Default key to store the relative pathname of a knowledge base resource
	 * associated to a terminology object. The type of the info item stored
	 * should be {@link String}. You may use the knowledge base method
	 * {@link KnowledgeBase#getResource(String)} to access the associated
	 * resource.
	 */
	public static final String RESOURCE = "resource";

	public static final Locale NO_LANGUAGE = null;

	/**
	 * Returns the value stored for the specified key with language
	 * {@link #NO_LANGUAGE}. If there is no such key for the
	 * {@link #NO_LANGUAGE}, the key's default value is returned. If there is no
	 * such default value defined, null is returned.
	 * 
	 * @param key the property to be accessed
	 * @return the value for that key
	 * @see Property#getDefaultValue()
	 */
	<StoredType> StoredType getValue(Property<StoredType> key);

	/**
	 * Returns the value stored for the specified key with the specified
	 * language. If there is no such language, it is tried to access the key
	 * with language {@link #NO_LANGUAGE}. If there is no such item, the key's
	 * default value is returned. If there is no such default value defined,
	 * null is returned.
	 * 
	 * @param key the property to be accessed
	 * @param language the language to be accessed
	 * @return the value stored for that key and language
	 * @see Property#getDefaultValue()
	 */
	<StoredType> StoredType getValue(Property<StoredType> key, Locale language);

	/**
	 * Removes the stored item for the specified key and the default language
	 * {@link #NO_LANGUAGE}.
	 * 
	 * @param key the key to be removed
	 * @return if there was such a property
	 */
	boolean remove(Property<?> key);

	/**
	 * Removes the stored item for the specified key and the specified language.
	 * 
	 * @param key the key to be removed
	 * @param language the language to be removed
	 * @return if there was such a property
	 */
	boolean remove(Property<?> key, Locale language);

	/**
	 * Check if there is a stored item for the specified key and the default
	 * language {@link #NO_LANGUAGE}.
	 * 
	 * @param key the key to be removed
	 * @return if there is such a property
	 */
	boolean contains(Property<?> key);

	/**
	 * Check if there is a stored item for the specified key and the specified
	 * language.
	 * 
	 * @param key the key to be removed
	 * @param language the language to be removed
	 * @return if there is such a property
	 */
	boolean contains(Property<?> key, Locale language);

	/**
	 * Adds a value to this InfoStore for the specified property. If the value
	 * is not compatible to the properties storage class, an ClassCastException
	 * is thrown. If there is already a value for that property (and no
	 * language) is defined, it will be overwritten.
	 * 
	 * @created 28.10.2010
	 * @param key the property to store the value for
	 * @param value the value to store
	 * @throws ClassCastException if the value is not compatible with the
	 *                            property
	 * @throws NullPointerException if the key or value is null
	 */
	void addValue(Property<?> key, Object value) throws ClassCastException;

	/**
	 * Adds a value to this InfoStore for the specified property. If the value
	 * is not compatible to the properties storage class, an ClassCastException
	 * is thrown. If there is already a value for that property and the
	 * specified language is defined, it will be overwritten.
	 * 
	 * @created 28.10.2010
	 * @param key the property to store the value for
	 * @param language the language to store the property for
	 * @param value the value to store
	 * @throws ClassCastException if the value is not compatible with the
	 *                            property
	 * @throws NullPointerException if the key or value is null
	 */
	void addValue(Property<?> key, Locale language, Object value) throws ClassCastException;

	/**
	 * Returns all entries of this store
	 * 
	 * @created 11.10.2010
	 * @return All entries
	 */
	Collection<Triple<Property<?>, Locale, Object>> entries();

	/**
	 * Returns true, if the collection is empty, false otherwise
	 * 
	 * @created 11.10.2010
	 * @return boolean
	 */
	boolean isEmpty();

}
