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

import java.util.Iterator;

/**
 * An iterator wrapping an existing iterator to get an indexed sub-span of its elements.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 01.12.14.
 */
public class SubSpanIterator<E> implements Iterator<E> {
	private final Iterator<E> delegate;
	private final int end;
	private int index = 0;

	/**
	 * Creates an iterator for a subset of the elements of the specified iterator, starting from
	 * element at index 'start' inclusively (where 0 is the first element) and end before element at
	 * index "end" (exclusively). If "start" is below 0, it will be assumed as 0. If "end" is above
	 * the current number of elements or end is below 0, it will be assumed to be the number of
	 * elements (so the iterator will stop after the last element of the specified iterator).
	 *
	 * @param iterator the elements to get the sub-span from
	 * @param start the index of the first element to iterate
	 * @param end the element index to stop iteration before
	 */
	public SubSpanIterator(Iterator<E> iterator, int start, int end) {
		this.delegate = iterator;
		this.end = (end < 0) ? Integer.MAX_VALUE : end;
		// skip first elements
		while (index < start && delegate.hasNext()) next();
	}

	@Override
	public boolean hasNext() {
		return (index < end) && delegate.hasNext();
	}

	@Override
	public E next() {
		E next = delegate.next();
		index++;
		return next;
	}
}
