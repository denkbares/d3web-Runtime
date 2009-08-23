package de.d3web.utilities;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Interface for maps, that contain sets as values.
 * 
 * @author Peter Klügl
 *
 */
public interface ISetMap<Key, Type> extends Map<Key, Set<Type>>{

	public boolean add(Key key, Type object);
	
	public void addAll(Key key, Collection<Type> objects);
	
	public void addAll(Collection<Key> keys, Type object); 
	
	public void addAll(Collection<Key> keys, Collection<Type> objects); 
	
	public void addAll(ISetMap<Key, Type> setmap); 
	
	public boolean remove(Key key, Type object); 
	
	public void removeAll(Key key, Collection<Type> objects);
	
	public void removeAll(Collection<Key> keys, Type object); 
	
	public void removeAll(Collection<Key> keys, Collection<Type> objects); 
	
	public Set<Type> getAllValues(); 

	public Set<Type> getAllValues(Set<Key> keys); 
	
}
