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

package de.d3web.scoring;

import java.util.Arrays;
import java.util.Iterator;

/**
 * This class represents the type of a heuristic decision tree
 * 
 * @author Norman Brümmer
 */
public class HDTType {

	private String name;
	public static final HDTType SOLUTION = new HDTType("SOLUTION");
	public static final HDTType NONE = new HDTType("NONE");
	public static final HDTType PROBLEMAREA = new HDTType("PROBLEMAREA");

	/**
	 * Creates a new HDTType object
	 */
	private HDTType(String _name) {
		name = _name;
	}

	/**
	 * @return a String representation of the HDTType
	 */
	public String toString() {
		return "HDTType: " + name;
	}

	/**
	 * This method is called immediately after an object of this class is
	 * deserialized. To avoid that several instances of a unique object are
	 * created, this method returns the current unique instance that is equal to
	 * the object that was deserialized.
	 * 
	 * @author georg
	 */
	private Object readResolve() {
		Iterator iter = Arrays.asList(new HDTType[] {
				HDTType.SOLUTION,
				HDTType.NONE,
				HDTType.PROBLEMAREA,
		}).iterator();
		while (iter.hasNext()) {
			HDTType t = (HDTType) iter.next();
			if (t.name.equals(this.name)) {
				return t;
			}
		}
		return this;
	}

}
