/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.d3web.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.strings.LowerCaseNormalizer;
import de.d3web.strings.TokenNormalizer;
import de.d3web.strings.Tokenizer;

/**
 * Class that represents an inverted index. It stores elements of a specified generic type E by text
 * phrases. the text phrases are tokenized. It allows to retrieve the stored elements by other text
 * phrases that shares one / multiple tokens.
 *
 * @param <E>
 */
public class InvertedIndex<E> {

	private final Map<String, Set<E>> index = new HashMap<String, Set<E>>();
	private final TokenNormalizer normalizer;

	/**
	 * Creates a new case insensitive inverted index
	 */
	public InvertedIndex() {
		this(new LowerCaseNormalizer());
	}

	/**
	 * Creates a new inverted index that uses the specified token normalizer to unify the tokens to
	 * be compared. E.g. if you specify some stemming method, the inverted index will work in
	 * stemmed tokens instead of case-insensitive matching of identical tokens.
	 *
	 * @param normalizer the token normalizer to be used
	 */
	public InvertedIndex(TokenNormalizer normalizer) {
		this.normalizer = normalizer;
	}

	/**
	 * Adds the specified element to all tokens that can be extracted in the specified text. Returns
	 * true if the index has been changed. If false is returned, the index remains unchanged,
	 * because the specified combinations has already been added.
	 *
	 * @param text the text to extract the tokens to add the element to
	 * @param element the element to be added
	 * @return if this index has been changed
	 * @created 06.11.2013
	 */
	public boolean put(String text, E element) {
		return addElement(Tokenizer.tokenize(text), element);
	}

	/**
	 * Removes the specified element from all tokens that can be extracted in the specified text.
	 * Returns true if the index has been changed. If false is returned, the index remains
	 * unchanged, because not any of the specified combinations did exist.
	 *
	 * @param text the text to extract the tokens to remove the element from
	 * @param element the element to be removed
	 * @return if this index has been changed
	 * @created 06.11.2013
	 */
	public boolean remove(String text, E element) {
		return removeElement(Tokenizer.tokenize(text), element);
	}

	/**
	 * Adds the specified element to all the specified tokens' texts. Returns true if the index has
	 * been changed. If false is returned, the index remains unchanged, because the specified
	 * combinations has already been added.
	 *
	 * @param tokens the tokens to add the element to
	 * @param element the element to be added
	 * @return if this index has been changed
	 * @created 06.11.2013
	 */
	private boolean addElement(Collection<String> tokens, E element) {
		boolean changed = false;
		for (String token : tokens) {
			changed |= addElement(token, element);
		}
		return changed;
	}

	/**
	 * Adds the specified element to the specified token's text. Returns true if the index has been
	 * changed. If false is returned, the index remains unchanged, because the specified combination
	 * has already been added.
	 *
	 * @param token the token to add the element to
	 * @param element the element to be added
	 * @return if this index has been changed
	 * @created 06.11.2013
	 */
	private boolean addElement(String token, E element) {
		String key = normalizer.normalize(token);
		Set<E> set = index.get(key);
		if (set == null) {
			set = new HashSet<E>();
			index.put(key, set);
		}
		return set.add(element);
	}

	/**
	 * Removes the specified element from all specified tokens. Returns true if the index has been
	 * changed. If false is returned, the index remains unchanged, because not any of the specified
	 * combinations did exist.
	 *
	 * @param tokens the tokens to remove the element from
	 * @param element the element to be removed
	 * @return if this index has been changed
	 * @created 06.11.2013
	 */
	private boolean removeElement(Collection<String> tokens, E element) {
		boolean changed = false;
		for (String token : tokens) {
			changed |= removeElement(token, element);
		}
		return changed;
	}

