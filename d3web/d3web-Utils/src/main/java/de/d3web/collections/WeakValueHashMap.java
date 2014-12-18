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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of a Map that will remove entries when the value in the map has been cleaned from
 * garbage collection – in contrast to WeakHashMap, which will remove the entries if the key is
 * garbage collected.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 15.12.14.
 */
public class WeakValueHashMap<K, V> extends AbstractMap<K, V> {

	public Set<Entry<K, V>> entrySet() {
		processQueue();
		final Set<Entry<K, WeakValueRef<K, V>>> entries = hash.entrySet();
		return new AbstractSet<Entry<K, V>>() {
			@Override
			public Iterator<Entry<K, V>> iterator() {
				final Iterator<Entry<K, WeakValueRef<K, V>>> iterator = entries.iterator();
				return new Iterator<Entry<K, V>>() {
					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}

					@Override
					public Entry<K, V> next() {
						final Entry<K, WeakValueRef<K, V>> next = iterator.next();
						return new Entry<K, V>() {
							@Override
							public K getKey() {
								return next.getKey();
							}

							@Override
							public V getValue() {
								return unwrap(next.getValue());
							}

							@Override
							public V setValue(V value) {
								return unwrap(next.setValue(wrap(next.getKey(), value)));
							}
						};
					}
				};
			}

			@Override
			public int size() {
				return entries.size();
			}
		};
	}

	/* Hash table mapping WeakKeys to values */
	private Map<K, WeakValueRef<K, V>> hash;

	/* Reference queue for cleared WeakKeys */
	private ReferenceQueue<V> queue = new ReferenceQueue<V>();

	// Remove all invalidated entries from the map:
	// remove all entries whose values have been discarded
	private void processQueue() {
		while (true) {
			WeakValueRef reference = (WeakValueRef) queue.poll();
			if (reference == null) break;
			// only remove if the stored value in the hash-table is still the queued one
			@SuppressWarnings("unchecked") K key = (K) reference.key;
			if (reference == hash.get(key)) {
				hash.remove(key);
			}
		}
	}

	/**
	 * Constructs a new, empty <code>WeakValueHashMap</code> with the given initial capacity and the
	 * given load factor.
	 *
	 * @param initialCapacity The initial capacity of the <code>WeakHashMap</code>
	 * @param loadFactor The load factor of the <code>WeakHashMap</code>
	 * @throws IllegalArgumentException If the initial capacity is less than zero, or if the load
	 * factor is nonpositive
	 */
	public WeakValueHashMap(int initialCapacity, float loadFactor) {
		hash = new HashMap<K, WeakValueRef<K, V>>(initialCapacity, loadFactor);
	}

	/**
	 * Constructs a new, empty <code>WeakValueHashMap</code> with the given initial capacity and the
	 * default load factor, which is <code>0.75</code>.
	 *
	 * @param initialCapacity The initial capacity of the <code>WeakHashMap</code>
	 * @throws IllegalArgumentException If the initial capacity is less than zero
	 */
	public WeakValueHashMap(int initialCapacity) {
		hash = new HashMap<K, WeakValueRef<K, V>>(initialCapacity);
	}

	/**
	 * Constructs a new, empty <code>WeakValueHashMap</code> with the default initial capacity and
	 * the default load factor, which is <code>0.75</code>.
	 */
	public WeakValueHashMap() {
		hash = new HashMap<K, WeakValueRef<K, V>>();
	}

	/**
	 * Constructs a new <code>WeakValueHashMap</code> with the same mappings as the specified
	 * <tt>Map</tt>.  The <tt>HashMap</tt> is created with default load factor (0.75) and an initial
	 * capacity sufficient to hold the mappings in the specified <tt>Map</tt>.
	 *
	 * @param map the map whose mappings are to be placed in this map
	 * @since 1.3
	 */
	public WeakValueHashMap(Map<? extends K, ? extends V> map) {
		this(Math.max(2 * map.size(), 11), 0.75f);
		putAll(map);
	}

	/**
	 * Returns the number of key-value mappings in this map. <strong>Note:</strong> <em>In contrast
	 * with most implementations of the <code>Map</code> interface, the time required by this
	 * operation is linear in the size of the map.</em>
	 */
	public int size() {
		processQueue();
		return hash.size();
	}

	/**
	 * Returns <code>true</code> if this map contains no key-value mappings.
	 */
	public boolean isEmpty() {
		processQueue();
		return hash.isEmpty();
	}

	/**
	 * Returns <code>true</code> if this map contains a mapping for the specified key.
	 *
	 * @param key The key whose presence in this map is to be tested
	 */
	public boolean containsKey(Object key) {
		processQueue();
		return hash.containsKey(key);
	}

	/**
	 * Returns the value to which this map maps the specified <code>key</code>. If this map does not
	 * contain a value for this key, then return <code>null</code>.
	 *
	 * @param key The key whose associated value, if any, is to be returned
	 */
	@Override
	public V get(Object key) {
		processQueue();
		return unwrap(hash.get(key));
	}

	/**
	 * Updates this map so that the given <code>key</code> maps to the given <code>value</code>.  If
	 * the map previously contained a mapping for <code>key</code> then that mapping is replaced and
	 * the previous value is returned.
	 *
	 * @param key The key that is to be mapped to the given <code>value</code>
	 * @param value The value to which the given <code>key</code> is to be mapped
	 * @return The previous value to which this key was mapped, or <code>null</code> if if there was
	 * no mapping for the key
	 */
	public V put(K key, V value) {
		processQueue();
		return unwrap(hash.put(key, wrap(key, value)));
	}

	/**
	 * Removes the mapping for the given <code>key</code> from this map, if present.
	 *
	 * @param key The key whose mapping is to be removed
	 * @return The value to which this key was mapped, or <code>null</code> if there was no mapping
	 * for the key
	 */
	public V remove(Object key) {
		processQueue();
		return unwrap(hash.remove(key));
	}

	/**
	 * Removes all mappings from this map.
	 */
	public void clear() {
		processQueue();
		hash.clear();
	}

	private static class WeakValueRef<K, V> extends WeakReference<V> {
		public K key;

		private WeakValueRef(K key, V val, ReferenceQueue<V> q) {
			super(val, q);
			this.key = key;
		}
	}

	private WeakValueRef<K, V> wrap(K key, V value) {
		if (value == null) return null;
		return new WeakValueRef<K, V>(key, value, queue);
	}

	private V unwrap(WeakValueRef<K, V> reference) {
		return (reference == null) ? null : reference.get();
	}
}