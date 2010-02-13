package de.d3web.core;

import java.net.URL;
import java.util.Collection;
import java.util.Locale;

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

	public static final Locale DEFAULT_LANGUAGE = null;
	
	/**
	 * Returns the value stored for the specified key with language
	 * "LANGUAGE_DEFAULT". If there is no such key for
	 * the "LANGUAGE_DEFAULT", null is returned.
	 * 
	 * @param key
	 *            the key to be accessed
	 * @return the value for that key
	 */
	Object getValue(String key);

	/**
	 * Returns the value stored for the specified key with the specified
	 * language. If there is no such language, it is tried to access the key
	 * with language "LANGUAGE_DEFAULT". If there is no such item, null is
	 * returned.
	 * 
	 * @param key
	 *            the key to be accessed
	 * @param language
	 *            the language to be accessed
	 * @return the value stored for that key and language
	 */
	Object getValue(String key, Locale language);

	/**
	 * Removes the stored item for the specified key and the default language
	 * "LANGUAGE_DEFAULT".
	 * 
	 * @param key
	 *            the key to be removed
	 */
	void remove(String key);

	/**
	 * Removes the stored item for the specified key and the specified language.
	 * 
	 * @param key
	 *            the key to be removed
	 * @param language
	 *            the language to be removed
	 */
	void remove(String key, Locale language);

	/**
	 * Returns a unmodifiable collection of all entries contained. Each entry is
	 * represented by a triple of key, language and stored item.
	 * 
	 * @return the collection of stored items
	 */
	Collection<Triple<String, Locale, Object>> entries();

}
