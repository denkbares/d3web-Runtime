package de.d3web.utilities;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class TreeSetMap<Key, Type> extends AbstractSetMap<Key, Type> {

	private static final long serialVersionUID = 4524230076836708067L;

	public TreeSetMap() {
		super();
	}
	
	public TreeSetMap(boolean removeEmtpy) {
		this();
		this.removeEmtpy = removeEmtpy;
	}

	public boolean add(Key key, Type object) {
		Set<Type> coll = get(key);
		if(coll == null) {
			coll = new TreeSet<Type>();
			put(key, coll);
		}
		if(object != null) {
			return coll.add(object);
		} else return true;
	}
}
