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
 * An iterable wrapping an existing iterable to get an indexed sub-span of its elements.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 30.12.2014
 */
public class SubSpanIterable<E> implements Iterable<E> {

	private final Iterable<E> delegate;
	private final int start;
	private final int end;

	public SubSpanIterable(Iterable<E> delegate, int start, int end) {
		this.delegate = delegate;
		this.start = start;
		this.end = end;
	}

	@Override
	public Iterator<E> iterator() {
		return new SubSpanIterator<E>(delegate.iterator(), start, end);
	}
}
