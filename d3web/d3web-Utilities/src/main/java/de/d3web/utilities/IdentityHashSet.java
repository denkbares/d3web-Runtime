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
		if (((Collection)o).size() != size()) return false;
		if(o instanceof IdentityHashSet) {
			return o.equals(map.keySet());
		} else {
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
		} else {
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
