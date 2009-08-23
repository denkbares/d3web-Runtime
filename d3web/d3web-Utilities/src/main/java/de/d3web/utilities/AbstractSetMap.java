package de.d3web.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractSetMap<Key, Type> extends HashMap<Key, Set<Type>> implements ISetMap<Key, Type> {
		
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
			Set set = get(key);
			if(set != null) {
				result.addAll(set);
			}
		}
		return result;
	}
	
}
