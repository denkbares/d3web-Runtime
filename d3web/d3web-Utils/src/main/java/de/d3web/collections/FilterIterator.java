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
 * Class to filter the elements of an given iterator by some accept method.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 13.03.2014
 */
public abstract class FilterIterator<E> implements Iterator<E> {

	private final Iterator<E> delegate;
	private E nextEntry = null;
	private boolean needsUpdate = true;

	public FilterIterator(Iterator<E> source) {
		this.delegate = source;
	}

	private void moveToNextEntry() {
		this.nextEntry = null;
		this.needsUpdate = false;
		while (delegate.hasNext()) {
			E next = delegate.next();
			if (accept(next)) {
				this.nextEntry = next;
				break;
			}
		}
	}

	/**
	 * This method determines if an item is accepted from the underlying delegate iterator or if
	 * rejected.
	 *
	 * @param item the item to be checked
	 * @return true if the item shall be accepted.
	 */
	public abstract boolean accept(E item);

	@Override
	public boolean hasNext() {
		if (needsUpdate) moveToNextEntry();
		return nextEntry != null;
	}

	@Override
	public E next() {
		if (needsUpdate) moveToNextEntry();
		if (nextEntry == null) throw new NoSuchElementException();
		needsUpdate = true;
		return nextEntry;
	}

	@Override
	public void remove() {
		// we can only remove if the cursor have not been updated since last next()
		if (needsUpdate && nextEntry != null) delegate.remove();
		else throw new UnsupportedOperationException();
	}
}
