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
 * Iterator that decorates an other iterator, but mapping its elements with a specified mapper
 * function.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 30.12.2014
 */
public class MappingIterator<S, E> implements Iterator<E> {

	/**
	 * Functional interface to map a source value of type S to a mapped value of type E
	 *
	 * @param <S> the source type
	 * @param <E> the mapped type
	 */
	public interface MappingFunction<S, E> {
		E apply(S sourceItem);
	}

	private final Iterator<S> source;
	private final MappingFunction<? super S, ? extends E> mapper;

	/**
	 * Creates a new iterator that decorates an other iterator, but mapping its elements with the
	 * specified mapper function.
	 *
	 * @param source the iterator to get the original elements from
	 * @param mapper the mapping function, applied for each element during iteration
	 */
	public MappingIterator(Iterator<S> source, MappingFunction<? super S, ? extends E> mapper) {
		this.source = source;
		this.mapper = mapper;
	}

	@Override
	public boolean hasNext() {
		return source.hasNext();
	}

	@Override
	public E next() {
		return mapper.apply(source.next());
	}

	@Override
	public void remove() {
		source.remove();
	}
}
