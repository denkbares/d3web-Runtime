/*
 * Copyright (C) 2016 denkbares GmbH, Germany
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

/**
 * This class provides some basic implementation for a {@link MultiMap}s for easier
 * implementation of the actual multi-maps:
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 07.01.2014
 */
public abstract class AbstractMultiMap<K, V> implements MultiMap<K, V> {

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
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new AbstractSet<Entry<K, V>>() {

			@Override
			public Iterator<Entry<K, V>> iterator() {
				return new Iterator<Entry<K, V>>() {

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
						return new AbstractMap.SimpleImmutableEntry<>(currentKey, currentVal);
					}

					@Override
					public void remove() {
						AbstractMultiMap.this.remove(currentKey, currentVal);
					}
				};
			}

			@Override
			public int size() {
				return AbstractMultiMap.this.size();
			}

			@Override
			public boolean contains(Object entry) {
				if (entry instanceof Entry) {
					Entry<?, ?> e = (Entry<?, ?>) entry;
					return AbstractMultiMap.this.contains(e.getKey(), e.getValue());
				}
				return false;
			}

			@Override
			public boolean remove(Object entry) {
				if (entry instanceof Entry) {
					Entry<?, ?> e = (Entry<?, ?>) entry;
					return AbstractMultiMap.this.remove(e.getKey(), e.getValue());
				}
				return false;
			}

			@Override
			public void clear() {
				AbstractMultiMap.this.clear();
			}
		};
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
		Iterator<Entry<K, V>> i = entrySet().iterator();
		if (!i.hasNext()) return "{}";

		StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (; ; ) {
			Entry<K, V> e = i.next();
			K key = e.getKey();
			V value = e.getValue();
			sb.append(key == this ? "(this Map)" : key);
			sb.append('=');
			sb.append(value == this ? "(this Map)" : value);
			if (!i.hasNext()) return sb.append('}').toString();
			sb.append(", ");
		}
	}

	@Override
	public Map<K, Set<V>> toMap() {
		return new AbstractMap<K, Set<V>>() {

			@Override
			public Set<Entry<K, Set<V>>> entrySet() {

				return new AbstractSet<Entry<K, Set<V>>>() {

					@Override
					public Iterator<Entry<K, Set<V>>> iterator() {
						final Iterator<K> keyIter = AbstractMultiMap.this.keySet().iterator();
						return new Iterator<Entry<K, Set<V>>>() {

							@Override
							public boolean hasNext() {
								return keyIter.hasNext();
							}

							@Override
							public Entry<K, Set<V>> next() {
								K key = keyIter.next();
								Set<V> values = getValues(key);
								return new SimpleImmutableEntry<>(key, values);
							}

							@Override
							public void remove() {
								throw new UnsupportedOperationException();
							}
						};
					}

					@Override
					public int size() {
						return AbstractMultiMap.this.keySet().size();
					}
				};
			}
		};
	}
}
