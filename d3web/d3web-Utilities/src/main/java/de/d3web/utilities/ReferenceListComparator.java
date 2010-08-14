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

import java.util.Comparator;
import java.util.List;

/**
 * A Comparator to sort a list according to the order of a reference list. All
 * elements in the list to sort should be contained in the reference list (else,
 * they will be set to the end of the list).<br>
 * <br>
 * <b>Example:</b><br>
 * List listToSort = [C, E, A, B];<br>
 * List supersetListInRightOrder = [A, B, C, D, E, F];<br>
 * Collections.sort(listToSort, new
 * ReferenceListComparator(supersetListInRightOrder));<br>
 * <b>Result:</b><br>
 * listToSort == [A, B, C, E]<br>
 * 
 * @author gbuscher
 */
public class ReferenceListComparator implements Comparator {

	private List referenceList;

	public ReferenceListComparator(List referenceList) {
		this.referenceList = referenceList;
	}

	public int compare(Object o1, Object o2) {
		int index1 = referenceList.indexOf(o1);
		if (index1 == -1) {
			index1 = Integer.MAX_VALUE;
		}
		int index2 = referenceList.indexOf(o2);
		if (index2 == -1) {
			index2 = Integer.MAX_VALUE;
		}
		return index1 - index2;
	}

}
