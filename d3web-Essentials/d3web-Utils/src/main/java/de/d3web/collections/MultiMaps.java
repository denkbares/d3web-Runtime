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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import de.d3web.utils.EqualsUtils;

/**
 * Utility class to provide useful methods for implementing and/or using MultiMaps.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 09.01.2014
 */
public class MultiMaps {

	/**
	 * Interface to provide factories to create the individual collection instances used by the
	 * MultiMap implementations to collect the keys and/or values.
	 *
	 * @param <T> the elements to have the collection factory for
	 * @author Volker Belli (denkbares GmbH)
	 * @created 09.01.2014
	 */
	public interface CollectionFactory<T> {

		/**
		 * Creates a new set used for storing the elements.
		 *
		 * @return a newly created set
		 * @created 09.01.2014
		 */
		Set<T> createSet();

		/**
		 * Creates a new map used for storing objects by keys of this class' elements.
		 *
		 * @return a newly created map
		 * @created 09.01.2014
		 */
		<E> Map<T, E> createMap();
	}

	private static final class TreeFactory<T> implements CollectionFactory<T> {

		private Comparator<T> comparator;

		public TreeFactory() {
			this(null);
		}

		public TreeFactory(Comparator<T> comparator) {
			this.comparator = comparator;
		}

		@Override
		public Set<T> createSet() {
			return new TreeSet<>(comparator);
		}

		@Override
		public <E> Map<T, E> createMap() {
			return new TreeMap<>(comparator);
		}
	}

	private static final class LinkedHashFactory<T> implements CollectionFactory<T> {

		@Override
		public Set<T> createSet() {
			return new LinkedHashSet<>();
		}

		@Override
		public <E> Map<T, E> createMap() {
			return new LinkedHashMap<>();
		}
	}

	private static final class HashFactory<T> implements CollectionFactory<T> {

		private int capacity;

		public HashFactory(int capacity) {
			this.capacity = capacity;
		}

		@Override
		public Set<T> createSet() {
			return new HashSet<>(capacity);
		}

		@Override
		public <E> Map<T, E> createMap() {
			return new HashMap<>();
		}
	}

	private static final class MinimizedHashFactory<T> implements CollectionFactory<T> {

		@Override
		public Set<T> createSet() {
			return new MinimizedHashSet<>();
		}

		@Override
		public <E> Map<T, E> createMap() {
			return new HashMap<>();
		}
	}

	/**
	 * HashSet memory optimized for cases where you have a lot of them but most of the time with only one element.
	 */
	static class MinimizedHashSet<T> extends AbstractSet<T> {

		private static Object EMPTY = new Object();
		private Object element = EMPTY;
		private HashSet<T> backUpSet = null;

		@Override
		public int size() {
			if (backUpSet != null) return backUpSet.size();
			if (element != EMPTY) return 1;
			return 0;
		}

		@Override
		public boolean contains(Object o) {
			if (backUpSet != null) return backUpSet.contains(o);
			return element != EMPTY && EqualsUtils.equals(element, o);
		}

		@Override
		public Iterator<T> iterator() {
			return new Iterator<T>() {

				private Object current = EMPTY;
				boolean removable = false;
				private Iterator<T> backupIterator = backUpSet == null ? null : backUpSet.iterator();

				@Override
				public boolean hasNext() {
					return (backupIterator != null && backupIterator.hasNext()) || (element != EMPTY && current != element);
				}

				@SuppressWarnings("unchecked")
				@Override
				public T next() {
					removable = true;
					if (backupIterator != null && backupIterator.hasNext()) {
						current = backupIterator.next();
						return (T) current;
					}
					if (current != element) {
						current = element;
						return (T) current;
					}
					removable = false;
					throw new NoSuchElementException();
				}

				@Override
				public void remove() {
					if (!removable) throw new IllegalStateException();
					removable = false;
					if (current == element && element != EMPTY) {
						element = EMPTY;
						return;
					}
					if (backupIterator != null) backupIterator.remove();
					if (backUpSet.size() == 1) {
						element = backupIterator.next();
						backUpSet = null;
					}
				}
			};
		}

		@Override
		public boolean add(T t) {
			if (backUpSet != null) return backUpSet.add(t);
			if (element != EMPTY && EqualsUtils.equals(element, t)) {
				return false;
			}
			if (element != EMPTY && !EqualsUtils.equals(element, t)) {
				backUpSet = new HashSet<>(4);
				//noinspection unchecked
				backUpSet.add((T) element);
				backUpSet.add(t);
				element = EMPTY;
				return true;
			}
			element = t;
			return true;
		}

		@Override
		public boolean remove(Object o) {
			if (backUpSet != null) {
				boolean remove = backUpSet.remove(o);
				if (backUpSet.size() == 1) {
					element = backUpSet.iterator().next();
					backUpSet = null;
				}
				return remove;
			}
			if (element != EMPTY && EqualsUtils.equals(element, o)) {
				element = EMPTY;
				return true;
			}
			return false;
		}

		@Override
		public void clear() {
			backUpSet = null;
			element = EMPTY;
		}
	}