	/**
	 * Removes the specified element from the specified token. Returns true if the index has been
	 * changed. If false is returned, the index remains unchanged, because not any of the specified
	 * combinations did exist.
	 *
	 * @param token the token to remove the element from
	 * @param element the element to be removed
	 * @return if this index has been changed
	 * @created 06.11.2013
	 */
	private boolean removeElement(String token, E element) {
		String key = normalizer.normalize(token);
		Set<E> set = index.get(key);

		// if there is no such set, we no not require to change the index
		if (set == null) return false;

		// if the element if not found, we do not require to change the index
		if (!set.remove(element)) return false;

		// otherwise we changed the set, so we clean up if the set is empty
		if (set.isEmpty()) {
			index.remove(key);
		}
		return true;
	}

	/**
	 * Returns all elements that are associated to any of the tokens of the specified phrase. If
	 * there are no tokens in the specified phrase, an empty set is returned.
	 *
	 * @param phrase the phrase to get the elements for
	 * @return the elements associated to any of the phrase's tokens
	 * @created 06.11.2013
	 */
	public Set<E> getAny(String phrase) {
		List<String> tokens = Tokenizer.tokenize(phrase);
		if (tokens.size() == 0) return Collections.emptySet();
		if (tokens.size() == 1) return get(tokens.get(0));
		Set<E> result = new HashSet<E>();
		for (String token : tokens) {
			result.addAll(get(token));
		}
		return Collections.unmodifiableSet(result);
	}

	/**
	 * Returns all elements that are associated to all of the tokens of the specified phrase. If
	 * there are no tokens in the specified phrase, an empty set is returned.
	 *
	 * @param phrase the phrase to get the elements for
	 * @return the elements associated to the all the phrase's tokens
	 * @created 06.11.2013
	 */
	public Set<E> getAll(String phrase) {
		List<String> tokens = Tokenizer.tokenize(phrase);
		if (tokens.size() == 0) return Collections.emptySet();
		if (tokens.size() == 1) return get(tokens.get(0));
		Set<E> result = null;
		for (String token : tokens) {
			Set<E> elements = get(token);
			if (result == null) {
				result = new HashSet<E>(elements);
			}
			else {
				result.retainAll(elements);
			}
			if (result.isEmpty()) break;
		}
		return (result == null) ? Collections.<E>emptySet() : Collections.unmodifiableSet(result);
	}

	/**
	 * Returns all elements that are associated to the specified token. If there are no such
	 * elements, an empty set is returned.
	 *
	 * @param token the token to get the elements for
	 * @return the elements associated to the token
	 * @created 06.11.2013
	 */
	private Set<E> get(String token) {
		String key = normalizer.normalize(token);
		Set<E> result = index.get(key);
		return (result == null)
				? Collections.<E>emptySet()
				: Collections.unmodifiableSet(result);
	}

	/**
	 * Checks if the specified notation is available in similar form starting at the specified start
	 * index in the specified text. If the notation can be matched, the index of the text is
	 * returned, after the notation has ended;
	 *
	 * @param text the text to match the notation in
	 * @param start the start index to search for the notation
	 * @param notation the notation to match
	 * @return the index after the match ends, or -1 if not matched
	 * @created 09.11.2013
	 */
	public static int matches(String text, int start, String notation) {
		int ti = start, tl = text.length();
		int ni = 0, nl = notation.length();
		char nc = '\0', tc = '\0';
		while (true) {
			// ignore non-word-characters in notation
			do {
				// if notation index reaches the end, the notation is found
				if (ni == nl) {
					// check if text has also reached end of word
					if (ti >= tl || !Tokenizer.isWordChar(text.charAt(ti))) {
						// then we matched it
						return ti;
					}
					else {
						// otherwise we only looking for a substring -> no match
						return -1;
					}
				}
				nc = notation.charAt(ni++);
			} while (!Tokenizer.isWordChar(nc));

			// ignore non-word-characters in text
			do {
				// if text index reached the end, the notation is too long
				if (ti == tl) {
					return -1;
				}
				tc = text.charAt(ti++);
			} while (!Tokenizer.isWordChar(tc));

			// otherwise consume the next char that is required to be equal
			if (Character.toLowerCase(nc) != Character.toLowerCase(tc)) {
				return -1;
			}
		}
	}

}
