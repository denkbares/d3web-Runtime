/*
 * Copyright (C) 2014 denkbares GmbH
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

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 
 * @author Volker Belli (denkbares GmbH)
 * @created 09.01.2014
 */
public class MultiMaps {

	/**
	 * Interface to provide factories to create the individual collection
	 * instances used by the MultiMap implementations to collect the keys and/or
	 * values.
	 * 
	 * @author Volker Belli (denkbares GmbH)
	 * @created 09.01.2014
	 * @param <T>
	 */
	public static interface CollectionFactory<T> {

		Set<T> createSet();

		<E> Map<T, E> createMap();
	}

	@SuppressWarnings("rawtypes")
	private static final CollectionFactory HASH = new CollectionFactory() {

		@Override
		public Set createSet() {
			return new HashSet();
		}

		@Override
		public Map createMap() {
			return new HashMap();
		}
	};

	@SuppressWarnings("rawtypes")
	private static final CollectionFactory LINKED = new CollectionFactory() {

		@Override
		public Set createSet() {
			return new LinkedHashSet();
		}

		@Override
		public Map createMap() {
			return new LinkedHashMap();
		}
	};

	@SuppressWarnings("rawtypes")
	private static final CollectionFactory TREE = new CollectionFactory() {

		@Override
		public Set createSet() {
			return new TreeSet();
		}

		@Override
		public Map createMap() {
			return new TreeMap();
		}
	};

	/**
	 * Returns a collection factory for hashing the entries, using
	 * {@link T#hashCode()} and {@link T#equals(Object)} method.
	 * 
	 * @created 09.01.2014
	 * @return the collection factory
	 */
	@SuppressWarnings("unchecked")
	public static final <T> CollectionFactory<T> hashFactory() {
		return (CollectionFactory<T>) HASH;
	}

/**
	 * Returns a collection factory for handling the entries as a tree, using
	 * {@link T#compareTo(Object)) method.
	 * 
	 * @created 09.01.2014
	 * @return the collection factory
	 */
	@SuppressWarnings("unchecked")
	public static final <T extends Comparable<? super T>> CollectionFactory<T> treeFactory() {
		return (CollectionFactory<T>) TREE;
	}

	/**
	 * Returns a collection factory for hashing the entries in linked sets/maps,
	 * using {@link T#hashCode()} and {@link T#equals(Object)} method. The order
	 * of the contained objects will remain stable.
	 * 
	 * @created 09.01.2014
	 * @return the collection factory
	 */
	@SuppressWarnings("unchecked")
	public static final <T> CollectionFactory<T> linkedFactory() {
		return (CollectionFactory<T>) LINKED;
	}

	public static <K, V> Map<K, Set<V>> asMap(final MultiMap<K, V> map) {
		return new AbstractMap<K, Set<V>>() {

			@Override
			public Set<Entry<K, Set<V>>> entrySet() {

				return new AbstractSet<Entry<K, Set<V>>>() {

					@Override
					public Iterator<Entry<K, Set<V>>> iterator() {
						final Iterator<K> keyIter = map.keySet().iterator();
						return new Iterator<Entry<K, Set<V>>>() {

							@Override
							public boolean hasNext() {
								return keyIter.hasNext();
							}

							@Override
							public Entry<K, Set<V>> next() {
								K key = keyIter.next();
								Set<V> values = map.getValues(key);
								return new SimpleImmutableEntry<K, Set<V>>(key, values);
							}

							@Override
							public void remove() {
								throw new UnsupportedOperationException();
							}
						};
					}

					@Override
					public int size() {
						return map.keySet().size();
					}
				};
			}
		};
	}

}
