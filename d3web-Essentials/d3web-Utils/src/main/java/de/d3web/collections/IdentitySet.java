/*
 * Copyright (C) 2011 denkbares GmbH
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

import java.util.AbstractSet;
import java.util.IdentityHashMap;
import java.util.Iterator;

/**
 * An Set using reference-equality
 * 
 * Should only be used for special purposes
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 02.02.2011
 */
public class IdentitySet<E> extends AbstractSet<E> {

	private transient IdentityHashMap<E, Object> map = new IdentityHashMap<E, Object>();
	private static final Object PRESENT = new Object();

	@Override
	public boolean add(E arg0) {
		return map.put(arg0, PRESENT) == null;
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean contains(Object arg0) {
		return map.containsKey(arg0);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	@Override
	public boolean remove(Object arg0) {
		return map.remove(arg0) == PRESENT;
	}

	@Override
	public int size() {
		return map.size();
	}
}
