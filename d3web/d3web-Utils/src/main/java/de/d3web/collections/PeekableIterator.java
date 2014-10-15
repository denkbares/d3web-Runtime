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
import java.util.NoSuchElementException;

/**
 * Implements an iterator that allows to fetch the next object without proceeding to it. Multiple
 * calls to the "peek" method will return the element that will be returned by the next call to the
 * "next" method.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 14.10.14.
 */
public class PeekableIterator<E> implements Iterator<E> {

	private static final Object SENTINEL = new Object();

	private final Iterator<E> iterator;
	private Object next = null;

	public PeekableIterator(Iterator<E> iterator) {
		this.iterator = iterator;
		updateNext();
	}

	private void updateNext() {
		next = iterator.hasNext() ? iterator.next() : SENTINEL;
	}

	/**
	 * Returns the next element in the iteration without proceeding to that element. Multiple calls
	 * to that method will always return the object that will be returned by the next call to the
	 * "next" method.
	 *
	 * @return the next element in the iteration
	 * @throws NoSuchElementException if the iteration has no more elements
	 */
	@SuppressWarnings("unchecked")
	public E peek() {
		if (next == SENTINEL) throw new NoSuchElementException();
		return (E) next;
	}

	@Override
	public E next() {
		E result = peek();
		updateNext();
		return result;
	}

	@Override
	public boolean hasNext() {
		return next != SENTINEL;
	}
}
