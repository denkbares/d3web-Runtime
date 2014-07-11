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
 * Concatenates the items of multiple {@link java.util.Iterator}s into one Iterator.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 14.01.2013
 */
public class ConcatenateIterable<T> implements Iterable<T> {

	private final Iterable<? extends T> iterables[];

	public ConcatenateIterable(Iterable<? extends T> iterable1, Iterable<? extends T> iterable2) {
		//noinspection unchecked
		this(new Iterable[] {iterable1, iterable2});
	}

	public ConcatenateIterable(Iterable<? extends T> iterable1, Iterable<? extends T> iterable2, Iterable<? extends T> iterable3) {
		//noinspection unchecked
		this(new Iterable[] {iterable1, iterable2, iterable3});
	}

	public ConcatenateIterable(Iterable<? extends T>... iterables) {
		this.iterables = iterables;
	}

	@Override
	public Iterator<T> iterator() {
		Iterator[] iterators = new Iterator[iterables.length];
		for (int i=0; i<iterables.length; i++) {
			iterators[i] = iterables[i].iterator();
		}
		//noinspection unchecked
		return new ConcatenateIterator(iterators);
	}
}
