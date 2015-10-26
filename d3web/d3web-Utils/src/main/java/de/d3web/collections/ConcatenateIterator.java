/*
 * Copyright (C) 2013 denkbares GmbH, Germany
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
 * Concatenates the items of multiple {@link Iterator}s into one Iterator.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 14.01.2013
 */
public class ConcatenateIterator<T> implements Iterator<T> {

	private final Iterator<? extends T> iterators[];
	private int current;

	public ConcatenateIterator(Iterator<? extends T>... iterators) {
		this.iterators = iterators;
		this.current = 0;
	}

	@Override
	public boolean hasNext() {
		while (current < iterators.length && !iterators[current].hasNext())
			current++;

		return current < iterators.length;
	}

	@Override
	public T next() {
		while (current < iterators.length && !iterators[current].hasNext())
			current++;

		return iterators[current].next();
	}

	@Override
	public void remove() {
		iterators[current].remove();
	}
}