	@SuppressWarnings("rawtypes")
	private static final CollectionFactory HASH = new HashFactory(16);

	@SuppressWarnings("rawtypes")
	private static final CollectionFactory HASH_MINIMIZED = new MinimizedHashFactory();

	@SuppressWarnings("rawtypes")
	private static final CollectionFactory LINKED = new LinkedHashFactory();

	@SuppressWarnings("rawtypes")
	private static final CollectionFactory TREE = new TreeFactory();

	/**
	 * Returns a collection factory for hashing the entries, using {@link T#hashCode()} and {@link
	 * T#equals(Object)} method.
	 *
	 * @return the collection factory
	 * @created 09.01.2014
	 */
	@SuppressWarnings("unchecked")
	public static <T> CollectionFactory<T> hashFactory() {
		return (CollectionFactory<T>) HASH;
	}

	/**
	 * Returns a collection factory for hashing the entries, using {@link T#hashCode()} and {@link
	 * T#equals(Object)} method. The initial hash tables to be used are kept as minimized as
	 * possible.
	 *
	 * @return the collection factory
	 * @created 09.01.2014
	 */
	@SuppressWarnings("unchecked")
	public static <T> CollectionFactory<T> hashMinimizedFactory() {
		return (CollectionFactory<T>) HASH_MINIMIZED;
	}

	/**
	 * Returns a collection factory for handling the entries as a tree, using {@link
	 * T#compareTo(Object)) method.
	 *
	 * @return the collection factory
	 * @created 09.01.2014
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Comparable<? super T>> CollectionFactory<T> treeFactory() {
		return (CollectionFactory<T>) TREE;
	}

	/**
	 * Returns a collection factory for handling the entries as a tree, using {@link Comparator} to sort.
	 *
	 * @return the collection factory
	 * @created 09.01.2014
	 */
	@SuppressWarnings("unchecked")
	public static <T> CollectionFactory<T> treeFactory(Comparator<T> comparator) {
		return new TreeFactory<>(comparator);
	}

	/**
	 * Returns a collection factory for hashing the entries in linked sets/maps, using {@link
	 * T#hashCode()} and {@link T#equals(Object)} method. The order of the contained objects will
	 * remain stable.
	 *
	 * @return the collection factory
	 * @created 09.01.2014
	 */
	@SuppressWarnings("unchecked")
	public static <T> CollectionFactory<T> linkedFactory() {
		return (CollectionFactory<T>) LINKED;
	}

	public static <K, V> MultiMap<K, V> synchronizedMultiMap(MultiMap<K, V> map) {
		return new SynchronizedMultiMap<>(map);
	}

	private static class SynchronizedMultiMap<K, V> extends AbstractMultiMap<K, V> {

		private final MultiMap<K, V> map;     // Backing Map
		final Object mutex;        // Object on which to synchronize

		SynchronizedMultiMap(MultiMap<K, V> m) {
			if (m == null) throw new NullPointerException();
			this.map = m;
			mutex = this;
		}

		SynchronizedMultiMap(MultiMap<K, V> m, Object mutex) {
			this.map = m;
			this.mutex = mutex;
		}

		@Override
		public int size() {
			synchronized (mutex) {
				return map.size();
			}
		}

		@Override
		public boolean isEmpty() {
			synchronized (mutex) {
				return map.isEmpty();
			}
		}

		@Override
		public boolean containsKey(Object key) {
			synchronized (mutex) {
				return map.containsKey(key);
			}
		}

		@Override
		public boolean containsValue(Object value) {
			synchronized (mutex) {
				return map.containsValue(value);
			}
		}

		@Override
		public boolean contains(Object key, Object value) {
			synchronized (mutex) {
				return map.contains(key, value);
			}
		}

		@Override
		public Set<V> getValues(Object key) {
			synchronized (mutex) {
				return map.getValues(key);
			}
		}

		@Override
		public Set<K> getKeys(Object value) {
			synchronized (mutex) {
				return map.getKeys(value);
			}
		}

		@Override
		public boolean put(K key, V value) {
			synchronized (mutex) {
				return map.put(key, value);
			}
		}

		@Override
		public Set<V> removeKey(Object key) {
			synchronized (mutex) {
				return map.removeKey(key);
			}
		}

		@Override
		public Set<K> removeValue(Object value) {
			synchronized (mutex) {
				return map.removeValue(value);
			}
		}

		@Override
		public boolean remove(Object key, Object value) {
			synchronized (mutex) {
				return map.remove(key, value);
			}
		}

		@Override
		public boolean putAll(Map<? extends K, ? extends V> m) {
			synchronized (mutex) {
				return this.map.putAll(m);
			}
		}

		@Override
		public boolean putAll(MultiMap<? extends K, ? extends V> m) {
			synchronized (mutex) {
				return this.map.putAll(m);
			}
		}

		@Override
		public void clear() {
			synchronized (mutex) {
				map.clear();
			}
		}

		@Override
		public Set<K> keySet() {
			synchronized (mutex) {
				return map.keySet();
			}
		}

