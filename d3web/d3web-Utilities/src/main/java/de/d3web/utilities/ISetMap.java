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
