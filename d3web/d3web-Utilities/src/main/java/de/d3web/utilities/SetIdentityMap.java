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

import java.util.HashSet;
import java.util.Set;

/**
 * A IdentityHashMap that conatin sets as values.
 * 
 * @author Peter Klügl
 * 
 */
public class SetIdentityMap<Key, Type> extends AbstractSetIdentityMap<Key, Type> {

	private static final long serialVersionUID = -7932985293685168247L;

	public boolean add(Key key, Type object) {
		Set<Type> coll = get(key);
		if (coll == null) {
			coll = new HashSet<Type>();
			put(key, coll);
		}
		return coll.add(object);
	}

}
