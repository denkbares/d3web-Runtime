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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Class that caches the entries of an iterable or iterator for repeatable and fast access to the
 * elements.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 30.12.2014
 */
public class CachedIterable<E> implements Iterable<E> {
	private final Iterator<E> futures;
	private final List<E> cache = new ArrayList<E>();

	/**
	 * Creates a new instance for the remaining elements of the specified iterator. Please note that
	 * the iterator shall no longer be used outside this instance after the instance has been
	 * created.
	 *
	 * @param elements the elements of this instance
	 */
	public CachedIterable(Iterator<E> elements) {
		this.futures = elements;
	}

	/**
	 * Creates a new instance for the elements of the specified iterable.
	 *
	 * @param elements the elements of this instance
	 */
	public CachedIterable(Iterable<E> elements) {
		this.futures = elements.iterator();
	}

	/**
	 * Fills the cache with at least count elements. After this call the cache will contain at least
	 * the specified number of elements, or less elements if the underlying iterator does not
	 * provide this many elements at all.
	 *
	 * @param count the minimum number of elements to be contained, if possible
	 */
	private void fillCache(int count) {
		while (cache.size() < count && futures.hasNext()) {
			cache.add(futures.next());
		}
	}

	@Override
	public Iterator<E> iterator() {
		return iterator(0, Integer.MAX_VALUE);
	}

	/**
	 * Returns an iterator over a subset of the elements of type {@code T}, starting from the
	 * element at startIndex (inclusively; where 0 is the first element) and stopping before the
	 * element at endIndex (exclusively).
	 * <p/>
	 * If startIndex is below 0, the iteration start from the first element. If endIndex is ≤ 0 or ≤
	 * startIndex the iterator will be empty. If endIndex is larger than the number of elements
	 * contained in this iterator the iteration will stop before endIndex is reached.
	 *
	 * @return an Iterator.
	 */
	public Iterator<E> iterator(final int startIndex, final int endIndex) {
		return new Iterator<E>() {
			private int index = Math.max(startIndex, 0);

			@Override
			public boolean hasNext() {
				if (index >= endIndex) return false;
				fillCache(index + 1);
				return index < cache.size();
			}

			@Override
			public E next() {
				if (!hasNext()) throw new NoSuchElementException();
				return cache.get(index++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
