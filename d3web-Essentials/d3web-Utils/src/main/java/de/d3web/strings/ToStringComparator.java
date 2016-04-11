/*
 * Copyright (C) 2014 denkbares GmbH
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
package de.d3web.strings;

import java.util.Comparator;

/**
 * Comparator comparing to Objects by the output of their toString() methods. If
 * null is compared, the String "null" will be used for comparison.
 * 
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 03.02.2014
 */
public class ToStringComparator implements Comparator<Object> {

	@Override
	public int compare(Object o1, Object o2) {
		String s1 = o1 == null ? "null" : o1.toString();
		String s2 = o2 == null ? "null" : o2.toString();
		return s1.compareTo(s2);
	}

}
