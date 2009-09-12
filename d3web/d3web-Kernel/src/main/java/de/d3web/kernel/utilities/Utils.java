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

package de.d3web.kernel.utilities;

/**
 * A collection of utilities Creation date: (18.07.00 09:53:10)
 * 
 * @author: Christian Betz
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Utils {

	/**
	 * Creates an ArrayList from objects. Creation date: (18.07.00 09:55:27)
	 */
	public final static <T> List<T> createList(T[] objects) {
		List<T> v = new ArrayList<T>(objects.length);
		for (int i = 0; i < objects.length; i++) {
			v.add(i, objects[i]);
		}
		return v;
	}

	/**
	 * Creates a vector from objects.
	 */
	public final static <T> Vector<T> createVector(T[] objects) {
		if (objects == null) {
			return null;
		}
		Vector<T> v = new Vector<T>(objects.length);
		for (int i = 0; i < objects.length; i++) {
			v.add(i, objects[i]);
		}
		return v;
	}

	/**
	 * The result is a collection that has the same elements except that those
	 * not satisfying the test (see above) have been removed. This is a
	 * non-destructive operation; the result is a copy of the input collection,
	 * save that some elements are not copied. Elements not removed occur in the
	 * same order in the result as they did in the argument.
	 * 
	 * @param collection
	 * @param ifTester
	 * @return
	 */
	public static <T> Collection<T> removeIfNot(Collection<T> collection, Tester ifTester) {
		Collection<T> newCollection = new Vector<T>();
		Iterator<T> iter = collection.iterator();
		while (iter.hasNext()) {
			T next = iter.next();
			if (ifTester.test(next)) {
				newCollection.add(next);
			}
		}
		return newCollection;
	}

	/**
	 * @return the first element of the given list that satisfies the given test
	 * @see Tester
	 */
	public static <T> T findIf(Collection<T> collection, Tester findIfTester) {
		Iterator<T> iter = collection.iterator();
		while (iter.hasNext()) {
			T next = iter.next();
			if (findIfTester.test(next)) {
				return next;
			}
		}
		return null;
	}

	/**
	 * inverts the given List
	 * 
	 * @return inverted List
	 */
	public static <T> List<T> invertList(List<T> list) {
		List<T> ret = new LinkedList<T>(list);
		Collections.reverse(ret);
		return ret;
	}
}