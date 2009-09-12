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
