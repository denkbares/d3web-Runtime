package de.d3web.utilities;

import java.util.HashSet;
import java.util.Set;


/**
 * A IdentityHashMap that conatin sets as values.
 * 
 * @author Peter Klügl
 *
 */
public class SetIdentityMap<Key, Type> extends AbstractSetIdentityMap<Key, Type>{


	
	private static final long serialVersionUID = -7932985293685168247L;


	public boolean add(Key key, Type object) {
		Set<Type> coll = get(key);
		if(coll == null) {
			coll = new HashSet<Type>();
			put(key, coll);
		}
		return coll.add(object);
	}
		
}
