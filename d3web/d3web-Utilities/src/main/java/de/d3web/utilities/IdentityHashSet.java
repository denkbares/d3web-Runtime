/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.utilities;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class IdentityHashSet<E> extends AbstractSet<E> {

	private Map<E, Object> map;

	private final static Object VALUE = new Object();

	public IdentityHashSet() {
		map = new IdentityHashMap<E, Object>();
	}

	public IdentityHashSet(int expectedMaxSize) {
		map = new IdentityHashMap<E, Object>(expectedMaxSize);
	}

	public IdentityHashSet(Collection<? extends E> collection) {
		this(collection.size());
		addAll(collection);
	}

	@Override
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	@Override
	public int size() {
		return map.keySet().size();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Set)) return false;
		if (((Collection) o).size() != size()) return false;
		if (o instanceof IdentityHashSet) {
			return o.equals(map.keySet());
		}
		else {
			return map.keySet().equals(o);
		}
	}

	@Override
	public int hashCode() {
		return map.keySet().hashCode();
	}

	@Override
	public boolean add(E o) {
		if (!map.containsKey(o)) {
			map.put(o, VALUE);
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean remove(Object o) {
		return map.remove(o) == VALUE;
	}

	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	@Override
	public void clear() {
		map.clear();
	}

}
