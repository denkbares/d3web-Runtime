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
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import de.d3web.collections.FlattingIterator.IteratorFactory;
import de.d3web.strings.Strings;

/**
 * Implementation of generalized suffix tree that is capable to take any element
 * types to be stored for any strings (String --> E). It is implemented like an
 * MultiMap and allows to store multiple objects for the same strings. It also
 * allows to receive the whole set of elements added that matches a specified
 * substring.
 * <p/>
 * In contrast to existing generalized suffix trees, our implementation allows
 * to easily remove key-value-pairs from the tree, keeping track if the
 * specified combination has been inserted or not.
 * <p/>
 * The implementation is based of a DefaultMultiMap mapping each key to the
 * values. Additionally for each key there is a tree-map of all suffixes to the
 * keys.
 * 
 * @author Volker Belli (denkbares GmbH)
 * @created 13.03.2014
 */
public class GeneralizedSuffixTree<E> extends DefaultMultiMap<String, E> {

	/**
	 * Here we store for each suffix all the key strings that have been used to
	 * generate the suffix. This map uses a tree-map to easily allow to access
	 * for any suffix all suffixes that are stored, because they are a sub-map
	 * of the tree-map. Using this, searching an infix is looking for the node
	 * in the suffix-tree, getting a sub-map of all suffixes starting with the
	 * infix and for them get the values that have been added.
	 * <p/>
	 * We use the tree-set also for the values, so we get a natural ordering of
	 * the keys.
	 */
	private final DefaultMultiMap<String, String> suffixTree = new DefaultMultiMap<String, String>(
			MultiMaps.<String> treeFactory(), MultiMaps.<String> treeFactory());

	@Override
	public boolean put(String key, E value) {
		boolean isNewKey = !containsKey(key);
		boolean added = super.put(key, value);
		if (isNewKey) {
			addSuffixes(key);
		}
		return added;
	}

	@Override
	public Set<E> removeKey(Object key) {
		Set<E> values = super.removeKey(key);
		removeSuffixes(key);
		return values;
	}

	@Override
	public Set<String> removeValue(Object value) {
		Set<String> keys = super.removeValue(value);
		for (String key : keys) {
			if (!containsKey(key)) {
				removeSuffixes(key);
			}
		}
		return keys;
	}

	@Override
	public boolean remove(Object key, Object value) {
		boolean removed = super.remove(key, value);
		boolean isKeyGone = removed && !containsKey(key);
		if (isKeyGone) {
			removeSuffixes(key);
		}
		return removed;
	}

	@Override
	public void clear() {
		super.clear();
		suffixTree.clear();
	}

	public Iterable<E> search(String phrase) {
		if (Strings.isBlank(phrase)) return Collections.emptyList();
		phrase = phrase.toLowerCase();
		final String[] infixes = phrase.split("\\s+");

		// use the first infix to search for the items
		// and all other ones to filter the found matches
		final Iterable<String> keys = findKeys(infixes[0]);
		return new Iterable<E>() {

			@Override
			public Iterator<E> iterator() {
				return new FlattingIterator<E>(keys, new IteratorFactory<String, E>() {

					@Override
					public Iterator<E> create(String key) {
						// filter all keys that are not matching all other
						// infixes
						for (int i = 1; i < infixes.length; i++) {
							if (!Strings.containsIgnoreCase(key, infixes[i])) {
								return Collections.<E> emptyList().iterator();
							}
						}
						// filter duplicate items from the values
						return new FilterDuplicateIterator<E>(getValues(key).iterator());
					}
				});
			}
		};
	}

	private Iterable<String> findKeys(String infix) {
		final NavigableMap<String, Set<String>> range = getSuffixSubTree(infix);
		if (range == null) return Collections.emptyList();
		final Collection<Set<String>> keysOfSuffixes = range.values();
		return new Iterable<String>() {

			@Override
			public Iterator<String> iterator() {
				return new FlattingIterator<String>(keysOfSuffixes);
			}
		};
	}

	private NavigableMap<String, Set<String>> getSuffixSubTree(String infix) {
		// get sub-map of prefixes that start with the infix
		TreeMap<String, Set<String>> tree = (TreeMap<String, Set<String>>) suffixTree.k2v;
		// get the first suffix that starts with infix
		String start = tree.ceilingKey(infix);
		if (start == null) return null;
		// get the last suffix that starts with infix
		// (we accept that the following will only work well if \uffff is not
		// used)
		String end = tree.floorKey(infix + '\uffff');
		if (end == null) return null;
		if (start.compareTo(end) > 0) return null;
		return tree.subMap(start, true, end, true);
	}

	private void addSuffixes(String key) {
		for (String suffix : suffixes(key)) {
			suffixTree.put(suffix, key);
		}
	}

	private void removeSuffixes(Object key) {
		if (key instanceof String) {
			for (String suffix : suffixes((String) key)) {
				suffixTree.remove(suffix, key);
			}
		}
	}

	/**
	 * Creates all suffixes for a given phrase. The phrase is splitted by
	 * whitespaces and each resulting token is indexed separately.
	 * 
	 * @param phrase the phrase to get the suffixes for
	 * @return all suffixes of all contained words
	 */
	private Set<String> suffixes(String phrase) {
		phrase = phrase.toLowerCase();
		Set<String> result = new HashSet<String>();
		for (String word : phrase.split("\\s+")) {
			for (int i = 0; i < word.length(); i++) {
				result.add(word.substring(i));
			}
		}
		return result;
	}

}
