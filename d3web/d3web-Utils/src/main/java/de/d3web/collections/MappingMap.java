/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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
import java.util.Set;

import de.d3web.collections.MappingIterator.MappingFunction;

/**
 * Implements a map that is located on top of an existing map but mapping the values through a
 * mapping functional interface. For convenience, null values are nor mapped with the mapping
 * function. The mapping function will never be called with a null value. Instead, null is assumed
 * to be mapped to null in every case.
 * <p/>
 * Example: <pre>{@code
 * Map<String, Double> ratings = getRatings();
 * Map<String, Long> rounded = new MappingMap<>(ratings, Math::round);
 * Long value = rounded.get("foo");
 * }</pre>
 * <p/>
 * The MappingMap is unmodifiable, because it is not possible to reflect all changes consistently to
 * the original map (putting a value to the MappingMap, you cannot create the value for the original
 * map, because the mapping functional interface provides a one-direction conversion only).
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 13.08.2015
 */
public class MappingMap<K, O, V> extends AbstractMap<K, V> {

	private final Map<K, O> delegate;
	private final MappingFunction<O, V> mapper;

	public MappingMap(Map<K, O> delegate, MappingFunction<O, V> mapper) {
		this.delegate = delegate;
		this.mapper = mapper;
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public Set<Entry<K, V>> entrySet() {
		return new AbstractSet<Entry<K, V>>() {
			@Override
			public Iterator<Entry<K, V>> iterator() {
				Iterator<Entry<K, O>> sourceIter = delegate.entrySet().iterator();
				return new MappingIterator<Entry<K, O>, Entry<K, V>>(
						sourceIter, new MappingFunction<Entry<K, O>, Entry<K, V>>() {
					@Override
					public Entry<K, V> apply(Entry<K, O> sourceItem) {
						return new SimpleImmutableEntry<K, V>(
								sourceItem.getKey(),
								mapValue(sourceItem.getValue()));
					}
				});
			}

			@Override
			public int size() {
				return MappingMap.this.size();
			}
		};
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public Set<K> keySet() {
		// implemented additionally, because we do not need any value mapping for the keys
		return Collections.unmodifiableSet(delegate.keySet());
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public boolean containsKey(Object key) {
		return delegate.containsKey(key);
	}

	@Override
	public V get(Object key) {
		return mapValue(delegate.get(key));
	}

	private V mapValue(O value) {
		return (value == null) ? null : mapper.apply(value);
	}
}