		@Override
		public Set<V> valueSet() {
			synchronized (mutex) {
				return map.valueSet();
			}
		}

		@Override
		public Set<Entry<K, V>> entrySet() {
			synchronized (mutex) {
				return map.entrySet();
			}
		}

		@Override
		public Map<K, Set<V>> toMap() {
			synchronized (mutex) {
				return map.toMap();
			}
		}
	}

	public static <K, V> MultiMap<K, V> unmodifiableMultiMap(MultiMap<K, V> map) {
		return new UnmodifiableMultiMap<>(map);
	}

	private static class UnmodifiableMultiMap<K, V> extends AbstractMultiMap<K, V> {

		private final MultiMap<K, V> map;

		private UnmodifiableMultiMap(MultiMap<K, V> map) {
			this.map = map;
		}

		@Override
		public int size() {
			return map.size();
		}

		@Override
		public boolean containsKey(Object key) {
			return map.containsKey(key);
		}

		@Override
		public boolean containsValue(Object value) {
			return map.containsValue(value);
		}

		@Override
		public boolean contains(Object key, Object value) {
			return map.contains(key, value);
		}

		@Override
		public Set<V> getValues(Object key) {
			return Collections.unmodifiableSet(map.getValues(key));
		}

		@Override
		public Set<K> getKeys(Object value) {
			return Collections.unmodifiableSet(map.getKeys(value));
		}

		@Override
		public boolean put(K key, V value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<V> removeKey(Object key) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<K> removeValue(Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object key, Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean putAll(Map<? extends K, ? extends V> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean putAll(MultiMap<? extends K, ? extends V> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<K> keySet() {
			return Collections.unmodifiableSet(map.keySet());
		}

		@Override
		public Set<V> valueSet() {
			return Collections.unmodifiableSet(map.valueSet());
		}

		@Override
		public Set<Entry<K, V>> entrySet() {
			return Collections.unmodifiableSet(map.entrySet());
		}

		@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
		@Override
		public boolean equals(Object o) {
			return map.equals(o);
		}

		@Override
		public Map<K, Set<V>> toMap() {
			return Collections.unmodifiableMap(map.toMap());
		}
	}


	public static final MultiMap EMPTY_MULTI_MAP = new EmptyMultiMap();

	public static <K, V> MultiMap<K, V> emptyMultiMap() {
		//noinspection unchecked
		return (MultiMap<K, V>) EMPTY_MULTI_MAP;
	}

	private static class EmptyMultiMap<K, V> extends AbstractMultiMap<K, V> {

		@Override
		public int size() {
			return 0;
		}

		@Override
		public boolean containsKey(Object key) {
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			return false;
		}

		@Override
		public boolean contains(Object key, Object value) {
			return false;
		}

		@Override
		public Set<V> getValues(Object key) {
			return Collections.emptySet();
		}

		@Override
		public Set<K> getKeys(Object value) {
			return Collections.emptySet();
		}

		@Override
		public boolean put(K key, V value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<V> removeKey(Object key) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<K> removeValue(Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object key, Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean putAll(Map<? extends K, ? extends V> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean putAll(MultiMap<? extends K, ? extends V> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
		}

		@Override
		public Set<K> keySet() {
			return Collections.emptySet();
		}

		@Override
		public Set<V> valueSet() {
			return Collections.emptySet();
		}

		@Override
		public Set<Entry<K, V>> entrySet() {
			return Collections.emptySet();
		}

		@Override
		public Map<K, Set<V>> toMap() {
			return Collections.emptyMap();
		}
	}


	public static <K, V> MultiMap<K, V> singletonMultiMap(K key, V value) {
		return new SingletonMultiMap<>(key, value);
	}

	private static class SingletonMultiMap<K, V> extends AbstractMultiMap<K, V> {

		private final K key;
		private final V value;

		public SingletonMultiMap(K key, V value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public boolean containsKey(Object key) {
			return Objects.equals(key, this.key);
		}

		@Override
		public boolean containsValue(Object value) {
			return Objects.equals(value, this.value);
		}

		@Override
		public boolean contains(Object key, Object value) {
			return containsKey(key) && containsValue(value);
		}

		@Override
		public Set<V> getValues(Object key) {
			return containsKey(key) ? valueSet() : Collections.emptySet();
		}

		@Override
		public Set<K> getKeys(Object value) {
			return containsValue(value) ? keySet() : Collections.emptySet();
		}

		@Override
		public boolean put(K key, V value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<V> removeKey(Object key) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<K> removeValue(Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object key, Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean putAll(Map<? extends K, ? extends V> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean putAll(MultiMap<? extends K, ? extends V> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<K> keySet() {
			return Collections.singleton(key);
		}

		@Override
		public Set<V> valueSet() {
			return Collections.singleton(value);
		}

		@Override
		public Set<Entry<K, V>> entrySet() {
			return Collections.singleton(new AbstractMap.SimpleImmutableEntry<>(key, value));
		}

		@Override
		public Map<K, Set<V>> toMap() {
			return Collections.singletonMap(key, valueSet());
		}
	}
}