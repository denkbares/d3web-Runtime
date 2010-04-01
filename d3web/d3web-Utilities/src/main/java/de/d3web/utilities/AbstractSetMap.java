/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractSetMap<Key, Type> extends HashMap<Key, Set<Type>> implements ISetMap<Key, Type> {
		
	private static final long serialVersionUID = 1103362229468046173L;
	protected boolean removeEmtpy = true;

	public void addAll(Key key, Collection<Type> objects) {
		for (Type object : new ArrayList<Type>(objects)) {
			add(key, object);
		}
	}
	
	public void addAll(Collection<Key> keys, Type object) {
		for (Key key : new ArrayList<Key>(keys)) {
			add(key, object);
		}
	}
	
	public void addAll(Collection<Key> keys, Collection<Type> objects) {
		for (Type object : new ArrayList<Type>(objects)) {
			addAll(keys, object);
		}
	}
	
	public void addAll(ISetMap<Key, Type> setMap) {
		for (Key key : setMap.keySet()) {
			addAll(key, setMap.get(key));
		}
	}
	
	public boolean remove(Key key, Type object) {
		Collection<Type> coll = get(key);
		if(coll != null) {
			boolean removed = coll.remove(object);
			if(removeEmtpy  && coll.isEmpty()) remove(key);
			return removed;
		}
		return false;
	}
	
	public void removeAll(Key key, Collection<Type> objects) {
		if(objects == null) return;
		for (Type object : new ArrayList<Type>(objects)) {
			remove(key, object);
		}
	}
	
	public void removeAll(Collection<Key> keys, Type object) {
		if(keys == null) return;
		for (Key key : new ArrayList<Key>(keys)) {
			remove(key, object);
		}
	}
	
	public void removeAll( Collection<Key> keys, Collection<Type> objects) {
		for (Key key : new ArrayList<Key>(keys)) {
			removeAll(key, objects);
		}
	}
	
	public Set<Type> getAllValues() {
		return getAllValues(keySet());
	}
		
	public Set<Type> getAllValues(Set<Key> keys) {
		Set<Type> result = new HashSet<Type>();
		if(keys == null) return result;
		for (Key key : new ArrayList<Key>(keys)) {
			Set<Type> set = get(key);
			if(set != null) {
				result.addAll(set);
			}
		}
		return result;
	}
	
}
