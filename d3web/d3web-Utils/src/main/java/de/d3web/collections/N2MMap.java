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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.d3web.collections.MultiMaps.CollectionFactory;

/**
 * This class provides an implementation for a {@link MultiMap} that is
 * efficient (= O(1)) in both directions, accessing values for keys and
 * accessing keys for values.
 * 
 * @author Volker Belli (denkbares GmbH)
 * @created 07.01.2014
 */
// we are suppressing warnings when using "Object"s as key and value.
// They are used when accessing entries, because java.util.Map also does it this way.
// only methods that adds items are forcing to have the correct parameter types.
@SuppressWarnings("SuspiciousMethodCalls")
public class N2MMap<K, V> implements MultiMap<K, V> {

	private int size = 0;
	private final Map<K, Set<V>> k2v;
	private final Map<V, Set<K>> v2k;

	private final CollectionFactory<K> keyFactory;
	private final CollectionFactory<V> valueFactory;

	/**
	 * Creates a new N2MMap using HashSets and HashMaps for the contained
	 * objects.
	 */
	public N2MMap() {
		this(MultiMaps.<K> hashFactory(), MultiMaps.<V> hashFactory());
	}

	/**
	 * Creates a new N2MMap using the specified collection factories to manage
	 * the keys and values to be added. The {@link CollectionFactory} can be
	 * accessed by the {@link MultiMaps} class through some utility methods
	 * provided.
	 * 
	 * @param keyFactory the collection factory used to manage the keys
	 * @param valueFactory the collection factory used to manage the values
	 */
	public N2MMap(CollectionFactory<K> keyFactory, CollectionFactory<V> valueFactory) {
		this.keyFactory = keyFactory;
		this.valueFactory = valueFactory;
		this.k2v = keyFactory.createMap();
		this.v2k = valueFactory.createMap();
	}

	@Override
	public boolean put(K key, V value) {
		// connect source to term
		Set<V> values = k2v.get(key);
		if (values == null) {
			values = valueFactory.createSet();
			k2v.put(key, values);
		}
		boolean isNew = values.add(value);

		// if the mapping already exists, we are done
		if (!isNew) return false;
		size++;

		// otherwise also connect the value to the key
		Set<K> keys = v2k.get(value);
		if (keys == null) {
			keys = keyFactory.createSet();
			v2k.put(value, keys);
		}
		keys.add(key);
		return true;
	}

	@Override
	public boolean putAll(Map<? extends K, ? extends V> map) {
		boolean hasChanged = false;
		for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
			hasChanged |= put(entry.getKey(), entry.getValue());
		}
		return hasChanged;
	}

	@Override
	public boolean putAll(MultiMap<? extends K, ? extends V> map) {
		boolean hasChanged = false;
		for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
			hasChanged |= put(entry.getKey(), entry.getValue());
		}
		return hasChanged;
	}

	@Override
	public void clear() {
		k2v.clear();
		v2k.clear();
		size = 0;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Set<K> removeValue(Object value) {
		Set<K> keys = v2k.remove(value);
		if (keys == null) return Collections.emptySet();
		for (K key : keys) {
			// for each key remove it from its support list
			Set<V> values = k2v.get(key);
			values.remove(value);
			// is the support list is empty, also remove the term itself
			if (values.isEmpty()) {
				k2v.remove(key);
			}
		}
		size -= keys.size();
		return Collections.unmodifiableSet(keys);
	}

	@Override
	public Set<V> removeKey(Object key) {
		Set<V> values = k2v.remove(key);
		if (values == null) return Collections.emptySet();
		for (V value : values) {
			// for each value remove it from its support list
			Set<K> keys = v2k.get(value);
			keys.remove(key);
			// is the support list is empty, also remove the term itself
			if (keys.isEmpty()) {
				v2k.remove(value);
			}
		}
		size -= values.size();
		return Collections.unmodifiableSet(values);
	}

	@Override
	public boolean remove(Object key, Object value) {
		// if no such value is known, noting to do
		Set<K> keys = v2k.get(value);
		if (keys == null) return false;

		// if there is no such key for the value, noting to do
		boolean hasFound = keys.remove(key);
		if (!hasFound) return false;

		// otherwise we have successfully removed the mapping
		size--;
		if (keys.isEmpty()) v2k.remove(value);

		// also delete reference from key to values
		Set<V> values = k2v.get(key);
		values.remove(value);

		// and check if the list has become empty
		// and can be removed completely
		if (values.isEmpty()) {
			k2v.remove(key);
		}
		return true;
	}

	@Override
	public Set<K> getKeys(Object value) {
		Set<K> keys = v2k.get(value);
		return (keys == null) ? Collections.<K> emptySet() : Collections.unmodifiableSet(keys);
	}

	@Override
	public Set<V> getValues(Object key) {
		Set<V> values = k2v.get(key);
		return (values == null) ? Collections.<V> emptySet() : Collections.unmodifiableSet(values);
	}

	@Override
	public boolean contains(Object key, Object value) {
		Set<V> values = k2v.get(key);
		return (values != null) && values.contains(value);
	}

	@Override
	public boolean containsKey(Object key) {
		return k2v.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return v2k.containsKey(value);
	}

	@Override
	public Set<K> keySet() {
		return Collections.unmodifiableSet(k2v.keySet());
	}

	@Override
	public Set<V> valueSet() {
		return Collections.unmodifiableSet(v2k.keySet());
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new AbstractSet<Entry<K, V>>() {

			@Override
			public Iterator<Entry<K, V>> iterator() {
				return new Iterator<Map.Entry<K, V>>() {

					Iterator<K> keyIter = keySet().iterator();
					Iterator<V> valIter = Collections.<V> emptySet().iterator();
					K currentKey = null;
					V currentVal = null;

					@Override
					public boolean hasNext() {
						return keyIter.hasNext() || valIter.hasNext();
					}

					@Override
					public Entry<K, V> next() {
						// if no next value available, proceed to next key
						if (!valIter.hasNext()) {
							currentKey = keyIter.next();
							valIter = getValues(currentKey).iterator();
						}
						currentVal = valIter.next();
						return new AbstractMap.SimpleImmutableEntry<K, V>(currentKey, currentVal);
					}

					@Override
					public void remove() {
						N2MMap.this.remove(currentKey, currentVal);
					}
				};
			}

			@Override
			public int size() {
				return size;
			}

			@Override
			public boolean contains(Object entry) {
				if (entry instanceof Entry) {
					Entry<?, ?> e = (Entry<?, ?>) entry;
					return N2MMap.this.contains(e.getKey(), e.getValue());
				}
				return false;
			}

			@Override
			public boolean remove(Object entry) {
				if (entry instanceof Entry) {
					Entry<?, ?> e = (Entry<?, ?>) entry;
					return N2MMap.this.remove(e.getKey(), e.getValue());
				}
				return false;
			}

			@Override
			public void clear() {
				N2MMap.this.clear();
			}
		};
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public int hashCode() {
		return entrySet().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MultiMap) {
			MultiMap<?, ?> multiMap = (MultiMap<?, ?>) obj;
			return entrySet().equals(multiMap.entrySet());
		}
		return false;
	}

	@Override
	public String toString() {
		return MultiMaps.toString(this);
	}

	@Override
	public Map<K, Set<V>> toMap() {
		return MultiMaps.asMap(this);
	}
}
