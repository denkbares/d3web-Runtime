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

import java.util.Collections;
import java.util.Iterator;

/**
 * Iterates over the elements of an iterable of iterables, e.g. you can iterate
 * over the Sting items of a Collection&lt;Set&lt;String&gt;&gt;.
 * 
 * @author Volker Belli (denkbares GmbH)
 * @created 14.01.2013
 */
public class FlattingIterator<T> implements Iterator<T> {

	public static interface IteratorFactory<S, T> {

		Iterator<T> create(S sourceObject);
	}

	private final Iterator<?> iterables;
	private final IteratorFactory<Object, T> factory;

	private Iterator<T> current = Collections.<T> emptyList().iterator();

	public <I extends Iterable<T>> FlattingIterator(Iterator<I> iterables) {
		this(iterables, new IteratorFactory<I, T>() {

			@Override
			public Iterator<T> create(I sourceObject) {
				return sourceObject.iterator();
			}
		});
	}

	public FlattingIterator(Iterable<? extends Iterable<T>> iterables) {
		this(iterables.iterator());
	}

	public <S> FlattingIterator(Iterable<S> iterables, IteratorFactory<S, T> factory) {
		this(iterables.iterator(), factory);
	}

	@SuppressWarnings("unchecked")
	public <S> FlattingIterator(Iterator<S> iterables, IteratorFactory<S, T> factory) {
		this.iterables = iterables;
		this.factory = (IteratorFactory<Object, T>) factory;
	}

	private void proceed() {
		// proceed if current has ended and we have further ones
		while (!current.hasNext() && iterables.hasNext()) {
			this.current = iterables.hasNext()
					? factory.create(iterables.next())
					: Collections.<T> emptyList().iterator();
		}
	}

	@Override
	public boolean hasNext() {
		proceed();
		return current.hasNext();
	}

	@Override
	public T next() {
		proceed();
		return current.next();
	}

	@Override
	public void remove() {
		current.remove();
	}
}
