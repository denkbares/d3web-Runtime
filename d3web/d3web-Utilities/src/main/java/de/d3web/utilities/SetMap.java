package de.d3web.utilities;

import java.util.HashSet;
import java.util.Set;


/**
 * A HashMap that conatin sets as values.
 * 
 * @author Peter Klügl
 *
 */
public class SetMap<Key, Type> extends AbstractSetMap<Key, Type> {
	static final long serialVersionUID = -7932985293685168247L;

	public SetMap() {
		super();
	}
	
	public SetMap(boolean removeEmtpy) {
		this();
		this.removeEmtpy = removeEmtpy;
	}

	public boolean add(Key key, Type object) {
		Set<Type> coll = get(key);
		if(coll == null) {
			coll = new HashSet<Type>();
			put(key, coll);
		}
		if(object != null) {
			return coll.add(object);
		} else return true;
	}
}
