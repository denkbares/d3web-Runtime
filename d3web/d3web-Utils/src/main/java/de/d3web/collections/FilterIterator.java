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

	/**
	 * Functional interface to check a particular item to be included in the filtered iteration or
	 * not. For more details see {@link #accept(Object)}.
	 *
	 * @param <E> the type of the items to be checked
	 */
	public interface EntryFilter<E> {
		/**
		 * Method to be overwritten. The method should check if the specified item should be
		 * included in the iteration. if the method returns false, the item is skipped.
		 * <p/>
		 * The method may (instead of returning a boolean value) throw the Stop.EXCLUDE or
		 * Stop.INCLUDE exception. In this case the iteration assumes that all succeeding items will
		 * not been accepted. The iteration stops immediately at the current item. Depending on the
		 * thrown exception instance, the currently specified item will be included or excluded
		 * before the iteration terminates.
		 *
		 * @param item the current item to be checked
		 * @return if the specified item should be included
		 * @throws Stop if the subsequent items are all not accepted (all skipped)
		 */
		boolean accept(E item) throws Stop;
	}

	public static class Stop extends Exception {
		/**
		 * Exception to be thrown by the accept method to signal that none of the succeeding items
		 * will be accepted, but the current item should be included in the filtered iteration.
		 */
		@SuppressWarnings("ThrowableInstanceNeverThrown")
		public static final Stop INCLUDE = new Stop("include");

		/**
		 * Exception to be thrown by the accept method to signal that none of the succeeding items
		 * will be accepted. The current item should also not been included in the filtered
		 * iteration.
		 */
		@SuppressWarnings("ThrowableInstanceNeverThrown")
		public static final Stop EXCLUDE = new Stop("exclude");

		private Stop(String message) {
			super(message);
		}
	}

	private final Iterator<E> delegate;
	private E nextEntry = null;
	private boolean needsUpdate = true;
	private boolean endReached = false;

	/**
	 * Creates a new FilterIterator for the specified iterator. The returned iterator contains only
	 * the elements that passes the {@link #accept(Object)} method.
	 *
	 * @param source the iterator to be filtered
	 */
	public FilterIterator(Iterator<E> source) {
		this.delegate = source;
	}

	/**
	 * Creates a new FilterIterator for the specified iterator and a filter functional interface.
	 * The returned iterator contains only the elements that passes the accept function of the
	 * specified filter with "true".
	 * <p/>
	 * This method allows to use this FilterIterator implementations in Java 8+ styled manner,
	 * without creating a subclass of this abstract class.
	 *
	 * @param iterator the iterator to be filtered
	 * @param filter the filter function to be applied
	 * @param <E> the type of the elements to be iterated
	 * @return an iterator only containing the accepted entries
	 */
	public static <E> FilterIterator<E> filter(Iterator<E> iterator, final EntryFilter<? super E> filter) {
		return new FilterIterator<E>(iterator) {
			@Override
			public boolean accept(E item) {
				try {
					return filter.accept(item);
				}
				catch (Stop e) {
					signalEnd();
					return e.equals(Stop.INCLUDE);
				}
			}
		};
	}

	private void moveToNextEntry() {
		this.nextEntry = null;
		this.needsUpdate = false;
		while (!endReached && delegate.hasNext()) {
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

	/**
	 * This method can be called by the implementer of this abstract class when the overwritten
	 * accept-method is sure that all sub-sequential elements of the underlying iterator will not
	 * been accepted and therefore filtered out. Thus the method signals that this iterator has
	 * reached its end.
	 * <p/>
	 * Note that if this method is called from within the accept method, the filtering of the
	 * current element is determined only (!) by the return value of the accept method. This method
	 * only influences further items. So you can detect the end also at the last accepted element by
	 * calling this method but return true within the accept method.
	 */
	protected void signalEnd() {
		endReached = true;
	}
}
