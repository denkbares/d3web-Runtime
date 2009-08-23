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
