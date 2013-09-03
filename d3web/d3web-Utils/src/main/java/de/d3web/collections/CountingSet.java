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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Utility class to count occurrences of particular objects. For each object a
 * counter is created. It will be counted how often the particular objects has
 * been added.
 * <p>
 * In contrast to an ordinary set, if an object has been added multiple times,
 * it remains in the set even on removal, until if has been removed as often as
 * it has been added.
 * 
 * @author Volker Belli (denkbares GmbH)
 * @created 14.02.2013
 */
public class CountingSet<E> implements Set<E> {

	private static class Count {

		int count = 0;

		@Override
		public String toString() {
			return String.valueOf(count);
		}
	}

	private final HashMap<E, Count> counters = new HashMap<E, Count>();

	@Override
	public int size() {
		return counters.size();
	}

	@Override
	public boolean isEmpty() {
		return counters.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return counters.containsKey(o);
	}

	@Override
	public Iterator<E> iterator() {
		return counters.keySet().iterator();
	}

	@Override
	public Object[] toArray() {
		return counters.keySet().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return counters.keySet().toArray(a);
	}

	@Override
	public boolean add(E object) {
		return inc(object) == 1;
	}

	/**
	 * Removes an object from the set. If the object has been added multiple
	 * times, the object still remains in the set, but its counter will be
	 * decreased by 1 instead. The method returns true if the object has been
	 * removed from the set.
	 */
	@Override
	public boolean remove(Object object) {
		return dec(object) == 0;
	}

	/**
	 * Returns the number of times the object has been added or 0 if the object
	 * is not in this set.
	 * 
	 * @created 14.02.2013
	 * @param object the object to access its count
	 * @return how often the object has been added
	 */
	public int getCount(E object) {
		Count count = counters.get(object);
		return (count != null) ? count.count : 0;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return counters.keySet().containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean changed = false;
		for (E object : c) {
			changed |= add(object);
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean changed = false;
		Collection<E> copy = new ArrayList<E>(this);
		if (!(c instanceof Set)) c = new HashSet<Object>(c);
		for (Object o : copy) {
			if (!c.contains(o)) {
				changed |= remove(o);
			}
		}
		return changed;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object object : c) {
			changed |= remove(object);
		}
		return changed;
	}

	@Override
	public void clear() {
		counters.clear();
	}

	/**
	 * The method is similar to {@link #add(Object)}, which also adds the object
	 * to the set. The only difference is that the method returns the number of
	 * occurrences of the specified object after it has been added (instead of a
	 * flag if the set has been changed).
	 * 
	 * @created 14.02.2013
	 * @param object the object to add / increase the counter for
	 * @return the actual counter of that object
	 */
	public int inc(E object) {
		Count count = counters.get(object);
		if (count == null) {
			count = new Count();
			counters.put(object, count);
		}
		count.count++;
		return count.count;
	}

	/**
	 * The method is similar to {@link #remove(Object)}, which also removes the
	 * object form the set or decrease its counter by 1. The only difference is
	 * that the method returns the number of occurrences of the specified object
	 * after it has been removed (instead of a flag if the set has been
	 * changed).
	 * 
	 * @created 14.02.2013
	 * @param object the object to add / increase the counter for
	 * @return the actual counter of that object
	 */
	public int dec(Object object) {
		Count count = counters.get(object);
		if (count != null) {
			count.count--;
			if (count.count == 0) {
				counters.remove(object);
			}
			return count.count;
		}
		return 0;
	}

}
