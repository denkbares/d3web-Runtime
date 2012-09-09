/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.xcl.io;

import java.util.Comparator;

import de.d3web.xcl.XCLRelation;

class XCLRelationComparator implements Comparator<XCLRelation> {

	private static final XCLRelationComparator INSTANCE = new XCLRelationComparator();

	private XCLRelationComparator() {
	}

	@Override
	public int compare(XCLRelation o1, XCLRelation o2) {
		if (o1 == o2) return 0;
		int h1 = o1 == null ? 0 : o1.hashCode();
		int h2 = o2 == null ? 0 : o2.hashCode();
		if (h1 < h2) return -1;
		if (h1 > h2) return 1;
		// same hashCode, use string representation
		String s1 = o1.toString();
		String s2 = o2.toString();
		return s1.compareTo(s2);
	}

	public static Comparator<? super XCLRelation> getInstance() {
		return INSTANCE;
	}

}
